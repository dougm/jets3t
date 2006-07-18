/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: CanonicalUser.java,v 1.1 2006/07/18 05:15:20 jmurty Exp $
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
 * Class CanonicalUser.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:20 $
 */
public class CanonicalUser extends org.jets3t.service.impl.soap.axis._2006_03_01.User 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _ID
     */
    private java.lang.String _ID;

    /**
     * Field _displayName
     */
    private java.lang.String _displayName;


      //----------------/
     //- Constructors -/
    //----------------/

    public CanonicalUser() 
     {
        super();
    } //-- org.jets3t.service.soap._2006_03_01.CanonicalUser()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'displayName'.
     * 
     * @return String
     * @return the value of field 'displayName'.
     */
    public java.lang.String getDisplayName()
    {
        return this._displayName;
    } //-- java.lang.String getDisplayName() 

    /**
     * Returns the value of field 'ID'.
     * 
     * @return String
     * @return the value of field 'ID'.
     */
    public java.lang.String getID()
    {
        return this._ID;
    } //-- java.lang.String getID() 

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
     * Sets the value of field 'displayName'.
     * 
     * @param displayName the value of field 'displayName'.
     */
    public void setDisplayName(java.lang.String displayName)
    {
        this._displayName = displayName;
    } //-- void setDisplayName(java.lang.String) 

    /**
     * Sets the value of field 'ID'.
     * 
     * @param ID the value of field 'ID'.
     */
    public void setID(java.lang.String ID)
    {
        this._ID = ID;
    } //-- void setID(java.lang.String) 

    /**
     * Method unmarshalCanonicalUser
     * 
     * 
     * 
     * @param reader
     * @return Grantee
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.Grantee unmarshalCanonicalUser(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.Grantee) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.CanonicalUser.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.Grantee unmarshalCanonicalUser(java.io.Reader) 

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
