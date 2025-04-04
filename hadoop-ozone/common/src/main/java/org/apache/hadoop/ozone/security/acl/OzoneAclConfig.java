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

package org.apache.hadoop.ozone.security.acl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.hdds.conf.Config;
import org.apache.hadoop.hdds.conf.ConfigGroup;
import org.apache.hadoop.hdds.conf.ConfigTag;
import org.apache.hadoop.hdds.conf.ConfigType;
import org.apache.hadoop.ozone.security.acl.IAccessAuthorizer.ACLType;

/**
 * Ozone ACL config pojo.
 * */
@ConfigGroup(prefix = "ozone.om")
public class OzoneAclConfig {

  // OM Default user/group permissions
  @Config(key = "user.rights",
      defaultValue = "ALL",
      type = ConfigType.STRING,
      tags = {ConfigTag.OM, ConfigTag.SECURITY},
      description = "Default user permissions set for an object in " +
          "OzoneManager."
  )
  private String userDefaultRights;

  @Config(key = "group.rights",
      defaultValue = "READ, LIST",
      type = ConfigType.STRING,
      tags = {ConfigTag.OM, ConfigTag.SECURITY},
      description = "Default group permissions set for an object in " +
          "OzoneManager."
  )
  private String groupDefaultRights;

  public ACLType[] getUserDefaultRights() {
    List<ACLType> types = new ArrayList();
    if (userDefaultRights == null) {
      types.add(ACLType.ALL);
    } else {
      String[] array = userDefaultRights.trim().split(",");
      Arrays.stream(array).forEach(t -> types.add(ACLType.valueOf(t.trim())));
    }
    return types.toArray(new ACLType[0]);
  }

  public ACLType[] getGroupDefaultRights() {
    List<ACLType> types = new ArrayList();
    if (groupDefaultRights == null) {
      types.add(ACLType.READ);
      types.add(ACLType.LIST);
    } else {
      String[] array = groupDefaultRights.trim().split(",");
      Arrays.stream(array).forEach(t -> types.add(ACLType.valueOf(t.trim())));
    }
    return types.toArray(new ACLType[0]);
  }
}
