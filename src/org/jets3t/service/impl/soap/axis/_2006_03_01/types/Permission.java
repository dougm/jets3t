/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: Permission.java,v 1.1 2006/07/18 05:16:08 jmurty Exp $
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
 * Class Permission.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:16:08 $
 */
public class Permission implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The READ type
     */
    public static final int READ_TYPE = 0;

    /**
     * The instance of the READ type
     */
    public static final Permission READ = new Permission(READ_TYPE, "READ");

    /**
     * The WRITE type
     */
    public static final int WRITE_TYPE = 1;

    /**
     * The instance of the WRITE type
     */
    public static final Permission WRITE = new Permission(WRITE_TYPE, "WRITE");

    /**
     * The READ_ACP type
     */
    public static final int READ_ACP_TYPE = 2;

    /**
     * The instance of the READ_ACP type
     */
    public static final Permission READ_ACP = new Permission(READ_ACP_TYPE, "READ_ACP");

    /**
     * The WRITE_ACP type
     */
    public static final int WRITE_ACP_TYPE = 3;

    /**
     * The instance of the WRITE_ACP type
     */
    public static final Permission WRITE_ACP = new Permission(WRITE_ACP_TYPE, "WRITE_ACP");

    /**
     * The FULL_CONTROL type
     */
    public static final int FULL_CONTROL_TYPE = 4;

    /**
     * The instance of the FULL_CONTROL type
     */
    public static final Permission FULL_CONTROL = new Permission(FULL_CONTROL_TYPE, "FULL_CONTROL");

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

    private Permission(int type, java.lang.String value) 
     {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- org.jets3t.service.soap._2006_03_01.types.Permission(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerate
     * 
     * Returns an enumeration of all possible instances of
     * Permission
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
     * Returns the type of this Permission
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
        members.put("READ", READ);
        members.put("WRITE", WRITE);
        members.put("READ_ACP", READ_ACP);
        members.put("WRITE_ACP", WRITE_ACP);
        members.put("FULL_CONTROL", FULL_CONTROL);
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
     * Returns the String representation of this Permission
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
     * Returns a new Permission based on the given String value.
     * 
     * @param string
     * @return Permission
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.types.Permission valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid Permission";
            throw new IllegalArgumentException(err);
        }
        return (Permission) obj;
    } //-- org.jets3t.service.soap._2006_03_01.types.Permission valueOf(java.lang.String) 

}
