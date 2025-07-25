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

package org.apache.hadoop.hdds;

/**
 * This class contains constants for configuration keys and default values
 * used in hdds.
 */
public final class HddsConfigKeys {

  public static final String HDDS_HEARTBEAT_INTERVAL =
      "hdds.heartbeat.interval";
  public static final String HDDS_HEARTBEAT_INTERVAL_DEFAULT =
      "30s";
  public static final String HDDS_INITIAL_HEARTBEAT_INTERVAL =
      "hdds.heartbeat.initial-interval";
  public static final String HDDS_INITIAL_HEARTBEAT_INTERVAL_DEFAULT =
      "2s";
  public static final String HDDS_RECON_HEARTBEAT_INTERVAL =
      "hdds.heartbeat.recon.interval";
  public static final String HDDS_RECON_HEARTBEAT_INTERVAL_DEFAULT =
      "60s";
  public static final String HDDS_RECON_INITIAL_HEARTBEAT_INTERVAL =
      "hdds.heartbeat.recon.initial-interval";
  public static final String HDDS_RECON_INITIAL_HEARTBEAT_INTERVAL_DEFAULT =
      "2s";
  public static final String HDDS_NODE_REPORT_INTERVAL =
      "hdds.node.report.interval";
  public static final String HDDS_NODE_REPORT_INTERVAL_DEFAULT =
      "60s";
  public static final String HDDS_CONTAINER_REPORT_INTERVAL =
      "hdds.container.report.interval";
  public static final String HDDS_CONTAINER_REPORT_INTERVAL_DEFAULT =
      "60m";
  public static final String HDDS_PIPELINE_REPORT_INTERVAL =
      "hdds.pipeline.report.interval";
  public static final String HDDS_PIPELINE_REPORT_INTERVAL_DEFAULT =
      "60s";
  public static final String HDDS_COMMAND_STATUS_REPORT_INTERVAL =
      "hdds.command.status.report.interval";
  public static final String HDDS_COMMAND_STATUS_REPORT_INTERVAL_DEFAULT =
      "60s";
  public static final String HDDS_CONTAINER_ACTION_MAX_LIMIT =
      "hdds.container.action.max.limit";
  public static final int HDDS_CONTAINER_ACTION_MAX_LIMIT_DEFAULT =
      20;
  public static final String HDDS_PIPELINE_ACTION_MAX_LIMIT =
      "hdds.pipeline.action.max.limit";
  public static final int HDDS_PIPELINE_ACTION_MAX_LIMIT_DEFAULT =
      20;
  // Configuration to allow volume choosing policy.
  public static final String HDDS_DATANODE_VOLUME_CHOOSING_POLICY =
      "hdds.datanode.volume.choosing.policy";

  public static final String HDDS_DB_PROFILE = "hdds.db.profile";

  // Once a container usage crosses this threshold, it is eligible for
  // closing.
  public static final String HDDS_CONTAINER_CLOSE_THRESHOLD =
      "hdds.container.close.threshold";
  public static final float HDDS_CONTAINER_CLOSE_THRESHOLD_DEFAULT = 0.9f;
  public static final String HDDS_SCM_SAFEMODE_ENABLED =
      "hdds.scm.safemode.enabled";

  public static final boolean HDDS_SCM_SAFEMODE_ENABLED_DEFAULT = true;
  public static final String HDDS_SCM_SAFEMODE_MIN_DATANODE =
      "hdds.scm.safemode.min.datanode";
  public static final int HDDS_SCM_SAFEMODE_MIN_DATANODE_DEFAULT = 3;

  public static final String
      HDDS_SCM_WAIT_TIME_AFTER_SAFE_MODE_EXIT =
      "hdds.scm.wait.time.after.safemode.exit";

  public static final String
      HDDS_SCM_WAIT_TIME_AFTER_SAFE_MODE_EXIT_DEFAULT = "5m";

  public static final String HDDS_SCM_SAFEMODE_PIPELINE_CREATION =
      "hdds.scm.safemode.pipeline.creation";
  public static final boolean
      HDDS_SCM_SAFEMODE_PIPELINE_CREATION_DEFAULT = true;

  // % of containers which should have at least one reported replica
  // before SCM comes out of safe mode.
  public static final String HDDS_SCM_SAFEMODE_THRESHOLD_PCT =
      "hdds.scm.safemode.threshold.pct";
  public static final double HDDS_SCM_SAFEMODE_THRESHOLD_PCT_DEFAULT = 0.99;

  // percentage of healthy pipelines, where all 3 datanodes are reported in the
  // pipeline.
  public static final String HDDS_SCM_SAFEMODE_HEALTHY_PIPELINE_THRESHOLD_PCT =
      "hdds.scm.safemode.healthy.pipeline.pct";
  public static final double
      HDDS_SCM_SAFEMODE_HEALTHY_PIPELINE_THRESHOLD_PCT_DEFAULT = 0.10;

  public static final String HDDS_SCM_SAFEMODE_ONE_NODE_REPORTED_PIPELINE_PCT =
      "hdds.scm.safemode.atleast.one.node.reported.pipeline.pct";
  public static final double
      HDDS_SCM_SAFEMODE_ONE_NODE_REPORTED_PIPELINE_PCT_DEFAULT = 0.90;

  // This configuration setting is used as a fallback location by all
  // Ozone/HDDS services for their metadata. It is useful as a single
  // config point for test/PoC clusters.
  //
  // In any real cluster where performance matters, the SCM, OM and DN
  // metadata locations must be configured explicitly.
  public static final String OZONE_METADATA_DIRS = "ozone.metadata.dirs";

  public static final String HDDS_PROMETHEUS_ENABLED =
      "hdds.prometheus.endpoint.enabled";

  public static final String HDDS_PROFILER_ENABLED =
      "hdds.profiler.endpoint.enabled";

  public static final String HDDS_KEY_LEN = "hdds.key.len";
  public static final int HDDS_DEFAULT_KEY_LEN = 2048;
  public static final String HDDS_KEY_ALGORITHM = "hdds.key.algo";
  public static final String HDDS_DEFAULT_KEY_ALGORITHM = "RSA";
  public static final String HDDS_SECURITY_PROVIDER = "hdds.security.provider";
  public static final String HDDS_DEFAULT_SECURITY_PROVIDER = "BC";
  public static final String HDDS_KEY_DIR_NAME = "hdds.key.dir.name";
  public static final String HDDS_KEY_DIR_NAME_DEFAULT = "keys";
  // TODO : Talk to StorageIO classes and see if they can return a secure
  // storage location for each node.
  public static final String HDDS_METADATA_DIR_NAME = "hdds.metadata.dir";
  public static final String HDDS_PRIVATE_KEY_FILE_NAME =
      "hdds.priv.key.file.name";
  public static final String HDDS_PRIVATE_KEY_FILE_NAME_DEFAULT = "private.pem";
  public static final String HDDS_PUBLIC_KEY_FILE_NAME = "hdds.public.key.file"
      + ".name";
  public static final String HDDS_PUBLIC_KEY_FILE_NAME_DEFAULT = "public.pem";

  public static final String HDDS_HTTP_SERVER_KEYSTORE_TYPE = "ssl.server.keystore.type";
  public static final String HDDS_HTTP_SERVER_KEYSTORE_TYPE_DEFAULT = "jks";
  public static final String HDDS_HTTP_SERVER_TRUSTSTORE_TYPE = "ssl.server.truststore.type";
  public static final String HDDS_HTTP_SERVER_TRUSTSTORE_TYPE_DEFAULT = "jks";

  public static final String HDDS_BLOCK_TOKEN_EXPIRY_TIME =
      "hdds.block.token.expiry.time";
  public static final String HDDS_BLOCK_TOKEN_EXPIRY_TIME_DEFAULT = "1d";
  /**
   * Maximum duration of certificates issued by SCM including Self-Signed
   * Roots and sub-ca certificates issued by root CA.
   * The formats accepted are based on the ISO-8601 duration format PnDTnHnMn.nS
   * Default value is 5 years and written as P1865D.
   */
  public static final String HDDS_X509_MAX_DURATION = "hdds.x509.max.duration";
  // Limit Certificate duration to a max value of 5 years.
  public static final String HDDS_X509_MAX_DURATION_DEFAULT = "P1865D";
  public static final String HDDS_X509_SIGNATURE_ALGO =
      "hdds.x509.signature.algorithm";
  public static final String HDDS_X509_SIGNATURE_ALGO_DEFAULT = "SHA256withRSA";
  public static final String HDDS_BLOCK_TOKEN_ENABLED =
      "hdds.block.token.enabled";
  public static final boolean HDDS_BLOCK_TOKEN_ENABLED_DEFAULT = false;
  public static final String HDDS_CONTAINER_TOKEN_ENABLED =
      "hdds.container.token.enabled";
  public static final boolean HDDS_CONTAINER_TOKEN_ENABLED_DEFAULT = false;

  public static final String HDDS_X509_DIR_NAME = "hdds.x509.dir.name";
  public static final String HDDS_X509_DIR_NAME_DEFAULT = "certs";
  public static final String HDDS_X509_FILE_NAME = "hdds.x509.file.name";
  public static final String HDDS_X509_FILE_NAME_DEFAULT = "certificate.crt";

  /**
   * Default duration of certificates issued by SCM CA. The formats accepted are
   * based on the ISO-8601 duration format PnDTnHnMn.nS Default value is 5 years
   * and written as P1865D.
   */
  public static final String HDDS_X509_DEFAULT_DURATION = "hdds.x509.default" +
      ".duration";
  // Default Certificate duration to one year.
  public static final String HDDS_X509_DEFAULT_DURATION_DEFAULT = "P365D";

  /**
   * Duration of the grace period within which a certificate should be
   * renewed before the current one expires.
   * Default is 28 days.
   */
  public static final String HDDS_X509_RENEW_GRACE_DURATION =
      "hdds.x509.renew.grace.duration";

  public static final String HDDS_X509_RENEW_GRACE_DURATION_DEFAULT = "P28D";
  public static final String HDDS_X509_GRACE_DURATION_TOKEN_CHECKS_ENABLED =
      "hdds.x509.grace.duration.token.checks.enabled";
  public static final boolean
      HDDS_X509_GRACE_DURATION_TOKEN_CHECKS_ENABLED_DEFAULT = true;
  public static final String HDDS_NEW_KEY_CERT_DIR_NAME_SUFFIX = "-next";
  public static final String HDDS_BACKUP_KEY_CERT_DIR_NAME_SUFFIX = "-previous";
  public static final String HDDS_NEW_KEY_CERT_DIR_NAME_PROGRESS_SUFFIX =
      "-progress";
  public static final String HDDS_X509_CA_ROTATION_CHECK_INTERNAL =
      "hdds.x509.ca.rotation.check.interval";
  public static final String HDDS_X509_CA_ROTATION_CHECK_INTERNAL_DEFAULT =
      "P1D";
  public static final String HDDS_X509_CA_ROTATION_TIME_OF_DAY =
      "hdds.x509.ca.rotation.time-of-day";
  // format hh:mm:ss, representing hour, minute, and second
  public static final String HDDS_X509_CA_ROTATION_TIME_OF_DAY_DEFAULT =
      "02:00:00";
  public static final String HDDS_X509_CA_ROTATION_ACK_TIMEOUT =
      "hdds.x509.ca.rotation.ack.timeout";
  public static final String HDDS_X509_CA_ROTATION_ACK_TIMEOUT_DEFAULT =
      "PT15M";
  public static final String HDDS_X509_ROOTCA_CERTIFICATE_POLLING_INTERVAL =
      "hdds.x509.rootca.certificate.polling.interval";
  public static final String
      HDDS_X509_ROOTCA_CERTIFICATE_POLLING_INTERVAL_DEFAULT = "PT2h";
  public static final String HDDS_X509_CA_ROTATION_ENABLED =
      "hdds.x509.ca.rotation.enabled";
  public static final boolean HDDS_X509_CA_ROTATION_ENABLED_DEFAULT = false;

  public static final String HDDS_X509_EXPIRED_CERTIFICATE_CHECK_INTERVAL =
      "hdds.x509.expired.certificate.check.interval";
  public static final String
      HDDS_X509_EXPIRED_CERTIFICATE_CHECK_INTERVAL_DEFAULT = "P1D";

  public static final String HDDS_CONTAINER_REPLICATION_COMPRESSION =
      "hdds.container.replication.compression";
  public static final String HDDS_X509_ROOTCA_CERTIFICATE_FILE =
      "hdds.x509.rootca.certificate.file";

  public static final String HDDS_X509_ROOTCA_CERTIFICATE_FILE_DEFAULT =
      "";

  public static final String HDDS_X509_ROOTCA_PUBLIC_KEY_FILE =
      "hdds.x509.rootca.public.key.file";

  public static final String HDDS_X509_ROOTCA_PUBLIC_KEY_FILE_DEFAULT =
      "";

  public static final String HDDS_X509_ROOTCA_PRIVATE_KEY_FILE =
      "hdds.x509.rootca.private.key.file";

  public static final String HDDS_X509_ROOTCA_PRIVATE_KEY_FILE_DEFAULT =
      "";

  public static final String HDDS_SECRET_KEY_FILE =
      "hdds.secret.key.file.name";
  public static final String HDDS_SECRET_KEY_FILE_DEFAULT = "secret_keys.json";

  public static final String HDDS_SECRET_KEY_EXPIRY_DURATION =
      "hdds.secret.key.expiry.duration";
  public static final String HDDS_SECRET_KEY_EXPIRY_DURATION_DEFAULT = "9d";

  public static final String HDDS_SECRET_KEY_ROTATE_DURATION =
      "hdds.secret.key.rotate.duration";
  public static final String HDDS_SECRET_KEY_ROTATE_DURATION_DEFAULT = "1d";

  public static final String HDDS_SECRET_KEY_ALGORITHM =
      "hdds.secret.key.algorithm";
  public static final String HDDS_SECRET_KEY_ALGORITHM_DEFAULT =
      "HmacSHA256";

  public static final String HDDS_SECRET_KEY_ROTATE_CHECK_DURATION =
      "hdds.secret.key.rotate.check.duration";
  public static final String HDDS_SECRET_KEY_ROTATE_CHECK_DURATION_DEFAULT
      = "10m";

  // Enable TLS for GRPC clients/server in ozone.
  public static final String HDDS_GRPC_TLS_ENABLED = "hdds.grpc.tls.enabled";
  public static final boolean HDDS_GRPC_TLS_ENABLED_DEFAULT = false;

  // Choose TLS provider the default is set to OPENSSL for better performance.
  public static final String HDDS_GRPC_TLS_PROVIDER = "hdds.grpc.tls.provider";
  public static final String HDDS_GRPC_TLS_PROVIDER_DEFAULT = "OPENSSL";

  // Test only settings for using test signed certificate, authority assume to
  // be localhost.
  public static final String HDDS_GRPC_TLS_TEST_CERT = "hdds.grpc.tls" +
      ".test.cert";
  public static final boolean HDDS_GRPC_TLS_TEST_CERT_DEFAULT = false;

  // Comma separated acls (users, groups) allowing clients accessing
  // datanode container protocol
  // when hadoop.security.authorization is true, this needs to be set in
  // hadoop-policy.xml, "*" allows all users/groups to access.
  public static final String
      HDDS_SECURITY_CLIENT_DATANODE_CONTAINER_PROTOCOL_ACL =
      "hdds.security.client.datanode.container.protocol.acl";

  // Comma separated acls (users, groups) allowing clients accessing
  // scm container protocol
  // when hadoop.security.authorization is true, this needs to be set in
  // hadoop-policy.xml, "*" allows all users/groups to access.
  public static final String HDDS_SECURITY_CLIENT_SCM_CONTAINER_PROTOCOL_ACL =
      "hdds.security.client.scm.container.protocol.acl";

  // Comma separated acls (users, groups) allowing clients accessing
  // scm block protocol
  // when hadoop.security.authorization is true, this needs to be set in
  // hadoop-policy.xml, "*" allows all users/groups to access.
  public static final String HDDS_SECURITY_CLIENT_SCM_BLOCK_PROTOCOL_ACL =
      "hdds.security.client.scm.block.protocol.acl";

  // Comma separated acls (users, groups) allowing clients accessing
  // scm certificate protocol
  // when hadoop.security.authorization is true, this needs to be set in
  // hadoop-policy.xml, "*" allows all users/groups to access.
  public static final String HDDS_SECURITY_CLIENT_SCM_CERTIFICATE_PROTOCOL_ACL =
      "hdds.security.client.scm.certificate.protocol.acl";

  public static final String
      HDDS_SECURITY_CLIENT_SCM_SECRET_KEY_OM_PROTOCOL_ACL =
      "hdds.security.client.scm.secretkey.om.protocol.acl";

  public static final String
      HDDS_SECURITY_CLIENT_SCM_SECRET_KEY_SCM_PROTOCOL_ACL =
      "hdds.security.client.scm.secretkey.scm.protocol.acl";

  public static final String
      HDDS_SECURITY_CLIENT_SCM_SECRET_KEY_DATANODE_PROTOCOL_ACL =
      "hdds.security.client.scm.secretkey.datanode.protocol.acl";

  public static final String OZONE_SECURITY_RECONFIGURE_PROTOCOL_ACL =
      "ozone.security.reconfigure.protocol.acl";

  // Determines if the Container Chunk Manager will write user data to disk
  // Set to false only for specific performance tests
  public static final String HDDS_CONTAINER_PERSISTDATA =
      "hdds.container.chunk.persistdata";
  public static final boolean HDDS_CONTAINER_PERSISTDATA_DEFAULT = true;

  public static final String HDDS_DATANODE_HTTP_ENABLED_KEY =
      "hdds.datanode.http.enabled";
  public static final String HDDS_DATANODE_HTTP_BIND_HOST_KEY =
      "hdds.datanode.http-bind-host";
  public static final String HDDS_DATANODE_HTTPS_BIND_HOST_KEY =
      "hdds.datanode.https-bind-host";
  public static final String HDDS_DATANODE_HTTP_ADDRESS_KEY =
      "hdds.datanode.http-address";
  public static final String HDDS_DATANODE_HTTPS_ADDRESS_KEY =
      "hdds.datanode.https-address";
  public static final String HDDS_DATANODE_CLIENT_ADDRESS_KEY =
      "hdds.datanode.client.address";
  public static final String HDDS_DATANODE_CLIENT_BIND_HOST_KEY =
      "hdds.datanode.client.bind.host";
  public static final String HDDS_DATANODE_CLIENT_BIND_HOST_DEFAULT =
      "0.0.0.0";
  public static final String HDDS_DATANODE_CLIENT_PORT_KEY =
      "hdds.datanode.client.port";
  public static final int HDDS_DATANODE_CLIENT_PORT_DEFAULT = 19864;
  public static final String HDDS_DATANODE_HANDLER_COUNT_KEY =
      "hdds.datanode.handler.count";
  public static final int HDDS_DATANODE_HANDLER_COUNT_DEFAULT = 10;
  public static final String HDDS_DATANODE_READ_THREADPOOL_KEY =
      "hdds.datanode.read.threadpool";
  public static final int HDDS_DATANODE_READ_THREADPOOL_DEFAULT = 10;
  public static final String HDDS_DATANODE_HTTP_BIND_HOST_DEFAULT = "0.0.0.0";
  public static final int HDDS_DATANODE_HTTP_BIND_PORT_DEFAULT = 9882;
  public static final int HDDS_DATANODE_HTTPS_BIND_PORT_DEFAULT = 9883;

  public static final String OZONE_DATANODE_HTTP_AUTH_CONFIG_PREFIX =
      "hdds.datanode.http.auth.";
  public static final String HDDS_DATANODE_HTTP_AUTH_TYPE =
      OZONE_DATANODE_HTTP_AUTH_CONFIG_PREFIX + "type";
  public static final String
      HDDS_DATANODE_HTTP_KERBEROS_PRINCIPAL_KEY =
      OZONE_DATANODE_HTTP_AUTH_CONFIG_PREFIX +  "kerberos.principal";
  public static final String
      HDDS_DATANODE_HTTP_KERBEROS_KEYTAB_FILE_KEY =
      OZONE_DATANODE_HTTP_AUTH_CONFIG_PREFIX + "kerberos.keytab";

  public static final String HDDS_DATANODE_RATIS_SERVER_REQUEST_TIMEOUT =
      "hdds.datanode.ratis.server.request.timeout";
  public static final String
      HDDS_DATANODE_RATIS_SERVER_REQUEST_TIMEOUT_DEFAULT = "2m";
  public static final String HDDS_CONTAINER_CHECKSUM_VERIFICATION_ENABLED =
          "hdds.container.checksum.verification.enabled";
  public static final boolean
          HDDS_CONTAINER_CHECKSUM_VERIFICATION_ENABLED_DEFAULT = true;

  public static final String OZONE_AUDIT_LOG_DEBUG_CMD_LIST_DNAUDIT =
      "ozone.audit.log.debug.cmd.list.dnaudit";

  public static final String HDDS_DATANODE_SLOW_OP_WARNING_THRESHOLD_KEY =
      "hdds.datanode.slow.op.warning.threshold";
  public static final String HDDS_DATANODE_SLOW_OP_WARNING_THRESHOLD_DEFAULT =
      "500ms";

  public static final String OZONE_DATANODE_IO_METRICS_PERCENTILES_INTERVALS_SECONDS_KEY =
      "ozone.volume.io.percentiles.intervals.seconds";

  public static final String HDDS_DATANODE_DNS_INTERFACE_KEY =
      "hdds.datanode.dns.interface";
  public static final String HDDS_DATANODE_DNS_NAMESERVER_KEY =
      "hdds.datanode.dns.nameserver";
  public static final String HDDS_DATANODE_HOST_NAME_KEY =
      "hdds.datanode.hostname";
  public static final String HDDS_DATANODE_USE_DN_HOSTNAME =
      "hdds.datanode.use.datanode.hostname";
  public static final boolean HDDS_DATANODE_USE_DN_HOSTNAME_DEFAULT = false;

  public static final String HDDS_XFRAME_OPTION_ENABLED = "hdds.xframe.enabled";
  public static final boolean HDDS_XFRAME_OPTION_ENABLED_DEFAULT = true;
  public static final String HDDS_XFRAME_OPTION_VALUE = "hdds.xframe.value";
  public static final String HDDS_XFRAME_OPTION_VALUE_DEFAULT = "SAMEORIGIN";

  public static final String HDDS_METRICS_SESSION_ID_KEY =
      "hdds.metrics.session-id";

  public static final String HDDS_DATANODE_KERBEROS_PRINCIPAL_KEY =
      "hdds.datanode.kerberos.principal";
  public static final String HDDS_DATANODE_KERBEROS_KEYTAB_FILE_KEY =
      "hdds.datanode.kerberos.keytab.file";
  public static final String HDDS_METRICS_PERCENTILES_INTERVALS_KEY =
      "hdds.metrics.percentiles.intervals";

  /** Do not instantiate. */
  private HddsConfigKeys() {
  }
}
