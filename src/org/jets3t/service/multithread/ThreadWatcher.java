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

public class ThreadWatcher {
    private long completedThreads = 0;
    private long threadCount = 0;
    private CancelEventListener cancelEventListener = null;
    private long bytesTransferred = -1;
    private long bytesTotal = -1;

    public ThreadWatcher(long completedThreads, long threadCount, 
        CancelEventListener cancelEventListener) 
    {
        this.completedThreads = completedThreads;
        this.threadCount = threadCount;
        this.cancelEventListener = cancelEventListener;
    }

    public ThreadWatcher(long completedThreads, long threadCount) {
        this(completedThreads, threadCount, null);
    }
    
    public void setBytesTransferredInfo(long bytesTransferred, long bytesTotal) {
        this.bytesTotal = bytesTotal;
        this.bytesTransferred = bytesTransferred;
    }

    public long getCompletedThreads() {
        return completedThreads;
    }

    public long getThreadCount() {
        return threadCount;
    }
    
    /**
     * If this method returns true, the methods {@link getBytesTotal} and {@link getBytesTransferred}
     * will contain information about the amount of data being transferred by the watched threads.
     * 
     * @return 
     * true if this watcher contains information about the bytes transferred by
     * the threads it is watching.  
     */
    public boolean isBytesTransferredInfoAvailable() {
        return (bytesTotal != -1 && bytesTransferred != -1);
    }    
    
    /**
     * @return
     * the expected total of bytes that will be transferred by the watched threads.
     * @throws IllegalStateException
     * if the bytes transferred information is not available - check this availability
     * with the {@link isBytesTransferredInfoAvailable} method.
     */
    public long getBytesTotal() throws IllegalStateException {
        if (!isBytesTransferredInfoAvailable()) {
            throw new IllegalStateException("Bytes Transferred Info is not available in this object");
        }
        return bytesTotal;
    }

    /**
     * @return
     * the count of bytes that have been transferred by the watched threads.
     * @throws IllegalStateException
     * if the bytes transferred information is not available - check this availability
     * with the {@link isBytesTransferredInfoAvailable} method.
     */
    public long getBytesTransferred() {
        if (!isBytesTransferredInfoAvailable()) {
            throw new IllegalStateException("Bytes Transferred Info is not available in this object");
        }
        return bytesTransferred;
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
