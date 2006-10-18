/**
 * LoggingSettings.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.jets3t.service.impl.soap.axis._2006_03_01;

public class LoggingSettings  implements java.io.Serializable {
    private java.lang.String targetBucket;

    private java.lang.String targetPrefix;

    public LoggingSettings() {
    }

    public LoggingSettings(
           java.lang.String targetBucket,
           java.lang.String targetPrefix) {
           this.targetBucket = targetBucket;
           this.targetPrefix = targetPrefix;
    }


    /**
     * Gets the targetBucket value for this LoggingSettings.
     * 
     * @return targetBucket
     */
    public java.lang.String getTargetBucket() {
        return targetBucket;
    }


    /**
     * Sets the targetBucket value for this LoggingSettings.
     * 
     * @param targetBucket
     */
    public void setTargetBucket(java.lang.String targetBucket) {
        this.targetBucket = targetBucket;
    }


    /**
     * Gets the targetPrefix value for this LoggingSettings.
     * 
     * @return targetPrefix
     */
    public java.lang.String getTargetPrefix() {
        return targetPrefix;
    }


    /**
     * Sets the targetPrefix value for this LoggingSettings.
     * 
     * @param targetPrefix
     */
    public void setTargetPrefix(java.lang.String targetPrefix) {
        this.targetPrefix = targetPrefix;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LoggingSettings)) return false;
        LoggingSettings other = (LoggingSettings) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.targetBucket==null && other.getTargetBucket()==null) || 
             (this.targetBucket!=null &&
              this.targetBucket.equals(other.getTargetBucket()))) &&
            ((this.targetPrefix==null && other.getTargetPrefix()==null) || 
             (this.targetPrefix!=null &&
              this.targetPrefix.equals(other.getTargetPrefix())));
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
        if (getTargetBucket() != null) {
            _hashCode += getTargetBucket().hashCode();
        }
        if (getTargetPrefix() != null) {
            _hashCode += getTargetPrefix().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(LoggingSettings.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "LoggingSettings"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("targetBucket");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "TargetBucket"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("targetPrefix");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "TargetPrefix"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
