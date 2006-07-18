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

import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;

public class CreateObjectsEvent extends ServiceEvent {	
	private S3Object[] objects = null;
	private S3Bucket bucket = null;
	private long bytesCompleted = 0;
	private long bytesTotal = 0;
	
	public CreateObjectsEvent(Throwable t) {
		super(t);
	}

	public CreateObjectsEvent(int eventStatus, long bytesCompleted, long bytesTotal) {
		super(eventStatus);
		this.bytesCompleted = bytesCompleted;
		this.bytesTotal = bytesTotal;
	}

	public CreateObjectsEvent(int eventStatus, ProgressStatus progressStatus, S3Bucket bucket, S3Object[] objects, long bytesCompleted, long bytesTotal) {
		super(eventStatus, progressStatus);
		this.bucket = bucket;		
		this.objects = objects;
		this.bytesCompleted = bytesCompleted;
		this.bytesTotal = bytesTotal;
	}

	public S3Bucket getBucket() {
		return bucket;
	}

	public S3Object[] getObjects() {
		return objects;
	}

	public long getBytesCompleted() {
		return bytesCompleted;
	}

	public long getBytesTotal() {
		return bytesTotal;
	}
	
}
