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

/**
 * Simple container object to combine a grantee object with a permission object.
 * 
 * @author James Murty
 */
public class GrantAndPermission {
	private GranteeInterface grantee = null;
	private Permission permission = null;
	
	public GrantAndPermission(GranteeInterface grantee, Permission permission) {
		this.grantee = grantee;
		this.permission = permission;
	}

	public GranteeInterface getGrantee() {
		return grantee;
	}

	public Permission getPermission() {
		return permission;
	}
	
	public int hashCode() {
		return (grantee + ":" + permission.toString()).hashCode();
	}
	
	public boolean equals(Object obj) {
		return (obj instanceof GrantAndPermission
			&& this.getGrantee().getIdentifier().equals(((GrantAndPermission)obj).getGrantee().getIdentifier())
			&& this.getPermission().equals(((GrantAndPermission)obj).getPermission())
			);
	}

}
