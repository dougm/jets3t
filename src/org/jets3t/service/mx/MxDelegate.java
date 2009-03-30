/*
 * jets3t : Java Extra-Tasty S3 Toolkit (for Amazon S3 online storage service)
 * This is a java.net project, see https://jets3t.dev.java.net/
 * 
 * Copyright 2009 James Murty
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
package org.jets3t.service.mx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;

public class MxDelegate implements MxInterface {
    private static final Log log = LogFactory.getLog(MxDelegate.class);
    
    private MxInterface handler = null; 
    
    private static MxDelegate instance = null;
    
    
    public static MxDelegate getInstance() {
        if (instance == null) {
            instance = new MxDelegate();
        }
        return instance;
    }

    protected MxDelegate() {     
        if (System.getProperty("com.sun.management.jmxremote") == null) {
            return;
        }
        try {
            // Load the contribs.mx.MxImpl implementation class, if available.
            Class impl = Class.forName("contribs.mx.MxImpl");
            handler = (MxInterface) impl.newInstance();
        } catch (ClassNotFoundException e) {
            log.error(
                "JMX instrumentation package 'contribs.mx' could not be found, "
                + " instrumentation will not available", e);
        } catch (Exception e) {
            log.error(
                "JMX instrumentation implementation in package 'contribs.mx' "
                + " could not be loaded", e);                
        }            
    }
            
    public void registerS3ServiceMBean() {
        if (handler != null) {
            handler.registerS3ServiceMBean();
        }
    }
    
    public void registerS3ServiceExceptionMBean() {
        if (handler != null) {
            handler.registerS3ServiceExceptionMBean();
        }
    }
    
    public void registerS3ServiceExceptionEvent() {
        if (handler != null) {
            handler.registerS3ServiceExceptionEvent();
        }
    }

    public void registerS3ServiceExceptionEvent(String s3ErrorCode) {
        if (handler != null) {
            handler.registerS3ServiceExceptionEvent(s3ErrorCode);
        }
    }
    
    public void registerS3BucketMBeans(S3Bucket[] buckets) {
        if (handler != null) {
            handler.registerS3BucketMBeans(buckets);
        }
    }
    
    public void registerS3BucketListEvent(String bucketName) {
        if (handler != null) {
            handler.registerS3BucketListEvent(bucketName);
        }
    }

    public void registerS3ObjectMBean(String bucketName, S3Object[] objects) {
        if (handler != null) {
            handler.registerS3ObjectMBean(bucketName, objects);
        }
    }

    public void registerS3ObjectPutEvent(String bucketName, String key) {
        if (handler != null) {
            handler.registerS3ObjectPutEvent(bucketName, key);
        }
    }

    public void registerS3ObjectGetEvent(String bucketName, String key) {
        if (handler != null) {
            handler.registerS3ObjectGetEvent(bucketName, key);
        }
    }

    public void registerS3ObjectHeadEvent(String bucketName, String key) {
        if (handler != null) {
            handler.registerS3ObjectHeadEvent(bucketName, key);
        }
    }

    public void registerS3ObjectDeleteEvent(String bucketName, String key) {
        if (handler != null) {
            handler.registerS3ObjectDeleteEvent(bucketName, key);
        }
    }

    public void registerS3ObjectCopyEvent(String bucketName, String key) {
        if (handler != null) {
            handler.registerS3ObjectCopyEvent(bucketName, key);
        }
    }
    
}
