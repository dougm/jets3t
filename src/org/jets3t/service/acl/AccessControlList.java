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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jets3t.service.Constants;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Owner;

/**
 * Represents an Amazon S3 Access Control List (ACL), including the ACL's set of grantees and the
 * permissions assigned to each grantee.
 * <p>
 * 
 * </p>
 * 
 * @author James Murty
 *
 */
public class AccessControlList implements Serializable {	
    private static final long serialVersionUID = 8095040648034788376L;

    /**
     * A pre-canned REST ACL to set an object's permissions to Private (only owner can read/write)
     */
	public static final AccessControlList REST_CANNED_PRIVATE = new AccessControlList(); 

    /**
     * A pre-canned REST ACL to set an object's permissions to Public Read (anyone can read, only owner 
     * can write)
     */
	public static final AccessControlList REST_CANNED_PUBLIC_READ = new AccessControlList();
    
    /**
     * A pre-canned REST ACL to set an object's permissions to Public Read and Write (anyone can 
     * read/write)
     */
	public static final AccessControlList REST_CANNED_PUBLIC_READ_WRITE = new AccessControlList();
    
    /**
     * A pre-canned REST ACL to set an object's permissions to Authenticated Read (authenticated Amazon 
     * users can read, only owner can write)
     */
	public static final AccessControlList REST_CANNED_AUTHENTICATED_READ = new AccessControlList(); 
	
	private HashSet grants = new HashSet();
	private S3Owner owner = null;
    
    /**
     * Returns a string representation of the ACL contents, useful for debugging.
     */
	public String toString() {
		return "AccessControlList [owner=" + owner + ", grants=" + getGrants() + "]";
	}
	    
	public S3Owner getOwner() {
		return owner;
	}

	public void setOwner(S3Owner owner) {
		this.owner = owner;
	}

    /**
     * Adds a grantee to the ACL with the given permission. If this ACL already contains the grantee
     * (ie the same grantee object) the permission for the grantee will be updated.
     * 
     * @param grantee
     *        the grantee to whom the permission will apply
     * @param permission
     *        the permission to apply to the grantee.
     */
	public void grantPermission(GranteeInterface grantee, Permission permission) {
		grants.add(new GrantAndPermission(grantee, permission));
	}
	
    /**
     * Adds a set of grantee/permission pairs to the ACL, where each item in the set is a
     * {@link GrantAndPermission} object.
     *  
     * @param grants
     *        a set of {@link GrantAndPermission} objects 
     */
	public void grantAllPermissions(Set grants) {
		for (Iterator iter = grants.iterator(); iter.hasNext();) {
			GrantAndPermission gap = (GrantAndPermission) iter.next();
			grantPermission(gap.getGrantee(), gap.getPermission());
		}
	}
	
    /**
     * Revokes the permissions of a grantee by removing the grantee from the ACL. 
     *  
     * @param grantee
     *        the grantee to remove from this ACL.
     */
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

    /**
     * @return 
     * the set of {@link GrantAndPermission} objects in this ACL. 
     */
	public Set getGrants() {
		return grants;
	}	
	
    /**
     * @return
     * an XML representation of the Access Control List object, suitable to send in a request to S3. 
     */
    /*
     * This method is a nasty hack - we should build the XML document in a more professional way...
     */
	public String toXml() throws S3ServiceException {
        if (owner == null) {
            throw new S3ServiceException("Invalid AccessControlList: missing an S3Owner");
        }
        
		StringBuffer sb = new StringBuffer();
		sb.append(
			"<AccessControlPolicy xmlns=\"" + Constants.XML_NAMESPACE + "\">" +
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
    
    /**
     * @return
     * true if this ACL is a REST pre-canned one, in which case REST/HTTP implementations can use
     * the <tt>x-amz-acl</tt> header as a short-cut to set permissions on upload rather than using 
     * a full ACL XML document.
     */
    public boolean isCannedRestACL() {
        return (this.equals(AccessControlList.REST_CANNED_AUTHENTICATED_READ)
            || this.equals(AccessControlList.REST_CANNED_PRIVATE)
            || this.equals(AccessControlList.REST_CANNED_PUBLIC_READ)
            || this.equals(AccessControlList.REST_CANNED_PUBLIC_READ_WRITE));        
    }
		
}
