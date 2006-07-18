/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: ListEntry.java,v 1.1 2006/07/18 05:15:26 jmurty Exp $
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
import java.util.Date;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class ListEntry.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:26 $
 */
public class ListEntry implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _key
     */
    private java.lang.String _key;

    /**
     * Field _lastModified
     */
    private java.util.Date _lastModified;

    /**
     * Field _ETag
     */
    private java.lang.String _ETag;

    /**
     * Field _size
     */
    private long _size;

    /**
     * keeps track of state for field: _size
     */
    private boolean _has_size;

    /**
     * Field _owner
     */
    private org.jets3t.service.impl.soap.axis._2006_03_01.CanonicalUser _owner;

    /**
     * Field _storageClass
     */
    private org.jets3t.service.impl.soap.axis._2006_03_01.types.StorageClass _storageClass;


      //----------------/
     //- Constructors -/
    //----------------/

    public ListEntry() 
     {
        super();
    } //-- org.jets3t.service.soap._2006_03_01.ListEntry()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteSize
     * 
     */
    public void deleteSize()
    {
        this._has_size= false;
    } //-- void deleteSize() 

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
     * Returns the value of field 'owner'.
     * 
     * @return CanonicalUser
     * @return the value of field 'owner'.
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.CanonicalUser getOwner()
    {
        return this._owner;
    } //-- org.jets3t.service.soap._2006_03_01.CanonicalUser getOwner() 

    /**
     * Returns the value of field 'size'.
     * 
     * @return long
     * @return the value of field 'size'.
     */
    public long getSize()
    {
        return this._size;
    } //-- long getSize() 

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
     * Method hasSize
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasSize()
    {
        return this._has_size;
    } //-- boolean hasSize() 

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
     * Sets the value of field 'ETag'.
     * 
     * @param ETag the value of field 'ETag'.
     */
    public void setETag(java.lang.String ETag)
    {
        this._ETag = ETag;
    } //-- void setETag(java.lang.String) 

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
     * Sets the value of field 'lastModified'.
     * 
     * @param lastModified the value of field 'lastModified'.
     */
    public void setLastModified(java.util.Date lastModified)
    {
        this._lastModified = lastModified;
    } //-- void setLastModified(java.util.Date) 

    /**
     * Sets the value of field 'owner'.
     * 
     * @param owner the value of field 'owner'.
     */
    public void setOwner(org.jets3t.service.impl.soap.axis._2006_03_01.CanonicalUser owner)
    {
        this._owner = owner;
    } //-- void setOwner(org.jets3t.service.soap._2006_03_01.CanonicalUser) 

    /**
     * Sets the value of field 'size'.
     * 
     * @param size the value of field 'size'.
     */
    public void setSize(long size)
    {
        this._size = size;
        this._has_size = true;
    } //-- void setSize(long) 

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
     * Method unmarshalListEntry
     * 
     * 
     * 
     * @param reader
     * @return ListEntry
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry unmarshalListEntry(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.ListEntry unmarshalListEntry(java.io.Reader) 

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
