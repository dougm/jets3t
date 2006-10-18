/**
 * GetObjectResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.jets3t.service.impl.soap.axis._2006_03_01;

public class GetObjectResult  extends org.jets3t.service.impl.soap.axis._2006_03_01.Result  implements java.io.Serializable {
    private org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry[] metadata;

    private byte[] data;

    private java.util.Calendar lastModified;

    private java.lang.String ETag;

    public GetObjectResult() {
    }

    public GetObjectResult(
           org.jets3t.service.impl.soap.axis._2006_03_01.Status status,
           org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry[] metadata,
           byte[] data,
           java.util.Calendar lastModified,
           java.lang.String ETag) {
        super(
            status);
        this.metadata = metadata;
        this.data = data;
        this.lastModified = lastModified;
        this.ETag = ETag;
    }


    /**
     * Gets the metadata value for this GetObjectResult.
     * 
     * @return metadata
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry[] getMetadata() {
        return metadata;
    }


    /**
     * Sets the metadata value for this GetObjectResult.
     * 
     * @param metadata
     */
    public void setMetadata(org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry[] metadata) {
        this.metadata = metadata;
    }

    public org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry getMetadata(int i) {
        return this.metadata[i];
    }

    public void setMetadata(int i, org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry _value) {
        this.metadata[i] = _value;
    }


    /**
     * Gets the data value for this GetObjectResult.
     * 
     * @return data
     */
    public byte[] getData() {
        return data;
    }


    /**
     * Sets the data value for this GetObjectResult.
     * 
     * @param data
     */
    public void setData(byte[] data) {
        this.data = data;
    }


    /**
     * Gets the lastModified value for this GetObjectResult.
     * 
     * @return lastModified
     */
    public java.util.Calendar getLastModified() {
        return lastModified;
    }


    /**
     * Sets the lastModified value for this GetObjectResult.
     * 
     * @param lastModified
     */
    public void setLastModified(java.util.Calendar lastModified) {
        this.lastModified = lastModified;
    }


    /**
     * Gets the ETag value for this GetObjectResult.
     * 
     * @return ETag
     */
    public java.lang.String getETag() {
        return ETag;
    }


    /**
     * Sets the ETag value for this GetObjectResult.
     * 
     * @param ETag
     */
    public void setETag(java.lang.String ETag) {
        this.ETag = ETag;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetObjectResult)) return false;
        GetObjectResult other = (GetObjectResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.metadata==null && other.getMetadata()==null) || 
             (this.metadata!=null &&
              java.util.Arrays.equals(this.metadata, other.getMetadata()))) &&
            ((this.data==null && other.getData()==null) || 
             (this.data!=null &&
              java.util.Arrays.equals(this.data, other.getData()))) &&
            ((this.lastModified==null && other.getLastModified()==null) || 
             (this.lastModified!=null &&
              this.lastModified.equals(other.getLastModified()))) &&
            ((this.ETag==null && other.getETag()==null) || 
             (this.ETag!=null &&
              this.ETag.equals(other.getETag())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getMetadata() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMetadata());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMetadata(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getData() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getData());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getData(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getLastModified() != null) {
            _hashCode += getLastModified().hashCode();
        }
        if (getETag() != null) {
            _hashCode += getETag().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetObjectResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "GetObjectResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("metadata");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Metadata"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "MetadataEntry"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("data");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Data"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastModified");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "LastModified"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ETag");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ETag"));
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
