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
package org.jets3t.apps.cockpit.gui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.jets3t.gui.HyperlinkActivatedListener;
import org.jets3t.gui.JHtmlLabel;
import org.jets3t.service.S3Service;

/**
 * Dialog box to query for settings to apply to signed GET URLs. This dialog should be created
 * and displayed with {@link #setVisible(boolean)}, and once control returns the user's responses 
 * are available via {@link #getOkClicked()}, {@link #isVirtualHost()} and {@link #getExpiryTime()}.
 * <p>
 * The caller is responsible for disposing of this dialog.  
 * 
 * @author James Murty
 *
 */
public class SignedGetUrlDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = -685777313074677991L;

    private boolean okClicked = false;
    
    private JCheckBox virtualHostCheckBox = null;
    private JTextField expiryTimeTextField = null;
    private JButton okButton = null;
    private JButton cancelButton = null;
    
    private final Insets insetsDefault = new Insets(3, 5, 3, 5);

    
    public SignedGetUrlDialog(Frame ownerFrame, HyperlinkActivatedListener hyperlinkListener) {
        super(ownerFrame, "Generate Signed GET URL", true);
        
        String introductionText = "<html><center>Generate a signed GET URL that you can provide to"
            + " anyone<br>who needs to access objects in your bucket for a limited time.</center></html>";
        JHtmlLabel introductionLabel = new JHtmlLabel(introductionText, hyperlinkListener);
        introductionLabel.setHorizontalAlignment(JLabel.CENTER);
        JHtmlLabel virtualHostLabel = new JHtmlLabel("<html><b>Bucket is a virtual Host?</b></html>", hyperlinkListener);
        virtualHostLabel.setHorizontalAlignment(JLabel.CENTER);        
        JHtmlLabel expiryTimeLabel = new JHtmlLabel("<html><b>Expiry Time</b> (Hours)</html>", hyperlinkListener);
        expiryTimeLabel.setHorizontalAlignment(JLabel.CENTER);        
        
        virtualHostCheckBox = new JCheckBox();
        virtualHostCheckBox.setSelected(false);
        virtualHostCheckBox.setToolTipText("Check this box if your bucket is configured as a virtual host.");
        expiryTimeTextField = new JTextField();
        expiryTimeTextField.setText("0.5");
        expiryTimeTextField.setToolTipText("How long in hours until the URL will expire");
                
        cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(this);
        okButton = new JButton("Generate URL");
        okButton.setActionCommand("OK");
        okButton.addActionListener(this);
        
        // Set default ENTER and ESCAPE buttons.
        this.getRootPane().setDefaultButton(okButton);        
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("ESCAPE"), "ESCAPE");
        this.getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            private static final long serialVersionUID = -6225706489569112809L;

            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
                okClicked = false;
            }
        });        
        
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.add(cancelButton, new GridBagConstraints(0, 0, 
            1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsDefault, 0, 0));
        buttonsPanel.add(okButton, new GridBagConstraints(1, 0, 
            1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsDefault, 0, 0));
        
        JPanel panel = new JPanel(new GridBagLayout());
        int row = 0;
        panel.add(introductionLabel, new GridBagConstraints(0, row, 
            2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        panel.add(virtualHostLabel, new GridBagConstraints(0, ++row, 
            1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insetsDefault, 0, 0));
        panel.add(virtualHostCheckBox, new GridBagConstraints(1, row, 
            1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insetsDefault, 0, 0));
        panel.add(expiryTimeLabel, new GridBagConstraints(0, ++row, 
            1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insetsDefault, 0, 0));        
        panel.add(expiryTimeTextField, new GridBagConstraints(1, row, 
            1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        panel.add(buttonsPanel, new GridBagConstraints(0, ++row, 
            2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));            
        this.getContentPane().setLayout(new GridBagLayout());
        this.getContentPane().add(panel, new GridBagConstraints(0, 0, 
            1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsDefault, 0, 0));        
        
        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(ownerFrame);
    }
    
    public void actionPerformed(ActionEvent event) {
        if (event.getSource().equals(okButton)) {
            this.setVisible(false);
            okClicked = true;
        } else if (event.getSource().equals(cancelButton)) {
            this.setVisible(false);
            okClicked = false;
        }
    }
    
    public boolean getOkClicked() {
        return okClicked;
    }
    
    public boolean isVirtualHost() {
        return virtualHostCheckBox.isSelected();
    }       
    
    public String getExpiryTime() {
        return expiryTimeTextField.getText();
    }
        
}
