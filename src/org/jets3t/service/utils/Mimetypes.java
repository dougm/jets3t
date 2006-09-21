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

import java.io.File;

/**
 * Utility class to contain known Mimetypes, and determine the mimetypes of files based on the file's
 * extension.
 *
 * TODO There is much work to do here.
 * 
 * @author James Murty
 */
public class Mimetypes {
    // Public mimetypes.
    public static String MIMETYPE_XML = "application/xml";
    public static String MIMETYPE_HTML = "text/html";
    public static String MIMETYPE_OCTET_STREAM = "application/octet-stream";
    public static String MIMETYPE_GZIP = "application/x-gzip";
    
    // Mimetypes specific to jetS3T.
    public static String MIMETYPE_JETS3T_DIRECTORY = "application/x-directory";
    
    /**
     * Guesses the mimetype of file data based on the file's extension. 
     *  
     * @param file
     * @return
     */
    public static String getMimetype(File file) {
        if (file.getName().endsWith(".html") || file.getName().endsWith(".htm")) {
            return MIMETYPE_HTML;
        } 
        else if (file.getName().endsWith(".xml")) {
            return MIMETYPE_XML;
        }
        else if (file.getName().endsWith(".gz")) {
            return MIMETYPE_GZIP;
        }
        else {
            return MIMETYPE_OCTET_STREAM;
        }
    }
    
}
