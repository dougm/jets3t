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
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.Constants;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Object;

/**
 * General utility methods used throughout the jets3t project.
 * 
 * @author James Murty
 */
public class ServiceUtils {
    private static final Log log = LogFactory.getLog(ServiceUtils.class);

    protected static SimpleDateFormat iso8601DateParser = new SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    protected static SimpleDateFormat rfc822DateParser = new SimpleDateFormat(
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
            log.debug("Canonical string will not be signed, as no AWS Secret Key was provided");
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
        byte[] b64 = Base64.encodeBase64(mac.doFinal(canonicalString.getBytes())); 
        return new String(b64);
    }

    /**
     * Reads text data from an input stream and returns it as a String.
     * 
     * @param is
     * @return
     * @throws IOException
     */
    public static String readInputStreamToString(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (Exception e) {
            log.warn("Unable to read String from Input Stream", e);
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
        log.debug("Cleaning up REST metadata items");
        HashMap cleanMap = new HashMap();
        if (metadata != null) {
            Iterator keysIter = metadata.keySet().iterator();
            while (keysIter.hasNext()) {
                Object key = keysIter.next();
                Object value = metadata.get(key);

                // Trim prefixes from keys.
                String keyStr = (key != null ? key.toString() : "");
                if (keyStr.startsWith(Constants.REST_METADATA_PREFIX)) {
                    key = keyStr
                        .substring(Constants.REST_METADATA_PREFIX.length(), keyStr.length());
                    log.debug("Removed Amazon meatadata header prefix from key: " + keyStr
                        + "=>" + key);
                } else if (keyStr.startsWith(Constants.REST_HEADER_PREFIX)) {
                    key = keyStr.substring(Constants.REST_HEADER_PREFIX.length(), keyStr.length());
                    log.debug("Removed Amazon header prefix from key: " + keyStr + "=>" + key);
                }

                // Convert connection header string Collections into simple strings (where
                // appropriate)
                if (value instanceof Collection) {
                    if (((Collection) value).size() == 1) {
                        log.debug("Converted metadata single-item Collection "
                            + value.getClass() + " " + value + " for key: " + key);
                        value = ((Collection) value).iterator().next();
                    } else {
                        log.warn("Collection " + value
                            + " has too many items to convert to a single string");
                    }
                }

                // Parse dates.
                if ("Date".equals(key) || "Last-Modified".equals(key)) {
                    try {
                        log.debug("Parsing date string '" + value
                            + "' into Date object for key: " + key);
                        value = parseRfc822Date(value.toString());
                    } catch (ParseException pe) {
                        log.warn("Unable to parse S3 date for metadata field " + key, pe);
                        value = null;
                    }
                }

                // Ignore/remove x-amz-id-2 and x-amz-request-id AWS debugging headers.
                if ("id-2".equals(key) || "request-id".equals(key)) {
                    log.debug("Ignoring AWS debugging header: " + key + "=" + value);
                } else {
                    cleanMap.put(key, value);
                }
            }
        }
        return cleanMap;
    }
    
    /**
     * Converts byte data to a Hex-encoded string.
     * 
     * @param data
     * @return
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
        return sb.toString().toLowerCase();
    }
    
    /**
     * Converts a Hex-encoded data string to the original byte data.
     * 
     * @param hexData
     * @return
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
     * @return
     */
    public static String toBase64(byte[] data) {
        byte[] b64 = Base64.encodeBase64(data); 
        return new String(b64);
    }
    
    /**
     * Converts a Base64-encoded string to the original byte data.
     * 
     * @param b64Data
     * @return
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
    
}
