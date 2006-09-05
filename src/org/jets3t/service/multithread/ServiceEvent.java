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
    
    public int getEventCode() {
        return eventCode;
    }

    public Throwable getErrorCause() throws IllegalStateException {
        if (eventCode != EVENT_ERROR) {
            throw new IllegalStateException("Error Cause is only available from EVENT_ERROR events");
        }        
        return t;
    }

    public ThreadWatcher getThreadWatcher() throws IllegalStateException {
        if (eventCode != EVENT_STARTED && eventCode != EVENT_IN_PROGRESS) {
            throw new IllegalStateException("Error Cause is only available from EVENT_STARTED "
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
