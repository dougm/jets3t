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

public class GetObjectsEvent extends ServiceEvent {	
	private String[] objectKeys = null;
	private S3Object[] objects = null;
	private S3Bucket bucket = null;
	
	public GetObjectsEvent(Throwable t) {
		super(t);
	}

	public GetObjectsEvent(int eventStatus) {
		super(eventStatus);
	}

	public GetObjectsEvent(int eventStatus, ProgressStatus progressStatus, S3Bucket bucket, S3Object[] objects) {
		super(eventStatus, progressStatus);
		this.bucket = bucket;		
		this.objects = objects;
	}

	public GetObjectsEvent(int eventStatus, S3Bucket bucket, S3Object[] objects) {
		super(eventStatus);
		this.bucket = bucket;		
		this.objects = objects;
	}

	public S3Bucket getBucket() {
		return bucket;
	}

	public S3Object[] getObjects() {
		return objects;
	}

	public String[] getObjectKeys() {
		return objectKeys;
	}
	
}
