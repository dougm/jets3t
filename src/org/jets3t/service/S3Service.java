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

import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.executor.S3ServiceExecutor;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.utils.RestUtils;
import org.jets3t.service.utils.ServiceUtils;

/**
 * A service that handles communication with S3, performing all available actions.
 * <p>
 * This class must be extended by implementation classes that perform the communication with S3 via
 * a particular interface, such as REST or SOAP.
 * <p>
 * Implementations of <code>S3Service</code> must be thread-safe as they will probably be used by
 * the multi-threaded {@link S3ServiceExecutor}.
 * 
 * @author James Murty
 */
public abstract class S3Service {

    private AWSCredentials awsCredentials = null;
    protected boolean isHttpsOnly = false;

    /**
     * Construct an <code>S3Service</code> identified by the given AWS Principal.
     * 
     * @param awsPrincipal
     * the S3 user credentials to use when communicating with S3, may be null in which case the
     * communication is done as an anonymous user.
     * @throws S3ServiceException
     */
    protected S3Service(AWSCredentials awsPrincipal) throws S3ServiceException {
        this.awsCredentials = awsPrincipal;
    }

    /**
     * @return true if this service has <code>AWSCredentials</code> identifying an S3 user, false
     * if the service is acting as an anonymous user.
     */
    public boolean isAuthenticatedConnection() {
        return awsCredentials != null;
    }

    /**
     * @return the AWS Credentials identifying the S3 user, may be null if the service is acting
     * anonymously.
     */
    public AWSCredentials getAWSCredentials() {
        return awsCredentials;
    }
    
    /**
     * Generates a signed URL string that will grant access to an S3 resource (bucket or object)
     * to whoever uses the URL up until the time specified.
     * 
     * @param bucketName
     * the name of the bucket to include in the URL, must be a valid bucket name.
     * @param objectKey
     * the name of the object to include in the URL, if null only the bucket name is used. 
     * @param awsCredentials
     * the credentials of someone with sufficient privileges to grant access to the bucket/object 
     * @param secondsSinceEpoch
     * the time after which URL's signature will no longer be valid. This time cannot be null.
     *  <b>Note:</b> This time is specified in seconds since the epoch, not milliseconds. 
     * @param isSecure
     * if true an HTTPS URL is created and data accessed using the generated URL will be encrypted
     * when accessed, otherwise a standard HTTP URL is created.
     * @return
     * a URL signed in such a way as to grant access to an S3 resource to whoever uses it.
     * @throws S3ServiceException
     */
    public static String createSignedUrl(String bucketName, String objectKey, AWSCredentials awsCredentials, 
        long secondsSinceEpoch, boolean isSecure) throws S3ServiceException
    {
        String fullKey = bucketName + (objectKey != null ? "/" + objectKey : "");
        fullKey += "?AWSAccessKeyId=" + awsCredentials.getAccessKey();
        fullKey += "&Expires=" + secondsSinceEpoch;

        String canonicalString = RestUtils.makeCanonicalString("GET", "/" + fullKey, null, String
            .valueOf(secondsSinceEpoch));

        String signedCanonical = ServiceUtils.signWithHmacSha1(awsCredentials.getSecretKey(),
            canonicalString);
        String encodedCanonical = RestUtils.encodeUrlString(signedCanonical);
        fullKey += "&Signature=" + encodedCanonical;

        if (isSecure) {
            return "https://" + Constants.REST_SERVER_DNS + "/" + fullKey;
        } else {
            return "http://" + Constants.REST_SERVER_DNS + "/" + fullKey;            
        }
    }
    
    /////////////////////////////////////////////////////////////////////////////
    // Assertion methods used to sanity-check parameters provided to this service
    /////////////////////////////////////////////////////////////////////////////
    
    /**
     * Throws an exception if this service is anonymous (that is, it was created without
     * an <code>AWSCredentials</code> object representing an S3 user account.
     * @param action
     * the action being attempted which this assertion is applied, for debugging purposes.
     * @throws S3ServiceException
     */
    protected void assertAuthenticatedConnection(String action) throws S3ServiceException {
        if (!isAuthenticatedConnection()) {
            throw new S3ServiceException(
                "The requested action cannot be performed with a non-authenticated S3 Service: "
                    + action);
        }
    }

    /**
     * Throws an exception if a bucket is null or contains a null/empty name.
     * @param bucket
     * @param action
     * the action being attempted which this assertion is applied, for debugging purposes.
     * @throws S3ServiceException
     */
    protected void assertValidBucket(S3Bucket bucket, String action) throws S3ServiceException {
        if (bucket == null || bucket.getName() == null || bucket.getName().length() == 0) {
            throw new S3ServiceException("The action " + action
                + " cannot be performed with an invalid bucket: " + bucket);
        }
    }

    /**
     * Throws an exception if an object is null or contains a null/empty key.
     * @param object
     * @param action
     * the action being attempted which this assertion is applied, for debugging purposes.
     * @throws S3ServiceException
     */
    protected void assertValidObject(S3Object object, String action) throws S3ServiceException {
        if (object == null || object.getKey() == null || object.getKey().length() == 0) {
            throw new S3ServiceException("The action " + action
                + " cannot be performed with an invalid object: " + object);
        }
    }    
    
    /////////////////////////////////////////////////
    // Methods below this point perform actions in S3
    /////////////////////////////////////////////////

    /**
     * Lists the objects in a bucket.
     * <p>
     * This method can be performed by anonymous services.
     * 
     * @param bucket
     * the bucket whose contents will be listed. 
     * This must be a valid S3Bucket object that is non-null and contains a name.
     * @return
     * the set of objects contained in a bucket.
     * @throws S3ServiceException
     */
    public S3Object[] listObjects(S3Bucket bucket) throws S3ServiceException {
        assertValidBucket(bucket, "listObjects");
        return listObjects(bucket, null, null, Constants.DEFAULT_OBJECT_LIST_CHUNK_SIZE);
    }

    /**
     * Lists the objects in a bucket matching a prefix.
     * <p>
     * This method can be performed by anonymous services.
     * 
     * @param bucket
     * the bucket whose contents will be listed. 
     * This must be a valid S3Bucket object that is non-null and contains a name.
     * @param prefix
     * only objects with a key that starts with this prefix will be listed
     * @param delimiter
     * only list objects with key names up to this delimiter, may be null.
     * <b>Note</b>: If a non-null delimiter is specified, the prefix must include enough text to
     * reach the first occurrence of the delimiter in the bucket's keys, or no results will be returned.
     * @return
     * the set of objects contained in a bucket whose keys start with the given prefix.
     * @throws S3ServiceException
     */
    public S3Object[] listObjects(S3Bucket bucket, String prefix, String delimiter) throws S3ServiceException {
        assertValidBucket(bucket, "listObjects");
        return listObjects(bucket, prefix, delimiter, Constants.DEFAULT_OBJECT_LIST_CHUNK_SIZE);
    }

    /**
     * Creates a bucket.
     * <p>
     * This method cannot be performed by anonymous services.
     * 
     * @param bucketName
     * the name of the bucket to create
     * @return
     * the created bucket object. <b>Note:</b> the object returned has minimal information about
     * the bucket that was created, including only the bucket's name.
     * @throws S3ServiceException
     */
    public S3Bucket createBucket(String bucketName) throws S3ServiceException {
        assertAuthenticatedConnection("createBucket");
        S3Bucket bucket = new S3Bucket();
        bucket.setName(bucketName);
        return createBucket(bucket);
    }

    /**
     * Retrieves the access control settings of a bucket. 
     * <p>
     * This method can be performed by anonymous services.
     * 
     * @param bucket
     * the bucket whose access control settings will be returned.
     * This must be a valid S3Bucket object that is non-null and contains a name.
     * @return
     * the access control settings for the bucket
     * @throws S3ServiceException
     */
    public AccessControlList getAcl(S3Bucket bucket) throws S3ServiceException {
        assertValidBucket(bucket, "getAcl");
        return getAcl(bucket, null);
    }

    /**
     * Sets the access control settings for the given bucket.
     * <p>
     * This method can be performed by anonymous services.
     * 
     * @param bucket
     * the bucket whose access control settings will be updated.
     * This must be a valid S3Bucket object that is non-null and contains a name.
     * @throws S3ServiceException
     */
    public void putAcl(S3Bucket bucket) throws S3ServiceException {
        assertValidBucket(bucket, "putAcl");
        putAcl(bucket, null);
    }

    /**
     * Returns an object representing the details and data of an item in S3, without applying any
     * preconditions.
     * <p>
     * This method can be performed by anonymous services.
     *  
     * @param bucket
     * the bucket containing the object.
     * This must be a valid S3Bucket object that is non-null and contains a name.
     * @param objectKey
     * the key identifying the object.
     * @return
     * the object with the given key in S3, including the object's data input stream.
     * @throws S3ServiceException
     */
    public S3Object getObject(S3Bucket bucket, String objectKey) throws S3ServiceException {
        assertValidBucket(bucket, "getObject");
        return getObject(bucket, objectKey, null, null, null, null, null, null);
    }

    /**
     * Returns an object representing the details of an item in S3 without the object's data, and
     * without applying any preconditions.
     * <p>
     * This method can be performed by anonymous services.
     * 
     * @param bucket
     * the bucket containing the object.
     * This must be a valid S3Bucket object that is non-null and contains a name.
     * @param objectKey
     * the key identifying the object.
     * @return
     * the object with the given key in S3, including only general details and metadata (not the data
     * input stream)
     * @throws S3ServiceException
     */
    public S3Object getObjectDetails(S3Bucket bucket, String objectKey) throws S3ServiceException {
        assertValidBucket(bucket, "getObjectDetails");
        return getObjectDetails(bucket, objectKey, null, null, null, null);
    }


    // /////////////////////////////////////////////////////////////////////////////////
    // Abstract methods that must be implemented by interface-specific S3Service classes
    // /////////////////////////////////////////////////////////////////////////////////

    /**
     * Indicates whether a bucket exists and is accessible to a service user. 
     * <p>
     * This method can be performed by anonymous services.
     * <p>
     * <b>Implementation notes</b><p>
     * This method can be implemented by attempting to list the objects in a bucket. If the listing
     * is successful return true, if the listing failed for any reason return false. 
     * 
     * @return
     * true if the bucket exists and is accessible to the service user, false otherwise.
     * @throws S3ServiceException
     */
    public abstract boolean isBucketAvailable(String bucketName) throws S3ServiceException;

    /**
     * Lists the buckets belonging to the service user. 
     * <p>
     * This method cannot be performed by anonymous services.
     * <p>
     * <b>Implementation notes</b><p>
     * Implementations should use the {@link #assertAuthenticatedConnection} assertion
     * before doing anything else, as this operation will fail in S3 without user credentials.
     * 
     * @return
     * the list of buckets owned by the service user.
     * @throws S3ServiceException
     */
    public abstract S3Bucket[] listAllBuckets() throws S3ServiceException;

    /**
     * Lists the objects in a bucket matching a prefix, chunking the results into batches of
     * a given size.
     * <p>
     * This method can be performed by anonymous services.
     * <p>
     * <b>Implementation notes</b><p>
     * The implementation of this method is expected to return <b>all</b> the objects
     * in a bucket, not a subset. This may require repeating the S3 list operation if the
     * first one doesn't include all the available objects, such as when the number of objects
     * is greater than <code>maxListingLength</code>.
     * <p>
     * Implementations should also start with the {@link #assertValidBucket} assertion.
     * 
     * @param bucket
     * the bucket whose contents will be listed. 
     * This must be a valid S3Bucket object that is non-null and contains a name.
     * @param prefix
     * only objects with a key that starts with this prefix will be listed
     * @param maxListingLength
     * the maximum number of objects to include in each result chunk
     * @return
     * the set of objects contained in a bucket whose keys start with the given prefix.
     * @throws S3ServiceException
     */
    public abstract S3Object[] listObjects(S3Bucket bucket, String prefix, 
        String delimiter, long maxListingLength) throws S3ServiceException;

    /**
     * Creates a bucket in S3 based on the provided bucket object.
     * <p>
     * This method cannot be performed by anonymous services.
     * <p>
     * <b>Implementation notes</b><p>
     * The implementing method must populate the bucket object's metadata with the results of the 
     * operation before returning the object. It must also apply any <code>AccessControlList</code> 
     * settings included with the bucket. 
     * <p>
     * Implementations should use the {@link #assertAuthenticatedConnection} and 
     * {@link #assertValidBucket} assertions.
     * 
     * @param bucket
     * an object representing the bucket to create which must be valid, and may contain ACL settings.
     * @return
     * the created bucket object, populated with all metadata made available by the creation operation. 
     * @throws S3ServiceException
     */
    public abstract S3Bucket createBucket(S3Bucket bucket) throws S3ServiceException;

    /**
     * Deletes an S3 bucket.
     * <p>
     * This method can be performed by anonymous services.
     * 
     * @param bucketName
     * the name of the bucket to delete.
     * @throws S3ServiceException
     */
    public abstract void deleteBucket(String bucketName) throws S3ServiceException;

    /**
     * Puts an object inside an existing bucket in S3, creating a new object or overwriting
     * an existing one with the same key.
     * <p>
     * This method can be performed by anonymous services.
     * <p>
     * <b>Implementation notes</b><p>
     * Implementations should use {@link #assertValidBucket} and {@link #assertValidObject} assertions.
     * 
     * @param bucket
     * the bucket inside which the object will be put, which must be valid.
     * @param object
     * the object containing all information that will be written to S3. At very least this object must
     * be valid. Beyond that it may contain: an input stream with the object's data content, metadata,
     * and access control settings.<p>
     * <b>Note:</b> It is very important to set the object's Content-Length to match the size of the 
     * data input stream when possible, as this can remove the need to read data into memory to
     * determine its size. 
     * 
     * @return
     * the object populated with any metadata information made available by S3. 
     * @throws S3ServiceException
     */
    public abstract S3Object putObject(S3Bucket bucket, S3Object object)
        throws S3ServiceException;

    /**
     * Deletes an object from a bucket in S3.
     * <p>
     * This method can be performed by anonymous services.
     * <p>
     * <b>Implementation notes</b><p>
     * Implementations should use {@link #assertValidBucket} assertion.
     * 
     * @param bucket
     * the bucket containing the object to be deleted.
     * @param objectKey
     * the key representing the object in S3.
     * @throws S3ServiceException
     */
    public abstract void deleteObject(S3Bucket bucket, String objectKey) throws S3ServiceException;

    /**
     * Returns an object representing the details of an item in S3 that meets any given preconditions.
     * The object is returned without the object's data.
     * <p>
     * An exception is thrown if any of the preconditions fail. 
     * Preconditions are only applied if they are non-null.
     * <p>
     * This method can be performed by anonymous services.
     * <p>
     * <b>Implementation notes</b><p>
     * Implementations should use {@link #assertValidBucket} assertion.
     * 
     * @param bucket
     * the bucket containing the object.
     * This must be a valid S3Bucket object that is non-null and contains a name.
     * @param objectKey
     * the key identifying the object.
     * @param ifModifiedSince
     * a precondition specifying a date after which the object must have been modified, ignored if null.
     * @param ifUnmodifiedSince
     * a precondition specifying a date after which the object must not have been modified, ignored if null.
     * @param ifMatchTags
     * a precondition specifying an MD5 hash the object must match, ignored if null.
     * @param ifNoneMatchTags
     * a precondition specifying an MD5 hash the object must not match, ignored if null.
     * @return
     * the object with the given key in S3, including only general details and metadata (not the data
     * input stream)
     * @throws S3ServiceException
     */
    public abstract S3Object getObjectDetails(S3Bucket bucket, String objectKey,
        Calendar ifModifiedSince, Calendar ifUnmodifiedSince, String[] ifMatchTags,
        String[] ifNoneMatchTags) throws S3ServiceException;

    /**
     * Returns an object representing the details of an item in S3 that meets any given preconditions.
     * The object is returned with the object's data.
     * <p>
     * An exception is thrown if any of the preconditions fail. 
     * Preconditions are only applied if they are non-null.
     * <p>
     * This method can be performed by anonymous services.
     * <p>
     * <b>Implementation notes</b><p>
     * Implementations should use {@link #assertValidBucket} assertion.
     * 
     * @param bucket
     * the bucket containing the object.
     * This must be a valid S3Bucket object that is non-null and contains a name.
     * @param objectKey
     * the key identifying the object.
     * @param ifModifiedSince
     * a precondition specifying a date after which the object must have been modified, ignored if null.
     * @param ifUnmodifiedSince
     * a precondition specifying a date after which the object must not have been modified, ignored if null.
     * @param ifMatchTags
     * a precondition specifying an MD5 hash the object must match, ignored if null.
     * @param ifNoneMatchTags
     * a precondition specifying an MD5 hash the object must not match, ignored if null.
     * @param byteRangeStart
     * include only a portion of the object's data - starting at this point, ignored if null. 
     * @param byteRangeEnd
     * include only a portion of the object's data - ending at this point, ignored if null. 
     * @return
     * the object with the given key in S3, including only general details and metadata (not the data
     * input stream)
     * @throws S3ServiceException
     */
    public abstract S3Object getObject(S3Bucket bucket, String objectKey, Calendar ifModifiedSince,
        Calendar ifUnmodifiedSince, String[] ifMatchTags, String[] ifNoneMatchTags,
        Long byteRangeStart, Long byteRangeEnd) throws S3ServiceException;

    /**
     * Applies access control settings to a bucket or object. The ACL settings must be included
     * with the object itself, that is inside the bucket or object.
     * <p>
     * <b>Implementation notes</b><p>
     * Implementing methods must check whether <code>object</code> is null, and if so update
     * the bucket's ACL settings. Otherwise the object's ACL settings are updated.
     * <p>
     * Implementations should use {@link #assertValidBucket} assertion in all cases, and
     * {@link #assertValidObject} if <code>object</code> is non-null. 
     * 
     * @param bucket
     * a bucket with ACL settings to apply (if object is null) or the bucket containing the object
     * whose ACL settings will be updated (if object is non-null).
     * @param object
     * if non-null, the object with ACL settings that will be applied. Ignored if null.
     * @throws S3ServiceException
     */
    public abstract void putAcl(S3Bucket bucket, S3Object object) throws S3ServiceException;

    /**
     * Retrieves the acces control settings of a bucket or object.
     * 
     * @param bucket
     * the bucket whose ACL settings will be retrieved (if objectKey is null) or the bucket containing the 
     * object whose ACL settings will be retrieved (if objectKey is non-null).
     * @param objectKey
     * if non-null, the key of the object whose ACL settings will be retrieved. Ignored if null.
     * @return
     * the ACL settings of the bucket or object.
     * @throws S3ServiceException
     */
    public abstract AccessControlList getAcl(S3Bucket bucket, String objectKey)
        throws S3ServiceException;

}
