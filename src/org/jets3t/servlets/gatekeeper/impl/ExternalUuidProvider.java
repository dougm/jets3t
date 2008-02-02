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
package org.jets3t.servlets.gatekeeper.impl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.jets3t.service.utils.gatekeeper.GatekeeperMessage;
import org.jets3t.servlets.gatekeeper.ClientInformation;
import org.jets3t.servlets.gatekeeper.TransactionIdProvider;
import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;

/**
 * Uses an externally provided UUID obtained from an application property <tt>externalUUID</tt>
 * when this is available. If not, a random-based UUID is created.
 *  
 * @author James Murty
 */
public class ExternalUuidProvider extends TransactionIdProvider {

    public ExternalUuidProvider(ServletConfig servletConfig) throws ServletException {
        super(servletConfig);
    }

    /**
     * If the application property <tt>externalUUID</tt> is available, the value of this property
     * is returned as the transaction ID. If the property is not availble a new random-based 
     * UUID is generated using the JUG library.
     */
    public String getTransactionId(GatekeeperMessage requestMessage, ClientInformation clientInformation)
    {
        // Use the external UUID as a transaction ID, if it's available.
        String externalUuid = requestMessage.getApplicationProperties().getProperty("externalUUID");
        if (externalUuid != null && externalUuid.length() > 0) {
            return externalUuid;            
        }
        // Use a generated UUID based on a random generation as a fallback if the external UUID isn't available.
        UUID uuid = UUIDGenerator.getInstance().generateRandomBasedUUID();        
        return uuid.toString();
    }

}
