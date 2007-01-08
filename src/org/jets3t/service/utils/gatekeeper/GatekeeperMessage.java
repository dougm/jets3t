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
package org.jets3t.service.utils.gatekeeper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 
 * @author James Murty
 */
public class GatekeeperMessage {
    private static final Log log = LogFactory.getLog(GatekeeperMessage.class);
    
    private static final String DELIM = "|";
    
    public static final String PROPERTY_TRANSACTION_ID = "transactionId";
    public static final String SUMMARY_DOCUMENT_METADATA_FLAG = "jets3t-uploader-summary-doc";
    
    private Properties applicationProperties = new Properties();
    private Properties messageProperties = new Properties(); 
    private List signatureRequestList = new ArrayList();
    
    
    public GatekeeperMessage() {        
    }
    
    public void addSignatureRequest(SignatureRequest signatureRequest) {
        signatureRequestList.add(signatureRequest);
    }
    
    public void addSignatureRequests(SignatureRequest[] signatureRequests) {
        for (int i = 0; i < signatureRequests.length; i++) {
            addSignatureRequest(signatureRequests[i]);
        }
    }
    
    public SignatureRequest[] getSignatureRequests() {
        return (SignatureRequest[]) signatureRequestList.toArray(new SignatureRequest[] {});
    }
    
    public void addApplicationProperty(String propertyName, String propertyValue) {
        applicationProperties.put(propertyName, propertyValue);
    }
    
    public void addApplicationProperties(Map propertiesMap) {
        applicationProperties.putAll(propertiesMap);
    }
    
    public Properties getApplicationProperties() {
        return applicationProperties;
    }
    
    public void addMessageProperty(String propertyName, String propertyValue) {
        messageProperties.put(propertyName, propertyValue);
    }
    
    public void addMessageProperties(Map propertiesMap) {
        messageProperties.putAll(propertiesMap);
    }
        
    public Properties getMessageProperties() {
        return messageProperties;
    }

    private void encodeProperty(Properties properties, String propertyName, Object value) {
        if (value != null && (value instanceof String)) {
            log.debug("Encoding property: " + propertyName + "=" + value);
            properties.put(propertyName, value);
        }        
    }
        
    public Properties encodeToProperties() {
        log.debug("Encoding GatekeeperMessage to properties");
        
        Properties encodedProperties = new Properties();
        Iterator iter = null;
        
        String prefix = "application";
        iter = applicationProperties.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            String value = applicationProperties.getProperty(key);
            encodeProperty(encodedProperties, prefix + DELIM + key, value);
        }
        
        prefix = "message";
        iter = messageProperties.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            String value = messageProperties.getProperty(key);
            encodeProperty(encodedProperties, prefix + DELIM + key, value);
        }
        
        prefix = "request";
        SignatureRequest[] requests = getSignatureRequests();
        for (int i = 0; i < requests.length; i++) {            
            SignatureRequest request = requests[i];
            String propertyPrefix = prefix + DELIM + i + DELIM;

            encodeProperty(encodedProperties, propertyPrefix + "signatureType", request.getSignatureType());
            encodeProperty(encodedProperties, propertyPrefix + "objectKey", request.getObjectKey());
            encodeProperty(encodedProperties, propertyPrefix + "bucketName", request.getBucketName());
            encodeProperty(encodedProperties, propertyPrefix + "signedUrl", request.getSignedUrl());
            encodeProperty(encodedProperties, propertyPrefix + "declineReason", request.getDeclineReason());
            
            propertyPrefix += "metadata" + DELIM;                
            Map metadata = request.getObjectMetadata();
            iter = metadata.keySet().iterator();
            while (iter.hasNext()) {
                String metadataName = iter.next().toString();
                Object metadataValue = metadata.get(metadataName);
                encodeProperty(encodedProperties, propertyPrefix + metadataName, metadataValue);                
            }
        }
        
        return encodedProperties;
    }
    
    public static GatekeeperMessage decodeFromProperties(Map postProperties) {
        log.debug("Decoding GatekeeperMessage from properties");
        
        GatekeeperMessage gatekeeperMessage = new GatekeeperMessage();
        
        Map signatureRequestMap = new HashMap();
                
        Iterator iter = postProperties.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            Object value = postProperties.get(key);
            String propertyValue = null;
            if (value instanceof String[]) {
                propertyValue = ((String[]) value)[0];
            } else {
                propertyValue = (String) value;
            }
            
            if (key.startsWith("application")) {
                String propertyName = key.substring(key.lastIndexOf(DELIM) + 1);
                gatekeeperMessage.addApplicationProperty(propertyName, propertyValue);
            } else if (key.startsWith("message")) {
                String propertyName = key.substring(key.lastIndexOf(DELIM) + 1);
                gatekeeperMessage.addMessageProperty(propertyName, propertyValue);                
            } else if (key.startsWith("request")) {
                StringTokenizer st = new StringTokenizer(key, DELIM);
                st.nextToken(); // Consume request prefix
                String objectIndexStr = st.nextToken();
                
                boolean isMetadata = false;
                String propertyName = st.nextToken();
                if (st.hasMoreTokens()) {
                    isMetadata = true;
                    propertyName = st.nextToken();
                }
                
                Integer objectIndex = Integer.valueOf(objectIndexStr);   
                SignatureRequest request = null;
                
                if (signatureRequestMap.containsKey(objectIndex)) {
                    request = (SignatureRequest) signatureRequestMap.get(objectIndex);
                } else {
                    request = new SignatureRequest();
                    signatureRequestMap.put(objectIndex, request);
                }
                
                if (isMetadata) {
                    request.addObjectMetadata(propertyName, propertyValue);
                } else {
                    if ("signatureType".equals(propertyName)) {
                        request.setSignatureType(propertyValue);
                    } else if ("objectKey".equals(propertyName)) {
                        request.setObjectKey(propertyValue);
                    } else if ("bucketName".equals(propertyName)) {
                        request.setBucketName(propertyValue);
                    } else if ("signedUrl".equals(propertyName)) {
                        request.signRequest(propertyValue);
                    } else if ("declineReason".equals(propertyName)) {
                        request.declineRequest(propertyValue);
                    } else {
                        log.warn("Ignoring unrecognised SignatureRequest property: " + propertyName);
                    }       
                }
            } else {
                log.warn("Ignoring unrecognised property name: " + key);
            }            
        }
        
        for (int i = 0; i < signatureRequestMap.size(); i++) {
            Integer objectIndex = new Integer(i);
            SignatureRequest request = (SignatureRequest) signatureRequestMap.get(objectIndex);
            gatekeeperMessage.addSignatureRequest(request);
        }                
        
        return gatekeeperMessage;
    }
    

    
    // TODO Remove after testing...
    public static void main(String[] args) {        
        SignatureRequest requests[] = new SignatureRequest[12];
        for (int i = 0; i < requests.length; i++) {
            requests[i] = new SignatureRequest(SignatureRequest.SIGNATURE_TYPE_PUT, "Request " + i);
            requests[i].addObjectMetadata("object-index", String.valueOf(i));
        }
        
        GatekeeperMessage request = new GatekeeperMessage();
        request.addSignatureRequests(requests);
        request.addMessageProperty("id", "123");
        request.addMessageProperty("date", (new Date()).toString());
        request.addApplicationProperty("username", "jmurty");
        
        System.err.println("=== Original WRITE");
        Properties properties = request.encodeToProperties();
        
        GatekeeperMessage response = GatekeeperMessage.decodeFromProperties(properties);
        
        System.err.println("=== Second WRITE");
        response.encodeToProperties();
    }
    
}
