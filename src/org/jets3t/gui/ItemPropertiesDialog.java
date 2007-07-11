/*
 * jets3t : Java Extra-Tasty S3 Toolkit (for Amazon S3 online storage service)
 * This is a java.net project, see https://jets3t.dev.java.net/
 * 
 * Copyright 2007 James Murty
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
package org.jets3t.gui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.gui.skins.SkinsFactory;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;

/**
 * Dialog to display detailed information about an {@link S3Bucket} or a set of {@link S3Object}s. 
 * The item's details cannot be modified within this dialog.
 * 
 * @author James Murty
 */
public class ItemPropertiesDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = -5701587170000431985L;

    private static final Log log = LogFactory.getLog(ItemPropertiesDialog.class);
    
    private Properties applicationProperties = null;
    private SkinsFactory skinsFactory = null;

    private final Insets insetsZero = new Insets(0, 0, 0, 0);
    private final Insets insetsDefault = new Insets(5, 7, 5, 7);
    private final Insets insetsVerticalSpace = new Insets(5, 0, 5, 0);
    
    private S3Object[] objects = null;
    private int currentObjectIndex = 0;
    
    private JTextField objectKeyTextField = null;
    private JTextField objectContentTypeTextField = null;
    private JTextField objectContentLengthTextField = null;
    private JTextField objectLastModifiedTextField = null;
    private JTextField objectETagTextField = null;
    private JTextField bucketNameTextField = null;
    private DefaultTableModel objectMetadataTableModel = null;
    private JLabel ownerNameLabel = null;
    private JLabel ownerIdLabel = null;
    private JLabel currentObjectLabel = null;
    private JButton previousObjectButton = null;
    private JButton nextObjectButton = null;
    private JPanel nextPreviousPanel = null;

    private JTextField ownerNameTextField = null;
    private JTextField ownerIdTextField = null;
    
    private JTextField bucketCreationDateTextField = null;

    /**
     * Construct a modal dialog displaying details of a bucket or object.
     * 
     * @param owner
     * the Frame over which the dialog will be displayed and centered
     * @param title
     * a title for the dialog
     * @param isObjectDialog
     * if true an object-specific dialog will be displayed, otherwise a bucket-specific dialog.
     */
    protected ItemPropertiesDialog(Frame owner, String title, boolean isObjectDialog,
        Properties applicationProperties) 
    {
        super(owner, title, true);
        this.applicationProperties = applicationProperties;
        this.initGui(isObjectDialog);
    }

    /**
     * Initialise the GUI elements to display the given item.
     * 
     * @param s3Item
     * the S3Bucket or an S3Object whose details will be displayed
     */
    private void initGui(boolean isObjectBased) {
        // Initialise skins factory. 
        skinsFactory = SkinsFactory.getInstance(applicationProperties); 
        
        // Set Skinned Look and Feel.
        LookAndFeel lookAndFeel = skinsFactory.createSkinnedMetalTheme("SkinnedLookAndFeel");        
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (UnsupportedLookAndFeelException e) {
            log.error("Unable to set skinned LookAndFeel", e);
        }       
        
        this.setResizable(true);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel commonPropertiesContainer = skinsFactory.createSkinnedJPanel("ItemPropertiesCommonPanel");
        commonPropertiesContainer.setLayout(new GridBagLayout());
        JPanel metadataContainer = skinsFactory.createSkinnedJPanel("ItemPropertiesMetadataPanel");
        metadataContainer.setLayout(new GridBagLayout());

        if (!isObjectBased) {
            // Display bucket details.
            JLabel bucketNameLabel = skinsFactory.createSkinnedJHtmlLabel("BucketNameLabel");
            bucketNameLabel.setText("Bucket name:");
            bucketNameTextField = skinsFactory.createSkinnedJTextField("BucketNameTextField");
            bucketNameTextField.setEditable(false);
            JLabel bucketCreationDateLabel = skinsFactory.createSkinnedJHtmlLabel("BucketCreationDateLabel");
            bucketCreationDateLabel.setText("Creation date:");
            bucketCreationDateTextField = skinsFactory.createSkinnedJTextField("BucketCreationDateTextField");
            bucketCreationDateTextField.setEditable(false);
            ownerNameLabel = skinsFactory.createSkinnedJHtmlLabel("OwnerNameLabel");
            ownerNameLabel.setText("Owner name:");
            ownerNameTextField = skinsFactory.createSkinnedJTextField("OwnerNameTextField");
            ownerNameTextField.setEditable(false);
            ownerIdLabel = skinsFactory.createSkinnedJHtmlLabel("OwnerIdLabel");
            ownerIdLabel.setText("Owner ID:");
            ownerIdTextField = skinsFactory.createSkinnedJTextField("OwnerIdTextField");
            ownerIdTextField.setEditable(false);
            
            commonPropertiesContainer.add(bucketNameLabel, new GridBagConstraints(0, 0,
                1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            commonPropertiesContainer.add(bucketNameTextField, new GridBagConstraints(1, 0, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            
            commonPropertiesContainer.add(bucketCreationDateLabel, new GridBagConstraints(0,
                1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0,
                0));
            commonPropertiesContainer.add(bucketCreationDateTextField, new GridBagConstraints(1, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            
            commonPropertiesContainer.add(ownerNameLabel, new GridBagConstraints(0, 2,
                1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            commonPropertiesContainer.add(ownerNameTextField, new GridBagConstraints(1, 2, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            
            commonPropertiesContainer.add(ownerIdLabel, new GridBagConstraints(0, 3, 1,
                1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            commonPropertiesContainer.add(ownerIdTextField, new GridBagConstraints(1, 3, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        } else {
            // Display object details.
            JLabel objectKeyLabel = skinsFactory.createSkinnedJHtmlLabel("ObjectKeyLabel");
            objectKeyLabel.setText("Object key:");
            objectKeyTextField = skinsFactory.createSkinnedJTextField("ObjectKeyTextField");
            objectKeyTextField.setEditable(false);
            JLabel objectContentTypeLabel = skinsFactory.createSkinnedJHtmlLabel("ContentTypeLabel");
            objectContentTypeLabel.setText("Content type:");
            objectContentTypeTextField = skinsFactory.createSkinnedJTextField("ContentTypeTextField");
            objectContentTypeTextField.setEditable(false);            
            JLabel objectContentLengthLabel = skinsFactory.createSkinnedJHtmlLabel("ObjectContentLengthLabel");
            objectContentLengthLabel.setText("Size:");
            objectContentLengthTextField = skinsFactory.createSkinnedJTextField("ObjectContentLengthTextField");
            objectContentLengthTextField.setEditable(false);            
            JLabel objectLastModifiedLabel = skinsFactory.createSkinnedJHtmlLabel("ObjectLastModifiedLabel");
            objectLastModifiedLabel.setText("Last modified:");
            objectLastModifiedTextField = skinsFactory.createSkinnedJTextField("ObjectLastModifiedTextField");
            objectLastModifiedTextField.setEditable(false);
            JLabel objectETagLabel = skinsFactory.createSkinnedJHtmlLabel("ObjectETagLabel");
            objectETagLabel.setText("ETag:");
            objectETagTextField = skinsFactory.createSkinnedJTextField("ObjectETagTextField");
            objectETagTextField.setEditable(false);
            JLabel bucketNameLabel = skinsFactory.createSkinnedJHtmlLabel("BucketNameLabel");
            bucketNameLabel.setText("Bucket name:");
            bucketNameTextField = skinsFactory.createSkinnedJTextField("BucketNameTextField");
            bucketNameTextField.setEditable(false);
            ownerNameLabel = skinsFactory.createSkinnedJHtmlLabel("OwnerNameLabel");
            ownerNameLabel.setText("Owner name:");
            ownerNameTextField = skinsFactory.createSkinnedJTextField("OwnerNameTextField");
            ownerNameTextField.setEditable(false);
            ownerIdLabel = skinsFactory.createSkinnedJHtmlLabel("OwnerIdLabel");
            ownerIdLabel.setText("Owner ID:");
            ownerIdTextField = skinsFactory.createSkinnedJTextField("OwnerIdTextField");
            ownerIdTextField.setEditable(false);

            
            
            commonPropertiesContainer.add(objectKeyLabel, new GridBagConstraints(0, 0,
                1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            commonPropertiesContainer.add(objectKeyTextField, new GridBagConstraints(1, 0, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            
            commonPropertiesContainer.add(objectContentTypeLabel, new GridBagConstraints(0, 1,
                1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            commonPropertiesContainer.add(objectContentTypeTextField, new GridBagConstraints(1, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            
            commonPropertiesContainer.add(objectContentLengthLabel, new GridBagConstraints(0, 2, 1, 1,
                0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            commonPropertiesContainer.add(objectContentLengthTextField, new GridBagConstraints(1, 2, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            
            commonPropertiesContainer.add(objectLastModifiedLabel, new GridBagConstraints(0,
                3, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            commonPropertiesContainer.add(objectLastModifiedTextField, new GridBagConstraints(1, 3, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            
            commonPropertiesContainer.add(objectETagLabel, new GridBagConstraints(0, 4, 1, 1,
                0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            commonPropertiesContainer.add(objectETagTextField, new GridBagConstraints(1, 4, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            
            commonPropertiesContainer.add(bucketNameLabel, new GridBagConstraints(0, 5, 1, 1,
                0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            commonPropertiesContainer.add(bucketNameTextField, new GridBagConstraints(1, 5, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));

            commonPropertiesContainer.add(ownerNameLabel, new GridBagConstraints(0, 7,
                1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            commonPropertiesContainer.add(ownerNameTextField, new GridBagConstraints(1, 7, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            
            commonPropertiesContainer.add(ownerIdLabel, new GridBagConstraints(0, 8,
                1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0,
                0));
            commonPropertiesContainer.add(ownerIdTextField, new GridBagConstraints(1, 8, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));

            // Build metadata table.
            objectMetadataTableModel = new DefaultTableModel(new Object[] {"Name", "Value" }, 0) {
                private static final long serialVersionUID = -3762866886166776851L;

                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            TableSorter metadataTableSorter = new TableSorter(objectMetadataTableModel);            
            JTable metadataTable = skinsFactory.createSkinnedJTable("MetadataTable");
            metadataTable.setModel(metadataTableSorter);
            metadataTableSorter.setTableHeader(metadataTable.getTableHeader());
            metadataContainer.add(new JScrollPane(metadataTable), new GridBagConstraints(0, 0, 1,
                1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsDefault, 0, 0));
        }

        // OK Button.
        JButton okButton = skinsFactory.createSkinnedJButton("ItemPropertiesOKButton");
        okButton.setText("Finished");
        okButton.setActionCommand("OK");
        okButton.addActionListener(this);
        
        // Set default ENTER button.
        this.getRootPane().setDefaultButton(okButton);        

        // Put it all together.
        int row = 0;
        JPanel container = skinsFactory.createSkinnedJPanel("ItemPropertiesPanel");
        container.setLayout(new GridBagLayout());
        container.add(commonPropertiesContainer, new GridBagConstraints(0, row++, 3, 1, 1, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));
        if (isObjectBased) {
            JHtmlLabel metadataLabel = skinsFactory.createSkinnedJHtmlLabel("MetadataLabel");
            metadataLabel.setText("<html><b>Metadata</b></html>");
            metadataLabel.setHorizontalAlignment(JLabel.CENTER);
            container.add(metadataLabel, new GridBagConstraints(0, row++, 3, 1,
                1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                insetsVerticalSpace, 0, 0));
            container.add(metadataContainer, new GridBagConstraints(0, row++, 3, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsZero, 0, 0));
            
            // Object previous and next buttons, if we have multiple objects.
            previousObjectButton = skinsFactory.createSkinnedJButton("ItemPropertiesPreviousButton");
            previousObjectButton.setText("Previous");
            previousObjectButton.addActionListener(this);
            previousObjectButton.setEnabled(false);
            nextObjectButton = skinsFactory.createSkinnedJButton("ItemPropertiesNextButton");
            nextObjectButton.setText("Next");
            nextObjectButton.addActionListener(this);
            nextObjectButton.setEnabled(false);
            currentObjectLabel = skinsFactory.createSkinnedJHtmlLabel("ItemPropertiesCurrentObjectLabel"); 
            currentObjectLabel.setHorizontalAlignment(JLabel.CENTER);
            
            nextPreviousPanel = skinsFactory.createSkinnedJPanel("ItemPropertiesNextPreviousPanel");
            nextPreviousPanel.setLayout(new GridBagLayout());
            nextPreviousPanel.add(previousObjectButton, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsZero, 0, 0));
            nextPreviousPanel.add(currentObjectLabel, new GridBagConstraints(1, 0, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));
            nextPreviousPanel.add(nextObjectButton, new GridBagConstraints(2, 0, 1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, insetsZero, 0, 0));

            container.add(nextPreviousPanel, new GridBagConstraints(0, row, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));
            row++;
        }
        container.add(okButton, new GridBagConstraints(0, row++, 3, 1, 0, 0, GridBagConstraints.CENTER,
            GridBagConstraints.NONE, insetsZero, 0, 0));
        this.getContentPane().add(container);

        this.pack();
        if (isObjectBased) {
            this.setSize(this.getWidth(), 500);
        }
        this.setLocationRelativeTo(this.getOwner());
    }
    
    private void displayBucketProperties(S3Bucket bucket) {
        bucketNameTextField.setText(bucket.getName());
        bucketCreationDateTextField.setText(String.valueOf(bucket.getCreationDate()));

        if (bucket.getOwner() != null) {
            ownerNameLabel.setVisible(true);
            ownerNameTextField.setVisible(true);
            ownerIdLabel.setVisible(true);
            ownerIdTextField.setVisible(true);            
            ownerNameTextField.setText(bucket.getOwner().getDisplayName());
            ownerIdTextField.setText(bucket.getOwner().getId());
        } else {
            ownerNameLabel.setVisible(false);
            ownerNameTextField.setVisible(false);
            ownerIdLabel.setVisible(false);
            ownerIdTextField.setVisible(false);            
        }        
        
        this.pack();
        this.setLocationRelativeTo(this.getOwner());
    }
    
    private void displayObjectsProperties(S3Object[] objects) {
        this.objects = objects;
        this.currentObjectIndex = 0;
        displayObjectProperties();
    }
    
    private void displayObjectProperties() {
        S3Object object = objects[currentObjectIndex];
        
        // Manage previous/next buttons.
        if (objects.length > 1) {
            nextPreviousPanel.setVisible(true);
            currentObjectLabel.setText((currentObjectIndex + 1) + " of " + objects.length);
            previousObjectButton.setEnabled(currentObjectIndex > 0);
            nextObjectButton.setEnabled(currentObjectIndex < (objects.length -1));
        } else {
            nextPreviousPanel.setVisible(false);            
        }
        
        objectKeyTextField.setText(object.getKey());
        objectContentTypeTextField.setText(object.getContentType());
        objectContentLengthTextField.setText(String.valueOf(object.getContentLength()));
        objectLastModifiedTextField.setText(String.valueOf(object.getLastModifiedDate()));
        objectETagTextField.setText(object.getETag());
        bucketNameTextField.setText(object.getBucketName());

        if (object.getOwner() != null) {
            ownerNameLabel.setVisible(true);
            ownerNameTextField.setVisible(true);
            ownerIdLabel.setVisible(true);
            ownerIdTextField.setVisible(true);            
            ownerNameTextField.setText(object.getOwner().getDisplayName());
            ownerIdTextField.setText(object.getOwner().getId());
        } else {
            ownerNameLabel.setVisible(false);
            ownerNameTextField.setVisible(false);
            ownerIdLabel.setVisible(false);
            ownerIdTextField.setVisible(false);            
        }
        
        // Clear old table contents
        while (objectMetadataTableModel.getRowCount() > 0) {
            objectMetadataTableModel.removeRow(0);
        }        

        // Remove the metadata items already displayed.
        Map objectMetadata = new HashMap(object.getMetadataMap());
        objectMetadata.remove(S3Object.METADATA_HEADER_CONTENT_LENGTH);
        objectMetadata.remove(S3Object.METADATA_HEADER_CONTENT_TYPE);
        objectMetadata.remove(S3Object.METADATA_HEADER_DATE);
        objectMetadata.remove(S3Object.METADATA_HEADER_ETAG);
        objectMetadata.remove(S3Object.METADATA_HEADER_LAST_MODIFIED_DATE);
        objectMetadata.remove(S3Object.METADATA_HEADER_OWNER);

        // Display remaining metadata items in the table.        
        Iterator mdIter = objectMetadata.entrySet().iterator();
        while (mdIter.hasNext()) {
            Map.Entry entry = (Map.Entry) mdIter.next();
            Object name = entry.getKey();
            Object value = entry.getValue();
            objectMetadataTableModel.addRow(new Object[] {name, value});
        }
    }

    /**
     * Displays a dialog showing the detailed properties of a bucket, which will remain until the user
     * dismisses the dialog.
     *  
     * @param owner
     * the Frame over which the dialog will be displayed and centered
     * @param bucket the bucket whose details will be displayed
     */
    public static void showDialog(Frame owner, S3Bucket bucket, Properties applicationProperties) {
        ItemPropertiesDialog dialog = 
            new ItemPropertiesDialog(owner, "Bucket properties", false, applicationProperties);
        dialog.displayBucketProperties(bucket);
        dialog.setVisible(true);
        dialog.dispose();
    }

    /**
     * Displays a dialog showing the detailed properties of an object, which will remain until the user
     * dismisses the dialog.
     * 
     * @param owner
     * the Frame over which the dialog will be displayed and centered
     * @param objects
     * the object whose details will be displayed
     */
    public static void showDialog(Frame owner, S3Object[] objects, Properties applicationProperties) {
        ItemPropertiesDialog dialog = 
            new ItemPropertiesDialog(owner, "Object properties", true, applicationProperties);
        dialog.displayObjectsProperties(objects);
        dialog.setVisible(true);
        dialog.dispose();
    }

    /**
     * Event handler for this dialog.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(nextObjectButton)) {
            currentObjectIndex++;
            displayObjectProperties();
        } else if (e.getSource().equals(previousObjectButton)) {
            currentObjectIndex--;
            displayObjectProperties();
        } else if ("OK".equals(e.getActionCommand())) {
            this.setVisible(false);
        }
    }

}
