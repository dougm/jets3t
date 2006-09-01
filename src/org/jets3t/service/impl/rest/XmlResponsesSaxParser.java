/*
 * jets3t : Java Extra-Tasty S3 Toolkit (for Amazon S3 online storage service)
 * This is a java.net project, see https://jets3t.dev.java.net/
 * 
 * Copyright 2006 James Murty
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.jets3t.service.impl.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.Constants;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.CanonicalGrantee;
import org.jets3t.service.acl.EmailAddressGrantee;
import org.jets3t.service.acl.GranteeInterface;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.model.S3Owner;
import org.jets3t.service.utils.ServiceUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class XmlResponsesSaxParser {
    private static final Log log = LogFactory.getLog(XmlResponsesSaxParser.class);

    private ServiceUtils serviceUtils = new ServiceUtils();

    private XMLReader xr = null;

    public XmlResponsesSaxParser() throws S3ServiceException {
        // Ensure we can load the XML Reader.
        try {
            xr = XMLReaderFactory.createXMLReader();
        } catch (SAXException e) {
            // oops, lets try doing this (needed in 1.4)
            System.setProperty("org.xml.sax.driver", "org.apache.crimson.parser.XMLReaderImpl");
            try {
                // Try once more...
                xr = XMLReaderFactory.createXMLReader();
            } catch (SAXException e2) {
                throw new S3ServiceException("Couldn't initialize a sax driver for the XMLReader");
            }
        }
    }

    public void parseXmlInputStream(DefaultHandler handler, InputStream inputStream)
        throws S3ServiceException
    {
        try {
            log.debug("Parsing XML response document with handler: " + handler.getClass());
            BufferedReader breader = new BufferedReader(new InputStreamReader(inputStream,
                Constants.DEFAULT_ENCODING));
            xr.setContentHandler(handler);
            xr.setErrorHandler(handler);
            xr.parse(new InputSource(breader));
        } catch (Throwable t) {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error("Unable to close response InputStream up after XML parse failure", e);
            }
            throw new S3ServiceException("Failed to parse XML document with handler "
                + handler.getClass(), t);
        }
    }

    public ListBucketHandler parseListBucketObjectsResponse(S3Bucket bucket, InputStream inputStream)
        throws S3ServiceException
    {
        ListBucketHandler handler = new ListBucketHandler(bucket);
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public ListAllMyBucketsHandler parseListMyBucketsResponse(InputStream inputStream)
        throws S3ServiceException
    {
        ListAllMyBucketsHandler handler = new ListAllMyBucketsHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public AccessControlListHandler parseAccessControlListResponse(InputStream inputStream)
        throws S3ServiceException
    {
        AccessControlListHandler handler = new AccessControlListHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    // ////////////
    // Handlers //
    // ////////////

    public class ListBucketHandler extends DefaultHandler {
        private S3Bucket ownerBucket = null;

        private S3Object currentObject = null;

        private S3Owner currentOwner = null;

        private List objects = new ArrayList();

        private StringBuffer currText = null;

        // Listing properties.
        private String bucketName = null;

        private String requestPrefix = null;

        private String requestMarker = null;

        private long requestMaxKeys = 0;

        private boolean listingTruncated = false;

        private String lastKey = null;

        public ListBucketHandler(S3Bucket ownerBucket) {
            super();
            this.ownerBucket = ownerBucket;
            this.currText = new StringBuffer();
        }

        public String getLastKey() {
            return lastKey;
        }

        public boolean isListingTruncated() {
            return listingTruncated;
        }

        public S3Object[] getObjects() {
            return (S3Object[]) objects.toArray(new S3Object[] {});
        }

        public String getRequestPrefix() {
            return requestPrefix;
        }

        public String getRequestMarker() {
            return requestMarker;
        }

        public long getRequestMaxKeys() {
            return requestMaxKeys;
        }

        public void startDocument() {
        }

        public void endDocument() {
        }

        public void startElement(String uri, String name, String qName, Attributes attrs) {
            if (name.equals("Contents")) {
                currentObject = new S3Object();
                currentObject.setBucket(ownerBucket);
            } else if (name.equals("Owner")) {
                currentOwner = new S3Owner();
                currentObject.setOwner(currentOwner);
            }
        }

        public void endElement(String uri, String name, String qName) {
            String elementText = this.currText.toString();
            // Listing details
            if (name.equals("Name")) {
                bucketName = elementText;
                log.debug("Examining listing for bucket: " + bucketName);
            } else if (name.equals("Prefix")) {
                requestPrefix = elementText;
            } else if (name.equals("Marker")) {
                requestMarker = elementText;
            } else if (name.equals("MaxKeys")) {
                requestMaxKeys = Long.parseLong(elementText);
            } else if (name.equals("IsTruncated")) {
                String isTruncatedStr = elementText.toLowerCase();
                if (isTruncatedStr.startsWith("false")) {
                    listingTruncated = false;
                } else if (isTruncatedStr.startsWith("true")) {
                    listingTruncated = true;
                } else {
                    throw new RuntimeException("Invalid value for IsTruncated field: "
                        + isTruncatedStr);
                }
            }
            // Object details.
            else if (name.equals("Contents")) {
                objects.add(currentObject);
                log.debug("=== Created new S3Object from listing: " + currentObject);
            } else if (name.equals("Key")) {
                currentObject.setKey(elementText);
                lastKey = elementText;
            } else if (name.equals("LastModified")) {
                try {
                    currentObject.setLastModifiedDate(serviceUtils.parseIso8601Date(elementText));
                } catch (ParseException e) {
                    throw new RuntimeException("Unexpected date format in list bucket output", e);
                }
            } else if (name.equals("ETag")) {
                currentObject.setETag(elementText);
            } else if (name.equals("Size")) {
                currentObject.setContentLength(Long.parseLong(elementText));
            } else if (name.equals("StorageClass")) {
                currentObject.setStorageClass(elementText);
            }
            // Owner details.
            else if (name.equals("ID")) {
                currentOwner.setId(elementText);
            } else if (name.equals("DisplayName")) {
                currentOwner.setDisplayName(elementText);
            }
            this.currText = new StringBuffer();
        }

        public void characters(char ch[], int start, int length) {
            this.currText.append(ch, start, length);
        }
    }

    public class ListAllMyBucketsHandler extends DefaultHandler {
        private S3Owner bucketsOwner = null;

        private S3Bucket currentBucket = null;

        private List buckets = null;

        private StringBuffer currText = null;

        public ListAllMyBucketsHandler() {
            super();
            buckets = new ArrayList();
            this.currText = new StringBuffer();
        }

        public S3Bucket[] getBuckets() {
            return (S3Bucket[]) buckets.toArray(new S3Bucket[] {});
        }

        public void startDocument() {
        }

        public void endDocument() {
        }

        public void startElement(String uri, String name, String qName, Attributes attrs) {
            if (name.equals("Bucket")) {
                currentBucket = new S3Bucket();
            } else if (name.equals("Owner")) {
                bucketsOwner = new S3Owner();
            }
        }

        public void endElement(String uri, String name, String qName) {
            String elementText = this.currText.toString();
            // Listing details.
            if (name.equals("ID")) {
                bucketsOwner.setId(elementText);
            } else if (name.equals("DisplayName")) {
                bucketsOwner.setDisplayName(elementText);
            }
            // Bucket item details.
            else if (name.equals("Bucket")) {
                log.debug("=== Created new bucket from listing: " + currentBucket);
                currentBucket.setOwner(bucketsOwner);
                buckets.add(currentBucket);
            } else if (name.equals("Name")) {
                currentBucket.setName(elementText);
            } else if (name.equals("CreationDate")) {
                try {
                    currentBucket.setCreationDate(serviceUtils.parseIso8601Date(elementText));
                } catch (ParseException e) {
                    throw new RuntimeException("Unexpected date format in list bucket output", e);
                }
            }
            this.currText = new StringBuffer();
        }

        public void characters(char ch[], int start, int length) {
            this.currText.append(ch, start, length);
        }
    }

    // TODO Remove?
    // public class ErrorResponseHandler extends DefaultHandler {
    // private String errorCode = null;
    // private String errorMessage = null;
    // private String resourcePath = null;
    // private String requestId = null;
    // private StringBuffer currText = null;
    //
    // public ErrorResponseHandler() {
    // super();
    // this.currText = new StringBuffer();
    // }
    //		
    // public String getErrorCode() {
    // return errorCode;
    // }
    //		
    // public String getErrorMessage() {
    // return errorMessage;
    // }
    //
    // public String getRequestId() {
    // return requestId;
    // }
    //
    // public String getResourcePath() {
    // return resourcePath;
    // }
    //
    // public void startDocument() { }
    //
    // public void endDocument() { }
    //
    // public void startElement(String uri, String name, String qName, Attributes attrs) { }
    //
    // public void endElement(String uri, String name, String qName) {
    // String elementText = this.currText.toString();
    // // Error details.
    // if (name.equals("Code")) {
    // errorCode = elementText;
    // } else if (name.equals("Message")) {
    // errorMessage = elementText;
    // } else if (name.equals("Resource")) {
    // resourcePath = elementText;
    // } else if (name.equals("RequestId")) {
    // requestId = elementText;
    // }
    // this.currText = new StringBuffer();
    // }
    //
    // public void characters(char ch[], int start, int length) {
    // this.currText.append(ch, start, length);
    // }
    // }

    public class AccessControlListHandler extends DefaultHandler {
        private AccessControlList accessControlList = null;

        private S3Owner owner = null;

        private GranteeInterface currentGrantee = null;

        private Permission currentPermission = null;

        private StringBuffer currText = null;

        private boolean insideACL = false;

        public AccessControlListHandler() {
            super();
            this.currText = new StringBuffer();
        }

        public AccessControlList getAccessControlList() {
            return accessControlList;
        }

        public void startDocument() {
        }

        public void endDocument() {
        }

        public void startElement(String uri, String name, String qName, Attributes attrs) {
            if (name.equals("Owner")) {
                owner = new S3Owner();
            } else if (name.equals("AccessControlList")) {
                accessControlList = new AccessControlList();
                accessControlList.setOwner(owner);
                insideACL = true;
            } else if (name.equals("Grantee")) {
                if ("AmazonCustomerByEmail".equals(attrs.getValue("xsi:type"))) {
                    currentGrantee = new EmailAddressGrantee();
                } else if ("CanonicalUser".equals(attrs.getValue("xsi:type"))) {
                    currentGrantee = new CanonicalGrantee();
                } else if ("Group".equals(attrs.getValue("xsi:type"))) {
                    currentGrantee = new GroupGrantee();
                }
            }
        }

        public void endElement(String uri, String name, String qName) {
            String elementText = this.currText.toString();
            // Owner details.
            if (name.equals("ID") && !insideACL) {
                owner.setId(elementText);
            } else if (name.equals("DisplayName") && !insideACL) {
                owner.setDisplayName(elementText);
            }
            // ACL details.
            else if (name.equals("ID")) {
                currentGrantee.setIdentifier(elementText);
            } else if (name.equals("EmailAddress")) {
                currentGrantee.setIdentifier(elementText);
            } else if (name.equals("URI")) {
                currentGrantee.setIdentifier(elementText);
            } else if (name.equals("DisplayName")) {
                ((CanonicalGrantee) currentGrantee).setDisplayname(elementText);
            } else if (name.equals("Permission")) {
                currentPermission = Permission.parsePermission(elementText);
            } else if (name.equals("Grant")) {
                accessControlList.grantPermission(currentGrantee, currentPermission);
            } else if (name.equals("AccessControlList")) {
                insideACL = false;
            }
            this.currText = new StringBuffer();
        }

        public void characters(char ch[], int start, int length) {
            this.currText.append(ch, start, length);
        }
    }

}
