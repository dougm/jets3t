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

import java.util.HashMap;
import java.util.Map;

public class SignatureRequest {
    public static final String SIGNATURE_TYPE_GET = "get";
    public static final String SIGNATURE_TYPE_HEAD = "head";
    public static final String SIGNATURE_TYPE_PUT = "put";
    public static final String SIGNATURE_TYPE_DELETE = "delete";
    
    private String signatureType = null;
    private String objectKey = null;
    private String bucketName = null;
    private Map objectMetadata = new HashMap();
    private String signedUrl = null;
    private String declineReason = null;
    
    public SignatureRequest(String signatureType, String objectKey) {
        if (!SIGNATURE_TYPE_GET.equals(signatureType)
            && !SIGNATURE_TYPE_HEAD.equals(signatureType)
            && !SIGNATURE_TYPE_PUT.equals(signatureType)
            && !SIGNATURE_TYPE_DELETE.equals(signatureType)) 
        {            
            throw new IllegalArgumentException("Illegal signature type: " + signatureType);
        }
        
        this.signatureType = signatureType;
        this.objectKey = objectKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public Map getObjectMetadata() {
        return objectMetadata;
    }

    public void setObjectMetadata(Map objectMetadata) {
        this.objectMetadata.putAll(objectMetadata);
    }
    
    public void addObjectMetadata(String metadataName, String metadataValue) {
        this.objectMetadata.put(metadataName, metadataValue);
    }
    
    public String getSignatureType() {
        return signatureType;
    }
    
    public void signRequest(String signedUrl) {
        this.signedUrl = signedUrl;
    }
    
    public String getSignedUrl() {
        return this.signedUrl;
    }

    public void declineRequest(String reason) {
        this.declineReason = reason;
    }
    
    public String getDeclineReason() {
        return this.declineReason;
    }
    
    public boolean isSigned() {
        return getSignedUrl() != null;
    }    
    
}
