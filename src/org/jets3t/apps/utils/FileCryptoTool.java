package org.jets3t.apps.utils;
/*
 * jets3t : Java Extra-Tasty S3 Toolkit (for Amazon S3 online storage service)
 * This is a java.net project, see https://jets3t.dev.java.net/
 * 
 * Copyright 2008 James Murty
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * A rudimentary GUI and utility for encrypting and decrypting files in a way
 * that is compatible with JetS3t's cryptographic features.
 * 
 * @author James Murty
 */
public class FileCryptoTool {
    
    private String algorithm = "PBEWithMD5AndDES";
    private SecretKey key = null;
    private AlgorithmParameterSpec algParamSpec = null;

    private int iterationCount = 5000;
    private byte[] salt = {
        (byte)0xA4, (byte)0x0B, (byte)0xC8, (byte)0x34,
        (byte)0xD6, (byte)0x95, (byte)0xF3, (byte)0x13
    };

    static {
        try {
            Class bouncyCastleProviderClass = 
                Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
            if (bouncyCastleProviderClass != null) {
                Provider bouncyCastleProvider = (Provider) bouncyCastleProviderClass
                    .getConstructor(new Class[] {}).newInstance(new Object[] {});                        
                Security.addProvider(bouncyCastleProvider);
            }
        } catch (Exception e) {
            System.err.println("Unable to load security provider BouncyCastleProvider");            
        }
    }

    public FileCryptoTool() {
    }
    
    public void init(String password, String algorithm, byte[] salt, int interationCount) 
        throws NoSuchAlgorithmException, InvalidKeySpecException 
    {
        this.algorithm = algorithm;
        this.salt = salt;
        this.iterationCount = interationCount;
        PBEKeySpec keyspec = new PBEKeySpec(password.toCharArray(), this.salt, this.iterationCount, 32);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm);        
        this.key = skf.generateSecret(keyspec);
        this.algParamSpec = new PBEParameterSpec(this.salt, this.iterationCount);
    }

    public void init(String password, String algorithm) 
        throws NoSuchAlgorithmException, InvalidKeySpecException 
    {
        init(password, algorithm, this.salt, this.iterationCount);
    }
    
    public void decryptFile(File inputFile, File outputFile) throws NoSuchAlgorithmException, 
        NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, 
        IOException 
    {
        Cipher cipher = Cipher.getInstance(this.algorithm);
        cipher.init(Cipher.DECRYPT_MODE, this.key, this.algParamSpec);
        transferFileData(inputFile, outputFile, cipher);
    }

    public void encryptFile(File inputFile, File outputFile) throws NoSuchAlgorithmException, 
        NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, 
        IOException 
    {
        Cipher cipher = Cipher.getInstance(this.algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, this.key, this.algParamSpec);
        transferFileData(inputFile, outputFile, cipher);
    }
    
    protected void transferFileData(File inputFile, File outputFile, Cipher cipher) throws IOException {
        InputStream is = new CipherInputStream(new BufferedInputStream(new FileInputStream(inputFile)), cipher);
        OutputStream os = new BufferedOutputStream(new FileOutputStream(outputFile));
        byte[] buffer = new byte[16384];
        int read = -1;
        while ((read = is.read(buffer)) != -1) {
            os.write(buffer, 0, read);
        }
        os.close();
        is.close();        
    }
    
    protected void initGui(final JFrame frame) {
        final FileCryptoTool self = this; 
        final File[] filesArray = new File[2];
        
        JLabel inputFileLabel = new JLabel("Input file:");
        final JTextField inputFileTextField = new JTextField();
        JButton inputFileBrowseButton = new JButton("Browse");
        inputFileBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                filesArray[0] = fileChooser("Input File", frame, false);                
                if (filesArray[0] != null) {
                    inputFileTextField.setText(filesArray[0].getAbsolutePath());
                } else {
                    inputFileTextField.setText("");
                }
            }
        });
        
        JLabel outputDirectoryLabel = new JLabel("Output directory:");
        final JTextField outputDirectoryTextField = new JTextField();
        JButton outputDirectoryBrowseButton = new JButton("Browse");
        outputDirectoryBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                filesArray[1] = fileChooser("Output Directory", frame, true);                
                if (filesArray[1] != null) {
                    outputDirectoryTextField.setText(filesArray[1].getAbsolutePath());
                } else {
                    outputDirectoryTextField.setText("");
                }
            }
        });
        
        JLabel passwordLabel = new JLabel("Password:");
        final JPasswordField passwordField = new JPasswordField();
        JLabel passwordConfirmLabel = new JLabel("Password (confirm):");
        final JPasswordField password2Field = new JPasswordField();
        
        JLabel algorithmLabel = new JLabel("Algorithm:");
        final JComboBox algorithmComboBox = new JComboBox(listAvailablePbeCiphers());
        algorithmComboBox.setSelectedItem(this.algorithm.toUpperCase());
        algorithmComboBox.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent arg0) {
               self.algorithm = (String) algorithmComboBox.getSelectedItem();
           } 
        });

        final JButton encryptButton = new JButton("Encrypt File");
        final JButton decryptButton = new JButton("Decrypt File");

        ActionListener buttonListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    if (inputFileTextField.getText().trim().length() == 0) {
                        JOptionPane.showMessageDialog(frame, "Choose an input file");
                    }
                    else if (outputDirectoryTextField.getText().trim().length() == 0) {
                        JOptionPane.showMessageDialog(frame, "Choose an output directory");
                    }
                    else if (passwordField.getPassword().length == 0 
                        || password2Field.getPassword().length == 0) 
                    {
                        JOptionPane.showMessageDialog(frame, "Enter your password twice");
                    }
                    else if (!new String(passwordField.getPassword()).equals(new String(password2Field.getPassword()))) 
                    {
                        JOptionPane.showMessageDialog(frame, "Password and confirmation password do not match");
                    }
                    else {
                        File inputFile = new File(inputFileTextField.getText().trim());
                        File outputDirectory = new File(outputDirectoryTextField.getText().trim());                    
                        File outputFile = new File(outputDirectory, inputFile.getName());
                        if (!outputDirectory.isDirectory()) {
                            JOptionPane.showMessageDialog(frame, "The output directory is not valid");                            
                        }
                        else if (inputFile.equals(outputFile)) {
                            JOptionPane.showMessageDialog(frame, 
                                "Choose a different output directory from the one that contains the input file"); 
                        }
                        else {
                            boolean encrypting = event.getSource().equals(encryptButton); 
                            init(new String(passwordField.getPassword()), self.algorithm);                            
                            if (encrypting) {
                                encryptFile(inputFile, outputFile);
                            } else {
                                decryptFile(inputFile, outputFile);                                
                            }
                            JOptionPane.showMessageDialog(frame,  
                                "'" + inputFile.getName() + "' "
                                + (encrypting ? "encrypted" : "decrypted") 
                                + " to: \n'" + outputFile.getAbsolutePath() + "'");                            
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, "ERROR: " + e.getMessage());                    
                }
            } 
        };
        
        encryptButton.addActionListener(buttonListener);
        decryptButton.addActionListener(buttonListener);

        Insets insetsDefault = new Insets(2, 3, 2, 3);

        JPanel inputsPanel = new JPanel(new GridBagLayout());
        inputsPanel.add(inputFileLabel, new GridBagConstraints(0, 0, 
            1, 1, 0, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
        inputsPanel.add(inputFileTextField, new GridBagConstraints(1, 0, 
            1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        inputsPanel.add(inputFileBrowseButton, new GridBagConstraints(2, 0, 
            1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsDefault, 0, 0));
        inputsPanel.add(outputDirectoryLabel, new GridBagConstraints(0, 1, 
            1, 1, 0, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
        inputsPanel.add(outputDirectoryTextField, new GridBagConstraints(1, 1, 
            1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        inputsPanel.add(outputDirectoryBrowseButton, new GridBagConstraints(2, 1, 
            1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsDefault, 0, 0));
        inputsPanel.add(passwordLabel, new GridBagConstraints(0, 2, 
            1, 1, 0, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
        inputsPanel.add(passwordField, new GridBagConstraints(1, 2, 
            1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        inputsPanel.add(passwordConfirmLabel, new GridBagConstraints(0, 3, 
            1, 1, 0, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
        inputsPanel.add(password2Field, new GridBagConstraints(1, 3, 
            1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        inputsPanel.add(algorithmLabel, new GridBagConstraints(0, 4, 
            1, 1, 0, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
        inputsPanel.add(algorithmComboBox, new GridBagConstraints(1, 4, 
            1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));

        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.add(encryptButton, new GridBagConstraints(0, 0, 
            1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insetsDefault, 0, 0));
        buttonsPanel.add(decryptButton, new GridBagConstraints(1, 0, 
            1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));

        frame.getContentPane().setLayout(new GridBagLayout());
        frame.getContentPane().add(inputsPanel, new GridBagConstraints(0, 0, 
            1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsDefault, 0, 0));
        frame.getContentPane().add(buttonsPanel, new GridBagConstraints(0, 1, 
            1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsDefault, 0, 0));
        
        frame.pack();
        frame.setVisible(true);
    }
    
    private File fileChooser(String title, JFrame frame, boolean dirOnly) {
        // Prompt user to choose their Cockpit home directory.
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setDialogTitle(title);
        fileChooser.setFileSelectionMode(
            (dirOnly ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY));
        fileChooser.setApproveButtonText("Select " + 
            (dirOnly ? "Directory" : "File"));

        int returnVal = fileChooser.showOpenDialog(frame);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        } else {
            return fileChooser.getSelectedFile();
        }
    }
    
    private static String[] listAvailablePbeCiphers() {
        Set ciphers = Security.getAlgorithms("Cipher");
        Set pbeCiphers = new HashSet();
        for (Iterator iter = ciphers.iterator(); iter.hasNext(); ) {
            String cipher = (String) iter.next();
            if (cipher.toLowerCase().startsWith("pbe")) {
                pbeCiphers.add(cipher);                    
            }
        }
        return (String[]) pbeCiphers.toArray(new String[pbeCiphers.size()]);           
    }


    public static void main(String[] args) throws Exception {
        FileCryptoTool fct = new FileCryptoTool();
        JFrame frame = new JFrame("File Crypto Tool");
        fct.initGui(frame);
    }
    
}
