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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.io.InputStreamWrapper;
import org.jets3t.service.io.InterruptableInputStream;

public class HttpMethodReleaseInputStream extends InputStream implements InputStreamWrapper {
    private final Log log = LogFactory.getLog(HttpMethodReleaseInputStream.class);
    
    private InputStream inputStream = null;
    private HttpMethod httpMethod = null;
    private boolean alreadyReleased = false;
    private boolean underlyingStreamConsumed = false;

    public HttpMethodReleaseInputStream(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
        try {
            this.inputStream = new InterruptableInputStream(httpMethod.getResponseBodyAsStream());
        } catch (IOException e) {
            log.warn("Unable to obtain HttpMethod's response data stream", e);
            httpMethod.releaseConnection();
            this.inputStream = new ByteArrayInputStream(new byte[] {}); // Empty input stream;
        }
    }
    
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
    
    private void releaseConnection() throws IOException {
        if (!alreadyReleased) {
            if (!underlyingStreamConsumed) {
                // Underlying input stream has not been consumed, abort method 
                // to force connection to be closed and cleaned-up.
                httpMethod.abort();                
            }
            httpMethod.releaseConnection();
            alreadyReleased = true;
        }
    }
    
    public int read() throws IOException {
        int read = inputStream.read();
        if (read == -1) {
            underlyingStreamConsumed = true;
            if (!alreadyReleased) {
                releaseConnection();
                log.debug("Released HttpMethod as its response data stream is fully consumed");
            }
        }
        return read;
    }
    
    public int read(byte[] b, int off, int len) throws IOException {        
        int read = inputStream.read(b, off, len);
        if (read == -1) {
            underlyingStreamConsumed = true;
            if (!alreadyReleased) {
                releaseConnection();
                log.debug("Released HttpMethod as its response data stream is fully consumed");
            }
        }
        return read;
    }
    
    public int available() throws IOException {
        return inputStream.available();
    }
    
    public void close() throws IOException {
        if (!alreadyReleased) {        
            releaseConnection();
            log.debug("Released HttpMethod as its response data stream is closed");
        }
        inputStream.close();
    }
    
    protected void finalize() throws Throwable {
        if (!alreadyReleased) {
            releaseConnection();
            log.warn("Released HttpMethod in finalize() as response data stream has gone out of scope. "
                + "This behaviour is not guarenteed! Please close all response data streams yourself.");
        }
        super.finalize();
    }
    
    public InputStream getWrappedInputStream() {
        return inputStream;
    }
    
}
