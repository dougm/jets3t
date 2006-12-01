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

package org.jets3t.apps.uploader.skins;

import java.awt.Color;
import java.awt.Font;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

/**
 * A specialisation of the default Metal theme that allows specific colours and
 * fonts to be used instead of those in the Metal theme.
 * <p>
 * This class controls:
 * <ul>
 * <li>the colour used for Window and Control backgrounds</li>
 * <li>the colour used for System, Control and User text</li>
 * <li>the font used for System, Control and User text</li>
 * </ul>
 * 
 * @author James Murty
 */
public class SkinnedMetalTheme extends DefaultMetalTheme {
    private ColorUIResource backgroundColorUIResource = null;
    private ColorUIResource textColorUIResource = null;
    private FontUIResource fontUIResource = null;
    
    /**
     * Construct the theme with the colours and font to be used.
     * 
     * @param backgroundColor
     * the background colour used for Windows and Controls.
     * @param textColor
     * the colour used for System, Control and User text.
     * @param font
     * the font used for System, Control and User text.
     */
    public SkinnedMetalTheme(Color backgroundColor, Color textColor, Font font) {
        this.backgroundColorUIResource = new ColorUIResource(backgroundColor);
        this.textColorUIResource = new ColorUIResource(textColor);
        this.fontUIResource = new FontUIResource(font);
    }

    public String getName() {
        return "Uploader skinnable theme";
    }
    
    public FontUIResource getSystemTextFont() {
        return fontUIResource;
    }
        
    public FontUIResource getControlTextFont() {
        return fontUIResource;
    }
        
    public FontUIResource getUserTextFont() {
        return fontUIResource;
    }
        
    public ColorUIResource getSystemTextColor() {
        return textColorUIResource;
    }
    
    public ColorUIResource getControlTextColor() {
        return textColorUIResource;
    }

    public ColorUIResource getUserTextColor() {
        return textColorUIResource;
    }

    public ColorUIResource getWindowBackground() {
        return backgroundColorUIResource;
    }

    public ColorUIResource getControl() {
        return backgroundColorUIResource;
    }
    
}
