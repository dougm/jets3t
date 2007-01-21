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

/**
 * Constants used by the S3Service and its implementation classes.
 * 
 * @author James Murty
 */
public class Constants {
    
    ////////////////////////////////////
    // Default file names and locations.
    ////////////////////////////////////
    
    /**
     * The name of the <a href="http://jets3t-test.s3.amazonaws.com/toolkit/configuration.html#jets3t">JetS3t properties</a>
     * file: jets3t.properties
     */
    public static final String JETS3T_PROPERTIES_FILENAME = "jets3t.properties";
    
    /**
     * The name of the <a href="http://jets3t-test.s3.amazonaws.com/toolkit/configuration.html#ignore">JetS3t ignore</a> 
     * file: .jets3t-ignore 
     */
    public static final String JETS3T_IGNORE_FILENAME = ".jets3t-ignore";
    
    /**
     * The default preferences directory: &lt;user.home&gt;/.jets3t
     */
    public static final File DEFAULT_PREFERENCES_DIRECTORY = new File(System.getProperty("user.home")
        + "/.jets3t");

    /**
     * The file delimiter used by JetS3t is the '/' character, which is compatible with standard
     * browser access to S3 files.
     */
    public static final String FILE_PATH_DELIM = "/";

    /**
     * The default encoding used for text data: UTF8 
     */
    public static final String DEFAULT_ENCODING = "UTF8";

    /**
     * HMAC/SHA1 Algorithm per RFC 2104, used when generating S3 signatures.
     */
    public static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    ///////////////////////////////////////
    // JetS3t-specific metadata item names.
    ///////////////////////////////////////
    /**
     * Metadata header for storing the original date of a local file uploaded to S3, so it can
     * be used subsequently to compare files instead of relying on the S3 upload date.
     */
    public static final String METADATA_JETS3T_LOCAL_FILE_DATE = "jets3t-original-file-date-iso860";
    
    /**
     * Metadata header for storing information about data encryption applied by JetS3t tools.
     * @deprecated Obsolete after version 0.4.0
     */
    public static final String METADATA_JETS3T_ENCRYPTED_OBSOLETE = "jets3t-encryption";

    /**
     * Metadata header for storing information about the data encryption algorithm applied by JetS3t tools.
     */
    public static final String METADATA_JETS3T_CRYPTO_ALGORITHM = "jets3t-crypto-alg";

    /**
     * Metadata header for storing information about the JetS3t version of encryption applied 
     * (to keep encryption compatibility between versions).
     */
    public static final String METADATA_JETS3T_CRYPTO_VERSION = "jets3t-crypto-ver";

    /**
     * Metadata header for storing information about data compression applied by jets3t tools.
     */
    public static final String METADATA_JETS3T_COMPRESSED = "jets3t-compression";

    ///////////////////////////////////
    // Settings used by all S3 Services
    ///////////////////////////////////

    /**
     * Default number of objects to include in each chunk of an object listing.
     */
    public static final long DEFAULT_OBJECT_LIST_CHUNK_SIZE = 1000;
    
    ///////////////////////////////////
    // Headers used by REST S3 Services
    ///////////////////////////////////

    /**
     * Header prefix for general Amazon headers: x-amz-
     */
    public static final String REST_HEADER_PREFIX = "x-amz-";
    /**
     * Header prefix for Amazon metadata headers: x-amz-meta-
     */
    public static final String REST_METADATA_PREFIX = "x-amz-meta-";
    /**
     * Header prefix for Amazon's alternative date header: x-amz-date
     */
    public static final String REST_METADATA_ALTERNATE_DATE = "x-amz-date";
    /**
     * XML namespace URL used when generating S3-compatible XML documents:
     * http://s3.amazonaws.com/doc/2006-03-01/
     */
    public static final String XML_NAMESPACE = "http://s3.amazonaws.com/doc/2006-03-01/";

    ///////////////////////////////////
    // Headers used by SOAP S3 Services
    ///////////////////////////////////

    /**
     * SOAP service name: AmazonS3
     */
    public static final String SOAP_SERVICE_NAME = "AmazonS3";
    
}
