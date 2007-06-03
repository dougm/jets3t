package org.jets3t.tests;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.utils.RestUtils;
import org.jets3t.service.utils.ServiceUtils;

/**
 * Very basic client implementation able to PUT files into S3 using the Java
 * socket implementation directly, with no HTTP library or transport of any kind.
 * <p>
 * To use this class:
 * <ul>
 * <li>provide a <tt>test.properties</tt> file in the classpath
 * that contains the settings <tt>aws.accesskey</tt> and <tt>aws.secretkey<tt></li>
 * <li>modify the <tt>filename</tt> and <tt>bucketName</tt> variables in main() to
 * provide the file you wish to upload, and the <b>already existing</b> buckety you
 * want to upload it to in S3</li>
 * </ul>
 * 
 * @author James Murty
 */
public class PutViaSocket {
    private static String TEST_PROPERTIES_FILENAME = "test.properties";

    private static Properties loadTestProperties() throws IOException {
        InputStream propertiesIS = 
            ClassLoader.getSystemResourceAsStream(TEST_PROPERTIES_FILENAME);
        
        if (propertiesIS == null) {
            throw new IOException("Unable to load test properties file from classpath: " 
                + TEST_PROPERTIES_FILENAME);
        }
        
        Properties testProperties = new Properties();        
        testProperties.load(propertiesIS);        
        return testProperties;
    }
    
    private static AWSCredentials loadAWSCredentials(Properties testProperties) throws Exception {
        return new AWSCredentials(
            testProperties.getProperty("aws.accesskey"),
            testProperties.getProperty("aws.secretkey"));
    }

    private static String generateAuthorizationString(AWSCredentials awsCredentials, 
        String url, Map headersMap) throws Exception 
    {
        String canonicalString = RestUtils.makeCanonicalString("PUT", url, 
            headersMap, null);

        // Sign the canonical string.
        String signedCanonical = ServiceUtils.signWithHmacSha1(
            awsCredentials.getSecretKey(), canonicalString);

        return "AWS " + awsCredentials.getAccessKey() + ":" + signedCanonical;
    }

    public static void main(String[] args) throws Exception {
        Properties testProperties = loadTestProperties();
        
        AWSCredentials awsCredentials = loadAWSCredentials(testProperties);
        
        String filename = testProperties.getProperty("filename");        
        String bucketName = testProperties.getProperty("bucketName");
        String contentType = testProperties.getProperty("contentType", "application/octet-stream");
        String serverHostname = testProperties.getProperty("serverHostname", "s3.amazonaws.com");        
        String bufferSizeStr = testProperties.getProperty("bufferSize", "2048");
        int byteBufferSize = Integer.parseInt(bufferSizeStr);

        int port = 80;
        boolean isEnableSSL;
        String enableSslStr = testProperties.getProperty("enableSSL", "false");
        if ("true".equalsIgnoreCase(enableSslStr)) {
            isEnableSSL = true;
            port = 443;
        } else if ("false".equalsIgnoreCase(enableSslStr)) {
            isEnableSSL = false;
        } else {
            throw new IllegalArgumentException("Boolean value '" + enableSslStr 
                + "' for property 'enableSSL' must be 'true' or 'false' (case-insensitive)");
        }
        
        // Over-ride default server ports (80, 443) if a special port is configured.
        String serverPortStr = testProperties.getProperty("serverPort", null);
        if (serverPortStr != null) {
            port = Integer.parseInt(serverPortStr);
        }
        
        boolean isS3AuthEnabled;
        String disableS3FeaturesStr = testProperties.getProperty("disableS3Features", "false");
        if ("true".equalsIgnoreCase(disableS3FeaturesStr)) {
            isS3AuthEnabled = false;
        } else if ("false".equalsIgnoreCase(disableS3FeaturesStr)) {
            isS3AuthEnabled = true;
        } else {
            throw new IllegalArgumentException("Boolean value '" + disableS3FeaturesStr 
                + "' for property 'disableS3Features' must be 'true' or 'false' (case-insensitive)");
        }
        
        System.out.println("AWS Access Key: " + awsCredentials.getAccessKey());
        System.out.println("filename: " + filename);
        System.out.println("bucketName: " + bucketName);
        System.out.println("contentType: " + contentType);
        System.out.println("serverHostname: " + serverHostname);
        System.out.println("serverPort: " + port);
        System.out.println("bufferSize: " + byteBufferSize);
        System.out.println("enableSSL? " + isEnableSSL);
        System.out.println("isS3AuthEnabled? " + isS3AuthEnabled);
        
        File file = new File(filename);
        String url = "/" + bucketName + "/" + file.getName();

        System.out.println("\nComputing MD5 hash of file: " + file.getName());
        long fileSize = file.length();
        byte[] md5Hash = ServiceUtils.computeMD5Hash(
            new BufferedInputStream(new FileInputStream(file)));
        System.out.println("MD5 hash of file B64=" + ServiceUtils.toBase64(md5Hash)
            + " Hex=" + ServiceUtils.toHex(md5Hash));
        
        SocketFactory socketFactory = null;
        if (isEnableSSL) {
            socketFactory = SSLSocketFactory.getDefault();
        } else {
            socketFactory = SocketFactory.getDefault();
        }

        System.out.println("Connecting to " + serverHostname + ":" + port);
        Socket socket = socketFactory.createSocket(serverHostname, port);
        
        socket.setKeepAlive(true);
        socket.setSoTimeout(60000);
        socket.setTcpNoDelay(true);
        
        OutputStream out = new BufferedOutputStream(socket.getOutputStream(), byteBufferSize);
        InputStream in = socket.getInputStream();
        
        Map headersMap = new HashMap();
        headersMap.put("Content-MD5", ServiceUtils.toBase64(md5Hash));
        headersMap.put("Content-Type", contentType);
        headersMap.put("Date", ServiceUtils.formatRfc822Date(new Date()));
        headersMap.put("S3Authorization", generateAuthorizationString(awsCredentials, url,headersMap));
                
        String headers = 
            "PUT " + url + " HTTP/1.1\r\n" +
            "Content-Length: " + fileSize + "\r\n" +
            "Content-MD5: " + headersMap.get("Content-MD5") + "\r\n" +
            "Content-Type: " + headersMap.get("Content-Type")  + "\r\n" +
            "Date: " + headersMap.get("Date") + "\r\n" +
            (isS3AuthEnabled
                ? "Authorization: " + headersMap.get("S3Authorization") + "\r\n" 
                : "") +                
            "Host: " + serverHostname + "\r\n" +
            "\r\n";
        
        out.write(headers.getBytes());
        
        FileInputStream fis = new FileInputStream(file);
        long fileBytesTransferred = 0;
        
        byte[] data = new byte[byteBufferSize];
        int dataRead = 0;

        int failureCount = 0;
        int MAX_FAILURE_RETRIES = 10;
        
        // PUT Data
        System.out.println("Uploading " + fileSize + " bytes");
        while ((dataRead = fis.read(data)) != -1) {
            try {
                out.write(data, 0, dataRead);
                fileBytesTransferred += dataRead;
                if (fileBytesTransferred % (1024 * 1024) == 0) {
                    System.out.println("Uploaded " 
                        + (fileBytesTransferred / (double)(1024 * 1024)) + "MB of "
                        + (fileSize / (double)(1024 * 1024)) + "MB");
                }            
                out.flush();
            } catch (Exception e) {
                // Try to recover from the failure (it's unlikely this will ever work)
                failureCount++;
                if (failureCount <= MAX_FAILURE_RETRIES) {
                    System.out.println("SocketException " + failureCount + ", will retry: " + e);
                    Thread.sleep(500);
                } else {
                    break;
                }
            }
        }
        fis.close();
        
        if (fileBytesTransferred < fileSize) {
            System.out.println("Upload did not complete, only " + fileBytesTransferred + " of "
                + fileSize + " bytes sent");                        
        } else {
            System.out.println("Upload completed");            
        }

        // Read response
        System.out.println("\nRESPONSE:");
        while ((dataRead = in.read(data)) != -1) {
            String line = new String(data, 0, dataRead);
            System.out.print(line);
            if (line.endsWith("\r\n\r\n")) {
                break;
            }
        }
        
        in.close();
        out.close();
        socket.close();
    }
}
