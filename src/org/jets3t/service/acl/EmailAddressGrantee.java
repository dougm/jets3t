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

public class EmailAddressGrantee implements GranteeInterface {
	private String emailAddress = null;
	
	public EmailAddressGrantee() {
	}
	
	public EmailAddressGrantee(String emailAddress) {
		this.setIdentifier(emailAddress);
	}
	
	public String toXml() {
		return "<Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"AmazonCustomerByEmail\"><EmailAddress>" + emailAddress + "</EmailAddress></Grantee>";
	}

	public void setIdentifier(String id) {
		emailAddress = id;
	}
	
	public String getIdentifier() {
		return emailAddress;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof EmailAddressGrantee) {
			return emailAddress.equals(((EmailAddressGrantee)obj).emailAddress);
		} 
		return false;
	}
	
	public int hashCode() {		
		return emailAddress.hashCode();
	}

}
