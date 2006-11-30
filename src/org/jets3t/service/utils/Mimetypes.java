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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class to contain known Mimetypes, and determine the mimetype of files based on the file's
 * extension.
 * <p>
 * This class is statically loaded with mime types from the file <code>mime.types</code> if this
 * file is available at the root of the classpath. The mime.types file format, and most of the
 * content, is taken from the Apache HTTP server's mime.types file.
 * <p>
 * The format for mime type setting documents is: 
 * <code>mimetype <Space | Tab>+ extension (<Space|Tab>+ extension)*</code>.
 * Any blank lines in the file are ignored, as are lines starting with <code>#</code> which are
 * considered comments. Lines that have a mimetype but no associated extensions are also ignored.
 * 
 * @author James Murty
 */
public class Mimetypes {
    private static final Log log = LogFactory.getLog(Mimetypes.class);
    
    // Mimetypes used frequently in jets3t.
    public static String MIMETYPE_XML = "application/xml";
    public static String MIMETYPE_HTML = "text/html";
    public static String MIMETYPE_OCTET_STREAM = "application/octet-stream";
    public static String MIMETYPE_GZIP = "application/x-gzip";
    
    // Mimetypes specific to jetS3T.
    public static String MIMETYPE_JETS3T_DIRECTORY = "application/x-directory";
    
    /**
     * Map that stores file extensions as keys, and the corresponding mimetype as values.
     */
    private static HashMap extensionToMimetypeMap = new HashMap();
    
    /**
     * Loads mime type settings from the file 'mime.types' in the classpath, if it's available.
     */
    static {
        InputStream mimetypesFile = extensionToMimetypeMap.getClass().getResourceAsStream("/mime.types");
        if (mimetypesFile != null) {
            log.debug("Loading mime types from file in the classpath: mime.types");
            try {
                loadAndReplaceMimetypes(mimetypesFile);
            } catch (IOException e) {
                log.error("Failed to load mime types from file in the classpath: mime.types", e); 
            }
        }
    }

    /**
     * Reads and stores the mime type setting corresponding to a file extension, by reading
     * text from an InputStream. If a mime type setting already exists when this method is run, 
     * the mime type value is replaced with the newer one.
     *  
     * @param is
     * 
     * @throws IOException
     */
    public static void loadAndReplaceMimetypes(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line =  null;
        
        while ((line = br.readLine()) != null) {
            line = line.trim();

            if (line.startsWith("#") || line.length() == 0) {
                // Ignore comments and empty lines.
            } else {
                StringTokenizer st = new StringTokenizer(line, " \t");
                if (st.countTokens() > 1) {
                    String mimetype = st.nextToken();
                    while (st.hasMoreTokens()) {
                        String extension = st.nextToken();
                        extensionToMimetypeMap.put(extension, mimetype);
                        log.debug("Setting mime type for extension '" + extension + "' to '" + mimetype + "'");
                    }
                } else {
                    log.debug("Ignoring mimetype with no associated file extensions: '" + line + "'");                    
                }
            }
        }
    }

    /**
     * Determines the mimetype of a file by looking up the file's extension in an internal listing
     * to find the corresponding mime type. If the file has no extension, or the extension is not
     * available in the listing contained in this class, the default mimetype 
     * <code>application/octet-stream</code> is returned. 
     * <p>
     * A file extension is one or more characters that occur after the last period (.) in the file's name.
     * If a file has no extension, 
     * Guesses the mimetype of file data based on the file's extension. 
     *  
     * @param fileName
     * the name of the file whose extension may match a known mimetype.
     * 
     * @return
     * the file's mimetype based on its extension, or a default value of 
     * <code>application/octet-stream</code> if a mime type value cannot be found.
     */
    public static String getMimetype(String fileName) {
        int lastPeriodIndex = fileName.lastIndexOf(".");
        if (lastPeriodIndex > 0 && lastPeriodIndex + 1 < fileName.length()) {
            String ext = fileName.substring(lastPeriodIndex + 1);
            if (extensionToMimetypeMap.keySet().contains(ext)) {
                String mimetype = (String) extensionToMimetypeMap.get(ext);
                log.debug("Recognised extension '" + ext + "', mimetype is: '" + mimetype + "'");                
                return mimetype;
            } else {
                log.debug("Extension '" + ext + "' is unrecognized in mime type listing"
                    + ", using default mime type: '" + MIMETYPE_OCTET_STREAM + "'");                
            }
        } else {
            log.debug("File name has no extension, mime type cannot be recognised for: " + fileName);
        }
        return MIMETYPE_OCTET_STREAM;
    }
    
    /**
     * Determines the mimetype of a file by looking up the file's extension in an internal listing
     * to find the corresponding mime type. If the file has no extension, or the extension is not
     * available in the listing contained in this class, the default mimetype 
     * <code>application/octet-stream</code> is returned. 
     * <p>
     * A file extension is one or more characters that occur after the last period (.) in the file's name.
     * If a file has no extension, 
     * Guesses the mimetype of file data based on the file's extension. 
     *  
     * @param file
     * the file whose extension may match a known mimetype.
     * 
     * @return
     * the file's mimetype based on its extension, or a default value of 
     * <code>application/octet-stream</code> if a mime type value cannot be found.
     */
    public static String getMimetype(File file) {
       return getMimetype(file.getName()); 
    }
        
}
