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

import com.jamesmurty.utils.XMLBuilder;

/**
 * A service that handles communication with the Amazon CloudFront REST API, offering 
 * all the operations that can be performed on CloudFront distributions.
 * <p>
 * This class uses properties obtained through {@link Jets3tProperties}. For more information on 
 * these properties please refer to 
 * <a href="http://jets3t.s3.amazonaws.com/toolkit/configuration.html">JetS3t Configuration</a>
 * </p>
 * 
 * @author James Murty
 */
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
    protected int internalErrorRetryMax = 5;


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

    /**
     * Constructs the service and initialises its properties.
     * 
     * @param awsCredentials
     * the AWS user credentials to use when communicating with CloudFront
     * @param invokingApplicationDescription
     * a short description of the application using the service, suitable for inclusion in a
     * user agent string for REST/HTTP requests. Ideally this would include the application's
     * version number, for example: <code>Cockpit/0.6.1</code> or <code>My App Name/1.0</code>.
     * May be null.
     * @param credentialsProvider
     * an implementation of the HttpClient CredentialsProvider interface, to provide a means for
     * prompting for credentials when necessary. May be null.
     * @param jets3tProperties
     * JetS3t properties that will be applied within this service. May be null.
     * @param hostConfig
     * Custom HTTP host configuration; e.g to register a custom Protocol Socket Factory.
     * May be null.
     * 
     * @throws CloudFrontServiceException
     */
    public CloudFrontService(AWSCredentials awsCredentials, String invokingApplicationDescription, 
        CredentialsProvider credentialsProvider, Jets3tProperties jets3tProperties,
        HostConfiguration hostConfig) throws CloudFrontServiceException 
    {
        this.awsCredentials = awsCredentials;        
        this.invokingApplicationDescription = invokingApplicationDescription;        
        this.credentialsProvider = credentialsProvider;
        if (jets3tProperties == null) {
            jets3tProperties = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME);
        }
        this.jets3tProperties = jets3tProperties;
        
        // Configure the InetAddress DNS caching times to work well with CloudFront. The cached DNS will
        // timeout after 5 minutes, while failed DNS lookups will be retried after 1 second.
        System.setProperty("networkaddress.cache.ttl", "300");
        System.setProperty("networkaddress.cache.negative.ttl", "1");
                
        this.internalErrorRetryMax = jets3tProperties.getIntProperty("cloudfront-service.internal-error-retry-max", 5);

        if (hostConfig == null) {
            hostConfig = new HostConfiguration();
        }        
        
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
     * Constructs the service with default properties.
     * 
     * @param awsCredentials
     * the AWS user credentials to use when communicating with CloudFront
     * 
     * @throws CloudFrontServiceException
     */
    public CloudFrontService(AWSCredentials awsCredentials) throws CloudFrontServiceException 
    {
        this(awsCredentials, null, null, null, null);
    }

    /**
     * @return the AWS Credentials identifying the AWS user.
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

    /**
     * Performs an HTTP/S request by invoking the provided HttpMethod object. If the HTTP
     * response code doesn't match the expected value, an exception is thrown.
     * 
     * @param httpMethod
     * the object containing a request target and all other information necessary to 
     * perform the request
     * @param expectedResponseCode
     * the HTTP response code that indicates a successful request. If the response code received
     * does not match this value an error must have occurred, so an exception is thrown.
     * @throws CloudFrontServiceException
     * all exceptions are wrapped in a CloudFrontServiceException. Depending on the kind of error that 
     * occurred, this exception may contain additional error information available from an XML
     * error response document.  
     */
    protected void performRestRequest(HttpMethod httpMethod, int expectedResponseCode) 
        throws CloudFrontServiceException 
    {
        // Set mandatory Request headers.
        if (httpMethod.getRequestHeader("Date") == null) {
            httpMethod.setRequestHeader("Date", ServiceUtils.formatRfc822Date(
                getCurrentTimeWithOffset()));
        }

        boolean completedWithoutRecoverableError = true;
        int internalErrorCount = 0;

        try {
            do {
                completedWithoutRecoverableError = true;
                authorizeHttpRequest(httpMethod);
                int responseCode = httpClient.executeMethod(httpMethod);
        
                if (responseCode != expectedResponseCode) {
                    if (responseCode == 500) {
                        // Retry on Internal Server errors, up to the defined limit.
                        long delayMs = 1000;
                        if (++internalErrorCount < this.internalErrorRetryMax) {
                            log.warn("Encountered " + internalErrorCount + 
                                " CloudFront Internal Server error(s), will retry in " + delayMs + "ms");
                            Thread.sleep(delayMs);
                            completedWithoutRecoverableError = false;
                        } else {
                            throw new CloudFrontServiceException("Encountered too many CloudFront Internal Server errors (" 
                                + internalErrorCount + "), aborting request.");                            
                        }
                    } else {
                        // Parse XML error message.
                        ErrorHandler handler = (new CloudFrontXmlResponsesSaxParser())
                            .parseErrorResponse(httpMethod.getResponseBodyAsStream());
                            
                        CloudFrontServiceException exception = new CloudFrontServiceException(
                            "Request failed with CloudFront Service error",
                            responseCode, handler.getType(), handler.getCode(), 
                            handler.getMessage(), handler.getDetail(), 
                            handler.getRequestId());
                        
                        if ("RequestExpired".equals(exception.getErrorCode())) {
                            // Retry on time skew errors.
                            this.timeOffset = RestUtils.getAWSTimeAdjustment();
                            if (log.isWarnEnabled()) {
                                log.warn("Adjusted time offset in response to RequestExpired error. " 
                                    + "Local machine and CloudFront server disagree on the time by approximately " 
                                    + (this.timeOffset / 1000) + " seconds. Retrying connection.");
                            }
                            completedWithoutRecoverableError = false;
                        } else {
                            throw exception;
                        }
                    }  
                } // End responseCode check
            } while (!completedWithoutRecoverableError);
        } catch (CloudFrontServiceException e) {
            httpMethod.releaseConnection();
            throw e;
        } catch (Throwable t) {
            httpMethod.releaseConnection();
            throw new CloudFrontServiceException("CloudFront Request failed", t);
        }
    }
        
    /**
     * List all your CloudFront distributions.
     *  
     * @return
     * a list of your distributions.
     * 
     * @throws CloudFrontServiceException
     */
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
        } catch (CloudFrontServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new CloudFrontServiceException(e);
        }        
    }
    
    /**
     * List the distributions for a given S3 bucket name, if any.
     * 
     * @param bucketName
     * the name of the S3 bucket whose distributions will be returned.
     * @return
     * a list of distributions applied to the given S3 bucket, or an empty list
     * if there are no such distributions.
     * 
     * @throws CloudFrontServiceException
     */
    public Distribution[] listDistributions(String bucketName) throws CloudFrontServiceException {
        if (log.isDebugEnabled()) {
            log.debug("Listing distributions for the S3 bucket '" + bucketName 
                + "' for AWS user: " + getAWSCredentials().getAccessKey());
        }
        ArrayList bucketDistributions = new ArrayList();
        Distribution[] allDistributions = listDistributions();
        for (int i = 0; i < allDistributions.length; i++) {
            String distributionOrigin = allDistributions[i].getOrigin(); 
            if (distributionOrigin.equals(bucketName) 
                || bucketName.equals(ServiceUtils.findBucketNameInHostname(distributionOrigin))) 
            {
                bucketDistributions.add(allDistributions[i]);
            }
        }
        return (Distribution[]) bucketDistributions.toArray(
            new Distribution[bucketDistributions.size()]);
    }
    
    /**
     * Create a CloudFront distribution for an S3 bucket that will be publicly
     * available once created.
     * 
     * @param origin
     * the Amazon S3 bucket to associate with the distribution.
     * 
     * @return
     * an object that describes the newly-created distribution, in particular the
     * distribution's identifier and domain name values. 
     * 
     * @throws CloudFrontServiceException
     */
    public Distribution createDistribution(String origin) throws CloudFrontServiceException 
    {
        return this.createDistribution(origin, null, null, null, true);
    }
    
    /**
     * Create a CloudFront distribution for an S3 bucket.
     * 
     * @param origin
     * the Amazon S3 bucket to associate with the distribution.
     * @param callerReference
     * A user-set unique reference value that ensures the request can't be replayed
     * (max UTF-8 encoding size 128 bytes). This parameter may be null, in which
     * case your computer's local epoch time in milliseconds will be used.
     * @param cnames
     * A list of up to 10 CNAME aliases to associate with the distribution. This 
     * parameter may be a null or empty array.
     * @param comment
     * An optional comment to describe the distribution in your own terms 
     * (max 128 characters). May be null. 
     * @param enabled
     * Should the distribution should be enabled and publicly accessible upon creation?
     * 
     * @return
     * an object that describes the newly-created distribution, in particular the
     * distribution's identifier and domain name values. 
     * 
     * @throws CloudFrontServiceException
     */
    public Distribution createDistribution(String origin, String callerReference, 
        String[] cnames, String comment, boolean enabled) throws CloudFrontServiceException 
    {
        if (log.isDebugEnabled()) {
            log.debug("Creating distribution for origin: " + origin);
        }
        
        // Sanitize parameters.
        if (callerReference == null) {
            callerReference = "" + System.currentTimeMillis();
        }
        if (cnames == null) {
            cnames = new String[] {};
        }
        if (comment == null) {
            comment = "";
        }
        
        PostMethod httpMethod = new PostMethod(ENDPOINT + VERSION + "/distribution");
                
        try {
            XMLBuilder builder = XMLBuilder.create("DistributionConfig")
                .a("xmlns", XML_NAMESPACE)
                .e("Origin").t(origin).up()
                .e("CallerReference").t(callerReference).up();
            for (int i = 0; i < cnames.length; i++) {
                builder.e("CNAME").t(cnames[i]).up();
            }
            builder
                .e("Comment").t(comment).up()
                .e("Enabled").t("" + enabled);

            httpMethod.setRequestEntity(
                new StringRequestEntity(builder.asString(null), "text/xml", Constants.DEFAULT_ENCODING));

            performRestRequest(httpMethod, 201);

            DistributionHandler handler = (new CloudFrontXmlResponsesSaxParser())
                .parseDistributionResponse(httpMethod.getResponseBodyAsStream());
            
            return handler.getDistribution();
        } catch (CloudFrontServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new CloudFrontServiceException(e);
        }                
    }
    
    /**
     * Lookup information for a distribution.
     * 
     * @param id
     * the distribution's unique identifier.
     * 
     * @return
     * an object that describes the distribution, including its identifier and domain 
     * name values as well as its configuration details. 
     * 
     * @throws CloudFrontServiceException
     */
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
        } catch (CloudFrontServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new CloudFrontServiceException(e);
        }                        
    }
    
    /**
     * Lookup configuration information for a distribution. The configuration information
     * is a subset of the information available from the {@link #getDistributionInfo(String)}
     * method.
     * 
     * @param id
     * the distribution's unique identifier.
     * 
     * @return
     * an object that describes the distribution's configuration, including its origin bucket
     * and CNAME aliases. 
     * 
     * @throws CloudFrontServiceException
     */
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
        } catch (CloudFrontServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new CloudFrontServiceException(e);
        }                        
    }

    /**
     * Update the configuration of an existing distribution to change its CNAME aliases, 
     * comment or enabled status. The new configuration settings <strong>replace</strong>
     * the existing configuration, and may take some time to be fully applied. 
     * <p>
     * This method performs all the steps necessary to update the configuration. It
     * first performs lookup on the distribution  using 
     * {@link #getDistributionConfig(String)} to find its origin and caller reference
     * values, then uses this information to apply your configuration changes.
     *  
     * @param id
     * the distribution's unique identifier.
     * @param cnames
     * A list of up to 10 CNAME aliases to associate with the distribution. This 
     * parameter may be null, in which case the original CNAME aliases are retained.
     * @param comment
     * An optional comment to describe the distribution in your own terms 
     * (max 128 characters). May be null, in which case the original comment is retained.
     * @param enabled
     * Should the distribution should be enabled and publicly accessible after the
     * configuration update?
     * 
     * @return
     * an object that describes the distribution's updated configuration, including its 
     * origin bucket and CNAME aliases.
     *  
     * @throws CloudFrontServiceException
     */
    public DistributionConfig updateDistributionConfig(String id, String[] cnames, 
        String comment, boolean enabled) throws CloudFrontServiceException 
    {
        if (log.isDebugEnabled()) {
            log.debug("Setting configuration of distribution with id: " + id);
        }

        // Retrieve the old configuration.
        DistributionConfig oldConfig = getDistributionConfig(id);
        
        // Sanitize parameters.
        if (cnames == null) {
            cnames = oldConfig.getCNAMEs();
        }
        if (comment == null) {
            comment = oldConfig.getComment();
        }        
        
        PutMethod httpMethod = new PutMethod(ENDPOINT + VERSION + "/distribution/" + id + "/config");
                
        try {
            XMLBuilder builder = XMLBuilder.create("DistributionConfig")
                .a("xmlns", XML_NAMESPACE)
                .e("Origin").t(oldConfig.getOrigin()).up()
                .e("CallerReference").t(oldConfig.getCallerReference()).up();
            for (int i = 0; i < cnames.length; i++) {
                builder.e("CNAME").t(cnames[i]).up();
            }
            builder
                .e("Comment").t(comment).up()
                .e("Enabled").t("" + enabled);
            
            httpMethod.setRequestEntity(
                new StringRequestEntity(builder.asString(null), "text/xml", Constants.DEFAULT_ENCODING));
            httpMethod.setRequestHeader("If-Match", oldConfig.getEtag());

            performRestRequest(httpMethod, 200);

            DistributionConfigHandler handler = (new CloudFrontXmlResponsesSaxParser())
                .parseDistributionConfigResponse(httpMethod.getResponseBodyAsStream());

            DistributionConfig config = handler.getDistributionConfig();
            config.setEtag(httpMethod.getResponseHeader("ETag").getValue());            
            return config; 
        } catch (CloudFrontServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new CloudFrontServiceException(e);
        }                
    }

    /**
     * Delete a disabled distribution. You can only delete a distribution that is 
     * already disabled, if you delete an enabled distribution this operation will 
     * fail with a <tt>DistributionNotDisabled</tt> error.
     * <p>
     * This method performs many of the steps necessary to delete a disabled
     * distribution. It first performs lookup on the distribution using 
     * {@link #getDistributionConfig(String)} to find its ETag value, then uses 
     * this information to delete the distribution.
     * <p>
     * Because it can take a long time (minutes) to disable a distribution, this
     * task is not performed automatically by this method. In your own code, you
     * need to verify that a distribution is disabled with a status of 
     * <tt>Deployed</tt> before you invoke this method.
     * 
     * @param id
     * the distribution's unique identifier.
     * 
     * @throws CloudFrontServiceException
     */
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
        } catch (CloudFrontServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new CloudFrontServiceException(e);
        }                
    }
    
}
