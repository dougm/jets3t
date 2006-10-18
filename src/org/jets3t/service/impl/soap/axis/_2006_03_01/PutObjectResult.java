/**
 * PutObjectResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.jets3t.service.impl.soap.axis._2006_03_01;

public class PutObjectResult  implements java.io.Serializable {
    private java.lang.String ETag;

    private java.util.Calendar lastModified;

    public PutObjectResult() {
    }

    public PutObjectResult(
           java.lang.String ETag,
           java.util.Calendar lastModified) {
           this.ETag = ETag;
           this.lastModified = lastModified;
    }


    /**
     * Gets the ETag value for this PutObjectResult.
     * 
     * @return ETag
     */
    public java.lang.String getETag() {
        return ETag;
    }


    /**
     * Sets the ETag value for this PutObjectResult.
     * 
     * @param ETag
     */
    public void setETag(java.lang.String ETag) {
        this.ETag = ETag;
    }


    /**
     * Gets the lastModified value for this PutObjectResult.
     * 
     * @return lastModified
     */
    public java.util.Calendar getLastModified() {
        return lastModified;
    }


    /**
     * Sets the lastModified value for this PutObjectResult.
     * 
     * @param lastModified
     */
    public void setLastModified(java.util.Calendar lastModified) {
        this.lastModified = lastModified;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PutObjectResult)) return false;
        PutObjectResult other = (PutObjectResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.ETag==null && other.getETag()==null) || 
             (this.ETag!=null &&
              this.ETag.equals(other.getETag()))) &&
            ((this.lastModified==null && other.getLastModified()==null) || 
             (this.lastModified!=null &&
              this.lastModified.equals(other.getLastModified())));
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
        if (getETag() != null) {
            _hashCode += getETag().hashCode();
        }
        if (getLastModified() != null) {
            _hashCode += getLastModified().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PutObjectResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "PutObjectResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ETag");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ETag"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastModified");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "LastModified"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
