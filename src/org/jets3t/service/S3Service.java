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

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.impl.rest.RestUtils;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.impl.soap.axis.SoapS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.utils.ServiceUtils;

public abstract class S3Service {
	private final Log log = LogFactory.getLog(S3Service.class);
	
	public static final String SERVICE_TYPE_REST = "REST";
	public static final String SERVICE_TYPE_HTTP = "REST"; // Synonym for REST.
	public static final String SERVICE_TYPE_SOAP = "SOAP";

	protected AWSCredentials awsCredentials = null;
	
	
	public static S3Service getS3Service(String serviceType) throws S3ServiceException {
		return getS3Service(serviceType, null);
	}	

	public static S3Service getS3Service(String serviceType, AWSCredentials awsCredentials) throws S3ServiceException {
		if (SERVICE_TYPE_REST.equals(serviceType)) {			
			return new RestS3Service(awsCredentials);
		} else if (SERVICE_TYPE_SOAP.equals(serviceType)) {
			return new SoapS3Service(awsCredentials);
		} else {
			throw new S3ServiceException("Unknown S3Service type '" + serviceType + "'. " +
				"Service type must be one of: " + SERVICE_TYPE_REST + ", " + SERVICE_TYPE_SOAP);
		}
	}

	/**
	 * Constructor.
	 * @param awsPrincipal
	 * @throws S3ServiceException
	 */
	protected S3Service(AWSCredentials awsPrincipal) throws S3ServiceException {
		this.awsCredentials = awsPrincipal;
	}	
	
	public boolean isAuthenticatedConnection() {
		return awsCredentials != null;
	}

	public AWSCredentials getAWSCredentials() {
		return awsCredentials;
	}
	
	public static long countBytesInObjects(S3Object[] objects) {
		long byteTotal = 0;
		for (int i = 0; objects != null && i < objects.length; i++) {
			byteTotal += objects[i].getContentLength();
		}
		return byteTotal;
	}
		
	public S3Object[] listObjects(S3Bucket bucket) throws S3ServiceException {
		return listObjects(bucket, null, Constants.OBJECT_LIST_CHUNK_SIZE);
	}
	
	public S3Object[] listObjects(S3Bucket bucket, String prefix) throws S3ServiceException {
		return listObjects(bucket, prefix, Constants.OBJECT_LIST_CHUNK_SIZE);
	}
	
	public S3Bucket createBucket(String bucketName) throws S3ServiceException {
		S3Bucket bucket = new S3Bucket();
		bucket.setName(bucketName);
		return createBucket(bucket);
	}
		
	public AccessControlList getAcl(S3Bucket bucket) throws S3ServiceException {
		return getAcl(bucket, null);
	}
    
    public void putAcl(S3Bucket bucket) throws S3ServiceException {
        putAcl(bucket, null);
    }    

	public S3Object getObject(S3Bucket bucket, String objectKey) throws S3ServiceException {
		return getObject(bucket, objectKey, null, null, null, null, null, null);
	}	

	public S3Object getObjectDetails(S3Bucket bucket, String objectKey) throws S3ServiceException {
		return getObjectDetails(bucket, objectKey, null, null, null, null);
	}
	
	public String createSignedUrl(String bucketName, String objectKey, AWSCredentials awsCredentials, long secondsSinceEpoch) 
		throws S3ServiceException
    {
        String fullKey = bucketName + (objectKey != null? "/" + objectKey : "");
        fullKey += "?AWSAccessKeyId=" + awsCredentials.getAccessKey();
        fullKey += "&Expires=" + secondsSinceEpoch;
                    
        String canonicalString = RestUtils.makeCanonicalString("GET", "/" + fullKey, 
            null, String.valueOf(secondsSinceEpoch));
        log.debug("Canonical string ('|' is a newline): " + canonicalString.replace('\n', '|'));
        
        String signedCanonical = ServiceUtils.signWithHmacSha1(
            awsCredentials.getSecretKey(), canonicalString);
        String encodedCanonical = RestUtils.encodeUrlString(signedCanonical);
        fullKey += "&Signature=" + encodedCanonical;
        
        return "http://" + Constants.REST_SERVER_DNS + "/" + fullKey;
    }    
    
    protected void assertAuthenticatedConnection(String action) throws S3ServiceException {
        if (!isAuthenticatedConnection()) {
            throw new S3ServiceException("The requested action cannot be performed with a non-authenticated S3 Service: "
                + action);
        }        
    }
    
    protected void assertValidBucket(S3Bucket bucket, String action) throws S3ServiceException {
        if (bucket == null || bucket.getName() == null || bucket.getName().length() == 0) {
            throw new S3ServiceException("The action " + action + " cannot be performed with an invalid bucket: " + bucket);
        }
    }
    
    protected void assertValidObject(S3Object object, String action) throws S3ServiceException {
        if (object == null || object.getKey() == null || object.getKey().length() == 0) {
            throw new S3ServiceException("The action " + action + " cannot be performed with an invalid object: " + object);
        }
    }

	///////////////////////////////////////////////////////////////////////////////////////////////////////
	// Abstract methods that must be implemented by S3Service implementations for particular transports. //
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public abstract S3Bucket[] listAllBuckets() throws S3ServiceException;

	public abstract S3Bucket getBucket(String bucketName) throws S3ServiceException;

	public abstract S3Object[] listObjects(S3Bucket bucket, String prefix, long maxListingLength) throws S3ServiceException; 

	public abstract S3Bucket createBucket(S3Bucket bucket) throws S3ServiceException;
    
    public abstract void deleteBucket(String bucketName) throws S3ServiceException;
	
	public abstract S3Object createObject(S3Bucket bucket, S3Object object) throws S3ServiceException;

    public abstract void deleteObject(S3Bucket bucket, String objectKey) throws S3ServiceException;
    
	public abstract S3Object getObjectDetails(S3Bucket bucket, String objectKey, 
        Calendar ifModifiedSince, Calendar ifUnmodifiedSince, 
        String[] ifMatchTags, String[] ifNoneMatchTags) throws S3ServiceException; 

	public abstract S3Object getObject(S3Bucket bucket, String objectKey, 
        Calendar ifModifiedSince, Calendar ifUnmodifiedSince, 
        String[] ifMatchTags, String[] ifNoneMatchTags, 
        Long byteRangeStart, Long byteRangeEnd) throws S3ServiceException; 

	public abstract void putAcl(S3Bucket bucket, S3Object object) throws S3ServiceException;

    public abstract AccessControlList getAcl(S3Bucket bucket, String objectKey) throws S3ServiceException;
	
}
