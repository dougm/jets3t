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
package org.jets3t.service.io;

import java.io.IOException;
import java.io.InputStream;


public class ProgressMonitoredInputStream extends InputStream implements InputStreamWrapper {
    private InputStream inputStream = null;
    private BytesTransferredListener bytesTransferredListener = null;
    private long minimumBytesBeforeNotification = 1024;
    private long bytesTransferredTotal = 0;
    private long bytesTransferredLastUpdate = 0;

    public ProgressMonitoredInputStream(InputStream inputStream, 
        BytesTransferredListener bytesTransferredListener, long minimumBytesBeforeNotification) 
    {
        if (inputStream == null) {
            throw new IllegalArgumentException(
                "ProgressMonitoredInputStream cannot run with a null InputStream");
        }
        this.inputStream = inputStream;
        this.bytesTransferredListener = bytesTransferredListener;
        this.minimumBytesBeforeNotification = minimumBytesBeforeNotification;
    }

    public ProgressMonitoredInputStream(InputStream inputStream, 
        BytesTransferredListener bytesTransferredListener) 
    {
        this(inputStream, bytesTransferredListener, 1024);
    }

    public void setBytesTransferredListener(BytesTransferredListener bytesTransferredListener) {
        this.bytesTransferredListener = bytesTransferredListener;
    }
    
    private void maybeNotifyListener(long bytesTransmitted) {
        bytesTransferredTotal += bytesTransmitted;
        if (bytesTransferredListener != null) {
            // Notify listener if more than the minimum number of bytes have been transferred since last time
            long bytesSinceLastUpdate = bytesTransferredTotal - bytesTransferredLastUpdate;
            if (bytesSinceLastUpdate > minimumBytesBeforeNotification) {
                bytesTransferredListener.bytesTransferredUpdate(bytesSinceLastUpdate);
                bytesTransferredLastUpdate = bytesTransferredTotal;
            }
        }
    }
    
    public int read() throws IOException {
        int read = inputStream.read();
        if (read != -1) {
            maybeNotifyListener(1);
        }
        return read; 
    }
    
    public int read(byte[] b, int off, int len) throws IOException {
        int read = inputStream.read(b, off, len);
        if (read != -1) {
            maybeNotifyListener(read);
        }
        return read;
    }
    
    public int available() throws IOException {
        return inputStream.available();
    }
    
    public void close() throws IOException {
        inputStream.close();            
    }
    
    public InputStream getWrappedInputStream() {
        return inputStream;
    }
    
}
