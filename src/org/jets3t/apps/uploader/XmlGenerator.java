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
package org.jets3t.apps.uploader;

import java.io.StringWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.jets3t.service.utils.ServiceUtils;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Generates XML documents containing metadata about files uploaded to Amazon S3 
 * by the Uploader. The XML document includes metadata for user inputs, 
 * inputs sourced from applet parameter tags, and additional information 
 * available from the uploader such as filenames and generated UUID.
 * <p>
 * XML documents produced by this class have the following format:<br>
 * <tt>
 * &ltUploader uploadDate="2006-11-07T10:37:53.077Z" version="1.0">
 * &ltProperty name="AspectRatio" source="userinput">&lt![CDATA[4:3]]>&lt/Property>
 * &ltProperty name="title" source="parameter">&lt![CDATA[A great title]]>&lt/Property>
 * &ltProperty name="originalFilename" source="uploader">&lt![CDATA[jug-asl-2.0.0 copy.jar.avi]]>&lt/Property>
 * &ltProperty name="uploaderUUID" source="uploader">&lt![CDATA[c759568f-f238-3972-82fd-f07ed7baa400]]>&lt/Property>
 * &lt/Uploader>
 * </tt>
 *   
 * @author James Murty
 */
public class XmlGenerator {
    private static final Log log = LogFactory.getLog(XmlGenerator.class);

    /**
     * Generates an XML document containing metadata information as Property elements.
     * The root of the document is the element Uploader.
     * 
     * @param xmlVersionNumber
     * text describing the version of the XML document, for future-proofing. Becomes a
     * <b>version</b> attribute of the root element. 
     * @param userInputComponentsMap
     * a map of property names to Swing components containing the user's responses. Swing
     * components supported include: {@link ButtonGroup}'s containing {@link JRadioButton}s,
     * {@link JComboBox}, {@link JTextField}, {@link JPasswordField}, {@link JTextArea}.  
     * @param appletParametersMap
     * a map of property names to applet parameter string values.
     * @param uploaderInfoMap
     * a map of property names to uploader-sourced string values.
     * @return
     * an XML document string containing Property elements.
     * 
     * @throws Exception
     */
    public String generateXml(String xmlVersionNumber, Map userInputComponentsMap, 
        Map appletParametersMap, Map uploaderInfoMap) throws Exception
    {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        
        Document document = builder.newDocument();
        Element rootElem = document.createElement("Uploader");
        document.appendChild(rootElem);
        rootElem.setAttribute("version", xmlVersionNumber);
        rootElem.setAttribute("uploadDate",
            ServiceUtils.formatIso8601Date(new Date()));
        
        // Add field components (user's input) to XML document.
        for (Iterator iter = userInputComponentsMap.keySet().iterator(); iter.hasNext();) {
            String fieldName = (String) iter.next();
            String fieldValue = null;
            
            Object component = userInputComponentsMap.get(fieldName);
            if (component instanceof ButtonGroup) {
                ButtonGroup bg = (ButtonGroup) component;
                Enumeration radioEnum = bg.getElements();
                while (radioEnum.hasMoreElements()) {
                    JRadioButton button = (JRadioButton) radioEnum.nextElement();
                    if (button.isSelected()) {
                        fieldValue = button.getText();
                        break;
                    }
                }
            } else if (component instanceof JComboBox) {
                fieldValue = ((JComboBox) component).getSelectedItem().toString();
            } else if (component instanceof JTextField) {
                fieldValue = ((JTextField) component).getText();
            } else if (component instanceof JPasswordField) {
                fieldValue = new String(((JPasswordField) component).getPassword());
            } else if (component instanceof JTextArea) {            
                fieldValue = ((JTextArea) component).getText();
            } else {
                log.warn("Unrecognised component type for field named '" + fieldName + "': "
                    + component.getClass().getName());
            }
            rootElem.appendChild(createPropertyElement(document, fieldName, fieldValue, "userinput"));
        }
        
        // Add applet parameters to XML document.
        for (Iterator iter = appletParametersMap.keySet().iterator(); iter.hasNext();) {
            String fieldName = (String) iter.next();
            String fieldValue = (String) appletParametersMap.get(fieldName);
            rootElem.appendChild(createPropertyElement(document, fieldName, fieldValue, "parameter"));
        }        
        
        // Add additional information to XML document.
        for (Iterator iter = uploaderInfoMap.keySet().iterator(); iter.hasNext();) {
            String fieldName = (String) iter.next();
            String fieldValue = (String) uploaderInfoMap.get(fieldName);
            rootElem.appendChild(createPropertyElement(document, fieldName, fieldValue, "uploader"));
        }        

        // Serialize XML document to String.
        OutputFormat outputFormat = new OutputFormat(document);
        StringWriter writer = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(writer, outputFormat);
        serializer.serialize(document);
        
        return writer.toString();
    }
    
    /**
     * Creates a Property XML element for a document.
     * 
     * @param document 
     * the document the property is being created for.
     * @param propertyName 
     * the property's name, becomes a <b>name</b> attribute of the element.
     * @param propertyValue 
     * the property's value, becomes the CDATA text value of the element. If this value
     * is null, the Property element is empty.
     * @param source 
     * text to describe the source of the information, such as userinput or parameter. 
     * Becomes a <b>source</b> attribute of the element.
     * 
     * @return
     * a Property element.
     */
    private Element createPropertyElement(
        Document document, String propertyName, String propertyValue, String source) 
    {
        Element propertyElem = document.createElement("Property");
        propertyElem.setAttribute("name", propertyName);
        propertyElem.setAttribute("source", source);            
        if (propertyValue != null) {
            CDATASection cdataSection = document.createCDATASection(propertyValue);
            propertyElem.appendChild(cdataSection);
        }
        return propertyElem;        
    }
    
}
