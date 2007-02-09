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
package org.jets3t.service;

import org.jets3t.service.model.S3Object;

/**
 * Stores a "chunk" of S3Objects returned from a list command - this particular chunk may or may
 * not include all the objects available in a bucket.
 * 
 * This class contains an array of S3objects and a the last key name returned by a prior
 * call to the method {@link S3Service#listObjectsChunked(String, String, String, long, String)}. 
 * 
 * @author James Murty
 */
public class S3ObjectsChunk {
    private S3Object[] objects = null;
    private String[] commonPrefixes = null;
    private String priorLastKey = null;
    
    public S3ObjectsChunk(S3Object[] objects, String[] commonPrefixes, String priorLastKey) {
        this.objects = objects;
        this.commonPrefixes = commonPrefixes;
        this.priorLastKey = priorLastKey;
    }

    /**
     * @return
     * the objects in this chunk.
     */
    public S3Object[] getObjects() {
        return objects;
    }
    
    /**
     * @return
     * the common prefixes in this chunk.
     */
    public String[] getCommonPrefixes() {
        return commonPrefixes;
    }


    /**
     * @return 
     * the last key returned by the previous chunk if that chunk was incomplete, null otherwise.
     */
    public String getPriorLastKey() {
        return priorLastKey;
    }
        
}
