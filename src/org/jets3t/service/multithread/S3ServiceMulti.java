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
package org.jets3t.service.multithread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.Constants;
import org.jets3t.service.Jets3tProperties;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.io.InterruptableInputStream;
import org.jets3t.service.io.BytesProgressWatcher;
import org.jets3t.service.io.ProgressMonitoredInputStream;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.utils.ServiceUtils;
import org.jets3t.service.utils.signedurl.SignedUrlAndObject;
import org.jets3t.service.utils.signedurl.SignedUrlHandler;

/**
 * S3 service wrapper that performs multiple S3 requests at a time using multi-threading and an
 * underlying thread-safe {@link S3Service} implementation. 
 * <p>
 * This service is designed to be run in non-blocking threads that therefore communicates
 * information about its progress by firing {@link ServiceEvent} events. It is the responsiblity
 * of applications using this service to correctly handle these events - see the JetS3t application 
 * {@link org.jets3t.apps.synchronize.Synchronize} for examples of how an application can use these 
 * events.
 * </p>
 * <p>
 * For cases where the full power, and complexity, of the event notification mechanism is not required
 * the simplified multi-threaded service {@link S3ServiceSimpleMulti} can be used.
 * </p>
 * <p>
 * This class uses properties obtained through {@link Jets3tProperties}. For more information on 
 * these properties please refer to 
 * <a href="http://jets3t.s3.amazonaws.com/toolkit/configuration.html">JetS3t Configuration</a>
 * </p>
 * 
 * @author James Murty
 */
public class S3ServiceMulti implements Serializable {
    private static final long serialVersionUID = -1031831146656816336L;

    private final Log log = LogFactory.getLog(S3ServiceMulti.class);
    
    private S3Service s3Service = null;
    private ArrayList serviceEventListeners = new ArrayList();
    private final long sleepTime;
    
    /**
     * Construct a multi-threaded service based on an S3Service and which sends event notifications
     * to an event listening class. EVENT_IN_PROGRESS events are sent at the default time interval
     * of 500ms. 
     * 
     * @param s3Service
     *        an S3Service implementation that will be used to perform S3 requests. This implementation
     *        <b>must</b> be thread-safe.
     * @param listener
     *        the event listener which will handle event notifications.
     */
    public S3ServiceMulti(S3Service s3Service, S3ServiceEventListener listener) {
        this(s3Service, listener, 500);
    }

    /**
     * Construct a multi-threaded service based on an S3Service and which sends event notifications
     * to an event listening class, and which will send EVENT_IN_PROGRESS events at the specified 
     * time interval. 
     * 
     * @param s3Service
     *        an S3Service implementation that will be used to perform S3 requests. This implementation
     *        <b>must</b> be thread-safe.
     * @param listener
     *        the event listener which will handle event notifications.
     * @param threadSleepTimeMS
     *        how many milliseconds to wait before sending each EVENT_IN_PROGRESS notification event.
     */
    public S3ServiceMulti(
        S3Service s3Service, S3ServiceEventListener listener, long threadSleepTimeMS) 
    {
        this.s3Service = s3Service;
        addServiceEventListener(listener);
        this.sleepTime = threadSleepTimeMS;
        
        // Sanity-check the maximum thread and connection settings to ensure the maximum number
        // of connections is at least equal to the largest of the maximum thread counts, and warn
        // the use of potential problems.
        int adminMaxThreadCount = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getIntProperty("s3service.admin-max-thread-count", 4);        
        int maxThreadCount = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getIntProperty("s3service.max-thread-count", 4);
        int maxConnectionCount = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getIntProperty("httpclient.max-connections", 4);
        if (maxConnectionCount < maxThreadCount) {
            log.warn("Insufficient connections available (httpclient.max-connections) to run " + maxThreadCount 
                + " simultaneous threads (s3service.max-thread-count) - please adjust JetS3t settings");
        } else if (maxConnectionCount < adminMaxThreadCount) {
            log.warn("Insufficient connections available (httpclient.max-connections) to run " + adminMaxThreadCount 
                + " simultaneous admin threads (s3service.admin-max-thread-count) - please adjust JetS3t settings");            
        }
    }    

    /**
     * @return
     * the underlying S3 service implementation.
     */
    public S3Service getS3Service() {
        return s3Service;
    }
    
    /**
     * Adds a service event listener to the set of listeners that will be notified of events.
     * 
     * @param listener
     * an event listener to add to the event notification chain.
     */
    public void addServiceEventListener(S3ServiceEventListener listener) {
        if (listener != null) {
            serviceEventListeners.add(listener);
        }
    }

    /**
     * Remvoes a service event listener to from set of listeners that will be notified of events.
     * 
     * @param listener
     * an event listener to remove from the event notification chain.
     */
    public void removeServiceEventListener(S3ServiceEventListener listener) {
        if (listener != null) {
            serviceEventListeners.remove(listener);
        }
    }

    /**
     * Sends a service event to each of the listeners registered with this service.
     * @param event
     * the event to send to this service's registered event listeners.
     */
    protected void fireServiceEvent(ServiceEvent event) {
        if (serviceEventListeners.size() == 0) {
            log.warn("S3ServiceMulti invoked without any S3ServiceEventListener objects, this is dangerous!");
        }
        Iterator listenerIter = serviceEventListeners.iterator();
        while (listenerIter.hasNext()) {
            S3ServiceEventListener listener = (S3ServiceEventListener) listenerIter.next();
            
            if (event instanceof CreateObjectsEvent) {
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

    
    /**
     * @return
     * true if the underlying S3Service implementation is authenticated.
     */
    public boolean isAuthenticatedConnection() {
        return s3Service.isAuthenticatedConnection();
    }

    /**
     * @return
     * the AWS credentials in the underlying S3Service.
     */
    public AWSCredentials getAWSCredentials() {
        return s3Service.getAWSCredentials();
    }
    
    /**
     * Creates multiple buckets, and sends {@link CreateBucketsEvent} notification events.
     * <p>
     * The maximum number of threads is controlled by the JetS3t configuration property 
     * <tt>s3service.admin-max-thread-count</tt>.
     * 
     * @param buckets
     * the buckets to create.
     */
    public void createBuckets(final S3Bucket[] buckets) {
        final List incompletedBucketList = new ArrayList();
        
        // Start all queries in the background.
        CreateBucketRunnable[] runnables = new CreateBucketRunnable[buckets.length];
        for (int i = 0; i < runnables.length; i++) {
            incompletedBucketList.add(buckets[i]);
            
            runnables[i] = new CreateBucketRunnable(buckets[i]);
        }
                
        int adminMaxThreadCount = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getIntProperty("s3service.admin-max-thread-count", 4);
        
        // Wait for threads to finish, or be cancelled.        
        (new ThreadGroupManager(runnables, adminMaxThreadCount, new ThreadWatcher(runnables.length)) {
            public void fireStartEvent(ThreadWatcher threadWatcher) {
                fireServiceEvent(CreateBucketsEvent.newStartedEvent(threadWatcher));        
            }
            public void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults) {
                incompletedBucketList.removeAll(completedResults);
                S3Bucket[] completedBuckets = (S3Bucket[]) completedResults
                    .toArray(new S3Bucket[completedResults.size()]);
                fireServiceEvent(CreateBucketsEvent.newInProgressEvent(threadWatcher, completedBuckets));
            }
            public void fireCancelEvent() {
                S3Bucket[] incompletedBuckets = (S3Bucket[]) incompletedBucketList
                    .toArray(new S3Bucket[incompletedBucketList.size()]);                
                fireServiceEvent(CreateBucketsEvent.newCancelledEvent(incompletedBuckets));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(CreateBucketsEvent.newCompletedEvent());                    
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(CreateBucketsEvent.newErrorEvent(throwable));
            }
        }).run();
    }
    
    /**
     * Creates multiple objects in a bucket, and sends {@link CreateObjectsEvent} notification events.
     * <p>
     * The maximum number of threads is controlled by the JetS3t configuration property 
     * <tt>s3service.max-thread-count</tt>.
     * 
     * @param bucket
     * the bucket to create the objects in 
     * @param objects
     * the objects to create/upload.
     */
    public void putObjects(final S3Bucket bucket, final S3Object[] objects) {    
        final List incompletedObjectsList = new ArrayList();
        final List progressWatchers = new ArrayList();
        
        // Start all queries in the background.
        CreateObjectRunnable[] runnables = new CreateObjectRunnable[objects.length];
        for (int i = 0; i < runnables.length; i++) {
            incompletedObjectsList.add(objects[i]);
            BytesProgressWatcher progressMonitor = new BytesProgressWatcher(objects[i].getContentLength());
            runnables[i] = new CreateObjectRunnable(bucket, objects[i], progressMonitor);
            progressWatchers.add(progressMonitor);
        }        
        
        int maxThreadCount = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getIntProperty("s3service.max-thread-count", 4);
        
        // Wait for threads to finish, or be cancelled.
        ThreadWatcher threadWatcher = new ThreadWatcher(
            (BytesProgressWatcher[]) progressWatchers.toArray(new BytesProgressWatcher[progressWatchers.size()]));
        (new ThreadGroupManager(runnables, maxThreadCount, threadWatcher) {
            public void fireStartEvent(ThreadWatcher threadWatcher) {
                fireServiceEvent(CreateObjectsEvent.newStartedEvent(threadWatcher));        
            }
            public void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults) {
                incompletedObjectsList.removeAll(completedResults);
                S3Object[] completedObjects = (S3Object[]) completedResults
                    .toArray(new S3Object[completedResults.size()]);
                fireServiceEvent(CreateObjectsEvent.newInProgressEvent(threadWatcher, completedObjects));
            }
            public void fireCancelEvent() {
                S3Object[] incompletedObjects = (S3Object[]) incompletedObjectsList
                    .toArray(new S3Object[incompletedObjectsList.size()]);
                fireServiceEvent(CreateObjectsEvent.newCancelledEvent(incompletedObjects));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(CreateObjectsEvent.newCompletedEvent());
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(CreateObjectsEvent.newErrorEvent(throwable));
            }
        }).run();
    }
    
    /**
     * Deletes multiple objects from a bucket, and sends {@link DeleteObjectsEvent} notification events.
     * <p>
     * The maximum number of threads is controlled by the JetS3t configuration property 
     * <tt>s3service.admin-max-thread-count</tt>.
     * 
     * @param bucket
     * the bucket containing the objects to be deleted
     * @param objects
     * the objects to delete
     */
    public void deleteObjects(final S3Bucket bucket, final S3Object[] objects) {
        final List objectsToDeleteList = new ArrayList();
        
        // Start all queries in the background.
        DeleteObjectRunnable[] runnables = new DeleteObjectRunnable[objects.length];
        for (int i = 0; i < runnables.length; i++) {
            objectsToDeleteList.add(objects[i]);
            runnables[i] = new DeleteObjectRunnable(bucket, objects[i]);
        }
        
        int adminMaxThreadCount = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getIntProperty("s3service.admin-max-thread-count", 4);
        
        // Wait for threads to finish, or be cancelled.        
        (new ThreadGroupManager(runnables, adminMaxThreadCount, new ThreadWatcher(runnables.length)) {
            public void fireStartEvent(ThreadWatcher threadWatcher) {
                fireServiceEvent(DeleteObjectsEvent.newStartedEvent(threadWatcher));        
            }
            public void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults) {
                objectsToDeleteList.removeAll(completedResults);
                S3Object[] deletedObjects = (S3Object[]) completedResults
                    .toArray(new S3Object[completedResults.size()]);                    
                fireServiceEvent(DeleteObjectsEvent.newInProgressEvent(threadWatcher, deletedObjects));
            }
            public void fireCancelEvent() {
                S3Object[] remainingObjects = (S3Object[]) objectsToDeleteList
                    .toArray(new S3Object[objectsToDeleteList.size()]);                    
                fireServiceEvent(DeleteObjectsEvent.newCancelledEvent(remainingObjects));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(DeleteObjectsEvent.newCompletedEvent());                    
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(DeleteObjectsEvent.newErrorEvent(throwable));
            }
        }).run();
    }
    
    /**
     * Retrieves multiple objects (details and data) from a bucket, and sends 
     * {@link GetObjectsEvent} notification events.
     * 
     * @param bucket
     * the bucket containing the objects to retrieve.
     * @param objects
     * the objects to retrieve.
     */
    public void getObjects(S3Bucket bucket, S3Object[] objects) {
        String[] objectKeys = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            objectKeys[i] = objects[i].getKey();
        }
        getObjects(bucket, objectKeys);
    }
    
    /**
     * Retrieves multiple objects (details and data) from a bucket, and sends 
     * {@link GetObjectsEvent} notification events.
     * <p>
     * The maximum number of threads is controlled by the JetS3t configuration property 
     * <tt>s3service.max-thread-count</tt>.
     * 
     * @param bucket
     * the bucket containing the objects to retrieve.
     * @param objectKeys
     * the key names of the objects to retrieve.
     */
    public void getObjects(final S3Bucket bucket, final String[] objectKeys) {
        final List pendingObjectKeysList = new ArrayList();

        // Start all queries in the background.
        GetObjectRunnable[] runnables = new GetObjectRunnable[objectKeys.length];
        for (int i = 0; i < runnables.length; i++) {
            pendingObjectKeysList.add(objectKeys[i]);
            runnables[i] = new GetObjectRunnable(bucket, objectKeys[i], false);
        }
        
        int maxThreadCount = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getIntProperty("s3service.max-thread-count", 4);
        
        // Wait for threads to finish, or be cancelled.        
        (new ThreadGroupManager(runnables, maxThreadCount, new ThreadWatcher(runnables.length)) {
            public void fireStartEvent(ThreadWatcher threadWatcher) {
                fireServiceEvent(GetObjectsEvent.newStartedEvent(threadWatcher));        
            }
            public void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults) {
                S3Object[] completedObjects = (S3Object[]) completedResults
                    .toArray(new S3Object[completedResults.size()]);
                for (int i = 0; i < completedObjects.length; i++) {
                    pendingObjectKeysList.remove(completedObjects[i].getKey());
                }
                fireServiceEvent(GetObjectsEvent.newInProgressEvent(threadWatcher, completedObjects));
            }
            public void fireCancelEvent() {
                List cancelledObjectsList = new ArrayList();
                Iterator iter = pendingObjectKeysList.iterator();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    cancelledObjectsList.add(new S3Object(key));
                }
                S3Object[] cancelledObjects = (S3Object[]) cancelledObjectsList
                    .toArray(new S3Object[cancelledObjectsList.size()]);
                fireServiceEvent(GetObjectsEvent.newCancelledEvent(cancelledObjects));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(GetObjectsEvent.newCompletedEvent());                    
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(GetObjectsEvent.newErrorEvent(throwable));
            }
        }).run();
    }
    
    /**
     * Retrieves details (but no data) about multiple objects from a bucket, and sends 
     * {@link GetObjectHeadsEvent} notification events.
     * 
     * @param bucket
     * the bucket containing the objects whose details will be retrieved.
     * @param objects
     * the objects with details to retrieve.
     */
    public void getObjectsHeads(S3Bucket bucket, S3Object[] objects) {
        String[] objectKeys = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            objectKeys[i] = objects[i].getKey();
        }
        getObjectsHeads(bucket, objectKeys);
    }

    /**
     * Retrieves details (but no data) about multiple objects from a bucket, and sends 
     * {@link GetObjectHeadsEvent} notification events.
     * <p>
     * The maximum number of threads is controlled by the JetS3t configuration property 
     * <tt>s3service.admin-max-thread-count</tt>.
     * 
     * @param bucket
     * the bucket containing the objects whose details will be retrieved.
     * @param objectKeys
     * the key names of the objects with details to retrieve.
     */
    public void getObjectsHeads(final S3Bucket bucket, final String[] objectKeys) {
        final List pendingObjectKeysList = new ArrayList();
        
        // Start all queries in the background.
        GetObjectRunnable[] runnables = new GetObjectRunnable[objectKeys.length];
        for (int i = 0; i < runnables.length; i++) {
            pendingObjectKeysList.add(objectKeys[i]);
            runnables[i] = new GetObjectRunnable(bucket, objectKeys[i], true);
        }

        int adminMaxThreadCount = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getIntProperty("s3service.admin-max-thread-count", 4);
        
        // Wait for threads to finish, or be cancelled.        
        (new ThreadGroupManager(runnables, adminMaxThreadCount, new ThreadWatcher(runnables.length)) {
            public void fireStartEvent(ThreadWatcher threadWatcher) {
                fireServiceEvent(GetObjectHeadsEvent.newStartedEvent(threadWatcher));        
            }
            public void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults) {
                S3Object[] completedObjects = (S3Object[]) completedResults
                    .toArray(new S3Object[completedResults.size()]);
                for (int i = 0; i < completedObjects.length; i++) {
                    pendingObjectKeysList.remove(completedObjects[i].getKey());
                }
                fireServiceEvent(GetObjectHeadsEvent.newInProgressEvent(threadWatcher, completedObjects));
            }
            public void fireCancelEvent() {
                List cancelledObjectsList = new ArrayList();
                Iterator iter = pendingObjectKeysList.iterator();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    cancelledObjectsList.add(new S3Object(key));
                }
                S3Object[] cancelledObjects = (S3Object[]) cancelledObjectsList
                    .toArray(new S3Object[cancelledObjectsList.size()]);
                fireServiceEvent(GetObjectHeadsEvent.newCancelledEvent(cancelledObjects));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(GetObjectHeadsEvent.newCompletedEvent());                    
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(GetObjectHeadsEvent.newErrorEvent(throwable));
            }
        }).run();
    }
    
    /**
     * Retrieves Acess Control List (ACL) information for multiple objects from a bucket, and sends 
     * {@link LookupACLEvent} notification events.
     * <p>
     * The maximum number of threads is controlled by the JetS3t configuration property 
     * <tt>s3service.admin-max-thread-count</tt>.
     * 
     * @param bucket
     * the bucket containing the objects
     * @param objects
     * the objects to retrieve ACL details for.
     */
    public void getObjectACLs(final S3Bucket bucket, final S3Object[] objects) {
        final List pendingObjectsList = new ArrayList();
        
        // Start all queries in the background.
        GetACLRunnable[] runnables = new GetACLRunnable[objects.length];
        for (int i = 0; i < runnables.length; i++) {
            pendingObjectsList.add(objects[i]);
            runnables[i] = new GetACLRunnable(bucket, objects[i]);
        }
        
        int adminMaxThreadCount = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getIntProperty("s3service.admin-max-thread-count", 4);
        
        // Wait for threads to finish, or be cancelled.        
        (new ThreadGroupManager(runnables, adminMaxThreadCount, new ThreadWatcher(runnables.length)) {
            public void fireStartEvent(ThreadWatcher threadWatcher) {
                fireServiceEvent(LookupACLEvent.newStartedEvent(threadWatcher));        
            }
            public void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults) {
                pendingObjectsList.removeAll(completedResults);
                S3Object[] completedObjects = (S3Object[]) completedResults
                    .toArray(new S3Object[completedResults.size()]);
                fireServiceEvent(LookupACLEvent.newInProgressEvent(threadWatcher, completedObjects));
            }
            public void fireCancelEvent() {
                S3Object[] cancelledObjects = (S3Object[]) pendingObjectsList
                    .toArray(new S3Object[pendingObjectsList.size()]);
                fireServiceEvent(LookupACLEvent.newCancelledEvent(cancelledObjects));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(LookupACLEvent.newCompletedEvent());
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(LookupACLEvent.newErrorEvent(throwable));
            }
        }).run();
    }

    /**
     * Updates/sets Acess Control List (ACL) information for multiple objects in a bucket, and sends 
     * {@link UpdateACLEvent} notification events.
     * <p>
     * The maximum number of threads is controlled by the JetS3t configuration property 
     * <tt>s3service.admin-max-thread-count</tt>.
     * 
     * @param bucket
     * the bucket containing the objects
     * @param objects
     * the objects to update/set ACL details for.
     */
    public void putACLs(final S3Bucket bucket, final S3Object[] objects) {
        final List pendingObjectsList = new ArrayList();

        // Start all queries in the background.
        PutACLRunnable[] runnables = new PutACLRunnable[objects.length];
        for (int i = 0; i < runnables.length; i++) {
            pendingObjectsList.add(objects[i]);
            runnables[i] = new PutACLRunnable(bucket, objects[i]);
        }
        
        int adminMaxThreadCount = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getIntProperty("s3service.admin-max-thread-count", 4);
        
        // Wait for threads to finish, or be cancelled.        
        (new ThreadGroupManager(runnables, adminMaxThreadCount, new ThreadWatcher(runnables.length)) {
            public void fireStartEvent(ThreadWatcher threadWatcher) {
                fireServiceEvent(UpdateACLEvent.newStartedEvent(threadWatcher));        
            }
            public void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults) {
                pendingObjectsList.removeAll(completedResults);
                S3Object[] completedObjects = (S3Object[]) completedResults
                    .toArray(new S3Object[completedResults.size()]);
                fireServiceEvent(UpdateACLEvent.newInProgressEvent(threadWatcher, completedObjects));
            }
            public void fireCancelEvent() {
                S3Object[] cancelledObjects = (S3Object[]) pendingObjectsList
                    .toArray(new S3Object[pendingObjectsList.size()]);
                fireServiceEvent(UpdateACLEvent.newCancelledEvent(cancelledObjects));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(UpdateACLEvent.newCompletedEvent());                    
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(UpdateACLEvent.newErrorEvent(throwable));
            }
        }).run();
    }

    /**
     * A convenience method to download multiple objects from S3 to pre-existing output streams, which
     * is particularly useful for downloading objects to files. This method sends 
     * {@link DownloadObjectsEvent} notification events.
     * <p>
     * The maximum number of threads is controlled by the JetS3t configuration property 
     * <tt>s3service.max-thread-count</tt>.
     * 
     * @param bucket
     * the bucket containing the objects
     * @param downloadPackages
     * an array of download packages containing the object to be downloaded, and able to build
     * an output stream where the object's contents will be written to.
     */
    public void downloadObjects(final S3Bucket bucket, final DownloadPackage[] downloadPackages) {
        final List progressWatchers = new ArrayList();        
        final List incompleteObjectDownloadList = new ArrayList();

        // Start all queries in the background.
        DownloadObjectRunnable[] runnables = new DownloadObjectRunnable[downloadPackages.length];
        final S3Object[] objects = new S3Object[downloadPackages.length];
        for (int i = 0; i < runnables.length; i++) {
            objects[i] = downloadPackages[i].getObject();
            BytesProgressWatcher progressMonitor = new BytesProgressWatcher(objects[i].getContentLength());
                        
            incompleteObjectDownloadList.add(objects[i]);
            progressWatchers.add(progressMonitor);
            
            runnables[i] = new DownloadObjectRunnable(bucket, objects[i].getKey(), 
                downloadPackages[i], progressMonitor);    
        }

        int maxThreadCount = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getIntProperty("s3service.max-thread-count", 4);
        
        // Wait for threads to finish, or be cancelled.        
        ThreadWatcher threadWatcher = new ThreadWatcher(
            (BytesProgressWatcher[]) progressWatchers.toArray(new BytesProgressWatcher[progressWatchers.size()]));
        (new ThreadGroupManager(runnables, maxThreadCount, threadWatcher) {
            public void fireStartEvent(ThreadWatcher threadWatcher) {
                fireServiceEvent(DownloadObjectsEvent.newStartedEvent(threadWatcher));
            }
            public void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults) {
                incompleteObjectDownloadList.removeAll(completedResults);                
                S3Object[] completedObjects = (S3Object[]) completedResults
                    .toArray(new S3Object[completedResults.size()]);
                fireServiceEvent(DownloadObjectsEvent.newInProgressEvent(threadWatcher, completedObjects));
            }
            public void fireCancelEvent() {
                S3Object[] incompleteObjects = (S3Object[]) incompleteObjectDownloadList
                    .toArray(new S3Object[incompleteObjectDownloadList.size()]);
                fireServiceEvent(DownloadObjectsEvent.newCancelledEvent(incompleteObjects));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(DownloadObjectsEvent.newCompletedEvent());                    
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(DownloadObjectsEvent.newErrorEvent(throwable));
            }
        }).run();
    }

    /**
     * A convenience method to download multiple objects from S3 to pre-existing output streams, which
     * is particularly useful for downloading objects to files. 
     * The S3 objects are represented as signed URLs.
     * <p>
     * This method sends {@link DownloadObjectsEvent} notification events.
     * <p>
     * The maximum number of threads is controlled by the JetS3t configuration property 
     * <tt>s3service.max-thread-count</tt>.
     * 
     * @param bucket
     * the bucket containing the objects
     * @param downloadPackages
     * an array of download packages containing the object to be downloaded, and able to build
     * an output stream where the object's contents will be written to.
     */
    public void downloadObjectsWithSignedURLs(final DownloadPackage[] downloadPackages) {
        final List progressWatchers = new ArrayList();        
        final List incompleteObjectDownloadList = new ArrayList();

        // Start all queries in the background.
        DownloadObjectRunnable[] runnables = new DownloadObjectRunnable[downloadPackages.length];
        final S3Object[] objects = new S3Object[downloadPackages.length];
        for (int i = 0; i < runnables.length; i++) {
            objects[i] = downloadPackages[i].getObject();
            BytesProgressWatcher progressMonitor = new BytesProgressWatcher(objects[i].getContentLength());
                        
            incompleteObjectDownloadList.add(objects[i]);
            progressWatchers.add(progressMonitor);
            
            runnables[i] = new DownloadObjectRunnable(downloadPackages[i], progressMonitor);    
        }

        int maxThreadCount = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getIntProperty("s3service.max-thread-count", 4);
        
        // Wait for threads to finish, or be cancelled.        
        ThreadWatcher threadWatcher = new ThreadWatcher(
            (BytesProgressWatcher[]) progressWatchers.toArray(new BytesProgressWatcher[progressWatchers.size()]));
        (new ThreadGroupManager(runnables, maxThreadCount, threadWatcher) {
            public void fireStartEvent(ThreadWatcher threadWatcher) {
                fireServiceEvent(DownloadObjectsEvent.newStartedEvent(threadWatcher));
            }
            public void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults) {
                incompleteObjectDownloadList.removeAll(completedResults);                
                S3Object[] completedObjects = (S3Object[]) completedResults
                    .toArray(new S3Object[completedResults.size()]);
                fireServiceEvent(DownloadObjectsEvent.newInProgressEvent(threadWatcher, completedObjects));
            }
            public void fireCancelEvent() {
                S3Object[] incompleteObjects = (S3Object[]) incompleteObjectDownloadList
                    .toArray(new S3Object[incompleteObjectDownloadList.size()]);
                fireServiceEvent(DownloadObjectsEvent.newCancelledEvent(incompleteObjects));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(DownloadObjectsEvent.newCompletedEvent());                    
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(DownloadObjectsEvent.newErrorEvent(throwable));
            }
        }).run();
    }

    /**
     * Retrieves multiple objects (details and data) from a bucket using signed GET URLs corresponding
     * to those objects.
     * <p>
     * Object retrieval using signed GET URLs can be performed without the underlying S3Service knowing 
     * the AWSCredentials for the target S3 account, however the underlying service must implement
     * the {@link SignedUrlHandler} interface. 
     * <p>
     * This method sends {@link GetObjectHeadsEvent} notification events.
     * <p>
     * The maximum number of threads is controlled by the JetS3t configuration property 
     * <tt>s3service.max-thread-count</tt>.
     * 
     * @param signedGetURLs
     * signed GET URL strings corresponding to the objects to be deleted.
     * 
     * @throws IllegalStateException
     * if the underlying S3Service does not implement {@link SignedUrlHandler}
     */
    public void getObjects(final String[] signedGetURLs) throws MalformedURLException, UnsupportedEncodingException {
        if (!(s3Service instanceof SignedUrlHandler)) {
            throw new IllegalStateException("S3ServiceMutli's underlying S3Service must implement the"
                + "SignedUrlHandler interface to make the method getObjects(String[] signedGetURLs) available");
        }
        
        final List pendingObjectKeysList = new ArrayList();

        // Start all queries in the background.
        GetObjectRunnable[] runnables = new GetObjectRunnable[signedGetURLs.length];
        for (int i = 0; i < runnables.length; i++) {
            URL url = new URL(signedGetURLs[i]);
            S3Object object = ServiceUtils.buildObjectFromPath(url.getPath());
            pendingObjectKeysList.add(object);
            
            runnables[i] = new GetObjectRunnable(signedGetURLs[i], false);
        }
        
        int maxThreadCount = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getIntProperty("s3service.max-thread-count", 4);

        // Wait for threads to finish, or be cancelled.        
        (new ThreadGroupManager(runnables, maxThreadCount, new ThreadWatcher(runnables.length)) {
            public void fireStartEvent(ThreadWatcher threadWatcher) {
                fireServiceEvent(GetObjectsEvent.newStartedEvent(threadWatcher));        
            }
            public void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults) {
                S3Object[] completedObjects = (S3Object[]) completedResults
                    .toArray(new S3Object[completedResults.size()]);
                for (int i = 0; i < completedObjects.length; i++) {
                    pendingObjectKeysList.remove(completedObjects[i].getKey());
                }
                fireServiceEvent(GetObjectsEvent.newInProgressEvent(threadWatcher, completedObjects));
            }
            public void fireCancelEvent() {
                List cancelledObjectsList = new ArrayList();
                Iterator iter = pendingObjectKeysList.iterator();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    cancelledObjectsList.add(new S3Object(key));
                }
                S3Object[] cancelledObjects = (S3Object[]) cancelledObjectsList
                    .toArray(new S3Object[cancelledObjectsList.size()]);
                fireServiceEvent(GetObjectsEvent.newCancelledEvent(cancelledObjects));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(GetObjectsEvent.newCompletedEvent());                    
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(GetObjectsEvent.newErrorEvent(throwable));
            }
        }).run();
    }
    
    /**
     * Retrieves details (but no data) about multiple objects using signed HEAD URLs corresponding
     * to those objects.
     * <p>
     * Detail retrieval using signed HEAD URLs can be performed without the underlying S3Service knowing 
     * the AWSCredentials for the target S3 account, however the underlying service must implement
     * the {@link SignedUrlHandler} interface. 
     * <p>
     * This method sends {@link GetObjectHeadsEvent} notification events.
     * <p>
     * The maximum number of threads is controlled by the JetS3t configuration property 
     * <tt>s3service.admin-max-thread-count</tt>.
     * 
     * @param signedHeadURLs
     * signed HEAD URL strings corresponding to the objects to be deleted.
     * 
     * @throws IllegalStateException
     * if the underlying S3Service does not implement {@link SignedUrlHandler}
     */
    public void getObjectsHeads(final String[] signedHeadURLs) throws MalformedURLException, UnsupportedEncodingException {
        if (!(s3Service instanceof SignedUrlHandler)) {
            throw new IllegalStateException("S3ServiceMutli's underlying S3Service must implement the"
                + "SignedUrlHandler interface to make the method getObjectsHeads(String[] signedHeadURLs) available");
        }
        
        final List pendingObjectKeysList = new ArrayList();
        
        // Start all queries in the background.
        GetObjectRunnable[] runnables = new GetObjectRunnable[signedHeadURLs.length];
        for (int i = 0; i < runnables.length; i++) {
            URL url = new URL(signedHeadURLs[i]);
            S3Object object = ServiceUtils.buildObjectFromPath(url.getPath());
            pendingObjectKeysList.add(object);

            runnables[i] = new GetObjectRunnable(signedHeadURLs[i], true);
        }
        
        int adminMaxThreadCount = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getIntProperty("s3service.admin-max-thread-count", 4);
        
        // Wait for threads to finish, or be cancelled.        
        (new ThreadGroupManager(runnables, adminMaxThreadCount, new ThreadWatcher(runnables.length)) {
            public void fireStartEvent(ThreadWatcher threadWatcher) {
                fireServiceEvent(GetObjectHeadsEvent.newStartedEvent(threadWatcher));        
            }
            public void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults) {
                S3Object[] completedObjects = (S3Object[]) completedResults
                    .toArray(new S3Object[completedResults.size()]);
                for (int i = 0; i < completedObjects.length; i++) {
                    pendingObjectKeysList.remove(completedObjects[i].getKey());
                }
                fireServiceEvent(GetObjectHeadsEvent.newInProgressEvent(threadWatcher, completedObjects));
            }
            public void fireCancelEvent() {
                List cancelledObjectsList = new ArrayList();
                Iterator iter = pendingObjectKeysList.iterator();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    cancelledObjectsList.add(new S3Object(key));
                }
                S3Object[] cancelledObjects = (S3Object[]) cancelledObjectsList
                    .toArray(new S3Object[cancelledObjectsList.size()]);
                fireServiceEvent(GetObjectHeadsEvent.newCancelledEvent(cancelledObjects));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(GetObjectHeadsEvent.newCompletedEvent());                    
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(GetObjectHeadsEvent.newErrorEvent(throwable));
            }
        }).run();
    }
    
    /**
     * Updates/sets Acess Control List (ACL) information for multiple objects in a bucket, and sends 
     * {@link UpdateACLEvent} notification events.
     * The S3 objects are represented as signed URLs.
     * <p>
     * The maximum number of threads is controlled by the JetS3t configuration property 
     * <tt>s3service.admin-max-thread-count</tt>.
     * 
     * @param bucket
     * the bucket containing the objects
     * @param objects
     * the objects to update/set ACL details for.
     */
    public void putObjectsACLs(final String[] signedURLs, final AccessControlList acl) throws MalformedURLException, UnsupportedEncodingException {
        final List pendingObjectsList = new ArrayList();

        // Start all queries in the background.
        PutACLRunnable[] runnables = new PutACLRunnable[signedURLs.length];
        for (int i = 0; i < runnables.length; i++) {
            URL url = new URL(signedURLs[i]);
            S3Object object = ServiceUtils.buildObjectFromPath(url.getPath());
            pendingObjectsList.add(object);
            runnables[i] = new PutACLRunnable(signedURLs[i], acl);
        }
        
        int adminMaxThreadCount = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getIntProperty("s3service.admin-max-thread-count", 4);
        
        // Wait for threads to finish, or be cancelled.        
        (new ThreadGroupManager(runnables, adminMaxThreadCount, new ThreadWatcher(runnables.length)) {
            public void fireStartEvent(ThreadWatcher threadWatcher) {
                fireServiceEvent(UpdateACLEvent.newStartedEvent(threadWatcher));        
            }
            public void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults) {
                pendingObjectsList.removeAll(completedResults);
                S3Object[] completedObjects = (S3Object[]) completedResults
                    .toArray(new S3Object[completedResults.size()]);
                fireServiceEvent(UpdateACLEvent.newInProgressEvent(threadWatcher, completedObjects));
            }
            public void fireCancelEvent() {
                S3Object[] cancelledObjects = (S3Object[]) pendingObjectsList
                    .toArray(new S3Object[pendingObjectsList.size()]);
                fireServiceEvent(UpdateACLEvent.newCancelledEvent(cancelledObjects));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(UpdateACLEvent.newCompletedEvent());                    
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(UpdateACLEvent.newErrorEvent(throwable));
            }
        }).run();
    }

    /**
     * Deletes multiple objects from a bucket using signed DELETE URLs corresponding to those objects.
     * <p>
     * Deletes using signed DELETE URLs can be performed without the underlying S3Service knowing 
     * the AWSCredentials for the target S3 account, however the underlying service must implement
     * the {@link SignedUrlHandler} interface. 
     * <p>
     * This method sends {@link DeleteObjectsEvent} notification events.
     * <p>
     * The maximum number of threads is controlled by the JetS3t configuration property 
     * <tt>s3service.admin-max-thread-count</tt>.
     * 
     * @param signedDeleteUrls
     * signed DELETE URL strings corresponding to the objects to be deleted.
     * 
     * @throws IllegalStateException
     * if the underlying S3Service does not implement {@link SignedUrlHandler}
     */
    public void deleteObjects(final String[] signedDeleteUrls) throws MalformedURLException, UnsupportedEncodingException {
        if (!(s3Service instanceof SignedUrlHandler)) {
            throw new IllegalStateException("S3ServiceMutli's underlying S3Service must implement the"
                + "SignedUrlHandler interface to make the method deleteObjects(String[] signedDeleteURLs) available");
        }

        final List objectsToDeleteList = new ArrayList();
        
        // Start all queries in the background.
        DeleteObjectRunnable[] runnables = new DeleteObjectRunnable[signedDeleteUrls.length];
        for (int i = 0; i < runnables.length; i++) {
            URL url = new URL(signedDeleteUrls[i]);
            S3Object object = ServiceUtils.buildObjectFromPath(url.getPath());
            objectsToDeleteList.add(object);
            
            runnables[i] = new DeleteObjectRunnable(signedDeleteUrls[i]);
        }
        
        int adminMaxThreadCount = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getIntProperty("s3service.admin-max-thread-count", 4);
        
        // Wait for threads to finish, or be cancelled.        
        (new ThreadGroupManager(runnables, adminMaxThreadCount, new ThreadWatcher(runnables.length)) {
            public void fireStartEvent(ThreadWatcher threadWatcher) {
                fireServiceEvent(DeleteObjectsEvent.newStartedEvent(threadWatcher));        
            }
            public void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults) {
                objectsToDeleteList.removeAll(completedResults);
                S3Object[] deletedObjects = (S3Object[]) completedResults
                    .toArray(new S3Object[completedResults.size()]);                    
                fireServiceEvent(DeleteObjectsEvent.newInProgressEvent(threadWatcher, deletedObjects));
            }
            public void fireCancelEvent() {
                S3Object[] remainingObjects = (S3Object[]) objectsToDeleteList
                    .toArray(new S3Object[objectsToDeleteList.size()]);                    
                fireServiceEvent(DeleteObjectsEvent.newCancelledEvent(remainingObjects));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(DeleteObjectsEvent.newCompletedEvent());                    
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(DeleteObjectsEvent.newErrorEvent(throwable));
            }
        }).run();
    }

    /**
     * Creates multiple objects in a bucket using a pre-signed PUT URL for each object.
     * <p>
     * Uploads using signed PUT URLs can be performed without the underlying S3Service knowing 
     * the AWSCredentials for the target S3 account, however the underlying service must implement
     * the {@link SignedUrlHandler} interface. 
     * <p>
     * This method sends {@link CreateObjectsEvent} notification events.
     * <p>
     * The maximum number of threads is controlled by the JetS3t configuration property 
     * <tt>s3service.max-thread-count</tt>.
     * 
     * @param signedPutUrlAndObjects
     * packages containing the S3Object to upload and the corresponding signed PUT URL.
     * 
     * @throws IllegalStateException
     * if the underlying S3Service does not implement {@link SignedUrlHandler}
     */
    public void putObjects(final SignedUrlAndObject[] signedPutUrlAndObjects) {
        if (!(s3Service instanceof SignedUrlHandler)) {
            throw new IllegalStateException("S3ServiceMutli's underlying S3Service must implement the"
                + "SignedUrlHandler interface to make the method putObjects(SignedUrlAndObject[] signedPutUrlAndObjects) available");
        }
        
        final List progressWatchers = new ArrayList();
        final List incompletedObjectsList = new ArrayList();
        
        // Calculate total byte count being transferred.
        S3Object objects[] = new S3Object[signedPutUrlAndObjects.length];
        for (int i = 0; i < signedPutUrlAndObjects.length; i++) {
            objects[i] = signedPutUrlAndObjects[i].getObject();
        }
                
        // Start all queries in the background.
        SignedPutRunnable[] runnables = new SignedPutRunnable[signedPutUrlAndObjects.length];
        for (int i = 0; i < runnables.length; i++) {
            BytesProgressWatcher progressMonitor = new BytesProgressWatcher(objects[i].getContentLength());
            progressWatchers.add(progressMonitor);
            incompletedObjectsList.add(signedPutUrlAndObjects[i].getObject());
            runnables[i] = new SignedPutRunnable(signedPutUrlAndObjects[i], progressMonitor);
        }        
        
        int maxThreadCount = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getIntProperty("s3service.max-thread-count", 4);
        
        // Wait for threads to finish, or be cancelled.        
        ThreadWatcher threadWatcher = new ThreadWatcher(
            (BytesProgressWatcher[]) progressWatchers.toArray(new BytesProgressWatcher[progressWatchers.size()]));
        (new ThreadGroupManager(runnables, maxThreadCount, threadWatcher) {
            public void fireStartEvent(ThreadWatcher threadWatcher) {
                fireServiceEvent(CreateObjectsEvent.newStartedEvent(threadWatcher));        
            }
            public void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults) {
                incompletedObjectsList.removeAll(completedResults);
                S3Object[] completedObjects = (S3Object[]) completedResults
                    .toArray(new S3Object[completedResults.size()]);
                fireServiceEvent(CreateObjectsEvent.newInProgressEvent(threadWatcher, completedObjects));
            }
            public void fireCancelEvent() {
                S3Object[] incompletedObjects = (S3Object[]) incompletedObjectsList
                    .toArray(new S3Object[incompletedObjectsList.size()]);
                fireServiceEvent(CreateObjectsEvent.newCancelledEvent(incompletedObjects));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(CreateObjectsEvent.newCompletedEvent());
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(CreateObjectsEvent.newErrorEvent(throwable));
            }
        }).run();
    }

    /**
     * Retrieves ACL information about multiple objects from a bucket using signed GET ACL URLs 
     * corresponding to those objects.
     * The S3 objects are represented as signed URLs.
     * <p>
     * Object retrieval using signed GET URLs can be performed without the underlying S3Service knowing 
     * the AWSCredentials for the target S3 account, however the underlying service must implement
     * the {@link SignedUrlHandler} interface. 
     * <p>
     * This method sends {@link LookupACLEvent} notification events.
     * <p>
     * The maximum number of threads is controlled by the JetS3t configuration property 
     * <tt>s3service.max-thread-count</tt>.
     * 
     * @param signedAclURLs
     * signed GET URL strings corresponding to the objects to be queried.
     * 
     * @throws IllegalStateException
     * if the underlying S3Service does not implement {@link SignedUrlHandler}
     */
    public void getObjectsACLs(final String[] signedAclURLs) throws MalformedURLException, UnsupportedEncodingException {
        if (!(s3Service instanceof SignedUrlHandler)) {
            throw new IllegalStateException("S3ServiceMutli's underlying S3Service must implement the"
                + "SignedUrlHandler interface to make the method getObjects(String[] signedGetURLs) available");
        }
        
        final List pendingObjectKeysList = new ArrayList();

        // Start all queries in the background.
        GetACLRunnable[] runnables = new GetACLRunnable[signedAclURLs.length];
        for (int i = 0; i < runnables.length; i++) {
            URL url = new URL(signedAclURLs[i]);
            S3Object object = ServiceUtils.buildObjectFromPath(url.getPath());
            pendingObjectKeysList.add(object);
            
            runnables[i] = new GetACLRunnable(signedAclURLs[i]);
        }
        
        int maxThreadCount = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
            .getIntProperty("s3service.max-thread-count", 4);

        // Wait for threads to finish, or be cancelled.        
        (new ThreadGroupManager(runnables, maxThreadCount, new ThreadWatcher(runnables.length)) {
            public void fireStartEvent(ThreadWatcher threadWatcher) {
                fireServiceEvent(LookupACLEvent.newStartedEvent(threadWatcher));        
            }
            public void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults) {
                S3Object[] completedObjects = (S3Object[]) completedResults
                    .toArray(new S3Object[completedResults.size()]);
                for (int i = 0; i < completedObjects.length; i++) {
                    pendingObjectKeysList.remove(completedObjects[i].getKey());
                }
                fireServiceEvent(LookupACLEvent.newInProgressEvent(threadWatcher, completedObjects));
            }
            public void fireCancelEvent() {
                List cancelledObjectsList = new ArrayList();
                Iterator iter = pendingObjectKeysList.iterator();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    cancelledObjectsList.add(new S3Object(key));
                }
                S3Object[] cancelledObjects = (S3Object[]) cancelledObjectsList
                    .toArray(new S3Object[cancelledObjectsList.size()]);
                fireServiceEvent(LookupACLEvent.newCancelledEvent(cancelledObjects));
            }
            public void fireCompletedEvent() {
                fireServiceEvent(LookupACLEvent.newCompletedEvent());                    
            }
            public void fireErrorEvent(Throwable throwable) {
                fireServiceEvent(LookupACLEvent.newErrorEvent(throwable));
            }
        }).run();
    }

    ///////////////////////////////////////////////
    // Private classes used by the methods above //
    ///////////////////////////////////////////////
    
    /**
     * All the operation threads used by this service extend this class, which provides common
     * methods used to retrieve the result object from a completed thread (via {@link #getResult()}
     * or force a thread to be interrupted (via {@link #forceInterrupt}. 
     */
    private abstract class AbstractRunnable implements Runnable {
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
    
    /**
     * Thread for performing the update/set of Access Control List information for an object.
     */
    private class PutACLRunnable extends AbstractRunnable {
        private S3Bucket bucket = null;
        private S3Object s3Object = null;        
        private String signedUrl = null;
        private AccessControlList signedUrlAcl = null;
        private Object result = null;
        
        public PutACLRunnable(S3Bucket bucket, S3Object s3Object) {
            this.bucket = bucket;
            this.s3Object = s3Object;
        }

        public PutACLRunnable(String signedAclUrl, AccessControlList signedUrlAcl) {
            this.signedUrl = signedAclUrl;
            this.signedUrlAcl = signedUrlAcl;
            this.bucket = null;
            this.s3Object = null;
        }

        public void run() {
            try {
                if (signedUrl == null) {
                    if (s3Object == null) {
                        s3Service.putBucketAcl(bucket);                    
                    } else {
                        s3Service.putObjectAcl(bucket, s3Object);                                        
                    }                
                    result = s3Object;
                } else {
                    SignedUrlHandler handler = (SignedUrlHandler) s3Service;
                    handler.putObjectAclWithSignedUrl(signedUrl, signedUrlAcl);
                    URL url = new URL(signedUrl);
                    S3Object object = ServiceUtils.buildObjectFromPath(url.getPath());
                    object.setAcl(signedUrlAcl);
                    result = object;                    
                }
            } catch (RuntimeException e) {
                result = e;
                throw e;
            } catch (Exception e) {
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

    /**
     * Thread for retrieving Access Control List information for an object.
     */
    private class GetACLRunnable extends AbstractRunnable {
        private S3Bucket bucket = null;
        private S3Object object = null;       
        private String signedAclUrl = null;
        private Object result = null;
        
        public GetACLRunnable(S3Bucket bucket, S3Object object) {
            this.bucket = bucket;
            this.object = object;
        }

        public GetACLRunnable(String signedAclUrl) {
            this.signedAclUrl = signedAclUrl;
            this.bucket = null;
            this.object = null;
        }

        public void run() {
            try {
                if (signedAclUrl == null) {
                    AccessControlList acl = s3Service.getObjectAcl(bucket, object.getKey());
                    object.setAcl(acl);
                    result = object;
                } else {
                    SignedUrlHandler handler = (SignedUrlHandler) s3Service;
                    AccessControlList acl = handler.getObjectAclWithSignedUrl(signedAclUrl);
                    URL url = new URL(signedAclUrl);
                    object = ServiceUtils.buildObjectFromPath(url.getPath());
                    object.setAcl(acl);
                    result = object;
                }
            } catch (RuntimeException e) {
                result = e;
                throw e;
            } catch (Exception e) {
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

    /**
     * Thread for deleting an object.
     */
    private class DeleteObjectRunnable extends AbstractRunnable {
        private S3Bucket bucket = null;
        private S3Object object = null;
        private String signedDeleteUrl = null;
        private Object result = null;
        
        public DeleteObjectRunnable(S3Bucket bucket, S3Object object) {
            this.signedDeleteUrl = null;
            this.bucket = bucket;
            this.object = object;
        }

        public DeleteObjectRunnable(String signedDeleteUrl) {
            this.signedDeleteUrl = signedDeleteUrl;
            this.bucket = null;
            this.object = null;
        }

        public void run() {
            try {
                if (signedDeleteUrl == null) {
                    s3Service.deleteObject(bucket, object.getKey());                    
                    result = object;
                } else {
                    SignedUrlHandler handler = (SignedUrlHandler) s3Service;
                    handler.deleteObjectWithSignedUrl(signedDeleteUrl);
                    URL url = new URL(signedDeleteUrl);
                    result = ServiceUtils.buildObjectFromPath(url.getPath());
                }
            } catch (RuntimeException e) {
                result = e;
                throw e;
            } catch (Exception e) {
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

    /**
     * Thread for creating a bucket.
     */
    private class CreateBucketRunnable extends AbstractRunnable {
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

    /**
     * Thread for creating/uploading an object. The upload of any object data is monitored with a
     * {@link ProgressMonitoredInputStream} and can be can cancelled as the input stream is wrapped in
     * an {@link InterruptableInputStream}.
     */
    private class CreateObjectRunnable extends AbstractRunnable {
        private S3Bucket bucket = null;
        private S3Object s3Object = null;    
        private InterruptableInputStream interruptableInputStream = null;
        private BytesProgressWatcher progressMonitor = null;
        
        private Object result = null;
        
        public CreateObjectRunnable(S3Bucket bucket, S3Object s3Object, BytesProgressWatcher progressMonitor) {
            this.bucket = bucket;
            this.s3Object = s3Object;
            this.progressMonitor = progressMonitor;
        }

        public void run() {
            try {
                if (s3Object.getDataInputStream() != null) {
                    interruptableInputStream = new InterruptableInputStream(s3Object.getDataInputStream());
                    ProgressMonitoredInputStream pmInputStream = new ProgressMonitoredInputStream(
                        interruptableInputStream, progressMonitor);
                    s3Object.setDataInputStream(pmInputStream);
                }
                result = s3Service.putObject(bucket, s3Object);
            } catch (S3ServiceException e) {
                result = e;
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

    /**
     * Thread for retrieving an object.
     */
    private class GetObjectRunnable extends AbstractRunnable {
        private S3Bucket bucket = null;
        private String objectKey = null;
        private String signedGetOrHeadUrl = null;
        private boolean headOnly = false;
        
        private Object result = null;
        
        public GetObjectRunnable(S3Bucket bucket, String objectKey, boolean headOnly) {
            this.signedGetOrHeadUrl = null;
            this.bucket = bucket;
            this.objectKey = objectKey;
            this.headOnly = headOnly;
        }

        public GetObjectRunnable(String signedGetOrHeadUrl, boolean headOnly) {
            this.signedGetOrHeadUrl = signedGetOrHeadUrl;
            this.bucket = null;
            this.objectKey = null;
            this.headOnly = headOnly;
        }

        public void run() {
            try {
                if (headOnly) {
                    if (signedGetOrHeadUrl == null) {
                        result = s3Service.getObjectDetails(bucket, objectKey);
                    } else {
                        SignedUrlHandler handler = (SignedUrlHandler) s3Service;
                        result = handler.getObjectDetailsWithSignedUrl(signedGetOrHeadUrl);
                    }
                } else {
                    if (signedGetOrHeadUrl == null) {
                        result = s3Service.getObject(bucket, objectKey);
                    } else {
                        SignedUrlHandler handler = (SignedUrlHandler) s3Service;
                        result = handler.getObjectWithSignedUrl(signedGetOrHeadUrl);
                    }
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
    
    /**
     * Thread for downloading an object. The download of any object data is monitored with a
     * {@link ProgressMonitoredInputStream} and can be can cancelled as the input stream is wrapped in
     * an {@link InterruptableInputStream}.
     */
    private class DownloadObjectRunnable extends AbstractRunnable {
        private String objectKey = null;
        private S3Bucket bucket = null;
        private DownloadPackage downloadPackage = null;
        private InterruptableInputStream interruptableInputStream = null;
        private BytesProgressWatcher progressMonitor = null;
        
        private Object result = null;
      
        public DownloadObjectRunnable(S3Bucket bucket, String objectKey, DownloadPackage downloadPackage, 
            BytesProgressWatcher progressMonitor) 
        {
            this.bucket = bucket;
            this.objectKey = objectKey;
            this.downloadPackage = downloadPackage;
            this.progressMonitor = progressMonitor;
        }
        
        public DownloadObjectRunnable(DownloadPackage downloadPackage, BytesProgressWatcher progressMonitor) 
        {
            this.downloadPackage = downloadPackage;
            this.progressMonitor = progressMonitor;
        }

        public void run() {            
            BufferedInputStream bufferedInputStream = null;
            BufferedOutputStream bufferedOutputStream = null;
            S3Object object = null;

            try {
            	if (!downloadPackage.isSignedDownload()) {
            		object = s3Service.getObject(bucket, objectKey);
            	} else {
                    SignedUrlHandler handler = (SignedUrlHandler) s3Service;
                    object = handler.getObjectWithSignedUrl(downloadPackage.getSignedUrl());
            	}

                // Setup monitoring of stream bytes tranferred. 
                interruptableInputStream = new InterruptableInputStream(object.getDataInputStream()); 
                bufferedInputStream = new BufferedInputStream(
                    new ProgressMonitoredInputStream(interruptableInputStream, progressMonitor));
                
                bufferedOutputStream = new BufferedOutputStream(
                    downloadPackage.getOutputStream());

                try {
                    byte[] buffer = new byte[1024];
                    int byteCount = -1;
    
                    while ((byteCount = bufferedInputStream.read(buffer)) != -1) {
                        bufferedOutputStream.write(buffer, 0, byteCount);
                    }
                } finally {
                    if (bufferedOutputStream != null) {
                        bufferedOutputStream.close();                        
                    }
                    if (bufferedInputStream != null) {
                        bufferedInputStream.close();                    
                    }
                }                

                object.setDataInputStream(null);
                object.setDataInputFile(downloadPackage.getDataFile());
                result = object;
            } catch (Throwable t) {
                result = t;
            } finally {
                if (bufferedInputStream != null) {
                    try {
                        bufferedInputStream.close();    
                    } catch (Exception e) {                    
                        log.error("Unable to close Object input stream", e);
                    }
                }
                if (bufferedOutputStream != null) {
                    try {
                        bufferedOutputStream.close();                    
                    } catch (Exception e) {
                        log.error("Unable to close download output stream", e);
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
    
    /**
     * Thread for creating/uploading an object using a pre-signed PUT URL. The upload of any object 
     * data is monitored with a {@link ProgressMonitoredInputStream} and can be can cancelled as 
     * the input stream is wrapped in an {@link InterruptableInputStream}.
     */
    private class SignedPutRunnable extends AbstractRunnable {
        private SignedUrlAndObject signedUrlAndObject = null;    
        private InterruptableInputStream interruptableInputStream = null;
        private BytesProgressWatcher progressMonitor = null;
        
        private Object result = null;
        
        public SignedPutRunnable(SignedUrlAndObject signedUrlAndObject, BytesProgressWatcher progressMonitor) {
            this.signedUrlAndObject = signedUrlAndObject;
            this.progressMonitor = progressMonitor;
        }

        public void run() {
            try {
                if (signedUrlAndObject.getObject().getDataInputStream() != null) {
                    interruptableInputStream = new InterruptableInputStream(
                        signedUrlAndObject.getObject().getDataInputStream());
                    ProgressMonitoredInputStream pmInputStream = new ProgressMonitoredInputStream(
                        interruptableInputStream, progressMonitor);
                    signedUrlAndObject.getObject().setDataInputStream(pmInputStream);
                }
                SignedUrlHandler signedPutUploader = (SignedUrlHandler) s3Service;
                result = signedPutUploader.putObjectWithSignedUrl(
                    signedUrlAndObject.getSignedUrl(), signedUrlAndObject.getObject());
            } catch (S3ServiceException e) {
                result = e;
            } finally {
                try {
                    signedUrlAndObject.getObject().closeDataInputStream();
                } catch (IOException e) {
                    log.error("Unable to close Object's input stream", e);                        
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

    

    /**
     * The thread group manager is responsible for starting, running and stopping the set of threads
     * required to perform an S3 operation.
     * <p>
     * The manager starts all the threads, monitors their progress and stops threads when they are   
     * cancelled or an error occurs - all the while firing the appropriate {@link ServiceEvent} event
     * notifications.
     */
    private abstract class ThreadGroupManager {
        private final Log log = LogFactory.getLog(ThreadGroupManager.class);
        private int maxThreadCount = 1;
        
        /**
         * the set of runnable objects to execute.
         */
        private AbstractRunnable[] runnables = null;
        
        /**
         * Thread objects that are currently running, where the index corresponds to the 
         * runnables index. Any AbstractThread runnable that is not started, or has completed,
         * will have a null value in this array.
         */
        private Thread[] threads = null;
        
        /**
         * set of flags indicating which runnable items have been started
         */
        private boolean started[] = null;
        
        /**
         * set of flags indicating which threads have already had In Progress events fired on
         * their behalf. These threads have finished running.
         */
        private boolean alreadyFired[] = null;
        
        private ThreadWatcher threadWatcher = null;
        
        private long lastProgressEventFiredTime = 0;
        
        
        public ThreadGroupManager(AbstractRunnable[] runnables, int maxThreadCount, ThreadWatcher threadWatcher) {            
            this.runnables = runnables;
            this.maxThreadCount = maxThreadCount;
            this.threadWatcher = threadWatcher;
            
            this.threads = new Thread[runnables.length];
            started = new boolean[runnables.length]; // All values initialized to false.
            alreadyFired = new boolean[runnables.length]; // All values initialized to false.
        }
        
        /**
         * Determine which threads, if any, have finished since the last time an In Progress event
         * was fired.
         * 
         * @return
         * a list of the threads that finished since the last In Progress event was fired. This list may
         * be empty.
         * 
         * @throws Throwable
         */
        private List getNewlyCompletedResults() throws Throwable 
        {
            ArrayList completedResults = new ArrayList();
            
            for (int i = 0; i < threads.length; i++) {
                if (!alreadyFired[i] && started[i] && !threads[i].isAlive()) {
                    alreadyFired[i] = true;
                    log.debug("Thread " + (i+1) + " of " + threads.length 
                        + " has recently completed, releasing resources");

                    if (runnables[i].getResult() instanceof Throwable) {
                        Throwable throwable = (Throwable) runnables[i].getResult();
                        runnables[i] = null;
                        threads[i] = null;
                        throw throwable;
                    } else {
                        completedResults.add(runnables[i].getResult());
                        runnables[i] = null;
                        threads[i] = null;
                    }                    
                }
            }
            return completedResults;
        }
        
        /**
         * Starts pending threads such that the total of running threads never exceeds the 
         * maximum count set in the jets3t property <i>s3service.max-thread-count</i>.
         *        
         * @throws Throwable
         */
        private void startPendingThreads() 
            throws Throwable 
        {
            // Count active threads that are running (ie have been started but final event not fired)
            int runningThreadCount = 0;
            for (int i = 0; i < runnables.length; i++) {
                if (started[i] && !alreadyFired[i]) {
                    runningThreadCount++;
                }
            }

            // Start threads until we are running the maximum number allowed.
            for (int i = 0; runningThreadCount < maxThreadCount && i < started.length; i++) {
                if (!started[i]) {
                    threads[i] = new Thread(runnables[i]);                    
                    threads[i].start();
                    started[i] = true;
                    runningThreadCount++;
                    log.debug("Thread " + (i+1) + " of " + runnables.length + " has started");
                }
            }
        }
        
        /**
         * @return
         * the number of threads that have not finished running (sum of those currently running, and those awaiting start)
         */
        private int getPendingThreadCount() {
            int pendingThreadCount = 0;
            for (int i = 0; i < runnables.length; i++) {
                if (!alreadyFired[i]) {
                    pendingThreadCount++;
                }
            }
            return pendingThreadCount;
        }
        
        /**
         * Invokes the {@link AbstractRunnable#forceInterrupt} on all threads being managed.
         *
         */
        private void forceInterruptAllRunnables() {
            log.debug("Setting force interrupt flag on all runnables");
            for (int i = 0; i < runnables.length; i++) {
                if (runnables[i] != null) {
                    runnables[i].forceInterrupt();
                    runnables[i] = null;
                }
            }
        }
        
        /**
         * Runs and manages all the threads involved in an S3 multi-operation.
         *
         */
        public void run() {
            log.debug("Started ThreadManager");
            
            final boolean[] interrupted = new boolean[] { false };
            
            /*
             * Create a cancel event trigger, so all the managed threads can be cancelled if required.
             */
            final CancelEventTrigger cancelEventTrigger = new CancelEventTrigger() {
                private static final long serialVersionUID = 6328417466929608235L;

                public void cancelTask(Object eventSource) {
                    log.debug("Cancel task invoked on ThreadManager");
                    
                    // Flag that this ThreadManager class should shutdown.
                    interrupted[0] = true;
                    
                    // Set force interrupt flag for all runnables.
                    forceInterruptAllRunnables();
                }
            };
                        
            // Actual thread management happens in the code block below.
            try {
                // Start some threads
                startPendingThreads();                
                
                threadWatcher.updateThreadsCompletedCount(0, cancelEventTrigger); 
                fireStartEvent(threadWatcher);
                
                // Loop while threads haven't been interrupted/cancelled, and at least one thread is 
                // still active (ie hasn't finished its work)
                while (!interrupted[0] && getPendingThreadCount() > 0) {
                    try {
                        Thread.sleep(100);
    
                        if (interrupted[0]) {
                            // Do nothing, we've been interrupted during sleep.                        
                        } else {
                            if (System.currentTimeMillis() - lastProgressEventFiredTime > sleepTime) {                                
                                // Fire progress event.
                                int completedThreads = runnables.length - getPendingThreadCount();                    
                                threadWatcher.updateThreadsCompletedCount(completedThreads, cancelEventTrigger);
                                List completedResults = getNewlyCompletedResults();
                                
                                lastProgressEventFiredTime = System.currentTimeMillis();
                                fireProgressEvent(threadWatcher, completedResults);                                
                            }
                            
                            // Start more threads.
                            startPendingThreads();                
                        }
                    } catch (InterruptedException e) {
                        interrupted[0] = true;
                        forceInterruptAllRunnables();
                    }
                }        
                
                if (interrupted[0]) {
                    fireCancelEvent();
                } else {
                    int completedThreads = runnables.length - getPendingThreadCount();                    
                    threadWatcher.updateThreadsCompletedCount(completedThreads, cancelEventTrigger);
                    List completedResults = getNewlyCompletedResults();                    
                    fireProgressEvent(threadWatcher, completedResults);
                    if (completedResults.size() > 0) {
                        log.debug(completedResults.size() + " threads have recently completed");
                    }                    
                    fireCompletedEvent();
                }
            } catch (Throwable t) {
                log.error("A thread failed with an exception. Firing ERROR event and cancelling all threads", t);
                // Set force interrupt flag for all runnables.
                forceInterruptAllRunnables();
                
                fireErrorEvent(t);                
            }
        }
        
        public abstract void fireStartEvent(ThreadWatcher threadWatcher);
        
        public abstract void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults);
        
        public abstract void fireCompletedEvent();

        public abstract void fireCancelEvent();

        public abstract void fireErrorEvent(Throwable t);
    }
    
}
