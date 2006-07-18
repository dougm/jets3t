/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: Grant.java,v 1.1 2006/07/18 05:15:19 jmurty Exp $
 */

package org.jets3t.service.impl.soap.axis._2006_03_01;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.jets3t.service.impl.soap.axis._2006_03_01.types.Permission;

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
 * Class Grant.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:19 $
 */
public class Grant implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _grantee
     */
    private org.jets3t.service.impl.soap.axis._2006_03_01.Grantee _grantee;

    /**
     * Field _permission
     */
    private org.jets3t.service.impl.soap.axis._2006_03_01.types.Permission _permission;


      //----------------/
     //- Constructors -/
    //----------------/

    public Grant() 
     {
        super();
    } //-- org.jets3t.service.soap._2006_03_01.Grant()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'grantee'.
     * 
     * @return Grantee
     * @return the value of field 'grantee'.
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.Grantee getGrantee()
    {
        return this._grantee;
    } //-- org.jets3t.service.soap._2006_03_01.Grantee getGrantee() 

    /**
     * Returns the value of field 'permission'.
     * 
     * @return Permission
     * @return the value of field 'permission'.
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.types.Permission getPermission()
    {
        return this._permission;
    } //-- org.jets3t.service.soap._2006_03_01.types.Permission getPermission() 

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
     * Sets the value of field 'grantee'.
     * 
     * @param grantee the value of field 'grantee'.
     */
    public void setGrantee(org.jets3t.service.impl.soap.axis._2006_03_01.Grantee grantee)
    {
        this._grantee = grantee;
    } //-- void setGrantee(org.jets3t.service.soap._2006_03_01.Grantee) 

    /**
     * Sets the value of field 'permission'.
     * 
     * @param permission the value of field 'permission'.
     */
    public void setPermission(org.jets3t.service.impl.soap.axis._2006_03_01.types.Permission permission)
    {
        this._permission = permission;
    } //-- void setPermission(org.jets3t.service.soap._2006_03_01.types.Permission) 

    /**
     * Method unmarshalGrant
     * 
     * 
     * 
     * @param reader
     * @return Grant
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.Grant unmarshalGrant(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.Grant) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.Grant.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.Grant unmarshalGrant(java.io.Reader) 

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
