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
package org.jets3t.gui.skins;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SkinsFactory {
    private static final Log log = LogFactory.getLog(SkinsFactory.class);

    public static final String DEFAULT_SKIN_NAME = "default";
    
    private String skinName = null;
    private Properties skinProperties = new Properties();
    
    
    private SkinsFactory(Properties properties) {
        this.skinName = properties.getProperty("skin.name");
        if (this.skinName == null) {
            this.skinName = DEFAULT_SKIN_NAME;
        }
        
        String skinPropertyPrefix = "skin." + this.skinName.toLowerCase() + ".";
        Iterator iter = properties.keySet().iterator();
        while (iter.hasNext()) {
            String propertyName = (String) iter.next();
            String propertyValue = properties.getProperty(propertyName);
            
            if (propertyName.toLowerCase().startsWith(skinPropertyPrefix)) {
                String skinPropertyName = propertyName.substring(skinPropertyPrefix.length());
                this.skinProperties.put(skinPropertyName, propertyValue);                
            }
        }
System.err.println("skinProperties=" + skinProperties);
    }
    
    public static SkinsFactory getInstance(Properties properties) {
        return new SkinsFactory(properties);
    }

    public LookAndFeel createSkinnedMetalTheme(String itemName) {
        Object instance = instantiateClass(buildSkinnedClassName("SkinnedLookAndFeel"), itemName);        
        if (instance != null) {
            return (LookAndFeel) instance;
        } else {
            Object lfInstance = null;
            try {
                Class lfClass = Class.forName(UIManager.getSystemLookAndFeelClassName());
                Constructor constructor = lfClass.getConstructor(new Class[] {});
                lfInstance = constructor.newInstance(new Object[] {});
            } catch (Exception e) {
                log.error("Unable to instantiate default system LookAndFeel class", e);
            }
            return (LookAndFeel) lfInstance;
        }        
    }
    
    public JButton createSkinnedJButton(String itemName) {
        Object instance = instantiateClass(buildSkinnedClassName("SkinnedJButton"), itemName);        
        if (instance != null) {
            return (JButton) instance;
        } else {
            return new JButton();
        }        
    }
    
    public JPanel createSkinnedJPanel(String itemName) {
        Object instance = instantiateClass(buildSkinnedClassName("SkinnedJPanel"), itemName);        
        if (instance != null) {
            return (JPanel) instance;
        } else {
            return new JPanel();
        }                
    }
    
    private String buildSkinnedClassName(String className) {
        String skinnedClassName = 
            this.getClass().getPackage().getName() + "." + this.skinName + "." + className;
        return skinnedClassName;
    }
    
    private Object instantiateClass(String className, String itemName) {
        try {
            Class myClass = Class.forName(className);
            Constructor constructor = myClass.getConstructor(
                new Class[] { Properties.class, String.class });
            Object instance = constructor.newInstance(new Object[] { skinProperties, itemName });
            return instance;
        } catch (ClassNotFoundException e) { 
            log.debug("Class does not exist, will use default. Skinned class name: " + className);
        } catch (Exception e) {
            log.warn("Unable to instantiate skinned class '" + className + "'", e);
        }
        return null;
    }
    
}
