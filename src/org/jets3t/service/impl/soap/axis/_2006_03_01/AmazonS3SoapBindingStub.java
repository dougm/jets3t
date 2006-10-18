/**
 * AmazonS3SoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.jets3t.service.impl.soap.axis._2006_03_01;

public class AmazonS3SoapBindingStub extends org.apache.axis.client.Stub implements org.jets3t.service.impl.soap.axis._2006_03_01.AmazonS3_PortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[15];
        _initOperationDesc1();
        _initOperationDesc2();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CreateBucket");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Bucket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AccessControlList"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AccessControlList"), org.jets3t.service.impl.soap.axis._2006_03_01.Grant[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Grant"));
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AWSAccessKeyId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Timestamp"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Signature"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "CreateBucketResult"));
        oper.setReturnClass(org.jets3t.service.impl.soap.axis._2006_03_01.CreateBucketResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "CreateBucketReturn"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("DeleteBucket");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Bucket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AWSAccessKeyId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Timestamp"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Signature"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Credential"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Status"));
        oper.setReturnClass(org.jets3t.service.impl.soap.axis._2006_03_01.Status.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "DeleteBucketResponse"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetObjectAccessControlPolicy");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Bucket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Key"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AWSAccessKeyId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Timestamp"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Signature"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Credential"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AccessControlPolicy"));
        oper.setReturnClass(org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlPolicy.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "GetObjectAccessControlPolicyResponse"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetBucketAccessControlPolicy");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Bucket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AWSAccessKeyId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Timestamp"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Signature"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Credential"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AccessControlPolicy"));
        oper.setReturnClass(org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlPolicy.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "GetBucketAccessControlPolicyResponse"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SetObjectAccessControlPolicy");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Bucket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Key"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AccessControlList"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AccessControlList"), org.jets3t.service.impl.soap.axis._2006_03_01.Grant[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Grant"));
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AWSAccessKeyId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Timestamp"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Signature"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Credential"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SetBucketAccessControlPolicy");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Bucket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AccessControlList"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AccessControlList"), org.jets3t.service.impl.soap.axis._2006_03_01.Grant[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Grant"));
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AWSAccessKeyId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Timestamp"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Signature"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Credential"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetObject");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Bucket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Key"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "GetMetadata"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "GetData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "InlineData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AWSAccessKeyId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Timestamp"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Signature"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Credential"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "GetObjectResult"));
        oper.setReturnClass(org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "GetObjectResponse"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetObjectExtended");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Bucket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Key"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "GetMetadata"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "GetData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "InlineData"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ByteRangeStart"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"), java.lang.Long.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ByteRangeEnd"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"), java.lang.Long.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "IfModifiedSince"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "IfUnmodifiedSince"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "IfMatch"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "IfNoneMatch"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ReturnCompleteObjectOnConditionFailure"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), java.lang.Boolean.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AWSAccessKeyId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Timestamp"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Signature"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Credential"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "GetObjectResult"));
        oper.setReturnClass(org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "GetObjectResponse"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("PutObject");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Bucket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Key"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Metadata"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "MetadataEntry"), org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ContentLength"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AccessControlList"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AccessControlList"), org.jets3t.service.impl.soap.axis._2006_03_01.Grant[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Grant"));
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "StorageClass"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "StorageClass"), org.jets3t.service.impl.soap.axis._2006_03_01.StorageClass.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AWSAccessKeyId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Timestamp"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Signature"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Credential"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "PutObjectResult"));
        oper.setReturnClass(org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "PutObjectResponse"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("PutObjectInline");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Bucket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Key"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Metadata"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "MetadataEntry"), org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Data"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"), byte[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ContentLength"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AccessControlList"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AccessControlList"), org.jets3t.service.impl.soap.axis._2006_03_01.Grant[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Grant"));
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "StorageClass"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "StorageClass"), org.jets3t.service.impl.soap.axis._2006_03_01.StorageClass.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AWSAccessKeyId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Timestamp"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Signature"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Credential"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "PutObjectResult"));
        oper.setReturnClass(org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "PutObjectInlineResponse"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("DeleteObject");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Bucket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Key"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AWSAccessKeyId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Timestamp"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Signature"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Credential"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Status"));
        oper.setReturnClass(org.jets3t.service.impl.soap.axis._2006_03_01.Status.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "DeleteObjectResponse"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ListBucket");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Bucket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Prefix"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Marker"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "MaxKeys"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), java.lang.Integer.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Delimiter"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AWSAccessKeyId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Timestamp"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Signature"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Credential"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ListBucketResult"));
        oper.setReturnClass(org.jets3t.service.impl.soap.axis._2006_03_01.ListBucketResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ListBucketResponse"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ListAllMyBuckets");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AWSAccessKeyId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Timestamp"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Signature"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ListAllMyBucketsResult"));
        oper.setReturnClass(org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ListAllMyBucketsResponse"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SetBucketLoggingStatus");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Bucket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AWSAccessKeyId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Timestamp"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Signature"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Credential"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "BucketLoggingStatus"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "BucketLoggingStatus"), org.jets3t.service.impl.soap.axis._2006_03_01.BucketLoggingStatus.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetBucketLoggingStatus");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Bucket"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AWSAccessKeyId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Timestamp"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Signature"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Credential"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "BucketLoggingStatus"));
        oper.setReturnClass(org.jets3t.service.impl.soap.axis._2006_03_01.BucketLoggingStatus.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "GetBucketLoggingStatusResponse"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[14] = oper;

    }

    public AmazonS3SoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public AmazonS3SoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public AmazonS3SoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AccessControlList");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.Grant[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Grant");
            qName2 = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Grant");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AccessControlPolicy");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlPolicy.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "AmazonCustomerByEmail");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.AmazonCustomerByEmail.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "BucketLoggingStatus");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.BucketLoggingStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "CanonicalUser");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.CanonicalUser.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "CreateBucketResult");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.CreateBucketResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "GetObjectResult");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Grant");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.Grant.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Grantee");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.Grantee.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Group");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.Group.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ListAllMyBucketsEntry");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsEntry.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ListAllMyBucketsList");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsEntry[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ListAllMyBucketsEntry");
            qName2 = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Bucket");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ListAllMyBucketsResult");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ListBucketResult");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.ListBucketResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ListEntry");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "LoggingSettings");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.LoggingSettings.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "MetadataEntry");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Permission");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.Permission.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "PrefixEntry");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "PutObjectResult");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Result");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.Result.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "Status");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.Status.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "StorageClass");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.StorageClass.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "User");
            cachedSerQNames.add(qName);
            cls = org.jets3t.service.impl.soap.axis._2006_03_01.User.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public org.jets3t.service.impl.soap.axis._2006_03_01.CreateBucketResult createBucket(java.lang.String bucket, org.jets3t.service.impl.soap.axis._2006_03_01.Grant[] accessControlList, java.lang.String AWSAccessKeyId, java.util.Calendar timestamp, java.lang.String signature) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "CreateBucket"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {bucket, accessControlList, AWSAccessKeyId, timestamp, signature});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.CreateBucketResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.CreateBucketResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.jets3t.service.impl.soap.axis._2006_03_01.CreateBucketResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.jets3t.service.impl.soap.axis._2006_03_01.Status deleteBucket(java.lang.String bucket, java.lang.String AWSAccessKeyId, java.util.Calendar timestamp, java.lang.String signature, java.lang.String credential) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "DeleteBucket"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {bucket, AWSAccessKeyId, timestamp, signature, credential});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.Status) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.Status) org.apache.axis.utils.JavaUtils.convert(_resp, org.jets3t.service.impl.soap.axis._2006_03_01.Status.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlPolicy getObjectAccessControlPolicy(java.lang.String bucket, java.lang.String key, java.lang.String AWSAccessKeyId, java.util.Calendar timestamp, java.lang.String signature, java.lang.String credential) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "GetObjectAccessControlPolicy"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {bucket, key, AWSAccessKeyId, timestamp, signature, credential});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlPolicy) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlPolicy) org.apache.axis.utils.JavaUtils.convert(_resp, org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlPolicy.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlPolicy getBucketAccessControlPolicy(java.lang.String bucket, java.lang.String AWSAccessKeyId, java.util.Calendar timestamp, java.lang.String signature, java.lang.String credential) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "GetBucketAccessControlPolicy"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {bucket, AWSAccessKeyId, timestamp, signature, credential});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlPolicy) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlPolicy) org.apache.axis.utils.JavaUtils.convert(_resp, org.jets3t.service.impl.soap.axis._2006_03_01.AccessControlPolicy.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void setObjectAccessControlPolicy(java.lang.String bucket, java.lang.String key, org.jets3t.service.impl.soap.axis._2006_03_01.Grant[] accessControlList, java.lang.String AWSAccessKeyId, java.util.Calendar timestamp, java.lang.String signature, java.lang.String credential) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "SetObjectAccessControlPolicy"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {bucket, key, accessControlList, AWSAccessKeyId, timestamp, signature, credential});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void setBucketAccessControlPolicy(java.lang.String bucket, org.jets3t.service.impl.soap.axis._2006_03_01.Grant[] accessControlList, java.lang.String AWSAccessKeyId, java.util.Calendar timestamp, java.lang.String signature, java.lang.String credential) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "SetBucketAccessControlPolicy"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {bucket, accessControlList, AWSAccessKeyId, timestamp, signature, credential});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectResult getObject(java.lang.String bucket, java.lang.String key, boolean getMetadata, boolean getData, boolean inlineData, java.lang.String AWSAccessKeyId, java.util.Calendar timestamp, java.lang.String signature, java.lang.String credential) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "GetObject"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {bucket, key, new java.lang.Boolean(getMetadata), new java.lang.Boolean(getData), new java.lang.Boolean(inlineData), AWSAccessKeyId, timestamp, signature, credential});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectResult getObjectExtended(java.lang.String bucket, java.lang.String key, boolean getMetadata, boolean getData, boolean inlineData, java.lang.Long byteRangeStart, java.lang.Long byteRangeEnd, java.util.Calendar ifModifiedSince, java.util.Calendar ifUnmodifiedSince, java.lang.String[] ifMatch, java.lang.String[] ifNoneMatch, java.lang.Boolean returnCompleteObjectOnConditionFailure, java.lang.String AWSAccessKeyId, java.util.Calendar timestamp, java.lang.String signature, java.lang.String credential) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "GetObjectExtended"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {bucket, key, new java.lang.Boolean(getMetadata), new java.lang.Boolean(getData), new java.lang.Boolean(inlineData), byteRangeStart, byteRangeEnd, ifModifiedSince, ifUnmodifiedSince, ifMatch, ifNoneMatch, returnCompleteObjectOnConditionFailure, AWSAccessKeyId, timestamp, signature, credential});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.jets3t.service.impl.soap.axis._2006_03_01.GetObjectResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectResult putObject(java.lang.String bucket, java.lang.String key, org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry[] metadata, long contentLength, org.jets3t.service.impl.soap.axis._2006_03_01.Grant[] accessControlList, org.jets3t.service.impl.soap.axis._2006_03_01.StorageClass storageClass, java.lang.String AWSAccessKeyId, java.util.Calendar timestamp, java.lang.String signature, java.lang.String credential) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "PutObject"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {bucket, key, metadata, new java.lang.Long(contentLength), accessControlList, storageClass, AWSAccessKeyId, timestamp, signature, credential});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectResult putObjectInline(java.lang.String bucket, java.lang.String key, org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry[] metadata, byte[] data, long contentLength, org.jets3t.service.impl.soap.axis._2006_03_01.Grant[] accessControlList, org.jets3t.service.impl.soap.axis._2006_03_01.StorageClass storageClass, java.lang.String AWSAccessKeyId, java.util.Calendar timestamp, java.lang.String signature, java.lang.String credential) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "PutObjectInline"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {bucket, key, metadata, data, new java.lang.Long(contentLength), accessControlList, storageClass, AWSAccessKeyId, timestamp, signature, credential});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.jets3t.service.impl.soap.axis._2006_03_01.PutObjectResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.jets3t.service.impl.soap.axis._2006_03_01.Status deleteObject(java.lang.String bucket, java.lang.String key, java.lang.String AWSAccessKeyId, java.util.Calendar timestamp, java.lang.String signature, java.lang.String credential) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "DeleteObject"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {bucket, key, AWSAccessKeyId, timestamp, signature, credential});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.Status) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.Status) org.apache.axis.utils.JavaUtils.convert(_resp, org.jets3t.service.impl.soap.axis._2006_03_01.Status.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.jets3t.service.impl.soap.axis._2006_03_01.ListBucketResult listBucket(java.lang.String bucket, java.lang.String prefix, java.lang.String marker, java.lang.Integer maxKeys, java.lang.String delimiter, java.lang.String AWSAccessKeyId, java.util.Calendar timestamp, java.lang.String signature, java.lang.String credential) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ListBucket"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {bucket, prefix, marker, maxKeys, delimiter, AWSAccessKeyId, timestamp, signature, credential});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.ListBucketResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.ListBucketResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.jets3t.service.impl.soap.axis._2006_03_01.ListBucketResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsResult listAllMyBuckets(java.lang.String AWSAccessKeyId, java.util.Calendar timestamp, java.lang.String signature) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "ListAllMyBuckets"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {AWSAccessKeyId, timestamp, signature});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsResult) org.apache.axis.utils.JavaUtils.convert(_resp, org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void setBucketLoggingStatus(java.lang.String bucket, java.lang.String AWSAccessKeyId, java.util.Calendar timestamp, java.lang.String signature, java.lang.String credential, org.jets3t.service.impl.soap.axis._2006_03_01.BucketLoggingStatus bucketLoggingStatus) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "SetBucketLoggingStatus"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {bucket, AWSAccessKeyId, timestamp, signature, credential, bucketLoggingStatus});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.jets3t.service.impl.soap.axis._2006_03_01.BucketLoggingStatus getBucketLoggingStatus(java.lang.String bucket, java.lang.String AWSAccessKeyId, java.util.Calendar timestamp, java.lang.String signature, java.lang.String credential) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://s3.amazonaws.com/doc/2006-03-01/", "GetBucketLoggingStatus"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {bucket, AWSAccessKeyId, timestamp, signature, credential});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.BucketLoggingStatus) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.jets3t.service.impl.soap.axis._2006_03_01.BucketLoggingStatus) org.apache.axis.utils.JavaUtils.convert(_resp, org.jets3t.service.impl.soap.axis._2006_03_01.BucketLoggingStatus.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
