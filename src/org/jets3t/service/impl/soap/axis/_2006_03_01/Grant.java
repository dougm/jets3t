/**
 * Grant.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.jets3t.service.impl.soap.axis._2006_03_01;

public class Grant  implements java.io.Serializable {
    private org.jets3t.service.impl.soap.axis._2006_03_01.Grantee grantee;

    private org.jets3t.service.impl.soap.axis._2006_03_01.Permission permission;

    public Grant() {
    }

    public Grant(
           org.jets3t.service.impl.soap.axis._2006_03_01.Grantee grantee,
           org.jets3t.service.impl.soap.axis._2006_03_01.Permission permission) {
           this.grantee = grantee;
           this.permission = permission;
    }


    /**
     * Gets the grantee value for this Grant.
     * 
     * @return grantee
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.Grantee getGrantee() {
        return grantee;
    }


    /**
     * Sets the grantee value for this Grant.
     * 
     * @param grantee
     */
    public void setGrantee(org.jets3t.service.impl.soap.axis._2006_03_01.Grantee grantee) {
        this.grantee = grantee;
    }


    /**
     * Gets the permission value for this Grant.
     * 
     * @return permission
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.Permission getPermission() {
        return permission;
    }


    /**
     * Sets the permission value for this Grant.
     * 
     * @param permission
     */
    public void setPermission(org.jets3t.service.impl.soap.axis._2006_03_01.Permission permission) {
        this.permission = permission;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Grant)) return false;
        Grant other = (Grant) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.grantee==null && other.getGrantee()==null) || 
             (this.grantee!=null &&
              this.grantee.equals(other.getGrantee()))) &&
            ((this.permission==null && other.getPermission()==null) || 
             (this.permission!=null &&
              this.permission.equals(other.getPermission())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getGrantee() != null) {
            _hashCode += getGrantee().hashCode();
        }
        if (getPermission() != null) {
            _hashCode += getPermission().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Grant.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Grant"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("grantee");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Grantee"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Grantee"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("permission");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Permission"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Permission"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
