/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: PutObjectResult.java,v 1.1 2006/07/18 05:15:46 jmurty Exp $
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
 * Class PutObjectResult.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:46 $
 */
public class PutObjectResult implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _ETag
     */
    private java.lang.String _ETag;

    /**
     * Field _lastModified
     */
    private java.util.Date _lastModified;


      //----------------/
     //- Constructors -/
    //----------------/

    public PutObjectResult() 
     {
        super();
    } //-- org.jets3t.service.soap._2006_03_01.PutObjectResult()


      //-----------/
     //- Methods -/
    //-----------/

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
     * Sets the value of field 'lastModified'.
     * 
     * @param lastModified the value of field 'lastModified'.
     */
    public void setLastModified(java.util.Date lastModified)
    {
        this._lastModified = lastModified;
    } //-- void setLastModified(java.util.Date) 

    /**
     * Method unmarshalPutObjectResult
     * 
     * 
     * 
     * @param reader
     * @return PutObjectResult
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectResult unmarshalPutObjectResult(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectResult) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectResult.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.PutObjectResult unmarshalPutObjectResult(java.io.Reader) 

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
