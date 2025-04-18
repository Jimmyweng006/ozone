/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.ozone.recon.recovery;

import static org.apache.hadoop.ozone.om.OMConfigKeys.OZONE_OM_DB_DIRS;
import static org.apache.hadoop.ozone.recon.ReconServerConfigKeys.OZONE_RECON_OM_SNAPSHOT_DB_DIR;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hdds.client.StandaloneReplicationConfig;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.proto.HddsProtos;
import org.apache.hadoop.hdds.utils.db.DBCheckpoint;
import org.apache.hadoop.hdds.utils.db.DBStore;
import org.apache.hadoop.ozone.om.OMMetadataManager;
import org.apache.hadoop.ozone.om.OmMetadataManagerImpl;
import org.apache.hadoop.ozone.om.helpers.BucketLayout;
import org.apache.hadoop.ozone.om.helpers.OmBucketInfo;
import org.apache.hadoop.ozone.om.helpers.OmKeyInfo;
import org.apache.hadoop.ozone.om.helpers.OmVolumeArgs;
import org.apache.hadoop.ozone.recon.ReconUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test Recon OM Metadata Manager implementation.
 */
public class TestReconOmMetadataManagerImpl {

  @TempDir
  private Path temporaryFolder;

  @Test
  public void testStart() throws Exception {

    OMMetadataManager omMetadataManager = getOMMetadataManager();

    //Take checkpoint of the above OM DB.
    DBCheckpoint checkpoint = omMetadataManager.getStore()
        .getCheckpoint(true);
    File snapshotFile = new File(
        checkpoint.getCheckpointLocation().getParent() + "/" +
            "om.snapshot.db_" + System.currentTimeMillis());
    assertTrue(checkpoint.getCheckpointLocation().toFile().renameTo(snapshotFile));

    //Create new Recon OM Metadata manager instance.
    File reconOmDbDir = Files.createDirectory(
        temporaryFolder.resolve("NewDir")).toFile();
    OzoneConfiguration configuration = new OzoneConfiguration();
    configuration.set(OZONE_RECON_OM_SNAPSHOT_DB_DIR, reconOmDbDir
        .getAbsolutePath());
    FileUtils.copyDirectory(snapshotFile.getParentFile(), reconOmDbDir);

    ReconOMMetadataManager reconOMMetadataManager =
        new ReconOmMetadataManagerImpl(configuration, new ReconUtils());
    reconOMMetadataManager.start(configuration);

    assertNotNull(reconOMMetadataManager.getBucketTable());
    assertNotNull(reconOMMetadataManager.getVolumeTable()
        .get("/sampleVol"));
    assertNotNull(reconOMMetadataManager.getBucketTable()
        .get("/sampleVol/bucketOne"));
    assertNotNull(reconOMMetadataManager.getKeyTable(getBucketLayout())
        .get("/sampleVol/bucketOne/key_one"));
    assertNotNull(reconOMMetadataManager.getKeyTable(getBucketLayout())
        .get("/sampleVol/bucketOne/key_two"));
  }

  @Test
  public void testUpdateOmDB() throws Exception {

    OMMetadataManager omMetadataManager = getOMMetadataManager();
    //Make sure OM Metadata reflects the keys that were inserted.
    assertNotNull(omMetadataManager.getKeyTable(getBucketLayout())
        .get("/sampleVol/bucketOne/key_one"));
    assertNotNull(omMetadataManager.getKeyTable(getBucketLayout())
        .get("/sampleVol/bucketOne/key_two"));

    //Take checkpoint of OM DB.
    DBCheckpoint checkpoint = omMetadataManager.getStore()
        .getCheckpoint(true);
    assertNotNull(checkpoint.getCheckpointLocation());

    //Create new Recon OM Metadata manager instance.
    File reconOmDbDir = Files.createDirectory(
        temporaryFolder.resolve("reconOmDbDir")).toFile();
    OzoneConfiguration configuration = new OzoneConfiguration();
    configuration.set(OZONE_RECON_OM_SNAPSHOT_DB_DIR, reconOmDbDir
        .getAbsolutePath());
    ReconOMMetadataManager reconOMMetadataManager =
        new ReconOmMetadataManagerImpl(configuration, new ReconUtils());
    reconOMMetadataManager.start(configuration);

    //Before accepting a snapshot, the metadata should have null tables.
    assertNull(reconOMMetadataManager.getBucketTable());

    //Update Recon OM DB with the OM DB checkpoint location.
    reconOMMetadataManager.updateOmDB(
        checkpoint.getCheckpointLocation().toFile());

    //Now, the tables should have been initialized.
    assertNotNull(reconOMMetadataManager.getBucketTable());

    // Check volume and bucket entries.
    assertNotNull(reconOMMetadataManager.getVolumeTable()
        .get("/sampleVol"));
    assertNotNull(reconOMMetadataManager.getBucketTable()
        .get("/sampleVol/bucketOne"));

    //Verify Keys inserted in OM DB are available in Recon OM DB.
    assertNotNull(reconOMMetadataManager.getKeyTable(getBucketLayout())
        .get("/sampleVol/bucketOne/key_one"));
    assertNotNull(reconOMMetadataManager.getKeyTable(getBucketLayout())
        .get("/sampleVol/bucketOne/key_two"));

    //Take a new checkpoint of OM DB.
    DBCheckpoint newCheckpoint = omMetadataManager.getStore()
        .getCheckpoint(true);
    assertNotNull(newCheckpoint.getCheckpointLocation());
    // Update again with an existing OM DB.
    DBStore current = reconOMMetadataManager.getStore();
    reconOMMetadataManager.updateOmDB(
        newCheckpoint.getCheckpointLocation().toFile());
    // Verify that the existing DB instance is closed.
    assertTrue(current.isClosed());
  }

  /**
   * Get test OM metadata manager.
   * @return OMMetadataManager instance
   * @throws IOException
   */
  private OMMetadataManager getOMMetadataManager() throws IOException {
    //Create a new OM Metadata Manager instance + DB.
    File omDbDir = Files.createDirectory(
        temporaryFolder.resolve("OmMetadataDir")).toFile();
    OzoneConfiguration omConfiguration = new OzoneConfiguration();
    omConfiguration.set(OZONE_OM_DB_DIRS,
        omDbDir.getAbsolutePath());
    OMMetadataManager omMetadataManager = new OmMetadataManagerImpl(
        omConfiguration, null);

    //Create a volume + bucket + 2 keys.
    String volumeKey = omMetadataManager.getVolumeKey("sampleVol");
    OmVolumeArgs args =
        OmVolumeArgs.newBuilder()
            .setVolume("sampleVol")
            .setAdminName("TestUser")
            .setOwnerName("TestUser")
            .build();
    omMetadataManager.getVolumeTable().put(volumeKey, args);

    OmBucketInfo bucketInfo = OmBucketInfo.newBuilder()
        .setVolumeName("sampleVol")
        .setBucketName("bucketOne")
        .build();

    String bucketKey =
        omMetadataManager.getBucketKey(bucketInfo.getVolumeName(),
            bucketInfo.getBucketName());
    omMetadataManager.getBucketTable().put(bucketKey, bucketInfo);


    omMetadataManager.getKeyTable(getBucketLayout()).put(
        "/sampleVol/bucketOne/key_one", new OmKeyInfo.Builder()
            .setBucketName("bucketOne")
            .setVolumeName("sampleVol")
            .setKeyName("key_one")
            .setReplicationConfig(StandaloneReplicationConfig.getInstance(
                HddsProtos.ReplicationFactor.ONE))
            .build());
    omMetadataManager.getKeyTable(getBucketLayout()).put(
        "/sampleVol/bucketOne/key_two", new OmKeyInfo.Builder()
            .setBucketName("bucketOne")
            .setVolumeName("sampleVol")
            .setKeyName("key_two")
            .setReplicationConfig(StandaloneReplicationConfig.getInstance(
                HddsProtos.ReplicationFactor.ONE))
            .build());

    return omMetadataManager;
  }

  private BucketLayout getBucketLayout() {
    return BucketLayout.DEFAULT;
  }
}
