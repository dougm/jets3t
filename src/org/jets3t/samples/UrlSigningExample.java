package org.jets3t.samples;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

import org.jets3t.service.S3Service;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.utils.signedurl.SignedUrlHandler;

/**
 * Demonstrates how to create and use Signed URLs.
 */
public class UrlSigningExample {
    
    /*
     * TODO: Change the name of this bucket to a bucket of your own.
     */
    private static final String myBucketName = "Test";

    public static void main(String[] args) throws Exception {
        // Initialise a SignedUrlHandler, which is an interface implemented by classes able to 
        // perform operations in S3 using signed URLs (no AWS Credentials required).
        // The RestS3Service provides an implementation of this interface in JetS3t. 
        SignedUrlHandler signedUrlHandler = new RestS3Service(null);

        // Create a bucket to test reading and writing to        
        S3Bucket bucket = new S3Bucket(myBucketName);
        
        // Create an object to use for testing.
        S3Object object = new S3Object(bucket, "urlSigningTestObject.txt", "Hello World!");
        
        // Determine what the time will be in 5 minutes - our signed URLs will be valid for 5 minutes only.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 5);
        Date expiryDate = cal.getTime();

        /*
         * Generate the signed URL strings for PUT, GET, HEAD and DELETE operations, using the
         * AWS Credentials in the samples.properties file. 
         */
        AWSCredentials awsCredentials = SamplesUtils.loadAWSCredentials();
        
        // Create a signed HTTP PUT URL valid for 5 minutes.        
        String putUrl = S3Service.createSignedPutUrl(bucket.getName(), object.getKey(), 
            object.getMetadataMap(), awsCredentials, expiryDate);
        
        // Create a signed HTTP GET URL valid for 5 minutes.
        String getUrl = S3Service.createSignedGetUrl(bucket.getName(), object.getKey(),  
            awsCredentials, expiryDate);

        // Create a signed HTTP HEAD URL valid for 5 minutes.
        String headUrl = S3Service.createSignedHeadUrl(bucket.getName(), object.getKey(),
            awsCredentials, expiryDate);

        // Create a signed HTTP DELETE URL valid for 5 minutes.
        String deleteUrl = S3Service.createSignedDeleteUrl(bucket.getName(), object.getKey(),
            awsCredentials, expiryDate);
        
        System.out.println("Signed PUT URL: " + putUrl);
        System.out.println("Signed GET URL: " + getUrl);
        System.out.println("Signed HEAD URL: " + headUrl);
        System.out.println("Signed DELETE URL: " + deleteUrl);

        System.out.println("Performing PUT with signed URL");
        S3Object putObject = signedUrlHandler.putObjectWithSignedUrl(putUrl, object);
        System.out.println("  Object has been uploaded to S3: " + putObject.getKey());

        System.out.println("Performing HEAD with signed URL");        
        S3Object headObject = signedUrlHandler.getObjectDetailsWithSignedUrl(headUrl);
        System.out.println("  Size of object in S3: " + headObject.getContentLength());

        System.out.println("Performing GET with signed URL");
        S3Object getObject = signedUrlHandler.getObjectWithSignedUrl(getUrl);
        String contentData = (new BufferedReader(
            new InputStreamReader(getObject.getDataInputStream()))).readLine();
        System.out.println("  Content of object in S3: " + contentData);
        
        System.out.println("Performing DELETE with signed URL");
        signedUrlHandler.deleteObjectWithSignedUrl(deleteUrl);
        System.out.println("  Object deleted - the example is finished");
    }

}
