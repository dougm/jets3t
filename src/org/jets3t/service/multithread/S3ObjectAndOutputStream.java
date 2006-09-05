package org.jets3t.service.multithread;

import java.io.OutputStream;

import org.jets3t.service.model.S3Object;

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
