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
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.gui.ErrorDialog;
import org.jets3t.gui.HyperlinkActivatedListener;
import org.jets3t.gui.JHtmlLabel;
import org.jets3t.service.Constants;
import org.jets3t.service.security.AWSCredentials;

public class LoginLocalFolderPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1500994545263522051L;

    private static final Log log = LogFactory.getLog(LoginLocalFolderPanel.class);
    
    private final Insets insetsDefault = new Insets(3, 5, 3, 5);
    
    private Frame ownerFrame = null;
    private HyperlinkActivatedListener hyperlinkListener = null;
    private File jets3tHomeFolder = null;
    
    private JTextField folderPathTextField = null;
    private JTable accountNicknameTable = null;
    private AWSCredentialsFileTableModel nicknamesTableModel = null;
    private JPasswordField passwordPasswordField = null;

    public LoginLocalFolderPanel(Frame ownerFrame, HyperlinkActivatedListener hyperlinkListener) 
    {
        super(new GridBagLayout());
        this.ownerFrame = ownerFrame;
        this.jets3tHomeFolder = Constants.DEFAULT_PREFERENCES_DIRECTORY;
        if (!jets3tHomeFolder.exists()) {
            jets3tHomeFolder.mkdirs();
        }
        this.hyperlinkListener = hyperlinkListener;
        
        initGui();
        findAWSCredentialFiles();
    }
    
    private void initGui() {
        // Textual information.
        String descriptionText = 
            "<html><center>Your AWS Credentials are stored in encrypted files in a folder on " +
            "your computer. Each stored login has a nickname.</center></html>";
        String folderLabelText = 
            "<html><b>Folder</b></html>";
        String folderDescriptionText = 
            "<html><font size=\"-2\">The folder containing your AWS Credentials. By default this will " +
            "be the JetS3t home folder.</font></html>";
        String browseButtonText = 
            "Change Folder";
        String accountNicknameText =
            "<html><b>Stored logins</b></html>";
        String accountNicknameDescriptionText =
            "<html><font size=\"-2\">Nicknames of the login credentials you have stored.</font></html>";
        String passwordLabelText = 
            "<html><b>Password</b></html>";
        String passwordDescriptionText =
            "<html><font size=\"-2\">A password of at least 6 characters.</font></html>";

        // Components.
        JHtmlLabel descriptionLabel = new JHtmlLabel(descriptionText, hyperlinkListener);
        descriptionLabel.setHorizontalAlignment(JLabel.CENTER);        
        JHtmlLabel folderPathLabel = new JHtmlLabel(folderLabelText, hyperlinkListener);
        folderPathTextField = new JTextField(this.jets3tHomeFolder.getAbsolutePath());
        folderPathTextField.setEnabled(false);
        JButton browseButton = new JButton(browseButtonText);
        browseButton.addActionListener(this);
        JHtmlLabel folderPathDescriptionLabel = new JHtmlLabel(folderDescriptionText, hyperlinkListener);
        JHtmlLabel accountNicknamesLabel = new JHtmlLabel(accountNicknameText, hyperlinkListener);
        nicknamesTableModel = new AWSCredentialsFileTableModel();
        accountNicknameTable = new JTable(nicknamesTableModel);
        accountNicknameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountNicknameTable.setShowHorizontalLines(true);
        accountNicknameTable.getTableHeader().setVisible(false);
        JScrollPane accountNicknamesScrollPane = new JScrollPane(accountNicknameTable);
        JHtmlLabel accountNicknamesDescriptionLabel = new JHtmlLabel(accountNicknameDescriptionText, hyperlinkListener);
        JHtmlLabel passwordLabel = new JHtmlLabel(passwordLabelText, hyperlinkListener);
        passwordPasswordField = new JPasswordField();
        JHtmlLabel passwordDescriptionLabel = new JHtmlLabel(passwordDescriptionText, hyperlinkListener);
        
        int row = 0;
        add(descriptionLabel, new GridBagConstraints(0, row++,
            2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsDefault, 0, 0));
        add(folderPathLabel, new GridBagConstraints(0, row++,
            2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        add(folderPathTextField, new GridBagConstraints(0, row,
            1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        add(browseButton, new GridBagConstraints(1, row++,
            1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
        add(folderPathDescriptionLabel, new GridBagConstraints(0, row++,
            2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        add(accountNicknamesLabel, new GridBagConstraints(0, row++,
            2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsDefault, 0, 0));
        add(accountNicknamesScrollPane, new GridBagConstraints(0, row++,
            2, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsDefault, 0, 0));
        add(accountNicknamesDescriptionLabel, new GridBagConstraints(0, row++,
            2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        add(passwordLabel, new GridBagConstraints(0, row++,
            2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        add(passwordPasswordField, new GridBagConstraints(0, row++,
            2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        add(passwordDescriptionLabel, new GridBagConstraints(0, row++,
            2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));        
    }    
    
    public void findAWSCredentialFiles() {
        nicknamesTableModel.removeAll();
        try {
            File[] files = jets3tHomeFolder.listFiles();
            for (int i = 0; files != null && i < files.length; i++) {
                File candidateFile = files[i];
                if (candidateFile.getName().endsWith(".enc")) {
                    // Load partial details from credentials file.
                    AWSCredentials credentials = AWSCredentials.load(null, candidateFile);
                    nicknamesTableModel.addAWSCredentialsFile(
                        credentials, candidateFile);
                }
            }
        } catch (Exception e) {
            String message = "Unable to find AWS Credential files in the folder " 
                + jets3tHomeFolder.getAbsolutePath();
            log.error(message, e);
            ErrorDialog.showDialog(ownerFrame, hyperlinkListener, message, e);
        }
    }
    
    private void chooseFolder() {
        // Prompt user to choose their jets3t home directory.
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setDialogTitle("Choose JetS3t Home Folder");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setApproveButtonText("Choose Folder");
        fileChooser.setCurrentDirectory(jets3tHomeFolder);

        int returnVal = fileChooser.showOpenDialog(ownerFrame);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
        } else {
            this.jets3tHomeFolder = fileChooser.getSelectedFile();
            this.folderPathTextField.setText(this.jets3tHomeFolder.getAbsolutePath());
            findAWSCredentialFiles();
        }
    }
    
    public void actionPerformed(ActionEvent arg0) {
        chooseFolder();
    }

    public File getHomeFolder() {
        return this.jets3tHomeFolder;
    }
    
    public File getAWSCredentialsFile() {
        int selectedNicknameIndex = accountNicknameTable.getSelectedRow();
        if (selectedNicknameIndex < 0) {
            return null;
        }
        return nicknamesTableModel.getAWSCredentialsFile(selectedNicknameIndex);
    }
    
    public String getPassword() {
        return new String(passwordPasswordField.getPassword()); 
    }
        
    private class AWSCredentialsFileTableModel extends DefaultTableModel {
        
        ArrayList awsCredentialsList = new ArrayList();
        ArrayList credentialFileList = new ArrayList();
        
        public AWSCredentialsFileTableModel() {
            super(new String[] {""}, 0);
        }
        
        public int addAWSCredentialsFile(AWSCredentials awsCredentials, File credentialsFile) {
            int insertRow = 
                Collections.binarySearch(awsCredentialsList, awsCredentials, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        String name1 = ((AWSCredentials)o1).getFriendlyName();
                        String name2 = ((AWSCredentials)o2).getFriendlyName();
                        int result =  name1.compareToIgnoreCase(name2);
                        return result;
                    }
                });
            if (insertRow >= 0) {
                // We already have an item with this key, replace it.
                awsCredentialsList.remove(insertRow);
                credentialFileList.remove(insertRow);
                this.removeRow(insertRow);                
            } else {
                insertRow = (-insertRow) - 1;                
            }
            // New object to insert.
            awsCredentialsList.add(insertRow, awsCredentials);
            credentialFileList.add(insertRow, credentialsFile);
            this.insertRow(insertRow, new Object[] {awsCredentials.getFriendlyName()});
            return insertRow;
        }
        
//        public void removeBucket(S3Bucket bucket) {
//            int index = credentialFileList.indexOf(bucket);
//            this.removeRow(index);
//            credentialFileList.remove(bucket);
//        }
        
        public void removeAll() {
            int rowCount = this.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                this.removeRow(0);
            }
            credentialFileList.clear();
            awsCredentialsList.clear();
        }
        
        public File getAWSCredentialsFile(int row) {
            return (File) credentialFileList.get(row);
        }
        
        public boolean isCellEditable(int row, int column) {
            return false;
        }
        
    }
        
}
