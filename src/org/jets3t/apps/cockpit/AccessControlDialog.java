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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.CanonicalGrantee;
import org.jets3t.service.acl.EmailAddressGrantee;
import org.jets3t.service.acl.GrantAndPermission;
import org.jets3t.service.acl.GranteeInterface;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.model.BaseS3Object;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.model.S3Owner;

/**
 * Dialog for managing S3 access control settings for buckets and objects.
 * <p>
 * All S3 group types are supported:
 * <ul>
 * <li>Canonical Users</li>
 * <li>Groups: All Users, and Authenticated Users</li>
 * <li>Users identified by Email address</li>
 * </ul>  
 * <p>
 * The following access permissions are supported:
 * <ul>
 * <li>READ</li>
 * <li>WRITE</li>
 * <li>READ_ACP</li>
 * <li>WRITE_ACP</li>
 * <li>FULL_CONTROL</li>
 * </ul>
 *  
 * @author James Murty
 */
public class AccessControlDialog extends JDialog implements ActionListener {
	private static AccessControlDialog accessControlDialog = null;
	
	private AccessControlList originalAccessControlList = null;
	private AccessControlList updatedAccessControlList = null;
	
	private JLabel itemsDescription = null;
	private JTable canonicalGranteeTable = null;	
	private GranteeTableModel canonicalGranteeTableModel = null;
	private JTable emailGranteeTable = null;	
	private GranteeTableModel emailGranteeTableModel = null;
	private JTable groupGranteeTable = null;	
	private GranteeTableModel groupGranteeTableModel = null;
	
    /**
     * The set of access permission values.
     */
	private final JComboBox permissionComboBox = new JComboBox(new Permission[] {
		Permission.PERMISSION_READ,
		Permission.PERMISSION_WRITE,
		Permission.PERMISSION_FULL_CONTROL,
		Permission.PERMISSION_READ_ACP,
		Permission.PERMISSION_WRITE_ACP
		});
	
    /**
     * The set of groups.
     */
	private final JComboBox groupGranteeComboBox = new JComboBox(new GroupGrantee[] {
		GroupGrantee.ALL_USERS,
		GroupGrantee.AUTHENTICATED_USERS
		});
	
	private final Insets insetsZero = new Insets(0, 0, 0, 0);
	private final Insets insetsDefault = new Insets(5, 7, 5, 7);
	private final Insets insetsTable = new Insets(5, 7, 0, 7);
	private final Insets insetsAddRemoveButtons = new Insets(0, 7, 5, 7);

	/**
     * Creates a modal dialog box with a title.
     *  
     * @param owner the frame within which this dialog will be displayed and centred.
	 */
	protected AccessControlDialog(Frame owner) {
		super(owner, "Update Access Control List Permissions", true);
		initGui();
	}
	
    /**
     * Initialises the dialog with access control information for the given S3 items (bucket or objects)
     * 
     * @param s3Items   May be a single <code>S3Bucket</code>, or one or more <code>S3Object</code>s
     * @param accessControlList the initial ACL settings to represent in the dialog.
     */
	protected void initData(BaseS3Object[] s3Items, AccessControlList accessControlList) {
		this.originalAccessControlList = accessControlList;

		// Item(s) description.
		if (s3Items.length > 1) {
			// Only objects can be updated in multiples, buckets are always single.
			itemsDescription.setText("Items: " + s3Items.length + " objects");
		} else {
			if (s3Items[0] instanceof S3Bucket) {
				itemsDescription.setText("Bucket: " + ((S3Bucket)s3Items[0]).getName());
			} else {
				itemsDescription.setText("Object: " + ((S3Object)s3Items[0]).getKey());				
			}
		}		
		
		// Populate grantees tables.
		canonicalGranteeTableModel.removeAllGrantAndPermissions();
		emailGranteeTableModel.removeAllGrantAndPermissions();
		groupGranteeTableModel.removeAllGrantAndPermissions();

		Iterator grantIter = originalAccessControlList.getGrants().iterator();
		while (grantIter.hasNext()) {
			GrantAndPermission gap = (GrantAndPermission) grantIter.next();
			GranteeInterface grantee = gap.getGrantee();
			Permission permission = gap.getPermission();
			if (grantee instanceof CanonicalGrantee) {
				canonicalGranteeTableModel.addGrantee(grantee, permission);
			} else if (grantee instanceof EmailAddressGrantee) {
				emailGranteeTableModel.addGrantee(grantee, permission);
			} else if (grantee instanceof GroupGrantee) {
				groupGranteeTableModel.addGrantee(grantee, permission);
			}			
		}		
	}
	
    /**
     * Initialises all GUI elements.
     */
	protected void initGui() {
		this.setResizable(true);
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

		// Canonical Grantee Table and add/remove buttons.
		canonicalGranteeTableModel = new GranteeTableModel(CanonicalGrantee.class);
		canonicalGranteeTable = new GranteeTable(canonicalGranteeTableModel);
		JButton removeCanonical = new JButton("-");
		removeCanonical.addActionListener(this);
		removeCanonical.setActionCommand("removeCanonicalGrantee");
		JButton addCanonical = new JButton("+");
		addCanonical.setActionCommand("addCanonicalGrantee");
		addCanonical.addActionListener(this);

		// Email Address Grantee Table and add/remove buttons.
		emailGranteeTableModel = new GranteeTableModel(EmailAddressGrantee.class);
		emailGranteeTable = new GranteeTable(emailGranteeTableModel);
		JButton removeEmail = new JButton("-");
		removeEmail.setActionCommand("removeEmailGrantee");
		removeEmail.addActionListener(this);
		JButton addEmail = new JButton("+");
		addEmail.setActionCommand("addEmailGrantee");
		addEmail.addActionListener(this);

		// Group grantee table and add/remove buttons.
		groupGranteeTableModel = new GranteeTableModel(GroupGrantee.class);
		groupGranteeTable = new GranteeTable(groupGranteeTableModel);
		JButton removeGroup = new JButton("-");
		removeGroup.setActionCommand("removeGroupGrantee");
		removeGroup.addActionListener(this);
		JButton addGroup = new JButton("+");
		addGroup.setActionCommand("addGroupGrantee");
		addGroup.addActionListener(this);

		// Action buttons.
		JPanel buttonsContainer = new JPanel(new GridBagLayout());
		JButton cancel = new JButton("Cancel Permission Changes");
		cancel.addActionListener(this);
		cancel.setActionCommand("Cancel");
		JButton ok = new JButton("Save Permission Changes");
		ok.setActionCommand("OK");
		ok.addActionListener(this);
			
		// Overall container.		
		JPanel container = new JPanel(new GridBagLayout());

		itemsDescription = new JLabel();
		container.add(itemsDescription, //itemBorderPanel, 
			new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));

		JPanel canonicalAddRemovePanel = new JPanel();
		canonicalAddRemovePanel.add(removeCanonical);
		canonicalAddRemovePanel.add(addCanonical);
		container.add(new JScrollPane(canonicalGranteeTable),  
			new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsTable, 0, 0));
		container.add(canonicalAddRemovePanel,
			new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsAddRemoveButtons, 0, 0));

		JPanel groupAddRemovePanel = new JPanel();
		groupAddRemovePanel.add(removeGroup);
		groupAddRemovePanel.add(addGroup);
		container.add(new JScrollPane(groupGranteeTable),  
			new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsTable, 0, 0));
		container.add(groupAddRemovePanel,
			new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsAddRemoveButtons, 0, 0));
		
		JPanel emailAddRemovePanel = new JPanel();
		emailAddRemovePanel.add(removeEmail);
		emailAddRemovePanel.add(addEmail);
		container.add(new JScrollPane(emailGranteeTable),  
			new GridBagConstraints(0, 5, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsTable, 0, 0));
		container.add(emailAddRemovePanel,
			new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsAddRemoveButtons, 0, 0));

		buttonsContainer.add(cancel, 
			new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsZero, 0, 0)); 
		buttonsContainer.add(ok, 
			new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsZero, 0, 0)); 
		
		container.add(buttonsContainer, 
			new GridBagConstraints(0, 7, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
				
		this.getContentPane().add(container);
		this.pack();

		// TODO Does this have to be hard-coded?
		this.setSize(new Dimension(700, 450));
		this.setLocationRelativeTo(this.getOwner());
		
		// Resize columns.
		canonicalGranteeTable.getColumnModel().getColumn(0).setPreferredWidth((int)
			(canonicalGranteeTable.getParent().getBounds().getWidth() * 0.9));
		emailGranteeTable.getColumnModel().getColumn(0).setPreferredWidth((int)
			(emailGranteeTable.getParent().getBounds().getWidth() * 0.9));
		groupGranteeTable.getColumnModel().getColumn(0).setPreferredWidth((int)
			(groupGranteeTable.getParent().getBounds().getWidth() * 0.9));
	}
	
    /**
     * @return the ACL settings as set by the user in the dialog
     */
	public AccessControlList getUpdatedAccessControlList() {
		return updatedAccessControlList;
	}
	
    /**
     * Populates the local {@link #updatedAccessControlList} variable with ACL
     * details set by the user in the GUI elements.
     */
	private void updateAccessControlList() {
		updatedAccessControlList = new AccessControlList();
		updatedAccessControlList.setOwner(originalAccessControlList.getOwner());
		
		for (int i = 0; i < canonicalGranteeTable.getRowCount(); i++) {
			GranteeInterface grantee = canonicalGranteeTableModel.getGrantee(i);
			Permission permission = canonicalGranteeTableModel.getPermission(i);
			updatedAccessControlList.grantPermission(grantee, permission);
		}
		for (int i = 0; i < emailGranteeTable.getRowCount(); i++) {
			GranteeInterface grantee = emailGranteeTableModel.getGrantee(i);
			Permission permission = emailGranteeTableModel.getPermission(i);
			updatedAccessControlList.grantPermission(grantee, permission);
		}
		for (int i = 0; i < groupGranteeTable.getRowCount(); i++) {
			GranteeInterface grantee = groupGranteeTableModel.getGrantee(i);
			Permission permission = groupGranteeTableModel.getPermission(i);
			updatedAccessControlList.grantPermission(grantee, permission);
		}
	}
	
    /**
     * Event handler for this dialog.
     */
	public void actionPerformed(ActionEvent e) {
		if ("OK".equals(e.getActionCommand())) {
			updateAccessControlList();
			this.hide();
		} else if ("Cancel".equals(e.getActionCommand())) {
			updatedAccessControlList = null;
			this.hide();
		} else if ("addCanonicalGrantee".equals(e.getActionCommand())) {
			int rowIndex = canonicalGranteeTableModel.addGrantee(
				new CanonicalGrantee("NewCanonicalId"), Permission.PERMISSION_READ);
			canonicalGranteeTable.setRowSelectionInterval(rowIndex, rowIndex);
		} else if ("removeCanonicalGrantee".equals(e.getActionCommand())) {
			if (canonicalGranteeTable.getSelectedRow() >= 0) {
				canonicalGranteeTableModel.removeGrantAndPermission(canonicalGranteeTable.getSelectedRow());
			}
		} else if ("addEmailGrantee".equals(e.getActionCommand())) {
			int rowIndex = emailGranteeTableModel.addGrantee(
				new EmailAddressGrantee("new.email@address.here"), Permission.PERMISSION_READ);
			emailGranteeTable.setRowSelectionInterval(rowIndex, rowIndex);
		} else if ("removeEmailGrantee".equals(e.getActionCommand())) {
			if (emailGranteeTable.getSelectedRow() >= 0) {
				emailGranteeTableModel.removeGrantAndPermission(emailGranteeTable.getSelectedRow());
			}
		} else if ("addGroupGrantee".equals(e.getActionCommand())) {
			int rowIndex = groupGranteeTableModel.addGrantee(
				GroupGrantee.AUTHENTICATED_USERS, Permission.PERMISSION_READ);
			groupGranteeTable.setRowSelectionInterval(rowIndex, rowIndex);
		} else if ("removeGroupGrantee".equals(e.getActionCommand())) {
			if (groupGranteeTable.getSelectedRow() >= 0) {
				groupGranteeTableModel.removeGrantAndPermission(groupGranteeTable.getSelectedRow());
			}
		} else {
			System.err.println("UNRECOGNISED ACTION COMMAND: " + e.getActionCommand());
		}
	}
	
    /**
     * Displays the dialog box and waits until the user applies their changes or cancels the dialog.
     * <p>
     * If the user elects to apply their changes, this method returns the updated ACL information. 
     * If the user cancels the dialog, this method returns null. 
     * 
     * @param owner     the Frame within which this dialog will be displayed and centered
     * @param s3Items   an array of {@link S3Bucket} or {@link S3Object}s to which ACL change will be applied
     * @param accessControlList the original ACL settings for the S3Bucket or S3Objects provided
     * @return  the update ACL settings if the user applies changes, null if the dialog is cancelled.
     */
	public static AccessControlList showDialog(Frame owner, BaseS3Object[] s3Items, AccessControlList accessControlList) {
		if (accessControlDialog == null) {
			accessControlDialog = new AccessControlDialog(owner);
		}
		accessControlDialog.initData(s3Items, accessControlList);
		accessControlDialog.show();
		return accessControlDialog.getUpdatedAccessControlList();
	}
	
	/**
     * Table to represent ACL grantees.
     *  
     * @author James Murty
	 */
	private class GranteeTable extends JTable {
		public GranteeTable(GranteeTableModel granteeTableModel) {
			super(granteeTableModel);		
			
			getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			getSelectionModel().addListSelectionListener(this);
			setDefaultRenderer(GroupGrantee.class, new DefaultTableCellRenderer() {
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					return super.getTableCellRendererComponent(table, ((GranteeInterface)value).getIdentifier(), isSelected, hasFocus, row, column);
				}
			});			
			setDefaultEditor(GroupGrantee.class, new DefaultCellEditor(groupGranteeComboBox));		
			setDefaultEditor(Permission.class, new DefaultCellEditor(permissionComboBox));			
		}
	}

    /**
     * Grantee table model that knows what kind of grantees it is displaying and displays 
     * them appropriately.
     * 
     * @author James Murty
     */
	private class GranteeTableModel extends DefaultTableModel {
		private Class granteeClass = null;
		ArrayList currentGrantees  = new ArrayList();
		
		public GranteeTableModel(Class granteeClass) {
			super(new String[] {
				(CanonicalGrantee.class.equals(granteeClass) ? "Canonical ID" :
					EmailAddressGrantee.class.equals(granteeClass) ? "Email Address" :
						GroupGrantee.class.equals(granteeClass) ? "Group URI" :
							granteeClass.toString()
				),
				"Permission"}, 0);
			this.granteeClass = granteeClass;
		}
		
		public int addGrantee(GranteeInterface grantee, Permission permission) {
			GrantAndPermission gap = new GrantAndPermission(grantee, permission);
			int insertRow = 
				Collections.binarySearch(currentGrantees, gap, new Comparator() {
					public int compare(Object o1, Object o2) {
						GrantAndPermission g1 = (GrantAndPermission) o1; 
						GrantAndPermission g2 = (GrantAndPermission) o2; 
						return g1.getGrantee().getIdentifier().compareToIgnoreCase(
							g2.getGrantee().getIdentifier());
					}
				});
			if (insertRow >= 0) {
				// We already have an item with this key, but that's OK.
			} else {
				insertRow = (-insertRow) - 1;				
			}
			// New object to insert.
			currentGrantees.add(insertRow, gap);
			if (GroupGrantee.class.equals(granteeClass)) {
				super.insertRow(insertRow, new Object[] {grantee, permission});				
			} else {
				super.insertRow(insertRow, new Object[] {grantee.getIdentifier(), permission});
			}
			return insertRow;
		}
		
		public void removeGrantAndPermission(int index) {
			super.removeRow(index);
			currentGrantees.remove(index);
		}
		
		public void removeAllGrantAndPermissions() {
			int rowCount = super.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				super.removeRow(0);
			}
			currentGrantees.clear();
		}
		
		public Permission getPermission(int index) {
			return (Permission) super.getValueAt(index, 1);
		}
		
		public GranteeInterface getGrantee(int index) {
			GrantAndPermission originalGAP = (GrantAndPermission) currentGrantees.get(index);
			Object updatedGrantee = super.getValueAt(index, 0);
			if (updatedGrantee instanceof GroupGrantee) {
				// We can return this as-is, because GroupGrantees are actually stored in the table.
				return (GroupGrantee) updatedGrantee;
			} else {
				// Non-group Grantees are stored as Strings in the table, so update the original's ID.
				originalGAP.getGrantee().setIdentifier((String) updatedGrantee);
				return originalGAP.getGrantee();
			}
		}

		public boolean isCellEditable(int row, int column) {
			return true; // (column > 0);
		}
		
		public Class getColumnClass(int columnIndex) {
			if (columnIndex == 0) {
				if (GroupGrantee.class.equals(granteeClass))
					return GroupGrantee.class;
				else
					return String.class;
			} else {
				return Permission.class;
			}
		}
	}
	
	/**
     * Creates stand-alone dialog box for testing only. 
     * 
     * @param args
     * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		// TEST DATA
		AccessControlList acl = new AccessControlList();
		S3Owner owner = new S3Owner("1234567890", "Some Name");
		acl.setOwner(owner);
		
		GranteeInterface grantee = new CanonicalGrantee();
		grantee.setIdentifier("zzz");
		acl.grantPermission(grantee, Permission.PERMISSION_WRITE);

		grantee = new CanonicalGrantee();
		grantee.setIdentifier("abc");
		acl.grantPermission(grantee, Permission.PERMISSION_FULL_CONTROL);
		grantee = new CanonicalGrantee();
		grantee.setIdentifier("aaa");
		acl.grantPermission(grantee, Permission.PERMISSION_READ);
		grantee = GroupGrantee.ALL_USERS;
		acl.grantPermission(grantee, Permission.PERMISSION_READ);
		grantee = GroupGrantee.AUTHENTICATED_USERS;
		acl.grantPermission(grantee, Permission.PERMISSION_WRITE);
		grantee = new EmailAddressGrantee();
		grantee.setIdentifier("james@test.com");
		acl.grantPermission(grantee, Permission.PERMISSION_READ);
		grantee = new EmailAddressGrantee();
		grantee.setIdentifier("james@test2.com");
		acl.grantPermission(grantee, Permission.PERMISSION_FULL_CONTROL);

		JFrame f = new JFrame("Cockpit");
		S3Bucket bucket = new S3Bucket();
		bucket.setName("SomeReallyLongAndWackyBucketNamePath.HereItIs");
		
		AccessControlList updatedACL = acl;
		while ((updatedACL = AccessControlDialog.showDialog(f, new S3Bucket[] {bucket}, updatedACL)) != null) { 
			System.out.println(updatedACL.toXml());
		}		
		
		f.dispose();		
	}
	
}
