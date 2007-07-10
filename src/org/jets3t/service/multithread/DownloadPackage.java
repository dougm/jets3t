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
package org.jets3t.service.multithread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.io.GZipInflatingOutputStream;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.EncryptionUtil;

/**
 * A simple container object to associate one of an {@link S3Object} or a signed URL string
 * with an output file, where the object's data will be written to. 
 * <p>
 * This class is used by {@link S3ServiceMulti#downloadObjects(S3Bucket, DownloadPackage[])}
 * to download objects.   
 * 
 * @author James Murty
 */
public class DownloadPackage {
    private static final Log log = LogFactory.getLog(DownloadPackage.class);

    private S3Object object = null;
    private String signedUrl = null;
    
    private File outputFile = null;
    private boolean isUnzipping = false;
    private EncryptionUtil encryptionUtil = null;
    
    public DownloadPackage(S3Object object, File outputFile) {
        this(object, outputFile, false, null);
    }
    
    public DownloadPackage(S3Object object, File outputFile, boolean isUnzipping, 
        EncryptionUtil encryptionUtil) 
    {
        this.object = object;        
        this.outputFile = outputFile;
        this.isUnzipping = isUnzipping;
        this.encryptionUtil = encryptionUtil;
    }
    
    public DownloadPackage(String signedUrl, S3Object object, File outputFile, boolean isUnzipping, 
            EncryptionUtil encryptionUtil) 
        {
            this.signedUrl = signedUrl;        
    		this.object = object;        
            this.outputFile = outputFile;
            this.isUnzipping = isUnzipping;
            this.encryptionUtil = encryptionUtil;
        }

    public S3Object getObject() {
        return object;
    }
    
    public File getDataFile() {
        return outputFile;
    }
    
    public String getSignedUrl() {
    	return signedUrl;
    }
     
    public void setSignedUrl(String url) {
    	signedUrl = url;
    }
    
    public boolean isSignedDownload() {
    	return signedUrl != null;
    }
    
    /**
     * Creates an output stream to receive the object's data. The output stream is based on a 
     * FileOutputStream, but will also be wrapped in a GZipInflatingOutputStream if
     * isUnzipping is true and/or a decrypting output stream if this package has an associated
     * non-null EncryptionUtil.
     * 
     * @return
     * an output stream that writes data to the output file managed by this class. 
     * 
     * @throws Exception
     */
    public OutputStream getOutputStream() throws Exception {
        OutputStream outputStream = new FileOutputStream(outputFile);
        if (isUnzipping) {
            log.debug("Inflating gzipped data for object: " + object.getKey());                    
            outputStream = new GZipInflatingOutputStream(outputStream);            
        }
        if (encryptionUtil != null) {
            log.debug("Decrypting encrypted data for object: " + object.getKey());
            outputStream = encryptionUtil.decrypt(outputStream);                                                        
        }
        return outputStream;       
    }    
    
}
