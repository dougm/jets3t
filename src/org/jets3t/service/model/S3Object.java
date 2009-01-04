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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.Constants;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.io.RepeatableFileInputStream;
import org.jets3t.service.utils.Mimetypes;
import org.jets3t.service.utils.ServiceUtils;

/**
 * An S3 object.
 * 
 * @author James Murty
 */
public class S3Object extends BaseS3Object implements Cloneable {
    private static final Log log = LogFactory.getLog(S3Object.class);

    private static final long serialVersionUID = -2883501141593631181L;
    
    /*
     * Listing of the metadata names that are required in S3 objects, or are used frequently
     * in jets3t applications.  
     */
	public static final String METADATA_HEADER_LAST_MODIFIED_DATE = "Last-Modified";
	public static final String METADATA_HEADER_DATE = "Date";
	public static final String METADATA_HEADER_OWNER = "Owner";	
	public static final String METADATA_HEADER_ETAG = "ETag";	
    public static final String METADATA_HEADER_HASH_MD5 = "md5-hash";   
    public static final String METADATA_HEADER_ORIGINAL_HASH_MD5 = "original-md5-hash";   
    public static final String METADATA_HEADER_CONTENT_MD5 = "Content-MD5";   
	public static final String METADATA_HEADER_CONTENT_LENGTH = "Content-Length";	
	public static final String METADATA_HEADER_CONTENT_TYPE = "Content-Type";	
    public static final String METADATA_HEADER_CONTENT_ENCODING = "Content-Encoding";   
	public static final String METADATA_HEADER_STORAGE_CLASS = "Storage-Class";	
    public static final String METADATA_HEADER_CONTENT_DISPOSITION = "Content-Disposition";   
    public static final String METADATA_HEADER_CONTENT_LANGUAGE = "Content-Language";   
	
	private String key = null;
	private String bucketName = null;
	private transient InputStream dataInputStream = null;
	private AccessControlList acl = null;
    private boolean isMetadataComplete = false;
    
    /**
     * Store references to files when the object's data comes from a file, to allow for lazy
     * opening of the file's input streams.
     */
    private File dataInputFile = null;
    
    
    /**
     * Create an object representing a file. The object is initialised with the file's name
     * as its key, the file's content as its data, a content type based on the file's extension
     * (see {@link Mimetypes}), and a content length matching the file's size.
     * The file's MD5 hash value is also calculated and provided to S3, so the service
     * can verify that no data are corrupted in transit.
     * 
     * @param bucket
     * the bucket the object belongs to, or will be placed in.
     * @param file
     * the file the object will represent. This file must exist and be readable.
     * 
     * @throws IOException when an i/o error occurred reading the file
     * @throws NoSuchAlgorithmException when this JRE doesn't support the MD5 hash algorithm 
     */
    public S3Object(S3Bucket bucket, File file) throws NoSuchAlgorithmException, IOException {
        this(bucket, file.getName());
        setContentLength(file.length());
        setContentType(Mimetypes.getInstance().getMimetype(file));
        if (!file.exists()) {
            throw new FileNotFoundException("Cannot read from file: " + file.getAbsolutePath());
        }
        setDataInputFile(file);
        setMd5Hash(ServiceUtils.computeMD5Hash(new FileInputStream(file)));
    }
    
    /**
     * Create an object representing text data. The object is initialized with the given
     * key, the given string as its data content (encoded as UTF-8), a content type of 
     * <code>text/plain; charset=utf-8</code>, and a content length matching the 
     * string's length.
     * The given string's MD5 hash value is also calculated and provided to S3, so the service
     * can verify that no data are corrupted in transit.
     * 
     * @param bucket
     * the bucket the object belongs to, or will be placed in.
     * @param key
     * the key name for the object.
     * @param dataString
     * the text data the object will contain. Text data will be encoded as UTF-8. 
     * This string cannot be null.
     * 
     * @throws IOException 
     * @throws NoSuchAlgorithmException when this JRE doesn't support the MD5 hash algorithm 
     */
    public S3Object(S3Bucket bucket, String key, String dataString) throws NoSuchAlgorithmException, IOException 
    {
        this(bucket, key);
        ByteArrayInputStream bais = new ByteArrayInputStream(
            dataString.getBytes(Constants.DEFAULT_ENCODING));
        setDataInputStream(bais);
        setContentLength(bais.available());
        setContentType("text/plain; charset=utf-8");
        setMd5Hash(ServiceUtils.computeMD5Hash(dataString.getBytes(Constants.DEFAULT_ENCODING)));        
    }

    /**
     * Create an object without any associated data, and no associated bucket.
     * 
     * @param key
     * the key name for the object.
     */
    public S3Object(String key) {
        this.key = key;
    }

    /**
     * Create an object without any associated data.
     * 
     * @param bucket
     * the bucket the object belongs to, or will be placed in.
     * @param key
     * the key name for the object.
     */
    public S3Object(S3Bucket bucket, String key) {
        if (bucket != null) {
            this.bucketName = bucket.getName();
        }
        this.key = key;
    }
	
    public String toString() {
		return "S3Object [key=" + getKey() + ",bucket=" + (bucketName == null ? "<Unknown>" : bucketName)  
			+ ",lastModified=" + getLastModifiedDate() + ", dataInputStream=" + dataInputStream 
			+ "] Metadata=" + getMetadataMap();
	}
	
    /**
     * Returns an input stream containing this object's data, or null if there is 
     * no data associated with the object.
     * <p>
     * If you are downloading data from S3, you should consider verifying the
     * integrity of the data you read from this stream using one of the  
     * {@link #verifyData(InputStream)} methods.  
     * 
     * @throws S3ServiceException 
     */
	public InputStream getDataInputStream() throws S3ServiceException {        
        if (dataInputStream == null && dataInputFile != null) {
            try {
                // Use a repeatable file data input stream, so transmissions can be retried if necessary.
                dataInputStream = new RepeatableFileInputStream(dataInputFile);                
            } catch (FileNotFoundException e) {
                throw new S3ServiceException("Cannot open file input stream", e); 
            }
        }
        return dataInputStream;
	}

    /**
     * Sets an input stream containing the data content to associate with this object.
     * <p>
     * <b>Note</b>: If the data content comes from a file, use the alternate method
     * {@link #setDataInputFile(File)} which allows objects to lazily open files and avoid any
     * Operating System limits on the number of files that may be opened simultaneously. 
     * <p>
     * <b>Note 2</b>: This method does not calculate an MD5 hash of the input data, 
     * which means S3 will not be able to recognize if data are corrupted in transit. 
     * To allow S3 to verify data you upload, you should set the MD5 hash value of
     * your data using {@link #setMd5Hash(byte[])}.
     * <p>
     * This method will set the object's file data reference to null.
     * 
     * @param dataInputStream
     * an input stream containing the object's data.
     */
	public void setDataInputStream(InputStream dataInputStream) {
        this.dataInputFile = null;
		this.dataInputStream = dataInputStream;
	}	
    
    /**
     * Sets the file containing the data content to associate with this object. This file will
     * be automatically opened as an input stream only when absolutely necessary, that is when
     * {@link #getDataInputStream()} is called.
     * <p>
     * <b>Note 2</b>: This method does not calculate an MD5 hash of the input data, 
     * which means S3 will not be able to recognize if data are corrupted in transit. 
     * To allow S3 to verify data you upload, you should set the MD5 hash value of
     * your data using {@link #setMd5Hash(byte[])}.
     * <p>
     * This method will set the object's input stream data reference to null.
     * 
     * @param dataInputFile
     * a file containing the object's data.
     */
    public void setDataInputFile(File dataInputFile) {
        this.dataInputStream = null;
        this.dataInputFile = dataInputFile;
    }
    
    /**
     * @return
     * Return the file that contains this object's data, if such a file has been
     * provided. Null otherwise. 
     */
    public File getDataInputFile() {
        return this.dataInputFile;
    }


    /**
     * Closes the object's data input stream if it exists.
     * 
     * @throws IOException
     */
    public void closeDataInputStream() throws IOException {
        if (this.dataInputStream != null) {
            this.dataInputStream.close();
            this.dataInputStream = null;
        }
    }
    
    /**
     * @return
     * the ETag value of the object as returned by S3 when an object is created. The ETag value
     * does not include quote (") characters. This value will be null if the object's ETag value
     * is not known, such as when an object has not yet been uploaded to S3.
     */
	public String getETag() {
		String etag = (String) getMetadata(METADATA_HEADER_ETAG);
		if (etag != null) {
			if (etag.startsWith("\"") && etag.endsWith("\"")) {
				return etag.substring(1, etag.length() -1);
			}
		}
		return etag;
	}
	
    /**
     * Set the ETag value of the object based on information returned from S3.
     * This method should only by used by code that reads S3 responses.
     * 
     * @param etag
     * the ETag value as provided by S3.
     */
    public void setETag(String etag) {
        addMetadata(METADATA_HEADER_ETAG, etag);
    }
    
    /**
     * @return
     * the hex-encoded MD5 hash of an object's data contents as stored in the jets3t-specific metadata
     * item <code>md5-hash</code>, or null if the hash value is not available.
     */
    public String getMd5HashAsHex() {
        return (String) getMetadata(METADATA_HEADER_HASH_MD5);
    }
	
    /**
     * @return
     * the Base64-encoded MD5 hash of an object's data contents as stored in the metadata
     * item <code>Content-MD5</code>, or as derived from an <code>ETag</code> or 
     * <code>md5-hash</code> hex-encoded version of the hash. Returns null if the hash value is not 
     * available.
     */
    public String getMd5HashAsBase64() {
        String md5HashBase64 = (String) getMetadata(METADATA_HEADER_CONTENT_MD5);
        if (md5HashBase64 == null) {
            // Try converting the object's ETag (a hex-encoded md5 hash).
            if (getETag() != null) {
                return ServiceUtils.toBase64(ServiceUtils.fromHex(getETag()));
            }
            // Try converting the object's md5-hash (another hex-encoded md5 hash).
            if (getMd5HashAsHex() != null) {
                return ServiceUtils.toBase64(ServiceUtils.fromHex(getMd5HashAsHex()));
            }
        }
        return md5HashBase64;
    }
    
    /**
     * Set the MD5 hash value of this object's data.
     * The hash value is stored as metadata under <code>Content-MD5</code> (Base64-encoded)
     * and the jets3t-specific <code>md5-hash</code> (Hex-encoded).
     * 
     * @param md5Hash
     * the MD5 hash value of the object's data.
     */
    public void setMd5Hash(byte[] md5Hash) {
        addMetadata(METADATA_HEADER_HASH_MD5, ServiceUtils.toHex(md5Hash));
        addMetadata(METADATA_HEADER_CONTENT_MD5, ServiceUtils.toBase64(md5Hash));
    }

    /**
     * @return
     * the last modified date of this object, as provided by S3. If the last modified date is not
     * available (e.g. if the object has only just been created) the object's creation date is 
     * returned instead. If both last modified and creation dates are unavailable, null is returned.
     */
    public Date getLastModifiedDate() {    	
		Date lastModifiedDate = (Date) getMetadata(METADATA_HEADER_LAST_MODIFIED_DATE);
		if (lastModifiedDate == null) {
			// Perhaps this object has just been created, in which case we can use the Date metadata.
			lastModifiedDate = (Date) getMetadata(METADATA_HEADER_DATE);
		}
		return lastModifiedDate;
	}
	
    /**
     * Set this object's last modified date based on information returned from S3.
     * This method should only by used internally by code that reads the last modified date
     * from an S3 response; it must not be set prior to uploading data to S3.
     * 
     * @param lastModifiedDate
     */
	public void setLastModifiedDate(Date lastModifiedDate) {
        addMetadata(METADATA_HEADER_LAST_MODIFIED_DATE, lastModifiedDate);
	}
	
    /**
     * @return
     * this object's owner, or null if the owner is not available.
     */
	public S3Owner getOwner() {
		return (S3Owner) getMetadata(METADATA_HEADER_OWNER);
	}

    /**
     * Set this object's owner object based on information returned from S3.
     * This method should only by used by code that reads S3 responses.
     * 
     * @param owner
     */
	public void setOwner(S3Owner owner) {
        addMetadata(METADATA_HEADER_OWNER, owner);
	}	

    /**
     * @return
     * the content length, or size, of this object's data, or 0 if it is unknown.
     */
	public long getContentLength() {
		Object contentLength = getMetadata(METADATA_HEADER_CONTENT_LENGTH);
		if (contentLength == null) {
			return 0;
		} else {
			return Long.parseLong(contentLength.toString());
		}
	}
	
	/**
	 * Set this object's content length. The content length is set internally by JetS3t for
	 * objects that are retrieved from S3. For objects that are uploaded into S3, JetS3t
	 * automatically calculates the content length if the data is provided to the String- or 
     * File-based S3Object constructor. If you manually provide data to this object via the
     * {@link #setDataInputStream(InputStream)} or {@link #setDataInputFile(File)} methods, 
     * you must also set the content length value.
	 * @param size
	 */
	public void setContentLength(long size) {
        addMetadata(METADATA_HEADER_CONTENT_LENGTH, String.valueOf(size));
	}

	/**
	 * @return
	 * the storage class of the object. 
	 */
	public String getStorageClass() {
		return (String) getMetadata(METADATA_HEADER_STORAGE_CLASS);
	}

    /**
     * Set the storage class based on information returned from S3.
     * This method should only by used by code that reads S3 responses.
     * 
     * @param storageClass
     */
	public void setStorageClass(String storageClass) {
        addMetadata(METADATA_HEADER_STORAGE_CLASS, storageClass);
	}

	/**
	 * @return
	 * the content type of the object
	 */
	public String getContentType() {
		return (String) getMetadata(METADATA_HEADER_CONTENT_TYPE);
	}

	/**
	 * Set the content type of the object. JetS3t can help you determine the 
     * content type when the associated data is a File (see {@link Mimetypes}). 
	 * You should set the content type for associated String or InputStream data.
	 * 
	 * @param contentType
	 */
	public void setContentType(String contentType) {
        addMetadata(METADATA_HEADER_CONTENT_TYPE, contentType);
	}
	
	/**
	 * @return
	 * the content language of this object, or null if it is unknown.
	 */
    public String getContentLanguage() {
        return (String) getMetadata(METADATA_HEADER_CONTENT_LANGUAGE);
    }

    /**
     * Set the content language of the object. 
     * @param contentLanguage
     */
    public void setContentLanguage(String contentLanguage) {
        addMetadata(METADATA_HEADER_CONTENT_LANGUAGE, contentLanguage);
    }

    /**
     * @return
     * the content disposition of this object, or null if it is unknown.
     */
    public String getContentDisposition() {
        return (String) getMetadata(METADATA_HEADER_CONTENT_DISPOSITION);
    }

    /**
     * Set the content disposition of the object.
     * @param contentDisposition
     */
    public void setContentDisposition(String contentDisposition) {
        addMetadata(METADATA_HEADER_CONTENT_DISPOSITION, contentDisposition);
    }

    /**
     * @return
     * the content encoding of this object, or null if it is unknown.
     */
    public String getContentEncoding() {
        return (String) getMetadata(METADATA_HEADER_CONTENT_ENCODING);
    }

    /**
     * Set the content encoding of this object.
     * @param contentEncoding
     */
    public void setContentEncoding(String contentEncoding) {
        addMetadata(METADATA_HEADER_CONTENT_ENCODING, contentEncoding);
    }

    /**
     * @return
     * the name of the bucket this object belongs to or will be placed into, or null if none is set.
     */
    public String getBucketName() {
		return bucketName;
	}
    
    
	/**
	 * Set the name of the bucket this object belongs to or will be placed into.
	 * @param bucketName the name for the bucket.
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * @return
	 * the object's ACL, or null if it is unknown.
	 */
	public AccessControlList getAcl() {
		return acl;
	}

    /**
     * Set the object's ACL. If a pre-canned REST ACL is used, the plain-text representation
     * of the canned ACL is also added as a metadata header <code>x-amz-acl</code>. 
     * 
     * @param acl
     */
	public void setAcl(AccessControlList acl) {
        this.acl = acl;
        
        if (acl != null) {
            if (AccessControlList.REST_CANNED_PRIVATE.equals(acl)) {                
                addMetadata(Constants.REST_HEADER_PREFIX + "acl", "private");
            } else if (AccessControlList.REST_CANNED_PUBLIC_READ.equals(acl)) { 
                addMetadata(Constants.REST_HEADER_PREFIX + "acl", "public-read");
            } else if (AccessControlList.REST_CANNED_PUBLIC_READ_WRITE.equals(acl)) { 
                addMetadata(Constants.REST_HEADER_PREFIX + "acl", "public-read-write");
            } else if (AccessControlList.REST_CANNED_AUTHENTICATED_READ.equals(acl)) {
                addMetadata(Constants.REST_HEADER_PREFIX + "acl", "authenticated-read");
            } else {
                // Non-REST canned ACLs are not added as headers...
            }
        }
	}

	/**
	 * @return
	 * the key of this object.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Set the key of this object.
	 * @param key the key for this object.
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
    /**
     * @return
     * true if the object's metadata are considered complete, such as when the object's metadata
     * has been retrieved from S3 by a HEAD request. If this value is not true, the metadata
     * information in this object should not be considered authoritative.
     */
    public boolean isMetadataComplete() {
        return isMetadataComplete;
    }

    /**
     * S3 Object metadata are only complete when it is populated with all values following
     * a HEAD or GET request.
     * This method should only by used by code that reads S3 responses.
     * 
     * @param isMetadataComplete
     */
    public void setMetadataComplete(boolean isMetadataComplete) {
        this.isMetadataComplete = isMetadataComplete;
    }
    
    /**
     * Add metadata information to the object. If date metadata items (as recognized by name)
     * are added and the value is not a date, the value is parsed as an ISO 8601 string.
     * @param name
     * @param value
     */
    public void addMetadata(String name, Object value) {
    	try {
	    	if (METADATA_HEADER_LAST_MODIFIED_DATE.equals(name) && !(value instanceof Date)) {
	    		value = ServiceUtils.parseIso8601Date(value.toString());
	    	} else if (METADATA_HEADER_DATE.equals(name) && !(value instanceof Date)) {
	    		value = ServiceUtils.parseIso8601Date(value.toString());
	    	} 
    	} catch (ParseException e) {
    		if (log.isErrorEnabled()) {
	    		log.error("Unable to parse value we expect to be a valid date: "
	                + name + "=" + value, e);
    		}
    	}
    	
    	super.addMetadata(name, value);
    }
    
    /**
     * Add all the metadata information to the object from the provided map.
     * 
     * @param metadata
     */
    public void addAllMetadata(Map metadata) {
    	Iterator iter = metadata.entrySet().iterator();
    	while (iter.hasNext()) {
    		Map.Entry entry = (Map.Entry) iter.next();
    		addMetadata(entry.getKey().toString(), entry.getValue());
    	}
    }
    
    /**
     * Returns only those object metadata items that can be modified in
     * S3. This list excludes those that are set by the S3 service, and
     * those that are specific to a particular HTTP request/response
     * session (such as request identifiers). 
     * 
     * @return
     * the limited set of metadata items that S3 allows users to control. 
     */
    public Map getModifiableMetadata() {
        Map objectMetadata = new HashMap(getMetadataMap());
        objectMetadata.remove(S3Object.METADATA_HEADER_CONTENT_LENGTH);
        objectMetadata.remove(S3Object.METADATA_HEADER_DATE);
        objectMetadata.remove(S3Object.METADATA_HEADER_ETAG);
        objectMetadata.remove(S3Object.METADATA_HEADER_LAST_MODIFIED_DATE);
        objectMetadata.remove(S3Object.METADATA_HEADER_OWNER);
        objectMetadata.remove("id-2"); // HTTP request-specific information
        objectMetadata.remove("request-id"); // HTTP request-specific information
        return objectMetadata;
    }

    
    public Object clone() {
        S3Object clone = new S3Object(key);
        clone.bucketName = bucketName;
        clone.dataInputStream = dataInputStream;
        clone.acl = acl;
        clone.isMetadataComplete = isMetadataComplete;
        clone.dataInputFile = dataInputFile;
        clone.addAllMetadata(getMetadataMap());
        return clone;
    }
    
    
    /**
     * Calculates the MD5 hash value of the given data object, and compares it
     * against this object's hash (as stored in the Content-MD5 header for 
     * uploads, or the ETag header for downloads). 
     * 
     * @param downloadedFile
     * @return
     * true if the calculated MD5 hash value of the file matches this object's 
     * hash value, false otherwise.
     * 
     * @throws NoSuchAlgorithmException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public boolean verifyData(File downloadedFile) 
        throws NoSuchAlgorithmException, FileNotFoundException, IOException 
    {
        return getMd5HashAsBase64().equals(
            ServiceUtils.toBase64(
                ServiceUtils.computeMD5Hash(
                    new FileInputStream(downloadedFile))));
    }
    
    /**
     * Calculates the MD5 hash value of the given data object, and compares it
     * against this object's hash (as stored in the Content-MD5 header for 
     * uploads, or the ETag header for downloads). 
     * 
     * @param downloadedData
     * @return
     * true if the calculated MD5 hash value of the bytes matches this object's 
     * hash value, false otherwise.
     * 
     * @throws NoSuchAlgorithmException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public boolean verifyData(byte[] downloadedData) 
        throws NoSuchAlgorithmException, FileNotFoundException, IOException 
    {
        return getMd5HashAsBase64().equals(
            ServiceUtils.toBase64(
                ServiceUtils.computeMD5Hash(downloadedData)));
    }

    /**
     * Calculates the MD5 hash value of the given data object, and compares it
     * against this object's hash (as stored in the Content-MD5 header for 
     * uploads, or the ETag header for downloads). 
     * 
     * @param downloadedDataStream
     * the input stream of a downloaded S3Object.
     * 
     * @return
     * true if the calculated MD5 hash value of the input stream matches this 
     * object's hash value, false otherwise.
     * 
     * @throws NoSuchAlgorithmException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public boolean verifyData(InputStream downloadedDataStream) 
        throws NoSuchAlgorithmException, FileNotFoundException, IOException 
    {
        return getMd5HashAsBase64().equals(
            ServiceUtils.toBase64(
                ServiceUtils.computeMD5Hash(downloadedDataStream)));
    }
}
