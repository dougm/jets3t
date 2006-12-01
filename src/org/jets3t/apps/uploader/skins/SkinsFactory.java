package org.jets3t.apps.uploader.skins;

import java.lang.reflect.Constructor;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.apps.uploader.Uploader;

public class SkinsFactory {
    private static final Log log = LogFactory.getLog(Uploader.class);

    public static final String DEFAULT_SKIN_NAME = "default";
    
    private String skinName = null;
    private Properties skinProperties = null;
    
    
    private SkinsFactory(String skinName, Properties skinProperties) {
        this.skinName = skinName;
        this.skinProperties = skinProperties;
    }
    
    public static SkinsFactory getInstance(Properties skinProperties) {
        return new SkinsFactory(DEFAULT_SKIN_NAME, skinProperties);
    }
    
    public static SkinsFactory getInstance(String skinName, Properties skinProperties) {
        if (skinName != null) { 
            return new SkinsFactory(skinName, skinProperties);
        } else {
            log.warn("Empty skin name, using default value");
            return getInstance(skinProperties);
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
    
    // TODO Remove
    public static void main(String[] args) {
        Properties skinProperties = new Properties();
        SkinsFactory factory = SkinsFactory.getInstance("html", skinProperties);
        JButton button = factory.createSkinnedJButton("Test");
        System.out.println("button=" + button);
    }
    
}
