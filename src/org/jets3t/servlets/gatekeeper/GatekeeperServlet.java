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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.utils.gatekeeper.GatekeeperMessage;
import org.jets3t.service.utils.gatekeeper.SignatureRequest;
import org.jets3t.servlets.gatekeeper.impl.DefaultGatekeeper;
import org.jets3t.servlets.gatekeeper.impl.DefaultTransactionIdProvider;
import org.jets3t.servlets.gatekeeper.impl.DefaultUrlSigner;

public class GatekeeperServlet extends HttpServlet {
    private final Log log = LogFactory.getLog(GatekeeperServlet.class);
    
    private ServletConfig servletConfig = null;

    private Object instantiateClass(String className, Class[] constructorParamClasses, 
        Object[] constructorParams)
    {
        try {
            Class myClass = Class.forName(className);
            Constructor constructor = myClass.getConstructor(constructorParamClasses);
            Object instance = constructor.newInstance(constructorParams);
            return instance;
        } catch (ClassNotFoundException e) { 
            log.debug("Class does not exist for name: " + className);
        } catch (Exception e) {
            log.warn("Unable to instantiate class '" + className + "'", e);
        }
        return null;
    }
    
    public void init(ServletConfig servletConfig) throws ServletException {
        log.info("Initialising GatekeeperServlet");
        this.servletConfig = servletConfig;
    }

    private Gatekeeper initGatekeeper() throws ServletException {
        String gatekeeperClass = servletConfig.getInitParameter("GatekeeperClass");
        log.debug("GatekeeperClass: " + gatekeeperClass);        
        if (gatekeeperClass != null) {
            log.info("Loading Gatekeeper implementation class: " + gatekeeperClass);
            return (Gatekeeper) instantiateClass(gatekeeperClass, 
                new Class[] {ServletConfig.class}, new Object[] {servletConfig});
        }
        log.info("Loaded default Gatekeeper implementation class: " 
            + DefaultGatekeeper.class.getName());
        return new DefaultGatekeeper(servletConfig);
    }

    private UrlSigner initUrlSigner() throws ServletException {
        String urlSignerClass = servletConfig.getInitParameter("UrlSignerClass");
        log.debug("UrlSignerClass: " + urlSignerClass);        
        if (urlSignerClass != null) {
            log.info("Loading UrlSigner implementation class: " + urlSignerClass);
            return (UrlSigner) instantiateClass(urlSignerClass,
                new Class[] {ServletConfig.class}, new Object[] {servletConfig});
        }
        log.info("Loaded default UrlSigner implementation class: " 
            + DefaultUrlSigner.class.getName());
        return new DefaultUrlSigner(servletConfig);            
    }

    private TransactionIdProvider initTransactionIdProvider() throws ServletException {
        String transactionIdProviderClass = servletConfig.getInitParameter("TransactionIdProviderClass");
        log.debug("TransactionIdProviderClass: " + transactionIdProviderClass);        
        if (transactionIdProviderClass != null) {
            log.info("Loading TransactionIdProvider implementation class: " + transactionIdProviderClass);
            return (TransactionIdProvider) instantiateClass(transactionIdProviderClass,
                new Class[] {ServletConfig.class}, new Object[] {servletConfig});
        }
        log.info("Loaded default TransactionIdProvider implementation class: " 
            + TransactionIdProvider.class.getName());
        return new DefaultTransactionIdProvider(servletConfig);            
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("Handling POST request");
        try {
            
            // Initialise required classes.
            TransactionIdProvider transactionIdProvider = initTransactionIdProvider();
            UrlSigner urlSigner = initUrlSigner();
            Gatekeeper gatekeeper = initGatekeeper();
        
            // Build Gatekeeper request from POST form parameters.
            GatekeeperMessage gatekeeperMessage = 
                GatekeeperMessage.decodeFromProperties(request.getParameterMap());
                    
            // Obtain client information
            ClientInformation clientInformation = new ClientInformation(
                request.getRemoteAddr(), request.getRemoteHost(), request.getRemoteUser(),
                request.getRemotePort(), request.getSession(false), request.getUserPrincipal(),
                request.getHeader("User-Agent"));
            
            // Generate Transaction ID, and store it in the message.
            String transactionId = transactionIdProvider.getTransactionId(
                gatekeeperMessage.getApplicationProperties(), 
                gatekeeperMessage.getMessageProperties(), clientInformation);
            if (transactionId != null) {
                gatekeeperMessage.addMessageProperty(GatekeeperMessage.PROPERTY_TRANSACTION_ID, transactionId);
            }
            
            // Process each signature request.
            for (int i = 0; i < gatekeeperMessage.getSignatureRequests().length; i++) {
                SignatureRequest signatureRequest = (SignatureRequest) gatekeeperMessage.getSignatureRequests()[i];
                
                // Determine whether the request will be allowed. If the request is not allowed, the
                // reason will be made available in the signature request object (with signatureRequest.declineRequest())
                boolean allowed = gatekeeper.allowSignatureRequest(gatekeeperMessage.getApplicationProperties(),
                    gatekeeperMessage.getMessageProperties(), clientInformation, signatureRequest);

                // Sign requests when they are allowed. When a request is signed, the signed URL is made available
                // in the SignatureRequest object.
                if (allowed) {
                    String signedUrl = null;
                    if (SignatureRequest.SIGNATURE_TYPE_GET.equals(signatureRequest.getSignatureType())) {
                        signedUrl = urlSigner.signGet(gatekeeperMessage.getApplicationProperties(), 
                            gatekeeperMessage.getMessageProperties(), clientInformation, signatureRequest);                        
                    } else if (SignatureRequest.SIGNATURE_TYPE_HEAD.equals(signatureRequest.getSignatureType())) {
                        signedUrl = urlSigner.signHead(gatekeeperMessage.getApplicationProperties(), 
                            gatekeeperMessage.getMessageProperties(), clientInformation, signatureRequest);
                    } else if (SignatureRequest.SIGNATURE_TYPE_PUT.equals(signatureRequest.getSignatureType())) {
                        signedUrl = urlSigner.signPut(gatekeeperMessage.getApplicationProperties(), 
                            gatekeeperMessage.getMessageProperties(), clientInformation, signatureRequest);
                    } else if (SignatureRequest.SIGNATURE_TYPE_DELETE.equals(signatureRequest.getSignatureType())) {
                        signedUrl = urlSigner.signDelete(gatekeeperMessage.getApplicationProperties(), 
                            gatekeeperMessage.getMessageProperties(), clientInformation, signatureRequest);                        
                    }
                    signatureRequest.signRequest(signedUrl);
                }                 
            }
                        
            // Build response as a set of properties, and return this document.
            Properties responseProperties = gatekeeperMessage.encodeToProperties();
            log.debug("Sending response message as properties: " + responseProperties);            
            
            // Serialize properties to bytes. 
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            responseProperties.store(baos, "");
            
            // Send successful response.
            response.setStatus(200);                
            response.setContentType(GatekeeperMessage.CONTENT_TYPE);
            response.getOutputStream().write(baos.toByteArray());
        } catch (Exception e) {
            log.error("Gatekeeper failed to send valid response", e);  
            response.setStatus(500);
            response.setContentType("text/plain");
            response.getWriter().println(e.toString());
        }
    }
    
}
