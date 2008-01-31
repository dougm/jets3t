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

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.GrantAndPermission;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3BucketLoggingStatus;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.multithread.S3ServiceMulti;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.utils.RestUtils;
import org.jets3t.service.utils.ServiceUtils;

/**
 * A service that handles communication with S3, offering all the operations that can be performed
 * on S3 accounts.
 * <p>
 * This class must be extended by implementation classes that perform the communication with S3 via
 * a particular interface, such as REST or SOAP. Implementations provided with the JetS3t suite 
 * include {@link org.jets3t.service.impl.rest.httpclient.RestS3Service} and 
 * {@link org.jets3t.service.impl.soap.axis.SoapS3Service}.
 * </p>
 * <p>
 * Implementations of <code>S3Service</code> must be thread-safe as they will probably be used by
 * the multi-threaded service class {@link S3ServiceMulti}. 
 * </p>
 * <p>
 * This class uses properties obtained through {@link Jets3tProperties}. For more information on 
 * these properties please refer to 
 * <a href="http://jets3t.s3.amazonaws.com/toolkit/configuration.html">JetS3t Configuration</a>
 * </p>
 * 
 * @author James Murty
 */
public abstract class S3Service implements Serializable {
    private static final Log log = LogFactory.getLog(S3Service.class);
    
    /**
     * The JetS3t suite version number implemented by this service: 0.5.1 
     */
    public static final String VERSION_NO__JETS3T_TOOLKIT = "0.5.1";
    
    private AWSCredentials awsCredentials = null;
    private String invokingApplicationDescription = null;
    private boolean isHttpsOnly = true;
    private int internalErrorRetryMax = 5;
    
    /**
     * The approximate difference in the current time between your computer and
     * Amazon's S3 server, measured in milliseconds.
     * 
     * This value is 0 by default. Use the {@link #currentTimeWithOffset()} to 
     * obtain the current time with this offset factor included, and the 
     * {@link #adjustTime()} method to calculate an offset value for your
     * computer based on a response from an AWS server.
     */
    protected long timeOffset = 0;
        
    /**
     * Construct an <code>S3Service</code> identified by the given AWS Principal.
     * 
     * @param awsCredentials
     * the S3 user credentials to use when communicating with S3, may be null in which case the
     * communication is done as an anonymous user.
     * @param invokingApplicationDescription
     * a short description of the application using the service, suitable for inclusion in a
     * user agent string for REST/HTTP requests. Ideally this would include the application's
     * version number, for example: <code>Cockpit/0.5.1</code> or <code>My App Name/1.0</code> 
     * @throws S3ServiceException
     */
    protected S3Service(AWSCredentials awsCredentials, String invokingApplicationDescription) throws S3ServiceException {
        this.awsCredentials = awsCredentials;
        this.invokingApplicationDescription = invokingApplicationDescription;
        isHttpsOnly = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getBoolProperty("s3service.https-only", true);        
        internalErrorRetryMax = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getIntProperty("s3service.internal-error-retry-max", 5);        
        
        // Configure the InetAddress DNS caching times to work well with S3. The cached DNS will
        // timeout after 5 minutes, while failed DNS lookups will be retried after 1 second.
        System.setProperty("networkaddress.cache.ttl", "300");
        System.setProperty("networkaddress.cache.negative.ttl", "1");
    }

    /**
     * Construct an <code>S3Service</code> identified by the given AWS Principal.
     * 
     * @param awsCredentials
     * the S3 user credentials to use when communicating with S3, may be null in which case the
     * communication is done as an anonymous user.
     * @throws S3ServiceException
     */
    protected S3Service(AWSCredentials awsCredentials) throws S3ServiceException {
        this(awsCredentials, null);
    }
        
    /**
     * @return 
     * true if this service has <code>AWSCredentials</code> identifying an S3 user, false
     * if the service is acting as an anonymous user.
     */
    public boolean isAuthenticatedConnection() {
        return awsCredentials != null;
    }
    
    /**
     * Whether to use secure HTTPS or insecure HTTP for communicating with S3, as set by the
     * JetS3t property: s3service.https-only 
     * 
     * @return
     * true if this service should use only secure HTTPS communication channels to S3. 
     * If false, the non-secure HTTP protocol will be used.   
     */
    public boolean isHttpsOnly() {
        return isHttpsOnly;
    }
    
    /**
     * The maximum number of times to retry when S3 Internal Error (500) errors are encountered,   
     * as set by the JetS3t property: s3service.internal-error-retry-max 
     */
    public int getInternalErrorRetryMax() {
        return internalErrorRetryMax;
    }
    
    /**
     * Returns true if the given bucket name can be used as a component of a valid
     * DNS name. If so, the bucket can be accessed using requests with the bucket name
     * as part of an S3 sub-domain. If not, the old-style bucket reference URLs must be 
     * used, in which case the bucket name must be the first component of the resource 
     * path.
     */
    public static boolean isBucketNameValidDNSName(String bucketName) {
        if (bucketName == null || bucketName.length() > 63 || bucketName.length() < 3) {
            return false;
        }
        
        // Only lower-case letters, numbers, '.' or '-' characters allowed
        if (!Pattern.matches("^[a-z0-9][a-z0-9.-]+$", bucketName)) {
            return false;
        }

        // Cannot be an IP address (must contain at least one lower-case letter)
        if (!Pattern.matches(".*[a-z].*", bucketName)) {
            return false;
        }
        
        // Components of name between '.' characters cannot start or end with '-', 
        // and cannot be empty
        String[] fragments = bucketName.split("\\.");
        for (int i = 0; i < fragments.length; i++) {
            if (Pattern.matches("^-.*", fragments[i])
                || Pattern.matches(".*-$", fragments[i])
                || Pattern.matches("^$", fragments[i])) 
            {
                return false;
            }
        }
        
        return true;
    }
    
    public static String generateS3HostnameForBucket(String bucketName) {
        if (isBucketNameValidDNSName(bucketName)) {
            return bucketName + "." + Constants.S3_HOSTNAME;
        } else {
            return Constants.S3_HOSTNAME;
        }        
    }
    
    /**
     * Sleeps for a period of time based on the number of S3 Internal Server errors a request has
     * encountered, provided the number of errors does not exceed the value set with the
     * property <code>s3service.internal-error-retry-max</code>. If the maximum error count is
     * exceeded, this method will throw an S3ServiceException.
     *  
     * The millisecond delay grows rapidly according to the formula 
     * <code>50 * (<i>internalErrorCount</i> ^ 2)</code>.
     * 
     * <table>
     * <tr><th>Error count</th><th>Delay in milliseconds</th></tr>
     * <tr><td>1</td><td>50</td></tr>
     * <tr><td>2</td><td>200</td></tr>
     * <tr><td>3</td><td>450</td></tr>
     * <tr><td>4</td><td>800</td></tr>
     * <tr><td>5</td><td>1250</td></tr>
     * </table>
     * 
     * @param internalErrorCount
     * the number of S3 Internal Server errors encountered by a request.
     * 
     * @throws S3ServiceException
     * thrown if the number of internal errors exceeds the value of internalErrorCount.
     * @throws InterruptedException
     * thrown if the thread sleep is interrupted.
     */
    protected void sleepOnInternalError(int internalErrorCount) 
        throws S3ServiceException, InterruptedException 
    {
        if (internalErrorCount <= internalErrorRetryMax) {
            long delayMs = 50L * (int) Math.pow(internalErrorCount, 2); 
            log.warn("Encountered " + internalErrorCount 
                + " S3 Internal Server error(s), will retry in " + delayMs + "ms");
            Thread.sleep(delayMs);
        } else {
            throw new S3ServiceException("Encountered too many S3 Internal Server errors (" 
                + internalErrorCount + "), aborting request.");
        }        
    }

    /**
     * @return the AWS Credentials identifying the S3 user, may be null if the service is acting
     * anonymously.
     */
    public AWSCredentials getAWSCredentials() {
        return awsCredentials;
    }
    
    /**
     * @return a description of the application using this service, suitable for inclusion in the
     * user agent string of REST/HTTP requests. 
     */
    public String getInvokingApplicationDescription() {
        return invokingApplicationDescription;
    }

    /**
     * Generates a signed URL string that will grant access to an S3 resource (bucket or object)
     * to whoever uses the URL up until the time specified.
     * 
     * @param method
     * the HTTP method to sign, such as GET or PUT (note that S3 does not support POST requests).
     * @param bucketName
     * the name of the bucket to include in the URL, must be a valid bucket name.
     * @param objectKey
     * the name of the object to include in the URL, if null only the bucket name is used.
     * @param specialParamName
     * the name of a request parameter to add to the URL generated by this method. 'Special'
     * parameters may include parameters that specify the kind of S3 resource that the URL
     * will refer to, such as 'acl', 'torrent', 'logging' or 'location'.
     * @param headersMap
     * headers to add to the signed URL, may be null. 
     * Headers that <b>must</b> match between the signed URL and the actual request include:
     * content-md5, content-type, and any header starting with 'x-amz-'.
     * @param awsCredentials
     * the credentials of someone with sufficient privileges to grant access to the bucket/object 
     * @param secondsSinceEpoch
     * the time after which URL's signature will no longer be valid. This time cannot be null.
     *  <b>Note:</b> This time is specified in seconds since the epoch, not milliseconds. 
     * @param isVirtualHost
     * if this parameter is true, the bucket name is treated as a virtual host name. To use
     * this option, the bucket name must be a valid DNS name that is an alias to an S3 bucket.
     * 
     * @return
     * a URL signed in such a way as to grant access to an S3 resource to whoever uses it.
     * 
     * @throws S3ServiceException
     */
    public static String createSignedUrl(String method, String bucketName, String objectKey, 
        String specialParamName, Map headersMap, AWSCredentials awsCredentials, 
        long secondsSinceEpoch, boolean isVirtualHost) 
        throws S3ServiceException
    {
        String uriPath = "";
        
        String hostname = (isVirtualHost? bucketName : generateS3HostnameForBucket(bucketName));
        
        // If we are using an alternative hostname, include the hostname/bucketname in the resource path.
        String virtualBucketPath = "";
        if (!Constants.S3_HOSTNAME.equals(hostname)) {
            int subdomainOffset = hostname.indexOf(".s3.amazonaws.com");
            if (subdomainOffset > 0) {
                // Hostname represents an S3 sub-domain, so the bucket's name is the CNAME portion
                virtualBucketPath = hostname.substring(0, subdomainOffset) + "/";                    
            } else {
                // Hostname represents a virtual host, so the bucket's name is identical to hostname
                virtualBucketPath = hostname + "/";
            }
            uriPath = (objectKey != null ? RestUtils.encodeUrlPath(objectKey, "/") : "");
        } else {
            uriPath = bucketName + (objectKey != null ? "/" + RestUtils.encodeUrlPath(objectKey, "/") : "");
        }
        
        if (specialParamName != null) {
            uriPath += "?" + specialParamName + "&";
        } else {
            uriPath += "?";
        }
        uriPath += "AWSAccessKeyId=" + awsCredentials.getAccessKey();
        uriPath += "&Expires=" + secondsSinceEpoch;

        String canonicalString = RestUtils.makeCanonicalString(method, "/" + virtualBucketPath + uriPath,
            RestUtils.renameMetadataKeys(headersMap), String.valueOf(secondsSinceEpoch));
        log.debug("Signing canonical string:\n" + canonicalString);

        String signedCanonical = ServiceUtils.signWithHmacSha1(awsCredentials.getSecretKey(),
            canonicalString);
        String encodedCanonical = RestUtils.encodeUrlString(signedCanonical);
        uriPath += "&Signature=" + encodedCanonical;

        // Append URL prefix (protocol and host end point) to signed string
        boolean isHttpsOnly = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getBoolProperty("s3service.https-only", true);

        if (isHttpsOnly) {
            return "https://" + hostname + "/" + uriPath;
        } else {            
            return "http://" + hostname + "/" + uriPath;
        }
    }
    
    /**
     * Generates a signed URL string that will grant access to an S3 resource (bucket or object)
     * to whoever uses the URL up until the time specified.
     * 
     * @param method
     * the HTTP method to sign, such as GET or PUT (note that S3 does not support POST requests).
     * @param bucketName
     * the name of the bucket to include in the URL, must be a valid bucket name.
     * @param objectKey
     * the name of the object to include in the URL, if null only the bucket name is used.
     * @param specialParamName
     * the name of a request parameter to add to the URL generated by this method. 'Special'
     * parameters may include parameters that specify the kind of S3 resource that the URL
     * will refer to, such as 'acl', 'torrent', 'logging' or 'location'.
     * @param headersMap
     * headers to add to the signed URL, may be null. 
     * Headers that <b>must</b> match between the signed URL and the actual request include:
     * content-md5, content-type, and any header starting with 'x-amz-'.
     * @param awsCredentials
     * the credentials of someone with sufficient privileges to grant access to the bucket/object 
     * @param secondsSinceEpoch
     * the time after which URL's signature will no longer be valid. This time cannot be null.
     *  <b>Note:</b> This time is specified in seconds since the epoch, not milliseconds. 
     * 
     * @return
     * a URL signed in such a way as to grant access to an S3 resource to whoever uses it.
     * 
     * @throws S3ServiceException
     */
    public static String createSignedUrl(String method, String bucketName, String objectKey, 
        String specialParamName, Map headersMap, AWSCredentials awsCredentials, long secondsSinceEpoch) 
        throws S3ServiceException
    {
        return createSignedUrl(method, bucketName, objectKey, specialParamName, headersMap, 
            awsCredentials, secondsSinceEpoch, false);
    }

                
    /**
     * Generates a signed GET URL.
     * 
     * @param bucketName
     * the name of the bucket to include in the URL, must be a valid bucket name.
     * @param objectKey
     * the name of the object to include in the URL, if null only the bucket name is used.
     * @param awsCredentials
     * the credentials of someone with sufficient privileges to grant access to the bucket/object 
     * @param expiryTime
     * the time after which URL's signature will no longer be valid. This time cannot be null.
     * @param isVirtualHost
     * if this parameter is true, the bucket name is treated as a virtual host name. To use
     * this option, the bucket name must be a valid DNS name that is an alias to an S3 bucket.
     * 
     * @return
     * a URL signed in such a way as to grant GET access to an S3 resource to whoever uses it.
     * @throws S3ServiceException
     */
    public static String createSignedGetUrl(String bucketName, String objectKey,
        AWSCredentials awsCredentials, Date expiryTime, boolean isVirtualHost) 
        throws S3ServiceException
    {
        long secondsSinceEpoch = expiryTime.getTime() / 1000;
        return createSignedUrl("GET", bucketName, objectKey, null, null, 
            awsCredentials, secondsSinceEpoch, isVirtualHost);
    }

    
    /**
     * Generates a signed GET URL.
     * 
     * @param bucketName
     * the name of the bucket to include in the URL, must be a valid bucket name.
     * @param objectKey
     * the name of the object to include in the URL, if null only the bucket name is used.
     * @param awsCredentials
     * the credentials of someone with sufficient privileges to grant access to the bucket/object 
     * @param expiryTime
     * the time after which URL's signature will no longer be valid. This time cannot be null.
     * 
     * @return
     * a URL signed in such a way as to grant GET access to an S3 resource to whoever uses it.
     * @throws S3ServiceException
     */
    public static String createSignedGetUrl(String bucketName, String objectKey,
        AWSCredentials awsCredentials, Date expiryTime) 
        throws S3ServiceException
    {
        return createSignedGetUrl(bucketName, objectKey, awsCredentials, expiryTime, false);
    }

    
    /**
     * Generates a signed PUT URL.
     * 
     * @param bucketName
     * the name of the bucket to include in the URL, must be a valid bucket name.
     * @param objectKey
     * the name of the object to include in the URL, if null only the bucket name is used.
     * @param headersMap
     * headers to add to the signed URL, may be null. 
     * Headers that <b>must</b> match between the signed URL and the actual request include:
     * content-md5, content-type, and any header starting with 'x-amz-'.
     * @param awsCredentials
     * the credentials of someone with sufficient privileges to grant access to the bucket/object 
     * @param expiryTime
     * the time after which URL's signature will no longer be valid. This time cannot be null.
     * @param isVirtualHost
     * if this parameter is true, the bucket name is treated as a virtual host name. To use
     * this option, the bucket name must be a valid DNS name that is an alias to an S3 bucket. 
     * 
     * @return
     * a URL signed in such a way as to allow anyone to PUT an object into S3.
     * @throws S3ServiceException
     */
    public static String createSignedPutUrl(String bucketName, String objectKey, 
        Map headersMap, AWSCredentials awsCredentials, Date expiryTime, boolean isVirtualHost) 
        throws S3ServiceException
    {
        long secondsSinceEpoch = expiryTime.getTime() / 1000;
        return createSignedUrl("PUT", bucketName, objectKey, null, headersMap, 
            awsCredentials, secondsSinceEpoch, isVirtualHost);
    }
    
    
    /**
     * Generates a signed PUT URL.
     * 
     * @param bucketName
     * the name of the bucket to include in the URL, must be a valid bucket name.
     * @param objectKey
     * the name of the object to include in the URL, if null only the bucket name is used.
     * @param headersMap
     * headers to add to the signed URL, may be null. 
     * Headers that <b>must</b> match between the signed URL and the actual request include:
     * content-md5, content-type, and any header starting with 'x-amz-'.
     * @param awsCredentials
     * the credentials of someone with sufficient privileges to grant access to the bucket/object 
     * @param expiryTime
     * the time after which URL's signature will no longer be valid. This time cannot be null.
     * 
     * @return
     * a URL signed in such a way as to allow anyone to PUT an object into S3.
     * @throws S3ServiceException
     */
    public static String createSignedPutUrl(String bucketName, String objectKey, 
        Map headersMap, AWSCredentials awsCredentials, Date expiryTime) 
        throws S3ServiceException
    {
        return createSignedPutUrl(bucketName, objectKey, headersMap, awsCredentials, expiryTime, false);        
    }
       
    
    /**
     * Generates a signed DELETE URL.
     * 
     * @param bucketName
     * the name of the bucket to include in the URL, must be a valid bucket name.
     * @param objectKey
     * the name of the object to include in the URL, if null only the bucket name is used.
     * @param awsCredentials
     * the credentials of someone with sufficient privileges to grant access to the bucket/object 
     * @param expiryTime
     * the time after which URL's signature will no longer be valid. This time cannot be null.
     * @param isVirtualHost
     * if this parameter is true, the bucket name is treated as a virtual host name. To use
     * this option, the bucket name must be a valid DNS name that is an alias to an S3 bucket.
     *  
     * @return
     * a URL signed in such a way as to allow anyone do DELETE an object in S3.
     * @throws S3ServiceException
     */
    public static String createSignedDeleteUrl(String bucketName, String objectKey, 
        AWSCredentials awsCredentials, Date expiryTime, boolean isVirtualHost) 
        throws S3ServiceException
    {
        long secondsSinceEpoch = expiryTime.getTime() / 1000;
        return createSignedUrl("DELETE", bucketName, objectKey, null, null, 
            awsCredentials, secondsSinceEpoch, isVirtualHost);
    }

    
    /**
     * Generates a signed DELETE URL.
     * 
     * @param bucketName
     * the name of the bucket to include in the URL, must be a valid bucket name.
     * @param objectKey
     * the name of the object to include in the URL, if null only the bucket name is used.
     * @param awsCredentials
     * the credentials of someone with sufficient privileges to grant access to the bucket/object 
     * @param expiryTime
     * the time after which URL's signature will no longer be valid. This time cannot be null.
     *  
     * @return
     * a URL signed in such a way as to allow anyone do DELETE an object in S3.
     * @throws S3ServiceException
     */
    public static String createSignedDeleteUrl(String bucketName, String objectKey, 
        AWSCredentials awsCredentials, Date expiryTime) 
        throws S3ServiceException
    {
        return createSignedDeleteUrl(bucketName, objectKey, awsCredentials, expiryTime, false);
    }
    
    
    /**
     * Generates a signed HEAD URL.
     * 
     * @param bucketName
     * the name of the bucket to include in the URL, must be a valid bucket name.
     * @param objectKey
     * the name of the object to include in the URL, if null only the bucket name is used.
     * @param awsCredentials
     * the credentials of someone with sufficient privileges to grant access to the bucket/object 
     * @param expiryTime
     * the time after which URL's signature will no longer be valid. This time cannot be null.
     * @param isVirtualHost
     * if this parameter is true, the bucket name is treated as a virtual host name. To use
     * this option, the bucket name must be a valid DNS name that is an alias to an S3 bucket.
     * 
     * @return
     * a URL signed in such a way as to grant HEAD access to an S3 resource to whoever uses it.
     * @throws S3ServiceException
     */
    public static String createSignedHeadUrl(String bucketName, String objectKey, 
        AWSCredentials awsCredentials, Date expiryTime, boolean isVirtualHost) 
        throws S3ServiceException
    {
        long secondsSinceEpoch = expiryTime.getTime() / 1000;
        return createSignedUrl("HEAD", bucketName, objectKey, null, null, 
            awsCredentials, secondsSinceEpoch, isVirtualHost);
    }

    
    /**
     * Generates a signed HEAD URL.
     * 
     * @param bucketName
     * the name of the bucket to include in the URL, must be a valid bucket name.
     * @param objectKey
     * the name of the object to include in the URL, if null only the bucket name is used.
     * @param awsCredentials
     * the credentials of someone with sufficient privileges to grant access to the bucket/object 
     * @param expiryTime
     * the time after which URL's signature will no longer be valid. This time cannot be null.
     * 
     * @return
     * a URL signed in such a way as to grant HEAD access to an S3 resource to whoever uses it.
     * @throws S3ServiceException
     */
    public static String createSignedHeadUrl(String bucketName, String objectKey, 
        AWSCredentials awsCredentials, Date expiryTime) 
        throws S3ServiceException
    {
        return createSignedHeadUrl(bucketName, objectKey, awsCredentials, expiryTime, false);
    }
    
    
    /**
     * Generates a URL string that will return a Torrent file for an object in S3, 
     * which file can be downloaded and run in a BitTorrent client.  
     * 
     * @param bucketName
     * the name of the bucket containing the object.
     * @param objectKey
     * the name of the object. 
     * @return
     * a URL to a Torrent file representing the object.
     * @throws S3ServiceException
     */
    public static String createTorrentUrl(String bucketName, String objectKey) {
        return "http://" + generateS3HostnameForBucket(bucketName) + "/" +
            (isBucketNameValidDNSName(bucketName) ? "" : bucketName + "/") + objectKey + "?torrent"; 
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

    /**
     * Throws an exception if an object's key name is null or empty.
     * @param key
     * An object's key name.
     * @param action
     * the action being attempted which this assertion is applied, for debugging purposes.
     * @throws S3ServiceException
     */
    protected void assertValidObject(String key, String action) throws S3ServiceException {
        if (key == null || key.length() == 0) {
            throw new S3ServiceException("The action " + action
                + " cannot be performed with an invalid object key name: " + key);
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
     * Creates a bucket in a specific location, after first checking to ensure the bucket doesn't 
     * already exist (using {@link #isBucketAccessible(String)}). 
     * <p>
     * This method cannot be performed by anonymous services.
     * 
     * @param bucketName
     * the name of the bucket to create, if it does not already exist.
     * @return
     * the created bucket object. <b>Note:</b> the object returned has minimal information about
     * the bucket that was created, including only the bucket's name.
     * @throws S3ServiceException
     */
    public S3Bucket createBucket(String bucketName, String location) throws S3ServiceException {
        assertAuthenticatedConnection("createBucket");

        if (isBucketAccessible(bucketName)) {
            log.debug("Bucket with name '" + bucketName + "' already exists, it will not be created");
            return new S3Bucket(bucketName);
        } 
        
        S3Bucket bucket = new S3Bucket(bucketName, location);
        return createBucket(bucket);
    }

    /**
     * Creates a bucket, after first checking to ensure the bucket doesn't already exist (using
     * {@link #isBucketAccessible(String)}). The bucket is created in the default location as
     * specified in the properties setting <tt>s3service.default-bucket-location</tt>.
     * <p>
     * This method cannot be performed by anonymous services.
     * 
     * @param bucketName
     * the name of the bucket to create, if it does not already exist.
     * @return
     * the created bucket object. <b>Note:</b> the object returned has minimal information about
     * the bucket that was created, including only the bucket's name.
     * @throws S3ServiceException
     */
    public S3Bucket createBucket(String bucketName) throws S3ServiceException {
        String defaultBucketLocation = Jets3tProperties.getInstance(
            Constants.JETS3T_PROPERTIES_FILENAME).getStringProperty(
                "s3service.default-bucket-location", S3Bucket.LOCATION_US);
        return createBucket(bucketName, defaultBucketLocation);        
    }
    
    /**
     * Returns an object representing the details and data of an item in S3, without applying any
     * preconditions.
     * <p>
     * This method can be performed by anonymous services.
     * <p>
     * <b>Important:</b> It is the caller's responsibility to close the object's data input stream.
     * The data stream should be consumed and closed as soon as is practical as network connections 
     * may be held open until the streams are closed. Excessive unclosed streams can lead to 
     * connection starvation.
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


    /**
     * Lists the buckets belonging to the service user. 
     * <p>
     * This method cannot be performed by anonymous services, and will fail with an exception
     * if the service is not authenticated.
     * 
     * @return
     * the list of buckets owned by the service user.
     * @throws S3ServiceException
     */
    public S3Bucket[] listAllBuckets() throws S3ServiceException {
        assertAuthenticatedConnection("List all buckets");
        return listAllBucketsImpl();
    }

    /**
     * Lists the objects in a bucket matching a prefix, chunking the results into batches of
     * a given size. 
     * <p>
     * This method can be performed by anonymous services.
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
    public S3Object[] listObjects(S3Bucket bucket, String prefix, String delimiter, 
        long maxListingLength) throws S3ServiceException
    {
        assertValidBucket(bucket, "List objects in bucket");
        return listObjects(bucket.getName(), prefix, delimiter, maxListingLength);
    }

    /**
     * Lists the objects in a bucket matching a prefix, chunking the results into batches of
     * a given size. 
     * <p>
     * This method can be performed by anonymous services.
     * 
     * @param bucketName
     * the name of the the bucket whose contents will be listed. 
     * @param prefix
     * only objects with a key that starts with this prefix will be listed
     * @param maxListingLength
     * the maximum number of objects to include in each result chunk
     * @return
     * the set of objects contained in a bucket whose keys start with the given prefix.
     * @throws S3ServiceException
     */
    public S3Object[] listObjects(String bucketName, String prefix, String delimiter, 
        long maxListingLength) throws S3ServiceException
    {
        return listObjectsImpl(bucketName, prefix, delimiter, maxListingLength);
    }

    /**
     * Lists the objects in a bucket matching a prefix, chunking the results into batches of
     * a given size, and returning each chunk separately. It is the responsility of the caller 
     * to building a complete bucket object listing by performing follow-up requests if necessary.
     * <p>
     * This method can be performed by anonymous services.
     * 
     * @param bucketName
     * the name of the the bucket whose contents will be listed. 
     * @param prefix
     * only objects with a key that starts with this prefix will be listed
     * @param maxListingLength
     * the maximum number of objects to include in each result chunk
     * @param priorLastKey
     * the last object key received in a prior call to this method. The next chunk of objects
     * listed will start with the next object in the bucket <b>after</b> this key name.
     * This paramater may be null, in which case the listing will start at the beginning of the
     * bucket's object contents.
     * @return
     * the set of objects contained in a bucket whose keys start with the given prefix.
     * @throws S3ServiceException
     */
    public S3ObjectsChunk listObjectsChunked(String bucketName, String prefix, String delimiter, 
        long maxListingLength, String priorLastKey) throws S3ServiceException
    {
        return listObjectsChunkedImpl(bucketName, prefix, delimiter, maxListingLength, priorLastKey);
    }

    /**
     * Creates a bucket in S3 based on the provided bucket object.
     * <p>
     * This method cannot be performed by anonymous services.
     * 
     * @param bucket
     * an object representing the bucket to create which must be valid, and may contain ACL settings.
     * @return
     * the created bucket object, populated with all metadata made available by the creation operation. 
     * @throws S3ServiceException
     */
    public S3Bucket createBucket(S3Bucket bucket) throws S3ServiceException {
        assertAuthenticatedConnection("Create Bucket");
        assertValidBucket(bucket, "Create Bucket");
        return createBucketImpl(bucket.getName(), bucket.getLocation(), bucket.getAcl());
    }

    /**
     * Deletes an S3 bucket.
     * <p>
     * This method can be performed by anonymous services.
     * 
     * @param bucket
     * the bucket to delete.
     * @throws S3ServiceException
     */
    public void deleteBucket(S3Bucket bucket) throws S3ServiceException {
        assertValidBucket(bucket, "Delete bucket");
        deleteBucketImpl(bucket.getName());
    }

    /**
     * Deletes an S3 bucket.
     * <p>
     * This method can be performed by anonymous services.
     * 
     * @param bucketName
     * the name of the bucket to delete.
     * @throws S3ServiceException
     */
    public void deleteBucket(String bucketName) throws S3ServiceException {
        deleteBucketImpl(bucketName);
    }

    /**
     * Puts an object inside an existing bucket in S3, creating a new object or overwriting
     * an existing one with the same key.
     * <p>
     * This method can be performed by anonymous services.
     * 
     * @param bucketName
     * the name of the bucket inside which the object will be put.
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
    public S3Object putObject(String bucketName, S3Object object) throws S3ServiceException {
        assertValidObject(object, "Create Object in bucket " + bucketName);        
        return putObjectImpl(bucketName, object);
    }

    /**
     * Puts an object inside an existing bucket in S3, creating a new object or overwriting
     * an existing one with the same key.
     * <p>
     * This method can be performed by anonymous services.
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
    public S3Object putObject(S3Bucket bucket, S3Object object) throws S3ServiceException {
        assertValidBucket(bucket, "Create Object in bucket");
        return putObject(bucket.getName(), object);
    }
    
    /**
     * Deletes an object from a bucket in S3.
     * <p>
     * This method can be performed by anonymous services.
     * 
     * @param bucket
     * the bucket containing the object to be deleted.
     * @param objectKey
     * the key representing the object in S3.
     * @throws S3ServiceException
     */
    public void deleteObject(S3Bucket bucket, String objectKey) throws S3ServiceException {
        assertValidBucket(bucket, "deleteObject");
        assertValidObject(objectKey, "deleteObject");
        deleteObject(bucket.getName(), objectKey);
    }

    /**
     * Deletes an object from a bucket in S3.
     * <p>
     * This method can be performed by anonymous services.
     * 
     * @param bucketName
     * the name of the bucket containing the object to be deleted.
     * @param objectKey
     * the key representing the object in S3.
     * @throws S3ServiceException
     */
    public void deleteObject(String bucketName, String objectKey) throws S3ServiceException {
        assertValidObject(objectKey, "deleteObject");
        deleteObjectImpl(bucketName, objectKey);
    }

    /**
     * Returns an object representing the details of an item in S3 that meets any given preconditions.
     * The object is returned without the object's data.
     * <p>
     * An exception is thrown if any of the preconditions fail. 
     * Preconditions are only applied if they are non-null.
     * <p>
     * This method can be performed by anonymous services.
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
    public S3Object getObjectDetails(S3Bucket bucket, String objectKey,
        Calendar ifModifiedSince, Calendar ifUnmodifiedSince, String[] ifMatchTags,
        String[] ifNoneMatchTags) throws S3ServiceException
    {
        assertValidBucket(bucket, "Get Object Details");
        return getObjectDetailsImpl(bucket.getName(), objectKey, ifModifiedSince, ifUnmodifiedSince, 
            ifMatchTags, ifNoneMatchTags);
    }

    public S3Object getObjectDetails(String bucketName, String objectKey,
        Calendar ifModifiedSince, Calendar ifUnmodifiedSince, String[] ifMatchTags,
        String[] ifNoneMatchTags) throws S3ServiceException
    {
        return getObjectDetailsImpl(bucketName, objectKey, ifModifiedSince, ifUnmodifiedSince, 
            ifMatchTags, ifNoneMatchTags);
    }

    /**
     * Returns an object representing the details of an item in S3 that meets any given preconditions.
     * The object is returned with the object's data.
     * <p>
     * <b>Important:</b> It is the caller's responsibility to close the object's data input stream.
     * The data stream should be consumed and closed as soon as is practical as network connections 
     * may be held open until the streams are closed. Excessive unclosed streams can lead to 
     * connection starvation.
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
    public S3Object getObject(S3Bucket bucket, String objectKey, Calendar ifModifiedSince,
        Calendar ifUnmodifiedSince, String[] ifMatchTags, String[] ifNoneMatchTags,
        Long byteRangeStart, Long byteRangeEnd) throws S3ServiceException 
    {
        assertValidBucket(bucket, "Get Object");
        return getObjectImpl(bucket.getName(), objectKey, ifModifiedSince, ifUnmodifiedSince, 
            ifMatchTags, ifNoneMatchTags, byteRangeStart, byteRangeEnd);
    }

    /**
     * Returns an object representing the details of an item in S3 that meets any given preconditions.
     * The object is returned with the object's data.
     * <p>
     * <b>Important:</b> It is the caller's responsibility to close the object's data input stream.
     * The data stream should be consumed and closed as soon as is practical as network connections 
     * may be held open until the streams are closed. Excessive unclosed streams can lead to 
     * connection starvation.
     * <p>
     * An exception is thrown if any of the preconditions fail. 
     * Preconditions are only applied if they are non-null.
     * <p>
     * This method can be performed by anonymous services.
     * <p>
     * <b>Implementation notes</b><p>
     * Implementations should use {@link #assertValidBucket} assertion.
     * 
     * @param bucketName
     * the name of the bucket containing the object.
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
    public S3Object getObject(String bucketName, String objectKey, Calendar ifModifiedSince,
        Calendar ifUnmodifiedSince, String[] ifMatchTags, String[] ifNoneMatchTags,
        Long byteRangeStart, Long byteRangeEnd) throws S3ServiceException 
    {
        return getObjectImpl(bucketName, objectKey, ifModifiedSince, ifUnmodifiedSince, 
            ifMatchTags, ifNoneMatchTags, byteRangeStart, byteRangeEnd);
    }

    /**
     * Applies access control settings to an object. The ACL settings must be included
     * with the object.
     * 
     * @param bucket
     * the bucket containing the object to modify.
     * @param object
     * the object with ACL settings that will be applied.
     * @throws S3ServiceException
     */
    public void putObjectAcl(S3Bucket bucket, S3Object object) throws S3ServiceException {
        assertValidBucket(bucket, "Put Object Access Control List");
        assertValidObject(object, "Put Object Access Control List");
        putObjectAcl(bucket.getName(), object.getKey(), object.getAcl());
    }

    /**
     * Applies access control settings to an object. The ACL settings must be included
     * with the object.
     * 
     * @param bucketName
     * the name of the bucket containing the object to modify.
     * @param objectKey
     * the key name of the object with ACL settings that will be applied.
     * @throws S3ServiceException
     */
    public void putObjectAcl(String bucketName, String objectKey, AccessControlList acl) 
        throws S3ServiceException 
    {
        if (acl == null) {
            throw new S3ServiceException("The object '" + objectKey +
                "' does not include ACL information");
        }
        putObjectAclImpl(bucketName, objectKey, acl);
    }

    /**
     * Applies access control settings to a bucket. The ACL settings must be included
     * inside the bucket.
     * 
     * @param bucket
     * a bucket with ACL settings to apply.
     * @throws S3ServiceException
     */
    public void putBucketAcl(S3Bucket bucket) throws S3ServiceException {
        assertValidBucket(bucket, "Put Bucket Access Control List");
        putBucketAcl(bucket.getName(), bucket.getAcl());
    }

    /**
     * Applies access control settings to a bucket. The ACL settings must be included
     * inside the bucket.
     * 
     * @param bucketName
     * a name of the bucket with ACL settings to apply.
     * @throws S3ServiceException
     */
    public void putBucketAcl(String bucketName, AccessControlList acl) throws S3ServiceException {
        if (acl == null) {
            throw new S3ServiceException("The bucket '" + bucketName +
                "' does not include ACL information");
        }
        putBucketAclImpl(bucketName, acl);
    }

    /**
     * Retrieves the access control settings of an object.
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
    public AccessControlList getObjectAcl(S3Bucket bucket, String objectKey) throws S3ServiceException {
        assertValidBucket(bucket, "Get Object Access Control List");
        return getObjectAclImpl(bucket.getName(), objectKey);
    }

    /**
     * Retrieves the access control settings of an object.
     * 
     * @param bucketName
     * the name of the bucket whose ACL settings will be retrieved (if objectKey is null) or the 
     * name of the bucket containing the object whose ACL settings will be retrieved (if objectKey is non-null).
     * @param objectKey
     * if non-null, the key of the object whose ACL settings will be retrieved. Ignored if null.
     * @return
     * the ACL settings of the bucket or object.
     * @throws S3ServiceException
     */
    public AccessControlList getObjectAcl(String bucketName, String objectKey) throws S3ServiceException {
        return getObjectAclImpl(bucketName, objectKey);
    }

    /**
     * Retrieves the access control settings of a bucket.
     * 
     * @param bucket
     * the bucket whose access control settings will be returned.
     * This must be a valid S3Bucket object that is non-null and contains a name.
     * @return
     * the ACL settings of the bucket.
     * @throws S3ServiceException
     */
    public AccessControlList getBucketAcl(S3Bucket bucket) throws S3ServiceException {
        assertValidBucket(bucket, "Get Bucket Access Control List");
        return getBucketAclImpl(bucket.getName());
    }

    /**
     * Retrieves the access control settings of a bucket.
     * 
     * @param bucketName
     * the name of the bucket whose access control settings will be returned.
     * @return
     * the ACL settings of the bucket.
     * @throws S3ServiceException
     */
    public AccessControlList getBucketAcl(String bucketName) throws S3ServiceException {
        return getBucketAclImpl(bucketName);
    }
    
    /**
     * Retrieves the location of a bucket.
     * 
     * @param bucketName
     * the name of the bucket whose location will be returned.
     * @return
     * a string representing the location of the bucket, such as "EU" for a bucket
     * located in Europe or null for a bucket in the default US location.
     * @throws S3ServiceException
     */
    public String getBucketLocation(String bucketName) throws S3ServiceException {
        return getBucketLocationImpl(bucketName);
    }

    /**
     * Retrieves the logging status settings of a bucket.
     * 
     * @param bucketName
     * the name of the bucket whose logging status settings will be returned.
     * @return
     * the Logging Status settings of the bucket.
     * @throws S3ServiceException
     */
    public S3BucketLoggingStatus getBucketLoggingStatus(String bucketName) throws S3ServiceException {
        return getBucketLoggingStatusImpl(bucketName);
    }
    
    /**
     * Applies logging settings to a bucket, optionally modifying the ACL permissions for the 
     * logging target bucket to ensure log files can be written to it.
     * 
     * @param bucketName
     * the name of the bucket the logging settings will apply to.
     * @param status
     * the logging status settings to apply to the bucket.
     * @param updateTargetACLifRequired
     * if true, when logging is enabled the method will check the target bucket to ensure it has the 
     * necessary ACL permissions set to allow logging (that is, WRITE and READ_ACP for the group
     * <tt>http://acs.amazonaws.com/groups/s3/LogDelivery</tt>). If the target bucket does not
     * have the correct permissions the bucket's ACL will be updated to have the correct 
     * permissions. If this parameter is false, no ACL checks or updates will occur. 
     * 
     * @throws S3ServiceException
     */
    public void setBucketLoggingStatus(String bucketName, S3BucketLoggingStatus status, 
        boolean updateTargetACLifRequired) 
        throws S3ServiceException
    {
        setBucketLoggingStatusImpl(bucketName, status);
        
        if (status.isLoggingEnabled() && updateTargetACLifRequired) {            
            // Check whether the target bucket has the ACL permissions necessary for logging.
            log.debug("Checking whether the target logging bucket '" + 
                status.getTargetBucketName() + "' has the appropriate ACL settings");
            boolean isSetLoggingGroupWrite = false;
            boolean isSetLoggingGroupReadACP = false;
            String groupIdentifier = GroupGrantee.LOG_DELIVERY.getIdentifier();
            
            AccessControlList logBucketACL = getBucketAcl(status.getTargetBucketName());
            
            Iterator grantIter = logBucketACL.getGrants().iterator();
            while (grantIter.hasNext()) {
                GrantAndPermission gap = (GrantAndPermission) grantIter.next();
                
                if (groupIdentifier.equals(gap.getGrantee().getIdentifier())) {
                    // Found a Group Grantee.                    
                    if (gap.getPermission().equals(Permission.PERMISSION_WRITE)) {
                        isSetLoggingGroupWrite = true;
                        log.debug("Target bucket '" + status.getTargetBucketName() + "' has ACL "
                            + "permission " + Permission.PERMISSION_WRITE + " for group " + 
                            groupIdentifier);
                    } else if (gap.getPermission().equals(Permission.PERMISSION_READ_ACP)) {
                        isSetLoggingGroupReadACP = true;
                        log.debug("Target bucket '" + status.getTargetBucketName() + "' has ACL "
                            + "permission " + Permission.PERMISSION_READ_ACP + " for group " + 
                            groupIdentifier);
                    }
                }
            }
            
            // Update target bucket's ACL if necessary.
            if (!isSetLoggingGroupWrite || !isSetLoggingGroupReadACP) {
                log.warn("Target logging bucket '" + status.getTargetBucketName() 
                    + "' does not have the necessary ACL settings, updating ACL now");
                
                logBucketACL.grantPermission(GroupGrantee.LOG_DELIVERY, Permission.PERMISSION_WRITE);
                logBucketACL.grantPermission(GroupGrantee.LOG_DELIVERY, Permission.PERMISSION_READ_ACP);
                putBucketAcl(status.getTargetBucketName(), logBucketACL);
            } else {
                log.debug("Target logging bucket '" + status.getTargetBucketName() 
                    + "' has the necessary ACL settings");                
            }
        }
    }
    

    /**
     * Sets a time offset value to reflect the time difference between your
     * computer's clock and the current time according to an S3 server. This
     * method returns the calculated time difference and also sets the 
     * timeOffset variable in this class.
     * 
     * Ideally you should not rely on this method to overcome clock-related
     * disagreements between your computer and S3. If you computer is set
     * to update its clock periodically and has the correct timezone setting 
     * you should never have to resort to this work-around.
     */
    public long adjustTime() throws Exception {
        // Connect to an AWS server to obtain response headers.
        URL url = new URL("http://s3.amazonaws.com/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();

        // Retrieve the time according to AWS, based on the Date header
        Date s3Time = ServiceUtils.parseRfc822Date(conn.getHeaderField("Date"));

        // Calculate the difference between the current time according to AWS,
        // and the current time according to your computer's clock.
        Date localTime = new Date();
        this.timeOffset = s3Time.getTime() - localTime.getTime();

        log.debug("Calculated time offset value of " + this.timeOffset +
                " milliseconds between the local machine and an S3 server");

        return this.timeOffset;
    }
    
    
    /**
     * Returns the current date and time, adjusted according to the time
     * offset between your computer and an AWS server (as set by the
     * {@link #adjustOffsetTime} method).
     * 
     * @return
     * the current time, or the current time adjusted to match the AWS time 
     * if the {@link #adjustOffsetTime} method has been invoked.
     */
    public Date getCurrentTimeWithOffset() {
        return new Date(System.currentTimeMillis() + timeOffset);
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
    public abstract boolean isBucketAccessible(String bucketName) throws S3ServiceException;
    
    protected abstract String getBucketLocationImpl(String bucketName) 
        throws S3ServiceException;

    protected abstract S3BucketLoggingStatus getBucketLoggingStatusImpl(String bucketName) 
        throws S3ServiceException;
    
    protected abstract void setBucketLoggingStatusImpl(String bucketName, S3BucketLoggingStatus status) 
        throws S3ServiceException;
    
    /**
     * @return
     * the buckets in an S3 account.
     * 
     * @throws S3ServiceException
     */
    protected abstract S3Bucket[] listAllBucketsImpl() throws S3ServiceException;
    
    /**
     * Lists objects in a bucket.
     * 
     * <b>Implementation notes</b><p>
     * The implementation of this method is expected to return <b>all</b> the objects
     * in a bucket, not a subset. This may require repeating the S3 list operation if the
     * first one doesn't include all the available objects, such as when the number of objects
     * is greater than <code>maxListingLength</code>.
     * <p>
     * 
     * @param bucketName
     * @param prefix
     * @param delimiter
     * @param maxListingLength
     * @return
     * the objects in a bucket.
     * 
     * @throws S3ServiceException
     */
    protected abstract S3Object[] listObjectsImpl(String bucketName, String prefix, 
        String delimiter, long maxListingLength) throws S3ServiceException;

    /**
     * Lists objects in a bucket up to the maximum listing length specified.
     *
     * <p>
     * <b>Implementation notes</b>
     * The implementation of this method returns only as many objects as requested in the chunk
     * size. It is the responsibility of the caller to build a complete object listing from 
     * multiple chunks, should this be necessary.
     * </p>
     * 
     * @param bucketName
     * @param prefix
     * @param delimiter
     * @param maxListingLength
     * @param priorLastKey
     * @throws S3ServiceException
     */
    protected abstract S3ObjectsChunk listObjectsChunkedImpl(String bucketName, String prefix, 
        String delimiter, long maxListingLength, String priorLastKey) throws S3ServiceException;

    /**
     * Creates a bucket.
     * 
     * <b>Implementation notes</b><p>
     * The implementing method must populate the bucket object's metadata with the results of the 
     * operation before returning the object. It must also apply any <code>AccessControlList</code> 
     * settings included with the bucket. 
     * 
     * @param bucketName
     * the name of the bucket to create.
     * @param location
     * the geographical location where the bucket will be stored (see {@link S3Bucket#getLocation()}. 
     * A null string value will cause the bucket to be stored in the default S3 location: US. 
     * @param acl
     * an access control object representing the initial acl values for the bucket. 
     * May be null, in which case the default permissions are applied.
     * @return
     * the created bucket object, populated with all metadata made available by the creation operation. 
     * @throws S3ServiceException
     */
    protected abstract S3Bucket createBucketImpl(String bucketName, String location, 
        AccessControlList acl) throws S3ServiceException;

    protected abstract void deleteBucketImpl(String bucketName) throws S3ServiceException;

    protected abstract S3Object putObjectImpl(String bucketName, S3Object object) throws S3ServiceException;

    protected abstract void deleteObjectImpl(String bucketName, String objectKey) throws S3ServiceException;
    
    protected abstract S3Object getObjectDetailsImpl(String bucketName, String objectKey,
        Calendar ifModifiedSince, Calendar ifUnmodifiedSince, String[] ifMatchTags,
        String[] ifNoneMatchTags) throws S3ServiceException;

    protected abstract S3Object getObjectImpl(String bucketName, String objectKey, Calendar ifModifiedSince,
        Calendar ifUnmodifiedSince, String[] ifMatchTags, String[] ifNoneMatchTags,
        Long byteRangeStart, Long byteRangeEnd) throws S3ServiceException;

    protected abstract void putBucketAclImpl(String bucketName, AccessControlList acl) 
        throws S3ServiceException;

    protected abstract void putObjectAclImpl(String bucketName, String objectKey, AccessControlList acl) 
        throws S3ServiceException;

    protected abstract AccessControlList getObjectAclImpl(String bucketName, String objectKey)
        throws S3ServiceException;

    protected abstract AccessControlList getBucketAclImpl(String bucketName) throws S3ServiceException;
    
}
