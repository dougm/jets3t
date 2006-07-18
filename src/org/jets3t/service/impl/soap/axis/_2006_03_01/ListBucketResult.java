/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.9.1</a>, using an XML
 * Schema.
 * $Id: ListBucketResult.java,v 1.1 2006/07/18 05:15:37 jmurty Exp $
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
 * Class ListBucketResult.
 * 
 * @version $Revision: 1.1 $ $Date: 2006/07/18 05:15:37 $
 */
public class ListBucketResult implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _metadataList
     */
    private java.util.ArrayList _metadataList;

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _prefix
     */
    private java.lang.String _prefix;

    /**
     * Field _marker
     */
    private java.lang.String _marker;

    /**
     * Field _nextMarker
     */
    private java.lang.String _nextMarker;

    /**
     * Field _maxKeys
     */
    private int _maxKeys;

    /**
     * keeps track of state for field: _maxKeys
     */
    private boolean _has_maxKeys;

    /**
     * Field _delimiter
     */
    private java.lang.String _delimiter;

    /**
     * Field _isTruncated
     */
    private boolean _isTruncated;

    /**
     * keeps track of state for field: _isTruncated
     */
    private boolean _has_isTruncated;

    /**
     * Field _contentsList
     */
    private java.util.ArrayList _contentsList;

    /**
     * Field _commonPrefixesList
     */
    private java.util.ArrayList _commonPrefixesList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ListBucketResult() 
     {
        super();
        _metadataList = new ArrayList();
        _contentsList = new ArrayList();
        _commonPrefixesList = new ArrayList();
    } //-- org.jets3t.service.soap._2006_03_01.ListBucketResult()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addCommonPrefixes
     * 
     * 
     * 
     * @param vCommonPrefixes
     */
    public void addCommonPrefixes(org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry vCommonPrefixes)
        throws java.lang.IndexOutOfBoundsException
    {
        _commonPrefixesList.add(vCommonPrefixes);
    } //-- void addCommonPrefixes(org.jets3t.service.soap._2006_03_01.PrefixEntry) 

    /**
     * Method addCommonPrefixes
     * 
     * 
     * 
     * @param index
     * @param vCommonPrefixes
     */
    public void addCommonPrefixes(int index, org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry vCommonPrefixes)
        throws java.lang.IndexOutOfBoundsException
    {
        _commonPrefixesList.add(index, vCommonPrefixes);
    } //-- void addCommonPrefixes(int, org.jets3t.service.soap._2006_03_01.PrefixEntry) 

    /**
     * Method addContents
     * 
     * 
     * 
     * @param vContents
     */
    public void addContents(org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry vContents)
        throws java.lang.IndexOutOfBoundsException
    {
        _contentsList.add(vContents);
    } //-- void addContents(org.jets3t.service.soap._2006_03_01.ListEntry) 

    /**
     * Method addContents
     * 
     * 
     * 
     * @param index
     * @param vContents
     */
    public void addContents(int index, org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry vContents)
        throws java.lang.IndexOutOfBoundsException
    {
        _contentsList.add(index, vContents);
    } //-- void addContents(int, org.jets3t.service.soap._2006_03_01.ListEntry) 

    /**
     * Method addMetadata
     * 
     * 
     * 
     * @param vMetadata
     */
    public void addMetadata(org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry vMetadata)
        throws java.lang.IndexOutOfBoundsException
    {
        _metadataList.add(vMetadata);
    } //-- void addMetadata(org.jets3t.service.soap._2006_03_01.MetadataEntry) 

    /**
     * Method addMetadata
     * 
     * 
     * 
     * @param index
     * @param vMetadata
     */
    public void addMetadata(int index, org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry vMetadata)
        throws java.lang.IndexOutOfBoundsException
    {
        _metadataList.add(index, vMetadata);
    } //-- void addMetadata(int, org.jets3t.service.soap._2006_03_01.MetadataEntry) 

    /**
     * Method clearCommonPrefixes
     * 
     */
    public void clearCommonPrefixes()
    {
        _commonPrefixesList.clear();
    } //-- void clearCommonPrefixes() 

    /**
     * Method clearContents
     * 
     */
    public void clearContents()
    {
        _contentsList.clear();
    } //-- void clearContents() 

    /**
     * Method clearMetadata
     * 
     */
    public void clearMetadata()
    {
        _metadataList.clear();
    } //-- void clearMetadata() 

    /**
     * Method deleteIsTruncated
     * 
     */
    public void deleteIsTruncated()
    {
        this._has_isTruncated= false;
    } //-- void deleteIsTruncated() 

    /**
     * Method deleteMaxKeys
     * 
     */
    public void deleteMaxKeys()
    {
        this._has_maxKeys= false;
    } //-- void deleteMaxKeys() 

    /**
     * Method enumerateCommonPrefixes
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateCommonPrefixes()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_commonPrefixesList.iterator());
    } //-- java.util.Enumeration enumerateCommonPrefixes() 

    /**
     * Method enumerateContents
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateContents()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_contentsList.iterator());
    } //-- java.util.Enumeration enumerateContents() 

    /**
     * Method enumerateMetadata
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateMetadata()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_metadataList.iterator());
    } //-- java.util.Enumeration enumerateMetadata() 

    /**
     * Method getCommonPrefixes
     * 
     * 
     * 
     * @param index
     * @return PrefixEntry
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry getCommonPrefixes(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _commonPrefixesList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry) _commonPrefixesList.get(index);
    } //-- org.jets3t.service.soap._2006_03_01.PrefixEntry getCommonPrefixes(int) 

    /**
     * Method getCommonPrefixes
     * 
     * 
     * 
     * @return PrefixEntry
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry[] getCommonPrefixes()
    {
        int size = _commonPrefixesList.size();
        org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry[] mArray = new org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry) _commonPrefixesList.get(index);
        }
        return mArray;
    } //-- org.jets3t.service.soap._2006_03_01.PrefixEntry[] getCommonPrefixes() 

    /**
     * Method getCommonPrefixesCount
     * 
     * 
     * 
     * @return int
     */
    public int getCommonPrefixesCount()
    {
        return _commonPrefixesList.size();
    } //-- int getCommonPrefixesCount() 

    /**
     * Method getContents
     * 
     * 
     * 
     * @param index
     * @return ListEntry
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry getContents(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _contentsList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry) _contentsList.get(index);
    } //-- org.jets3t.service.soap._2006_03_01.ListEntry getContents(int) 

    /**
     * Method getContents
     * 
     * 
     * 
     * @return ListEntry
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry[] getContents()
    {
        int size = _contentsList.size();
        org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry[] mArray = new org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry) _contentsList.get(index);
        }
        return mArray;
    } //-- org.jets3t.service.soap._2006_03_01.ListEntry[] getContents() 

    /**
     * Method getContentsCount
     * 
     * 
     * 
     * @return int
     */
    public int getContentsCount()
    {
        return _contentsList.size();
    } //-- int getContentsCount() 

    /**
     * Returns the value of field 'delimiter'.
     * 
     * @return String
     * @return the value of field 'delimiter'.
     */
    public java.lang.String getDelimiter()
    {
        return this._delimiter;
    } //-- java.lang.String getDelimiter() 

    /**
     * Returns the value of field 'isTruncated'.
     * 
     * @return boolean
     * @return the value of field 'isTruncated'.
     */
    public boolean getIsTruncated()
    {
        return this._isTruncated;
    } //-- boolean getIsTruncated() 

    /**
     * Returns the value of field 'marker'.
     * 
     * @return String
     * @return the value of field 'marker'.
     */
    public java.lang.String getMarker()
    {
        return this._marker;
    } //-- java.lang.String getMarker() 

    /**
     * Returns the value of field 'maxKeys'.
     * 
     * @return int
     * @return the value of field 'maxKeys'.
     */
    public int getMaxKeys()
    {
        return this._maxKeys;
    } //-- int getMaxKeys() 

    /**
     * Method getMetadata
     * 
     * 
     * 
     * @param index
     * @return MetadataEntry
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry getMetadata(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _metadataList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry) _metadataList.get(index);
    } //-- org.jets3t.service.soap._2006_03_01.MetadataEntry getMetadata(int) 

    /**
     * Method getMetadata
     * 
     * 
     * 
     * @return MetadataEntry
     */
    public org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry[] getMetadata()
    {
        int size = _metadataList.size();
        org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry[] mArray = new org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry) _metadataList.get(index);
        }
        return mArray;
    } //-- org.jets3t.service.soap._2006_03_01.MetadataEntry[] getMetadata() 

    /**
     * Method getMetadataCount
     * 
     * 
     * 
     * @return int
     */
    public int getMetadataCount()
    {
        return _metadataList.size();
    } //-- int getMetadataCount() 

    /**
     * Returns the value of field 'name'.
     * 
     * @return String
     * @return the value of field 'name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

    /**
     * Returns the value of field 'nextMarker'.
     * 
     * @return String
     * @return the value of field 'nextMarker'.
     */
    public java.lang.String getNextMarker()
    {
        return this._nextMarker;
    } //-- java.lang.String getNextMarker() 

    /**
     * Returns the value of field 'prefix'.
     * 
     * @return String
     * @return the value of field 'prefix'.
     */
    public java.lang.String getPrefix()
    {
        return this._prefix;
    } //-- java.lang.String getPrefix() 

    /**
     * Method hasIsTruncated
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasIsTruncated()
    {
        return this._has_isTruncated;
    } //-- boolean hasIsTruncated() 

    /**
     * Method hasMaxKeys
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasMaxKeys()
    {
        return this._has_maxKeys;
    } //-- boolean hasMaxKeys() 

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
     * Method removeCommonPrefixes
     * 
     * 
     * 
     * @param vCommonPrefixes
     * @return boolean
     */
    public boolean removeCommonPrefixes(org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry vCommonPrefixes)
    {
        boolean removed = _commonPrefixesList.remove(vCommonPrefixes);
        return removed;
    } //-- boolean removeCommonPrefixes(org.jets3t.service.soap._2006_03_01.PrefixEntry) 

    /**
     * Method removeContents
     * 
     * 
     * 
     * @param vContents
     * @return boolean
     */
    public boolean removeContents(org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry vContents)
    {
        boolean removed = _contentsList.remove(vContents);
        return removed;
    } //-- boolean removeContents(org.jets3t.service.soap._2006_03_01.ListEntry) 

    /**
     * Method removeMetadata
     * 
     * 
     * 
     * @param vMetadata
     * @return boolean
     */
    public boolean removeMetadata(org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry vMetadata)
    {
        boolean removed = _metadataList.remove(vMetadata);
        return removed;
    } //-- boolean removeMetadata(org.jets3t.service.soap._2006_03_01.MetadataEntry) 

    /**
     * Method setCommonPrefixes
     * 
     * 
     * 
     * @param index
     * @param vCommonPrefixes
     */
    public void setCommonPrefixes(int index, org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry vCommonPrefixes)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _commonPrefixesList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _commonPrefixesList.set(index, vCommonPrefixes);
    } //-- void setCommonPrefixes(int, org.jets3t.service.soap._2006_03_01.PrefixEntry) 

    /**
     * Method setCommonPrefixes
     * 
     * 
     * 
     * @param commonPrefixesArray
     */
    public void setCommonPrefixes(org.jets3t.service.impl.soap.axis._2006_03_01.PrefixEntry[] commonPrefixesArray)
    {
        //-- copy array
        _commonPrefixesList.clear();
        for (int i = 0; i < commonPrefixesArray.length; i++) {
            _commonPrefixesList.add(commonPrefixesArray[i]);
        }
    } //-- void setCommonPrefixes(org.jets3t.service.soap._2006_03_01.PrefixEntry) 

    /**
     * Method setContents
     * 
     * 
     * 
     * @param index
     * @param vContents
     */
    public void setContents(int index, org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry vContents)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _contentsList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _contentsList.set(index, vContents);
    } //-- void setContents(int, org.jets3t.service.soap._2006_03_01.ListEntry) 

    /**
     * Method setContents
     * 
     * 
     * 
     * @param contentsArray
     */
    public void setContents(org.jets3t.service.impl.soap.axis._2006_03_01.ListEntry[] contentsArray)
    {
        //-- copy array
        _contentsList.clear();
        for (int i = 0; i < contentsArray.length; i++) {
            _contentsList.add(contentsArray[i]);
        }
    } //-- void setContents(org.jets3t.service.soap._2006_03_01.ListEntry) 

    /**
     * Sets the value of field 'delimiter'.
     * 
     * @param delimiter the value of field 'delimiter'.
     */
    public void setDelimiter(java.lang.String delimiter)
    {
        this._delimiter = delimiter;
    } //-- void setDelimiter(java.lang.String) 

    /**
     * Sets the value of field 'isTruncated'.
     * 
     * @param isTruncated the value of field 'isTruncated'.
     */
    public void setIsTruncated(boolean isTruncated)
    {
        this._isTruncated = isTruncated;
        this._has_isTruncated = true;
    } //-- void setIsTruncated(boolean) 

    /**
     * Sets the value of field 'marker'.
     * 
     * @param marker the value of field 'marker'.
     */
    public void setMarker(java.lang.String marker)
    {
        this._marker = marker;
    } //-- void setMarker(java.lang.String) 

    /**
     * Sets the value of field 'maxKeys'.
     * 
     * @param maxKeys the value of field 'maxKeys'.
     */
    public void setMaxKeys(int maxKeys)
    {
        this._maxKeys = maxKeys;
        this._has_maxKeys = true;
    } //-- void setMaxKeys(int) 

    /**
     * Method setMetadata
     * 
     * 
     * 
     * @param index
     * @param vMetadata
     */
    public void setMetadata(int index, org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry vMetadata)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _metadataList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _metadataList.set(index, vMetadata);
    } //-- void setMetadata(int, org.jets3t.service.soap._2006_03_01.MetadataEntry) 

    /**
     * Method setMetadata
     * 
     * 
     * 
     * @param metadataArray
     */
    public void setMetadata(org.jets3t.service.impl.soap.axis._2006_03_01.MetadataEntry[] metadataArray)
    {
        //-- copy array
        _metadataList.clear();
        for (int i = 0; i < metadataArray.length; i++) {
            _metadataList.add(metadataArray[i]);
        }
    } //-- void setMetadata(org.jets3t.service.soap._2006_03_01.MetadataEntry) 

    /**
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(java.lang.String name)
    {
        this._name = name;
    } //-- void setName(java.lang.String) 

    /**
     * Sets the value of field 'nextMarker'.
     * 
     * @param nextMarker the value of field 'nextMarker'.
     */
    public void setNextMarker(java.lang.String nextMarker)
    {
        this._nextMarker = nextMarker;
    } //-- void setNextMarker(java.lang.String) 

    /**
     * Sets the value of field 'prefix'.
     * 
     * @param prefix the value of field 'prefix'.
     */
    public void setPrefix(java.lang.String prefix)
    {
        this._prefix = prefix;
    } //-- void setPrefix(java.lang.String) 

    /**
     * Method unmarshalListBucketResult
     * 
     * 
     * 
     * @param reader
     * @return ListBucketResult
     */
    public static org.jets3t.service.impl.soap.axis._2006_03_01.ListBucketResult unmarshalListBucketResult(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.jets3t.service.impl.soap.axis._2006_03_01.ListBucketResult) Unmarshaller.unmarshal(org.jets3t.service.impl.soap.axis._2006_03_01.ListBucketResult.class, reader);
    } //-- org.jets3t.service.soap._2006_03_01.ListBucketResult unmarshalListBucketResult(java.io.Reader) 

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
