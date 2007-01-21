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
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.gui.JHtmlLabel;

/**
 * Manages the creation of skinned GUI elements. 
 * Skinned elements are created using the following process:
 * <ol>
 * <li>Instantiate a skin-specific class in the skin's package 
 * <code>org.jets3t.gui.skins.<i>&lt;skinName&gt;</i></code></li>
 * <li>If a skin-specific class is not available or cannot be created,
 * instantiate a generic GUI class instead</li>
 * </ol>
 * <p>
 * Skinned classes are specially-named extensions to standard Swing classes, which must have a 
 * constructor of the form <br><code>public SkinnedJButton(Properties skinProperties, String itemName)</code>.
 * This constructor allows skinned GUI elements to change their look or behaviour based on any 
 * skin-specific properties that are provided, or based on the name of a specific GUI element.
 * <p>
 * The skinned class names supported by this factory include:
 * <table>
 * <tr><th>Class name</th><th>Extends</th></tr>
 * <tr><td>SkinnedJButton</td><td>javax.swing.JButton</td></tr>
 * <tr><td>SkinnedJHtmlLabel</td><td>org.jets3t.gui.JHtmlLabel</td></tr>
 * <tr><td>SkinnedJPanel</td><td>javax.swing.JPanel</td></tr>
 * <tr><td>SkinnedLookAndFeel</td><td>javax.swing.plaf.metal.MetalLookAndFeel</td></tr>
 * </table>
 * 
 * @author James Murty
 *
 */
public class SkinsFactory {
    private static final Log log = LogFactory.getLog(SkinsFactory.class);

    public static final String DEFAULT_SKIN_NAME = "default";
    
    /**
     * The name of the chosen skin.
     */
    private String skinName = null;
    
    /**
     * Properties that apply specifically to the chosen skin.
     */
    private Properties skinProperties = new Properties();
    
    /**
     * Construct the factory and find skin-specific properties in the provided properties set.
     * 
     * @param properties
     * A set of properties that may contain skin-specific properties.
     */
    private SkinsFactory(Properties properties) {
        this.skinName = properties.getProperty("skin.name");
        if (this.skinName == null) {
            this.skinName = DEFAULT_SKIN_NAME;
        }
        
        // Find skin-specific properties.
        String skinPropertyPrefix = "skin." + this.skinName.toLowerCase(Locale.getDefault()) + ".";
        Iterator iter = properties.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String propertyName = (String) entry.getKey();
            String propertyValue = (String) entry.getValue();            
            
            if (propertyName.toLowerCase(Locale.getDefault()).startsWith(skinPropertyPrefix)) {
                String skinPropertyName = propertyName.substring(skinPropertyPrefix.length());
                this.skinProperties.put(skinPropertyName, propertyValue);                
            }
        }
    }
    
    /**
     * Provides a skin factory initialised with skin-specific properties from the provided 
     * properties set. Skin-specific properties are identified as those properties with the 
     * prefix <code>skin.<i>&lt;skinName&gt;</i>.</code>
     * 
     * @param properties
     * a set of properties that may contain skin-specific properties.
     * 
     * @return
     * the skins factory initialised with skin settings.
     */
    public static SkinsFactory getInstance(Properties properties) {
        return new SkinsFactory(properties);
    }

    /**
     * @param itemName
     * the name of this specific item in the GUI, which may be used to determine how the skinned
     * item should look or behave.
     * 
     * @return
     * a <code>SkinnedLookAndFeel</code> class implementation for the current skin, or the default
     * system LookAndFeel if no skin-specific implementation is available.
     */
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
    
    /**
     * @param itemName
     * the name of this specific item in the GUI, which may be used to determine how the skinned
     * item should look or behave.
     * 
     * @return
     * a <code>SkinnedJButton</code> class implementation for the current skin, or a default
     * JButton if no skin-specific implementation is available.
     */
    public JButton createSkinnedJButton(String itemName) {
        Object instance = instantiateClass(buildSkinnedClassName("SkinnedJButton"), itemName);        
        if (instance != null) {
            return (JButton) instance;
        } else {
            return new JButton();
        }        
    }
    
    /**
     * @param itemName
     * the name of this specific item in the GUI, which may be used to determine how the skinned
     * item should look or behave.
     * 
     * @return
     * a <code>SkinnedJPanel</code> class implementation for the current skin, or a default
     * JPanel if no skin-specific implementation is available.
     */
    public JPanel createSkinnedJPanel(String itemName) {
        Object instance = instantiateClass(buildSkinnedClassName("SkinnedJPanel"), itemName);        
        if (instance != null) {
            return (JPanel) instance;
        } else {
            return new JPanel();
        }                
    }
    
    /**
     * @param itemName
     * the name of this specific item in the GUI, which may be used to determine how the skinned
     * item should look or behave.
     * 
     * @return
     * a <code>SkinnedJLabel</code> class implementation for the current skin, or a default
     * JHtmlLabel if no skin-specific implementation is available.
     */
    public JHtmlLabel createSkinnedJHtmlLabel(String itemName) {
        Object instance = instantiateClass(buildSkinnedClassName("SkinnedJHtmlLabel"), itemName);        
        if (instance != null) {
            return (JHtmlLabel) instance;
        } else {
            return new JHtmlLabel(null);
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
