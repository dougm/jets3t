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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * 
 * 
 * @author James Murty
 */
public class GatekeeperMessage {
    private Properties applicationProperties = new Properties();
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
        
    private void setPropertyIfAvailable(Properties properties, String propertyName, String value) {
        if (value != null) {
System.out.println("=== WRITE Property. " + propertyName + "=" + value);            
            properties.put(propertyName, value);
        }        
    }
        
    public Properties encodeToProperties() {
        Properties postProperties = new Properties();
        Iterator iter = null;
        
        String prefix = "applicationProperties";
        iter = applicationProperties.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            String name = prefix + "." + key;
            String value = applicationProperties.getProperty(key);
            postProperties.put(name, value);
        }
        
        prefix = "request";
        SignatureRequest[] requests = getSignatureRequests();
        for (int i = 0; i < requests.length; i++) {
            SignatureRequest request = requests[i];
            setPropertyIfAvailable(postProperties, prefix + "." + i + "." + request.getSignatureType() + ".objectKey", request.getObjectKey());
            setPropertyIfAvailable(postProperties, prefix + "." + i + "." + request.getSignatureType() + ".bucketName", request.getBucketName());
            setPropertyIfAvailable(postProperties, prefix + "." + i + "." + request.getSignatureType() + ".signedUrl", request.getSignedUrl());
            setPropertyIfAvailable(postProperties, prefix + "." + i + "." + request.getSignatureType() + ".declineReason", request.getDeclineReason());
            
            Map metadata = request.getObjectMetadata();
            iter = metadata.keySet().iterator();
            while (iter.hasNext()) {
                String metadataName = iter.next().toString();
                String metadataValue = metadata.get(metadataName).toString();
                setPropertyIfAvailable(postProperties, prefix + "." + i + "." + request.getSignatureType() + ".metadata." + metadataName, metadataValue);                
            }
        }
        
        return postProperties;
    }
    
    public static GatekeeperMessage decodeFromProperties(Map postProperties) {
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

System.out.println("=== READ Property. " + key + "=" + propertyValue);
            
            if (key.startsWith("applicationProperties")) {
                String propertyName = key.substring(key.lastIndexOf(".") + 1);
                gatekeeperMessage.addApplicationProperty(propertyName, propertyValue);
            } else if (key.startsWith("request")) {
                StringTokenizer st = new StringTokenizer(key, ".");
                st.nextToken(); // Consume object prefix
                String objectIndexStr = st.nextToken();
                Integer objectIndex = Integer.valueOf(objectIndexStr);   
                
                String signatureType = st.nextToken();
                
                boolean isMetadata = false;
                String propertyName = st.nextToken();
                if (st.hasMoreTokens()) {
                    isMetadata = true;
                    propertyName = st.nextToken();
                }
                
                SignatureRequest request = null;
                
                if (signatureRequestMap.containsKey(objectIndex)) {
                    request = (SignatureRequest) signatureRequestMap.get(objectIndex);
                } else {
                    request = new SignatureRequest(signatureType, null);
                    signatureRequestMap.put(objectIndex, request);
                }
                
                if (isMetadata) {
                    request.addObjectMetadata(propertyName, propertyValue);
                } else {
                    if ("objectKey".equals(propertyName)) {
                        request.setObjectKey(propertyValue);
                    } else if ("bucketName".equals(propertyName)) {
                        request.setBucketName(propertyValue);
                    } else if ("signedUrl".equals(propertyName)) {
                        request.signRequest(propertyValue);
                    } else if ("declineReason".equals(propertyName)) {
                        request.declineRequest(propertyValue);
                    } else {
System.err.println("=== WARNING: Unrecognised object property name: " + propertyName);
                    }       
                }
            } else {
System.err.println("=== READ Unrecognised key: " + key); // TODO                
            }            
        }
        
        for (int i = 0; i < signatureRequestMap.size(); i++) {
            Integer objectIndex = new Integer(i);
            SignatureRequest request = (SignatureRequest) signatureRequestMap.get(objectIndex);
            gatekeeperMessage.addSignatureRequest(request);
        }                
        
        return gatekeeperMessage;
    }
    
    
    public static void main(String[] args) {        
        SignatureRequest requests[] = new SignatureRequest[21];
        for (int i = 0; i < requests.length; i++) {
            requests[i] = new SignatureRequest(SignatureRequest.SIGNATURE_TYPE_PUT, "Request " + i);
            requests[i].addObjectMetadata("object-index", String.valueOf(i));
        }
        
        GatekeeperMessage request = new GatekeeperMessage();
        request.addSignatureRequests(requests);
        
        System.err.println("=== Original WRITE");
        Properties properties = request.encodeToProperties();
        
        GatekeeperMessage response = GatekeeperMessage.decodeFromProperties(properties);
        
        System.err.println("=== Second WRITE");
        response.encodeToProperties();
    }
    
}
