package org.jets3t.service.multithread;

/**
 * Simple implementation of {@link S3ServiceEventListener} to listen for events produced by
 * {@link S3ServiceMulti}.
 * <p>
 * This adaptor does nothing but store the first Error event it comes across, if any,
 * and make it available through {@link #getErrorThrown}.
 * 
 * @author James Murty
 */
public class S3ServiceEventAdaptor implements S3ServiceEventListener {
    
    private Throwable t[] = new Throwable[1];

    public void s3ServiceEventPerformed(CreateObjectsEvent event) {
        storeThrowable(event);
    }

    public void s3ServiceEventPerformed(CreateBucketsEvent event) {
        storeThrowable(event);
    }

    public void s3ServiceEventPerformed(DeleteObjectsEvent event) {
        storeThrowable(event);
    }

    public void s3ServiceEventPerformed(GetObjectsEvent event) {
        storeThrowable(event);
    }

    public void s3ServiceEventPerformed(GetObjectHeadsEvent event) {
        storeThrowable(event);
    }

    public void s3ServiceEventPerformed(LookupACLEvent event) {
        storeThrowable(event);
    }

    public void s3ServiceEventPerformed(UpdateACLEvent event) {
        storeThrowable(event);
    }

    public void s3ServiceEventPerformed(DownloadObjectsEvent event) {
        storeThrowable(event);
    }
    
    private void storeThrowable(ServiceEvent event) {
        if (t[0] == null && event.getEventCode() == ServiceEvent.EVENT_ERROR) {
            t[0] = event.getErrorCause();
        }
    }
    
    /**
     * @return
     * true if an event has resulted in an exception.
     */
    public boolean wasErrorThrown() {
        return t[0] != null;
    }
    
    /**
     * @return
     * the first error thrown by an event, or null if no error has been thrown.
     */
    public Throwable getErrorThrown() {
        return t[0];
    }

}
