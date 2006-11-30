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
