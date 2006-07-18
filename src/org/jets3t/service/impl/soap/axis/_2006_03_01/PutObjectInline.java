/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: PutObjectInline.java,v 1.1 2006/07/18 05:15:30 jmurty Exp $
 */

package org.jets3t.service.impl.soap.axis._2006_03_01;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.jets3t.service.impl.soap.axis._2006_03_01.types.StorageClass;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class PutObjectInline.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:30 $
 */
public class PutObjectInline implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _bucket
     */
    private java.lang.String _bucket;

    /**
     * Field _key
     */
    private java.lang.String _key;

    /**
     * Field _metadataList
     */
    private java.util.ArrayList _metadataList;

    /**
     * Field _data
     */
    private byte[] _data;

    /**
     * Field _contentLength
     */
    private long _contentLength;

    /**
     * keeps track of state for field: _contentLength
     */
    private boolean _has_contentLength;

    /**
     * Field _accessControlList
     */
    private org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlList _accessControlList;

    /**
     * Field _storageClass
     */
    private org.jets3t.service.impl.soap.axis._2006_03_01.types.StorageClass _storageClass;

    /**
     * Field _AWSAccessKeyId
     */
    private java.lang.String _AWSAccessKeyId;

    /**
     * Field _timestamp
     */
    private java.util.Date _timestamp;

    /**
     * Field _signature
     */
    private java.lang.String _signature;

    /**
     * Field _credential
     */
    private java.lang.String _credential;


      //----------------/
     //- Constructors -/
    //----------------/

    public PutObjectInline() 
     {
        super();
        _metadataList = new ArrayList();
    } //-- org.jets3t.service.soap._2006_03_01.PutObjectInline()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addMetadata
     * 
     * 
     * 
     * @param vMetadata
     */
    public void addMetadata(org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry vMetadata)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_metadataList.size() < 100)) {
            throw new IndexOutOfBoundsException();
        }
        _metadataList.add(vMetadata);
    } //-- void addMetadata(org.jets3t.service.soap._2006_03_01.MetadataEntry) 

    /**
     * Method addMetadata
     * 
     * 
     * 
     * @param index
     * @param vMetadata
     */
    public void addMetadata(int index, org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry vMetadata)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_metadataList.size() < 100)) {
            throw new IndexOutOfBoundsException();
        }
        _metadataList.add(index, vMetadata);
    } //-- void addMetadata(int, org.jets3t.service.soap._2006_03_01.MetadataEntry) 

    /**
     * Method clearMetadata
     * 
     */
    public void clearMetadata()
    {
        _metadataList.clear();
    } //-- void clearMetadata() 

    /**
     * Method deleteContentLength
     * 
     */
    public void deleteContentLength()
    {
        this._has_contentLength= false;
    } //-- void deleteContentLength() 

    /**
     * Method enumerateMetadata
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateMetadata()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_metadataList.iterator());
    } //-- java.util.Enumeration enumerateMetadata() 

    /**
     * Returns the value of field 'AWSAccessKeyId'.
     * 
     * @return String
     * @return the value of field 'AWSAccessKeyId'.
     */
    public java.lang.String getAWSAccessKeyId()
    {
        return this._AWSAccessKeyId;
    } //-- java.lang.String getAWSAccessKeyId() 

    /**
     * Returns the value of field 'accessControlList'.
     * 
     * @return AccessControlList
     * @return the value of field 'accessControlList'.
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlList getAccessControlList()
    {
        return this._accessControlList;
    } //-- org.jets3t.service.soap._2006_03_01.AccessControlList getAccessControlList() 

    /**
     * Returns the value of field 'bucket'.
     * 
     * @return String
     * @return the value of field 'bucket'.
     */
    public java.lang.String getBucket()
    {
        return this._bucket;
    } //-- java.lang.String getBucket() 

    /**
     * Returns the value of field 'contentLength'.
     * 
     * @return long
     * @return the value of field 'contentLength'.
     */
    public long getContentLength()
    {
        return this._contentLength;
    } //-- long getContentLength() 

    /**
     * Returns the value of field 'credential'.
     * 
     * @return String
     * @return the value of field 'credential'.
     */
    public java.lang.String getCredential()
    {
        return this._credential;
    } //-- java.lang.String getCredential() 

    /**
     * Returns the value of field 'data'.
     * 
     * @return byte
     * @return the value of field 'data'.
     */
    public byte[] getData()
    {
        return this._data;
    } //-- byte[] getData() 

    /**
     * Returns the value of field 'key'.
     * 
     * @return String
     * @return the value of field 'key'.
     */
    public java.lang.String getKey()
    {
        return this._key;
    } //-- java.lang.String getKey() 

    /**
     * Method getMetadata
     * 
     * 
     * 
     * @param index
     * @return MetadataEntry
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry getMetadata(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _metadataList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry) _metadataList.get(index);
    } //-- org.jets3t.service.soap._2006_03_01.MetadataEntry getMetadata(int) 

    /**
     * Method getMetadata
     * 
     * 
     * 
     * @return MetadataEntry
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry[] getMetadata()
    {
        int size = _metadataList.size();
        org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry[] mArray = new org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry) _metadataList.get(index);
        }
        return mArray;
    } //-- org.jets3t.service.soap._2006_03_01.MetadataEntry[] getMetadata() 

    /**
     * Method getMetadataCount
     * 
     * 
     * 
     * @return int
     */
    public int getMetadataCount()
    {
        return _metadataList.size();
    } //-- int getMetadataCount() 

    /**
     * Returns the value of field 'signature'.
     * 
     * @return String
     * @return the value of field 'signature'.
     */
    public java.lang.String getSignature()
    {
        return this._signature;
    } //-- java.lang.String getSignature() 

    /**
     * Returns the value of field 'storageClass'.
     * 
     * @return StorageClass
     * @return the value of field 'storageClass'.
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.types.StorageClass getStorageClass()
    {
        return this._storageClass;
    } //-- org.jets3t.service.soap._2006_03_01.types.StorageClass getStorageClass() 

    /**
     * Returns the value of field 'timestamp'.
     * 
     * @return Date
     * @return the value of field 'timestamp'.
     */
    public java.util.Date getTimestamp()
    {
        return this._timestamp;
    } //-- java.util.Date getTimestamp() 

    /**
     * Method hasContentLength
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasContentLength()
    {
        return this._has_contentLength;
    } //-- boolean hasContentLength() 

    /**
     * Method isValid
     * 
     * 
     * 
     * @return boolean
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Method removeMetadata
     * 
     * 
     * 
     * @param vMetadata
     * @return boolean
     */
    public boolean removeMetadata(org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry vMetadata)
    {
        boolean removed = _metadataList.remove(vMetadata);
        return removed;
    } //-- boolean removeMetadata(org.jets3t.service.soap._2006_03_01.MetadataEntry) 

    /**
     * Sets the value of field 'AWSAccessKeyId'.
     * 
     * @param AWSAccessKeyId the value of field 'AWSAccessKeyId'.
     */
    public void setAWSAccessKeyId(java.lang.String AWSAccessKeyId)
    {
        this._AWSAccessKeyId = AWSAccessKeyId;
    } //-- void setAWSAccessKeyId(java.lang.String) 

    /**
     * Sets the value of field 'accessControlList'.
     * 
     * @param accessControlList the value of field
     * 'accessControlList'.
     */
    public void setAccessControlList(org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlList accessControlList)
    {
        this._accessControlList = accessControlList;
    } //-- void setAccessControlList(org.jets3t.service.soap._2006_03_01.AccessControlList) 

    /**
     * Sets the value of field 'bucket'.
     * 
     * @param bucket the value of field 'bucket'.
     */
    public void setBucket(java.lang.String bucket)
    {
        this._bucket = bucket;
    } //-- void setBucket(java.lang.String) 

    /**
     * Sets the value of field 'contentLength'.
     * 
     * @param contentLength the value of field 'contentLength'.
     */
    public void setContentLength(long contentLength)
    {
        this._contentLength = contentLength;
        this._has_contentLength = true;
    } //-- void setContentLength(long) 

    /**
     * Sets the value of field 'credential'.
     * 
     * @param credential the value of field 'credential'.
     */
    public void setCredential(java.lang.String credential)
    {
        this._credential = credential;
    } //-- void setCredential(java.lang.String) 

    /**
     * Sets the value of field 'data'.
     * 
     * @param data the value of field 'data'.
     */
    public void setData(byte[] data)
    {
        this._data = data;
    } //-- void setData(byte) 

    /**
     * Sets the value of field 'key'.
     * 
     * @param key the value of field 'key'.
     */
    public void setKey(java.lang.String key)
    {
        this._key = key;
    } //-- void setKey(java.lang.String) 

    /**
     * Method setMetadata
     * 
     * 
     * 
     * @param index
     * @param vMetadata
     */
    public void setMetadata(int index, org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry vMetadata)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _metadataList.size())) {
            throw new IndexOutOfBoundsException();
        }
        if (!(index < 100)) {
            throw new IndexOutOfBoundsException();
        }
        _metadataList.set(index, vMetadata);
    } //-- void setMetadata(int, org.jets3t.service.soap._2006_03_01.MetadataEntry) 

    /**
     * Method setMetadata
     * 
     * 
     * 
     * @param metadataArray
     */
    public void setMetadata(org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry[] metadataArray)
    {
        //-- copy array
        _metadataList.clear();
        for (int i = 0; i < metadataArray.length; i++) {
            _metadataList.add(metadataArray[i]);
        }
    } //-- void setMetadata(org.jets3t.service.soap._2006_03_01.MetadataEntry) 

    /**
     * Sets the value of field 'signature'.
     * 
     * @param signature the value of field 'signature'.
     */
    public void setSignature(java.lang.String signature)
    {
        this._signature = signature;
    } //-- void setSignature(java.lang.String) 

    /**
     * Sets the value of field 'storageClass'.
     * 
     * @param storageClass the value of field 'storageClass'.
     */
    public void setStorageClass(org.jets3t.service.impl.soap.axis._2006_03_01.types.StorageClass storageClass)
    {
        this._storageClass = storageClass;
    } //-- void setStorageClass(org.jets3t.service.soap._2006_03_01.types.StorageClass) 

    /**
     * Sets the value of field 'timestamp'.
     * 
     * @param timestamp the value of field 'timestamp'.
     */
    public void setTimestamp(java.util.Date timestamp)
    {
        this._timestamp = timestamp;
    } //-- void setTimestamp(java.util.Date) 

    /**
     * Method unmarshalPutObjectInline
     * 
     * 
     * 
     * @param reader
     * @return PutObjectInline
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectInline unmarshalPutObjectInline(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectInline) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectInline.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.PutObjectInline unmarshalPutObjectInline(java.io.Reader) 

    /**
     * Method validate
     * 
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
