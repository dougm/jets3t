package org.jets3t.samples;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.jets3t.service.Constants;
import org.jets3t.service.S3Service;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.utils.ServiceUtils;

/**
 * Demonstrates how Cockpit stores Passphrase-based login credentials in S3.
 * 
 * <b><font color="red">WARNING</font></b>: Do not run this class without changing the
 * default values for passphrase and password or you risk making your real AWS Credentials
 * available online with a publicly known Passphrase and Password!
 *  
 * @author James Murty
 */
public class StorePassphraseBasedLogin {
    
    /*
     * TODO: CHANGE THESE VALUES BEFORE TESTING, TO AVOID PUTTING YOUR CREDENTIALS 
     * IN S3 IN AN OBVIOUS PLACE! 
     */
    private static final String passphrase = "Example passphrase";
    private static final String password = "password";

    public static void main(String[] args) throws Exception {
        AWSCredentials awsCredentials = SamplesUtils.loadAWSCredentials();
        
        String combinedPassphraseAndPassword = passphrase + password;
        
        // Generate the S3 bucket name based on the passphrase hash.
        String bucketName = "jets3t-" + ServiceUtils.toHex(
            ServiceUtils.computeMD5Hash(passphrase.getBytes(Constants.DEFAULT_ENCODING)));

        // Generate the S3 object name based on the combined passphrase & password hash.
        String credentialObjectName = ServiceUtils.toHex(
            ServiceUtils.computeMD5Hash(combinedPassphraseAndPassword.getBytes(Constants.DEFAULT_ENCODING)))
            + "/jets3t.credentials";
                
        S3Bucket bucket = new S3Bucket(bucketName);
        System.out.println("bucketName=" + bucketName);
        System.out.println("credentialObjectName=" + credentialObjectName);
        
        /*
         * Store credentials.
         */
        
        // Initialise an S3 Service that knows the AWS credentials.
        S3Service s3Service = new RestS3Service(awsCredentials);
        
        // Encrypt credentials into InputStream 
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        awsCredentials.save(password, baos); 
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        
        // Create the target bucket
        bucket = s3Service.createBucket(bucketName);

        // Upload credentials object, which must be publicly readable.
        S3Object credsObject = new S3Object(credentialObjectName);        
        credsObject.setDataInputStream(bais);
        credsObject.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
        s3Service.putObject(bucket, credsObject);

        /*
         * Retrieve credentials.
         */ 
        
        // Initialise an S3 Service that does not know the AWS credentials.
        s3Service = new RestS3Service(null);
        
        // Check whether the passphrase-based bucket exists and is accessible.
        System.out.println("Is bucket accessible? " + s3Service.isBucketAccessible(bucketName));
        
        // Download the encrypted credentials object.
        S3Object retrievedCredsObject = s3Service.getObject(bucket, credentialObjectName);
        
        // Decrypt the credentials object.
        AWSCredentials retrievedCreds = AWSCredentials.load(password, 
            new BufferedInputStream(retrievedCredsObject.getDataInputStream()));
        
        System.out.println("Retrieved credentials from S3: " 
            + retrievedCreds.getAccessKey() + " : " + retrievedCreds.getSecretKey());
    }
    
}
