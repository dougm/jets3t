/**
 * ListBucketResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.jets3t.service.impl.soap.axis._2006_03_01;

public class ListBucketResult  implements java.io.Serializable {
    private org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry[] metadata;

    private java.lang.String name;

    private java.lang.String prefix;

    private java.lang.String marker;

    private java.lang.String nextMarker;

    private int maxKeys;

    private java.lang.String delimiter;

    private boolean isTruncated;

    private org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry[] contents;

    private org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry[] commonPrefixes;

    public ListBucketResult() {
    }

    public ListBucketResult(
           org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry[] metadata,
           java.lang.String name,
           java.lang.String prefix,
           java.lang.String marker,
           java.lang.String nextMarker,
           int maxKeys,
           java.lang.String delimiter,
           boolean isTruncated,
           org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry[] contents,
           org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry[] commonPrefixes) {
           this.metadata = metadata;
           this.name = name;
           this.prefix = prefix;
           this.marker = marker;
           this.nextMarker = nextMarker;
           this.maxKeys = maxKeys;
           this.delimiter = delimiter;
           this.isTruncated = isTruncated;
           this.contents = contents;
           this.commonPrefixes = commonPrefixes;
    }


    /**
     * Gets the metadata value for this ListBucketResult.
     * 
     * @return metadata
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry[] getMetadata() {
        return metadata;
    }


    /**
     * Sets the metadata value for this ListBucketResult.
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
     * Gets the name value for this ListBucketResult.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this ListBucketResult.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the prefix value for this ListBucketResult.
     * 
     * @return prefix
     */
    public java.lang.String getPrefix() {
        return prefix;
    }


    /**
     * Sets the prefix value for this ListBucketResult.
     * 
     * @param prefix
     */
    public void setPrefix(java.lang.String prefix) {
        this.prefix = prefix;
    }


    /**
     * Gets the marker value for this ListBucketResult.
     * 
     * @return marker
     */
    public java.lang.String getMarker() {
        return marker;
    }


    /**
     * Sets the marker value for this ListBucketResult.
     * 
     * @param marker
     */
    public void setMarker(java.lang.String marker) {
        this.marker = marker;
    }


    /**
     * Gets the nextMarker value for this ListBucketResult.
     * 
     * @return nextMarker
     */
    public java.lang.String getNextMarker() {
        return nextMarker;
    }


    /**
     * Sets the nextMarker value for this ListBucketResult.
     * 
     * @param nextMarker
     */
    public void setNextMarker(java.lang.String nextMarker) {
        this.nextMarker = nextMarker;
    }


    /**
     * Gets the maxKeys value for this ListBucketResult.
     * 
     * @return maxKeys
     */
    public int getMaxKeys() {
        return maxKeys;
    }


    /**
     * Sets the maxKeys value for this ListBucketResult.
     * 
     * @param maxKeys
     */
    public void setMaxKeys(int maxKeys) {
        this.maxKeys = maxKeys;
    }


    /**
     * Gets the delimiter value for this ListBucketResult.
     * 
     * @return delimiter
     */
    public java.lang.String getDelimiter() {
        return delimiter;
    }


    /**
     * Sets the delimiter value for this ListBucketResult.
     * 
     * @param delimiter
     */
    public void setDelimiter(java.lang.String delimiter) {
        this.delimiter = delimiter;
    }


    /**
     * Gets the isTruncated value for this ListBucketResult.
     * 
     * @return isTruncated
     */
    public boolean isIsTruncated() {
        return isTruncated;
    }


    /**
     * Sets the isTruncated value for this ListBucketResult.
     * 
     * @param isTruncated
     */
    public void setIsTruncated(boolean isTruncated) {
        this.isTruncated = isTruncated;
    }


    /**
     * Gets the contents value for this ListBucketResult.
     * 
     * @return contents
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry[] getContents() {
        return contents;
    }


    /**
     * Sets the contents value for this ListBucketResult.
     * 
     * @param contents
     */
    public void setContents(org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry[] contents) {
        this.contents = contents;
    }

    public org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry getContents(int i) {
        return this.contents[i];
    }

    public void setContents(int i, org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry _value) {
        this.contents[i] = _value;
    }


    /**
     * Gets the commonPrefixes value for this ListBucketResult.
     * 
     * @return commonPrefixes
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry[] getCommonPrefixes() {
        return commonPrefixes;
    }


    /**
     * Sets the commonPrefixes value for this ListBucketResult.
     * 
     * @param commonPrefixes
     */
    public void setCommonPrefixes(org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry[] commonPrefixes) {
        this.commonPrefixes = commonPrefixes;
    }

    public org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry getCommonPrefixes(int i) {
        return this.commonPrefixes[i];
    }

    public void setCommonPrefixes(int i, org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry _value) {
        this.commonPrefixes[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ListBucketResult)) return false;
        ListBucketResult other = (ListBucketResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.metadata==null && other.getMetadata()==null) || 
             (this.metadata!=null &&
              java.util.Arrays.equals(this.metadata, other.getMetadata()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.prefix==null && other.getPrefix()==null) || 
             (this.prefix!=null &&
              this.prefix.equals(other.getPrefix()))) &&
            ((this.marker==null && other.getMarker()==null) || 
             (this.marker!=null &&
              this.marker.equals(other.getMarker()))) &&
            ((this.nextMarker==null && other.getNextMarker()==null) || 
             (this.nextMarker!=null &&
              this.nextMarker.equals(other.getNextMarker()))) &&
            this.maxKeys == other.getMaxKeys() &&
            ((this.delimiter==null && other.getDelimiter()==null) || 
             (this.delimiter!=null &&
              this.delimiter.equals(other.getDelimiter()))) &&
            this.isTruncated == other.isIsTruncated() &&
            ((this.contents==null && other.getContents()==null) || 
             (this.contents!=null &&
              java.util.Arrays.equals(this.contents, other.getContents()))) &&
            ((this.commonPrefixes==null && other.getCommonPrefixes()==null) || 
             (this.commonPrefixes!=null &&
              java.util.Arrays.equals(this.commonPrefixes, other.getCommonPrefixes())));
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
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getPrefix() != null) {
            _hashCode += getPrefix().hashCode();
        }
        if (getMarker() != null) {
            _hashCode += getMarker().hashCode();
        }
        if (getNextMarker() != null) {
            _hashCode += getNextMarker().hashCode();
        }
        _hashCode += getMaxKeys();
        if (getDelimiter() != null) {
            _hashCode += getDelimiter().hashCode();
        }
        _hashCode += (isIsTruncated() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getContents() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getContents());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getContents(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCommonPrefixes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCommonPrefixes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCommonPrefixes(), i);
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
        new org.apache.axis.description.TypeDesc(ListBucketResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ListBucketResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("metadata");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Metadata"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "MetadataEntry"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("prefix");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Prefix"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("marker");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Marker"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nextMarker");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "NextMarker"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxKeys");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "MaxKeys"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("delimiter");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Delimiter"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isTruncated");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "IsTruncated"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contents");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Contents"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ListEntry"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("commonPrefixes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "CommonPrefixes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "PrefixEntry"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
