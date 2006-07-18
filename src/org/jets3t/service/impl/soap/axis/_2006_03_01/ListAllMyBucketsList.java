/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: ListAllMyBucketsList.java,v 1.1 2006/07/18 05:15:21 jmurty Exp $
 */

package org.jets3t.service.impl.soap.axis._2006_03_01;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class ListAllMyBucketsList.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:21 $
 */
public class ListAllMyBucketsList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _bucketList
     */
    private java.util.ArrayList _bucketList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ListAllMyBucketsList() 
     {
        super();
        _bucketList = new ArrayList();
    } //-- org.jets3t.service.soap._2006_03_01.ListAllMyBucketsList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addBucket
     * 
     * 
     * 
     * @param vBucket
     */
    public void addBucket(org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsEntry vBucket)
        throws java.lang.IndexOutOfBoundsException
    {
        _bucketList.add(vBucket);
    } //-- void addBucket(org.jets3t.service.soap._2006_03_01.ListAllMyBucketsEntry) 

    /**
     * Method addBucket
     * 
     * 
     * 
     * @param index
     * @param vBucket
     */
    public void addBucket(int index, org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsEntry vBucket)
        throws java.lang.IndexOutOfBoundsException
    {
        _bucketList.add(index, vBucket);
    } //-- void addBucket(int, org.jets3t.service.soap._2006_03_01.ListAllMyBucketsEntry) 

    /**
     * Method clearBucket
     * 
     */
    public void clearBucket()
    {
        _bucketList.clear();
    } //-- void clearBucket() 

    /**
     * Method enumerateBucket
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateBucket()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_bucketList.iterator());
    } //-- java.util.Enumeration enumerateBucket() 

    /**
     * Method getBucket
     * 
     * 
     * 
     * @param index
     * @return ListAllMyBucketsEntry
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsEntry getBucket(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _bucketList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsEntry) _bucketList.get(index);
    } //-- org.jets3t.service.soap._2006_03_01.ListAllMyBucketsEntry getBucket(int) 

    /**
     * Method getBucket
     * 
     * 
     * 
     * @return ListAllMyBucketsEntry
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsEntry[] getBucket()
    {
        int size = _bucketList.size();
        org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsEntry[] mArray = new org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsEntry[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsEntry) _bucketList.get(index);
        }
        return mArray;
    } //-- org.jets3t.service.soap._2006_03_01.ListAllMyBucketsEntry[] getBucket() 

    /**
     * Method getBucketCount
     * 
     * 
     * 
     * @return int
     */
    public int getBucketCount()
    {
        return _bucketList.size();
    } //-- int getBucketCount() 

    /**
     * Method isValid
     * 
     * 
     * 
     * @return boolean
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Method removeBucket
     * 
     * 
     * 
     * @param vBucket
     * @return boolean
     */
    public boolean removeBucket(org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsEntry vBucket)
    {
        boolean removed = _bucketList.remove(vBucket);
        return removed;
    } //-- boolean removeBucket(org.jets3t.service.soap._2006_03_01.ListAllMyBucketsEntry) 

    /**
     * Method setBucket
     * 
     * 
     * 
     * @param index
     * @param vBucket
     */
    public void setBucket(int index, org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsEntry vBucket)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _bucketList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _bucketList.set(index, vBucket);
    } //-- void setBucket(int, org.jets3t.service.soap._2006_03_01.ListAllMyBucketsEntry) 

    /**
     * Method setBucket
     * 
     * 
     * 
     * @param bucketArray
     */
    public void setBucket(org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsEntry[] bucketArray)
    {
        //-- copy array
        _bucketList.clear();
        for (int i = 0; i < bucketArray.length; i++) {
            _bucketList.add(bucketArray[i]);
        }
    } //-- void setBucket(org.jets3t.service.soap._2006_03_01.ListAllMyBucketsEntry) 

    /**
     * Method unmarshalListAllMyBucketsList
     * 
     * 
     * 
     * @param reader
     * @return ListAllMyBucketsList
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsList unmarshalListAllMyBucketsList(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsList) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.ListAllMyBucketsList.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.ListAllMyBucketsList unmarshalListAllMyBucketsList(java.io.Reader) 

    /**
     * Method validate
     * 
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
