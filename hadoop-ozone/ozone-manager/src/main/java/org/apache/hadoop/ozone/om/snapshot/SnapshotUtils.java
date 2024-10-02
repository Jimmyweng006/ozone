/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.ozone.om.snapshot;

import org.apache.hadoop.hdds.utils.db.managed.ManagedRocksDB;
import org.apache.hadoop.ozone.om.OMMetadataManager;
import org.apache.hadoop.ozone.om.OmMetadataManagerImpl;
import org.apache.hadoop.ozone.om.OzoneManager;
import org.apache.hadoop.ozone.om.SnapshotChainManager;
import org.apache.hadoop.ozone.om.exceptions.OMException;
import org.apache.hadoop.ozone.om.helpers.OmKeyInfo;
import org.apache.hadoop.ozone.om.helpers.RepeatedOmKeyInfo;
import org.apache.hadoop.ozone.om.helpers.SnapshotInfo;
import org.apache.hadoop.ozone.om.helpers.SnapshotInfo.SnapshotStatus;
import org.apache.hadoop.ozone.protocol.proto.OzoneManagerProtocolProtos;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.apache.hadoop.ozone.OzoneConsts.OM_KEY_PREFIX;
import static org.apache.hadoop.ozone.om.exceptions.OMException.ResultCodes.INVALID_SNAPSHOT_ERROR;
import static org.apache.hadoop.ozone.om.OmMetadataManagerImpl.DIRECTORY_TABLE;
import static org.apache.hadoop.ozone.om.OmMetadataManagerImpl.FILE_TABLE;
import static org.apache.hadoop.ozone.om.OmMetadataManagerImpl.KEY_TABLE;
import static org.apache.hadoop.ozone.om.exceptions.OMException.ResultCodes.FILE_NOT_FOUND;
import static org.apache.hadoop.ozone.om.exceptions.OMException.ResultCodes.KEY_NOT_FOUND;
import static org.apache.hadoop.ozone.om.exceptions.OMException.ResultCodes.TIMEOUT;

/**
 * Util class for snapshot diff APIs.
 */
public final class SnapshotUtils {
  private static final Logger LOG =
      LoggerFactory.getLogger(SnapshotUtils.class);

  private SnapshotUtils() {
    throw new IllegalStateException("SnapshotUtils should not be initialized.");
  }

  public static SnapshotInfo getSnapshotInfo(final OzoneManager ozoneManager,
                                             final String volumeName,
                                             final String bucketName,
                                             final String snapshotName)
      throws IOException {
    return getSnapshotInfo(ozoneManager,
        SnapshotInfo.getTableKey(volumeName, bucketName, snapshotName));
  }

  public static SnapshotInfo getSnapshotInfo(final OzoneManager ozoneManager,
                                             final String snapshotKey)
      throws IOException {
    SnapshotInfo snapshotInfo;
    try {
      snapshotInfo = ozoneManager.getMetadataManager()
          .getSnapshotInfoTable()
          .get(snapshotKey);
    } catch (IOException e) {
      LOG.error("Snapshot '{}' is not found.", snapshotKey, e);
      throw e;
    }
    if (snapshotInfo == null) {
      throw new OMException("Snapshot '" + snapshotKey + "' is not found.",
          KEY_NOT_FOUND);
    }
    return snapshotInfo;
  }

  public static SnapshotInfo getSnapshotInfo(OzoneManager ozoneManager,
                                             SnapshotChainManager chainManager,
                                             UUID snapshotId) throws IOException {
    String tableKey = chainManager.getTableKey(snapshotId);
    return SnapshotUtils.getSnapshotInfo(ozoneManager, tableKey);
  }

  public static void dropColumnFamilyHandle(
      final ManagedRocksDB rocksDB,
      final ColumnFamilyHandle columnFamilyHandle) {

    if (columnFamilyHandle == null) {
      return;
    }

    try {
      rocksDB.get().dropColumnFamily(columnFamilyHandle);
    } catch (RocksDBException exception) {
      // TODO: [SNAPSHOT] Fail gracefully.
      throw new RuntimeException(exception);
    }
  }

  /**
   * Throws OMException TIMEOUT if snapshot directory does not exist.
   * @param checkpoint Snapshot checkpoint directory
   */
  public static void checkSnapshotDirExist(File checkpoint)
      throws IOException {
    if (!checkpoint.exists()) {
      throw new OMException("Unable to load snapshot. " +
          "Snapshot checkpoint directory '" + checkpoint.getAbsolutePath() +
          "' does not exist yet. Please wait a few more seconds before " +
          "retrying", TIMEOUT);
    }
  }

  /**
   * Throws OMException FILE_NOT_FOUND if snapshot is not in active status.
   * @param snapshotTableKey snapshot table key
   */
  public static void checkSnapshotActive(OzoneManager ozoneManager,
                                         String snapshotTableKey)
      throws IOException {
    checkSnapshotActive(getSnapshotInfo(ozoneManager, snapshotTableKey), false);
  }

  public static void checkSnapshotActive(SnapshotInfo snapInfo,
                                         boolean skipCheck)
      throws OMException {

    if (!skipCheck &&
        snapInfo.getSnapshotStatus() != SnapshotStatus.SNAPSHOT_ACTIVE) {
      throw new OMException("Unable to load snapshot. " +
          "Snapshot with table key '" + snapInfo.getTableKey() +
          "' is no longer active", FILE_NOT_FOUND);
    }
  }

  /**
   * Get the next snapshot in the snapshot chain.
   */
  public static SnapshotInfo getNextSnapshot(OzoneManager ozoneManager,
                                             SnapshotChainManager chainManager,
                                             SnapshotInfo snapInfo)
      throws IOException {
    // If the snapshot is deleted in the previous run, then the in-memory
    // SnapshotChainManager might throw NoSuchElementException as the snapshot
    // is removed in-memory but OMDoubleBuffer has not flushed yet.
    if (snapInfo == null) {
      throw new OMException("Provided Snapshot Info argument is null. Cannot get the next snapshot for a null value",
          INVALID_SNAPSHOT_ERROR);
    }
    try {
      if (chainManager.hasNextPathSnapshot(snapInfo.getSnapshotPath(),
          snapInfo.getSnapshotId())) {
        UUID nextPathSnapshot = chainManager.nextPathSnapshot(snapInfo.getSnapshotPath(), snapInfo.getSnapshotId());
        return getSnapshotInfo(ozoneManager, chainManager, nextPathSnapshot);
      }
    } catch (NoSuchElementException ex) {
      LOG.error("The snapshot {} is not longer in snapshot chain, It " +
              "maybe removed in the previous Snapshot purge request.",
          snapInfo.getTableKey());
    }
    return null;
  }

  /**
   * Get the previous snapshot in the snapshot chain.
   */
  public static SnapshotInfo getPreviousSnapshot(OzoneManager ozoneManager,
                                                 SnapshotChainManager chainManager,
                                                 SnapshotInfo snapInfo)
      throws IOException {
    UUID previousSnapshotId = getPreviousSnapshotId(snapInfo, chainManager);
    return previousSnapshotId == null ? null : getSnapshotInfo(ozoneManager, chainManager, previousSnapshotId);
  }

  /**
   * Get the previous snapshot in the snapshot chain.
   */
  private static UUID getPreviousSnapshotId(SnapshotInfo snapInfo, SnapshotChainManager chainManager)
      throws IOException {
    // If the snapshot is deleted in the previous run, then the in-memory
    // SnapshotChainManager might throw NoSuchElementException as the snapshot
    // is removed in-memory but OMDoubleBuffer has not flushed yet.
    if (snapInfo == null) {
      throw new OMException("Provided Snapshot Info argument is null. Cannot get the previous snapshot for a null " +
          "value", INVALID_SNAPSHOT_ERROR);
    }
    try {
      if (chainManager.hasPreviousPathSnapshot(snapInfo.getSnapshotPath(),
          snapInfo.getSnapshotId())) {
        return chainManager.previousPathSnapshot(snapInfo.getSnapshotPath(),
            snapInfo.getSnapshotId());
      }
    } catch (NoSuchElementException ignored) {

    }
    return null;
  }

  /**
   * Return a map column family to prefix for the keys in the table for
   * the given volume and bucket.
   * Column families, map is returned for, are keyTable, dirTable and fileTable.
   */
  public static Map<String, String> getColumnFamilyToKeyPrefixMap(
      OMMetadataManager omMetadataManager,
      String volumeName,
      String bucketName
  ) throws IOException {
    String keyPrefix = getOzonePathKey(volumeName, bucketName);
    String keyPrefixFso = getOzonePathKeyForFso(omMetadataManager, volumeName,
        bucketName);

    Map<String, String> columnFamilyToPrefixMap = new HashMap<>();
    columnFamilyToPrefixMap.put(KEY_TABLE, keyPrefix);
    columnFamilyToPrefixMap.put(DIRECTORY_TABLE, keyPrefixFso);
    columnFamilyToPrefixMap.put(FILE_TABLE, keyPrefixFso);
    return columnFamilyToPrefixMap;
  }

  /**
   * Helper method to generate /volumeName/bucketBucket/ DB key prefix from
   * given volume name and bucket name as a prefix for legacy and OBS buckets.
   * Follows:
   * {@link OmMetadataManagerImpl#getOzonePathKey(long, long, long, String)}.
   * <p>
   * Note: Currently, this is only intended to be a special use case in
   * Snapshot. If this is used elsewhere, consider moving this to
   * {@link OMMetadataManager}.
   *
   * @param volumeName volume name
   * @param bucketName bucket name
   * @return /volumeName/bucketName/
   */
  public static String getOzonePathKey(String volumeName,
                                       String bucketName) throws IOException {
    return OM_KEY_PREFIX + volumeName + OM_KEY_PREFIX + bucketName +
        OM_KEY_PREFIX;
  }

  /**
   * Helper method to generate /volumeId/bucketId/ DB key prefix from given
   * volume name and bucket name as a prefix for FSO buckets.
   * Follows:
   * {@link OmMetadataManagerImpl#getOzonePathKey(long, long, long, String)}.
   * <p>
   * Note: Currently, this is only intended to be a special use case in
   * Snapshot. If this is used elsewhere, consider moving this to
   * {@link OMMetadataManager}.
   *
   * @param volumeName volume name
   * @param bucketName bucket name
   * @return /volumeId/bucketId/
   *    e.g. /-9223372036854772480/-9223372036854771968/
   */
  public static String getOzonePathKeyForFso(OMMetadataManager metadataManager,
                                             String volumeName,
                                             String bucketName)
      throws IOException {
    final long volumeId = metadataManager.getVolumeId(volumeName);
    final long bucketId = metadataManager.getBucketId(volumeName, bucketName);
    return OM_KEY_PREFIX + volumeId + OM_KEY_PREFIX + bucketId + OM_KEY_PREFIX;
  }

  /**
   * Returns merged repeatedKeyInfo entry with the existing deleted entry in the table.
   * @param snapshotMoveKeyInfos keyInfos to be added.
   * @param metadataManager metadataManager for a store.
   * @return RepeatedOmKeyInfo
   * @throws IOException
   */
  public static RepeatedOmKeyInfo createMergedRepeatedOmKeyInfoFromDeletedTableEntry(
      OzoneManagerProtocolProtos.SnapshotMoveKeyInfos snapshotMoveKeyInfos, OMMetadataManager metadataManager) throws
      IOException {
    String dbKey = snapshotMoveKeyInfos.getKey();
    List<OmKeyInfo> keyInfoList = new ArrayList<>();
    for (OzoneManagerProtocolProtos.KeyInfo info : snapshotMoveKeyInfos.getKeyInfosList()) {
      OmKeyInfo fromProtobuf = OmKeyInfo.getFromProtobuf(info);
      keyInfoList.add(fromProtobuf);
    }
    // When older version of keys are moved to the next snapshot's deletedTable
    // The newer version might also be in the next snapshot's deletedTable and
    // it might overwrite the existing value which inturn could lead to orphan block in the system.
    // Checking the keyInfoList with the last n versions of the omKeyInfo versions would ensure all versions are
    // present in the list and would also avoid redundant additions to the list if the last n versions match, which
    // can happen on om transaction replay on snapshotted rocksdb.
    RepeatedOmKeyInfo result = metadataManager.getDeletedTable().get(dbKey);
    if (result == null) {
      result = new RepeatedOmKeyInfo(keyInfoList);
    } else if (!isSameAsLatestOmKeyInfo(keyInfoList, result)) {
      keyInfoList.forEach(result::addOmKeyInfo);
    }
    return result;
  }

  private static boolean isSameAsLatestOmKeyInfo(List<OmKeyInfo> omKeyInfos,
                                                 RepeatedOmKeyInfo result) {
    int size = result.getOmKeyInfoList().size();
    if (size >= omKeyInfos.size()) {
      return omKeyInfos.equals(result.getOmKeyInfoList().subList(size - omKeyInfos.size(), size));
    }
    return false;
  }

  public static SnapshotInfo getLatestSnapshotInfo(String volumeName, String bucketName,
                                                   OzoneManager ozoneManager,
                                                   SnapshotChainManager snapshotChainManager) throws IOException {
    Optional<UUID> latestPathSnapshot = Optional.ofNullable(
        getLatestPathSnapshotId(volumeName, bucketName, snapshotChainManager));
    return latestPathSnapshot.isPresent() ?
        getSnapshotInfo(ozoneManager, snapshotChainManager, latestPathSnapshot.get()) : null;
  }

  public static UUID getLatestPathSnapshotId(String volumeName, String bucketName,
                                             SnapshotChainManager snapshotChainManager) throws IOException {
    String snapshotPath = volumeName + OM_KEY_PREFIX + bucketName;
    return snapshotChainManager.getLatestPathSnapshotId(snapshotPath);
  }

  // Validates the previous path snapshotId for given a snapshotInfo. In case snapshotInfo is
  // null, the snapshotInfo would be considered as AOS and previous snapshot becomes the latest snapshot in the global
  // snapshot chain. Would throw OMException if validation fails otherwise function would pass.
  public static void validatePreviousSnapshotId(SnapshotInfo snapshotInfo,
                                                SnapshotChainManager snapshotChainManager,
                                                UUID expectedPreviousSnapshotId) throws IOException {
    UUID previousSnapshotId = snapshotInfo == null ? snapshotChainManager.getLatestGlobalSnapshotId() :
        SnapshotUtils.getPreviousSnapshotId(snapshotInfo, snapshotChainManager);
    if (!Objects.equals(expectedPreviousSnapshotId, previousSnapshotId)) {
      throw new OMException("Snapshot validation failed. Expected previous snapshotId : " +
          expectedPreviousSnapshotId + " but was " + previousSnapshotId,
          OMException.ResultCodes.INVALID_REQUEST);
    }
  }
}
