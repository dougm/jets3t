package org.jets3t.apps.uploader.skins.html;

import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public class SkinnedJButton extends JButton {

    public SkinnedJButton(Properties skinProperties, String itemName) {
        super();
        this.setBorder(BorderFactory.createEmptyBorder());
    }
    
}
