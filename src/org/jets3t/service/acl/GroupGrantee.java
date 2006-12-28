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
 * Represents a Group grantee.
 * <p>
 * Only three groups are available in S3:<br>
 * <tt>ALL_USERS</tt>: The general public<br>
 * <tt>AUTHENTICATED_USERS</tt>: Authenticated Amazon S3 users<br> 
 * <tt>LOG_DELIVERY</tt>: Amazon's S3 Log Delivery group, who deliver bucket log files<br> 
 * 
 * @author James Murty
 *
 */
public class GroupGrantee implements GranteeInterface {
	public static final GroupGrantee ALL_USERS = new GroupGrantee("http://acs.amazonaws.com/groups/global/AllUsers");
	public static final GroupGrantee AUTHENTICATED_USERS = new GroupGrantee("http://acs.amazonaws.com/groups/global/AuthenticatedUsers");
    public static final GroupGrantee LOG_DELIVERY = new GroupGrantee("http://acs.amazonaws.com/groups/s3/LogDelivery");
	
	private String uri = null;
	
	public GroupGrantee() {
	}

    /**
     * Constructs a group grantee object using the given group URI as an identifier.
     * <p>
     * <b>Note</b>: All possible group types are available as public static variables from this class,
     * so this constructor should rarely be necessary.  
     * 
     * @param groupUri
     */
	public GroupGrantee(String groupUri) {
		this.uri = groupUri;
	}
	
	public String toXml() {
		return "<Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"Group\"><URI>" + uri + "</URI></Grantee>";
	}

	public void setIdentifier(String id) {
		uri = id;		
	}
	
	public String getIdentifier() {
		return uri;
	}
	
	public String toString() {
		return "GroupGrantee [" + uri + "]";
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof GroupGrantee) {
			return uri.equals(((GroupGrantee)obj).uri);
		} 
		return false;
	}
	
	public int hashCode() {		
		return uri.hashCode();
	}
	
}
