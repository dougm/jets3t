package org.jets3t.service.impl.rest.httpclient;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;

public class HttpClientAndConnectionManager {
    protected HttpClient httpClient = null;
    protected HttpConnectionManager httpConnectionManager = null;
    
    public HttpClientAndConnectionManager(HttpClient httpClient,
        HttpConnectionManager httpConnectionManager) 
    {
        this.httpClient = httpClient;
        this.httpConnectionManager = httpConnectionManager;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public HttpConnectionManager getHttpConnectionManager() {
        return httpConnectionManager;
    }
        
}
