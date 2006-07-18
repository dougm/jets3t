/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: GetObject.java,v 1.1 2006/07/18 05:15:23 jmurty Exp $
 */

package org.jets3t.service.impl.soap.axis._2006_03_01;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

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
 * Class GetObject.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:23 $
 */
public class GetObject implements java.io.Serializable {


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

    public GetObject() 
     {
        super();
    } //-- org.jets3t.service.soap._2006_03_01.GetObject()


      //-----------/
     //- Methods -/
    //-----------/

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
     * Method unmarshalGetObject
     * 
     * 
     * 
     * @param reader
     * @return GetObject
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.GetObject unmarshalGetObject(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.GetObject) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.GetObject.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.GetObject unmarshalGetObject(java.io.Reader) 

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
