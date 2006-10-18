package org.jets3t.service;

import org.jets3t.service.model.S3Object;

/**
 * Contains an array of S3objects and a the last key name returned by a prior
 * call to the {@S3Service#listObjectsChunked} method. 
 * 
 * @author James Murty
 */
public class S3ObjectsChunk {
    private S3Object[] objects = null;
    private String priorLastKey = null;
    
    public S3ObjectsChunk(S3Object[] objects, String priorLastKey) {
        this.objects = objects;
        this.priorLastKey = priorLastKey;
    }

    public S3Object[] getObjects() {
        return objects;
    }

    /**
     * @return the last key returned by the previous chunk if that chunk was incomplete, null otherwise.
     */
    public String getPriorLastKey() {
        return priorLastKey;
    }
    
}
