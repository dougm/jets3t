/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: ListBucketResponse.java,v 1.1 2006/07/18 05:15:43 jmurty Exp $
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
 * Class ListBucketResponse.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:43 $
 */
public class ListBucketResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _listBucketResponse
     */
    private org.jets3t.service.impl.soap.axis._2006_03_01.ListBucketResult _listBucketResponse;


      //----------------/
     //- Constructors -/
    //----------------/

    public ListBucketResponse() 
     {
        super();
    } //-- org.jets3t.service.soap._2006_03_01.ListBucketResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'listBucketResponse'.
     * 
     * @return ListBucketResult
     * @return the value of field 'listBucketResponse'.
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.ListBucketResult getListBucketResponse()
    {
        return this._listBucketResponse;
    } //-- org.jets3t.service.soap._2006_03_01.ListBucketResult getListBucketResponse() 

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
     * Sets the value of field 'listBucketResponse'.
     * 
     * @param listBucketResponse the value of field
     * 'listBucketResponse'.
     */
    public void setListBucketResponse(org.jets3t.service.impl.soap.axis._2006_03_01.ListBucketResult listBucketResponse)
    {
        this._listBucketResponse = listBucketResponse;
    } //-- void setListBucketResponse(org.jets3t.service.soap._2006_03_01.ListBucketResult) 

    /**
     * Method unmarshalListBucketResponse
     * 
     * 
     * 
     * @param reader
     * @return ListBucketResponse
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.ListBucketResponse unmarshalListBucketResponse(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.ListBucketResponse) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.ListBucketResponse.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.ListBucketResponse unmarshalListBucketResponse(java.io.Reader) 

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
