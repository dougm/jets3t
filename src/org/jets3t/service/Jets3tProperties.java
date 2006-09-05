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

public class Jets3tProperties {
    private static final Log log = LogFactory.getLog(Jets3tProperties.class);
    
    private static Properties properties = new Properties();

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
    
    public static String getStringProperty(String propertyName, String defaultValue) {
        String stringValue = properties.getProperty(propertyName, defaultValue);
        log.debug(propertyName + "=" + stringValue);
        return stringValue;
    }
    
    public static long getLongProperty(String propertyName, long defaultValue) 
        throws NumberFormatException 
    {
        String longValue = properties.getProperty(propertyName, String.valueOf(defaultValue));
        log.debug(propertyName + "=" + longValue);
        return Long.parseLong(longValue);
    }
        
    public static int getIntProperty(String propertyName, int defaultValue) 
        throws NumberFormatException 
    {
        String intValue = properties.getProperty(propertyName, String.valueOf(defaultValue));
        log.debug(propertyName + "=" + intValue);
        return Integer.parseInt(intValue);
    }

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
