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

import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;

public interface MxInterface {

    public abstract void registerS3ServiceMBean();

    public abstract void registerS3ServiceExceptionMBean();

    public abstract void registerS3ServiceExceptionEvent();

    public abstract void registerS3ServiceExceptionEvent(String s3ErrorCode);

    public abstract void registerS3BucketMBeans(S3Bucket[] buckets);

    public abstract void registerS3BucketListEvent(String bucketName);

    public abstract void registerS3ObjectMBean(String bucketName,
        S3Object[] objects);

    public abstract void registerS3ObjectPutEvent(String bucketName, String key);

    public abstract void registerS3ObjectGetEvent(String bucketName, String key);

    public abstract void registerS3ObjectHeadEvent(String bucketName, String key);

    public abstract void registerS3ObjectDeleteEvent(String bucketName,
        String key);

    public abstract void registerS3ObjectCopyEvent(String bucketName, String key);

}