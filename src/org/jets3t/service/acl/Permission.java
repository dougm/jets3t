/*
 * jets3t : Java Extra-Tasty S3 Toolkit (for Amazon S3 online storage service)
 * This is a java.net project, see https://jets3t.dev.java.net/
 * 
 * Copyright 2006 James Murty
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.jets3t.service.acl;


public class Permission {
    public static final Permission PERMISSION_FULL_CONTROL = new Permission("FULL_CONTROL");
    public static final Permission PERMISSION_READ = new Permission("READ");
    public static final Permission PERMISSION_WRITE = new Permission("WRITE");
    public static final Permission PERMISSION_READ_ACP = new Permission("READ_ACP");
    public static final Permission PERMISSION_WRITE_ACP = new Permission("WRITE_ACP");

    private String permissionString = "";

    private Permission(String permissionString) {
        this.permissionString = permissionString;
    }
    
    public static Permission parsePermission(String str) {
        Permission permission = null;
        if (str == null) {
            // Do nothing
        } else if (str.equals(PERMISSION_FULL_CONTROL.toString())) {
            permission = PERMISSION_FULL_CONTROL;
        } else if (str.equals(PERMISSION_READ.toString())) {
            permission = PERMISSION_READ;
        } else if (str.equals(PERMISSION_WRITE.toString())) {
            permission = PERMISSION_WRITE;
        } else if (str.equals(PERMISSION_READ_ACP.toString())) {
            permission = PERMISSION_READ_ACP;
        } else if (str.equals(PERMISSION_WRITE_ACP.toString())) {
            permission = PERMISSION_WRITE_ACP;
        } else {
            permission = null;
        }
        return permission;
    }    

    public String toString() {
        return permissionString;
    }

    public int hashCode() {
        return permissionString.hashCode();
    }

    public boolean equals(Object obj) {
        return (obj instanceof Permission) && toString().equals(obj.toString());
    }

}
