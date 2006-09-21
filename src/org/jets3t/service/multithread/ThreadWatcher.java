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
 * A thread watcher is associated with a multi-threaded S3 operation and contains information about
 * the progress of the operation.
 * <p>
 * As a minimum, this object will contain a count of the total number of threads involved in the operation
 * (via {@link #getThreadCount}) and the count of threads that have already finished 
 * (via {@link #getCompletedThreads}).
 * <p>
 * For operations involving data transfer, such as S3 object creation or downloads, this object may
 * also include a count of the total bytes being transferred (via {@link #getBytesTotal}) and a count of
 * how many bytes have already been transferred (via {@link #getBytesTransferred}). 
 * <p>
 * It may be possible some S3 operations. If an operation may be cancelled, this object will include
 * a {@link CancelEventTrigger} (via {@link #getCancelEventListener()}) which can be used to trigger a 
 * cancellation.
 * 
 * @author James Murty
 */
public class ThreadWatcher {
    private long completedThreads = 0;
    private long threadCount = 0;
    private CancelEventTrigger cancelEventListener = null;
    private long bytesTransferred = -1;
    private long bytesTotal = -1;

    public ThreadWatcher(long completedThreads, long threadCount, 
        CancelEventTrigger cancelEventListener) 
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
     * If this method returns true, the methods {@link #getBytesTotal()} and {@link #getBytesTransferred()}
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
     * with the {@link #isBytesTransferredInfoAvailable()} method.
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
     * with the {@link #isBytesTransferredInfoAvailable()} method.
     */
    public long getBytesTransferred() {
        if (!isBytesTransferredInfoAvailable()) {
            throw new IllegalStateException("Bytes Transferred Info is not available in this object");
        }
        return bytesTransferred;
    }

    /** 
     * @return
     * true if the S3 operation this object is associated with can be cancelled, and a 
     * {@link CancelEventTrigger} is available.
     */
    public boolean isCancelTaskSupported() {
        return cancelEventListener != null;
    }

    /**
     * Convenience method to trigger an event cancellation via {@link CancelEventTrigger#cancelTask} 
     * if this thread watcher is associated with an operation that can be cancelled.
     */
    public void cancelTask() {
        if (isCancelTaskSupported()) {
            cancelEventListener.cancelTask(this);
        }
    }

    /**
     * @return
     * the cancel event trigger associated with an S3 operation, if any.
     */
    public CancelEventTrigger getCancelEventListener() {
        return cancelEventListener;
    }

}
