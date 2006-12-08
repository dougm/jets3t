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

import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.utils.gatekeeper.GatekeeperMessage;
import org.jets3t.service.utils.gatekeeper.SignatureRequest;

public class RenameToUuidUrlSigner extends DefaultUrlSigner {
    private final static Log log = LogFactory.getLog(RenameToUuidUrlSigner.class);
    
    private int countOfRenamedObjects = 0;

    public RenameToUuidUrlSigner(ServletConfig servletConfig) throws ServletException {
        super(servletConfig);
    }
    
    protected void updateObject(SignatureRequest signatureRequest, Properties messageProperties)
        throws S3ServiceException 
    {
        super.updateObject(signatureRequest, messageProperties);
        
        String transactionId = messageProperties.getProperty(GatekeeperMessage.PROPERTY_TRANSACTION_ID);            
        Map objectMetadata = signatureRequest.getObjectMetadata();

System.err.println("=== transactionId=" + transactionId);

        if (!objectMetadata.containsKey("uploader-summary-xml") /*TODO*/ &&  transactionId != null) {
            String originalKey = signatureRequest.getObjectKey();
            
            String extension = null;
            int lastDotIndex = originalKey.lastIndexOf(".");
            if (lastDotIndex >= 0) {
                extension = originalKey.substring(lastDotIndex + 1);
            }
            
            String newKey = transactionId + "." + (++countOfRenamedObjects) + "." + extension;
System.err.println("=== Renamed " + originalKey + " to " + newKey);            
            signatureRequest.setObjectKey(newKey);            
        }

        if (!objectMetadata.containsKey(GatekeeperMessage.PROPERTY_TRANSACTION_ID)) {
            if (transactionId != null) {
                objectMetadata.put(GatekeeperMessage.PROPERTY_TRANSACTION_ID, transactionId);
            }
        }
    }
    
}
