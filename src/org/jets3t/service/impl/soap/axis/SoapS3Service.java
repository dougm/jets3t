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
package org.jets3t.service.impl.soap.axis;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.stream.StreamSource;

import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.attachments.SourceDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.Constants;
import org.jets3t.service.S3ObjectsChunk;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.CanonicalGrantee;
import org.jets3t.service.acl.EmailAddressGrantee;
import org.jets3t.service.acl.GrantAndPermission;
import org.jets3t.service.acl.GranteeInterface;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlPolicy;
import org.jets3t.service.impl.soap.axis._2006_03_01.AmazonCustomerByEmail;
import org.jets3t.service.impl.soap.axis._2006_03_01.AmazonS3SoapBindingStub;
import org.jets3t.service.impl.soap.axis._2006_03_01.AmazonS3_ServiceLocator;
import org.jets3t.service.impl.soap.axis._2006_03_01.BucketLoggingStatus;
import org.jets3t.service.impl.soap.axis._2006_03_01.CanonicalUser;
import org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectResult;
import org.jets3t.service.impl.soap.axis._2006_03_01.Grant;
import org.jets3t.service.impl.soap.axis._2006_03_01.Grantee;
import org.jets3t.service.impl.soap.axis._2006_03_01.Group;
import org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsEntry;
import org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsResult;
import org.jets3t.service.impl.soap.axis._2006_03_01.ListBucketResult;
import org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry;
import org.jets3t.service.impl.soap.axis._2006_03_01.LoggingSettings;
import org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry;
import org.jets3t.service.impl.soap.axis._2006_03_01.Permission;
import org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry;
import org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectResult;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3BucketLoggingStatus;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.model.S3Owner;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.utils.Mimetypes;
import org.jets3t.service.utils.ServiceUtils;

/**
 * SOAP implementation of an S3Service based on the 
 * <a href="http://ws.apache.org/axis/">Apache Axis 1.4</a> library.  
 * <p>
 * <b>Note</b>: This SOAP implementation does <b>not</b> support IO streaming uploads to S3. Any 
 * documents uploaded by this implementation must fit inside memory allocated to the Java program
 * running this class if OutOfMemory errors are to be avoided. 
 * </p>
 * <p>
 * <b>Note 2</b>: The SOAP implementation does not perform retries when communication with s3 fails.
 * </p> 
 * <p>
 * The preferred S3Service implementation in JetS3t is {@link RestS3Service}. This SOAP 
 * implementation class is provided with JetS3t as a proof-of-concept, showing that alternative
 * service implementations are possible and what a SOAP service might look like. <b>We do not
 * recommend that this service be used to perform any real work.</b>
 * </p>
 * 
 * @author James Murty
 */
public class SoapS3Service extends S3Service {
    private static final long serialVersionUID = 6421138869712673819L;
    
    private final Log log = LogFactory.getLog(SoapS3Service.class);
    private AmazonS3_ServiceLocator locator = null;

    /**
     * Constructs the SOAP service implementation and, based on the value of {@link S3Service#isHttpsOnly}
     * sets the SOAP endpoint to use HTTP or HTTPS protocols.
     * 
     * @param awsCredentials
     * @param invokingApplicationDescription
     * a short description of the application using the service, suitable for inclusion in a
     * user agent string for REST/HTTP requests. Ideally this would include the application's
     * version number, for example: <code>Cockpit/0.5.1</code> or <code>My App Name/1.0</code>
     * @throws S3ServiceException
     */
    public SoapS3Service(AWSCredentials awsCredentials, String invokingApplicationDescription) 
        throws S3ServiceException 
    {
        super(awsCredentials, invokingApplicationDescription);
        
        locator = new AmazonS3_ServiceLocator();
        if (super.isHttpsOnly()) {
            // Use an SSL connection, to further secure the signature. 
            log.debug("SOAP service will use HTTPS for all communication");            
            locator.setAmazonS3EndpointAddress("https://" + Constants.S3_HOSTNAME + "/soap");
        } else {
            log.debug("SOAP service will use HTTP for all communication");
            locator.setAmazonS3EndpointAddress("http://" + Constants.S3_HOSTNAME + "/soap");            
        }
        // Ensure we can get the stub.
        getSoapBinding();
    }
    
    /**
     * Constructs the SOAP service implementation and, based on the value of {@link S3Service#isHttpsOnly}
     * sets the SOAP endpoint to use HTTP or HTTPS protocols.
     * 
     * @param awsCredentials
     * @throws S3ServiceException
     */
    public SoapS3Service(AWSCredentials awsCredentials) throws S3ServiceException { 
        this(awsCredentials, null);
    }

    private AmazonS3SoapBindingStub getSoapBinding() throws S3ServiceException {
        try {
            return (AmazonS3SoapBindingStub) locator.getAmazonS3();
        } catch (ServiceException e) {
            throw new S3ServiceException("Unable to initialise SOAP binding", e);
        }
    }
    
    private String getAWSAccessKey() {
        if (getAWSCredentials() == null) {
            return null;
        } else {
            return getAWSCredentials().getAccessKey();
        }
    }
    
    private String getAWSSecretKey() {
        if (getAWSCredentials() == null) {
            return null;
        } else {
            return getAWSCredentials().getSecretKey();
        }
    }

    private Calendar getTimeStamp( long timestamp ) throws ParseException {
        if (getAWSCredentials() == null) {
            return null;
        }
        Calendar ts = new GregorianCalendar();
        Date date = ServiceUtils.parseIso8601Date(convertDateToString(ts));
        ts.setTime(date);
        return ts;
    }

    private String convertDateToString(Calendar cal) {
        if (cal != null) {
            return ServiceUtils.formatIso8601Date(cal.getTime());
        } else {
            return "";
        }
    }
    
    private S3Owner convertOwner(CanonicalUser user) {
        S3Owner owner = new S3Owner(user.getID(), user.getDisplayName());
        return owner;
    }
    
    /**
     * Converts a SOAP object AccessControlPolicy to a jets3t AccessControlList.
     * 
     * @param policy
     * @return
     * @throws S3ServiceException
     */
    private AccessControlList convertAccessControlTypes(AccessControlPolicy policy) 
        throws S3ServiceException 
    {
        AccessControlList acl = new AccessControlList();
        acl.setOwner(convertOwner(policy.getOwner()));
        
        Grant[] grants = policy.getAccessControlList();
        for (int i = 0; i < grants.length; i++) {
            Grant grant = (Grant) grants[i];
            org.jets3t.service.acl.Permission permission =
                org.jets3t.service.acl.Permission.parsePermission(grant.getPermission().toString());            
            
            Grantee grantee = grant.getGrantee();
            if (grantee instanceof Group) {
                GroupGrantee jets3tGrantee = new GroupGrantee();
                jets3tGrantee.setIdentifier(((Group)grantee).getURI());                
                acl.grantPermission(jets3tGrantee, permission);                
            } else if (grantee instanceof CanonicalUser) {
                CanonicalUser canonicalUser = (CanonicalUser) grantee;
                CanonicalGrantee jets3tGrantee = new CanonicalGrantee();
                jets3tGrantee.setIdentifier(canonicalUser.getID());
                jets3tGrantee.setDisplayName(canonicalUser.getDisplayName());
                acl.grantPermission(jets3tGrantee, permission);                
            } else if (grantee instanceof AmazonCustomerByEmail) {
                AmazonCustomerByEmail customerByEmail = (AmazonCustomerByEmail) grantee;
                EmailAddressGrantee jets3tGrantee = new EmailAddressGrantee();
                jets3tGrantee.setIdentifier(customerByEmail.getEmailAddress());
                acl.grantPermission(jets3tGrantee, permission);                
            } else {
                throw new S3ServiceException("Unrecognised grantee type: " + grantee.getClass());
            }
        }
        return acl;
    }
    
    /**
     * Converts a jets3t AccessControlList object to an array of SOAP Grant objects.
     * 
     * @param acl
     * @return
     * @throws S3ServiceException
     */
    private Grant[] convertACLtoGrants(AccessControlList acl) throws S3ServiceException {
        if (acl == null) {
            return null;
        }
        if (acl.isCannedRestACL()) {
            throw new S3ServiceException("Cannot use canned REST ACLs with SOAP service");        
        }        
        
        Grant[] grants = new Grant[acl.getGrants().size()];
            
        Iterator grantIter = acl.getGrants().iterator();
        int index = 0;
        while (grantIter.hasNext()) {
            GrantAndPermission jets3tGaP = (GrantAndPermission) grantIter.next();
            GranteeInterface jets3tGrantee = jets3tGaP.getGrantee();
            Grant grant = new Grant();
            
            if (jets3tGrantee instanceof GroupGrantee) {
                GroupGrantee groupGrantee = (GroupGrantee) jets3tGrantee;
                Group group = new Group();
                group.setURI(groupGrantee.getIdentifier());
                grant.setGrantee(group);
            } else if (jets3tGrantee instanceof CanonicalGrantee) {
                CanonicalGrantee canonicalGrantee = (CanonicalGrantee) jets3tGrantee;
                CanonicalUser canonicalUser = new CanonicalUser();
                canonicalUser.setID(canonicalGrantee.getIdentifier());
                canonicalUser.setDisplayName(canonicalGrantee.getDisplayName());
                grant.setGrantee(canonicalUser);
            } else if (jets3tGrantee instanceof EmailAddressGrantee) {
                EmailAddressGrantee emailGrantee = (EmailAddressGrantee) jets3tGrantee;
                AmazonCustomerByEmail customerByEmail = new AmazonCustomerByEmail();
                customerByEmail.setEmailAddress(emailGrantee.getIdentifier());
                grant.setGrantee(customerByEmail);
            } else {
                throw new S3ServiceException("Unrecognised jets3t grantee type: " 
                    + jets3tGrantee.getClass());
            }
            Permission permission = Permission.fromString(jets3tGaP.getPermission().toString());
            grant.setPermission(permission);
            grants[index++] = grant;
        }
        return grants;
    }

    /**
     * Converts metadata information from a standard map to SOAP objects.
     * 
     * @param metadataMap
     * @return
     */
    private MetadataEntry[] convertMetadata(Map metadataMap) {
        MetadataEntry[] metadata = new MetadataEntry[metadataMap.size()];
        Iterator metadataIter = metadataMap.entrySet().iterator();
        int index = 0;
        while (metadataIter.hasNext()) {
            Map.Entry entry = (Map.Entry) metadataIter.next();
            Object metadataName = entry.getKey();
            Object metadataValue = entry.getValue();
            log.debug("Setting metadata: " + metadataName + "=" + metadataValue);
            MetadataEntry mdEntry = new MetadataEntry();
            mdEntry.setName(metadataName.toString());
            mdEntry.setValue(metadataValue.toString());
            metadata[index++] = mdEntry;
        }
        return metadata;
    }
    
    ////////////////////////////////////////////////////////////////
    // Methods below this point implement S3Service abstract methods
    ////////////////////////////////////////////////////////////////    
    
    protected S3Bucket[] listAllBucketsImpl() throws S3ServiceException {
        log.debug("Listing all buckets for AWS user: " + getAWSCredentials().getAccessKey());
        
        S3Bucket[] buckets = null;
        try {
            AmazonS3SoapBindingStub s3SoapBinding = getSoapBinding();
            Calendar timestamp = getTimeStamp( System.currentTimeMillis() );            
            String signature = ServiceUtils.signWithHmacSha1(getAWSSecretKey(), 
                Constants.SOAP_SERVICE_NAME + "ListAllMyBuckets" + convertDateToString(timestamp));
            ListAllMyBucketsResult result = s3SoapBinding.listAllMyBuckets(
                getAWSAccessKey(), timestamp, signature);

            ListAllMyBucketsEntry[] entries = result.getBuckets();            
            buckets = new S3Bucket[entries.length];
            int index = 0;
            for (int i = 0; i < entries.length; i++) {
                ListAllMyBucketsEntry entry = (ListAllMyBucketsEntry) entries[i];
                S3Bucket bucket = new S3Bucket();
                bucket.setName(entry.getName());
                bucket.setCreationDate(entry.getCreationDate().getTime());
                buckets[index++] = bucket;
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new S3ServiceException("Unable to List Buckets", e);
        }
        return buckets;
    }

    public boolean isBucketAccessible(String bucketName) throws S3ServiceException {
        log.debug("Checking existence of bucket: " + bucketName);
        try {
            AmazonS3SoapBindingStub s3SoapBinding = getSoapBinding();
            Calendar timestamp = getTimeStamp( System.currentTimeMillis() );
            String signature = ServiceUtils.signWithHmacSha1(getAWSSecretKey(), 
                        Constants.SOAP_SERVICE_NAME + "ListBucket" + convertDateToString(timestamp));
            s3SoapBinding.listBucket(
                bucketName, null, null, new Integer(0), 
                null, getAWSAccessKey(), timestamp, signature, null);
            
            // If we get this far, the bucket exists.
            return true;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return false;
        }
    }
    
    protected S3Object[] listObjectsImpl(String bucketName, String prefix, String delimiter, long maxListingLength)
        throws S3ServiceException
    {
        return listObjectsInternalImpl(bucketName, prefix, delimiter, maxListingLength, true, null)
            .getObjects();            
    }
    
    protected S3ObjectsChunk listObjectsChunkedImpl(String bucketName, String prefix, String delimiter, long maxListingLength, String priorLastKey) throws S3ServiceException {
        return listObjectsInternalImpl(bucketName, prefix, delimiter, maxListingLength, false, priorLastKey);
    }    

    protected S3ObjectsChunk listObjectsInternalImpl(String bucketName, String prefix, 
        String delimiter, long maxListingLength, boolean automaticallyMergeChunks, String priorLastKey)
        throws S3ServiceException
    {
        ArrayList objects = new ArrayList();        
        ArrayList commonPrefixes = new ArrayList();

        boolean incompleteListing = true;            

        try {
            while (incompleteListing) {
                AmazonS3SoapBindingStub s3SoapBinding = getSoapBinding();
                Calendar timestamp = getTimeStamp( System.currentTimeMillis() );
                String signature = ServiceUtils.signWithHmacSha1(getAWSSecretKey(), 
                    Constants.SOAP_SERVICE_NAME + "ListBucket" + convertDateToString(timestamp));
                ListBucketResult result = s3SoapBinding.listBucket(
                    bucketName, prefix, priorLastKey, new Integer((int)maxListingLength), 
                    delimiter, getAWSAccessKey(), timestamp, signature, null);
                
                ListEntry[] entries = result.getContents();
                S3Object[] partialObjects = new S3Object[
                   (entries == null? 0 : entries.length)];
                
                log.debug("Found " + partialObjects.length + " objects in one batch");
                for (int i = 0; entries != null && i < entries.length; i++) {
                    ListEntry entry = entries[i];
                    S3Object object = new S3Object(entry.getKey());
                    object.setLastModifiedDate(entry.getLastModified().getTime());
                    object.setETag(entry.getETag());
                    object.setContentLength(entry.getSize());
                    object.setStorageClass(entry.getStorageClass().toString());
                    object.setOwner(convertOwner(entry.getOwner()));
                    partialObjects[i] = object;
                    
                    // This shouldn't be necessary, but result.getNextMarker() doesn't work as expected.
                    priorLastKey = object.getKey();
                }
                
                objects.addAll(Arrays.asList(partialObjects));
                
                PrefixEntry[] prefixEntries = result.getCommonPrefixes();
                if (prefixEntries != null) {
                    log.debug("Found " + prefixEntries.length + " common prefixes in one batch");                    
                }
                for (int i = 0; prefixEntries != null && i < prefixEntries.length; i++ ) {
                    PrefixEntry entry = prefixEntries[i];
                    commonPrefixes.add(entry.getPrefix());
                }
                
                incompleteListing = result.isIsTruncated();
                if (incompleteListing) {
                	if (result.getNextMarker() != null) {
                		// Use NextMarker as the marker for where subsequent listing should start
	                    priorLastKey = result.getNextMarker();
                	} else {
                		// TODO: Why doesn't result.getNextMarker() actually return the marker value?
                		// Use the prior last key instead of NextMarker if it isn't available.
                	}
                    log.debug("Yet to receive complete listing of bucket contents, "
                        + "last key for prior chunk: " + priorLastKey);
                } else {
                    priorLastKey = null;
                }
                if (!automaticallyMergeChunks)
                    break;
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new S3ServiceException("Unable to List Objects in bucket: " + bucketName, e);   
        }
        if (automaticallyMergeChunks) {
            log.debug("Found " + objects.size() + " objects in total");
            return new S3ObjectsChunk(
                (S3Object[]) objects.toArray(new S3Object[objects.size()]), 
                (String[]) commonPrefixes.toArray(new String[commonPrefixes.size()]),
                null);
        } else {
            return new S3ObjectsChunk(
                (S3Object[]) objects.toArray(new S3Object[objects.size()]), 
                (String[]) commonPrefixes.toArray(new String[commonPrefixes.size()]),
                priorLastKey);            
        }
    }

    protected S3Bucket createBucketImpl(String bucketName, String location, 
        AccessControlList acl) throws S3ServiceException 
    {
        if (location != S3Bucket.LOCATION_US) {
            throw new S3ServiceException("The SOAP API interface for S3 does " +
                "not allow you to create buckets located anywhere other than " +
                "the US");
        }
        
        Grant[] grants = null;
        if (acl != null) {
            grants = convertACLtoGrants(acl);        
        }
        try {
            AmazonS3SoapBindingStub s3SoapBinding = getSoapBinding();
            Calendar timestamp = getTimeStamp( System.currentTimeMillis() );
            String signature = ServiceUtils.signWithHmacSha1(getAWSSecretKey(), 
                Constants.SOAP_SERVICE_NAME + "CreateBucket" + convertDateToString(timestamp));
            s3SoapBinding.createBucket(
                bucketName, grants, getAWSAccessKey(), timestamp, signature);
            
            S3Bucket bucket = new S3Bucket(bucketName);
            bucket.setAcl(acl);
            return bucket;            
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new S3ServiceException("Unable to Create Bucket: " + bucketName, e);   
        }
    }
    
    protected String getBucketLocationImpl(String bucketName) 
        throws S3ServiceException
    {
        throw new S3ServiceException("The SOAP API interface for S3 does " +
            "not allow you to retrieve location information for a bucket");    
    }

    
    protected void deleteBucketImpl(String bucketName) throws S3ServiceException {
        try {
            AmazonS3SoapBindingStub s3SoapBinding = getSoapBinding();
            Calendar timestamp = getTimeStamp( System.currentTimeMillis() );
            String signature = ServiceUtils.signWithHmacSha1(getAWSSecretKey(), 
                Constants.SOAP_SERVICE_NAME + "DeleteBucket" + convertDateToString(timestamp));
            s3SoapBinding.deleteBucket(
                bucketName, getAWSAccessKey(), timestamp, signature, null);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new S3ServiceException("Unable to Delete Bucket: " + bucketName, e);   
        }            
    }

    protected S3Object putObjectImpl(String bucketName, S3Object object) throws S3ServiceException {
        log.debug("Creating Object with key " + object.getKey() + " in bucket " + bucketName);        

        Grant[] grants = null;
        if (object.getAcl() != null) {
            grants = convertACLtoGrants(object.getAcl());
        }
        MetadataEntry[] metadata = convertMetadata(object.getMetadataMap());
        
        try {
            AmazonS3SoapBindingStub s3SoapBinding = getSoapBinding();
            long contentLength = object.getContentLength();
            String contentType = object.getContentType();
            if (contentType == null) {
                // Set default content type.
                contentType = Mimetypes.MIMETYPE_OCTET_STREAM;
            }
            
            if (object.getDataInputStream() != null) {                
                log.debug("Uploading data input stream for S3Object: " + object.getKey());
                
                if (contentLength == 0 && object.getDataInputStream().available() > 0) {
                    
                    log.warn("S3Object " + object.getKey() 
                        + " - Content-Length was set to 0 despite having a non-empty data"
                        + " input stream. The Content-length will be determined in memory.");
                    
                    // Read all data into memory to determine it's length.
                    BufferedInputStream bis = new BufferedInputStream(object.getDataInputStream());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    BufferedOutputStream bos = new BufferedOutputStream(baos);
                    try {
                        byte[] buffer = new byte[8192];
                        int read = -1;
                        while ((read = bis.read(buffer)) != -1) {
                            bos.write(buffer, 0, read);
                        }
                    } finally {
                        if (bis != null) {
                            bis.close();                            
                        }
                        if (bos != null) {
                            bos.close();                        
                        }
                    }

                    contentLength = baos.size();
                    object.setDataInputStream(new ByteArrayInputStream(baos.toByteArray()));
                    
                    log.debug("Content-Length value has been reset to " + contentLength);
                }                
                
                DataHandler dataHandler = new DataHandler(
                    new SourceDataSource(
                        null, contentType, new StreamSource(object.getDataInputStream())));           
                s3SoapBinding.addAttachment(dataHandler);                
            } else {
                DataHandler dataHandler = new DataHandler(
                    new SourceDataSource(
                        null, contentType, new StreamSource()));
                s3SoapBinding.addAttachment(dataHandler);                
            }
            
            Calendar timestamp = getTimeStamp( System.currentTimeMillis() );
            String signature = ServiceUtils.signWithHmacSha1(getAWSSecretKey(), 
                Constants.SOAP_SERVICE_NAME + "PutObject" + convertDateToString(timestamp));
            PutObjectResult result = 
                s3SoapBinding.putObject(bucketName, object.getKey(), metadata, 
                    contentLength, grants, null, getAWSAccessKey(), 
                    timestamp, signature, null);
            
            // Ensure no data was corrupted, if we have the MD5 hash available to check.
            String eTag = result.getETag().substring(1, result.getETag().length() - 1);
            String eTagAsBase64 = ServiceUtils.toBase64(
                ServiceUtils.fromHex(eTag));
            String md5HashAsBase64 = object.getMd5HashAsBase64();            
            if (md5HashAsBase64 != null && !eTagAsBase64.equals(md5HashAsBase64)) {
                throw new S3ServiceException(
                    "Object created but ETag returned by S3 does not match MD5 hash value of object");
            }
            
            object.setETag(result.getETag());
            object.setContentLength(contentLength);
            object.setContentType(contentType);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new S3ServiceException("Unable to Create Object: " + object.getKey(), e);   
        }
        return object;
    }

    protected void deleteObjectImpl(String bucketName, String objectKey) throws S3ServiceException {
        try {
            AmazonS3SoapBindingStub s3SoapBinding = getSoapBinding();
            Calendar timestamp = getTimeStamp( System.currentTimeMillis() );
            String signature = ServiceUtils.signWithHmacSha1(getAWSSecretKey(), 
                Constants.SOAP_SERVICE_NAME + "DeleteObject" + convertDateToString(timestamp));
            s3SoapBinding.deleteObject(bucketName, objectKey, 
                getAWSAccessKey(), timestamp, signature, null);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new S3ServiceException("Unable to Delete Object: " + objectKey, e);   
        } 
    }

    protected S3Object getObjectDetailsImpl(String bucketName, String objectKey, Calendar ifModifiedSince, 
        Calendar ifUnmodifiedSince, String[] ifMatchTags, String[] ifNoneMatchTags) 
        throws S3ServiceException
    {
        return getObjectImpl(false, bucketName, objectKey, ifModifiedSince, ifUnmodifiedSince,
            ifMatchTags, ifNoneMatchTags, null, null);
    }
    
    protected S3Object getObjectImpl(String bucketName, String objectKey, Calendar ifModifiedSince, 
        Calendar ifUnmodifiedSince, String[] ifMatchTags, String[] ifNoneMatchTags, 
        Long byteRangeStart, Long byteRangeEnd)
        throws S3ServiceException 
    {
        return getObjectImpl(true, bucketName, objectKey, ifModifiedSince, ifUnmodifiedSince,
            ifMatchTags, ifNoneMatchTags, byteRangeStart, byteRangeEnd);
    }    

    private S3Object getObjectImpl(boolean withData, String bucketName, String objectKey, 
        Calendar ifModifiedSince, Calendar ifUnmodifiedSince, String[] ifMatchTags, 
        String[] ifNoneMatchTags, Long byteRangeStart, Long byteRangeEnd)
        throws S3ServiceException
    {
        boolean useExtendedGet = 
            ifModifiedSince != null || ifUnmodifiedSince != null
            || ifMatchTags != null || ifNoneMatchTags != null 
            || byteRangeStart != null || byteRangeEnd != null;
        
        try {
            AmazonS3SoapBindingStub s3SoapBinding = getSoapBinding();
            Calendar timestamp = getTimeStamp( System.currentTimeMillis() );
            GetObjectResult result = null;
            
            if (useExtendedGet) {
                log.debug("Using Extended GET to apply constraints: "
                    + "ifModifiedSince=" + (ifModifiedSince != null? ifModifiedSince.getTime().toString() : "null")
                    + ", ifUnmodifiedSince=" + (ifUnmodifiedSince != null? ifUnmodifiedSince.getTime().toString() : "null")
                    + ", ifMatchTags=" + (ifMatchTags != null? Arrays.asList(ifMatchTags).toString() : "null") 
                    + ", ifNoneMatchTags=" + (ifNoneMatchTags != null? Arrays.asList(ifNoneMatchTags).toString() : "null")
                    + ", byteRangeStart=" + byteRangeStart + ", byteRangeEnd=" + byteRangeEnd);
                
                String signature = ServiceUtils.signWithHmacSha1(getAWSSecretKey(), 
                    Constants.SOAP_SERVICE_NAME + "GetObjectExtended" + convertDateToString(timestamp));
                result = s3SoapBinding.getObjectExtended(
                    bucketName, objectKey, true, withData, false, byteRangeStart, byteRangeEnd,
                    ifModifiedSince, ifUnmodifiedSince, ifMatchTags, ifNoneMatchTags,
                    Boolean.FALSE, getAWSAccessKey(), timestamp, signature, null);
                
                // Throw an exception if the preconditions failed.
                int expectedStatusCode = 200;
                if (byteRangeStart != null || byteRangeEnd != null) {
                    // Partial data responses have a status code of 206. 
                    expectedStatusCode = 206;
                }
                if (result.getStatus().getCode() != expectedStatusCode) {
                    throw new S3ServiceException("Precondition failed when getting object "
                        + objectKey + ": " + result.getStatus().getDescription());
                }
            } else {
                log.debug("Using standard GET (no constraints to apply)");
                String signature = ServiceUtils.signWithHmacSha1(getAWSSecretKey(), 
                    Constants.SOAP_SERVICE_NAME + "GetObject" + convertDateToString(timestamp));
                result = s3SoapBinding.getObject(
                    bucketName, objectKey, true, withData, false,                
                    getAWSAccessKey(), timestamp, signature, null);                
            }
            
            S3Object object = new S3Object(objectKey);
            object.setETag(result.getETag());
            object.setLastModifiedDate(result.getLastModified().getTime());
            object.setBucketName(bucketName);
            
            // Get data details from the SOAP attachment.
            if (withData) {
                Object[] attachments = s3SoapBinding.getAttachments();
                log.debug("SOAP attachment count for " + object.getKey() + ": " + attachments.length);
                for (int i = 0; i < attachments.length; i++) {
                    if (i > 0) {
                        throw new S3ServiceException(
                            "Received multiple SOAP attachment parts, this shouldn't happen");
                    }
                    AttachmentPart part = (AttachmentPart) attachments[i];
                    object.setContentType(part.getContentType());
                    object.setContentLength(part.getSize());
                    object.setDataInputStream(part.getDataHandler().getInputStream());
                }
            }
            
            // Populate object's metadata details.
            MetadataEntry[] metadata = result.getMetadata();
            for (int i = 0; i < metadata.length; i++) {
                MetadataEntry entry = metadata[i];
                object.addMetadata(entry.getName(), entry.getValue());
            }
            object.setMetadataComplete(true);
            
            return object;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new S3ServiceException("Unable to Get Object: " + objectKey, e);   
        } 
    }

    protected void putObjectAclImpl(String bucketName, String objectKey, AccessControlList acl) 
        throws S3ServiceException 
    {
        try {
            AmazonS3SoapBindingStub s3SoapBinding = getSoapBinding();
            Calendar timestamp = getTimeStamp( System.currentTimeMillis() );
            Grant[] grants = convertACLtoGrants(acl);
                
            String signature = ServiceUtils.signWithHmacSha1(getAWSSecretKey(), 
                Constants.SOAP_SERVICE_NAME + "SetObjectAccessControlPolicy" + convertDateToString(timestamp));
            s3SoapBinding.setObjectAccessControlPolicy(bucketName, objectKey, grants, 
                getAWSAccessKey(), timestamp, signature, null);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new S3ServiceException("Unable to Put Object ACL", e);   
        }
    }

    protected void putBucketAclImpl(String bucketName, AccessControlList acl) 
        throws S3ServiceException 
    {
        try {
            AmazonS3SoapBindingStub s3SoapBinding = getSoapBinding();
            Calendar timestamp = getTimeStamp( System.currentTimeMillis() );
            Grant[] grants = convertACLtoGrants(acl);
                
            String signature = ServiceUtils.signWithHmacSha1(getAWSSecretKey(), 
                Constants.SOAP_SERVICE_NAME + "SetBucketAccessControlPolicy" + convertDateToString(timestamp));
            s3SoapBinding.setBucketAccessControlPolicy(bucketName, grants, 
                getAWSAccessKey(), timestamp, signature, null);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new S3ServiceException("Unable to Put Bucket ACL", e);   
        }        
    }

    protected AccessControlList getObjectAclImpl(String bucketName, String objectKey) throws S3ServiceException {
        try {
            AmazonS3SoapBindingStub s3SoapBinding = getSoapBinding();
            Calendar timestamp = getTimeStamp( System.currentTimeMillis() );
            String signature = ServiceUtils.signWithHmacSha1(getAWSSecretKey(), 
                Constants.SOAP_SERVICE_NAME + "GetObjectAccessControlPolicy" + convertDateToString(timestamp));
            AccessControlPolicy result = s3SoapBinding.getObjectAccessControlPolicy(
                bucketName, objectKey, getAWSAccessKey(), 
                timestamp, signature, null);
            return convertAccessControlTypes(result);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new S3ServiceException("Unable to Get ACL", e);   
        }
    }

    protected AccessControlList getBucketAclImpl(String bucketName) throws S3ServiceException {
        try {
            AmazonS3SoapBindingStub s3SoapBinding = getSoapBinding();
            Calendar timestamp = getTimeStamp( System.currentTimeMillis() );
            String signature = ServiceUtils.signWithHmacSha1(getAWSSecretKey(), 
                Constants.SOAP_SERVICE_NAME + "GetBucketAccessControlPolicy" + convertDateToString(timestamp));
            AccessControlPolicy result = s3SoapBinding.getBucketAccessControlPolicy(bucketName, 
                getAWSAccessKey(), timestamp, signature, null);
            return convertAccessControlTypes(result);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new S3ServiceException("Unable to Get ACL", e);   
        }
    }

    protected S3BucketLoggingStatus getBucketLoggingStatusImpl(String bucketName) throws S3ServiceException {
        try {
            AmazonS3SoapBindingStub s3SoapBinding = getSoapBinding();
            Calendar timestamp = getTimeStamp( System.currentTimeMillis() );
            String signature = ServiceUtils.signWithHmacSha1(getAWSSecretKey(), 
                Constants.SOAP_SERVICE_NAME + "GetBucketLoggingStatus" + convertDateToString(timestamp));
            
            BucketLoggingStatus loggingStatus = s3SoapBinding.getBucketLoggingStatus(                
                bucketName, getAWSAccessKey(), timestamp, signature, null);            
            LoggingSettings loggingSettings = loggingStatus.getLoggingEnabled();
            if (loggingSettings != null) {
                return new S3BucketLoggingStatus(loggingSettings.getTargetBucket(), loggingSettings.getTargetPrefix());                
            } else {
                return new S3BucketLoggingStatus();
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new S3ServiceException("Unable to Get Bucket logging status for " + bucketName, e);   
        }        
    }

    protected void setBucketLoggingStatusImpl(String bucketName, S3BucketLoggingStatus status) throws S3ServiceException {
        try {
            AmazonS3SoapBindingStub s3SoapBinding = getSoapBinding();
            Calendar timestamp = getTimeStamp( System.currentTimeMillis() );
            String signature = ServiceUtils.signWithHmacSha1(getAWSSecretKey(), 
                Constants.SOAP_SERVICE_NAME + "SetBucketLoggingStatus" + convertDateToString(timestamp));
            
            LoggingSettings loggingSettings = null;
            if (status.isLoggingEnabled()) {
                loggingSettings = new LoggingSettings(status.getTargetBucketName(), status.getLogfilePrefix());                
            }
            
            s3SoapBinding.setBucketLoggingStatus(
                bucketName, getAWSAccessKey(), timestamp, signature, null, 
                new BucketLoggingStatus(loggingSettings)); 
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new S3ServiceException("Unable to Set Bucket logging status for " + bucketName
                + ": " + status, e);   
        }        
    }

}
