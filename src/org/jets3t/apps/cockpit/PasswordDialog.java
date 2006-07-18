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
package org.jets3t.apps.cockpit;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 * Dialog box for user to enter a password.
 * 
 * @author James Murty
 */
public class PasswordDialog extends JDialog implements ActionListener {
    private final Insets insetsZero = new Insets(0, 0, 0, 0);
    private final Insets insetsDefault = new Insets(5, 7, 5, 7);
    private JPasswordField passwordField = null;
    private JPasswordField confirmationPasswordField = null;

    private String password = null;
    private boolean doConfirmPassword = false;

    /**
     * Construct modal dialog for display over a Frame.
     * 
     * @param owner     Frame over which this dialog will be displayed and centred.
     * @param title     the dialog's title text
     * @param question  the question/statement to prompt the user for their password
     * @param doConfirmPassword   if true the password must be entered twice and must match  
     */
    public PasswordDialog(Frame owner, String title, String question, boolean doConfirmPassword) {
        super(owner, title, true);
        this.doConfirmPassword = doConfirmPassword;
        initGui(question);
    }

    /**
     * Construct modal dialog for display over another Dialog.
     * 
     * @param owner     Dialog over which this dialog will be displayed and centred.
     * @param title     the dialog's title text
     * @param question  the question/statement to prompt the user for their password
     * @param doConfirmPassword   if true the password must be entered twice and must match  
     */
    public PasswordDialog(Dialog owner, String title, String question, boolean doConfirmPassword) {
        super(owner, title, true);
        this.doConfirmPassword = doConfirmPassword;
        initGui(question);
    }

    /**
     * Initialises all GUI elements.
     */
    private void initGui(String question) {
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        
        int rowIndex = 0;

        JPanel container = new JPanel(new GridBagLayout());
        container.add(new JLabel(question), new GridBagConstraints(0, rowIndex++, 2, 1, 0, 0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, insetsDefault, 0, 0));
        
        passwordField = new JPasswordField();
        passwordField.setColumns(25);
        passwordField.setActionCommand("OK");
        passwordField.addActionListener(this);
        container.add(new JLabel("Password:"), new GridBagConstraints(0, rowIndex, 1, 1, 0, 0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
        container.add(passwordField, new GridBagConstraints(1, rowIndex++, 1, 1, 1, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));

        if (doConfirmPassword) {
            confirmationPasswordField = new JPasswordField();
            confirmationPasswordField.setColumns(25);
            confirmationPasswordField.setActionCommand("OK");
            confirmationPasswordField.addActionListener(this);
            container.add(new JLabel("Confirm Password:"), new GridBagConstraints(0, rowIndex, 1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            container.add(confirmationPasswordField, new GridBagConstraints(1, rowIndex++, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        }

        JPanel buttonsContainer = new JPanel(new GridBagLayout());
        JButton cancel = new JButton("Don't use my password");
        cancel.setActionCommand("Cancel");
        cancel.addActionListener(this);
        JButton okButton = new JButton("Use my password");
        okButton.setActionCommand("OK");
        okButton.setDefaultCapable(true);
        okButton.addActionListener(this);
        buttonsContainer.add(cancel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsZero, 0, 0));
        buttonsContainer.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsZero, 0, 0));

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
            if (doConfirmPassword) {
                String password = String.valueOf(passwordField.getPassword());
                String confirmationPassword = String.valueOf(confirmationPasswordField.getPassword());
                // Check that the Password and Confirmed Password fields match.
                if (!confirmationPassword.equals(password)) {
                    confirmationPasswordField.requestFocus();
                    return;
                }
            }
            this.password = new String(passwordField.getPassword());
        } else if ("Cancel".equals(e.getActionCommand())) {
            this.password = null;
        }
        this.hide();
    }

    /**
     * @return  the password entered by the user, or null if the dialog was cancelled.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Displays the dialog box and waits until the user enters their password, or cancells the dialog.
     * <p>
     * If the user enters their password, this method returns the password entered. 
     * If the user cancels the dialog, this method returns null.
     * 
     * @param owner     the Dialog over which this dialog will be displayed and centred.
     * @param title     the dialog's title text
     * @param question  the question/statement to prompt the user for their password
     * @param doConfirmPassword   if true the password must be entered twice and must match  
     * @return  the users password if the dialog was confirmed, null if it was cancelled
     */
    public static String showDialog(Dialog owner, String title, String question,
        boolean doConfirmPassword)
    {
        PasswordDialog passwordDialog = new PasswordDialog(owner, title, question, doConfirmPassword);
        passwordDialog.show();
        passwordDialog.dispose();
        return passwordDialog.getPassword();
    }

    /**
     * @param owner     the Frame over which this dialog will be displayed and centred.
     * @param title     the dialog's title text
     * @param question  the question/statement to prompt the user for their password
     * @param doConfirmPassword   if true the password must be entered twice and must match  
     * @return  the users password if the dialog was confirmed, null if it was cancelled
     */
    public static String showDialog(Frame owner, String title, String question,
        boolean doConfirmPassword)
    {
        PasswordDialog passwordDialog = new PasswordDialog(owner, title, question, doConfirmPassword);
        passwordDialog.show();
        passwordDialog.dispose();
        return passwordDialog.getPassword();
    }

    /**
     * Creates stand-alone dialog box for testing only.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        JFrame f = new JFrame();

        String password = null;
        while ((password = PasswordDialog.showDialog(f, "Enter your password",
            "Password to unlock remembered login:", true)) != null) {
            System.out.println("Password: " + password);
        }

        f.dispose();
    }

}
