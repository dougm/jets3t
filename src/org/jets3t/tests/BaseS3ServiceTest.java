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
package org.jets3t.tests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.jets3t.service.S3ObjectsChunk;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3BucketLoggingStatus;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.model.S3Owner;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.utils.FileComparer;
import org.jets3t.service.utils.Mimetypes;
import org.jets3t.service.utils.ServiceUtils;

/**
 * Runs S3Service-related tests. S3Service implementations should implement more specific
 * versions of this class.
 * 
 * @author James Murty
 */
public abstract class BaseS3ServiceTest extends TestCase {
    protected String TEST_PROPERTIES_FILENAME = "test.properties";
    protected AWSCredentials awsCredentials = null;
    
    public BaseS3ServiceTest() throws IOException {
        InputStream propertiesIS = 
            ClassLoader.getSystemResourceAsStream(TEST_PROPERTIES_FILENAME);
        
        if (propertiesIS == null) {
            throw new IOException("Unable to load test properties file from classpath: " 
                + TEST_PROPERTIES_FILENAME);
        }
        
        Properties testProperties = new Properties();        
        testProperties.load(propertiesIS);
        awsCredentials = new AWSCredentials(
            testProperties.getProperty("aws.accesskey"),
            testProperties.getProperty("aws.secretkey"));
    }
    
    protected abstract S3Service getS3Service(AWSCredentials awsCredentials) throws S3ServiceException;
            
    public void testObtainAnonymousServices() throws Exception {
        getS3Service(null);
    }

    public void testListBucketsWithoutCredentials() throws Exception {
        try {
            getS3Service(null).listAllBuckets();
            fail("Bucket listing should fail without authentication");
        } catch (S3ServiceException e) {
        }
    }

    public void testListBucketsWithCredentials() throws Exception {
        getS3Service(awsCredentials).listAllBuckets();
    }

    public void testBucketManagement() throws Exception {
        S3Service s3Service = getS3Service(awsCredentials);

        try {
            s3Service.createBucket(new S3Bucket());
            fail("Cannot create a bucket with name unset");
        } catch (S3ServiceException e) {
        }

        try {
            s3Service.createBucket("");
            fail("Cannot create a bucket with empty name");
        } catch (S3ServiceException e) {
        }

        try {
            s3Service.createBucket("test");
            fail("Cannot create a bucket with non-unique name");
        } catch (S3ServiceException e) {
        }

        String bucketName = awsCredentials.getAccessKey() + ".S3ServiceTest1";
        s3Service.createBucket(bucketName);

        boolean bucketExists = s3Service.isBucketAccessible(bucketName);
        assertTrue("Bucket should exist", bucketExists);

        try {
            s3Service.deleteBucket((S3Bucket) null);
            fail("Cannot delete a bucket with name null");
        } catch (S3ServiceException e) {
        }

        try {
            s3Service.deleteBucket("");
            fail("Cannot delete a bucket with empty name");
        } catch (S3ServiceException e) {
        }

        try {
            s3Service.deleteBucket("test");
            fail("Cannot delete a bucket you don't own");
        } catch (S3ServiceException e) {
        }

        s3Service.deleteBucket(bucketName);
    }

    public void testObjectManagement() throws Exception {
        S3Service s3Service = getS3Service(awsCredentials);

        String bucketName = awsCredentials.getAccessKey() + ".S3ServiceTest2";

        S3Bucket bucket = s3Service.createBucket(bucketName);
        S3Object object = new S3Object();
        object.setKey("TestObject");

        try {
            s3Service.putObject( (S3Bucket) null, null);
            fail("Cannot create an object without a valid bucket");
        } catch (S3ServiceException e) {
        }

        try {
            s3Service.putObject( (S3Bucket) null, object);
            fail("Cannot create an object without a valid bucket");
        } catch (S3ServiceException e) {
        }

        try {
            s3Service.putObject(bucket, new S3Object());
            fail("Cannot create an object without a valid object");
        } catch (S3ServiceException e) {
        }

        // Create basic object with default content type and no data.
        S3Object basicObject = s3Service.putObject(bucket, object);
        assertEquals("Unexpected default content type", Mimetypes.MIMETYPE_OCTET_STREAM,
            basicObject.getContentType());

        // Retrieve object to ensure it was correctly created.
        basicObject = s3Service.getObject(bucket, object.getKey());
        assertEquals("Unexpected default content type", Mimetypes.MIMETYPE_OCTET_STREAM,
            basicObject.getContentType());
        assertEquals("Unexpected size for 'empty' object", 0, basicObject.getContentLength());
        basicObject.getDataInputStream().close();

        // Make sure bucket cannot be removed while it has contents.
        try {
            s3Service.deleteBucket(bucket.getName());
            fail("Cannot delete a bucket containing objects");
        } catch (S3ServiceException e) {
        }

        // Update/overwrite object to be a 'directory' object which has a specific content type and
        // no data.
        String contentType = Mimetypes.MIMETYPE_JETS3T_DIRECTORY;
        object.setContentType(contentType);
        S3Object directoryObject = s3Service.putObject(bucket, object);
        assertEquals("Unexpected default content type", contentType, directoryObject
            .getContentType());

        // Retrieve object to ensure it was correctly created.
        directoryObject = s3Service.getObject(bucket, object.getKey());
        assertEquals("Unexpected default content type", contentType, directoryObject
            .getContentType());
        assertEquals("Unexpected size for 'empty' object", 0, directoryObject.getContentLength());
        basicObject.getDataInputStream().close();

        // Update/overwrite object with real data content and some metadata.
        contentType = "text/plain";
        String objectData = "Just some rubbish text to include as data";
        String dataHash = ServiceUtils.computeMD5Hash(objectData.getBytes());
        HashMap metadata = new HashMap();
        metadata.put("creator", "S3ServiceTest");
        metadata.put("purpose", "For testing purposes");
        object.replaceAllMetadata(metadata);
        object.setContentType(contentType);
        object.setDataInputStream(new ByteArrayInputStream(objectData.getBytes()));
        S3Object dataObject = s3Service.putObject(bucket, object);
        assertEquals("Unexpected content type", contentType, dataObject.getContentType());
        assertEquals("Mismatching hash", dataHash, dataObject.getETag());

        // Retrieve data object to ensure it was correctly created, the server-side hash matches
        // what we expect, and we get our metadata back.
        dataObject = s3Service.getObject(bucket, object.getKey());
        assertEquals("Unexpected default content type", "text/plain", dataObject.getContentType());
        assertEquals("Unexpected size for object", objectData.length(), dataObject
            .getContentLength());
        assertEquals("Mismatching hash", dataHash, dataObject.getETag());
        assertEquals("Missing creator metadata", "S3ServiceTest", dataObject.getMetadata().get(
            "creator"));
        assertEquals("Missing purpose metadata", "For testing purposes", dataObject.getMetadata()
            .get("purpose"));
        assertNotNull("Expected data input stream to be available", dataObject.getDataInputStream());
        // Ensure we can get the data from S3.
        StringBuffer sb = new StringBuffer();
        int b = -1;
        while ((b = dataObject.getDataInputStream().read()) != -1) {
            sb.append((char) b);
        }
        dataObject.getDataInputStream().close();
        assertEquals("Mismatching data", objectData, sb.toString());

        // Retrieve only HEAD of data object (all metadata is available, but not the object content
        // data input stream)
        dataObject = s3Service.getObjectDetails(bucket, object.getKey());
        assertEquals("Unexpected default content type", "text/plain", dataObject.getContentType());
        assertEquals("Unexpected size for object", objectData.length(), dataObject.getContentLength());
        assertEquals("Mismatching hash", dataHash, dataObject.getETag());
        assertEquals("Missing creator metadata", "S3ServiceTest", dataObject.getMetadata().get(
            "creator"));
        assertEquals("Missing purpose metadata", "For testing purposes", dataObject.getMetadata()
            .get("purpose"));
        assertNull("Expected data input stream to be unavailable", dataObject.getDataInputStream());

        // Test object GET constraints.
        Calendar objectCreationTimeCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.US);
        objectCreationTimeCal.setTime(dataObject.getLastModifiedDate());
        
        objectCreationTimeCal.add(Calendar.SECOND, 1);
        Calendar afterObjectCreation = (Calendar) objectCreationTimeCal.clone();
        objectCreationTimeCal.add(Calendar.DAY_OF_YEAR, -1);
        Calendar yesterday = (Calendar) objectCreationTimeCal.clone();
        objectCreationTimeCal.add(Calendar.DAY_OF_YEAR, +2);
        Calendar tomorrow = (Calendar) objectCreationTimeCal.clone();

        // Precondition: Modified since yesterday
        s3Service.getObjectDetails(bucket, object.getKey(), yesterday, null, null, null);
        // Precondition: Mot modified since after creation date.
        try {
            s3Service.getObjectDetails(bucket, object.getKey(), afterObjectCreation, null, null, null);
            fail("Cannot have been modified since object was created");
        } catch (S3ServiceException e) { }
        // Precondition: Not modified since yesterday
        try {
            s3Service.getObjectDetails(bucket, object.getKey(), null, yesterday, null, null);
            fail("Cannot be unmodified since yesterday");
        } catch (S3ServiceException e) { }
        // Precondition: Not modified since tomorrow
        s3Service.getObjectDetails(bucket, object.getKey(), null, tomorrow, null, null);
        // Precondition: matches correct hash
        s3Service.getObjectDetails(bucket, object.getKey(), null, null, new String[] {dataHash}, null);
        // Precondition: doesn't match incorrect hash
        try {
            s3Service.getObjectDetails(bucket, object.getKey(), null, null, 
                new String[] {"__" + dataHash.substring(2)}, null);
            fail("Hash values should not match");
        } catch (S3ServiceException e) {
        }
        // Precondition: doesn't match correct hash
        try {
            s3Service.getObjectDetails(bucket, object.getKey(), null, null, null, new String[] {dataHash});
            fail("Hash values should mis-match");
        } catch (S3ServiceException e) {
        }
        // Precondition: doesn't match incorrect hash
        s3Service.getObjectDetails(bucket, object.getKey(), null, null, null, 
            new String[] {"__" + dataHash.substring(2)});

        // Retrieve only a limited byte-range of the data, with a start and end.
        Long byteRangeStart = new Long(3);
        Long byteRangeEnd = new Long(12);
        dataObject = s3Service.getObject(bucket, object.getKey(), null, null, null, null, byteRangeStart, byteRangeEnd);
        String dataReceived = readStringFromInputStream(dataObject.getDataInputStream());
        String dataExpected = objectData.substring(byteRangeStart.intValue(), byteRangeEnd.intValue() + 1);
        assertEquals("Mismatching data from range precondition", dataExpected, dataReceived);

        // Retrieve only a limited byte-range of the data, with a start range only.
        byteRangeStart = new Long(7);
        byteRangeEnd = null;
        dataObject = s3Service.getObject(bucket, object.getKey(), null, null, null, null, byteRangeStart, byteRangeEnd);
        dataReceived = readStringFromInputStream(dataObject.getDataInputStream());
        dataExpected = objectData.substring(byteRangeStart.intValue());
        assertEquals("Mismatching data from range precondition", dataExpected, dataReceived);

        // Retrieve only a limited byte-range of the data, with an end range only.
        byteRangeStart = null;
        byteRangeEnd = new Long(13);
        dataObject = s3Service.getObject(bucket, object.getKey(), null, null, null, null, byteRangeStart, byteRangeEnd);
        dataReceived = readStringFromInputStream(dataObject.getDataInputStream());
        dataExpected = objectData.substring(objectData.length() - byteRangeEnd.intValue());
        assertEquals("Mismatching data from range precondition", dataExpected, dataReceived);

        // Clean-up.
        s3Service.deleteObject(bucket, object.getKey());
        s3Service.deleteBucket(bucket.getName());
    }
    
    public void testACLManagement() throws Exception {
        String s3Url = "http://s3.amazonaws.com";
        
        // Access public "third-party" bucket
        S3Service anonymousS3Service = getS3Service(null);
        anonymousS3Service.isBucketAccessible("jetS3T");

        S3Service s3Service = getS3Service(awsCredentials);

        String bucketName = awsCredentials.getAccessKey() + ".S3ServiceTest3";
        S3Bucket bucket = s3Service.createBucket(bucketName);
        S3Object object = null;

        // Create private object (default permissions).
        String privateKey = "PrivateObject";
        object = new S3Object(bucket, privateKey, "Private object sample text");
        s3Service.putObject(bucket, object);
        URL url = new URL(s3Url + "/" + bucketName + "/" + privateKey);
        assertEquals("Expected denied access (403) error", 403, ((HttpURLConnection) url
            .openConnection()).getResponseCode());
        
        // Get ACL details for private object so we can determine the bucket owner.
        AccessControlList bucketACL = s3Service.getBucketAcl(bucket);
        S3Owner bucketOwner = bucketACL.getOwner();

        // Create a public object.
        String publicKey = "PublicObject";
        object = new S3Object(bucket, publicKey, "Public object sample text");        
        AccessControlList acl = new AccessControlList();
        acl.setOwner(bucketOwner);
        acl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ);
        object.setAcl(acl);
        s3Service.putObject(bucket, object);
        url = new URL(s3Url + "/" + bucketName + "/" + publicKey);      
        assertEquals("Expected access (200)", 
                200, ((HttpURLConnection)url.openConnection()).getResponseCode());

        // Update ACL to make private object public.
        AccessControlList privateToPublicACL = s3Service.getObjectAcl(bucket, privateKey);
        privateToPublicACL.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ);
        object.setKey(privateKey);
        object.setAcl(privateToPublicACL);
        s3Service.putObjectAcl(bucket, object);
        url = new URL(s3Url + "/" + bucketName + "/" + privateKey + "?"); // ? is hack to outsmart Web page caching at my ISP...
        assertEquals("Expected access (200)", 200, ((HttpURLConnection) url.openConnection())
            .getResponseCode());

        // Create a non-standard uncanned public object.
        String publicKey2 = "PublicObject2";
        object = new S3Object(publicKey2);
        object.setAcl(privateToPublicACL); // This ACL has ALL_USERS READ permission set above.
        s3Service.putObject(bucket, object);
        url = new URL(s3Url + "/" + bucketName + "/" + publicKey2);
        assertEquals("Expected access (200)", 200, ((HttpURLConnection) url.openConnection())
            .getResponseCode());

        // Update ACL to make public object private.
        AccessControlList publicToPrivateACL = s3Service.getObjectAcl(bucket, publicKey);
        publicToPrivateACL.revokeAllPermissions(GroupGrantee.ALL_USERS);
        object.setKey(publicKey);
        object.setAcl(publicToPrivateACL);
        s3Service.putObjectAcl(bucket, object);
        url = new URL(s3Url + "/" + bucketName + "/" + publicKey + "?"); // ? is hack to outsmart Web page caching at my ISP...
        assertEquals("Expected denied access (403) error", 403, ((HttpURLConnection) url
            .openConnection()).getResponseCode());

        // Generate URL granting anonymous user access.
        int secondsUntilExpiry = 3;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, secondsUntilExpiry);
        String urlString = S3Service.createSignedUrl(bucket.getName(), privateKey, awsCredentials,
            cal.getTimeInMillis() / 1000, false);
        url = new URL(urlString);
        assertEquals("Expected access (200)", 200, ((HttpURLConnection) url.openConnection())
            .getResponseCode());
        // Ensure anonymous user access URL expires.
        Thread.sleep((secondsUntilExpiry + 1) * 1000);
        assertEquals("Expected denied access (403) error", 403, ((HttpURLConnection) url
            .openConnection()).getResponseCode());

        // Clean-up.
        s3Service.deleteObject(bucket, privateKey);
        s3Service.deleteObject(bucket, publicKey);
        s3Service.deleteObject(bucket, publicKey2);
        s3Service.deleteBucket(bucket.getName());
    }
    
    public void testObjectListing() throws Exception {
        S3Service s3Service = getS3Service(awsCredentials);

        String bucketName = awsCredentials.getAccessKey() + ".S3ServiceTest4";

        S3Bucket bucket = s3Service.createBucket(bucketName);
        
        // Represent a directory structure in S3.
        List objectsList = new ArrayList();
        objectsList.add(new S3Object(bucket, "dir1"));
        objectsList.add(new S3Object(bucket, "dir1/doc1Level1"));
        objectsList.add(new S3Object(bucket, "dir1/doc2level1"));
        objectsList.add(new S3Object(bucket, "dir1/dir1Level1"));
        objectsList.add(new S3Object(bucket, "dir1/dir1Level1/doc1Level2"));
        objectsList.add(new S3Object(bucket, "dir1/dir1Level1/dir1Level2"));
        objectsList.add(new S3Object(bucket, "dir1/dir1Level1/dir1Level2/doc1Level3"));
        
        // Create objects
        Iterator iter = objectsList.iterator();
        while (iter.hasNext()) {
            S3Object object = (S3Object) iter.next();
            s3Service.putObject(bucket, object);
        }
        
        S3Object[] objects = null;
        
        // List all items in directory.
        objects = s3Service.listObjects(bucket);        
        assertEquals("Incorrect number of objects in directory structure", 7, objects.length);
        
        // List items in chunks of size 2, ensure we get a total of seven.
        int chunkedObjectsCount = 0;
        int chunkedIterationsCount = 0;
        String priorLastKey = null;
        do {
            S3ObjectsChunk chunk = s3Service.listObjectsChunked(
                bucket.getName(), null, null, 2, priorLastKey);
            priorLastKey = chunk.getPriorLastKey();
            chunkedObjectsCount += chunk.getObjects().length;
            chunkedIterationsCount++;
        } while (priorLastKey != null);
        assertEquals("Chunked bucket listing retreived incorrect number of objects", 7, chunkedObjectsCount);
        assertEquals("Chunked bucket listing ran for an unexpected number of iterations", 4, chunkedIterationsCount);
        
        // List the same items with a prefix.
        objects = s3Service.listObjects(bucket, "dir1", null);        
        assertEquals("Incorrect number of objects matching prefix", 7, objects.length);
        
        // List items up one directory with a prefix (will include dir1Level1)
        objects = s3Service.listObjects(bucket, "dir1/dir1Level1", null);        
        assertEquals("Incorrect number of objects matching prefix", 4, objects.length);

        // List items up one directory with a prefix (will not include dir1Level1)
        objects = s3Service.listObjects(bucket, "dir1/dir1Level1/", null);        
        assertEquals("Incorrect number of objects matching prefix", 3, objects.length);

        // Try a prefix matching no object keys.
        objects = s3Service.listObjects(bucket, "dir1-NonExistent", null);        
        assertEquals("Expected no results", 0, objects.length);

        // Use delimiter with an partial prefix. 
        objects = s3Service.listObjects(bucket, "dir", "/");        
        assertEquals("Expected no results", 1, objects.length);
        
        // Use delimiter to find item dir1 only.
        objects = s3Service.listObjects(bucket, "dir1", "/");        
        assertEquals("Incorrect number of objects matching prefix and delimiter", 1, objects.length);
        
        // Use delimiter to find items within dir1 only.
        objects = s3Service.listObjects(bucket, "dir1/", "/");        
        assertEquals("Incorrect number of objects matching prefix and delimiter", 3, objects.length);

        // List items up one directory with prefix and delimiter (will include only dir1Level1)
        objects = s3Service.listObjects(bucket, "dir1/dir1Level1", "/");        
        assertEquals("Incorrect number of objects matching prefix", 1, objects.length);

        // List items up one directory with prefix and delimiter (will include only contents of dir1Level1)
        objects = s3Service.listObjects(bucket, "dir1/dir1Level1/", "/");        
        assertEquals("Incorrect number of objects matching prefix", 2, objects.length);
        
        // Clean up.
        iter = objectsList.iterator();
        while (iter.hasNext()) {
            S3Object object = (S3Object) iter.next();
            s3Service.deleteObject(bucket, object.getKey());
        }
        s3Service.deleteBucket(bucket.getName());
    }

    public void testBucketLogging() throws Exception {
        S3Service s3Service = getS3Service(awsCredentials);

        String bucketName = awsCredentials.getAccessKey() + ".S3ServiceTest5";

        S3Bucket bucket = s3Service.createBucket(bucketName);
        
        // Check logging status is false
        S3BucketLoggingStatus loggingStatus = s3Service.getBucketLoggingStatus(bucket.getName());
        assertFalse("Expected logging to be disabled for bucket " + bucketName, 
            loggingStatus.isLoggingEnabled());
        
        // Enable logging (non-existent target bucket)
        try {
            S3BucketLoggingStatus newLoggingStatus = new S3BucketLoggingStatus(
                awsCredentials.getAccessKey() + ".NonExistentBucketName", "access-log-");
            s3Service.setBucketLoggingStatusImpl(bucket.getName(), newLoggingStatus);
            fail("Using non-existent target bucket should have caused an exception");            
        } catch (Exception e) {            
        }
        
        // Enable logging (in same bucket)
        S3BucketLoggingStatus newLoggingStatus = new S3BucketLoggingStatus(bucketName, "access-log-");
        s3Service.setBucketLoggingStatusImpl(bucket.getName(), newLoggingStatus);
        loggingStatus = s3Service.getBucketLoggingStatus(bucket.getName());
        assertTrue("Expected logging to be enabled for bucket " + bucketName, 
            loggingStatus.isLoggingEnabled());
        assertEquals("Target bucket", bucketName, loggingStatus.getTargetBucketName());
        assertEquals("Log file prefix", "access-log-", loggingStatus.getLogfilePrefix());
        
        // Disable logging
        newLoggingStatus = new S3BucketLoggingStatus();
        s3Service.setBucketLoggingStatusImpl(bucket.getName(), newLoggingStatus);
        loggingStatus = s3Service.getBucketLoggingStatus(bucket.getName());
        assertFalse("Expected logging to be disabled for bucket " + bucketName, 
            loggingStatus.isLoggingEnabled());

        s3Service.deleteBucket(bucket.getName());
    }

    private String readStringFromInputStream(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        int b = -1;
        while ((b = is.read()) != -1) {
            sb.append((char)b);
        }
        return sb.toString();
    }

}
