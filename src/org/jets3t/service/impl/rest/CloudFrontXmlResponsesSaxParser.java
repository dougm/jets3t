/*
 * jets3t : Java Extra-Tasty S3 Toolkit (for Amazon S3 online storage service)
 * This is a java.net project, see https://jets3t.dev.java.net/
 * 
 * Copyright 2008 James Murty
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
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.CloudFrontServiceException;
import org.jets3t.service.Constants;
import org.jets3t.service.Jets3tProperties;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.cloudfront.Distribution;
import org.jets3t.service.model.cloudfront.DistributionConfig;
import org.jets3t.service.utils.ServiceUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * XML Sax parser to read XML documents returned by the CloudFront service via 
 * the REST interface, and convert these documents into JetS3t objects.
 * 
 * @author James Murty
 */
public class CloudFrontXmlResponsesSaxParser {
    private static final Log log = LogFactory.getLog(CloudFrontXmlResponsesSaxParser.class);

    private XMLReader xr = null;
    private Jets3tProperties properties = null;

    /**
     * Constructs the XML SAX parser.  
     * 
     * @param properties
     * the JetS3t properties that will be applied when parsing XML documents.
     * 
     * @throws S3ServiceException
     */
    public CloudFrontXmlResponsesSaxParser(Jets3tProperties properties) throws S3ServiceException {
        this.properties = properties;
        
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
    
    /**
     * Constructs the XML SAX parser.  
     * @throws S3ServiceException
     */
    public CloudFrontXmlResponsesSaxParser() throws S3ServiceException {
        this(Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME));
    }    

    /**
     * Parses an XML document from an input stream using a document handler.
     * @param handler
     *        the handler for the XML document
     * @param inputStream
     *        an input stream containing the XML document to parse
     * @throws S3ServiceException
     *        any parsing, IO or other exceptions are wrapped in an S3ServiceException.
     */
    protected void parseXmlInputStream(DefaultHandler handler, InputStream inputStream)
        throws CloudFrontServiceException
    {
        try {
        	if (log.isDebugEnabled()) {
        		log.debug("Parsing XML response document with handler: " + handler.getClass());
        	}
            BufferedReader breader = new BufferedReader(new InputStreamReader(inputStream,
                Constants.DEFAULT_ENCODING));
            xr.setContentHandler(handler);
            xr.setErrorHandler(handler);
            xr.parse(new InputSource(breader));
        } catch (Throwable t) {
            try {
                inputStream.close();
            } catch (IOException e) {
            	if (log.isErrorEnabled()) {
            		log.error("Unable to close response InputStream up after XML parse failure", e);
            	}
            }
            throw new CloudFrontServiceException("Failed to parse XML document with handler "
                + handler.getClass(), t);
        }
    }
    
    /**
     * Parses a ListBucket response XML document from an input stream.
     * @param inputStream
     * XML data input stream.
     * @return
     * the XML handler object populated with data parsed from the XML stream.
     * @throws S3ServiceException
     */
    public ListDistributionListHandler parseDistributionListResponse(InputStream inputStream)
        throws CloudFrontServiceException
    {
        ListDistributionListHandler handler = new ListDistributionListHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public DistributionHandler parseDistributionResponse(InputStream inputStream)
        throws CloudFrontServiceException
    {
        DistributionHandler handler = new DistributionHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public DistributionConfigHandler parseDistributionConfigResponse(InputStream inputStream)
        throws CloudFrontServiceException
    {
        DistributionConfigHandler handler = new DistributionConfigHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public ErrorHandler parseErrorResponse(InputStream inputStream)
        throws CloudFrontServiceException
    {
        ErrorHandler handler = new ErrorHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    // ////////////
    // Handlers //
    // ////////////

    public class SimpleHandler extends DefaultHandler {
        private StringBuffer textContent = null;
        protected SimpleHandler currentHandler = null;
        protected SimpleHandler parentHandler = null;

        public SimpleHandler() {
            this.textContent = new StringBuffer();
            currentHandler = this;
        }
        
        public void transferControlToHandler(SimpleHandler toHandler) {
            currentHandler = toHandler;
            toHandler.parentHandler = this;
        }
        
        public void returnControlToParentHandler() {
            if (isChildHandler()) {
                parentHandler.currentHandler = parentHandler;
                parentHandler.controlReturned(this);
            } else {
                log.debug("Ignoring call to return control to parent handler, as this class has no parent: " + 
                    this.getClass().getName());
            }
        }
        
        public boolean isChildHandler() {
            return parentHandler != null;
        }
        
        public void controlReturned(SimpleHandler childHandler) {}
        
        public void startElement(String uri, String name, String qName, Attributes attrs) {
            try {
                Method method = currentHandler.getClass().getMethod("start" + name, new Class[] {});
                method.invoke(currentHandler, new Object[] {});
            } catch (NoSuchMethodException e) {
                log.debug("Skipped non-existent SimpleHandler subclass's startElement method for '" + name + "' in " + this.getClass().getName());
            } catch (Throwable t) {
                log.error("Unable to invoke SimpleHandler subclass's startElement method for '" + name + "' in " + this.getClass().getName(), t);
            }
        }
        
        public void endElement(String uri, String name, String qName) {
            String elementText = this.textContent.toString();
            try {
                Method method = currentHandler.getClass().getMethod("end" + name, new Class[] {String.class});
                method.invoke(currentHandler, new Object[] {elementText});
            } catch (NoSuchMethodException e) {
                log.debug("Skipped non-existent SimpleHandler subclass's endElement method for '" + name + "' in " + this.getClass().getName());
            } catch (Throwable t) {
                log.error("Unable to invoke SimpleHandler subclass's endElement method for '" + name + "' in " + this.getClass().getName(), t);
            }
            this.textContent = new StringBuffer();
        }        
        
        public void characters(char ch[], int start, int length) {
            this.textContent.append(ch, start, length);
        }        
        
        
    }
    
    public class DistributionHandler extends SimpleHandler {
        private Distribution distribution = null;
        
        private String id = null;
        private String status = null;        
        private Date lastModifiedTime = null;
        private String domainName = null;
        
        public Distribution getDistribution() {
            return distribution;
        }
        
        public void endId(String text) {
            this.id = text;
        }
        
        public void endStatus(String text) {
            this.status = text;
        }

        public void endLastModifiedTime(String text) throws ParseException {
            this.lastModifiedTime = ServiceUtils.parseIso8601Date(text);
        }

        public void endDomainName(String text) {
            this.domainName = text;
        }
        
        public void startDistributionConfig() {
            transferControlToHandler(new DistributionConfigHandler());
        }
        
        public void controlReturned(SimpleHandler childHandler) {
            DistributionConfig config = 
                ((DistributionConfigHandler) childHandler).getDistributionConfig();
            this.distribution = new Distribution(id, status, 
                lastModifiedTime, domainName, config);
        }

        public void endDistribution(String text) {
            returnControlToParentHandler();
        }
    }

    public class DistributionConfigHandler extends SimpleHandler {
        private DistributionConfig distributionConfig = null;
        
        private String origin = null;
        private String callerReference = null;        
        private List cnamesList = new ArrayList();
        private String comment = null;
        private boolean enabled = false;
        
        public DistributionConfig getDistributionConfig() {
            return distributionConfig;
        }
        
        public void endOrigin(String text) {
            this.origin = text;
        }
        
        public void endCallerReference(String text) {
            this.callerReference = text;
        }
        
        public void endCNAME(String text) {
            this.cnamesList.add(text);
        }

        public void endComment(String text) {
            this.comment = text;
        }
        
        public void endEnabled(String text) {
            this.enabled = "true".equalsIgnoreCase(text);
        }

        public void endDistributionConfig(String text) {
            this.distributionConfig = new DistributionConfig(
                origin, callerReference, 
                (String[]) cnamesList.toArray(new String[cnamesList.size()]), 
                comment, enabled);
            returnControlToParentHandler();
        }
    }

    public class DistributionSummaryHandler extends SimpleHandler {
        private Distribution distribution = null;
        
        private String id = null;
        private String status = null;        
        private Date lastModifiedTime = null;
        private String domainName = null;
        private String origin = null;
        private List cnamesList = new ArrayList();
        private String comment = null;
        private boolean enabled = false;
        
        public Distribution getDistribution() {
            return distribution;
        }
        
        public void endId(String text) {
            this.id = text;
        }
        
        public void endStatus(String text) {
            this.status = text;
        }

        public void endLastModifiedTime(String text) throws ParseException {
            this.lastModifiedTime = ServiceUtils.parseIso8601Date(text);
        }

        public void endDomainName(String text) {
            this.domainName = text;
        }

        public void endOrigin(String text) {
            this.origin = text;
        }

        public void endCNAME(String text) {
            this.cnamesList.add(text);
        }

        public void endComment(String text) {
            this.comment = text;
        }

        public void endEnabled(String text) {
            this.enabled = "true".equalsIgnoreCase(text);
        }

        public void endDistributionSummary(String text) {
            this.distribution = new Distribution(id, status, 
                lastModifiedTime, domainName, origin, 
                (String[]) cnamesList.toArray(new String[cnamesList.size()]),
                comment, enabled);
            returnControlToParentHandler();
        }
    }
    
    public class ListDistributionListHandler extends SimpleHandler {
        private List distributions = new ArrayList();
        private List cnamesList = new ArrayList();
        private String marker = null;
        private int maxItems = 100;
        private boolean isTruncated = false;
        
        public List getDistributions() {
            return distributions;
        }
        
        public boolean isTruncated() {
            return isTruncated;
        }

        public String getMarker() {
            return marker;
        }

        public int getMaxItems() {
            return maxItems;
        }

        public void startDistributionSummary() {
            transferControlToHandler(new DistributionSummaryHandler());
        }
        
        public void controlReturned(SimpleHandler childHandler) {
            distributions.add(
                ((DistributionSummaryHandler) childHandler).getDistribution());
        }
        
        public void endCNAME(String text) {
            this.cnamesList.add(text);
        }

        public void endMarker(String text) {
            this.marker = text;
        }
        
        public void endMaxItems(String text) {
            this.maxItems = Integer.parseInt(text);
        }

        public void endIsTruncated(String text) {
            this.isTruncated = "true".equalsIgnoreCase(text);
        }
    }

    public class ErrorHandler extends SimpleHandler {
        private String type = null;
        private String code = null;
        private String message = null;
        private String detail = null;
        private String requestId = null;

        public String getCode() {
            return code;
        }

        public String getDetail() {
            return detail;
        }

        public String getMessage() {
            return message;
        }

        public String getRequestId() {
            return requestId;
        }

        public String getType() {
            return type;
        }

        public void endType(String text) {
            this.type = text;
        }
        
        public void endCode(String text) {
            this.code = text;
        }
        
        public void endMessage(String text) {
            this.message = text;
        }

        public void endDetail(String text) {
            this.detail = text;
        }

        public void endRequestId(String text) {
            this.requestId = text;
        }
    }

}
