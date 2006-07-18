package org.jets3t.tests;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.impl.soap.axis.SoapS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

public class AxisSoapS3ServiceTest extends BaseS3ServiceTest {
    
    public AxisSoapS3ServiceTest() throws IOException {
        super();
    }

    protected S3Service getS3Service(AWSCredentials awsCredentials) throws S3ServiceException {
        return new SoapS3Service(awsCredentials);
    }
    
    public void testIllegalACL() throws Exception {
        S3Service s3Service = getS3Service(awsCredentials);
        
        // Create test bucket.
        String bucketName = awsCredentials.getAccessKey() + ".AxisSoapS3ServiceTest";
        S3Bucket bucket = s3Service.createBucket(bucketName);
        
        // Try to create illegal REST canned public object.        
        String publicKey = "PublicObject";
        S3Object object = new S3Object();
        object.setKey(publicKey);
        object.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
        
        try {
            s3Service.putObject(bucket, object);
            fail("Shouldn't be able to use REST canned ACL setting with SOAP service");
        } finally {
            s3Service.deleteBucket(bucket.getName());
        }
    }

}
