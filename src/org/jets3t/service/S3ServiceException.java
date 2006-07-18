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
package org.jets3t.service;

/**
 * Exception for use by <code>S3Service</code>s and related utilities.
 * This exception can hold useful additional information about errors that occur
 * when communicating with S3.
 *  
 * @author James Murty
 */
public class S3ServiceException extends Exception {
	private static final long serialVersionUID = -7025759441563263552L;

	private String xmlMessage = null;

	public S3ServiceException(String message, String xmlMessage) {
		super(message);
		this.xmlMessage = xmlMessage;
	}

	public S3ServiceException() {
		super();
	}
	
	public S3ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public S3ServiceException(String message) {
		super(message);
	}

	public S3ServiceException(Throwable cause) {
		super(cause);
	}
	
	public String toString() {
		return super.toString() + (xmlMessage != null? " XML Error Message: " + xmlMessage: "");
	}
	
	public String getErrorCode() {
		if (xmlMessage != null && xmlMessage.indexOf("<Code>") >= 0) {
			int startIndex = xmlMessage.indexOf("<Code>") + 6;
			int endIndex = xmlMessage.indexOf("</Code>");
			return xmlMessage.substring(startIndex, endIndex);
		} else {
			return null;
		}
	}
	
}
