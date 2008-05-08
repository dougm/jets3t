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

import java.util.Map;

import org.jets3t.service.S3Service;
import org.jets3t.service.model.S3Object;

/**
 * Multi-threaded service event fired by 
 * {@link S3ServiceMulti#copyObjects(String, String, String[], S3Object[], boolean)}.
 * <p>
 * EVENT_IN_PROGRESS events include an array of the Map results returned by the
 * copy operation, see 
 * {@link S3Service#copyObject(String, String, String, S3Object, boolean)}. 
 * These objects are available via {@link #getCopyResults()}.
 * <p>
 * EVENT_CANCELLED events include an array of the {@link S3Object}s that had not been 
 * copied before the operation was cancelled. These objects are available via 
 * {@link #getCancelledObjects()}.   
 *  
 * @author James Murty
 */
public class CopyObjectsEvent extends ServiceEvent {
    private Map[] results = null;
    private S3Object[] copyCancelledObjects = null;
    private S3Object[] destinationObjects = null;
    private String[] sourceObjectKeys = null;
    
    private CopyObjectsEvent(int eventCode, Object uniqueOperationId) {
        super(eventCode, uniqueOperationId);
    }
    

    public static CopyObjectsEvent newErrorEvent(Throwable t, Object uniqueOperationId) {
        CopyObjectsEvent event = new CopyObjectsEvent(EVENT_ERROR, uniqueOperationId);
        event.setErrorCause(t);
        return event;
    }

    public static CopyObjectsEvent newStartedEvent(ThreadWatcher threadWatcher, Object uniqueOperationId) {
        CopyObjectsEvent event = new CopyObjectsEvent(EVENT_STARTED, uniqueOperationId);
        event.setThreadWatcher(threadWatcher);
        return event;
    }

    public static CopyObjectsEvent newInProgressEvent(ThreadWatcher threadWatcher, 
        Map[] completedResults, Object uniqueOperationId) 
    {
        CopyObjectsEvent event = new CopyObjectsEvent(EVENT_IN_PROGRESS, uniqueOperationId);
        event.setThreadWatcher(threadWatcher);
        event.setResults(completedResults);
        return event;
    }

    public static CopyObjectsEvent newCompletedEvent(Object uniqueOperationId, String[] sourceObjectKeys, 
        S3Object[] destinationObjects) 
    {
        CopyObjectsEvent event = new CopyObjectsEvent(EVENT_COMPLETED, uniqueOperationId);
        event.setDestinationObjects(destinationObjects);
        event.setSourceObjectKeys(sourceObjectKeys);
        return event;
    }
    
    public static CopyObjectsEvent newCancelledEvent(S3Object[] incompletedObjects, Object uniqueOperationId) {
        CopyObjectsEvent event = new CopyObjectsEvent(EVENT_CANCELLED, uniqueOperationId);
        event.setCopyCancelledObjects(incompletedObjects);
        return event;
    }

    private void setResults(Map[] results) {
        this.results = results;
    }
    
    private void setSourceObjectKeys(String[] sourceObjectKeys) {
        this.sourceObjectKeys = sourceObjectKeys;
    }

    private void setCopyCancelledObjects(S3Object[] objects) {
        this.copyCancelledObjects = objects;
    }

    private void setDestinationObjects(S3Object[] objects) {
        this.destinationObjects = objects;
    }

    /**
     * @return
     * the Map results for the objects that have been copied since the last progress event was fired.
     * @throws IllegalStateException
     * results are only available from EVENT_IN_PROGRESS events.
     */
    public Map[] getCopyResults() throws IllegalStateException {
        if (getEventCode() != EVENT_IN_PROGRESS) {
            throw new IllegalStateException("Copy results are only available from EVENT_IN_PROGRESS events");
        }                
        return results;
    }
    
    /**
     * @return
     * the S3Objects that were not copied before the operation was cancelled.
     * @throws IllegalStateException
     * cancelled objects are only available from EVENT_CANCELLED events.
     */
    public S3Object[] getCancelledObjects() throws IllegalStateException {
        if (getEventCode() != EVENT_CANCELLED) {
            throw new IllegalStateException("Cancelled Objects are only available from EVENT_CANCELLED events");
        }                
        return copyCancelledObjects;
    }

    /**
     * @return
     * the S3Objects that were created by a a successful copy operation.
     * @throws IllegalStateException
     * copied objects are only available from EVENT_COMPLETED events.
     */
    public S3Object[] getCopiedObjects() throws IllegalStateException {
        if (getEventCode() != EVENT_COMPLETED) {
            throw new IllegalStateException("Cancelled Objects are only available from EVENT_COMPLETED events");
        }                
        return destinationObjects;
    }

    /**
     * @return
     * the key names of source objects that were copied in a successful operation.
     * @throws IllegalStateException
     * source object key names are only available from EVENT_COMPLETED events.
     */
    public String[] getSourceObjectKeys() throws IllegalStateException {
        if (getEventCode() != EVENT_COMPLETED) {
            throw new IllegalStateException("Source Objects are only available from EVENT_COMPLETED events");
        }                
        return sourceObjectKeys;
    }

}
