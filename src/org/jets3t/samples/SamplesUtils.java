package org.jets3t.samples;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jets3t.service.security.AWSCredentials;

public class SamplesUtils {
    
    public static final String SAMPLES_PROPERTIES_NAME = "samples.properties";
    public static final String AWS_ACCESS_KEY_PROPERTY_NAME = "awsAccessKey";
    public static final String AWS_SECRET_KEY_PROPERTY_NAME = "awsSecretKey";
    
    /**
     * Loads AWS Credentials from the file <tt>test.properties</tt> that must be available in the  
     * classpath, and must contain settings {@link #AWS_ACCESS_KEY_PROPERTY_NAME} and 
     * {@link #AWS_SECRET_KEY_PROPERTY_NAME}.
     * 
     * @return
     */
    public static AWSCredentials loadAWSCredentials() throws IOException {
        InputStream propertiesIS = 
            ClassLoader.getSystemResourceAsStream(SAMPLES_PROPERTIES_NAME);
        
        if (propertiesIS == null) {
            throw new RuntimeException("Unable to load test properties file from classpath: " 
                + SAMPLES_PROPERTIES_NAME);
        }
        
        Properties testProperties = new Properties();        
        testProperties.load(propertiesIS);
        
        if (!testProperties.containsKey(AWS_ACCESS_KEY_PROPERTY_NAME)) {
            throw new RuntimeException(
                "Properties file 'test.properties' does not contain required property: " + AWS_ACCESS_KEY_PROPERTY_NAME); 
        }        
        if (!testProperties.containsKey(AWS_SECRET_KEY_PROPERTY_NAME)) {
            throw new RuntimeException(
                "Properties file 'test.properties' does not contain required property: " + AWS_SECRET_KEY_PROPERTY_NAME); 
        }
        
        AWSCredentials awsCredentials = new AWSCredentials(
            testProperties.getProperty(AWS_ACCESS_KEY_PROPERTY_NAME),
            testProperties.getProperty(AWS_SECRET_KEY_PROPERTY_NAME));
        
        return awsCredentials;        
    }

}
