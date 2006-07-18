package org.jets3t.tests;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

public class HttpClientRestS3ServiceTest extends BaseS3ServiceTest {
    
    public HttpClientRestS3ServiceTest() throws IOException {
        super();
    }

    protected S3Service getS3Service(AWSCredentials awsCredentials) throws S3ServiceException {
        return new RestS3Service(awsCredentials);
    }
    
    public void testRestCannedACL() throws Exception {
        S3Service s3Service = getS3Service(awsCredentials);
        
        // Create test bucket.
        String bucketName = awsCredentials.getAccessKey() + ".HttpClientRestS3ServiceTest";
        S3Bucket bucket = s3Service.createBucket(bucketName);
        
        // Try to create REST canned public object.        
        String publicKey = "PublicObject";
        S3Object object = new S3Object();
        object.setKey(publicKey);
        object.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
        object.setOwner(bucket.getOwner());
        
        try {
            s3Service.putObject(bucket, object);
            URL url = new URL("http://s3.amazonaws.com/" + bucketName + "/" + publicKey);      
            assertEquals("Expected public access (200)", 
                    200, ((HttpURLConnection)url.openConnection()).getResponseCode());
        } finally {
            s3Service.deleteObject(bucket, object.getKey());
            s3Service.deleteBucket(bucket.getName());
        }
    }

}
