package org.jets3t.service.impl.rest.httpclient;

import org.apache.commons.httpclient.HttpMethod;

public interface AWSRequestAuthorizer {

    public void authorizeHttpRequest(HttpMethod httpMethod) throws Exception;
}
