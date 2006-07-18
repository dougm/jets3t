/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: PutObjectInlineResponse.java,v 1.1 2006/07/18 05:15:39 jmurty Exp $
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
 * Class PutObjectInlineResponse.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:39 $
 */
public class PutObjectInlineResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _putObjectInlineResponse
     */
    private org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectResult _putObjectInlineResponse;


      //----------------/
     //- Constructors -/
    //----------------/

    public PutObjectInlineResponse() 
     {
        super();
    } //-- org.jets3t.service.soap._2006_03_01.PutObjectInlineResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'putObjectInlineResponse'.
     * 
     * @return PutObjectResult
     * @return the value of field 'putObjectInlineResponse'.
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectResult getPutObjectInlineResponse()
    {
        return this._putObjectInlineResponse;
    } //-- org.jets3t.service.soap._2006_03_01.PutObjectResult getPutObjectInlineResponse() 

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
     * Sets the value of field 'putObjectInlineResponse'.
     * 
     * @param putObjectInlineResponse the value of field
     * 'putObjectInlineResponse'.
     */
    public void setPutObjectInlineResponse(org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectResult putObjectInlineResponse)
    {
        this._putObjectInlineResponse = putObjectInlineResponse;
    } //-- void setPutObjectInlineResponse(org.jets3t.service.soap._2006_03_01.PutObjectResult) 

    /**
     * Method unmarshalPutObjectInlineResponse
     * 
     * 
     * 
     * @param reader
     * @return PutObjectInlineResponse
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectInlineResponse unmarshalPutObjectInlineResponse(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectInlineResponse) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectInlineResponse.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.PutObjectInlineResponse unmarshalPutObjectInlineResponse(java.io.Reader) 

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
