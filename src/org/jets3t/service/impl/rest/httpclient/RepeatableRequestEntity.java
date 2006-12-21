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
package org.jets3t.service.impl.rest.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.io.IRepeatableInputStream;
import org.jets3t.service.io.InputStreamWrapper;
import org.jets3t.service.io.ProgressMonitoredInputStream;
import org.jets3t.service.io.RepeatableInputStream;

/**
 * A request entity whose underlying data can be repeated if necessary to retry when
 * transmission errors occur.
 * <p>
 * This class works by using an underlying {@link IRepeatableInputStream} input stream,
 * which is repeated when necessary.
 * <p>
 * When data is repeated, any attached {@link ProgressMonitoredInputStream} is notified
 * that a repeat transmission is occurring.
 * 
 * @author James Murty
 */
public class RepeatableRequestEntity implements RequestEntity {
    private final Log log = LogFactory.getLog(RepeatableRequestEntity.class);

    private InputStream is = null;
    private String contentType = null;
    private long contentLength = 0;
    
    int bytesWritten = 0;    
    private IRepeatableInputStream repeatableInputStream = null;
    private ProgressMonitoredInputStream progressMonitoredIS = null;
    
    /**
     * Creates a repeatable request entity for the input stream provided.
     * <p>
     * If the input stream, or any underlying wrapped input streams, implements the 
     * {@link IRepeatableInputStream} interface then this underlying input stream is used to
     * trigger repeats.
     * <p>
     * If no underlying {@link IRepeatableInputStream} input stream is available the
     * constructor wraps the provided input stream in a {@link RepeatableInputStream}, 
     * which is used internally.
     * <p>
     * This constructor also detects when an underlying {@link ProgressMonitoredInputStream} is
     * present, and will notify this monitor if a repeat occurs.
     * 
     * @param is
     * @param contentType
     * @param contentLength
     */
    public RepeatableRequestEntity(InputStream is, String contentType, long contentLength) {
        if (is == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        this.is = is;
        this.contentLength = contentLength;
        this.contentType = contentType;
        
        InputStream inputStream = is;
        while (inputStream instanceof InputStreamWrapper) {
            if (inputStream instanceof ProgressMonitoredInputStream) {
                progressMonitoredIS = (ProgressMonitoredInputStream) inputStream;
            } 
            if (inputStream instanceof IRepeatableInputStream) {
                repeatableInputStream = (IRepeatableInputStream) inputStream;
            }
            inputStream = ((InputStreamWrapper) inputStream).getWrappedInputStream();
        }

        if (this.repeatableInputStream == null) {
            log.debug("Wrapping non-repeatable input stream in a RepeatableInputStream");
            this.is = new RepeatableInputStream(is);
            this.repeatableInputStream = (IRepeatableInputStream) this.is;
        } 
    }
    
    public long getContentLength() {
      return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    /**
     * @return
     * always returns true. 
     * If the input stream is not actually repeatable, an IOException will be thrown 
     * later by the {@link #writeRequest(OutputStream)} method when the repeat is attempted.
     */
    public boolean isRepeatable() {
        return true;
    }
    
    /**
     * Writes the request to the output stream. If the request is being repeated, the underlying 
     * repeatable input stream will be reset with a call to 
     * {@link IRepeatableInputStream#repeatInputStream()}. 
     * <p>
     * If a {@link ProgressMonitoredInputStream} is attached, this monitor will be notified that 
     * data is being repeated by being sent the number of bytes being repeated as a negative number
     * (because the progress has jumped backwards as this amount of data has to be repeated).
     */
    public void writeRequest(OutputStream out) throws IOException {        
        if (bytesWritten > 0) {
            // This entity is being repeated.           
            repeatableInputStream.repeatInputStream();
            log.warn("Repeating transmission of " + bytesWritten + " bytes");

            // Notify progress monitored input stream that we've gone backwards (if one is attached) 
            if (progressMonitoredIS != null) {
                progressMonitoredIS.sendNotificationUpdate(0 - bytesWritten);
            }
            
            bytesWritten = 0;
        }

        byte[] tmp = new byte[8192];
        int count = 0;

        while ((count = is.read(tmp)) >= 0) {
            bytesWritten += count;
            
            out.write(tmp, 0, count);            
        }                
    }
    
}
