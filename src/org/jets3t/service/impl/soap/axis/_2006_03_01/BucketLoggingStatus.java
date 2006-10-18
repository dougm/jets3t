/**
 * BucketLoggingStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.jets3t.service.impl.soap.axis._2006_03_01;

public class BucketLoggingStatus  implements java.io.Serializable {
    private org.jets3t.service.impl.soap.axis._2006_03_01.LoggingSettings loggingEnabled;

    public BucketLoggingStatus() {
    }

    public BucketLoggingStatus(
           org.jets3t.service.impl.soap.axis._2006_03_01.LoggingSettings loggingEnabled) {
           this.loggingEnabled = loggingEnabled;
    }


    /**
     * Gets the loggingEnabled value for this BucketLoggingStatus.
     * 
     * @return loggingEnabled
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.LoggingSettings getLoggingEnabled() {
        return loggingEnabled;
    }


    /**
     * Sets the loggingEnabled value for this BucketLoggingStatus.
     * 
     * @param loggingEnabled
     */
    public void setLoggingEnabled(org.jets3t.service.impl.soap.axis._2006_03_01.LoggingSettings loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BucketLoggingStatus)) return false;
        BucketLoggingStatus other = (BucketLoggingStatus) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.loggingEnabled==null && other.getLoggingEnabled()==null) || 
             (this.loggingEnabled!=null &&
              this.loggingEnabled.equals(other.getLoggingEnabled())));
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
        if (getLoggingEnabled() != null) {
            _hashCode += getLoggingEnabled().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BucketLoggingStatus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "BucketLoggingStatus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loggingEnabled");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "LoggingEnabled"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "LoggingSettings"));
        elemField.setMinOccurs(0);
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
