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
package org.jets3t.service.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class to represent both S3 objects and buckets - both these object types contain metadata.
 * 
 * @author James Murty
 */
public abstract class BaseS3Object implements Serializable {
    /**
     * Map to privately store metadata associated with this object.
     */
	private Map metadata = new HashMap();

    
    /**
     * @return
     * an <b>immutable</b> map containing all the metadata associated with this S3 object.
     */
	public Map getMetadataMap() {
        return Collections.unmodifiableMap(metadata);
	}
    
    /**
     * @param name
     * the metadata item name.
     * 
     * @return
     * the value of the metadata with the given name, or null if no such metadata item exists.
     */
    public Object getMetadata(String name) {
        return this.metadata.get(name);
    }
    
    /**
     * @param name
     * the metadata item name.
     * 
     * @return
     * true if this object contains a metadata item with the given name, false otherwise.
     */
    public boolean containsMetadata(String name) {
        return this.metadata.keySet().contains(name);
    }
	
    /**
     * Adds a metadata item to the object.
     * 
     * @param name
     * the metadata item name.
     * @param value
     * the metadata item value.
     */
	public void addMetadata(String name, Object value) {
		this.metadata.put(name, value);
	}
	
    /**
     * Adds all the items in the provided map to this object's metadata.
     * 
     * @param metadata
     * metadata items to add.
     */
	public void addAllMetadata(Map metadata) {
		this.metadata.putAll(metadata);
	}
    
    /**
     * Removes a metadata item from the object.
     * 
     * @param name
     * the name of the metadata item to remove.
     */
    public void removeMetadata(String name) {
        this.metadata.remove(name);
    }
	
    /**
     * Removes all the metadata items associated with this object, then adds all the items
     * in the provided map. After performing this operation, the metadata list will contain
     * only those items in the provided map.
     * 
     * @param metadata
     * metadata items to add.
     */
	public void replaceAllMetadata(Map metadata) {
		this.metadata.clear();
		addAllMetadata(metadata);
	}
	
}
