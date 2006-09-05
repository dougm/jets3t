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

public class DeleteObjectsEvent extends ServiceEvent {
    private S3Object[] objects = null;
    
    private DeleteObjectsEvent(int eventCode) {
        super(eventCode);
    }
    

    public static DeleteObjectsEvent newErrorEvent(Throwable t) {
        DeleteObjectsEvent event = new DeleteObjectsEvent(EVENT_ERROR);
        event.setErrorCause(t);
        return event;
    }

    public static DeleteObjectsEvent newStartedEvent(ThreadWatcher threadWatcher) {
        DeleteObjectsEvent event = new DeleteObjectsEvent(EVENT_STARTED);
        event.setThreadWatcher(threadWatcher);
        return event;
    }

    public static DeleteObjectsEvent newInProgressEvent(ThreadWatcher threadWatcher, S3Object[] deletedObjects) {
        DeleteObjectsEvent event = new DeleteObjectsEvent(EVENT_IN_PROGRESS);
        event.setThreadWatcher(threadWatcher);
        event.setObjects(deletedObjects);
        return event;
    }

    public static DeleteObjectsEvent newCompletedEvent() {
        DeleteObjectsEvent event = new DeleteObjectsEvent(EVENT_COMPLETED);
        return event;
    }
    
    public static DeleteObjectsEvent newCancelledEvent(S3Object[] remainingObjects) {
        DeleteObjectsEvent event = new DeleteObjectsEvent(EVENT_CANCELLED);
        event.setObjects(remainingObjects);
        return event;
    }

    private void setObjects(S3Object[] objects) {
        this.objects = objects;
    }
    
    public S3Object[] getDeletedObjects() throws IllegalStateException {
        if (getEventCode() != EVENT_IN_PROGRESS) {
            throw new IllegalStateException("Deleted Objects are only available from EVENT_IN_PROGRESS events");
        }                
        return objects;
    }
    
    public S3Object[] getCancelledObjects() throws IllegalStateException {
        if (getEventCode() != EVENT_CANCELLED) {
            throw new IllegalStateException("Cancelled Objects are  only available from EVENT_CANCELLED events");
        }                
        return objects;
    }

}
