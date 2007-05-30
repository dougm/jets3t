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

    private static AWSCredentials loadAWSCredentials() throws Exception {
        InputStream propertiesIS = 
            ClassLoader.getSystemResourceAsStream(TEST_PROPERTIES_FILENAME);
        
        if (propertiesIS == null) {
            throw new IOException("Unable to load test properties file from classpath: " 
                + TEST_PROPERTIES_FILENAME);
        }
        
        Properties testProperties = new Properties();        
        testProperties.load(propertiesIS);
        return new AWSCredentials(
            testProperties.getProperty("aws.accesskey"),
            testProperties.getProperty("aws.secretkey"));
    }

    private static String generateAuthorizationString(String url, Map headersMap) throws Exception {
        String canonicalString = RestUtils.makeCanonicalString("PUT", url, 
            headersMap, null);

        // Sign the canonical string.
        AWSCredentials awsCredentials = loadAWSCredentials();
        String signedCanonical = ServiceUtils.signWithHmacSha1(
            awsCredentials.getSecretKey(), canonicalString);

        return "AWS " + awsCredentials.getAccessKey() + ":" + signedCanonical;
    }

    public static void main(String[] args) throws Exception {
        
        String filename = "/Users/jmurty/temp/Maildir.backup.tar"; // "system.sparseimage";
        String bucketName = "1FMFX9QNQHMZ32MPA7G2.Test";
        String contentType = "application/octet-stream";
        String serverHostname = "s3.amazonaws.com";         
        int port = 443;
        
        File file = new File(filename);
        String url = "/" + bucketName + "/" + file.getName();

        System.out.println("Computing MD5 hash of file: " + file.getName());
        long fileSize = file.length();
        byte[] md5Hash = ServiceUtils.computeMD5Hash(
            new BufferedInputStream(new FileInputStream(file)));
        System.out.println("MD5 hash of file B64=" + ServiceUtils.toBase64(md5Hash)
            + " Hex=" + ServiceUtils.toHex(md5Hash));
        
        SocketFactory socketFactory = SSLSocketFactory.getDefault();

        System.out.println("Connecting to " + serverHostname + ":" + port);
        Socket socket = socketFactory.createSocket(serverHostname, port);
        
        OutputStream out = new BufferedOutputStream(socket.getOutputStream(), 2048);
        InputStream in = socket.getInputStream();
        
        Map headersMap = new HashMap();
        headersMap.put("Content-MD5", ServiceUtils.toBase64(md5Hash));
        headersMap.put("Content-Type", contentType);
        headersMap.put("Date", ServiceUtils.formatRfc822Date(new Date()));
        String headers = 
            "PUT " + url + " HTTP/1.1\r\n" +
            "Content-Length: " + fileSize + "\r\n" +
            "Content-MD5: " + headersMap.get("Content-MD5") + "\r\n" +
            "Content-Type: " + headersMap.get("Content-Type")  + "\r\n" +
            "Date: " + headersMap.get("Date") + "\r\n" + 
            "Authorization: " + generateAuthorizationString(url,headersMap) + "\r\n" +                
            "Host: " + serverHostname + "\r\n" +
            "\r\n";
        
        out.write(headers.getBytes());
        
        FileInputStream fis = new FileInputStream(file);
        long fileBytesTransferred = 0;
        
        byte[] data = new byte[2048];
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
