/**
 * AccessControlPolicy.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.jets3t.service.impl.soap.axis._2006_03_01;

public class AccessControlPolicy  implements java.io.Serializable {
    private org.jets3t.service.impl.soap.axis._2006_03_01.CanonicalUser owner;

    private org.jets3t.service.impl.soap.axis._2006_03_01.Grant[] accessControlList;

    public AccessControlPolicy() {
    }

    public AccessControlPolicy(
           org.jets3t.service.impl.soap.axis._2006_03_01.CanonicalUser owner,
           org.jets3t.service.impl.soap.axis._2006_03_01.Grant[] accessControlList) {
           this.owner = owner;
           this.accessControlList = accessControlList;
    }


    /**
     * Gets the owner value for this AccessControlPolicy.
     * 
     * @return owner
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.CanonicalUser getOwner() {
        return owner;
    }


    /**
     * Sets the owner value for this AccessControlPolicy.
     * 
     * @param owner
     */
    public void setOwner(org.jets3t.service.impl.soap.axis._2006_03_01.CanonicalUser owner) {
        this.owner = owner;
    }


    /**
     * Gets the accessControlList value for this AccessControlPolicy.
     * 
     * @return accessControlList
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.Grant[] getAccessControlList() {
        return accessControlList;
    }


    /**
     * Sets the accessControlList value for this AccessControlPolicy.
     * 
     * @param accessControlList
     */
    public void setAccessControlList(org.jets3t.service.impl.soap.axis._2006_03_01.Grant[] accessControlList) {
        this.accessControlList = accessControlList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AccessControlPolicy)) return false;
        AccessControlPolicy other = (AccessControlPolicy) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.owner==null && other.getOwner()==null) || 
             (this.owner!=null &&
              this.owner.equals(other.getOwner()))) &&
            ((this.accessControlList==null && other.getAccessControlList()==null) || 
             (this.accessControlList!=null &&
              java.util.Arrays.equals(this.accessControlList, other.getAccessControlList())));
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
        if (getOwner() != null) {
            _hashCode += getOwner().hashCode();
        }
        if (getAccessControlList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAccessControlList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAccessControlList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AccessControlPolicy.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AccessControlPolicy"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("owner");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Owner"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "CanonicalUser"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accessControlList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AccessControlList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Grant"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Grant"));
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
