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

/**
 * Base class of all events produced by {@link S3ServiceMulti}.
 * <p>
 * Every event has an event code, which indicates the state of a process when the event was
 * generated. The event code will also give a guide as to what information the event will contain.
 * <p>
 * The event codes, and their meanings, are:
 * <ul>
 * <li>EVENT_STARTED: An S3 operation has commenced, but no work has yet been done.</li>
 * <li>EVENT_IN_PROGRESS: An S3 operation is in progress. Progress events are fired at regular time
 *     intervals, and will include information about any sub-operations that have been completed 
 *     as part of the overall operation.</li>
 * <li>EVENT_COMPLETED: An S3 operation has completed, and all the work has been done.</li>
 * <li>EVENT_CANCELLED: An S3 operation was started but has been cancelled before it could complete. 
 *     If an operation is cancelled, this event will be fired instead of the EVENT_COMPLETED.</li>
 * <li>EVENT_ERROR: An S3 operation has failed and an exception has been thrown. The error 
 *     will be availble from {@link #getErrorCause()}</li>
 * </ul>
 * <p>
 * EVENT_STARTED and EVENT_IN_PROGRESS events may include a {@link ThreadWatcher} object containing
 * information about the progress of an S3 operation.
 * <p>
 * See the event object specific to the operation you are performing for more details about the
 * information available in service events. 
 * 
 * @author James Murty
 */
public abstract class ServiceEvent {
    public static final int EVENT_ERROR = 0;
    public static final int EVENT_STARTED = 1;
    public static final int EVENT_COMPLETED = 2;
    public static final int EVENT_IN_PROGRESS = 3;
    public static final int EVENT_CANCELLED = 4;

    private int eventCode = 0;
    private Throwable t = null;
    private ThreadWatcher threadWatcher = null;

    protected ServiceEvent(int eventCode) {
        this.eventCode = eventCode;
    }
    
    protected void setThreadWatcher(ThreadWatcher threadWatcher) {
        this.threadWatcher = threadWatcher;
    }
    
    protected void setErrorCause(Throwable t) {
        this.t = t;
    }
    
    /**
     * @return
     * the event code, which will match one of this class's public static EVENT_XXX variables.
     */
    public int getEventCode() {
        return eventCode;
    }

    /**
     * @return
     * the error that caused an S3 operation to fail. 
     * @throws IllegalStateException
     * an error cause can only be retrieved from an EVENT_ERROR event.
     */
    public Throwable getErrorCause() throws IllegalStateException {
        if (eventCode != EVENT_ERROR) {
            throw new IllegalStateException("Error Cause is only available from EVENT_ERROR events");
        }        
        return t;
    }

    /**
     * @return
     * a thread watcher object containing information about the progress of an S3 operation.
     * @throws IllegalStateException
     * a thread watcher can only be retrieved from an EVENET_STARTED or EVENT_IN_PROGRESS event.
     */
    public ThreadWatcher getThreadWatcher() throws IllegalStateException {
        if (eventCode != EVENT_STARTED && eventCode != EVENT_IN_PROGRESS) {
            throw new IllegalStateException("Thread Watcher is only available from EVENT_STARTED "
                + "or EVENT_IN_PROGRESS events");
        }        
        return threadWatcher;
    }

    public String toString() {
        String eventText = eventCode == EVENT_ERROR ? "EVENT_ERROR"
            : eventCode == EVENT_STARTED ? "EVENT_STARTED"
                : eventCode == EVENT_COMPLETED ? "EVENT_COMPLETED"
                    : eventCode == EVENT_IN_PROGRESS ? "EVENT_IN_PROGRESS"
                        : eventCode == EVENT_CANCELLED ? "EVENT_CANCELLED"
                            : "Unrecognised event status code: " + eventCode;

        if (getErrorCause() != null) {
            return eventText + " " + getErrorCause();
        } else {
            return eventText;
        }
    }

}
