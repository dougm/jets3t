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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jets3t.service.Constants;
import org.jets3t.service.S3ServiceException;

/**
 * Utilities useful for REST/HTTP S3Service implementations.
 * 
 * @author James Murty
 */
public class RestUtils {

    /**
     * Encodes a URL string, and ensures that spaces are encoded as "%20" instead of "+" to keep
     * fussy web browsers happier.
     * 
     * @param path
     * @return
     * encoded URL.
     * @throws S3ServiceException
     */
    public static String encodeUrlString(String path) throws S3ServiceException {
        try {
            String encodedPath = URLEncoder.encode(path, Constants.DEFAULT_ENCODING);
            // Web browsers do not always handle '+' characters well, use the well-supported '%20' instead.
            encodedPath = encodedPath.replaceAll("\\+", "%20");            
            return encodedPath;
        } catch (UnsupportedEncodingException uee) {
            throw new S3ServiceException("Unable to encode path: " + path, uee);
        }
    }

    /**
     * Encodes a URL string but leaves a delimiter string unencoded.
     * Spaces are encoded as "%20" instead of "+".
     * 
     * @param path
     * @param delimiter
     * @return
     * encoded URL string.
     * @throws S3ServiceException
     */
    public static String encodeUrlPath(String path, String delimiter) throws S3ServiceException {
        StringBuffer result = new StringBuffer();
        String tokens[] = path.split(delimiter);
        for (int i = 0; i < tokens.length; i++) {
            result.append(encodeUrlString(tokens[i]));
            if (i < tokens.length - 1) {
                result.append(delimiter);
            }
        }
        return result.toString();
    }
    
    /**
     * Calculate the canonical string.  When expires is non-null, it will be
     * used instead of the Date header.
     * 
     * (c) 2006 Amazon Digital Services, Inc. or its affiliates.
     */
    public static String makeCanonicalString(String method, String resource, Map headersMap, String expires)
    {
        StringBuffer buf = new StringBuffer();
        buf.append(method + "\n");

        // Add all interesting headers to a list, then sort them.  "Interesting"
        // is defined as Content-MD5, Content-Type, Date, and x-amz-
        SortedMap interestingHeaders = new TreeMap();
        if (headersMap != null && headersMap.size() > 0) {
            Iterator headerIter = headersMap.keySet().iterator();
            while (headerIter.hasNext()) {
                Object key = headerIter.next();
                if (key == null) continue;                
                String lk = key.toString().toLowerCase();

                // Ignore any headers that are not particularly interesting.
                if (lk.equals("content-type") || lk.equals("content-md5") || lk.equals("date") ||
                    lk.startsWith(Constants.REST_HEADER_PREFIX))
                {                        
                    interestingHeaders.put(lk, headersMap.get(key));
                }
            }
        }

        if (interestingHeaders.containsKey(Constants.REST_METADATA_ALTERNATE_DATE)) {
            interestingHeaders.put("date", "");
        }

        // if the expires is non-null, use that for the date field.  this
        // trumps the x-amz-date behavior.
        if (expires != null) {
            interestingHeaders.put("date", expires);
        }

        // these headers require that we still put a new line in after them,
        // even if they don't exist.
        if (! interestingHeaders.containsKey("content-type")) {
            interestingHeaders.put("content-type", "");
        }
        if (! interestingHeaders.containsKey("content-md5")) {
            interestingHeaders.put("content-md5", "");
        }

        // Finally, add all the interesting headers (i.e.: all that startwith x-amz- ;-))
        for (Iterator i = interestingHeaders.keySet().iterator(); i.hasNext(); ) {
            String key = (String)i.next();
            if (key.startsWith(Constants.REST_HEADER_PREFIX)) {
                buf.append(key).append(':').append(interestingHeaders.get(key));
            } else {
                buf.append(interestingHeaders.get(key));
            }
            buf.append("\n");
        }

        // don't include the query parameters...
        int queryIndex = resource.indexOf('?');
        if (queryIndex == -1) {
            buf.append(resource);
        } else {
            buf.append(resource.substring(0, queryIndex));
        }

        // ...unless there is an acl, torrent or logging parameter
        if (resource.matches(".*[&?]acl($|=|&).*")) {
            buf.append("?acl");
        } else if (resource.matches(".*[&?]torrent($|=|&).*")) {
            buf.append("?torrent");
        } else if (resource.matches(".*[&?]logging($|=|&).*")) {
            buf.append("?logging");
        }

        return buf.toString();
    }
    
    /**
     * Renames metadata property names to be suitable for use as HTTP Headers. This is done
     * by renaming any non-HTTP headers to have the prefix <code>x-amz-meta-</code> and leaving the 
     * HTTP header names unchanged. The HTTP header names left unchanged are:
     * <table>
     * <tr><th>Unchanged metadata names</th></tr>
     * <tr><td>content-type</td></tr>
     * <tr><td>content-md5</td></tr>
     * <tr><td>content-length</td></tr>
     * <tr><td>content-language</td></tr>
     * <tr><td>expires</td></tr>
     * <tr><td>cache-control</td></tr>
     * <tr><td>content-disposition</td></tr>
     * <tr><td>content-encoding</td></tr>
     * </table>
     * 
     * @param metadata
     * @return
     */
    public static Map renameMetadataKeys(Map metadata) {
        Map convertedMetadata = new HashMap();
        // Add all meta-data headers.
        if (metadata != null) {
            Iterator metaDataIter = metadata.keySet().iterator();
            while (metaDataIter.hasNext()) {                
                String key = (String) metaDataIter.next();
                Object value = metadata.get(key);

                if (!key.equalsIgnoreCase("content-type") 
                    && !key.equalsIgnoreCase("content-md5")
                    && !key.equalsIgnoreCase("content-length")
                    && !key.equalsIgnoreCase("content-language")
                    && !key.equalsIgnoreCase("expires")
                    && !key.equalsIgnoreCase("cache-control")
                    && !key.equalsIgnoreCase("content-disposition")
                    && !key.equalsIgnoreCase("content-encoding")
                    && !key.startsWith(Constants.REST_HEADER_PREFIX)) 
                {
                    key = Constants.REST_METADATA_PREFIX + key;
                }                
                convertedMetadata.put(key, value);
            }
        }
        return convertedMetadata;
    }    

}
