package org.jets3t.service.multithread;

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
    
    public boolean wasErrorThrown() {
        return t[0] != null;
    }
    
    public Throwable getErrorThrown() {
        return t[0];
    }

}
