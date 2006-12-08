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
package org.jets3t.servlets.gatekeeper;

import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.jets3t.service.S3ServiceException;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.utils.gatekeeper.SignatureRequest;

public abstract class UrlSigner {
    protected AWSCredentials awsCredentials = null;
    protected String urlPrefix = null;
    
    public UrlSigner(ServletConfig servletConfig) throws ServletException {
        String awsAccessKey = servletConfig.getInitParameter("AwsAccessKey");
        String awsSecretKey = servletConfig.getInitParameter("AwsSecretKey");
        String urlPrefix = servletConfig.getInitParameter("UrlPrefix");

        // Fail with an exception if required init params are missing.
        boolean missingInitParam = false;
        String errorMessage = "Missing required servlet init parameters for UrlSigner: ";
        if (awsAccessKey == null || awsAccessKey.length() == 0) {
            errorMessage += "AwsAccessKey ";
            missingInitParam = true;
        }
        if (awsSecretKey == null || awsSecretKey.length() == 0) {
            errorMessage += "AwsSecretKey ";
            missingInitParam = true;
        }
        if (urlPrefix == null || urlPrefix.length() == 0) {
            errorMessage += "UrlPrefix ";
            missingInitParam = true;
        }
        if (missingInitParam) {
            throw new ServletException(errorMessage);
        }        
        
        this.awsCredentials = new AWSCredentials(awsAccessKey, awsSecretKey);
        this.urlPrefix = urlPrefix;        
    }

    public abstract String signGet(Properties applicationProperties, Properties messageProperties,
        ClientInformation clientInformation, SignatureRequest signatureRequest)
        throws S3ServiceException;

    public abstract String signHead(Properties applicationProperties, Properties messageProperties, 
        ClientInformation clientInformation, SignatureRequest signatureRequest)
        throws S3ServiceException;

    public abstract String signPut(Properties applicationProperties, Properties messageProperties, 
        ClientInformation clientInformation, SignatureRequest signatureRequest)
        throws S3ServiceException;

    public abstract String signDelete(Properties applicationProperties,  Properties messageProperties,
        ClientInformation clientInformation, SignatureRequest signatureRequest)
        throws S3ServiceException;

}
