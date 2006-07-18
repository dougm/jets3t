/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: ListAllMyBucketsResponse.java,v 1.1 2006/07/18 05:15:18 jmurty Exp $
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
 * Class ListAllMyBucketsResponse.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:18 $
 */
public class ListAllMyBucketsResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _listAllMyBucketsResponse
     */
    private org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsResult _listAllMyBucketsResponse;


      //----------------/
     //- Constructors -/
    //----------------/

    public ListAllMyBucketsResponse() 
     {
        super();
    } //-- org.jets3t.service.soap._2006_03_01.ListAllMyBucketsResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'listAllMyBucketsResponse'.
     * 
     * @return ListAllMyBucketsResult
     * @return the value of field 'listAllMyBucketsResponse'.
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsResult getListAllMyBucketsResponse()
    {
        return this._listAllMyBucketsResponse;
    } //-- org.jets3t.service.soap._2006_03_01.ListAllMyBucketsResult getListAllMyBucketsResponse() 

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
     * Sets the value of field 'listAllMyBucketsResponse'.
     * 
     * @param listAllMyBucketsResponse the value of field
     * 'listAllMyBucketsResponse'.
     */
    public void setListAllMyBucketsResponse(org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsResult listAllMyBucketsResponse)
    {
        this._listAllMyBucketsResponse = listAllMyBucketsResponse;
    } //-- void setListAllMyBucketsResponse(org.jets3t.service.soap._2006_03_01.ListAllMyBucketsResult) 

    /**
     * Method unmarshalListAllMyBucketsResponse
     * 
     * 
     * 
     * @param reader
     * @return ListAllMyBucketsResponse
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsResponse unmarshalListAllMyBucketsResponse(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsResponse) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsResponse.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.ListAllMyBucketsResponse unmarshalListAllMyBucketsResponse(java.io.Reader) 

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
