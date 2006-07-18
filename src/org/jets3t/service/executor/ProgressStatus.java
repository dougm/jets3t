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


public class ProgressStatus {
	long completedThreads = 0;
	long threadCount = 0;
	CancelEventListener cancelEventListener = null;
	
	public ProgressStatus(long completedThreads, long threadCount, CancelEventListener cancelEventListener) {
		this.completedThreads = completedThreads;
		this.threadCount = threadCount;
		this.cancelEventListener = cancelEventListener;
	}

	public ProgressStatus(long completedThreads, long threadCount) {
		this(completedThreads, threadCount, null);
	}

	public long getCompletedThreads() {
		return completedThreads;
	}

	public long getThreadCount() {
		return threadCount;
	}

	public boolean isCancelTaskSupported() {
		return cancelEventListener != null;
	}
	
	public void cancelTask() {
		if (isCancelTaskSupported()) {
			cancelEventListener.cancelTask(this);
		}
	}
	
	public CancelEventListener getCancelEventListener() {
		return cancelEventListener;
	}

}
