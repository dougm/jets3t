package contribs.mx;

public interface S3BucketMxMBean {

    public long getTotalRequests();

    public long getTotalListRequests();

    public long getTotalObjectGetRequests();

    public long getTotalObjectHeadRequests();

    public long getTotalObjectPutRequests();

    public long getTotalObjectDeleteRequests();

    public long getTotalObjectCopyRequests();
}
