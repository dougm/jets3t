/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: SetBucketAccessControlPolicy.java,v 1.1 2006/07/18 05:15:31 jmurty Exp $
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
 * Class SetBucketAccessControlPolicy.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:31 $
 */
public class SetBucketAccessControlPolicy implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _bucket
     */
    private java.lang.String _bucket;

    /**
     * Field _accessControlList
     */
    private org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlList _accessControlList;

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

    public SetBucketAccessControlPolicy() 
     {
        super();
    } //-- org.jets3t.service.soap._2006_03_01.SetBucketAccessControlPolicy()


      //-----------/
     //- Methods -/
    //-----------/

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
     * Sets the value of field 'credential'.
     * 
     * @param credential the value of field 'credential'.
     */
    public void setCredential(java.lang.String credential)
    {
        this._credential = credential;
    } //-- void setCredential(java.lang.String) 

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
     * Method unmarshalSetBucketAccessControlPolicy
     * 
     * 
     * 
     * @param reader
     * @return SetBucketAccessControlPolicy
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.SetBucketAccessControlPolicy unmarshalSetBucketAccessControlPolicy(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.SetBucketAccessControlPolicy) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.SetBucketAccessControlPolicy.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.SetBucketAccessControlPolicy unmarshalSetBucketAccessControlPolicy(java.io.Reader) 

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
