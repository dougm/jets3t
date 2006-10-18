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

import org.jets3t.service.Constants;
import org.jets3t.service.model.BaseS3Object;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.model.S3Owner;
import org.jets3t.service.security.AWSCredentials;

/**
 * Dialog to display detailed information about an S3Bucket or S3Object. The item's details cannot
 * be modified within this dialog.
 * <p>
 * TODO Currently this dialog can only display details for one item at a time, it would be good if
 * it could cope with multiple S3Objects - displaying details for one with Next/Previous buttons 
 * for cycling through them all. 
 * 
 * @author James Murty
 */
public class PropertiesDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = -5001163487616690221L;
    
    private final Insets insetsZero = new Insets(0, 0, 0, 0);
    private final Insets insetsDefault = new Insets(5, 7, 5, 7);
    private final Insets insetsVerticalSpace = new Insets(5, 0, 5, 0);

    /**
     * Construct a modal dialog displaying details of a bucket.
     * 
     * @param owner
     * the Frame over which the dialog will be displayed and centered
     * @param title
     * a title for the dialog
     * @param bucket
     * the bucket whose details will be displayed
     */
    protected PropertiesDialog(Frame owner, String title, S3Bucket bucket) {
        super(owner, title, true);
        this.initGui(bucket);
    }

    /**
     * Construct a modal dialog displaying details of an object.
     * 
     * @param owner
     * the Frame over which the dialog will be displayed and centered
     * @param title
     * a title for the dialog
     * @param object
     * the object whose details will be displayed
     */
    protected PropertiesDialog(Frame owner, String title, S3Object object) {
        super(owner, title, true);
        this.initGui(object);
    }

    /**
     * Initialise the GUI elements to display the given item.
     * 
     * @param s3Item
     * the S3Bucket or an S3Object whose details will be displayed
     */
    private void initGui(BaseS3Object s3Item) {
        this.setResizable(true);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel commonPropertiesContainer = new JPanel(new GridBagLayout());
        JPanel metadataContainer = new JPanel(new GridBagLayout());

        if (s3Item instanceof S3Bucket) {
            // Display bucket details.
            S3Bucket bucket = (S3Bucket) s3Item;
            commonPropertiesContainer.add(new JLabel("Bucket name:"), new GridBagConstraints(0, 0,
                1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            JTextField bucketNameTF = new JTextField(bucket.getName());
            bucketNameTF.setEditable(false);
            commonPropertiesContainer.add(bucketNameTF, new GridBagConstraints(1, 0, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            commonPropertiesContainer.add(new JLabel("Creation date:"), new GridBagConstraints(0,
                1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0,
                0));
            JTextField creationDateTF = new JTextField(bucket.getCreationDate().toString());
            creationDateTF.setEditable(false);
            commonPropertiesContainer.add(creationDateTF, new GridBagConstraints(1, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            commonPropertiesContainer.add(new JLabel("Owner Name:"), new GridBagConstraints(0, 2,
                1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            JTextField ownerNameTF = new JTextField(bucket.getOwner().getDisplayName());
            ownerNameTF.setEditable(false);
            commonPropertiesContainer.add(ownerNameTF, new GridBagConstraints(1, 2, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            commonPropertiesContainer.add(new JLabel("Owner ID:"), new GridBagConstraints(0, 3, 1,
                1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            JTextField ownerIdTF = new JTextField(bucket.getOwner().getId());
            ownerIdTF.setEditable(false);
            commonPropertiesContainer.add(ownerIdTF, new GridBagConstraints(1, 3, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        } else {
            // Display object details.
            S3Object object = (S3Object) s3Item;
            commonPropertiesContainer.add(new JLabel("Object key:"), new GridBagConstraints(0, 0,
                1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            JTextField ketTF = new JTextField(object.getKey());
            ketTF.setEditable(false);
            commonPropertiesContainer.add(ketTF, new GridBagConstraints(1, 0, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            commonPropertiesContainer.add(new JLabel("Content type:"), new GridBagConstraints(0, 1,
                1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            JTextField contentTypeTF = new JTextField(object.getContentType());
            contentTypeTF.setEditable(false);
            commonPropertiesContainer.add(contentTypeTF, new GridBagConstraints(1, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            commonPropertiesContainer.add(new JLabel("Size:"), new GridBagConstraints(0, 2, 1, 1,
                0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            JTextField contentLengthTF = new JTextField(String.valueOf(object.getContentLength()));
            contentLengthTF.setEditable(false);
            commonPropertiesContainer.add(contentLengthTF, new GridBagConstraints(1, 2, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            commonPropertiesContainer.add(new JLabel("Last modified:"), new GridBagConstraints(0,
                3, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0,
                0));
            JTextField lastModifiedTF = new JTextField(String.valueOf(object.getLastModifiedDate()
                .toString()));
            lastModifiedTF.setEditable(false);
            commonPropertiesContainer.add(lastModifiedTF, new GridBagConstraints(1, 3, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            commonPropertiesContainer.add(new JLabel("ETag:"), new GridBagConstraints(0, 4, 1, 1,
                0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            JTextField hashTF = new JTextField(object.getETag());
            hashTF.setEditable(false);
            commonPropertiesContainer.add(hashTF, new GridBagConstraints(1, 4, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            commonPropertiesContainer.add(new JLabel("Bucket:"), new GridBagConstraints(0, 5, 1, 1,
                0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            JTextField bucketNameTF = new JTextField(object.getBucketName());
            bucketNameTF.setEditable(false);
            commonPropertiesContainer.add(bucketNameTF, new GridBagConstraints(1, 5, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            commonPropertiesContainer.add(new JLabel("Storage class:"), new GridBagConstraints(0,
                6, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0,
                0));
            JTextField storageClassTF = new JTextField(object.getStorageClass());
            storageClassTF.setEditable(false);
            commonPropertiesContainer.add(storageClassTF, new GridBagConstraints(1, 6, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            commonPropertiesContainer.add(new JLabel("Owner Name:"), new GridBagConstraints(0, 7,
                1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            if (object.getOwner() != null) {
                JTextField ownerNameTF = new JTextField(object.getOwner().getDisplayName());
                ownerNameTF.setEditable(false);
                commonPropertiesContainer.add(ownerNameTF, new GridBagConstraints(1, 7, 1, 1, 1, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
                commonPropertiesContainer.add(new JLabel("Owner ID:"), new GridBagConstraints(0, 8,
                    1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0,
                    0));
                JTextField ownerIdTF = new JTextField(object.getOwner().getId());
                ownerIdTF.setEditable(false);
                commonPropertiesContainer.add(ownerIdTF, new GridBagConstraints(1, 8, 1, 1, 1, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            }

            // Build metadata table contents.

            // Remove the metadata items already displayed above.
            Map objectMetadata = new HashMap(object.getMetadata());
            objectMetadata.remove(S3Object.METADATA_HEADER_CONTENT_LENGTH);
            objectMetadata.remove(S3Object.METADATA_HEADER_CONTENT_TYPE);
            objectMetadata.remove(S3Object.METADATA_HEADER_DATE);
            objectMetadata.remove(S3Object.METADATA_HEADER_ETAG);
            objectMetadata.remove(S3Object.METADATA_HEADER_LAST_MODIFIED_DATE);
            objectMetadata.remove(S3Object.METADATA_HEADER_STORAGE_CLASS);
            objectMetadata.remove(S3Object.METADATA_HEADER_OWNER);

            // Display remaining metadata items in the table.
            Object[][] metadata = new Object[objectMetadata.keySet().size()][2];
            int rowIndex = 0;
            Iterator mdRowIter = objectMetadata.keySet().iterator();
            while (mdRowIter.hasNext()) {
                Object name = mdRowIter.next();
                Object value = objectMetadata.get(name);
                metadata[rowIndex][0] = name;
                metadata[rowIndex][1] = value;
                rowIndex++;
            }

            JTable metadataTable = new JTable(new DefaultTableModel(metadata, 
                new Object[] {"Name", "Value" }) {
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            metadataContainer.add(new JScrollPane(metadataTable), new GridBagConstraints(0, 0, 1,
                1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsDefault, 0, 0));
        }

        // OK Button.
        JButton okButton = new JButton("OK");
        okButton.setActionCommand("OK");
        okButton.addActionListener(this);

        // Put it all together.
        JPanel container = new JPanel(new GridBagLayout());
        container.add(commonPropertiesContainer, new GridBagConstraints(0, 0, 1, 1, 1, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));
        if (s3Item instanceof S3Object) {
            container.add(new JLabel("Metadata", JLabel.CENTER), new GridBagConstraints(0, 1, 1, 1,
                1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                insetsVerticalSpace, 0, 0));
            container.add(metadataContainer, new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsZero, 0, 0));
        }
        container.add(okButton, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.CENTER,
            GridBagConstraints.NONE, insetsZero, 0, 0));
        this.getContentPane().add(container);

        this.pack();
        if (s3Item instanceof S3Object) {
            this.setSize(this.getWidth(), 460);
        }
        this.setLocationRelativeTo(this.getOwner());
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
        PropertiesDialog dialog = new PropertiesDialog(owner, "Bucket properties", bucket);
        dialog.show();
        dialog.dispose();
    }

    /**
     * Displays a dialog showing the detailed properties of an object, which will remain until the user
     * dismisses the dialog.
     * 
     * @param owner
     * the Frame over which the dialog will be displayed and centered
     * @param object the object whose details will be displayed
     */
    public static void showDialog(Frame owner, S3Object object) {
        PropertiesDialog dialog = new PropertiesDialog(owner, "Object properties", object);
        dialog.show();
        dialog.dispose();
    }

    /**
     * Event handler for this dialog.
     */
    public void actionPerformed(ActionEvent e) {
        if ("OK".equals(e.getActionCommand())) {
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

        AWSCredentials awsCredentials = null;

        S3Owner owner = new S3Owner("1234567890", "owner_name");

        S3Bucket bucket = new S3Bucket();
        bucket.setName("ExampleBucketName.ThisIs-QuiteALongName");
        bucket.setCreationDate(new Date());
        bucket.setOwner(owner);

        S3Object object = new S3Object();
        object.setBucketName(bucket.getName());
        object.setOwner(owner);
        object.setKey("src/org/jets3t/apps/cockpit/PropertiesDialog.java");
        object.setContentLength(54367);
        object.setContentType("text/plain");
        object.setETag("fd43lhg984l4knhohnlg44");
        object.setLastModifiedDate(new Date());
        object.setStorageClass("STANDARD");
        object.addMetadata("sample-metadata", "Valuable");
        object.addMetadata(Constants.METADATA_JETS3T_CRYPTO_ALGORITHM, "exampleAlgorithmName");

        PropertiesDialog.showDialog(f, bucket);
        PropertiesDialog.showDialog(f, object);

        f.dispose();
    }

}
