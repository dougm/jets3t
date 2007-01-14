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

/**
 * Utility class to wrap InputStreams obtained from an HttpClient library's HttpMethod object, and
 * ensure the stream and HTTP connection is cleaned up properly. 
 * <p>
 * <b>Important!</b> This input stream must be completely consumed or closed to ensure the necessary
 * cleanup operations can be performed.
 * 
 * @author James Murty
 *
 */
public class HttpMethodReleaseInputStream extends InputStream implements InputStreamWrapper {
    private final Log log = LogFactory.getLog(HttpMethodReleaseInputStream.class);
    
    private InputStream inputStream = null;
    private HttpMethod httpMethod = null;
    private boolean alreadyReleased = false;
    private boolean underlyingStreamConsumed = false;

    /**
     * Constructs an input stream based on an {@link HttpMethod} object representing an HTTP connection.
     * If a connection input stream is available, this constructor wraps the underlying input stream
     * in an {@link InterruptableInputStream} and makes that stream available. If no underlying connection 
     * is available, an empty {@link ByteArrayInputStream} is made available.
     * 
     * @param httpMethod
     */
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
    
    /**
     * Returns the underlying HttpMethod object that contains/manages the actual HTTP connection.
     * 
     * @return
     * the HTTPMethod object that provides the data input stream.
     */
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
    
    /**
     * Forces the release of an HttpMethod's connection in a way that will perform all the necessary
     * cleanup through the correct use of HttpClient methods. 
     * 
     * @throws IOException
     */
    protected void releaseConnection() throws IOException {
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
    
    /**
     * Standard input stream read method, except it calls {@link #releaseConnection} when the underlying
     * input stream is consumed.
     */
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
    
    /**
     * Standard input stream read method, except it calls {@link #releaseConnection} when the underlying
     * input stream is consumed.
     */
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
    
    /**
     * Standard input stream close method, except it ensures that {@link #releaseConnection()} is called
     * before the input stream is closed.
     */
    public void close() throws IOException {
        if (!alreadyReleased) {        
            releaseConnection();
            log.debug("Released HttpMethod as its response data stream is closed");
        }
        inputStream.close();
    }
    
    /**
     * Tries to ensure a connection is always cleaned-up correctly by calling {@link #releaseConnection()}
     * on class destruction if the cleanup hasn't already been done. 
     * <p>
     * This desperate cleanup act will only be necessary if the user of this class does not completely
     * consume or close this input stream prior to object destruction. This method will log Warning 
     * messages if cleanup is required berating the caller. 
     */
    protected void finalize() throws Throwable {
        if (!alreadyReleased) {
            releaseConnection();
            log.warn("Released HttpMethod in finalize() as response data stream has gone out of scope. "
                + "This behaviour is not guarenteed! Please close all response data streams yourself.");
        }
        super.finalize();
    }
    
    /**
     * @return
     * the underlying input stream wrapped by this class.
     */
    public InputStream getWrappedInputStream() {
        return inputStream;
    }
    
}
