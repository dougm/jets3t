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
package org.jets3t.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class to locate jets3t-specific properties files and make them available from a central place.
 * <p>
 * Properties are loaded from sources in the following order. 
 * <ol>
 * <li><tt>jets3t.properties</tt> file in the classpath</li>
 * <li><tt>jets3t.properties</tt> file in the current working directory</li>
 * <li>System Properties</li>
 * </ol>
 * Properties sourced from later locations over-ride those sourced from prior locations. For example,
 * if a property exists in <tt>jets3t.properties</tt> in the classpath and is also set as a system
 * property, the system property version will be used.    
 * 
 * @author James Murty
 */
public class Jets3tProperties {
    private static final Log log = LogFactory.getLog(Jets3tProperties.class);
    
    /**
     * Stores the jets3t properties.
     */
    private static Properties properties = new Properties();

    /*
     * Load properties from sources in order.
     */
    static {       
        // Load properties from classpath.
        if (ClassLoader.getSystemResource(Constants.JETS3T_PROPERTIES_FILENAME) != null) {
            log.debug("Loading properties from resource in the classpath: " + 
                Constants.JETS3T_PROPERTIES_FILENAME);
            try {
                loadAndReplaceProperties(
                    ClassLoader.getSystemResourceAsStream(Constants.JETS3T_PROPERTIES_FILENAME),
                    "Resource '" + Constants.JETS3T_PROPERTIES_FILENAME + "' in classpath");
            } catch (IOException e) {
                log.error("Failed to load properties from resource in classpath: " 
                    + Constants.JETS3T_PROPERTIES_FILENAME, e);
            }
        } 
        
        // Load properties from file in current working directory.
        File file = new File(Constants.JETS3T_PROPERTIES_FILENAME);
        if (file.canRead()) {
            log.debug("Loading properties from file: " + file.getAbsolutePath());  
            try {
            
                loadAndReplaceProperties(
                    new FileInputStream(file), "File '" + file.getAbsolutePath() + "'");
            } catch (IOException e) {
                log.error("Failed to load properties from file: " + file.getAbsolutePath(), e);
            }
        } 
                
        // Load properties from System.
        log.debug("Loading System properties");  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            System.getProperties().store(baos, null);      
            loadAndReplaceProperties(new ByteArrayInputStream(baos.toByteArray()), "System properties");
        } catch (IOException e) {
            log.error("Failed to load System properties", e);            
        }
    }
    
    /**
     * Reads properties from an InputStream and stores them in this class's properties object. 
     * If a new property already exists, the property value is replaced.
     *  
     * @param is
     * @param propertiesSource
     * @throws IOException
     */
    private static void loadAndReplaceProperties(InputStream is, String propertiesSource) 
        throws IOException 
    {
        Properties newProperties = new Properties();
        newProperties.load(is);
        
        Iterator iter = newProperties.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next().toString();
            if (properties.containsKey(key)) {
                log.debug("Over-riding jets3t property [" + key + "=" + properties.getProperty(key)
                    + "] with value from properties source " + propertiesSource 
                    + ". New value: [" + key + "=" + newProperties.getProperty(key) + "]");
            } 
            properties.put(key, newProperties.getProperty(key));                
        }
    }
    
    /**
     * @param propertyName
     * @param defaultValue
     * @return 
     * the named Property value as a string if the property is set, otherwise returns the default value.
     */
    public static String getStringProperty(String propertyName, String defaultValue) {
        String stringValue = properties.getProperty(propertyName, defaultValue);
        log.debug(propertyName + "=" + stringValue);
        return stringValue;
    }

    /**
     * 
     * @param propertyName
     * @param defaultValue
     * @return
     * the named Property value as a long if the property is set, otherwise returns the default value.
     * @throws NumberFormatException
     */
    public static long getLongProperty(String propertyName, long defaultValue) 
        throws NumberFormatException 
    {
        String longValue = properties.getProperty(propertyName, String.valueOf(defaultValue));
        log.debug(propertyName + "=" + longValue);
        return Long.parseLong(longValue);
    }
        
    /**
     * 
     * @param propertyName
     * @param defaultValue
     * @return
     * the named Property value as an int if the property is set, otherwise returns the default value.
     * @throws NumberFormatException
     */
    public static int getIntProperty(String propertyName, int defaultValue) 
        throws NumberFormatException 
    {
        String intValue = properties.getProperty(propertyName, String.valueOf(defaultValue));
        log.debug(propertyName + "=" + intValue);
        return Integer.parseInt(intValue);
    }

    /**
     * 
     * @param propertyName
     * @param defaultValue
     * @return
     * the named Property value as a boolean if the property is set, otherwise returns the default value.
     * @throws IllegalArgumentException
     */
    public static boolean getBoolProperty(String propertyName, boolean defaultValue) 
        throws IllegalArgumentException 
    {
        String boolValue = properties.getProperty(propertyName, String.valueOf(defaultValue));
        log.debug(propertyName + "=" + boolValue);
        if ("true".equalsIgnoreCase(boolValue)) {
            return true;
        } else if ("false".equalsIgnoreCase(boolValue)) {
            return false;
        } else {
            throw new IllegalArgumentException("Boolean value '" + boolValue + "' for jets3t property '" 
                + propertyName + "' must be 'true' or 'false' (case-insensitive)");
        }
    }
    
}
