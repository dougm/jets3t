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
package org.jets3t.service.executor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.io.BytesTransferredListener;
import org.jets3t.service.io.InterruptableInputStream;
import org.jets3t.service.io.ProgressMonitoredInputStream;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.utils.ServiceUtils;

public class S3ServiceExecutor {
    private final Log log = LogFactory.getLog(S3ServiceExecutor.class);
    
    private static final long DEFAULT_SLEEP_TIME = 250;    
    private final ThreadGroup threadGroup = new ThreadGroup("S3ServiceExecutor");    
    
    private S3Service s3Service = null;
    private ArrayList serviceEventListeners = new ArrayList();
    private final long sleepTime;
    
    public S3ServiceExecutor(S3Service s3Service, S3ServiceEventListener listener) {
        this(s3Service, listener, DEFAULT_SLEEP_TIME);
    }

    public S3ServiceExecutor(
        S3Service s3Service, S3ServiceEventListener listener, long threadSleepTimeMS) 
    {
        this.s3Service = s3Service;
        serviceEventListeners.add(listener);
        this.sleepTime = threadSleepTimeMS;
    }    

    public S3Service getS3Service() {
        return s3Service;
    }
    
    public void addServiceEventListener(S3ServiceEventListener listener) {
        serviceEventListeners.add(listener);
    }

    public void removeServiceEventListener(S3ServiceEventListener listener) {
        serviceEventListeners.remove(listener);
    }

    protected void fireServiceEvent(ServiceEvent event) {
        Iterator listenerIter = new ArrayList(serviceEventListeners).iterator();
        while (listenerIter.hasNext()) {
            S3ServiceEventListener listener = (S3ServiceEventListener) listenerIter.next();
            
            if (event instanceof ListAllBucketsEvent) {
                listener.s3ServiceEventPerformed((ListAllBucketsEvent) event);
            } else if (event instanceof ListObjectsEvent) {
                listener.s3ServiceEventPerformed((ListObjectsEvent) event);
            } else if (event instanceof CreateObjectsEvent) {
                listener.s3ServiceEventPerformed((CreateObjectsEvent) event);
            } else if (event instanceof CreateBucketsEvent) {
                listener.s3ServiceEventPerformed((CreateBucketsEvent) event);
            } else if (event instanceof DeleteObjectsEvent) {
                listener.s3ServiceEventPerformed((DeleteObjectsEvent) event);
            } else if (event instanceof GetObjectsEvent) {
                listener.s3ServiceEventPerformed((GetObjectsEvent) event);
            } else if (event instanceof GetObjectHeadsEvent) {
                listener.s3ServiceEventPerformed((GetObjectHeadsEvent) event);
            } else if (event instanceof LookupACLEvent) {
                listener.s3ServiceEventPerformed((LookupACLEvent) event);
            } else if (event instanceof UpdateACLEvent) {
                listener.s3ServiceEventPerformed((UpdateACLEvent) event);
            } else if (event instanceof DownloadObjectsEvent) {
                listener.s3ServiceEventPerformed((DownloadObjectsEvent) event);
            } else {
                throw new IllegalArgumentException("Listener not invoked for event class: " + event.getClass());
            }
        }
    }

    
    public boolean isAuthenticatedConnection() {
        return s3Service.isAuthenticatedConnection();
    }

    public AWSCredentials getAWSCredentials() {
        return s3Service.getAWSCredentials();
    }
    
    public void listAllBuckets() {
        fireServiceEvent(new ListAllBucketsEvent(ServiceEvent.EVENT_STARTED));
        S3Bucket[] buckets = null;
        try {
            buckets = s3Service.listAllBuckets();
            fireServiceEvent(new ListAllBucketsEvent(ServiceEvent.EVENT_IN_PROGRESS, buckets));
            fireServiceEvent(new ListAllBucketsEvent(ServiceEvent.EVENT_COMPLETED));
        } catch (Throwable t) {
            fireServiceEvent(new ListAllBucketsEvent(t));
        }
    }
    
    public void listObjects(S3Bucket bucket) {
        fireServiceEvent(new ListObjectsEvent(ServiceEvent.EVENT_STARTED, bucket, null));        
        try {
            S3Object[] objects = s3Service.listObjects(bucket);        
            fireServiceEvent(new ListObjectsEvent(ServiceEvent.EVENT_IN_PROGRESS, bucket, objects, null));
            fireServiceEvent(new ListObjectsEvent(ServiceEvent.EVENT_COMPLETED));
        } catch (Throwable t) {
            fireServiceEvent(new ListObjectsEvent(t));
        }
    }
    
    public void listObjects(S3Bucket bucket, String prefix, String delimiter) {
        fireServiceEvent(new ListObjectsEvent(ServiceEvent.EVENT_STARTED, bucket, prefix));
        try {
            S3Object[] objects = s3Service.listObjects(bucket, prefix, delimiter);        
            fireServiceEvent(new ListObjectsEvent(ServiceEvent.EVENT_IN_PROGRESS, bucket, objects, prefix));
            fireServiceEvent(new ListObjectsEvent(ServiceEvent.EVENT_COMPLETED));
        } catch (Throwable t) {
            fireServiceEvent(new ListObjectsEvent(t));
        }
    }
    
    public void createBucket(String bucketName) {
        S3Bucket bucket = new S3Bucket();
        bucket.setName(bucketName);
        createBuckets(new S3Bucket[] { bucket } );
    }
    
    public void createBucket(S3Bucket bucket) {
        createBuckets(new S3Bucket[] { bucket } );
    }

    public void createBuckets(final S3Bucket[] buckets) {
        // Start all queries in the background.
        Thread[] threads = new Thread[buckets.length];
        CreateBucketRunnable[] runnables = new CreateBucketRunnable[buckets.length];
        ThreadGroup localThreadGroup = new ThreadGroup(threadGroup, "createObjects");
        for (int i = 0; i < runnables.length; i++) {
            runnables[i] = new CreateBucketRunnable(buckets[i]);
            threads[i] = new Thread(localThreadGroup, runnables[i]);
            threads[i].start();                    
        }
        
        // Wait for threads to finish, or be cancelled.        
        (new ThreadGroupManager(localThreadGroup, threads, runnables) {
            public void fireStartEvent(ProgressStatus progressStatus) {
                fireServiceEvent(new CreateBucketsEvent(ServiceEvent.EVENT_STARTED, progressStatus, buckets));        
            }
            public void fireProgressEvent(ProgressStatus progressStatus, List completedResults) {
                S3Bucket[] resultBuckets = (S3Bucket[]) completedResults.toArray(new S3Bucket[] {});
                fireServiceEvent(new CreateBucketsEvent(ServiceEvent.EVENT_IN_PROGRESS, progressStatus, resultBuckets));
            }
            public void fireCancelEvent() {
                fireServiceEvent(new CreateBucketsEvent(ServiceEvent.EVENT_CANCELLED));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(new CreateBucketsEvent(ServiceEvent.EVENT_COMPLETED));                    
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(new CreateBucketsEvent(throwable));
            }
        }).run();
    }
    
    public void createObjects(final S3Bucket bucket, final S3Object[] objects) {       
        final long bytesTotal = ServiceUtils.countBytesInObjects(objects);
        final long bytesCompleted[] = new long[] {0};
        
        BytesTransferredListener bytesTransferredListener = new BytesTransferredListener() {
            public void bytesTransferredUpdate(long transferredBytes) {
                bytesCompleted[0] += transferredBytes;
            }
        };
        
        // Start all queries in the background.
        Thread[] threads = new Thread[objects.length];
        CreateObjectRunnable[] runnables = new CreateObjectRunnable[objects.length];
        ThreadGroup localThreadGroup = new ThreadGroup(threadGroup, "createObjects");
        for (int i = 0; i < runnables.length; i++) {
            runnables[i] = new CreateObjectRunnable(bucket, objects[i], bytesTransferredListener);
            threads[i] = new Thread(localThreadGroup, runnables[i]);
            threads[i].start();                    
        }        
        
        // Wait for threads to finish, or be cancelled.        
        (new ThreadGroupManager(localThreadGroup, threads, runnables) {
            public void fireStartEvent(ProgressStatus progressStatus) {
                fireServiceEvent(new CreateObjectsEvent(ServiceEvent.EVENT_STARTED, progressStatus, bucket, new S3Object[] {}, 0, bytesTotal));        
            }
            public void fireProgressEvent(ProgressStatus progressStatus, List completedResults) {
                S3Object[] resultObjects = (S3Object[]) completedResults.toArray(new S3Object[] {});
                fireServiceEvent(new CreateObjectsEvent(ServiceEvent.EVENT_IN_PROGRESS, progressStatus, bucket, resultObjects, bytesCompleted[0], bytesTotal));
            }
            public void fireCancelEvent() {
                fireServiceEvent(new CreateObjectsEvent(ServiceEvent.EVENT_CANCELLED, bytesCompleted[0], bytesTotal));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(new CreateObjectsEvent(ServiceEvent.EVENT_COMPLETED, bytesCompleted[0], bytesTotal));
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(new CreateBucketsEvent(throwable));
            }
        }).run();
    }
    
    public void deleteObject(S3Bucket bucket, S3Object object) {
        deleteObjects(bucket, new S3Object[] { object });
    }

    public void deleteObjects(final S3Bucket bucket, final S3Object[] objects) {
        // Start all queries in the background.
        Thread[] threads = new Thread[objects.length];
        DeleteObjectRunnable[] runnables = new DeleteObjectRunnable[objects.length];
        ThreadGroup localThreadGroup = new ThreadGroup(threadGroup, "deleteObjects");
        for (int i = 0; i < runnables.length; i++) {
            runnables[i] = new DeleteObjectRunnable(bucket, objects[i]);
            threads[i] = new Thread(localThreadGroup, runnables[i]);
            threads[i].start();
        }
        
        // Wait for threads to finish, or be cancelled.        
        (new ThreadGroupManager(localThreadGroup, threads, runnables) {
            public void fireStartEvent(ProgressStatus progressStatus) {
                fireServiceEvent(new DeleteObjectsEvent(ServiceEvent.EVENT_STARTED, progressStatus, bucket, new S3Object[] {}));        
            }
            public void fireProgressEvent(ProgressStatus progressStatus, List completedResults) {
                S3Object[] deletedObjects = (S3Object[]) completedResults.toArray(new S3Object[] {});                    
                fireServiceEvent(new DeleteObjectsEvent(ServiceEvent.EVENT_IN_PROGRESS, progressStatus, bucket, deletedObjects));
            }
            public void fireCancelEvent() {
                fireServiceEvent(new DeleteObjectsEvent(ServiceEvent.EVENT_CANCELLED));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(new DeleteObjectsEvent(ServiceEvent.EVENT_COMPLETED));                    
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(new CreateBucketsEvent(throwable));
            }
        }).run();
    }
    
    public void getObjects(S3Bucket bucket, S3Object[] objects) {
        String[] objectKeys = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            objectKeys[i] = objects[i].getKey();
        }
        getObjects(bucket, objectKeys);
    }
    
    public void getObjects(final S3Bucket bucket, final String[] objectKeys) {
        // Start all queries in the background.
        Thread[] threads = new Thread[objectKeys.length];
        GetObjectRunnable[] runnables = new GetObjectRunnable[objectKeys.length];
        ThreadGroup localThreadGroup = new ThreadGroup(threadGroup, "getObjects");
        for (int i = 0; i < runnables.length; i++) {
            runnables[i] = new GetObjectRunnable(bucket, objectKeys[i], false);
            threads[i] = new Thread(localThreadGroup, runnables[i]);
            threads[i].start();
        }
        // Wait for threads to finish, or be cancelled.        
        (new ThreadGroupManager(localThreadGroup, threads, runnables) {
            public void fireStartEvent(ProgressStatus progressStatus) {
                fireServiceEvent(new GetObjectsEvent(ServiceEvent.EVENT_STARTED, progressStatus, bucket, new S3Object[] {}));        
            }
            public void fireProgressEvent(ProgressStatus progressStatus, List completedResults) {
                S3Object[] resultObjects = (S3Object[]) completedResults.toArray(new S3Object[] {});
                fireServiceEvent(new GetObjectsEvent(ServiceEvent.EVENT_IN_PROGRESS, progressStatus, bucket, resultObjects));
            }
            public void fireCancelEvent() {
                fireServiceEvent(new GetObjectsEvent(ServiceEvent.EVENT_CANCELLED));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(new GetObjectsEvent(ServiceEvent.EVENT_COMPLETED));                    
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(new CreateBucketsEvent(throwable));
            }
        }).run();
    }
    
    public void getObjectsHeads(S3Bucket bucket, S3Object[] objects) {
        String[] objectKeys = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            objectKeys[i] = objects[i].getKey();
        }
        getObjectsHeads(bucket, objectKeys);
    }

    public void getObjectsHeads(final S3Bucket bucket, final String[] objectKeys) {
        // Start all queries in the background.
        Thread[] threads = new Thread[objectKeys.length];
        GetObjectRunnable[] runnables = new GetObjectRunnable[objectKeys.length];
        ThreadGroup localThreadGroup = new ThreadGroup(threadGroup, "getObjects");
        for (int i = 0; i < runnables.length; i++) {
            runnables[i] = new GetObjectRunnable(bucket, objectKeys[i], true);
            threads[i] = new Thread(localThreadGroup, runnables[i]);
            threads[i].start();
        }
        // Wait for threads to finish, or be cancelled.        
        (new ThreadGroupManager(localThreadGroup, threads, runnables) {
            public void fireStartEvent(ProgressStatus progressStatus) {
                fireServiceEvent(new GetObjectHeadsEvent(ServiceEvent.EVENT_STARTED, progressStatus, bucket, new S3Object[] {}));        
            }
            public void fireProgressEvent(ProgressStatus progressStatus, List completedResults) {
                S3Object[] resultObjects = (S3Object[]) completedResults.toArray(new S3Object[] {});
                fireServiceEvent(new GetObjectHeadsEvent(ServiceEvent.EVENT_IN_PROGRESS, progressStatus, bucket, resultObjects));
            }
            public void fireCancelEvent() {
                fireServiceEvent(new GetObjectHeadsEvent(ServiceEvent.EVENT_CANCELLED));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(new GetObjectHeadsEvent(ServiceEvent.EVENT_COMPLETED));                    
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(new CreateBucketsEvent(throwable));
            }
        }).run();
    }
    
    public void getACLs(final S3Bucket bucket, final S3Object[] objects) {
        // Start all queries in the background.
        Thread[] threads = new Thread[objects.length];
        GetACLRunnable[] runnables = new GetACLRunnable[objects.length];
        ThreadGroup localThreadGroup = new ThreadGroup(threadGroup, "getObjects");
        for (int i = 0; i < runnables.length; i++) {
            runnables[i] = new GetACLRunnable(bucket, objects[i]);
            threads[i] = new Thread(localThreadGroup, runnables[i]);
            threads[i].start();
        }
        // Wait for threads to finish, or be cancelled.        
        (new ThreadGroupManager(localThreadGroup, threads, runnables) {
            public void fireStartEvent(ProgressStatus progressStatus) {
                fireServiceEvent(new LookupACLEvent(ServiceEvent.EVENT_STARTED, progressStatus, bucket, new S3Object[] {}));        
            }
            public void fireProgressEvent(ProgressStatus progressStatus, List completedResults) {
                S3Object[] resultObjects = (S3Object[]) completedResults.toArray(new S3Object[] {});
                fireServiceEvent(new LookupACLEvent(ServiceEvent.EVENT_IN_PROGRESS, progressStatus, bucket, resultObjects));
            }
            public void fireCancelEvent() {
                fireServiceEvent(new LookupACLEvent(ServiceEvent.EVENT_CANCELLED));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(new LookupACLEvent(ServiceEvent.EVENT_COMPLETED));
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(new CreateBucketsEvent(throwable));
            }
        }).run();
    }

    public void putACLs(final S3Bucket bucket, final S3Object[] objects) {
        // Start all queries in the background.
        Thread[] threads = new Thread[objects.length];
        PutACLRunnable[] runnables = new PutACLRunnable[objects.length];
        ThreadGroup localThreadGroup = new ThreadGroup(threadGroup, "getObjects");
        for (int i = 0; i < runnables.length; i++) {
            runnables[i] = new PutACLRunnable(bucket, objects[i]);
            threads[i] = new Thread(localThreadGroup, runnables[i]);
            threads[i].start();
        }
        // Wait for threads to finish, or be cancelled.        
        (new ThreadGroupManager(localThreadGroup, threads, runnables) {
            public void fireStartEvent(ProgressStatus progressStatus) {
                fireServiceEvent(new UpdateACLEvent(ServiceEvent.EVENT_STARTED, progressStatus, bucket, new S3Object[] {}));        
            }
            public void fireProgressEvent(ProgressStatus progressStatus, List completedResults) {
                S3Object[] resultObjects = (S3Object[]) completedResults.toArray(new S3Object[] {});
                fireServiceEvent(new UpdateACLEvent(ServiceEvent.EVENT_IN_PROGRESS, progressStatus, bucket, resultObjects));
            }
            public void fireCancelEvent() {
                fireServiceEvent(new UpdateACLEvent(ServiceEvent.EVENT_CANCELLED));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(new UpdateACLEvent(ServiceEvent.EVENT_COMPLETED));                    
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(new CreateBucketsEvent(throwable));
            }
        }).run();
    }
    
    public void downloadObjects(final S3Bucket bucket, final S3ObjectAndOutputStream[] objectAndOutputStream) {
        // Initialise byte transfer monitoring variables.
        final long bytesCompleted[] = new long[] {0};
        final BytesTransferredListener bytesTransferredListener = new BytesTransferredListener() {
            public void bytesTransferredUpdate(long transferredBytes) {
                bytesCompleted[0] += transferredBytes;
            }
        };
        final List incompleteObjectDownloadList = new ArrayList();

        // Start all queries in the background.
        Thread[] threads = new Thread[objectAndOutputStream.length];
        DownloadObjectRunnable[] runnables = new DownloadObjectRunnable[objectAndOutputStream.length];
        final S3Object[] objects = new S3Object[objectAndOutputStream.length];
        ThreadGroup localThreadGroup = new ThreadGroup(threadGroup, "getObjects");
        for (int i = 0; i < runnables.length; i++) {
            objects[i] = objectAndOutputStream[i].getObject();
                        
            incompleteObjectDownloadList.add(objects[i]);
            runnables[i] = new DownloadObjectRunnable(bucket, objects[i].getKey(), 
                objectAndOutputStream[i].getOuputStream(), bytesTransferredListener);    
            threads[i] = new Thread(localThreadGroup, runnables[i]);
            threads[i].start();
        }

        // Set total bytes to 0 to flag the fact we cannot monitor the bytes transferred. 
        final long bytesTotal = ServiceUtils.countBytesInObjects(objects);
        
        // Wait for threads to finish, or be cancelled.        
        (new ThreadGroupManager(localThreadGroup, threads, runnables) {
            public void fireStartEvent(ProgressStatus progressStatus) {
                fireServiceEvent(new DownloadObjectsEvent(ServiceEvent.EVENT_STARTED, progressStatus, new S3Object[] {}, bytesCompleted[0], bytesTotal));        
            }
            public void fireProgressEvent(ProgressStatus progressStatus, List completedResults) {
                incompleteObjectDownloadList.removeAll(completedResults);
                S3Object[] resultObjects = (S3Object[]) completedResults.toArray(new S3Object[] {});
                fireServiceEvent(new DownloadObjectsEvent(ServiceEvent.EVENT_IN_PROGRESS, progressStatus, resultObjects, bytesCompleted[0], bytesTotal));
            }
            public void fireCancelEvent() {
                S3Object[] incompleteObjects = (S3Object[]) incompleteObjectDownloadList.toArray(new S3Object[] {});
                fireServiceEvent(new DownloadObjectsEvent(ServiceEvent.EVENT_CANCELLED, incompleteObjects));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(new DownloadObjectsEvent(ServiceEvent.EVENT_COMPLETED));                    
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(new DownloadObjectsEvent(throwable));
            }
        }).run();
    }

    public class S3ObjectAndOutputStream {
        private S3Object object = null;
        private OutputStream outputStream = null;
        
        public S3ObjectAndOutputStream(S3Object object, OutputStream outputStream) {
            this.object = object;
            this.outputStream = outputStream;
        }
        
        public S3Object getObject() {
            return object;
        }
        
        public OutputStream getOuputStream() {
            return outputStream;
        }
    }

    ///////////////////////////////////////////////
    // Private classes used by the methods above //
    ///////////////////////////////////////////////
    
    private abstract class AbstractThread implements Runnable {
        private boolean forceInterrupt = false;

        public abstract Object getResult();
        
        public abstract void forceInterruptCalled();
        
        protected void forceInterrupt() {
            this.forceInterrupt = true;
            forceInterruptCalled();
        }
        
        protected boolean notInterrupted() throws InterruptedException {
            if (forceInterrupt || Thread.interrupted()) {
                throw new InterruptedException("Interrupted by JAMES");
            }
            return true;
        }        
    }
    
    private class PutACLRunnable extends AbstractThread {
        private S3Bucket bucket = null;
        private S3Object s3Object = null;        
        private Object result = null;
        
        public PutACLRunnable(S3Bucket bucket, S3Object s3Object) {
            this.bucket = bucket;
            this.s3Object = s3Object;
        }

        public void run() {
            try {
                s3Service.putAcl(bucket, s3Object);
                result = s3Object;
            } catch (S3ServiceException e) {
                result = e;
            }            
        }
        
        public Object getResult() {
            return result;
        }        
        
        public void forceInterruptCalled() {            
            // This is an atomic operation, cannot interrupt. Ignore.
        }
    }

    private class GetACLRunnable extends AbstractThread {
        private S3Bucket bucket = null;
        private S3Object object = null;        
        private Object result = null;
        
        public GetACLRunnable(S3Bucket bucket, S3Object object) {
            this.bucket = bucket;
            this.object = object;
        }

        public void run() {
            try {
                AccessControlList acl = acl = s3Service.getAcl(bucket, object.getKey());
                object.setAcl(acl);
                result = object;
            } catch (S3ServiceException e) {
                result = e;
            }            
        }
        
        public Object getResult() {
            return result;
        }        

        public void forceInterruptCalled() {            
            // This is an atomic operation, cannot interrupt. Ignore.
        }
    }

    private class DeleteObjectRunnable extends AbstractThread {
        private S3Bucket bucket = null;
        private S3Object object = null;        
        private Object result = null;
        
        public DeleteObjectRunnable(S3Bucket bucket, S3Object object) {
            this.bucket = bucket;
            this.object = object;
        }

        public void run() {
            try {
                s3Service.deleteObject(bucket, object.getKey());
                result = object;
            } catch (S3ServiceException e) {
                result = e;
            }            
        }
        
        public Object getResult() {
            return result;
        }        
        
        public void forceInterruptCalled() {            
            // This is an atomic operation, cannot interrupt. Ignore.
        }
    }

    private class CreateBucketRunnable extends AbstractThread {
        private S3Bucket bucket = null;
        private Object result = null;
        
        public CreateBucketRunnable(S3Bucket bucket) {
            this.bucket = bucket;
        }

        public void run() {
            try {                
                result = s3Service.createBucket(bucket);
            } catch (S3ServiceException e) {
                result = e;
            }            
        }
        
        public Object getResult() {
            return result;
        }        
        
        public void forceInterruptCalled() {            
            // This is an atomic operation, cannot interrupt. Ignore.
        }
    }

    private class CreateObjectRunnable extends AbstractThread {
        private S3Bucket bucket = null;
        private S3Object s3Object = null;    
        private InterruptableInputStream interruptableInputStream = null;
        private BytesTransferredListener bytesTransferredListener = null;
        
        private Object result = null;
        
        public CreateObjectRunnable(S3Bucket bucket, S3Object s3Object, BytesTransferredListener bytesTransferredListener) {
            this.bucket = bucket;
            this.s3Object = s3Object;
            this.bytesTransferredListener = bytesTransferredListener;
        }

        public void run() {
            try {
                if (s3Object.getDataInputStream() != null) {
                    interruptableInputStream = new InterruptableInputStream(s3Object.getDataInputStream());
                    ProgressMonitoredInputStream pmInputStream = new ProgressMonitoredInputStream(
                        interruptableInputStream, bytesTransferredListener);
                    s3Object.setDataInputStream(pmInputStream);
                }
                result = s3Service.putObject(bucket, s3Object);
            } catch (S3ServiceException e) {
                result = e;
            } finally {
                if (s3Object.getDataInputStream() != null) {
                    try {
                        s3Object.getDataInputStream().close();
                    } catch (IOException e) {
                        log.error("Unable to close Object's input stream", e);                        
                    }
                }
            }
        }
        
        public Object getResult() {
            return result;
        }        
        
        public void forceInterruptCalled() {        
            if (interruptableInputStream != null) {
                interruptableInputStream.interrupt();
            }
        }
    }

    private class GetObjectRunnable extends AbstractThread {
        private S3Bucket bucket = null;
        private String objectKey = null;
        private boolean headOnly = false;
        
        private Object result = null;
        
        public GetObjectRunnable(S3Bucket bucket, String objectKey, boolean headOnly) {
            this.bucket = bucket;
            this.objectKey = objectKey;
            this.headOnly = headOnly;
        }

        public void run() {
            try {
                if (headOnly) {
                    result = s3Service.getObjectDetails(bucket, objectKey);
                } else {
                    result = s3Service.getObject(bucket, objectKey);                    
                }
            } catch (S3ServiceException e) {
                result = e;
            }            
        }
        
        public Object getResult() {
            return result;
        }        
        
        public void forceInterruptCalled() {            
            // This is an atomic operation, cannot interrupt. Ignore.
        }
    }
    
    private class DownloadObjectRunnable extends AbstractThread {
        private String objectKey = null;
        private S3Bucket bucket = null;
        private OutputStream outputStream = null;
        private InterruptableInputStream interruptableInputStream = null;
        private BytesTransferredListener bytesTransferredListener = null;
        
        private Object result = null;

        public DownloadObjectRunnable(S3Bucket bucket, String objectKey, OutputStream outputStream, BytesTransferredListener bytesTransferredListener) {
            this.bucket = bucket;
            this.objectKey = objectKey;
            this.outputStream = outputStream;
            this.bytesTransferredListener = bytesTransferredListener;
        }
        
        public void run() {            
            BufferedInputStream bufferedInputStream = null;
            BufferedOutputStream bufferedOutputStream = null;
            S3Object object = null;
            try {
                object = s3Service.getObject(bucket, objectKey);

                // Setup monitoring of stream bytes tranferred. 
                interruptableInputStream = new InterruptableInputStream(object.getDataInputStream()); 
                bufferedInputStream = new BufferedInputStream(
                    new ProgressMonitoredInputStream(interruptableInputStream, bytesTransferredListener));
                
                bufferedOutputStream = new BufferedOutputStream(outputStream);
                
                byte[] buffer = new byte[1024];
                int byteCount = -1;

                while ((byteCount = bufferedInputStream.read(buffer)) != -1) {
                    bufferedOutputStream.write(buffer, 0, byteCount);
                }
                
                bufferedOutputStream.close();
                bufferedInputStream.close();

                object.setDataInputStream(null);
                result = object;
            } catch (Throwable t) {                
                result = t;
            } finally {
                try {
                    bufferedInputStream.close();    
                } catch (Exception e) {
                    log.error("Unable to close Object input stream", e);
                }
                try {
                    bufferedOutputStream.close();                    
                } catch (Exception e) {
                    log.error("Unable to close download output stream", e);
                }
            }
        }
        
        public Object getResult() {
            return result;
        }

        public void forceInterruptCalled() {
            interruptableInputStream.interrupt();
        }
    }
    

    private abstract class ThreadGroupManager {
        private final Log log = LogFactory.getLog(ThreadGroupManager.class);
        
        private ThreadGroup localThreadGroup = null;
        private Thread[] threads = null;
        private AbstractThread[] runnables = null;
        
        public ThreadGroupManager(ThreadGroup localThreadGroup, Thread[] threads, AbstractThread[] runnables) {
            this.localThreadGroup = localThreadGroup;
            this.threads = threads;
            this.runnables = runnables;            
        }
        
        private List getNewlyCompletedResults(boolean alreadyFired[]) throws Throwable {
            ArrayList completedResults = new ArrayList();
            
            for (int i = 0; i < threads.length; i++) {
                if (!alreadyFired[i] && !threads[i].isAlive()) {
                    alreadyFired[i] = true;
                    log.debug("Thread " + (i+1) + " of " + threads.length + " has recently completed");

                    if (runnables[i].getResult() instanceof Throwable) {
                        throw (Throwable) runnables[i].getResult();
                    } else {
                        completedResults.add(runnables[i].getResult());
                    }                    
                }
            }
            return completedResults;
        }
        
        private void forceInterruptAllRunnables() {
            log.debug("Setting force interrupt flag on all runnables");
            for (int i = 0; i < runnables.length; i++) {
                runnables[i].forceInterrupt();
            }
        }
        
        public void run() {
            log.debug("Started ThreadManager for thread group: " + threadGroup.getName());
            
            final boolean[] interrupted = new boolean[] { false };
            
            final CancelEventListener cancelEventListener = new CancelEventListener() {
                public void cancelTask(Object eventSource) {
                    log.debug("Cancel task invoked on ThreadManager");
                    
                    // Flag that this ThreadManager class should shutdown.
                    interrupted[0] = true;
                    
                    // Set force interrupt flag for all runnables.
                    forceInterruptAllRunnables();
                }
            };
            
            final boolean alreadyFired[] = new boolean[runnables.length]; // All values initialized to false.

            try {
                ProgressStatus progressStatus = new ProgressStatus(0, runnables.length, cancelEventListener); 
                fireStartEvent(progressStatus);
                
                while (!interrupted[0] && localThreadGroup.activeCount() > 0) {
                    try {
                        Thread.sleep(sleepTime);
    
                        if (interrupted[0]) {
                            // Do nothing, we've been interrupted during sleep.                        
                        } else {
                            // Fire progress event.
                            int completedThreads = runnables.length - localThreadGroup.activeCount();                    
                            progressStatus = new ProgressStatus(completedThreads, runnables.length, cancelEventListener);
                            List completedResults = getNewlyCompletedResults(alreadyFired);                    
                            fireProgressEvent(progressStatus, completedResults);
                            
                            if (completedResults.size() > 0) {
                                log.debug(completedResults.size() + " of " + threads.length + " have completed");
                            }
                        }
                    } catch (InterruptedException e) {
                        interrupted[0] = true;
                        forceInterruptAllRunnables();
                    }
                }        
                
                if (interrupted[0]) {
                    fireCancelEvent();
                } else {
                    int completedThreads = localThreadGroup.activeCount();                    
                    progressStatus = new ProgressStatus(completedThreads, runnables.length, cancelEventListener);
                    List completedResults = getNewlyCompletedResults(alreadyFired);                    
                    fireProgressEvent(progressStatus, completedResults);
                    if (completedResults.size() > 0) {
                        log.debug(completedResults.size() + " of " + threads.length + " have completed");
                    }                    
                    fireCompletedEvent();
                }
            } catch (Throwable t) {
                log.error("A thread failed with an exception. Firing ERROR event and cancellling all threads", t);
                // Set force interrupt flag for all runnables.
                forceInterruptAllRunnables();
                
                fireErrorEvent(t);                
            }
        }
        
        public abstract void fireStartEvent(ProgressStatus progressStatus);
        
        public abstract void fireProgressEvent(ProgressStatus progressStatus, List completedResults);
        
        public abstract void fireCompletedEvent();

        public abstract void fireCancelEvent();

        public abstract void fireErrorEvent(Throwable t);
    }
    
}
