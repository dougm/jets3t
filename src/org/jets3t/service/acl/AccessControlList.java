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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jets3t.service.Constants;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Owner;

public class AccessControlList {	
	// TODO Warning, these are place-holders only.
	public static final AccessControlList REST_CANNED_PRIVATE = new AccessControlList(); 
	public static final AccessControlList REST_CANNED_PUBLIC_READ = new AccessControlList(); 
	public static final AccessControlList REST_CANNED_PUBLIC_READ_WRITE = new AccessControlList(); 
	public static final AccessControlList REST_CANNED_AUTHENTICATED_READ = new AccessControlList(); 
	
	private HashSet grants = new HashSet();
	private S3Owner owner = null;
    
	public String toString() {
		return "AccessControlList [owner=" + owner + ", grants=" + getGrants() + "]";
	}
	
	public S3Owner getOwner() {
		return owner;
	}

	public void setOwner(S3Owner owner) {
		this.owner = owner;
	}

	public void grantPermission(GranteeInterface grantee, Permission permission) {
		grants.add(new GrantAndPermission(grantee, permission));
	}
	
	public void grantAllPermissions(Set grants) {
		for (Iterator iter = grants.iterator(); iter.hasNext();) {
			GrantAndPermission gap = (GrantAndPermission) iter.next();
			grantPermission(gap.getGrantee(), gap.getPermission());
		}
	}
	
	public void revokeAllPermissions(GranteeInterface grantee) {
		ArrayList grantsToRemove = new ArrayList();
		for (Iterator iter = grants.iterator(); iter.hasNext();) {
			GrantAndPermission gap = (GrantAndPermission) iter.next();
			if (gap.getGrantee().equals(grantee)) {
				grantsToRemove.add(gap);				
			}
		}
		grants.removeAll(grantsToRemove);
	}

	public Set getGrants() {
		return grants;
	}	
	
	// TODO Fix this...
	public String toXml() throws S3ServiceException {
        if (owner == null) {
            throw new S3ServiceException("Invalid AccessControlList: missing an S3Owner");
        }
        
		StringBuffer sb = new StringBuffer();
		sb.append(
			"<AccessControlPolicy xmlns=\"" + Constants.ACL_NAMESPACE + "\">" +
                "<Owner>" +
                    "<ID>" + owner.getId() + "</ID>" +
                    "<DisplayName>" + owner.getDisplayName() + "</DisplayName>" +
                "</Owner>" +
				"<AccessControlList>");
		
		Iterator grantIter = grants.iterator();
		while (grantIter.hasNext()) {
			GrantAndPermission gap = (GrantAndPermission) grantIter.next();
			GranteeInterface grantee = gap.getGrantee();
			Permission permission = gap.getPermission();
			sb.append(
					"<Grant>" +
						grantee.toXml() +
						"<Permission>" + permission + "</Permission>" +
					"</Grant>"
					);
		}
		
		sb.append(
				"</AccessControlList>" +
			"</AccessControlPolicy>"
				); 
		
		return sb.toString();
	}
    
    public boolean isCannedRestACL() {
        return (this.equals(AccessControlList.REST_CANNED_AUTHENTICATED_READ)
            || this.equals(AccessControlList.REST_CANNED_PRIVATE)
            || this.equals(AccessControlList.REST_CANNED_PUBLIC_READ)
            || this.equals(AccessControlList.REST_CANNED_PUBLIC_READ_WRITE));        
    }
		
}
