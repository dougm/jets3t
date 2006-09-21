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

import org.jets3t.service.model.S3Object;

/**
 * Multi-threaded service event fired by {@link S3ServiceMulti#getObjects}.
 * <p>
 * EVENT_IN_PROGRESS events include an array of the {@link S3Object}s that have been retrieved
 * since the last progress event was fired. These objects are available via 
 * {@link #getCompletedObjects()}.
 * <p>
 * EVENT_CANCELLED events include an array of the {@link S3Object}s that had not been retrieved
 * before the operation was cancelled. These objects are available via 
 * {@link #getCancelledObjects()}.   
 *  
 * @author James Murty
 */
public class GetObjectsEvent extends ServiceEvent {	
    private S3Object[] objects = null;
    
    private GetObjectsEvent(int eventCode) {
        super(eventCode);
    }
    

    public static GetObjectsEvent newErrorEvent(Throwable t) {
        GetObjectsEvent event = new GetObjectsEvent(EVENT_ERROR);
        event.setErrorCause(t);
        return event;
    }

    public static GetObjectsEvent newStartedEvent(ThreadWatcher threadWatcher) {
        GetObjectsEvent event = new GetObjectsEvent(EVENT_STARTED);
        event.setThreadWatcher(threadWatcher);
        return event;
    }

    public static GetObjectsEvent newInProgressEvent(ThreadWatcher threadWatcher, S3Object[] completedObjects) {
        GetObjectsEvent event = new GetObjectsEvent(EVENT_IN_PROGRESS);
        event.setThreadWatcher(threadWatcher);
        event.setObjects(completedObjects);
        return event;
    }

    public static GetObjectsEvent newCompletedEvent() {
        GetObjectsEvent event = new GetObjectsEvent(EVENT_COMPLETED);
        return event;
    }
    
    public static GetObjectsEvent newCancelledEvent(S3Object[] incompletedObjects) {
        GetObjectsEvent event = new GetObjectsEvent(EVENT_CANCELLED);
        event.setObjects(incompletedObjects);
        return event;
    }

    private void setObjects(S3Object[] objects) {
        this.objects = objects;
    }
    
    /**
     * @return
     * the S3Objects that have been retrieved since the last progress event was fired.
     * @throws IllegalStateException
     * completed objects are only available from EVENT_IN_PROGRESS events.
     */
    public S3Object[] getCompletedObjects() throws IllegalStateException {
        if (getEventCode() != EVENT_IN_PROGRESS) {
            throw new IllegalStateException("Completed Objects are only available from EVENT_IN_PROGRESS events");
        }                
        return objects;
    }
    
    /**
     * @return
     * the S3Objects that were not retrieved before the operation was cancelled.
     * @throws IllegalStateException
     * cancelled objects are only available from EVENT_CANCELLED events.
     */
    public S3Object[] getCancelledObjects() throws IllegalStateException {
        if (getEventCode() != EVENT_CANCELLED) {
            throw new IllegalStateException("Cancelled Objects are  only available from EVENT_CANCELLED events");
        }                
        return objects;
    }

}
