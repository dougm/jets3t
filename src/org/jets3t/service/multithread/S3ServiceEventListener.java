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
 * Listener for events produced by {@link S3ServiceMulti}, where each event type is represented
 * by an object passed to the <tt>s3ServiceEventPerformed</tt> method.
 * 
 * @author James Murty
 */
public interface S3ServiceEventListener {

    public void s3ServiceEventPerformed(ListObjectsEvent event);

    public void s3ServiceEventPerformed(CreateObjectsEvent event);

    public void s3ServiceEventPerformed(CopyObjectsEvent event);

    public void s3ServiceEventPerformed(CreateBucketsEvent event);
	
	public void s3ServiceEventPerformed(DeleteObjectsEvent event);

	public void s3ServiceEventPerformed(GetObjectsEvent event);

	public void s3ServiceEventPerformed(GetObjectHeadsEvent event);

	public void s3ServiceEventPerformed(LookupACLEvent event);

	public void s3ServiceEventPerformed(UpdateACLEvent event);

	public void s3ServiceEventPerformed(DownloadObjectsEvent event);

}
