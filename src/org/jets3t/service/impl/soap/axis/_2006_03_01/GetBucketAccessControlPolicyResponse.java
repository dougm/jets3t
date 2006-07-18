/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: GetBucketAccessControlPolicyResponse.java,v 1.1 2006/07/18 05:15:19 jmurty Exp $
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
 * Class GetBucketAccessControlPolicyResponse.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:19 $
 */
public class GetBucketAccessControlPolicyResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _getBucketAccessControlPolicyResponse
     */
    private org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlPolicy _getBucketAccessControlPolicyResponse;


      //----------------/
     //- Constructors -/
    //----------------/

    public GetBucketAccessControlPolicyResponse() 
     {
        super();
    } //-- org.jets3t.service.soap._2006_03_01.GetBucketAccessControlPolicyResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field
     * 'getBucketAccessControlPolicyResponse'.
     * 
     * @return AccessControlPolicy
     * @return the value of field
     * 'getBucketAccessControlPolicyResponse'.
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlPolicy getGetBucketAccessControlPolicyResponse()
    {
        return this._getBucketAccessControlPolicyResponse;
    } //-- org.jets3t.service.soap._2006_03_01.AccessControlPolicy getGetBucketAccessControlPolicyResponse() 

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
     * Sets the value of field
     * 'getBucketAccessControlPolicyResponse'.
     * 
     * @param getBucketAccessControlPolicyResponse the value of
     * field 'getBucketAccessControlPolicyResponse'.
     */
    public void setGetBucketAccessControlPolicyResponse(org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlPolicy getBucketAccessControlPolicyResponse)
    {
        this._getBucketAccessControlPolicyResponse = getBucketAccessControlPolicyResponse;
    } //-- void setGetBucketAccessControlPolicyResponse(org.jets3t.service.soap._2006_03_01.AccessControlPolicy) 

    /**
     * Method unmarshalGetBucketAccessControlPolicyResponse
     * 
     * 
     * 
     * @param reader
     * @return GetBucketAccessControlPolicyResponse
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.GetBucketAccessControlPolicyResponse unmarshalGetBucketAccessControlPolicyResponse(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.GetBucketAccessControlPolicyResponse) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.GetBucketAccessControlPolicyResponse.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.GetBucketAccessControlPolicyResponse unmarshalGetBucketAccessControlPolicyResponse(java.io.Reader) 

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
