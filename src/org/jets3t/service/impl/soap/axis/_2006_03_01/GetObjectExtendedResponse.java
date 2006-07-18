/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: GetObjectExtendedResponse.java,v 1.1 2006/07/18 05:15:29 jmurty Exp $
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
 * Class GetObjectExtendedResponse.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:29 $
 */
public class GetObjectExtendedResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _getObjectResponse
     */
    private org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectResult _getObjectResponse;


      //----------------/
     //- Constructors -/
    //----------------/

    public GetObjectExtendedResponse() 
     {
        super();
    } //-- org.jets3t.service.soap._2006_03_01.GetObjectExtendedResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'getObjectResponse'.
     * 
     * @return GetObjectResult
     * @return the value of field 'getObjectResponse'.
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectResult getGetObjectResponse()
    {
        return this._getObjectResponse;
    } //-- org.jets3t.service.soap._2006_03_01.GetObjectResult getGetObjectResponse() 

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
     * Sets the value of field 'getObjectResponse'.
     * 
     * @param getObjectResponse the value of field
     * 'getObjectResponse'.
     */
    public void setGetObjectResponse(org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectResult getObjectResponse)
    {
        this._getObjectResponse = getObjectResponse;
    } //-- void setGetObjectResponse(org.jets3t.service.soap._2006_03_01.GetObjectResult) 

    /**
     * Method unmarshalGetObjectExtendedResponse
     * 
     * 
     * 
     * @param reader
     * @return GetObjectExtendedResponse
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectExtendedResponse unmarshalGetObjectExtendedResponse(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectExtendedResponse) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectExtendedResponse.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.GetObjectExtendedResponse unmarshalGetObjectExtendedResponse(java.io.Reader) 

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
