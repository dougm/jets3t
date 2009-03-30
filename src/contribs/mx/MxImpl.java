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
package contribs.mx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.mx.MxDelegate;
import org.jets3t.service.mx.MxInterface;

public class MxImpl implements MxInterface {
    private static final Log log = LogFactory.getLog(MxDelegate.class);

    public MxImpl() {
        log.debug("JMX instrumentation implementation started."
            + " S3BucketMx enabled? " + S3BucketMx.isEnabled
            + ", S3ObjectMx enabled? " + S3ObjectMx.isEnabled);
    }

    // MBean registrations and events are all delegated to the implemtation classes.

    public void registerS3ServiceMBean() {
        S3ServiceMx.registerMBean();
    }

    public void registerS3ServiceExceptionMBean() {
        S3ServiceExceptionMx.registerMBean();
    }

    public void registerS3ServiceExceptionEvent() {
        S3ServiceExceptionMx.increment();
    }

    public void registerS3ServiceExceptionEvent(String s3ErrorCode) {
        S3ServiceExceptionMx.increment(s3ErrorCode);
    }

    public void registerS3BucketMBeans(S3Bucket[] buckets) {
        S3BucketMx.registerMBeans(buckets);
    }

    public void registerS3BucketListEvent(String bucketName) {
        S3BucketMx.list(bucketName);
    }

    public void registerS3ObjectMBean(String bucketName, S3Object[] objects) {
        S3ObjectMx.registerMBeans(bucketName, objects);
    }

    public void registerS3ObjectPutEvent(String bucketName, String key) {
        S3ObjectMx.put(bucketName, key);
    }

    public void registerS3ObjectGetEvent(String bucketName, String key) {
        S3ObjectMx.get(bucketName, key);
    }

    public void registerS3ObjectHeadEvent(String bucketName, String key) {
        S3ObjectMx.head(bucketName, key);
    }

    public void registerS3ObjectDeleteEvent(String bucketName, String key) {
        S3ObjectMx.delete(bucketName, key);
    }

    public void registerS3ObjectCopyEvent(String bucketName, String key) {
        S3ObjectMx.copy(bucketName, key);
    }

}
