/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: PrefixEntry.java,v 1.1 2006/07/18 05:15:44 jmurty Exp $
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
 * Class PrefixEntry.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:44 $
 */
public class PrefixEntry implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _prefix
     */
    private java.lang.String _prefix;


      //----------------/
     //- Constructors -/
    //----------------/

    public PrefixEntry() 
     {
        super();
    } //-- org.jets3t.service.soap._2006_03_01.PrefixEntry()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'prefix'.
     * 
     * @return String
     * @return the value of field 'prefix'.
     */
    public java.lang.String getPrefix()
    {
        return this._prefix;
    } //-- java.lang.String getPrefix() 

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
     * Sets the value of field 'prefix'.
     * 
     * @param prefix the value of field 'prefix'.
     */
    public void setPrefix(java.lang.String prefix)
    {
        this._prefix = prefix;
    } //-- void setPrefix(java.lang.String) 

    /**
     * Method unmarshalPrefixEntry
     * 
     * 
     * 
     * @param reader
     * @return PrefixEntry
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry unmarshalPrefixEntry(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.PrefixEntry unmarshalPrefixEntry(java.io.Reader) 

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
