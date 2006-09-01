package org.jets3t.tests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jets3t.service.S3Service;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.impl.soap.axis.SoapS3Service;
import org.jets3t.service.io.GZipDeflatingInputStream;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

import junit.framework.TestCase;

public class TempTest extends TestCase {
    protected String TEST_PROPERTIES_FILENAME = "test.properties";

    public void testPrefixAndDelimiter() throws Exception {
        InputStream propertiesIS = 
            ClassLoader.getSystemResourceAsStream(TEST_PROPERTIES_FILENAME);
        
        if (propertiesIS == null) {
            throw new IOException("Unable to load test properties file from classpath: " 
                + TEST_PROPERTIES_FILENAME);
        }
        
        Properties testProperties = new Properties();        
        testProperties.load(propertiesIS);
        AWSCredentials awsCredentials = new AWSCredentials(
            testProperties.getProperty("aws.accesskey"),
            testProperties.getProperty("aws.secretkey"));

        S3Service service = new SoapS3Service(awsCredentials);
        
        S3Bucket bucket = new S3Bucket(awsCredentials.getAccessKey() + ".NewBucket");
        
        String testData = "<p><h1>Testing SOAP</h1>This is some test text data...</p>";
        
        S3Object object = new S3Object("ContentEncodingTest.html");
//        object.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
        object.setDataInputStream(new GZipDeflatingInputStream(new ByteArrayInputStream(testData.getBytes())));
        // object.setDataInputStream(new ByteArrayInputStream(testData.getBytes()));
        object.setContentType("text/html");
        object.addMetadata("Content-Encoding", "gzip");
        
        object = service.putObject(bucket, object);
    }
    
}
