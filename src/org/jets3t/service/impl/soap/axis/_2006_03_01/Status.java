/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: Status.java,v 1.1 2006/07/18 05:15:40 jmurty Exp $
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
 * Class Status.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:40 $
 */
public class Status implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _code
     */
    private int _code;

    /**
     * keeps track of state for field: _code
     */
    private boolean _has_code;

    /**
     * Field _description
     */
    private java.lang.String _description;


      //----------------/
     //- Constructors -/
    //----------------/

    public Status() 
     {
        super();
    } //-- org.jets3t.service.soap._2006_03_01.Status()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteCode
     * 
     */
    public void deleteCode()
    {
        this._has_code= false;
    } //-- void deleteCode() 

    /**
     * Returns the value of field 'code'.
     * 
     * @return int
     * @return the value of field 'code'.
     */
    public int getCode()
    {
        return this._code;
    } //-- int getCode() 

    /**
     * Returns the value of field 'description'.
     * 
     * @return String
     * @return the value of field 'description'.
     */
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

    /**
     * Method hasCode
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasCode()
    {
        return this._has_code;
    } //-- boolean hasCode() 

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
     * Sets the value of field 'code'.
     * 
     * @param code the value of field 'code'.
     */
    public void setCode(int code)
    {
        this._code = code;
        this._has_code = true;
    } //-- void setCode(int) 

    /**
     * Sets the value of field 'description'.
     * 
     * @param description the value of field 'description'.
     */
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

    /**
     * Method unmarshalStatus
     * 
     * 
     * 
     * @param reader
     * @return Status
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.Status unmarshalStatus(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.Status) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.Status.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.Status unmarshalStatus(java.io.Reader) 

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
