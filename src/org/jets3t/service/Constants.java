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
package org.jets3t.service;

import java.io.File;

public class Constants {	
	public static File DEFAULT_PREFERENCES_DIRECTORY = new File(System.getProperty("user.home") + "/.jets3t");
		
	public static String METADATA_JETS3T_LOCAL_FILE_DATE = "local-file-date-iso860";
    public static String METADATA_JETS3T_ENCRYPTED = "x-encrypted";
    
    
    public static String FILE_PATH_DELIM = "/";
	

	public static String DEFAULT_ENCODING = "UTF8";
    /**
     * HMAC/SHA1 Algorithm per RFC 2104.
     */
    public static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	
	public static String REST_HEADER_PREFIX = "x-amz-";
	public static String REST_METADATA_PREFIX = "x-amz-meta-";
	public static String REST_METADATA_ALTERNATE_DATE = "x-amz-date";
	public static String REST_SERVER_DNS = "s3.amazonaws.com";
    
    public static String ACL_NAMESPACE = "http://s3.amazonaws.com/doc/2006-03-01/";
    
    public static final String SOAP_SERVICE_NAME = "AmazonS3";    
	
	public static String INPUT_STREAM_MAP_KEY = "ResponseInputStream";
	public static long OBJECT_LIST_CHUNK_SIZE = 1000;
    
    public static File getPreferencesDirectory(File directory) {
        File preferencesDirectory = new File(directory, ".jets3t");
        if (!preferencesDirectory.exists()) { 
            preferencesDirectory.mkdirs();
        }
        return preferencesDirectory;
    }
    
}
