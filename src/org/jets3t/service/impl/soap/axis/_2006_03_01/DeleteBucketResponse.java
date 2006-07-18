/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: DeleteBucketResponse.java,v 1.1 2006/07/18 05:15:36 jmurty Exp $
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
 * Class DeleteBucketResponse.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:36 $
 */
public class DeleteBucketResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _deleteBucketResponse
     */
    private org.jets3t.service.impl.soap.axis._2006_03_01.Status _deleteBucketResponse;


      //----------------/
     //- Constructors -/
    //----------------/

    public DeleteBucketResponse() 
     {
        super();
    } //-- org.jets3t.service.soap._2006_03_01.DeleteBucketResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'deleteBucketResponse'.
     * 
     * @return Status
     * @return the value of field 'deleteBucketResponse'.
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.Status getDeleteBucketResponse()
    {
        return this._deleteBucketResponse;
    } //-- org.jets3t.service.soap._2006_03_01.Status getDeleteBucketResponse() 

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
     * Sets the value of field 'deleteBucketResponse'.
     * 
     * @param deleteBucketResponse the value of field
     * 'deleteBucketResponse'.
     */
    public void setDeleteBucketResponse(org.jets3t.service.impl.soap.axis._2006_03_01.Status deleteBucketResponse)
    {
        this._deleteBucketResponse = deleteBucketResponse;
    } //-- void setDeleteBucketResponse(org.jets3t.service.soap._2006_03_01.Status) 

    /**
     * Method unmarshalDeleteBucketResponse
     * 
     * 
     * 
     * @param reader
     * @return DeleteBucketResponse
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.DeleteBucketResponse unmarshalDeleteBucketResponse(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.DeleteBucketResponse) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.DeleteBucketResponse.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.DeleteBucketResponse unmarshalDeleteBucketResponse(java.io.Reader) 

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
