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
package org.jets3t.servlets.gatekeeper.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.utils.gatekeeper.GatekeeperMessage;
import org.jets3t.service.utils.gatekeeper.SignatureRequest;
import org.jets3t.servlets.gatekeeper.ClientInformation;
import org.jets3t.servlets.gatekeeper.UrlSigner;

public class DefaultUrlSigner extends UrlSigner {
    private final static Log log = LogFactory.getLog(DefaultUrlSigner.class);
    private String s3BucketName = null;
    int secondsUntilExpiry = 0;

    public DefaultUrlSigner(ServletConfig servletConfig) throws ServletException {
        super(servletConfig);
        
        String secondsToSign = servletConfig.getInitParameter("SecondsToSign");
        if (secondsToSign == null || secondsToSign.length() == 0) {
            throw new ServletException("Missing required servlet init parameters for DefaultUrlSigner: "
                + "SecondsToSign");
        }
        try {
            secondsUntilExpiry = Integer.parseInt(secondsToSign);
        } catch (NumberFormatException e) {
            throw new ServletException("Invalid servlet init param: SecondsToSign", e);
        }

        s3BucketName = servletConfig.getInitParameter("S3BucketName");
        if (s3BucketName == null || s3BucketName.length() == 0) {
            throw new ServletException("Missing required servlet init parameters for DefaultUrlSigner: "
                + "S3BucketName");
        }
    }
    
    protected void updateObject(SignatureRequest signatureRequest, Properties messageProperties) 
        throws S3ServiceException 
    {
        signatureRequest.setBucketName(s3BucketName);

        Map objectMetadata = signatureRequest.getObjectMetadata();
        if (!objectMetadata.containsKey(GatekeeperMessage.PROPERTY_TRANSACTION_ID)) {
            String transactionId = 
                messageProperties.getProperty(GatekeeperMessage.PROPERTY_TRANSACTION_ID);
            if (transactionId != null) {
                objectMetadata.put(GatekeeperMessage.PROPERTY_TRANSACTION_ID, transactionId);
            }
        }
    }
    
    private Date calculateExpiryTime() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, secondsUntilExpiry);
        log.debug("Expiry time for all signed URLs: " + cal.getTime());
        return cal.getTime();        
    }

    public String signDelete(Properties applicationProperties,  Properties messageProperties, 
        ClientInformation clientInformation,SignatureRequest signatureRequest) throws S3ServiceException
    {
        updateObject(signatureRequest, messageProperties);
        return S3Service.createSignedDeleteUrl(signatureRequest.getBucketName(), signatureRequest.getObjectKey(), 
            awsCredentials, calculateExpiryTime());
    }

    public String signGet(Properties applicationProperties,  Properties messageProperties, 
        ClientInformation clientInformation,SignatureRequest signatureRequest) throws S3ServiceException
    {
        updateObject(signatureRequest, messageProperties);
        return S3Service.createSignedGetUrl(signatureRequest.getBucketName(), signatureRequest.getObjectKey(), 
            awsCredentials, calculateExpiryTime());
    }

    public String signHead(Properties applicationProperties,  Properties messageProperties, 
        ClientInformation clientInformation,SignatureRequest signatureRequest) throws S3ServiceException
    {
        updateObject(signatureRequest, messageProperties);
        return S3Service.createSignedHeadUrl(signatureRequest.getBucketName(), signatureRequest.getObjectKey(), 
            awsCredentials, calculateExpiryTime());
    }

    public String signPut(Properties applicationProperties,  Properties messageProperties, 
        ClientInformation clientInformation,SignatureRequest signatureRequest) throws S3ServiceException
    {
        updateObject(signatureRequest, messageProperties);
        return S3Service.createSignedPutUrl(signatureRequest.getBucketName(), signatureRequest.getObjectKey(),
            signatureRequest.getObjectMetadata(), awsCredentials, calculateExpiryTime());
    }

}
