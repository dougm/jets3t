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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.jets3t.gui.JHtmlLabel;
import org.jets3t.gui.TableSorter;
import org.jets3t.service.Constants;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.model.S3Owner;

/**
 * Dialog to display detailed information about an S3Bucket or S3Object. The item's details cannot
 * be modified within this dialog.
 * 
 * @author James Murty
 */
public class ItemPropertiesDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = -5001163487616690221L;
    
    private final Insets insetsZero = new Insets(0, 0, 0, 0);
    private final Insets insetsDefault = new Insets(5, 7, 5, 7);
    private final Insets insetsVerticalSpace = new Insets(5, 0, 5, 0);
    
    private S3Object[] objects = null;
    private int currentObjectIndex = 0;
    
    private JTextField objectKeyTF = null;
    private JTextField objectContentTypeTF = null;
    private JTextField objectContentLengthTF = null;
    private JTextField objectLastModifiedTF = null;
    private JTextField objectETagTF = null;
    private JTextField bucketNameTF = null;
    private DefaultTableModel objectMetadataTableModel = null;
    private JLabel currentObjectLabel = null;
    private JButton previousObjectButton = null;
    private JButton nextObjectButton = null;

    private JTextField ownerNameTF = null;
    private JTextField ownerIdTF = null;
    
    private JTextField bucketCreationDateTF = null;

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
    protected ItemPropertiesDialog(Frame owner, String title, boolean isObjectDialog) {
        super(owner, title, true);
        this.initGui(isObjectDialog);
    }

    /**
     * Initialise the GUI elements to display the given item.
     * 
     * @param s3Item
     * the S3Bucket or an S3Object whose details will be displayed
     */
    private void initGui(boolean isObjectBased) {
        this.setResizable(true);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel commonPropertiesContainer = new JPanel(new GridBagLayout());
        JPanel metadataContainer = new JPanel(new GridBagLayout());

        if (!isObjectBased) {
            // Display bucket details.
            commonPropertiesContainer.add(new JLabel("Bucket name:"), new GridBagConstraints(0, 0,
                1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            bucketNameTF = new JTextField();
            bucketNameTF.setEditable(false);
            commonPropertiesContainer.add(bucketNameTF, new GridBagConstraints(1, 0, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            commonPropertiesContainer.add(new JLabel("Creation date:"), new GridBagConstraints(0,
                1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0,
                0));
            bucketCreationDateTF = new JTextField();
            bucketCreationDateTF.setEditable(false);
            commonPropertiesContainer.add(bucketCreationDateTF, new GridBagConstraints(1, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            commonPropertiesContainer.add(new JLabel("Owner Name:"), new GridBagConstraints(0, 2,
                1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            ownerNameTF = new JTextField();
            ownerNameTF.setEditable(false);
            commonPropertiesContainer.add(ownerNameTF, new GridBagConstraints(1, 2, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            commonPropertiesContainer.add(new JLabel("Owner ID:"), new GridBagConstraints(0, 3, 1,
                1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            ownerIdTF = new JTextField();
            ownerIdTF.setEditable(false);
            commonPropertiesContainer.add(ownerIdTF, new GridBagConstraints(1, 3, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        } else {
            // Display object details.
            commonPropertiesContainer.add(new JLabel("Object key:"), new GridBagConstraints(0, 0,
                1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            objectKeyTF = new JTextField();
            objectKeyTF.setEditable(false);
            commonPropertiesContainer.add(objectKeyTF, new GridBagConstraints(1, 0, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            commonPropertiesContainer.add(new JLabel("Content type:"), new GridBagConstraints(0, 1,
                1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            objectContentTypeTF = new JTextField();
            objectContentTypeTF.setEditable(false);
            commonPropertiesContainer.add(objectContentTypeTF, new GridBagConstraints(1, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            commonPropertiesContainer.add(new JLabel("Size:"), new GridBagConstraints(0, 2, 1, 1,
                0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            objectContentLengthTF = new JTextField();
            objectContentLengthTF.setEditable(false);
            commonPropertiesContainer.add(objectContentLengthTF, new GridBagConstraints(1, 2, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            commonPropertiesContainer.add(new JLabel("Last modified:"), new GridBagConstraints(0,
                3, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0,
                0));
            objectLastModifiedTF = new JTextField();
            objectLastModifiedTF.setEditable(false);
            commonPropertiesContainer.add(objectLastModifiedTF, new GridBagConstraints(1, 3, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            commonPropertiesContainer.add(new JLabel("ETag:"), new GridBagConstraints(0, 4, 1, 1,
                0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            objectETagTF = new JTextField();
            objectETagTF.setEditable(false);
            commonPropertiesContainer.add(objectETagTF, new GridBagConstraints(1, 4, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            commonPropertiesContainer.add(new JLabel("Bucket:"), new GridBagConstraints(0, 5, 1, 1,
                0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            bucketNameTF = new JTextField();
            bucketNameTF.setEditable(false);
            commonPropertiesContainer.add(bucketNameTF, new GridBagConstraints(1, 5, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));

            commonPropertiesContainer.add(new JLabel("Owner Name:"), new GridBagConstraints(0, 7,
                1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            ownerNameTF = new JTextField();
            ownerNameTF.setEditable(false);
            commonPropertiesContainer.add(ownerNameTF, new GridBagConstraints(1, 7, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            commonPropertiesContainer.add(new JLabel("Owner ID:"), new GridBagConstraints(0, 8,
                1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0,
                0));
            ownerIdTF = new JTextField();
            ownerIdTF.setEditable(false);
            commonPropertiesContainer.add(ownerIdTF, new GridBagConstraints(1, 8, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));

            // Build metadata table.
            objectMetadataTableModel = new DefaultTableModel(new Object[] {"Name", "Value" }, 0) {
                private static final long serialVersionUID = -3762866886166776851L;

                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            TableSorter metadataTableSorter = new TableSorter(objectMetadataTableModel);            
            JTable metadataTable = new JTable(metadataTableSorter);
            metadataTableSorter.setTableHeader(metadataTable.getTableHeader());
            metadataContainer.add(new JScrollPane(metadataTable), new GridBagConstraints(0, 0, 1,
                1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsDefault, 0, 0));
        }

        // OK Button.
        JButton okButton = new JButton("Finished");
        okButton.setActionCommand("OK");
        okButton.addActionListener(this);
        
        // Set default ENTER button.
        this.getRootPane().setDefaultButton(okButton);        

        // Put it all together.
        int row = 0;
        JPanel container = new JPanel(new GridBagLayout());
        container.add(commonPropertiesContainer, new GridBagConstraints(0, row++, 3, 1, 1, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));
        if (isObjectBased) {
            JHtmlLabel metadataLabel = 
                new JHtmlLabel("<html><b>Metadata</b></html>", null);
            metadataLabel.setHorizontalAlignment(JLabel.CENTER);
            container.add(metadataLabel, new GridBagConstraints(0, row++, 3, 1,
                1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                insetsVerticalSpace, 0, 0));
            container.add(metadataContainer, new GridBagConstraints(0, row++, 3, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsZero, 0, 0));
            
            // Object previous and next buttons, if we have multiple objects.
            previousObjectButton = new JButton("Previous");
            previousObjectButton.addActionListener(this);
            previousObjectButton.setEnabled(false);
            nextObjectButton = new JButton("Next");
            nextObjectButton.addActionListener(this);
            nextObjectButton.setEnabled(false);
            currentObjectLabel = new JLabel();
            currentObjectLabel.setHorizontalAlignment(JLabel.CENTER);
            
            container.add(previousObjectButton, new GridBagConstraints(0, row, 1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsZero, 0, 0));
            container.add(currentObjectLabel, new GridBagConstraints(1, row, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));
            container.add(nextObjectButton, new GridBagConstraints(2, row, 1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, insetsZero, 0, 0));
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
    
    public void displayBucketProperties(S3Bucket bucket) {
        bucketNameTF.setText(bucket.getName());
        bucketCreationDateTF.setText(String.valueOf(bucket.getCreationDate()));

        if (bucket.getOwner() != null) {
            ownerNameTF.setText(bucket.getOwner().getDisplayName());
            ownerIdTF.setText(bucket.getOwner().getId());
        } else {
            ownerNameTF.setText("");
            ownerIdTF.setText("");            
        }        
        
        this.pack();
        this.setLocationRelativeTo(this.getOwner());
    }
    
    public void displayObjectsProperties(S3Object[] objects) {
        this.objects = objects;
        this.currentObjectIndex = 0;
        displayObjectProperties();
    }
    
    private void displayObjectProperties() {
        S3Object object = objects[currentObjectIndex];
        
        // Manage previous/next buttons.
        currentObjectLabel.setText((currentObjectIndex + 1) + " of " + objects.length);
        previousObjectButton.setEnabled(currentObjectIndex > 0);
        nextObjectButton.setEnabled(currentObjectIndex < (objects.length -1));
        
        objectKeyTF.setText(object.getKey());
        objectContentTypeTF.setText(object.getContentType());
        objectContentLengthTF.setText(String.valueOf(object.getContentLength()));
        objectLastModifiedTF.setText(String.valueOf(object.getLastModifiedDate()));
        objectETagTF.setText(object.getETag());
        bucketNameTF.setText(object.getBucketName());

        if (object.getOwner() != null) {
            ownerNameTF.setText(object.getOwner().getDisplayName());
            ownerIdTF.setText(object.getOwner().getId());
        } else {
            ownerNameTF.setText("");
            ownerIdTF.setText("");            
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
    public static void showDialog(Frame owner, S3Bucket bucket) {
        ItemPropertiesDialog dialog = new ItemPropertiesDialog(owner, "Bucket properties", false);
        dialog.displayBucketProperties(bucket);
        dialog.show();
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
    public static void showDialog(Frame owner, S3Object[] objects) {
        ItemPropertiesDialog dialog = new ItemPropertiesDialog(owner, "Object properties", true);
        dialog.displayObjectsProperties(objects);
        dialog.show();
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
            this.hide();
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

        S3Owner owner = new S3Owner("1234567890", "owner_name");

        S3Bucket bucket = new S3Bucket();
        bucket.setName("ExampleBucketName.ThisIs-QuiteALongName");
        bucket.setCreationDate(new Date());
        bucket.setOwner(owner);

        S3Object object1 = new S3Object("src/org/jets3t/apps/cockpit/PropertiesDialog.java");
        object1.setBucketName(bucket.getName());
        object1.setOwner(owner);
        object1.setContentLength(54367);
        object1.setContentType("text/plain");
        object1.setETag("fd43lhg984l4knhohnlg44");
        object1.setLastModifiedDate(new Date());
        object1.addMetadata("sample-metadata", "Valuable");
        object1.addMetadata(Constants.METADATA_JETS3T_CRYPTO_ALGORITHM, "exampleAlgorithmName");

        S3Object object2 = new S3Object("src/org/jets3t/apps/cockpit/ProgressDisplay.java");
        object2.setBucketName(bucket.getName());
        object2.setOwner(owner);
        object2.setContentLength(42534);
        object2.setContentType("text/plain");
        object2.setETag("gs43lhg434l4knhohnlg44");
        object2.setLastModifiedDate(new Date());
        object2.addMetadata("sample-metadata", "Worthless");
        object2.addMetadata(Constants.METADATA_JETS3T_CRYPTO_ALGORITHM, "secondAlgorithmName");
        
        ItemPropertiesDialog.showDialog(f, bucket);
        ItemPropertiesDialog.showDialog(f, new S3Object[] {object1, object2});

        f.dispose();
    }

}
