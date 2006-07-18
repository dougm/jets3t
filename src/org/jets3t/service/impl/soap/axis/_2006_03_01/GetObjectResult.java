/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: GetObjectResult.java,v 1.1 2006/07/18 05:15:27 jmurty Exp $
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
 * Class GetObjectResult.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:27 $
 */
public class GetObjectResult extends org.jets3t.service.impl.soap.axis._2006_03_01.Result 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _metadataList
     */
    private java.util.ArrayList _metadataList;

    /**
     * Field _data
     */
    private byte[] _data;

    /**
     * Field _lastModified
     */
    private java.util.Date _lastModified;

    /**
     * Field _ETag
     */
    private java.lang.String _ETag;


      //----------------/
     //- Constructors -/
    //----------------/

    public GetObjectResult() 
     {
        super();
        _metadataList = new ArrayList();
    } //-- org.jets3t.service.soap._2006_03_01.GetObjectResult()


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
     * Returns the value of field 'ETag'.
     * 
     * @return String
     * @return the value of field 'ETag'.
     */
    public java.lang.String getETag()
    {
        return this._ETag;
    } //-- java.lang.String getETag() 

    /**
     * Returns the value of field 'lastModified'.
     * 
     * @return Date
     * @return the value of field 'lastModified'.
     */
    public java.util.Date getLastModified()
    {
        return this._lastModified;
    } //-- java.util.Date getLastModified() 

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
     * Sets the value of field 'data'.
     * 
     * @param data the value of field 'data'.
     */
    public void setData(byte[] data)
    {
        this._data = data;
    } //-- void setData(byte) 

    /**
     * Sets the value of field 'ETag'.
     * 
     * @param ETag the value of field 'ETag'.
     */
    public void setETag(java.lang.String ETag)
    {
        this._ETag = ETag;
    } //-- void setETag(java.lang.String) 

    /**
     * Sets the value of field 'lastModified'.
     * 
     * @param lastModified the value of field 'lastModified'.
     */
    public void setLastModified(java.util.Date lastModified)
    {
        this._lastModified = lastModified;
    } //-- void setLastModified(java.util.Date) 

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
     * Method unmarshalGetObjectResult
     * 
     * 
     * 
     * @param reader
     * @return Result
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.Result unmarshalGetObjectResult(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.Result) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectResult.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.Result unmarshalGetObjectResult(java.io.Reader) 

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
