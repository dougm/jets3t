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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Dialog box for user to enter authentication information for NT or Basic 
 * authentication domains.
 * 
 * @author James Murty
 */
public class AuthenticationDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = -8112836668013270984L;

    private final Insets insetsDefault = new Insets(5, 7, 5, 7);
    private JTextField domainField = null;
    private JTextField usernameField = null;
    private JPasswordField passwordField = null;

    private boolean isNtAuthentication = false;
    
    private String domain = null;
    private String user = null;
    private String password = null;

    /**
     * Construct modal dialog for display over a Frame.
     * 
     * @param owner     Frame over which this dialog will be displayed and centred.
     * @param title     the dialog's title text
     * @param question  the question/statement to prompt the user for their password
     * @param isNtAuthentication   if true a domain name is required in addition to the username and password.  
     */
    public AuthenticationDialog(Frame owner, String title, String question, boolean isNtAuthentication) {
        super(owner, title, true);
        this.isNtAuthentication = isNtAuthentication;
        initGui(question);
    }

    /**
     * Construct modal dialog for display over another Dialog.
     * 
     * @param owner     Dialog over which this dialog will be displayed and centred.
     * @param title     the dialog's title text
     * @param question  the question/statement to prompt the user for their password
     * @param isNtAuthentication   if true a domain name is required in addition to the username and password.  
     */
    public AuthenticationDialog(Dialog owner, String title, String question, boolean isNtAuthentication) {
        super(owner, title, true);
        this.isNtAuthentication = isNtAuthentication;
        initGui(question);
    }

    /**
     * Initialises all GUI elements.
     * 
     * @param question  the question/statement to prompt the user for their password
     */
    private void initGui(String question) {
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        
        int rowIndex = 0;

        JPanel container = new JPanel(new GridBagLayout());
        container.add(new JLabel(question), new GridBagConstraints(0, rowIndex++, 2, 1, 0, 0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, insetsDefault, 0, 0));
        
        domainField = new JTextField();
        usernameField = new JTextField();        
        passwordField = new JPasswordField();
        
        if (isNtAuthentication) {
            container.add(new JLabel("Domain:"), new GridBagConstraints(0, rowIndex, 1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            container.add(domainField, new GridBagConstraints(1, rowIndex++, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        }
        
        container.add(new JLabel("User:"), new GridBagConstraints(0, rowIndex, 1, 1, 0, 0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
        container.add(usernameField, new GridBagConstraints(1, rowIndex++, 1, 1, 1, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        container.add(new JLabel("Password:"), new GridBagConstraints(0, rowIndex, 1, 1, 0, 0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
        container.add(passwordField, new GridBagConstraints(1, rowIndex++, 1, 1, 1, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));

        JPanel buttonsContainer = new JPanel(new GridBagLayout());
        JButton cancel = new JButton("Cancel");
        cancel.setActionCommand("Cancel");
        cancel.addActionListener(this);
        JButton okButton = new JButton("Authenticate me");
        okButton.setActionCommand("OK");
        okButton.setDefaultCapable(true);
        okButton.addActionListener(this);
        buttonsContainer.add(cancel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsDefault, 0, 0));
        buttonsContainer.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsDefault, 0, 0));

        container.add(buttonsContainer, new GridBagConstraints(0, rowIndex++, 2, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsDefault, 0, 0));

        this.getContentPane().add(container);
        this.pack();
        this.setLocationRelativeTo(this.getOwner());
    }

    /**
     * Event handler for this dialog.
     */
    public void actionPerformed(ActionEvent e) {
        if ("OK".equals(e.getActionCommand())) {
            if (isNtAuthentication) {
                this.domain = domainField.getText();
            }
            this.user = usernameField.getText();
            this.password = new String(passwordField.getPassword());
        } else if ("Cancel".equals(e.getActionCommand())) {
            this.domain = null;
            this.user = null;
            this.password = null;
        }
        this.setVisible(false);
    }

    /**
     * @return  
     * the domain entered by the user, or null if the dialog was cancelled or NT authentication wasn't used.
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @return  
     * the user name entered by the user, or null if the dialog was cancelled.
     */
    public String getUser() {
        return user;
    }

    /**
     * @return  
     * the password entered by the user, or null if the dialog was cancelled.
     */
    public String getPassword() {
        return password;
    }

}
