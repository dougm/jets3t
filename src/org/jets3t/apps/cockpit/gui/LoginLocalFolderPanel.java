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

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jets3t.gui.HyperlinkActivatedListener;
import org.jets3t.gui.JHtmlLabel;

public class LoginLocalFolderPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1500994545263522051L;

    private final Insets insetsDefault = new Insets(3, 5, 3, 5);
    
    private Frame ownerFrame = null;
    private HyperlinkActivatedListener hyperlinkListener = null;
    private File jets3tHomeFolder = null;
    
    private JTextField folderPathTextField = null;
    private JPasswordField passwordPasswordField = null;

    public LoginLocalFolderPanel(Frame ownerFrame, File jets3tHomeFolder, 
        HyperlinkActivatedListener hyperlinkListener) 
    {
        super(new GridBagLayout());
        this.ownerFrame = ownerFrame;
        this.jets3tHomeFolder = jets3tHomeFolder;
        this.hyperlinkListener = hyperlinkListener;
        
        initGui();
    }
    
    private void initGui() {
        // Textual information.
        String descriptionText = 
            "<html><center>Your AWS Credentials are stored in an encrypted file in a folder available " +
            "from your computer. To access your credentials you must select the folder and enter your " +
            "password.</center></html>";
        String folderLabelText = 
            "<html><b>Folder</b></html>";
        String folderDescriptionText = 
            "<html><font size=\"-2\">The folder containing your AWS Credentials. By default this will " +
            "be your home folder.</font></html>";
        String browseButtonText = 
            "Change Folder";
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
        JHtmlLabel passwordLabel = new JHtmlLabel(passwordLabelText, hyperlinkListener);
        passwordPasswordField = new JPasswordField();
        JHtmlLabel passwordDescriptionLabel = new JHtmlLabel(passwordDescriptionText, hyperlinkListener);
        
        int row = 0;
        add(descriptionLabel, new GridBagConstraints(0, row++,
            1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsDefault, 0, 0));
        add(folderPathLabel, new GridBagConstraints(0, row++,
            1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        add(folderPathTextField, new GridBagConstraints(0, row++,
            1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        add(folderPathDescriptionLabel, new GridBagConstraints(0, row++,
            1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        add(browseButton, new GridBagConstraints(0, row++,
            1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsDefault, 0, 0));
        add(passwordLabel, new GridBagConstraints(0, row++,
            1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        add(passwordPasswordField, new GridBagConstraints(0, row++,
            1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        add(passwordDescriptionLabel, new GridBagConstraints(0, row++,
            2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        
        // Padder.
        add(new JLabel(), new GridBagConstraints(0, row++,
            2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsDefault, 0, 0));
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
        }
    }
    
    public void actionPerformed(ActionEvent arg0) {
        chooseFolder();
    }

    public File getHomeFolder() {
        return this.jets3tHomeFolder;
    }
    
    public String getPassword() {
        return new String(passwordPasswordField.getPassword()); 
    }
        
}
