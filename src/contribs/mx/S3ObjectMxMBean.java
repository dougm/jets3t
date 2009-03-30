package contribs.mx;

public interface S3ObjectMxMBean {

    public long getTotalRequests();

    public long getTotalGetRequests();

    public long getTotalHeadRequests();

    public long getTotalPutRequests();
    
    public long getTotalCopyRequests();

    public long getTotalDeleteRequests();
}
