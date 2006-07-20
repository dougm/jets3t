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

import java.io.InputStream;
import java.util.Date;

import org.jets3t.service.acl.AccessControlList;

public class S3Object extends BaseS3Object {
	public static final String METADATA_HEADER_LAST_MODIFIED_DATE = "Last-Modified";
	public static final String METADATA_HEADER_DATE = "Date";
	public static final String METADATA_HEADER_OWNER = "Owner";	
	public static final String METADATA_HEADER_ETAG = "ETag";	
    public static final String METADATA_HEADER_HASH_MD5 = "md5-hash";   
	public static final String METADATA_HEADER_CONTENT_LENGTH = "Content-Length";	
	public static final String METADATA_HEADER_CONTENT_TYPE = "Content-Type";	
	public static final String METADATA_HEADER_STORAGE_CLASS = "Storage-Class";	
	
	private String key = null;
	private S3Bucket bucket = null;
	private InputStream dataInputStream = null;
	private AccessControlList acl = null;
    private boolean isMetadataComplete = false;
    
    public S3Object() {        
    }
    
    public S3Object(S3Bucket bucket, String key) {
        this.bucket = bucket;
        this.key = key;
    }
	
    public String toString() {
		return "S3Object [key=" + getKey() + ",bucket=" + (getBucket() != null? getBucket().getName() : null) 
			+ ",lastModified=" + getLastModifiedDate() + ", dataInputStream=" + dataInputStream 
			+ "] Metadata=" + getMetadata();
	}
	
	public InputStream getDataInputStream() {
		return dataInputStream;
	}

	public void setDataInputStream(InputStream dataInputStream) {
		this.dataInputStream = dataInputStream;
	}	

	public String getETag() {
		String hash = (String) getMetadata().get(METADATA_HEADER_ETAG);
		if (hash != null) {
			if (hash.startsWith("\"") && hash.endsWith("\"")) {
				return hash.substring(1, hash.length() -1);
			}
		}
		return hash;
	}
	
    public void setETag(String hash) {
        getMetadata().put(METADATA_HEADER_ETAG, hash);
    }
    
    public String getMd5Hash() {
        return (String) getMetadata().get(METADATA_HEADER_HASH_MD5);
    }
	
    public void setMd5Hash(String hash) {
        getMetadata().put(METADATA_HEADER_HASH_MD5, hash);
    }

    public Date getLastModifiedDate() {
		Date lastModifiedDate = (Date) getMetadata().get(METADATA_HEADER_LAST_MODIFIED_DATE);
		if (lastModifiedDate == null) {
			// Perhaps this object has just been created, in which case we can use the Date metadata.
			lastModifiedDate = (Date) getMetadata().get(METADATA_HEADER_DATE);
		}
		return lastModifiedDate;
	}
	
	public void setLastModifiedDate(Date lastModifiedDate) {
		getMetadata().put(METADATA_HEADER_LAST_MODIFIED_DATE, lastModifiedDate);
	}
	
	public S3Owner getOwner() {
		return (S3Owner) getMetadata().get(METADATA_HEADER_OWNER);
	}

	public void setOwner(S3Owner owner) {
		getMetadata().put(METADATA_HEADER_OWNER, owner);
	}	
	
	public long getContentLength() {
		Object contentLength = getMetadata().get(METADATA_HEADER_CONTENT_LENGTH);
		if (contentLength == null) {
			return 0;
		} else {
			return Long.parseLong(contentLength.toString());
		}
	}
	
	public void setContentLength(long size) {
		getMetadata().put(METADATA_HEADER_CONTENT_LENGTH, String.valueOf(size));
	}

	public String getStorageClass() {
		return (String) getMetadata().get(METADATA_HEADER_STORAGE_CLASS);
	}

	public void setStorageClass(String storageClass) {
		getMetadata().put(METADATA_HEADER_STORAGE_CLASS, storageClass);
	}

	public String getContentType() {
		return (String) getMetadata().get(METADATA_HEADER_CONTENT_TYPE);
	}

	public void setContentType(String contentType) {
		getMetadata().put(METADATA_HEADER_CONTENT_TYPE, contentType);
	}
	
	public S3Bucket getBucket() {
		return bucket;
	}

	public void setBucket(S3Bucket bucket) {
		this.bucket = bucket;
	}

	public AccessControlList getAcl() {
		return acl;
	}

	public void setAcl(AccessControlList acl) {
		this.acl = acl;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
    public boolean isMetadataComplete() {
        return isMetadataComplete;
    }

    /**
     * S3 Object metadata is only complete when it is populated with all values following
     * a HEAD or GET request.
     * 
     * @param isMetadataComplete
     */
    public void setMetadataComplete(boolean isMetadataComplete) {
        this.isMetadataComplete = isMetadataComplete;
    }
}
