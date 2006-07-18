/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: GetObjectExtended.java,v 1.1 2006/07/18 05:15:48 jmurty Exp $
 */

package org.jets3t.service.impl.soap.axis._2006_03_01;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

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
 * Class GetObjectExtended.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:48 $
 */
public class GetObjectExtended implements java.io.Serializable {


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
     * Field _getMetadata
     */
    private boolean _getMetadata;

    /**
     * keeps track of state for field: _getMetadata
     */
    private boolean _has_getMetadata;

    /**
     * Field _getData
     */
    private boolean _getData;

    /**
     * keeps track of state for field: _getData
     */
    private boolean _has_getData;

    /**
     * Field _inlineData
     */
    private boolean _inlineData;

    /**
     * keeps track of state for field: _inlineData
     */
    private boolean _has_inlineData;

    /**
     * Field _byteRangeStart
     */
    private long _byteRangeStart;

    /**
     * keeps track of state for field: _byteRangeStart
     */
    private boolean _has_byteRangeStart;

    /**
     * Field _byteRangeEnd
     */
    private long _byteRangeEnd;

    /**
     * keeps track of state for field: _byteRangeEnd
     */
    private boolean _has_byteRangeEnd;

    /**
     * Field _ifModifiedSince
     */
    private java.util.Date _ifModifiedSince;

    /**
     * Field _ifUnmodifiedSince
     */
    private java.util.Date _ifUnmodifiedSince;

    /**
     * Field _ifMatchList
     */
    private java.util.ArrayList _ifMatchList;

    /**
     * Field _ifNoneMatchList
     */
    private java.util.ArrayList _ifNoneMatchList;

    /**
     * Field _returnCompleteObjectOnConditionFailure
     */
    private boolean _returnCompleteObjectOnConditionFailure;

    /**
     * keeps track of state for field:
     * _returnCompleteObjectOnConditionFailure
     */
    private boolean _has_returnCompleteObjectOnConditionFailure;

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

    public GetObjectExtended() 
     {
        super();
        _ifMatchList = new ArrayList();
        _ifNoneMatchList = new ArrayList();
    } //-- org.jets3t.service.soap._2006_03_01.GetObjectExtended()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addIfMatch
     * 
     * 
     * 
     * @param vIfMatch
     */
    public void addIfMatch(java.lang.String vIfMatch)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_ifMatchList.size() < 100)) {
            throw new IndexOutOfBoundsException();
        }
        _ifMatchList.add(vIfMatch);
    } //-- void addIfMatch(java.lang.String) 

    /**
     * Method addIfMatch
     * 
     * 
     * 
     * @param index
     * @param vIfMatch
     */
    public void addIfMatch(int index, java.lang.String vIfMatch)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_ifMatchList.size() < 100)) {
            throw new IndexOutOfBoundsException();
        }
        _ifMatchList.add(index, vIfMatch);
    } //-- void addIfMatch(int, java.lang.String) 

    /**
     * Method addIfNoneMatch
     * 
     * 
     * 
     * @param vIfNoneMatch
     */
    public void addIfNoneMatch(java.lang.String vIfNoneMatch)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_ifNoneMatchList.size() < 100)) {
            throw new IndexOutOfBoundsException();
        }
        _ifNoneMatchList.add(vIfNoneMatch);
    } //-- void addIfNoneMatch(java.lang.String) 

    /**
     * Method addIfNoneMatch
     * 
     * 
     * 
     * @param index
     * @param vIfNoneMatch
     */
    public void addIfNoneMatch(int index, java.lang.String vIfNoneMatch)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_ifNoneMatchList.size() < 100)) {
            throw new IndexOutOfBoundsException();
        }
        _ifNoneMatchList.add(index, vIfNoneMatch);
    } //-- void addIfNoneMatch(int, java.lang.String) 

    /**
     * Method clearIfMatch
     * 
     */
    public void clearIfMatch()
    {
        _ifMatchList.clear();
    } //-- void clearIfMatch() 

    /**
     * Method clearIfNoneMatch
     * 
     */
    public void clearIfNoneMatch()
    {
        _ifNoneMatchList.clear();
    } //-- void clearIfNoneMatch() 

    /**
     * Method deleteByteRangeEnd
     * 
     */
    public void deleteByteRangeEnd()
    {
        this._has_byteRangeEnd= false;
    } //-- void deleteByteRangeEnd() 

    /**
     * Method deleteByteRangeStart
     * 
     */
    public void deleteByteRangeStart()
    {
        this._has_byteRangeStart= false;
    } //-- void deleteByteRangeStart() 

    /**
     * Method deleteGetData
     * 
     */
    public void deleteGetData()
    {
        this._has_getData= false;
    } //-- void deleteGetData() 

    /**
     * Method deleteGetMetadata
     * 
     */
    public void deleteGetMetadata()
    {
        this._has_getMetadata= false;
    } //-- void deleteGetMetadata() 

    /**
     * Method deleteInlineData
     * 
     */
    public void deleteInlineData()
    {
        this._has_inlineData= false;
    } //-- void deleteInlineData() 

    /**
     * Method deleteReturnCompleteObjectOnConditionFailure
     * 
     */
    public void deleteReturnCompleteObjectOnConditionFailure()
    {
        this._has_returnCompleteObjectOnConditionFailure= false;
    } //-- void deleteReturnCompleteObjectOnConditionFailure() 

    /**
     * Method enumerateIfMatch
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateIfMatch()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_ifMatchList.iterator());
    } //-- java.util.Enumeration enumerateIfMatch() 

    /**
     * Method enumerateIfNoneMatch
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateIfNoneMatch()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_ifNoneMatchList.iterator());
    } //-- java.util.Enumeration enumerateIfNoneMatch() 

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
     * Returns the value of field 'byteRangeEnd'.
     * 
     * @return long
     * @return the value of field 'byteRangeEnd'.
     */
    public long getByteRangeEnd()
    {
        return this._byteRangeEnd;
    } //-- long getByteRangeEnd() 

    /**
     * Returns the value of field 'byteRangeStart'.
     * 
     * @return long
     * @return the value of field 'byteRangeStart'.
     */
    public long getByteRangeStart()
    {
        return this._byteRangeStart;
    } //-- long getByteRangeStart() 

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
     * Returns the value of field 'getData'.
     * 
     * @return boolean
     * @return the value of field 'getData'.
     */
    public boolean getGetData()
    {
        return this._getData;
    } //-- boolean getGetData() 

    /**
     * Returns the value of field 'getMetadata'.
     * 
     * @return boolean
     * @return the value of field 'getMetadata'.
     */
    public boolean getGetMetadata()
    {
        return this._getMetadata;
    } //-- boolean getGetMetadata() 

    /**
     * Method getIfMatch
     * 
     * 
     * 
     * @param index
     * @return String
     */
    public java.lang.String getIfMatch(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _ifMatchList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (String)_ifMatchList.get(index);
    } //-- java.lang.String getIfMatch(int) 

    /**
     * Method getIfMatch
     * 
     * 
     * 
     * @return String
     */
    public java.lang.String[] getIfMatch()
    {
        int size = _ifMatchList.size();
        java.lang.String[] mArray = new java.lang.String[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (String)_ifMatchList.get(index);
        }
        return mArray;
    } //-- java.lang.String[] getIfMatch() 

    /**
     * Method getIfMatchCount
     * 
     * 
     * 
     * @return int
     */
    public int getIfMatchCount()
    {
        return _ifMatchList.size();
    } //-- int getIfMatchCount() 

    /**
     * Returns the value of field 'ifModifiedSince'.
     * 
     * @return Date
     * @return the value of field 'ifModifiedSince'.
     */
    public java.util.Date getIfModifiedSince()
    {
        return this._ifModifiedSince;
    } //-- java.util.Date getIfModifiedSince() 

    /**
     * Method getIfNoneMatch
     * 
     * 
     * 
     * @param index
     * @return String
     */
    public java.lang.String getIfNoneMatch(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _ifNoneMatchList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (String)_ifNoneMatchList.get(index);
    } //-- java.lang.String getIfNoneMatch(int) 

    /**
     * Method getIfNoneMatch
     * 
     * 
     * 
     * @return String
     */
    public java.lang.String[] getIfNoneMatch()
    {
        int size = _ifNoneMatchList.size();
        java.lang.String[] mArray = new java.lang.String[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (String)_ifNoneMatchList.get(index);
        }
        return mArray;
    } //-- java.lang.String[] getIfNoneMatch() 

    /**
     * Method getIfNoneMatchCount
     * 
     * 
     * 
     * @return int
     */
    public int getIfNoneMatchCount()
    {
        return _ifNoneMatchList.size();
    } //-- int getIfNoneMatchCount() 

    /**
     * Returns the value of field 'ifUnmodifiedSince'.
     * 
     * @return Date
     * @return the value of field 'ifUnmodifiedSince'.
     */
    public java.util.Date getIfUnmodifiedSince()
    {
        return this._ifUnmodifiedSince;
    } //-- java.util.Date getIfUnmodifiedSince() 

    /**
     * Returns the value of field 'inlineData'.
     * 
     * @return boolean
     * @return the value of field 'inlineData'.
     */
    public boolean getInlineData()
    {
        return this._inlineData;
    } //-- boolean getInlineData() 

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
     * Returns the value of field
     * 'returnCompleteObjectOnConditionFailure'.
     * 
     * @return boolean
     * @return the value of field
     * 'returnCompleteObjectOnConditionFailure'.
     */
    public boolean getReturnCompleteObjectOnConditionFailure()
    {
        return this._returnCompleteObjectOnConditionFailure;
    } //-- boolean getReturnCompleteObjectOnConditionFailure() 

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
     * Method hasByteRangeEnd
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasByteRangeEnd()
    {
        return this._has_byteRangeEnd;
    } //-- boolean hasByteRangeEnd() 

    /**
     * Method hasByteRangeStart
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasByteRangeStart()
    {
        return this._has_byteRangeStart;
    } //-- boolean hasByteRangeStart() 

    /**
     * Method hasGetData
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasGetData()
    {
        return this._has_getData;
    } //-- boolean hasGetData() 

    /**
     * Method hasGetMetadata
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasGetMetadata()
    {
        return this._has_getMetadata;
    } //-- boolean hasGetMetadata() 

    /**
     * Method hasInlineData
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasInlineData()
    {
        return this._has_inlineData;
    } //-- boolean hasInlineData() 

    /**
     * Method hasReturnCompleteObjectOnConditionFailure
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasReturnCompleteObjectOnConditionFailure()
    {
        return this._has_returnCompleteObjectOnConditionFailure;
    } //-- boolean hasReturnCompleteObjectOnConditionFailure() 

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
     * Method removeIfMatch
     * 
     * 
     * 
     * @param vIfMatch
     * @return boolean
     */
    public boolean removeIfMatch(java.lang.String vIfMatch)
    {
        boolean removed = _ifMatchList.remove(vIfMatch);
        return removed;
    } //-- boolean removeIfMatch(java.lang.String) 

    /**
     * Method removeIfNoneMatch
     * 
     * 
     * 
     * @param vIfNoneMatch
     * @return boolean
     */
    public boolean removeIfNoneMatch(java.lang.String vIfNoneMatch)
    {
        boolean removed = _ifNoneMatchList.remove(vIfNoneMatch);
        return removed;
    } //-- boolean removeIfNoneMatch(java.lang.String) 

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
     * Sets the value of field 'bucket'.
     * 
     * @param bucket the value of field 'bucket'.
     */
    public void setBucket(java.lang.String bucket)
    {
        this._bucket = bucket;
    } //-- void setBucket(java.lang.String) 

    /**
     * Sets the value of field 'byteRangeEnd'.
     * 
     * @param byteRangeEnd the value of field 'byteRangeEnd'.
     */
    public void setByteRangeEnd(long byteRangeEnd)
    {
        this._byteRangeEnd = byteRangeEnd;
        this._has_byteRangeEnd = true;
    } //-- void setByteRangeEnd(long) 

    /**
     * Sets the value of field 'byteRangeStart'.
     * 
     * @param byteRangeStart the value of field 'byteRangeStart'.
     */
    public void setByteRangeStart(long byteRangeStart)
    {
        this._byteRangeStart = byteRangeStart;
        this._has_byteRangeStart = true;
    } //-- void setByteRangeStart(long) 

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
     * Sets the value of field 'getData'.
     * 
     * @param getData the value of field 'getData'.
     */
    public void setGetData(boolean getData)
    {
        this._getData = getData;
        this._has_getData = true;
    } //-- void setGetData(boolean) 

    /**
     * Sets the value of field 'getMetadata'.
     * 
     * @param getMetadata the value of field 'getMetadata'.
     */
    public void setGetMetadata(boolean getMetadata)
    {
        this._getMetadata = getMetadata;
        this._has_getMetadata = true;
    } //-- void setGetMetadata(boolean) 

    /**
     * Method setIfMatch
     * 
     * 
     * 
     * @param index
     * @param vIfMatch
     */
    public void setIfMatch(int index, java.lang.String vIfMatch)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _ifMatchList.size())) {
            throw new IndexOutOfBoundsException();
        }
        if (!(index < 100)) {
            throw new IndexOutOfBoundsException();
        }
        _ifMatchList.set(index, vIfMatch);
    } //-- void setIfMatch(int, java.lang.String) 

    /**
     * Method setIfMatch
     * 
     * 
     * 
     * @param ifMatchArray
     */
    public void setIfMatch(java.lang.String[] ifMatchArray)
    {
        //-- copy array
        _ifMatchList.clear();
        for (int i = 0; i < ifMatchArray.length; i++) {
            _ifMatchList.add(ifMatchArray[i]);
        }
    } //-- void setIfMatch(java.lang.String) 

    /**
     * Sets the value of field 'ifModifiedSince'.
     * 
     * @param ifModifiedSince the value of field 'ifModifiedSince'.
     */
    public void setIfModifiedSince(java.util.Date ifModifiedSince)
    {
        this._ifModifiedSince = ifModifiedSince;
    } //-- void setIfModifiedSince(java.util.Date) 

    /**
     * Method setIfNoneMatch
     * 
     * 
     * 
     * @param index
     * @param vIfNoneMatch
     */
    public void setIfNoneMatch(int index, java.lang.String vIfNoneMatch)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _ifNoneMatchList.size())) {
            throw new IndexOutOfBoundsException();
        }
        if (!(index < 100)) {
            throw new IndexOutOfBoundsException();
        }
        _ifNoneMatchList.set(index, vIfNoneMatch);
    } //-- void setIfNoneMatch(int, java.lang.String) 

    /**
     * Method setIfNoneMatch
     * 
     * 
     * 
     * @param ifNoneMatchArray
     */
    public void setIfNoneMatch(java.lang.String[] ifNoneMatchArray)
    {
        //-- copy array
        _ifNoneMatchList.clear();
        for (int i = 0; i < ifNoneMatchArray.length; i++) {
            _ifNoneMatchList.add(ifNoneMatchArray[i]);
        }
    } //-- void setIfNoneMatch(java.lang.String) 

    /**
     * Sets the value of field 'ifUnmodifiedSince'.
     * 
     * @param ifUnmodifiedSince the value of field
     * 'ifUnmodifiedSince'.
     */
    public void setIfUnmodifiedSince(java.util.Date ifUnmodifiedSince)
    {
        this._ifUnmodifiedSince = ifUnmodifiedSince;
    } //-- void setIfUnmodifiedSince(java.util.Date) 

    /**
     * Sets the value of field 'inlineData'.
     * 
     * @param inlineData the value of field 'inlineData'.
     */
    public void setInlineData(boolean inlineData)
    {
        this._inlineData = inlineData;
        this._has_inlineData = true;
    } //-- void setInlineData(boolean) 

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
     * Sets the value of field
     * 'returnCompleteObjectOnConditionFailure'.
     * 
     * @param returnCompleteObjectOnConditionFailure the value of
     * field 'returnCompleteObjectOnConditionFailure'.
     */
    public void setReturnCompleteObjectOnConditionFailure(boolean returnCompleteObjectOnConditionFailure)
    {
        this._returnCompleteObjectOnConditionFailure = returnCompleteObjectOnConditionFailure;
        this._has_returnCompleteObjectOnConditionFailure = true;
    } //-- void setReturnCompleteObjectOnConditionFailure(boolean) 

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
     * Sets the value of field 'timestamp'.
     * 
     * @param timestamp the value of field 'timestamp'.
     */
    public void setTimestamp(java.util.Date timestamp)
    {
        this._timestamp = timestamp;
    } //-- void setTimestamp(java.util.Date) 

    /**
     * Method unmarshalGetObjectExtended
     * 
     * 
     * 
     * @param reader
     * @return GetObjectExtended
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectExtended unmarshalGetObjectExtended(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectExtended) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectExtended.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.GetObjectExtended unmarshalGetObjectExtended(java.io.Reader) 

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
