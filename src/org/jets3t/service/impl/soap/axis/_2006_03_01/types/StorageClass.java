/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: StorageClass.java,v 1.1 2006/07/18 05:16:08 jmurty Exp $
 */

package org.jets3t.service.impl.soap.axis._2006_03_01.types;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class StorageClass.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:16:08 $
 */
public class StorageClass implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The STANDARD type
     */
    public static final int STANDARD_TYPE = 1;

    /**
     * The instance of the STANDARD type
     */
    public static final StorageClass STANDARD = new StorageClass(STANDARD_TYPE, "STANDARD");

    /**
     * The UNKNOWN type
     */
    public static final int UNKNOWN_TYPE = 2;

    /**
     * The instance of the UNKNOWN type
     */
    public static final StorageClass UNKNOWN = new StorageClass(UNKNOWN_TYPE, "UNKNOWN");

    /**
     * Field _memberTable
     */
    private static java.util.Hashtable _memberTable = init();

    /**
     * Field type
     */
    private int type = -1;

    /**
     * Field stringValue
     */
    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private StorageClass(int type, java.lang.String value) 
     {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- org.jets3t.service.soap._2006_03_01.types.StorageClass(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerate
     * 
     * Returns an enumeration of all possible instances of
     * StorageClass
     * 
     * @return Enumeration
     */
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Method getType
     * 
     * Returns the type of this StorageClass
     * 
     * @return int
     */
    public int getType()
    {
        return this.type;
    } //-- int getType() 

    /**
     * Method init
     * 
     * 
     * 
     * @return Hashtable
     */
    private static java.util.Hashtable init()
    {
        Hashtable members = new Hashtable();
        members.put("STANDARD", STANDARD);
        members.put("UNKNOWN", UNKNOWN);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Method readResolve
     * 
     *  will be called during deserialization to replace the
     * deserialized object with the correct constant instance.
     * <br/>
     * 
     * @return Object
     */
    private java.lang.Object readResolve()
    {
        return valueOf(this.stringValue);
    } //-- java.lang.Object readResolve() 

    /**
     * Method toString
     * 
     * Returns the String representation of this StorageClass
     * 
     * @return String
     */
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Method valueOf
     * 
     * Returns a new StorageClass based on the given String value.
     * 
     * @param string
     * @return StorageClass
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.types.StorageClass valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid StorageClass";
            throw new IllegalArgumentException(err);
        }
        return (StorageClass) obj;
    } //-- org.jets3t.service.soap._2006_03_01.types.StorageClass valueOf(java.lang.String) 

}
