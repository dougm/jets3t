/**
 * ListEntry.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.jets3t.service.impl.soap.axis._2006_03_01;

public class ListEntry  implements java.io.Serializable {
    private java.lang.String key;

    private java.util.Calendar lastModified;

    private java.lang.String ETag;

    private long size;

    private org.jets3t.service.impl.soap.axis._2006_03_01.CanonicalUser owner;

    private org.jets3t.service.impl.soap.axis._2006_03_01.StorageClass storageClass;

    public ListEntry() {
    }

    public ListEntry(
           java.lang.String key,
           java.util.Calendar lastModified,
           java.lang.String ETag,
           long size,
           org.jets3t.service.impl.soap.axis._2006_03_01.CanonicalUser owner,
           org.jets3t.service.impl.soap.axis._2006_03_01.StorageClass storageClass) {
           this.key = key;
           this.lastModified = lastModified;
           this.ETag = ETag;
           this.size = size;
           this.owner = owner;
           this.storageClass = storageClass;
    }


    /**
     * Gets the key value for this ListEntry.
     * 
     * @return key
     */
    public java.lang.String getKey() {
        return key;
    }


    /**
     * Sets the key value for this ListEntry.
     * 
     * @param key
     */
    public void setKey(java.lang.String key) {
        this.key = key;
    }


    /**
     * Gets the lastModified value for this ListEntry.
     * 
     * @return lastModified
     */
    public java.util.Calendar getLastModified() {
        return lastModified;
    }


    /**
     * Sets the lastModified value for this ListEntry.
     * 
     * @param lastModified
     */
    public void setLastModified(java.util.Calendar lastModified) {
        this.lastModified = lastModified;
    }


    /**
     * Gets the ETag value for this ListEntry.
     * 
     * @return ETag
     */
    public java.lang.String getETag() {
        return ETag;
    }


    /**
     * Sets the ETag value for this ListEntry.
     * 
     * @param ETag
     */
    public void setETag(java.lang.String ETag) {
        this.ETag = ETag;
    }


    /**
     * Gets the size value for this ListEntry.
     * 
     * @return size
     */
    public long getSize() {
        return size;
    }


    /**
     * Sets the size value for this ListEntry.
     * 
     * @param size
     */
    public void setSize(long size) {
        this.size = size;
    }


    /**
     * Gets the owner value for this ListEntry.
     * 
     * @return owner
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.CanonicalUser getOwner() {
        return owner;
    }


    /**
     * Sets the owner value for this ListEntry.
     * 
     * @param owner
     */
    public void setOwner(org.jets3t.service.impl.soap.axis._2006_03_01.CanonicalUser owner) {
        this.owner = owner;
    }


    /**
     * Gets the storageClass value for this ListEntry.
     * 
     * @return storageClass
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.StorageClass getStorageClass() {
        return storageClass;
    }


    /**
     * Sets the storageClass value for this ListEntry.
     * 
     * @param storageClass
     */
    public void setStorageClass(org.jets3t.service.impl.soap.axis._2006_03_01.StorageClass storageClass) {
        this.storageClass = storageClass;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ListEntry)) return false;
        ListEntry other = (ListEntry) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.key==null && other.getKey()==null) || 
             (this.key!=null &&
              this.key.equals(other.getKey()))) &&
            ((this.lastModified==null && other.getLastModified()==null) || 
             (this.lastModified!=null &&
              this.lastModified.equals(other.getLastModified()))) &&
            ((this.ETag==null && other.getETag()==null) || 
             (this.ETag!=null &&
              this.ETag.equals(other.getETag()))) &&
            this.size == other.getSize() &&
            ((this.owner==null && other.getOwner()==null) || 
             (this.owner!=null &&
              this.owner.equals(other.getOwner()))) &&
            ((this.storageClass==null && other.getStorageClass()==null) || 
             (this.storageClass!=null &&
              this.storageClass.equals(other.getStorageClass())));
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
        if (getKey() != null) {
            _hashCode += getKey().hashCode();
        }
        if (getLastModified() != null) {
            _hashCode += getLastModified().hashCode();
        }
        if (getETag() != null) {
            _hashCode += getETag().hashCode();
        }
        _hashCode += new Long(getSize()).hashCode();
        if (getOwner() != null) {
            _hashCode += getOwner().hashCode();
        }
        if (getStorageClass() != null) {
            _hashCode += getStorageClass().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ListEntry.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ListEntry"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("key");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Key"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("size");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Size"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("owner");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Owner"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "CanonicalUser"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("storageClass");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "StorageClass"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "StorageClass"));
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
