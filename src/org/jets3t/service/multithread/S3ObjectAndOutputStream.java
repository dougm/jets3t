package org.jets3t.service.multithread;

import java.io.OutputStream;

import org.jets3t.service.model.S3Object;

/**
 * A simple container object to associate an {@link S3Object} with an OutputStream.
 * <p>
 * This class is used by {@link S3ServiceMulti#downloadObjects(S3Bucket, S3ObjectAndOutputStream[])}
 * to download objects, as it indicates the output stream an object's data should be written to when
 * it is downloaded.   
 * 
 * @author James Murty
 */
public class S3ObjectAndOutputStream {
    private S3Object object = null;
    private OutputStream outputStream = null;
    
    public S3ObjectAndOutputStream(S3Object object, OutputStream outputStream) {
        this.object = object;
        this.outputStream = outputStream;
    }
    
    public S3Object getObject() {
        return object;
    }
    
    public OutputStream getOuputStream() {
        return outputStream;
    }
}
