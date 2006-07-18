/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: AccessControlList.java,v 1.1 2006/07/18 05:15:38 jmurty Exp $
 */

package org.jets3t.service.impl.soap.axis._2006_03_01;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class AccessControlList.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:38 $
 */
public class AccessControlList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _grantList
     */
    private java.util.ArrayList _grantList;


      //----------------/
     //- Constructors -/
    //----------------/

    public AccessControlList() 
     {
        super();
        _grantList = new ArrayList();
    } //-- org.jets3t.service.soap._2006_03_01.AccessControlList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addGrant
     * 
     * 
     * 
     * @param vGrant
     */
    public void addGrant(org.jets3t.service.impl.soap.axis._2006_03_01.Grant vGrant)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_grantList.size() < 100)) {
            throw new IndexOutOfBoundsException();
        }
        _grantList.add(vGrant);
    } //-- void addGrant(org.jets3t.service.soap._2006_03_01.Grant) 

    /**
     * Method addGrant
     * 
     * 
     * 
     * @param index
     * @param vGrant
     */
    public void addGrant(int index, org.jets3t.service.impl.soap.axis._2006_03_01.Grant vGrant)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_grantList.size() < 100)) {
            throw new IndexOutOfBoundsException();
        }
        _grantList.add(index, vGrant);
    } //-- void addGrant(int, org.jets3t.service.soap._2006_03_01.Grant) 

    /**
     * Method clearGrant
     * 
     */
    public void clearGrant()
    {
        _grantList.clear();
    } //-- void clearGrant() 

    /**
     * Method enumerateGrant
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateGrant()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_grantList.iterator());
    } //-- java.util.Enumeration enumerateGrant() 

    /**
     * Method getGrant
     * 
     * 
     * 
     * @param index
     * @return Grant
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.Grant getGrant(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _grantList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (org.jets3t.service.impl.soap.axis._2006_03_01.Grant) _grantList.get(index);
    } //-- org.jets3t.service.soap._2006_03_01.Grant getGrant(int) 

    /**
     * Method getGrant
     * 
     * 
     * 
     * @return Grant
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.Grant[] getGrant()
    {
        int size = _grantList.size();
        org.jets3t.service.impl.soap.axis._2006_03_01.Grant[] mArray = new org.jets3t.service.impl.soap.axis._2006_03_01.Grant[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.jets3t.service.impl.soap.axis._2006_03_01.Grant) _grantList.get(index);
        }
        return mArray;
    } //-- org.jets3t.service.soap._2006_03_01.Grant[] getGrant() 

    /**
     * Method getGrantCount
     * 
     * 
     * 
     * @return int
     */
    public int getGrantCount()
    {
        return _grantList.size();
    } //-- int getGrantCount() 

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
     * Method removeGrant
     * 
     * 
     * 
     * @param vGrant
     * @return boolean
     */
    public boolean removeGrant(org.jets3t.service.impl.soap.axis._2006_03_01.Grant vGrant)
    {
        boolean removed = _grantList.remove(vGrant);
        return removed;
    } //-- boolean removeGrant(org.jets3t.service.soap._2006_03_01.Grant) 

    /**
     * Method setGrant
     * 
     * 
     * 
     * @param index
     * @param vGrant
     */
    public void setGrant(int index, org.jets3t.service.impl.soap.axis._2006_03_01.Grant vGrant)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _grantList.size())) {
            throw new IndexOutOfBoundsException();
        }
        if (!(index < 100)) {
            throw new IndexOutOfBoundsException();
        }
        _grantList.set(index, vGrant);
    } //-- void setGrant(int, org.jets3t.service.soap._2006_03_01.Grant) 

    /**
     * Method setGrant
     * 
     * 
     * 
     * @param grantArray
     */
    public void setGrant(org.jets3t.service.impl.soap.axis._2006_03_01.Grant[] grantArray)
    {
        //-- copy array
        _grantList.clear();
        for (int i = 0; i < grantArray.length; i++) {
            _grantList.add(grantArray[i]);
        }
    } //-- void setGrant(org.jets3t.service.soap._2006_03_01.Grant) 

    /**
     * Method unmarshalAccessControlList
     * 
     * 
     * 
     * @param reader
     * @return AccessControlList
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlList unmarshalAccessControlList(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlList) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlList.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.AccessControlList unmarshalAccessControlList(java.io.Reader) 

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
