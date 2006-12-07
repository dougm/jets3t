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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.utils.gatekeeper.GatekeeperMessage;
import org.jets3t.service.utils.gatekeeper.SignatureRequest;
import org.jets3t.service.utils.signedurl.SignedUrlAndObject;
import org.jets3t.servlets.gatekeeper.impl.DefaultGatekeeper;
import org.jets3t.servlets.gatekeeper.impl.DefaultUrlSigner;

public class GatekeeperServlet extends HttpServlet {
    private final Log log = LogFactory.getLog(GatekeeperServlet.class);
    
    private Gatekeeper gatekeeper = null;
    private UrlSigner urlSigner = null;


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

        String gatekeeperClass = servletConfig.getInitParameter("GatekeeperClass");
        log.debug("GatekeeperClass: " + gatekeeperClass);        
        if (gatekeeperClass != null) {
            log.info("Loading Gatekeeper implementation class: " + gatekeeperClass);
            gatekeeper = (Gatekeeper) instantiateClass(gatekeeperClass, 
                new Class[] {}, new Object[] {});
        }
        if (gatekeeper == null) {
            gatekeeper = new DefaultGatekeeper();            
            log.info("Loaded default Gatekeeper implementation class: " 
                + gatekeeper.getClass().getName());
        }

        String urlSignerClass = servletConfig.getInitParameter("UrlSignerClass");
        log.debug("UrlSignerClass: " + urlSignerClass);        
        if (urlSignerClass != null) {
            log.info("Loading UrlSigner implementation class: " + urlSignerClass);
            urlSigner = (UrlSigner) instantiateClass(urlSignerClass,
                new Class[] {ServletConfig.class}, new Object[] {servletConfig});
        }
        if (urlSigner == null) {
            urlSigner = new DefaultUrlSigner(servletConfig);            
            log.info("Loaded default UrlSigner implementation class: " 
                + urlSigner.getClass().getName());
        }
    }
    
    // TODO
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
    {
        String userAgent =  req.getHeader("user-agent");
        String clientBrowser =  "Not known!";   
        if( userAgent != null)
            clientBrowser = userAgent;
        req.setAttribute("client.browser",clientBrowser );
        req.getRequestDispatcher("/showBrowser.jsp").forward(req,resp);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("Handling POST request");
        try {
        
            // Build Gatekeeper request
System.err.println("=== request.getParameterMap()=" + request.getParameterMap());
            GatekeeperMessage gatekeeperMessage = 
                GatekeeperMessage.decodeFromProperties(request.getParameterMap());
                    
            // TODO Obtain client information
            ClientInformation clientInformation = null;
            
            // Process each object request.
            for (int i = 0; i < gatekeeperMessage.getSignatureRequests().length; i++) {
                SignatureRequest signatureRequest = (SignatureRequest) gatekeeperMessage.getSignatureRequests()[i];
                boolean allowed = gatekeeper.allowPut(gatekeeperMessage.getApplicationProperties(),
                    clientInformation, signatureRequest);
                
                if (allowed) {
                    String signedUrl = null;
                    if (SignatureRequest.SIGNATURE_TYPE_GET.equals(signatureRequest.getSignatureType())) {
                        signedUrl = urlSigner.signGet(gatekeeperMessage.getApplicationProperties(), 
                            clientInformation, signatureRequest);                        
                    } else if (SignatureRequest.SIGNATURE_TYPE_HEAD.equals(signatureRequest.getSignatureType())) {
                        signedUrl = urlSigner.signHead(gatekeeperMessage.getApplicationProperties(), 
                            clientInformation, signatureRequest);
                    } else if (SignatureRequest.SIGNATURE_TYPE_PUT.equals(signatureRequest.getSignatureType())) {
                        signedUrl = urlSigner.signPut(gatekeeperMessage.getApplicationProperties(), 
                            clientInformation, signatureRequest);
                    } else if (SignatureRequest.SIGNATURE_TYPE_DELETE.equals(signatureRequest.getSignatureType())) {
                        signedUrl = urlSigner.signDelete(gatekeeperMessage.getApplicationProperties(), 
                            clientInformation, signatureRequest);                        
                    }
                    signatureRequest.signRequest(signedUrl);
                }                 
            }
            
            // Build response.
            Properties responseProperties = gatekeeperMessage.encodeToProperties();
System.out.println("=== POST Properties before response: " + responseProperties);
            
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            responseProperties.store(baos, "");
            
            response.setStatus(200);                
            response.getOutputStream().write(baos.toByteArray());
        } catch (Exception e) {
            log.error("Failed to sign request", e);  
            response.setStatus(500);
            response.getWriter().println(e.toString());
        }

    }
    
}
