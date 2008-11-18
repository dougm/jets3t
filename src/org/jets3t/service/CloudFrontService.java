/*
 * jets3t : Java Extra-Tasty S3 Toolkit (for Amazon S3 online storage service)
 * This is a java.net project, see https://jets3t.dev.java.net/
 * 
 * Copyright 2008 James Murty
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
package org.jets3t.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.auth.CredentialsProvider;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.impl.rest.CloudFrontXmlResponsesSaxParser;
import org.jets3t.service.impl.rest.CloudFrontXmlResponsesSaxParser.DistributionConfigHandler;
import org.jets3t.service.impl.rest.CloudFrontXmlResponsesSaxParser.DistributionHandler;
import org.jets3t.service.impl.rest.CloudFrontXmlResponsesSaxParser.ErrorHandler;
import org.jets3t.service.impl.rest.CloudFrontXmlResponsesSaxParser.ListDistributionListHandler;
import org.jets3t.service.impl.rest.httpclient.AWSRequestAuthorizer;
import org.jets3t.service.impl.rest.httpclient.HttpClientAndConnectionManager;
import org.jets3t.service.model.cloudfront.Distribution;
import org.jets3t.service.model.cloudfront.DistributionConfig;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.utils.RestUtils;
import org.jets3t.service.utils.ServiceUtils;

public class CloudFrontService implements AWSRequestAuthorizer {
    private static final Log log = LogFactory.getLog(CloudFrontService.class);

    public static final String ENDPOINT = "https://cloudfront.amazonaws.com/";
    public static final String VERSION = "2008-06-30";
    public static final String XML_NAMESPACE = "http://cloudfront.amazonaws.com/doc/" + VERSION + "/";
    
    private HttpClient httpClient = null;
    private CredentialsProvider credentialsProvider = null;

    private AWSCredentials awsCredentials = null;
    protected Jets3tProperties jets3tProperties = null;
    private String invokingApplicationDescription = null;

    /**
     * The approximate difference in the current time between your computer and
     * Amazon's servers, measured in milliseconds.
     * 
     * This value is 0 by default. Use the {@link #getCurrentTimeWithOffset()} 
     * to obtain the current time with this offset factor included, and the 
     * {@link #getAWSTimeAdjustment()} method to calculate an offset value for your
     * computer based on a response from an AWS server.
     */
    protected long timeOffset = 0;

    public CloudFrontService(AWSCredentials awsCredentials, String invokingApplicationDescription, 
        CredentialsProvider credentialsProvider, Jets3tProperties jets3tProperties,
        HostConfiguration hostConfig) throws CloudFrontServiceException 
    {
        this.awsCredentials = awsCredentials;        
        this.invokingApplicationDescription = invokingApplicationDescription;        
        this.credentialsProvider = credentialsProvider;
        this.jets3tProperties = jets3tProperties;                
        
        // Configure the InetAddress DNS caching times to work well with CloudFront. The cached DNS will
        // timeout after 5 minutes, while failed DNS lookups will be retried after 1 second.
        System.setProperty("networkaddress.cache.ttl", "300");
        System.setProperty("networkaddress.cache.negative.ttl", "1");
        
        HttpClientAndConnectionManager initHttpResult = RestUtils.initHttpConnection(
            this, hostConfig, jets3tProperties, 
            this.invokingApplicationDescription, this.credentialsProvider);
        this.httpClient = initHttpResult.getHttpClient();
        
        // Retrieve Proxy settings.
        if (this.jets3tProperties.getBoolProperty("httpclient.proxy-autodetect", true)) {
            RestUtils.initHttpProxy(this.httpClient);
        } else {
            String proxyHostAddress = this.jets3tProperties.getStringProperty("httpclient.proxy-host", null);
            int proxyPort = this.jets3tProperties.getIntProperty("httpclient.proxy-port", -1);            
            String proxyUser = this.jets3tProperties.getStringProperty("httpclient.proxy-user", null);
            String proxyPassword = this.jets3tProperties.getStringProperty("httpclient.proxy-password", null);
            String proxyDomain = this.jets3tProperties.getStringProperty("httpclient.proxy-domain", null);            
            RestUtils.initHttpProxy(this.httpClient, proxyHostAddress, proxyPort, proxyUser, proxyPassword, proxyDomain);
        }                
    }

    /**
     * @return the AWS Credentials identifying the AWS user, may be null if the service is acting
     * anonymously.
     */
    public AWSCredentials getAWSCredentials() {
        return awsCredentials;
    }

    /**
     * Returns the current date and time, adjusted according to the time
     * offset between your computer and an AWS server (as set by the
     * {@link #getAWSTimeAdjustment()} method).
     * 
     * @return
     * the current time, or the current time adjusted to match the AWS time 
     * if the {@link #getAWSTimeAdjustment()} method has been invoked.
     */
    protected Date getCurrentTimeWithOffset() {
        return new Date(System.currentTimeMillis() + timeOffset);
    }

    /**
     * @param httpMethod
     * the request object
     * @throws Exception
     */
    public void authorizeHttpRequest(HttpMethod httpMethod) throws Exception {
        String date = ServiceUtils.formatRfc822Date(getCurrentTimeWithOffset());
        
        // Set/update the date timestamp to the current time 
        // Note that this will be over-ridden if an "x-amz-date" header is present.
        httpMethod.setRequestHeader("Date", date);
        
        // Sign the date to authenticate the request.
        // Sign the canonical string.
        String signature = ServiceUtils.signWithHmacSha1(
            getAWSCredentials().getSecretKey(), date);
        
        // Add encoded authorization to connection as HTTP Authorization header. 
        String authorizationString = "AWS " + getAWSCredentials().getAccessKey() + ":" + signature;
        httpMethod.setRequestHeader("Authorization", authorizationString);
    }

    protected void performRestRequest(HttpMethod httpMethod, int expectedResponseCode) 
        throws CloudFrontServiceException 
    {
        // Set mandatory Request headers.
        if (httpMethod.getRequestHeader("Date") == null) {
            httpMethod.setRequestHeader("Date", ServiceUtils.formatRfc822Date(
                getCurrentTimeWithOffset()));
        }

        try {
            authorizeHttpRequest(httpMethod);
            int responseCode = httpClient.executeMethod(httpMethod);
    
            if (responseCode != expectedResponseCode) {
                if ("text/xml".equals(httpMethod.getResponseHeader("Content-Type"))) {
                    ErrorHandler handler = (new CloudFrontXmlResponsesSaxParser())
                        .parseErrorResponse(httpMethod.getResponseBodyAsStream());
                    throw new CloudFrontServiceException("Request failed with CloudFront Service error",
                        responseCode, handler.getType(), handler.getCode(), 
                        handler.getMessage(), handler.getDetail(), 
                        handler.getRequestId());            
                } else {
                    throw new CloudFrontServiceException(
                        "Request failed with HTTP error " + responseCode + 
                        ":" + httpMethod.getStatusText());
                }
            }
        } catch (CloudFrontServiceException e) {
            throw e;
        } catch (Throwable t) {
            throw new CloudFrontServiceException("CloudFront Request failed", t);
        }
    }
    
    
    public Distribution[] listDistributions() throws CloudFrontServiceException {
        if (log.isDebugEnabled()) {
            log.debug("Listing distributions for AWS user: " + getAWSCredentials().getAccessKey());
        }
        try {            
            List distributions = new ArrayList();
            boolean incompleteListing = true;
            do {
                HttpMethod httpMethod = new GetMethod(ENDPOINT + VERSION + "/distribution");            
                performRestRequest(httpMethod, 200);

                ListDistributionListHandler handler = (new CloudFrontXmlResponsesSaxParser())
                    .parseDistributionListResponse(httpMethod.getResponseBodyAsStream());
                distributions.addAll(handler.getDistributions());
                
                incompleteListing = handler.isTruncated();
                // TODO: Under what circumstances are IsTruncated and Marker elements used?           
            } while (incompleteListing);
            
            return (Distribution[]) distributions.toArray(new Distribution[distributions.size()]);            
        } catch (Exception e) {
            throw new CloudFrontServiceException(e);
        }        
    }
    
    public Distribution createDistribution(String origin, String callerReference, 
        String[] cnames, String comment, boolean enabled) throws CloudFrontServiceException 
    {
        if (log.isDebugEnabled()) {
            log.debug("Creating distribution for origin: " + origin);
        }
        PostMethod httpMethod = new PostMethod(ENDPOINT + VERSION + "/distribution");
                
        String xmlRequest = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +            
            "<DistributionConfig xmlns=\"" + XML_NAMESPACE + "\">" + 
                "<Origin>" + origin + "</Origin>" +
                "<CallerReference>" + callerReference + "</CallerReference>";
        for (int i = 0; i < cnames.length; i++) {
            xmlRequest += "<CNAME>" + cnames[i] + "</CNAME>";
        }
        xmlRequest += 
                "<Comment>" + comment + "</Comment>" +
                "<Enabled>" + enabled + "</Enabled>" +
            "</DistributionConfig>";
                
        try {
            httpMethod.setRequestEntity(
                new StringRequestEntity(xmlRequest, "text/xml", Constants.DEFAULT_ENCODING));

            performRestRequest(httpMethod, 201);

            DistributionHandler handler = (new CloudFrontXmlResponsesSaxParser())
                .parseDistributionResponse(httpMethod.getResponseBodyAsStream());
            
            return handler.getDistribution();
        } catch (Exception e) {
            throw new CloudFrontServiceException(e);
        }                
    }
    
    public Distribution getDistributionInfo(String id) throws CloudFrontServiceException {
        if (log.isDebugEnabled()) {
            log.debug("Getting information for distribution with id: " + id);
        }        
        GetMethod httpMethod = new GetMethod(ENDPOINT + VERSION + "/distribution/" + id);
        
        try {
            performRestRequest(httpMethod, 200);

            DistributionHandler handler = (new CloudFrontXmlResponsesSaxParser())
                .parseDistributionResponse(httpMethod.getResponseBodyAsStream());
            
            return handler.getDistribution();
        } catch (Exception e) {
            throw new CloudFrontServiceException(e);
        }                        
    }
    
    public DistributionConfig getDistributionConfig(String id) throws CloudFrontServiceException {
        if (log.isDebugEnabled()) {
            log.debug("Getting configuration for distribution with id: " + id);
        }        
        GetMethod httpMethod = new GetMethod(ENDPOINT + VERSION + "/distribution/" + id + "/config");
        
        try {
            performRestRequest(httpMethod, 200);

            DistributionConfigHandler handler = (new CloudFrontXmlResponsesSaxParser())
                .parseDistributionConfigResponse(httpMethod.getResponseBodyAsStream());
            
            DistributionConfig config = handler.getDistributionConfig();
            config.setEtag(httpMethod.getResponseHeader("ETag").getValue());            
            return config; 
        } catch (Exception e) {
            throw new CloudFrontServiceException(e);
        }                        
    }

    public DistributionConfig updateDistributionConfig(
        String id, String[] cnames, String comment, boolean enabled) throws CloudFrontServiceException 
    {
        if (log.isDebugEnabled()) {
            log.debug("Setting configuration of distribution with id: " + id);
        }
        // Retrieve the old configuration.
        DistributionConfig oldConfig = getDistributionConfig(id);
        
        PutMethod httpMethod = new PutMethod(ENDPOINT + VERSION + "/distribution/" + id + "/config");
                
        String xmlRequest = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +            
            "<DistributionConfig xmlns=\"" + XML_NAMESPACE + "\">" + 
                "<Origin>" + oldConfig.getOrigin() + "</Origin>" +
                "<CallerReference>" + oldConfig.getCallerReference() + "</CallerReference>"; 
            for (int i = 0; i < cnames.length; i++) {
                xmlRequest += "<CNAME>" + cnames[i] + "</CNAME>";
            }
            xmlRequest += 
                "<Comment>" + comment + "</Comment>" +
                "<Enabled>" + enabled + "</Enabled>" +
            "</DistributionConfig>";
                
        try {
            httpMethod.setRequestEntity(
                new StringRequestEntity(xmlRequest, "text/xml", Constants.DEFAULT_ENCODING));
            httpMethod.setRequestHeader("If-Match", oldConfig.getEtag());

            performRestRequest(httpMethod, 200);

            DistributionConfigHandler handler = (new CloudFrontXmlResponsesSaxParser())
                .parseDistributionConfigResponse(httpMethod.getResponseBodyAsStream());

            DistributionConfig config = handler.getDistributionConfig();
            config.setEtag(httpMethod.getResponseHeader("ETag").getValue());            
            return config; 
        } catch (Exception e) {
            throw new CloudFrontServiceException(e);
        }                
    }

    public void deleteDistribution(String id) throws CloudFrontServiceException 
    {
        if (log.isDebugEnabled()) {
            log.debug("Deleting distribution with id: " + id);
        }
        
        // Get the distribution's current config.
        DistributionConfig currentConfig = getDistributionConfig(id);

        DeleteMethod httpMethod = new DeleteMethod(ENDPOINT + VERSION + "/distribution/" + id);
                
        try {
            httpMethod.setRequestHeader("If-Match", currentConfig.getEtag());
            performRestRequest(httpMethod, 204);
        } catch (Exception e) {
            throw new CloudFrontServiceException(e);
        }                
    }
    
}
