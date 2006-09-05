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

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jets3t.service.Constants;
import org.jets3t.service.security.AWSCredentials;

/**
 * Dialog box for managing AWS Credentials, including storing and retrieving encrypted credentials.
 * <p>
 * AWS login credentials can be entered manually by the user, or they may be saved/remembered to
 * files encrypted with a password of the user's choosing.
 * 
 * @author James Murty
 */
public class LoginDialog extends JDialog implements ActionListener, ListSelectionListener, DocumentListener {
    
    private Frame ownerFrame = null;
    private File preferencesDirectory = null;
    private AWSCredentials awsCredentials = null;
    
    private SavedLoginTableModel savedLoginTableModel = null;
    private JTable savedLoginTable = null;
    private JTextField savedNameTF = null;
    private JPasswordField savedPasswordPF = null;
    private JPasswordField confirmSavedPasswordPF = null;
    private JTextField savedAccessKeyTF = null;
    private JPasswordField savedSecretKeyPF = null;
    private JButton savedLogInButton = null;
    private JButton doSaveLogin = null;
    private JButton doForgetLogin = null;
    private JTextField accessKeyTF = null;
    private JPasswordField secretKeyPF = null;
    
    private final Insets insetsZero = new Insets(0, 0, 0, 0);
    private final Insets insetsDefault = new Insets(5, 7, 5, 7);

    /**
     * Creates a modal dialog box with a title.
     * 
     * @param owner
     * the frame within which this dialog will be displayed and centred.
     */
    public LoginDialog(Frame owner, File preferencesDirectory) {
        super(owner, "Amazon S3 Login", true);
        this.ownerFrame = owner;
        this.initGui();
        this.initData(preferencesDirectory);
    }

    /**
     * Searches the given directory for all encrypted files (*.enc) which are assumed to be
     * remembered login files, and displays the friendly name of each in the remembered logins
     * table.
     * 
     * @param preferencesDirectory
     * the directory containing encrypted files representing "remembered" logins
     */
    private void initData(final File preferencesDirectory) {
		this.preferencesDirectory = preferencesDirectory;
		
		// Find any pre-existing remembered logins.
		File rememberedLoginFiles[] = preferencesDirectory.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return preferencesDirectory.equals(file.getParentFile())
					&& file.getName().endsWith(".enc");						
			}				
		});
		if (rememberedLoginFiles == null || rememberedLoginFiles.length == 0) {
			return;
		}
		try {
			for (int i = 0; i < rememberedLoginFiles.length; i++) {
				BufferedReader br = new BufferedReader(new FileReader(rememberedLoginFiles[i]));
				String friendlyName = br.readLine();
				savedLoginTableModel.addSavedLogin(friendlyName, rememberedLoginFiles[i]);
			}
			savedLoginTable.setRowSelectionInterval(0, 0);
		} catch (Exception e) {
			Cockpit.reportException(ownerFrame, "Unable to load existing remembered login files", e);
		}
	}
    
    /**
     * Initialises all GUI elements.
     */
    protected void initGui() {
        this.setResizable(true);
        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

        // Remembered Login Tab section.
        savedLoginTableModel = new SavedLoginTableModel();
        savedLoginTable = new JTable(savedLoginTableModel);
        savedLoginTable.getSelectionModel().addListSelectionListener(this);
        doForgetLogin = new JButton("Forget this login");
        doForgetLogin.setEnabled(false);
        doForgetLogin.setActionCommand("ForgetLogin");
        doForgetLogin.addActionListener(this);
        JButton cancelSL = new JButton("Don't log in");
        cancelSL.setActionCommand("Cancel");
        cancelSL.addActionListener(this);
        savedLogInButton = new JButton("Log in");
        savedLogInButton.setEnabled(false);
        savedLogInButton.setActionCommand("LogInWithRememberedCredentials");
        savedLogInButton.addActionListener(this);

        JPanel rememberedLoginContainer = new JPanel(new GridBagLayout());
        rememberedLoginContainer.add(new JScrollPane(savedLoginTable), new GridBagConstraints(0, 0,
            3, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsZero, 0, 0));
        rememberedLoginContainer.add(doForgetLogin, new GridBagConstraints(0, 1, 1, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsZero, 0, 0));
        rememberedLoginContainer.add(cancelSL, new GridBagConstraints(1, 1, 1, 1, 1, 0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, insetsZero, 0, 0));
        rememberedLoginContainer.add(savedLogInButton, new GridBagConstraints(2, 1, 1, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsZero, 0, 0));

        savedNameTF = new JTextField();
        savedNameTF.getDocument().addDocumentListener(this);
        savedPasswordPF = new JPasswordField();
        savedPasswordPF.getDocument().addDocumentListener(this);
        confirmSavedPasswordPF = new JPasswordField();
        confirmSavedPasswordPF.getDocument().addDocumentListener(this);
        savedAccessKeyTF = new JTextField();
        savedAccessKeyTF.getDocument().addDocumentListener(this);
        savedAccessKeyTF.setColumns(25);
        savedSecretKeyPF = new JPasswordField();
        savedSecretKeyPF.getDocument().addDocumentListener(this);
        doSaveLogin = new JButton("Remember this login");
        doSaveLogin.setActionCommand("RememberLogin");
        doSaveLogin.setEnabled(false);
        doSaveLogin.addActionListener(this);

        JPanel rememberedLoginDetailsContainer = new JPanel(new GridBagLayout());
        rememberedLoginDetailsContainer.add(new JLabel("Login details to remember", JLabel.CENTER),
            new GridBagConstraints(0, 0, 2, 1, 2, 0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        rememberedLoginDetailsContainer.add(new JLabel("Your Name:"), new GridBagConstraints(0, 1,
            1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsZero, 0, 0));
        rememberedLoginDetailsContainer.add(savedNameTF, new GridBagConstraints(1, 1, 1, 1, 1, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));
        rememberedLoginDetailsContainer.add(new JLabel("AWS Access Key:"), new GridBagConstraints(
            0, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsZero, 0, 0));
        rememberedLoginDetailsContainer.add(savedAccessKeyTF, new GridBagConstraints(1, 2, 1, 1, 1,
            0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));
        rememberedLoginDetailsContainer.add(new JLabel("AWS Secret Key:"), new GridBagConstraints(
            0, 3, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsZero, 0, 0));
        rememberedLoginDetailsContainer.add(savedSecretKeyPF, new GridBagConstraints(1, 3, 1, 1, 1,
            0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));
        rememberedLoginDetailsContainer.add(new JLabel("Password:"), new GridBagConstraints(0,
            4, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsZero, 0, 0));
        rememberedLoginDetailsContainer.add(savedPasswordPF, new GridBagConstraints(1, 4, 1, 1, 1,
            0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));
        rememberedLoginDetailsContainer.add(new JLabel("Confirm Password:"), new GridBagConstraints(0,
            5, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsZero, 0, 0));
        rememberedLoginDetailsContainer.add(confirmSavedPasswordPF, new GridBagConstraints(1, 5, 1, 1, 1,
            0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));
        rememberedLoginDetailsContainer.add(doSaveLogin, new GridBagConstraints(0, 6, 2, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsZero, 0, 0));

        JPanel tab1Container = new JPanel(new GridBagLayout());
        tab1Container.add(rememberedLoginContainer, new GridBagConstraints(0, 0, 1, 1, 1, 1,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsDefault, 0, 0));
        tab1Container.add(new JSeparator(), new GridBagConstraints(0, 1, 1, 1, 1, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));
        tab1Container.add(rememberedLoginDetailsContainer, new GridBagConstraints(0, 2, 1, 1, 1, 0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsDefault, 0, 0));

        // Direct Login Tab section.
        JPanel directLoginFieldsContainer = new JPanel(new GridBagLayout());
        directLoginFieldsContainer.add(new JLabel("Access Key:"), new GridBagConstraints(0, 0, 1,
            1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsZero, 0, 0));
        accessKeyTF = new JTextField();
        accessKeyTF.setColumns(25);
        directLoginFieldsContainer.add(accessKeyTF, new GridBagConstraints(1, 0, 1, 1, 1, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));
        directLoginFieldsContainer.add(new JLabel("Secret Key:"), new GridBagConstraints(0, 1, 1,
            1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsZero, 0, 0));
        secretKeyPF = new JPasswordField();
        directLoginFieldsContainer.add(secretKeyPF, new GridBagConstraints(1, 1, 1, 1, 1, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));

        JPanel directLoginButtonsContainer = new JPanel(new GridBagLayout());
        JButton cancel = new JButton("Don't log in");
        cancel.setActionCommand("Cancel");
        cancel.addActionListener(this);
        JButton login = new JButton("Log in");
        login.setActionCommand("LogInWithDirectCredentials");
        login.addActionListener(this);
        directLoginButtonsContainer.add(cancel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsZero, 0, 0));
        directLoginButtonsContainer.add(login, new GridBagConstraints(1, 0, 1, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsZero, 0, 0));

        JPanel directLoginContainer = new JPanel(new GridBagLayout());
        directLoginContainer.add(directLoginFieldsContainer, new GridBagConstraints(0, 0, 1, 1, 1,
            0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        directLoginContainer.add(directLoginButtonsContainer, new GridBagConstraints(0, 1, 1, 1, 1,
            1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));

        // Combine sections.
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add(tab1Container, "Remembered Logins");
        tabbedPane.add(directLoginContainer, "Login Directly");

        this.getContentPane().add(tabbedPane);
        this.pack();
        this.setSize(500, 360);
        this.setLocationRelativeTo(this.getOwner());
    }

    /**
     * Event handler for this dialog.
     */
    public void actionPerformed(ActionEvent e) {
        if ("LogInWithDirectCredentials".equals(e.getActionCommand())) {
            String accessKey = accessKeyTF.getText();
            String secretKey = new String(secretKeyPF.getPassword());
            this.awsCredentials = new AWSCredentials(accessKey, secretKey);
            this.hide();
        } else if ("LogInWithRememberedCredentials".equals(e.getActionCommand())) {
            if (loadRememberedCredentials()) {
                this.hide();
            }
        } else if ("RememberLogin".equals(e.getActionCommand())) {
            rememberLogin();
        } else if ("ForgetLogin".equals(e.getActionCommand())) {
            forgetLogin();
        } else if ("Cancel".equals(e.getActionCommand())) {
            this.awsCredentials = null;
            this.hide();
        }
    }

    /**
     * Table selection event handler for this dialog.
     */
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && e.getSource().equals(savedLoginTable.getSelectionModel())) {
            if (savedLoginTable.getSelectedRows().length == 0) {
                // Nothing selected.
                doForgetLogin.setEnabled(false);
                savedLogInButton.setEnabled(false);
            } else {
                doForgetLogin.setEnabled(true);
                savedLogInButton.setEnabled(true);
            }
        }
    }

    /**
     * Text field event handler for character inserts, defers to {@link #saveLoginFieldsChanged}
     */
    public void insertUpdate(DocumentEvent e) {
        saveLoginFieldsChanged();
    }

    /**
     * Text field event handler for character inserts, defers to {@link #saveLoginFieldsChanged}
     */
    public void removeUpdate(DocumentEvent e) {
        saveLoginFieldsChanged();
    }

    /**
     * Text field event handler for character changes, defers to {@link #saveLoginFieldsChanged}
     */
    public void changedUpdate(DocumentEvent e) {
        saveLoginFieldsChanged();
    }

    /**
     * Determines whether sufficient details have been provided for a set of AWS Authorization
     * credentials to be saved/remembered. When sufficient details are available, the Remember
     * button is enabled.
     * <p>
     * Credentials may only be saved/remembered when the AWS Access Key, AWS Secret Key, and a
     * friendly name has been specified. An encryption password may also be specified but is not
     * required - the default password will be used if none is provided.
     */
    protected void saveLoginFieldsChanged() {
        if (savedNameTF.getText().length() > 0 
            && savedAccessKeyTF.getText().length() > 0
            && savedSecretKeyPF.getPassword().length > 0
            // && savedPasswordPF.getText().length() > 0 // Password can be empty.
            ) 
        {
            doSaveLogin.setEnabled(true);
        } else {
            doSaveLogin.setEnabled(false);
        }
    }

    /**
     * Deletes/forgets a previously saved/remembered login by deleting the associated file and
     * removing the login from the remembered logins table.
     */
    private void forgetLogin() {
        int selectedRow = savedLoginTable.getSelectedRows()[0];
        File credFile = savedLoginTableModel.getSavedLoginFile(selectedRow);
        credFile.delete();
        savedLoginTableModel.removeSavedLogin(selectedRow);
        if (savedLoginTable.getRowCount() > 0) {
            if (selectedRow > savedLoginTable.getRowCount() - 1) {
                // Select last row.
                int lastRow = savedLoginTable.getRowCount() - 1;
                savedLoginTable.setRowSelectionInterval(lastRow, lastRow);
            } else {
                // Select next row
                savedLoginTable.setRowSelectionInterval(selectedRow, selectedRow);
            }
        }
    }

    /**
     * Saves/remembers AWS login credentials by creating an encrypted file containing the
     * human-friendly name for the login, the AWS Access Key and the AWS Secret Key.
     * <p>
     * If a password is provided it is used when encrypting the saved/remembered file, otherwise a
     * default password is used.
     * <p>
     * The encrypted file is saved as &lt;Friendly name>.enc.
     * <p> 
     * TODO Add sanity checks to ensure the friendly name provided is a legal file name. 
     * Presently this will just fail.
     */
    private void rememberLogin() {
        String awsAccessKey = savedAccessKeyTF.getText();
        String awsSecretKey = String.valueOf(savedSecretKeyPF.getPassword());
        String friendlyName = savedNameTF.getText();

        AWSCredentials credentialsToSave = new AWSCredentials(awsAccessKey, awsSecretKey,
            friendlyName);

        File credFile = new File(preferencesDirectory, friendlyName + ".enc");
        try {
            String encryptionPassword = String.valueOf(savedPasswordPF.getPassword());
            String encryptionPasswordConfirmation = String.valueOf(confirmSavedPasswordPF.getPassword());
            
            if (!encryptionPassword.equals(encryptionPasswordConfirmation)) {
                confirmSavedPasswordPF.requestFocus();
                return;
            }

            credentialsToSave.save(encryptionPassword, credFile);

            int addedAt = savedLoginTableModel.addSavedLogin(credentialsToSave.getFriendlyName(),
                credFile);
            savedLoginTable.setRowSelectionInterval(addedAt, addedAt);

            savedNameTF.setText("");
            savedAccessKeyTF.setText("");
            savedSecretKeyPF.setText("");
            savedPasswordPF.setText("");
            confirmSavedPasswordPF.setText("");
        } catch (Exception e) {
            Cockpit.reportException(ownerFrame, "Unable to save credentials to file " 
                + credFile.getAbsolutePath(), e);
        }
    }

    /**
     * Loads AWS login credentials from a saved/remembered login file by decrypting the file. The
     * user is prompted to enter a password to decrypt the file.
     * 
     * @return true if the file was decrypted and the AWS login credentials read, false otherwise.
     */
    private boolean loadRememberedCredentials() {
        String friendlyName = (String) savedLoginTableModel
            .getSavedLoginFriendlyName(savedLoginTable.getSelectedRows()[0]);

        String password = PasswordDialog.showDialog(this, "Unlock remembered login",
            "Password to unlock login for '" + friendlyName + "':", false);

        if (password != null) {
            try {
                File credentialsFile = savedLoginTableModel.getSavedLoginFile(savedLoginTable
                    .getSelectedRows()[0]);
                awsCredentials = AWSCredentials.load(password, credentialsFile);
                return true;
            } catch (Exception e) {
                Cockpit.reportException(ownerFrame, "Unable to open saved login", e);
            }
        }
        return false;
    }

    /**
     * @return the AWS login credentials entered or selected by the user.
     */
    protected AWSCredentials getAWSCredentials() {
        return awsCredentials;
    }

    /**
     * Displays the dialog box and waits until the user enters/selects the AWS login credentials to
     * use, or cancells the dialog.
     * <p>
     * If the user has entered or selected credentials, these are returned by this method. If the
     * user cancels the dialog, this method returns null.
     * 
     * @param owner
     * the Frame within which this dialog will be displayed and centered
     * @param preferencesDirectory
     * the directory where saved/remembered logins are stored as *.enc files.
     * @return the AWS credentials entered/selected by the user, or null if the dialog was cancelled
     */
    public static AWSCredentials showDialog(Frame owner, File preferencesDirectory) {
        LoginDialog dialog = new LoginDialog(owner, preferencesDirectory);
        dialog.show();
        dialog.dispose();
        return dialog.getAWSCredentials();
    }

    /**
     * Table model for displaying saved/remembered logins.
     * 
     * @author James Murty
     */
    private class SavedLoginTableModel extends DefaultTableModel {
        HashMap savedLoginMap = new HashMap();

        ArrayList savedLoginList = new ArrayList();

        public SavedLoginTableModel() {
            super(new String[] { "Remembered Logins" }, 0);
        }

        public int addSavedLogin(String friendlyName, File file) {
            int insertRow = Collections.binarySearch(savedLoginList, friendlyName,
                new Comparator() {
                    public int compare(Object o1, Object o2) {
                        return ((String) o1).compareToIgnoreCase(((String) o2));
                    }
                });
            if (insertRow >= 0) {
                // We already have an item with this key, replace it.
                savedLoginList.remove(insertRow);
                super.removeRow(insertRow);
            } else {
                insertRow = (-insertRow) - 1;
            }
            // New object to insert.
            savedLoginList.add(insertRow, friendlyName);
            savedLoginMap.put(friendlyName, file);
            super.insertRow(insertRow, new Object[] { friendlyName });
            return insertRow;
        }

        public void removeSavedLogin(int index) {
            String friendlyName = (String) savedLoginList.get(index);
            super.removeRow(index);
            savedLoginList.remove(index);
            savedLoginMap.remove(friendlyName);
        }

        public void removeAllSavedLogins() {
            int rowCount = super.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                super.removeRow(0);
            }
            savedLoginList.clear();
            savedLoginMap.clear();
        }

        public File getSavedLoginFile(int index) {
            String friendlyName = (String) savedLoginList.get(index);
            return (File) savedLoginMap.get(friendlyName);
        }

        public String getSavedLoginFriendlyName(int index) {
            String friendlyName = (String) savedLoginList.get(index);
            return friendlyName;
        }

        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    /**
     * Creates stand-alone dialog box for testing only.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        JFrame f = new JFrame();

        AWSCredentials awsCredentials = null;

        while ((awsCredentials = LoginDialog.showDialog(f, Constants.DEFAULT_PREFERENCES_DIRECTORY)) != null) {
            System.out.println("AWS Credentials (" + awsCredentials.getFriendlyName() + ") : "
                + awsCredentials.getAccessKey() + " : " + awsCredentials.getSecretKey());
        }
        f.dispose();
    }

}
