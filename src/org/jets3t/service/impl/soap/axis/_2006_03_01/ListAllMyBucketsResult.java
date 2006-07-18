/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: ListAllMyBucketsResult.java,v 1.1 2006/07/18 05:15:35 jmurty Exp $
 */

package org.jets3t.service.impl.soap.axis._2006_03_01;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class ListAllMyBucketsResult.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:35 $
 */
public class ListAllMyBucketsResult implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _owner
     */
    private org.jets3t.service.impl.soap.axis._2006_03_01.CanonicalUser _owner;

    /**
     * Field _buckets
     */
    private org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsList _buckets;


      //----------------/
     //- Constructors -/
    //----------------/

    public ListAllMyBucketsResult() 
     {
        super();
    } //-- org.jets3t.service.soap._2006_03_01.ListAllMyBucketsResult()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'buckets'.
     * 
     * @return ListAllMyBucketsList
     * @return the value of field 'buckets'.
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsList getBuckets()
    {
        return this._buckets;
    } //-- org.jets3t.service.soap._2006_03_01.ListAllMyBucketsList getBuckets() 

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
     * Sets the value of field 'buckets'.
     * 
     * @param buckets the value of field 'buckets'.
     */
    public void setBuckets(org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsList buckets)
    {
        this._buckets = buckets;
    } //-- void setBuckets(org.jets3t.service.soap._2006_03_01.ListAllMyBucketsList) 

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
     * Method unmarshalListAllMyBucketsResult
     * 
     * 
     * 
     * @param reader
     * @return ListAllMyBucketsResult
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsResult unmarshalListAllMyBucketsResult(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsResult) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsResult.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.ListAllMyBucketsResult unmarshalListAllMyBucketsResult(java.io.Reader) 

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
