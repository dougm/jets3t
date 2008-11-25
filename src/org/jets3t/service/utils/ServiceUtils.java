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
package org.jets3t.service.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.Constants;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Object;

/**
 * General utility methods used throughout the jets3t project.
 * 
 * @author James Murty
 */
public class ServiceUtils {
    private static final Log log = LogFactory.getLog(ServiceUtils.class);

    protected static final SimpleDateFormat iso8601DateParser = new SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    protected static final SimpleDateFormat rfc822DateParser = new SimpleDateFormat(
        "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

    static {
        iso8601DateParser.setTimeZone(new SimpleTimeZone(0, "GMT"));
        rfc822DateParser.setTimeZone(new SimpleTimeZone(0, "GMT"));
    }

    public static Date parseIso8601Date(String dateString) throws ParseException {
        synchronized (iso8601DateParser) {
            return iso8601DateParser.parse(dateString);
        }
    }

    public static String formatIso8601Date(Date date) {
        synchronized (iso8601DateParser) {
            return iso8601DateParser.format(date);
        }
    }

    public static Date parseRfc822Date(String dateString) throws ParseException {
        synchronized (rfc822DateParser) {
            return rfc822DateParser.parse(dateString);
        }
    }

    public static String formatRfc822Date(Date date) {
        synchronized (rfc822DateParser) {
            return rfc822DateParser.format(date);
        }
    }

    /**
     * Calculate the HMAC/SHA1 on a string.
     * 
     * @param awsSecretKey
     * AWS secret key.
     * @param canonicalString
     * canonical string representing the request to sign.
     * @return Signature
     * @throws S3ServiceException
     */
    public static String signWithHmacSha1(String awsSecretKey, String canonicalString)
        throws S3ServiceException
    {
        if (awsSecretKey == null) {
        	if (log.isDebugEnabled()) {
        		log.debug("Canonical string will not be signed, as no AWS Secret Key was provided");
        	}
            return null;
        }
        
        // The following HMAC/SHA1 code for the signature is taken from the
        // AWS Platform's implementation of RFC2104 (amazon.webservices.common.Signature)
        //
        // Acquire an HMAC/SHA1 from the raw key bytes.
        SecretKeySpec signingKey = null;
        try {
            signingKey = new SecretKeySpec(awsSecretKey.getBytes(Constants.DEFAULT_ENCODING),
                Constants.HMAC_SHA1_ALGORITHM);
        } catch (UnsupportedEncodingException e) {
            throw new S3ServiceException("Unable to get bytes from secret string", e);
        }

        // Acquire the MAC instance and initialize with the signing key.
        Mac mac = null;
        try {
            mac = Mac.getInstance(Constants.HMAC_SHA1_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            // should not happen
            throw new RuntimeException("Could not find sha1 algorithm", e);
        }
        try {
            mac.init(signingKey);
        } catch (InvalidKeyException e) {
            // also should not happen
            throw new RuntimeException("Could not initialize the MAC algorithm", e);
        }

        // Compute the HMAC on the digest, and set it.
        try {
            byte[] b64 = Base64.encodeBase64(mac.doFinal(
                canonicalString.getBytes(Constants.DEFAULT_ENCODING))); 
            return new String(b64);
        } catch (UnsupportedEncodingException e) {
            throw new S3ServiceException("Unable to get bytes from canonical string", e);
        }
    }

    /**
     * Reads text data from an input stream and returns it as a String.
     * 
     * @param is
     * input stream from which text data is read.
     * @param encoding
     * the character encoding of the textual data in the input stream. If this
     * parameter is null, the default system encoding will be used.
     * 
     * @return
     * text data read from the input stream.
     * 
     * @throws IOException
     */
    public static String readInputStreamToString(InputStream is, String encoding) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        if (encoding != null) {
            br = new BufferedReader(new InputStreamReader(is, encoding));
        } else {
            br = new BufferedReader(new InputStreamReader(is));
        }
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (Exception e) {
        	if (log.isWarnEnabled()) {
        		log.warn("Unable to read String from Input Stream", e);
        	}
        }
        return sb.toString();
    }
    
    /**
     * Reads from an input stream until a newline character or the end of the stream is reached.
     * 
     * @param is
     * @return
     * text data read from the input stream, not including the newline character.
     * @throws IOException
     */
    public static String readInputStreamLineToString(InputStream is, String encoding) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b = -1;        
        while ((b = is.read()) != -1) {
            if ('\n' == (char) b) {
                break;
            } else {
                baos.write(b);
            }
        }
        return new String(baos.toByteArray(), encoding);
    }    
    
    /**
     * Counts the total number of bytes in a set of S3Objects by summing the
     * content length of each. 
     * 
     * @param objects
     * @return
     * total number of bytes in all S3Objects.
     */
    public static long countBytesInObjects(S3Object[] objects) {
        long byteTotal = 0;
        for (int i = 0; objects != null && i < objects.length; i++) {
            byteTotal += objects[i].getContentLength();
        }
        return byteTotal;
    }
            
    /**
     * From a map of metadata returned from a REST Get Object or Get Object Head request, returns a map
     * of metadata with the HTTP-connection-specific metadata items removed.    
     * 
     * @param metadata
     * @return
     * metadata map with HTTP-connection-specific items removed.
     */
    public static Map cleanRestMetadataMap(Map metadata) {
    	if (log.isDebugEnabled()) {
    		log.debug("Cleaning up REST metadata items");
    	}
        HashMap cleanMap = new HashMap();
        if (metadata != null) {
            Iterator metadataIter = metadata.entrySet().iterator();
            while (metadataIter.hasNext()) {
                Map.Entry entry = (Map.Entry) metadataIter.next();
                Object key = entry.getKey();
                Object value = entry.getValue();

                // Trim prefixes from keys.
                String keyStr = (key != null ? key.toString() : "");
                if (keyStr.startsWith(Constants.REST_METADATA_PREFIX)) {
                    key = keyStr
                        .substring(Constants.REST_METADATA_PREFIX.length(), keyStr.length());
                    if (log.isDebugEnabled()) {
	                    log.debug("Removed Amazon meatadata header prefix from key: " + keyStr
	                        + "=>" + key);
                    }
                } else if (keyStr.startsWith(Constants.REST_HEADER_PREFIX)) {
                    key = keyStr.substring(Constants.REST_HEADER_PREFIX.length(), keyStr.length());
                    if (log.isDebugEnabled()) {
                    	log.debug("Removed Amazon header prefix from key: " + keyStr + "=>" + key);
                    }
                } else if (RestUtils.HTTP_HEADER_METADATA_NAMES.contains(keyStr.toLowerCase(Locale.getDefault()))) {
                    key = keyStr;
                    if (log.isDebugEnabled()) {
                    	log.debug("Leaving HTTP header item unchanged: " + key + "=" + value);                    
                    }
                } else if ("ETag".equalsIgnoreCase(keyStr)
                    || "Date".equalsIgnoreCase(keyStr)
                    || "Last-Modified".equalsIgnoreCase(keyStr))
                {
                    key = keyStr;
                    if (log.isDebugEnabled()) {
                    	log.debug("Leaving header item unchanged: " + key + "=" + value);                    
                    }
                } else {
                	if (log.isDebugEnabled()) {
                		log.debug("Ignoring metadata item: " + keyStr + "=" + value);
                	}
                    continue;
                }

                // Convert connection header string Collections into simple strings (where
                // appropriate)
                if (value instanceof Collection) {
                    if (((Collection) value).size() == 1) {
                    	if (log.isDebugEnabled()) {
                    		log.debug("Converted metadata single-item Collection "
                				+ value.getClass() + " " + value + " for key: " + key);
                    	}
                        value = ((Collection) value).iterator().next();
                    } else {
                    	if (log.isWarnEnabled()) {
                    		log.warn("Collection " + value
                				+ " has too many items to convert to a single string");
                    	}
                    }
                }

                // Parse date strings into Date objects, if necessary.
                if ("Date".equals(key) || "Last-Modified".equals(key)) {
                    if (!(value instanceof Date)) {
                        try {
                        	if (log.isDebugEnabled()) {
                        		log.debug("Parsing date string '" + value
                                + "' into Date object for key: " + key);
                        	}
                            value = ServiceUtils.parseRfc822Date(value.toString());
                        } catch (ParseException pe) {
                        	if (log.isWarnEnabled()) {
                        		log.warn("Date string is not RFC 822 compliant for metadata field " + key, pe);
                        	}
                            continue;
                        }
                    }
                }

                cleanMap.put(key, value);
            }
        }
        return cleanMap;
    }
    
    /**
     * Converts byte data to a Hex-encoded string.
     * 
     * @param data
     * data to hex encode.
     * @return
     * hex-encoded string.
     */
    public static String toHex(byte[] data) {
        StringBuffer sb = new StringBuffer(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i]);
            if (hex.length() == 1) {
                // Append leading zero.
                sb.append("0");
            } else if (hex.length() == 8) {
                // Remove ff prefix from negative numbers.
                hex = hex.substring(6);
            }
            sb.append(hex);
        }
        return sb.toString().toLowerCase(Locale.getDefault());
    }
    
    /**
     * Converts a Hex-encoded data string to the original byte data.
     * 
     * @param hexData
     * hex-encoded data to decode.
     * @return
     * decoded data from the hex string.
     */
    public static byte[] fromHex(String hexData) {
        byte[] result = new byte[(hexData.length() + 1) / 2];
        String hexNumber = null;
        int stringOffset = 0;
        int byteOffset = 0;
        while (stringOffset < hexData.length()) {
            hexNumber = hexData.substring(stringOffset, stringOffset + 2);
            stringOffset += 2;
            result[byteOffset++] = (byte) Integer.parseInt(hexNumber, 16);
        }
        return result;
    }
    
    /**
     * Converts byte data to a Base64-encoded string.
     * 
     * @param data
     * data to Base64 encode.
     * @return
     * encoded Base64 string.
     */
    public static String toBase64(byte[] data) {
        byte[] b64 = Base64.encodeBase64(data); 
        return new String(b64);
    }

    /**
     * Joins a list of items into a delimiter-separated string. Each item
     * is converted to a string value with the toString() method before being
     * added to the final delimited list.
     * 
     * @param items
     * the items to include in a delimited string
     * @param delimiter
     * the delimiter character or string to insert between each item in the list
     * @return
     * a delimited string
     */
    public static String join(List items, String delimiter) {
        StringBuffer sb = new StringBuffer();        
        for (int i = 0; i < items.size(); i++) {
            sb.append(items.get(i));
            if (i < items.size() - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    /**
     * Joins a list of items into a delimiter-separated string. Each item
     * is converted to a string value with the toString() method before being
     * added to the final delimited list.
     * 
     * @param items
     * the items to include in a delimited string
     * @param delimiter
     * the delimiter character or string to insert between each item in the list
     * @return
     * a delimited string
     */
    public static String join(Object[] items, String delimiter) {
        StringBuffer sb = new StringBuffer();        
        for (int i = 0; i < items.length; i++) {
            sb.append(items[i]);
            if (i < items.length - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    /**
     * Converts a Base64-encoded string to the original byte data.
     * 
     * @param b64Data
     * a Base64-encoded string to decode. 
     * 
     * @return
     * bytes decoded from a Base64 string.
     */
    public static byte[] fromBase64(String b64Data) {
        byte[] decoded = Base64.decodeBase64(b64Data.getBytes());
        return decoded;
    }

    /**
     * Computes the MD5 hash of the data in the given input stream and returns it as a hex string.
     * 
     * @param is
     * @return
     * MD5 hash
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static byte[] computeMD5Hash(InputStream is) throws NoSuchAlgorithmException, IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[16384];
            int bytesRead = -1;
            while ((bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
                messageDigest.update(buffer, 0, bytesRead);
            }
            return messageDigest.digest();
        } finally {
            try {
                bis.close();
            } catch (Exception e) {
                System.err.println("Unable to close input stream of hash candidate: " + e);
            }
        }
    }

    /**
     * Computes the MD5 hash of the given data and returns it as a hex string.
     * 
     * @param data
     * @return
     * MD5 hash.
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static byte[] computeMD5Hash(byte[] data) throws NoSuchAlgorithmException, IOException {
        return computeMD5Hash(new ByteArrayInputStream(data));
    }    
    
    /**
     * Identifies the name of a bucket from a given host name, if available. 
     * Returns null if the bucket name cannot be identified, as might happen
     * when a bucket name is represented by the path component of a URL instead 
     * of the host name component.
     * 
     * @param host
     * the host name component of a URL that may include the bucket name, 
     * if an alternative host name is in use.
     * 
     * @return
     * The S3 bucket name represented by the DNS host name, or null if none.
     */
    public static String findBucketNameInHostname(String host) {
        String bucketName = null;
        // Bucket name is available in URL's host name.
        if (host.endsWith(Constants.S3_HOSTNAME)) {
            // Bucket name is available as S3 subdomain
            bucketName = host.substring(0, 
                host.length() - Constants.S3_HOSTNAME.length() - 1);
        } else {
            // URL refers to a virtual host name
            bucketName = host;
        }        
        return bucketName;
    }
    
    /**
     * Builds an object based on the bucket name and object key information 
     * available in the components of a URL. 
     * 
     * @param host
     * the host name component of a URL that may include the bucket name, 
     * if an alternative host name is in use.
     * @param urlPath
     * the path of a URL that references an S3 object, and which may or may 
     * not include the bucket name.
     * 
     * @return
     * the object referred to by the URL components.
     */
    public static S3Object buildObjectFromUrl(String host, String urlPath) throws UnsupportedEncodingException {
        if (urlPath.startsWith("/")) {
            urlPath = urlPath.substring(1); // Ignore first '/' character in url path.
        }
        
        String bucketName = null;
        String objectKey = null;
        
        if (!Constants.S3_HOSTNAME.equals(host)) {
            bucketName = findBucketNameInHostname(host);            
        } else {
            // Bucket name must be first component of URL path
            int slashIndex = urlPath.indexOf("/");
            bucketName = URLDecoder.decode(
                urlPath.substring(0, slashIndex), Constants.DEFAULT_ENCODING);
            
            // Remove the bucket name component of the host name
            urlPath = urlPath.substring(bucketName.length() + 1);                        
        }

        objectKey = URLDecoder.decode( 
            urlPath, Constants.DEFAULT_ENCODING);            

        S3Object object = new S3Object(objectKey);
        object.setBucketName(bucketName);
        return object;
    }

    /**
     * Returns a user agent string describing the jets3t library, and optionally the application
     * using it, to server-side services.
     * 
     * @param applicationDescription
     * a description of the application using the jets3t toolkit, included at the end of the
     * user agent string. This value may be null. 
     * @return
     * a string built with the following components (some elements may not be available): 
     * <tt>jets3t/</tt><i>{@link S3Service#VERSION_NO__JETS3T_TOOLKIT}</i> 
     * (<i>os.name</i>/<i>os.version</i>; <i>os.arch</i>; <i>user.region</i>; 
     * <i>user.region</i>; <i>user.language</i>) <i>applicationDescription</i></tt>
     * 
     */
    public static String getUserAgentDescription(String applicationDescription) {
        return         
            "JetS3t/" + S3Service.VERSION_NO__JETS3T_TOOLKIT + " ("
            + System.getProperty("os.name") + "/" 
            + System.getProperty("os.version") + ";"
            + " " + System.getProperty("os.arch")
            + (System.getProperty("user.region") != null 
                ? "; " + System.getProperty("user.region")
                : "")
            + (System.getProperty("user.language") != null
                ? "; " + System.getProperty("user.language")
                : "")
            + (System.getProperty("java.version") != null
                ? "; JVM " + System.getProperty("java.version") 
                : "")                
            + ")"
            + (applicationDescription != null
                ? " " + applicationDescription 
                : "");
    }
        
}
