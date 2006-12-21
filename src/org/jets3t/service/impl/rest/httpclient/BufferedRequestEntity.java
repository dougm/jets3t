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
import org.jets3t.service.Constants;
import org.jets3t.service.Jets3tProperties;
import org.jets3t.service.io.UnrecoverableIOException;
import org.jets3t.service.io.InputStreamWrapper;
import org.jets3t.service.io.ProgressMonitoredInputStream;

/**
 * A request entity that is repeatable provided only a small amount of data has been transmitted. 
 * This class buffers a small amount of data, and if the request fails having transmitted less than
 * this buffer holds the request can be repeated.
 * <p><b>Properties</b></p>
 * <p>This class uses the following properties:</p>
 * <table>
 * <tr><th>Property</th><th>Description</th><th>Default</th></tr>
 * <tr><td>httpclient.retry-buffer-size</td>
 *   <td>How many bytes to buffer for use when retrying failed transmissions</td>
 *   <td>1024000</td></tr>
 * </table>
 * 
 * @author James Murty
 */
public class BufferedRequestEntity implements RequestEntity {
    private final Log log = LogFactory.getLog(BufferedRequestEntity.class);

    private int bufferSize = 0;    
    private InputStream is = null;
    private String contentType = null;
    private long contentLength = 0;
    
    private int bytesWritten = 0;
    private byte[] buffer = null;
    private ProgressMonitoredInputStream progressMonitoredIS = null;
    
    /**
     * Constructs a buffered entity with a given buffer size.
     * 
     * @param bufferSize
     * @param is
     * @param contentType
     * @param contentLength
     */
    public BufferedRequestEntity(int bufferSize, InputStream is, String contentType, long contentLength) {
        if (is == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        this.bufferSize = bufferSize;
        this.is = is;
        this.contentLength = contentLength;
        this.contentType = contentType;
        buffer = new byte[this.bufferSize];
        
        InputStream isWrapper = is;
        while (isWrapper instanceof InputStreamWrapper) {
            if (isWrapper instanceof ProgressMonitoredInputStream) {
                progressMonitoredIS = (ProgressMonitoredInputStream) isWrapper;
            }
            isWrapper = ((InputStreamWrapper) isWrapper).getWrappedInputStream();
        }
    }

    /**
     * Constructs a buffered entity with the buffer size set by the JetS3t property
     * <code>httpclient.retry-buffer-size</code>
     * 
     * @param is
     * @param contentType
     * @param contentLength
     */
    public BufferedRequestEntity(InputStream is, String contentType, long contentLength) {
        this(
            Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
                .getIntProperty("httpclient.retry-buffer-size", 1024000),
            is, contentType, contentLength);
    }
    
    public long getContentLength() {
      return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    /**
     * @return
     * true if fewer bytes have been written than are available in the buffer, and are therefore
     * available for replay. False otherwise.
     */
    public boolean isRepeatable() {
        boolean repeatable = (bytesWritten < bufferSize);
        if (repeatable) {
            log.warn("Repeating " + this.bytesWritten + " bytes in buffered request entity. Buffer size is " 
                 + this.bufferSize + " bytes ");

            // Notify progress monitored input stream that we've gone backwards (if one is attached) 
            if (progressMonitoredIS != null) {
                progressMonitoredIS.sendNotificationUpdate(0 - bytesWritten);
            }
            
        } else {
            log.warn("Buffered request entity is not repeatable as " + this.bytesWritten 
                + " bytes have been written, which exceeds the available buffer size " 
                + this.bufferSize);
        }
        return repeatable;
    }

    /**
     * Writes data from the wrapped input stream to the output, buffering a small amount of data
     * as it goes. If this method is repeated (ie is called when the buffer is not empty) the
     * buffer is written to the output first, then the remaining data is passed from the input stream
     * to the output.
     */
    public void writeRequest(OutputStream out) throws IOException {
        byte[] tmp = new byte[8192];

        if (bytesWritten > 0) {
            // This entity is being repeated, flush data from buffer before reading from input stream.

            // We write the buffered data in chunks so the progress monitor is only updated a
            // little as a time, as the output stream actually pushes through data.
            int offset = 0;
            while (offset < bytesWritten) {
                int length = tmp.length;
                if (offset + length > bytesWritten) {
                    length = bytesWritten - offset;
                }
                out.write(buffer, offset, length);
                offset += length;
                
                // Notify the attached progress monitored input stream directly, as we are not
                // actually reading from the wrapped input stream at this stage.
                if (progressMonitoredIS != null) {
                    progressMonitoredIS.sendNotificationUpdate(length);
                }                
            }
        }
        
        int count = 0;

        while ((count = is.read(tmp)) >= 0) {
            if (bytesWritten + count < bufferSize) {
                System.arraycopy(tmp, 0, buffer, bytesWritten, count);
            }
            bytesWritten += count;
            
            out.write(tmp, 0, count);
        }        
        
        if (bytesWritten < contentLength) {
            throw new UnrecoverableIOException(
                "Data underflow error as too little data was available. Expected " 
                + contentLength + " bytes, but only " + bytesWritten + " bytes were available");
        }
        
    }

}
