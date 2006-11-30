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
package org.jets3t.tests;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

public class HttpClientRestS3ServiceTest extends BaseS3ServiceTest {
    
    public HttpClientRestS3ServiceTest() throws IOException {
        super();
    }

    protected S3Service getS3Service(AWSCredentials awsCredentials) throws S3ServiceException {
        return new RestS3Service(awsCredentials);
    }
    
    public void testRestCannedACL() throws Exception {
        S3Service s3Service = getS3Service(awsCredentials);
        
        // Create test bucket.
        String bucketName = awsCredentials.getAccessKey() + ".jets3t_TestCases";
        S3Bucket bucket = s3Service.createBucket(bucketName);
        
        // Try to create REST canned public object.        
        String publicKey = "PublicObject";
        S3Object object = new S3Object(publicKey);
        object.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
        object.setOwner(bucket.getOwner());
        
        try {
            s3Service.putObject(bucket, object);
            URL url = new URL("http://s3.amazonaws.com/" + bucketName + "/" + publicKey);      
            assertEquals("Expected public access (200)", 
                    200, ((HttpURLConnection)url.openConnection()).getResponseCode());
        } finally {
            s3Service.deleteObject(bucket, object.getKey());
//            s3Service.deleteBucket(bucket.getName());
        }
    }

}
