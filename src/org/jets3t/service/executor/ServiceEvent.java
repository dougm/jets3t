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

public abstract class ServiceEvent {
	public static final int EVENT_ERROR = 0;
	public static final int EVENT_STARTED = 1;
	public static final int EVENT_COMPLETED = 2;
	public static final int EVENT_IN_PROGRESS = 3;
	public static final int EVENT_CANCELLED = 4;
	
	private int eventStatus = 0;
	private Throwable t = null;
	private 	ProgressStatus progressStatus = null;
	
	public ServiceEvent(int eventStatus) {
		this.eventStatus = eventStatus;
	}

	public ServiceEvent(int eventStatus, ProgressStatus progressStatus) {
		this(eventStatus);
		this.progressStatus = progressStatus;
	}

	public ServiceEvent(Throwable t) {
		this(EVENT_ERROR);
		this.t = t;
	}
	
	public int getEventStatus() {
		return eventStatus; 
	}
	
	public Throwable getErrorCause() {
		return t;
	}
	
	public boolean isProgressStatusAvailable() {
		return progressStatus != null;
	}
	
	public ProgressStatus getProgressStatus() {
		return progressStatus;
	}
	
	public String toString() {
		String eventText = 
			eventStatus == EVENT_ERROR? "EVENT_ERROR" :
			eventStatus == EVENT_STARTED? "EVENT_STARTED" :
			eventStatus == EVENT_COMPLETED? "EVENT_COMPLETED" :
			eventStatus == EVENT_IN_PROGRESS? "EVENT_IN_PROGRESS" :
			eventStatus == EVENT_CANCELLED? "EVENT_CANCELLED" :
			"Unrecognised event status code: " + eventStatus;
		
		if (getErrorCause() != null) {
			return eventText + " " + getErrorCause();
		} else {
			return eventText;			
		}		
	}

}
