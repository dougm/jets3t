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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.NoSuchPaddingException;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.NumberFormatter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.Constants;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.executor.CancelEventListener;
import org.jets3t.service.executor.CreateBucketsEvent;
import org.jets3t.service.executor.CreateObjectsEvent;
import org.jets3t.service.executor.DeleteObjectsEvent;
import org.jets3t.service.executor.DownloadObjectsEvent;
import org.jets3t.service.executor.GetObjectHeadsEvent;
import org.jets3t.service.executor.GetObjectsEvent;
import org.jets3t.service.executor.ListAllBucketsEvent;
import org.jets3t.service.executor.ListObjectsEvent;
import org.jets3t.service.executor.LookupACLEvent;
import org.jets3t.service.executor.ProgressStatus;
import org.jets3t.service.executor.S3ServiceEventListener;
import org.jets3t.service.executor.S3ServiceExecutor;
import org.jets3t.service.executor.ServiceEvent;
import org.jets3t.service.executor.UpdateACLEvent;
import org.jets3t.service.executor.S3ServiceExecutor.S3ObjectAndOutputStream;
import org.jets3t.service.io.GZipDeflatingInputStream;
import org.jets3t.service.io.GZipInflatingOutputStream;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.security.EncryptionUtil;
import org.jets3t.service.utils.FileComparerResults;
import org.jets3t.service.utils.FileComparer;
import org.jets3t.service.utils.Mimetypes;
import org.jets3t.service.utils.ServiceUtils;

/**
 * Cockpit is a graphical application for viewing and managing the contents of an 
 * Amazon S3 account.
 * <p>
 * Cockpit offers the following capabilities:
 * <ul>
 * <li>Create and delete buckets in your account</li>
 * <li>Manage the contents of your own buckets, and publicly-accessibly third-party buckets</li>
 * <li>Upload files to S3 using drag-and-drop, download them using the menus</li>
 * <li>Change access permissions for S3 buckets and objects</li>
 * <li>Automatically gzip and/or encrypt files sent to S3</li>
 * <li>Automatically gunzip and/or decrypt files retrieved from S3</li>
 * <li>Store your AWS login credentials in encrypted files</li>
 * </ul>
 * <p>
 * The manager can be run as a stand-alone application or deployed as an applet.
 * <p>
 * This application should be useful in its own right, but is also intended to  
 * serve as an example of using the jets3t {@link S3ServiceExecutor} multi-threaded interface.
 * 
 * @author jmurty
 */
public class Cockpit extends JApplet implements S3ServiceEventListener, ActionListener, ListSelectionListener {
    private static final Log log = LogFactory.getLog(Cockpit.class);
    
    public static final String APPLICATION_TITLE = "jetS3T Manager";
    private File preferencesDirectory = Constants.DEFAULT_PREFERENCES_DIRECTORY;
    
    private final Insets insetsZero = new Insets(0, 0, 0, 0);
    private final Insets insetsDefault = new Insets(5, 7, 5, 7);

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    private S3ServiceExecutor s3ServiceExecutor = null;
    
    private Frame ownerFrame = null;
    private boolean isStandAloneApplication = false;
    
    // Service main menu items
    private JMenuItem loginMenuItem = null;
    private JMenuItem logoutMenuItem = null;    
    
    // Bucket main menu items
    private JMenuItem viewBucketPropertiesMenuItem = null;
    private JMenuItem refreshBucketMenuItem = null; 
    private JMenuItem createBucketMenuItem = null;
    private JMenuItem updateBucketACLMenuItem = null;
    private JMenuItem deleteBucketMenuItem = null;
    
    // Object main menu items
    private JMenuItem viewObjectPropertiesMenuItem = null;
    private JMenuItem refreshObjectMenuItem = null;
    private JMenuItem updateObjectACLMenuItem = null;
    private JMenuItem downloadObjectMenuItem = null;
    private JMenuItem deleteObjectMenuItem = null;

    // Preference menu items.
    private JCheckBoxMenuItem prefAutomaticGzip = null;
    private JCheckBoxMenuItem prefAutomaticEncryption = null;
    private JMenuItem prefEncryptionPassword = null;
    
    // Tables
    private JTable bucketsTable = null;
    private JTable objectsTable = null;
    private JScrollPane objectsTableSP = null;
    
    private JLabel objectsSummaryLabel = null;
        
    private HashMap cachedBuckets = new HashMap();
    private ProgressDisplay progressDisplay = null;
    
    // Preferences selected.
    private String preferenceServiceType = S3Service.SERVICE_TYPE_REST;
    private String preferenceEncryptionPassword = null;
    private EncryptionUtil encryptionPasswordUtil = null;    
    
    // Class variables used for uploading or downloading files.
    private File downloadDirectory = null;
    private Map downloadObjectsToFileMap = null;
    private boolean downloadingObjects = false;
    private boolean uploadingObjects = false;
    private File[] uploadingFiles = null;
    
    
    // File comparison options
    private final String UPLOAD_NEW_FILES_ONLY = "Only upload new file(s)";
    private final String UPLOAD_NEW_AND_CHANGED_FILES = "Replace changed file(s)";
    private final String UPLOAD_ALL_FILES = "Replace all files";
    private final String DOWNLOAD_NEW_FILES_ONLY = "Only download new file(s)";
    private final String DOWNLOAD_NEW_AND_CHANGED_FILES = "Replace changed file(s)";
    private final String DOWNLOAD_ALL_FILES = "Replace all files";
    
    private boolean viewingObjectProperties = false;
    
    /**
     * Constructor used when this application is run as an Applet.
     */
    public Cockpit() {
    }
            
    /**
     * Constructor used when this application is run in a stand-alone window.
     * 
     * @param ownerFrame the frame the application will be displayed in
     * @throws S3ServiceException
     */
    public Cockpit(JFrame ownerFrame) throws S3ServiceException {
        this.ownerFrame = ownerFrame;
        isStandAloneApplication = true;
        init();
    }
    
    /**
     * Initialises the application's GUI elements and Preferences directory.
     * <p>
     * When run as an applet a root owner frame is also found or created. 
     */
    public void init() {
        super.init();

        // Find or create a Frame to own modal dialog boxes.
        if (this.ownerFrame == null) {
            Component c = this;
            while (!(c instanceof Frame) && c.getParent() != null) {
                c = c.getParent();
            }
            if (!(c instanceof Frame)) {
                this.ownerFrame = new JFrame();        
            } else {
                this.ownerFrame = (Frame) c;                    
            }
        }
        
        // Ensure the preferences directory exists.
        if (!preferencesDirectory.exists()) {
            log.info("Creating preferences directory " + preferencesDirectory);
            preferencesDirectory.mkdir();
        }
        
        // Initialise the GUI.
        initGui();        
    }    
    
    /**
     * Initialises the application's GUI elements.
     */
    private void initGui() {        
        initMenus();
        
        JPanel appContent = new JPanel(new GridBagLayout());
        this.getContentPane().add(appContent);
                
        // Listing section.        
        JPanel bucketsContainer = new JPanel(new GridBagLayout());
        bucketsTable = new JTable(new BucketTableModel());
        bucketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bucketsTable.getSelectionModel().addListSelectionListener(this);
        bucketsTable.setShowHorizontalLines(true);
        bucketsTable.setShowVerticalLines(true);
        bucketsTable.addMouseListener(new ContextMenuListener());
        bucketsContainer.add(new JScrollPane(bucketsTable), 
            new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsZero, 0, 0));
        bucketsContainer.add(new JLabel(" "), 
            new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insetsDefault, 0, 0));
        
        JPanel objectsContainer = new JPanel(new GridBagLayout());
        objectsTable = new JTable(new ObjectTableModel());
        objectsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Date) {
                    Date date = (Date) value;
                    return super.getTableCellRendererComponent(table, sdf.format(date), isSelected, hasFocus, row, column);                    
                } else {
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            }
        });
        objectsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        objectsTable.getSelectionModel().addListSelectionListener(this);
        objectsTable.setShowHorizontalLines(true);
        objectsTable.setShowVerticalLines(true);
        objectsTable.addMouseListener(new ContextMenuListener());
        objectsTableSP = new JScrollPane(objectsTable);
        objectsContainer.add(objectsTableSP, 
                new GridBagConstraints(0, 0, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsZero, 0, 0));
        objectsSummaryLabel = new JLabel("Please select a bucket", JLabel.CENTER);
        objectsSummaryLabel.setFocusable(false);
        objectsContainer.add(objectsSummaryLabel, 
                new GridBagConstraints(0, 1, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        
        // Combine sections.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                bucketsContainer, objectsContainer);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);

        appContent.add(splitPane, 
                new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsDefault, 0, 0));
                
        // Set preferred sizes
        int preferredWidth = 800;
        int preferredHeight = 600;
        this.setBounds(new Rectangle(new Dimension(preferredWidth, preferredHeight)));
        
        splitPane.setResizeWeight(0.30);

        // Resize columns. // TODO Is there a better way to do this?
        int scrollPaneWidth = (int) objectsTable.getParent().getBounds().getWidth();
        TableColumnModel objectsTableColumnModel = objectsTable.getColumnModel();
        objectsTableColumnModel.getColumn(0).setPreferredWidth((int)(scrollPaneWidth * 0.70)); // 70% for Name
        objectsTableColumnModel.getColumn(1).setPreferredWidth((int)(scrollPaneWidth * 0.10)); // 10% for Size (total 80%)
        objectsTableColumnModel.getColumn(2).setPreferredWidth((int)(scrollPaneWidth * 0.20)); // 20% for Last Modified  (total 100%)
        
        // Initialize drop target.
        initDropTarget(new JComponent[] {objectsTableSP, objectsTable} );
        objectsTable.getDropTarget().setActive(false);
        objectsTableSP.getDropTarget().setActive(false);
    }
    
    /**
     * Initialise the application's menu bar.
     */
    private void initMenus() {
        JMenuBar appMenuBar = new JMenuBar();
        this.setJMenuBar(appMenuBar);
        
        // Service menu
        JMenu serviceMenu = new JMenu("Service");

        loginMenuItem = new JMenuItem("Log in...");
        loginMenuItem.setActionCommand("LoginEvent");
        loginMenuItem.addActionListener(this);
        serviceMenu.add(loginMenuItem);
        
        logoutMenuItem = new JMenuItem("Log out");
        logoutMenuItem.setActionCommand("LogoutEvent");
        logoutMenuItem.addActionListener(this);
        serviceMenu.add(logoutMenuItem);

        if (isStandAloneApplication) {
            serviceMenu.add(new JSeparator());
            
            JMenuItem quitMenuItem = new JMenuItem("Quit");
            quitMenuItem.setActionCommand("QuitEvent");
            quitMenuItem.addActionListener(this);
            serviceMenu.add(quitMenuItem);
        }

        loginMenuItem.setEnabled(true);
        logoutMenuItem.setEnabled(false);

        // Bucket menu
        JMenu bucketMenu = new JMenu("Buckets");
        
        refreshBucketMenuItem = new JMenuItem("Refresh bucket listing");
        refreshBucketMenuItem.setActionCommand("RefreshBuckets");
        refreshBucketMenuItem.addActionListener(this);
        bucketMenu.add(refreshBucketMenuItem);
        
        viewBucketPropertiesMenuItem = new JMenuItem("View bucket properties...");
        viewBucketPropertiesMenuItem.setActionCommand("ViewBucketProperties");
        viewBucketPropertiesMenuItem.addActionListener(this);
        bucketMenu.add(viewBucketPropertiesMenuItem);
        
        createBucketMenuItem = new JMenuItem("Create new bucket...");
        createBucketMenuItem.setActionCommand("CreateBucket");
        createBucketMenuItem.addActionListener(this);
        bucketMenu.add(createBucketMenuItem);

        updateBucketACLMenuItem = new JMenuItem("Update bucket's Access Control List...");
        updateBucketACLMenuItem.setActionCommand("UpdateBucketACL");
        updateBucketACLMenuItem.addActionListener(this);
        bucketMenu.add(updateBucketACLMenuItem);
        
        JMenuItem thirdPartyBucketMenuItem = new JMenuItem("Add third-party bucket...");
        thirdPartyBucketMenuItem.setActionCommand("AddThirdPartyBucket");
        thirdPartyBucketMenuItem.addActionListener(this);
        bucketMenu.add(thirdPartyBucketMenuItem);

        bucketMenu.add(new JSeparator());
        
        deleteBucketMenuItem = new JMenuItem("Delete selected bucket...");
        deleteBucketMenuItem.setActionCommand("DeleteBucket");
        deleteBucketMenuItem.addActionListener(this);
        bucketMenu.add(deleteBucketMenuItem);
        
        viewBucketPropertiesMenuItem.setEnabled(false);
        refreshBucketMenuItem.setEnabled(false);
        createBucketMenuItem.setEnabled(false);
        updateBucketACLMenuItem.setEnabled(false);
        deleteBucketMenuItem.setEnabled(false);

        // Object menu
        JMenu objectMenu = new JMenu("Objects");
        
        refreshObjectMenuItem = new JMenuItem("Refresh object listing");
        refreshObjectMenuItem.setActionCommand("RefreshObjects");
        refreshObjectMenuItem.addActionListener(this);
        objectMenu.add(refreshObjectMenuItem);
        
        viewObjectPropertiesMenuItem = new JMenuItem("View object properties...");
        viewObjectPropertiesMenuItem.setActionCommand("ViewObjectProperties");
        viewObjectPropertiesMenuItem.addActionListener(this);
        objectMenu.add(viewObjectPropertiesMenuItem);
        
        updateObjectACLMenuItem = new JMenuItem("Update selected object(s) Access Control List(s)...");
        updateObjectACLMenuItem.setActionCommand("UpdateObjectACL");
        updateObjectACLMenuItem.addActionListener(this);
        objectMenu.add(updateObjectACLMenuItem);

        downloadObjectMenuItem = new JMenuItem("Download selected object(s)...");
        downloadObjectMenuItem.setActionCommand("DownloadObjects");
        downloadObjectMenuItem.addActionListener(this);
        objectMenu.add(downloadObjectMenuItem);
            
        objectMenu.add(new JSeparator());

        deleteObjectMenuItem = new JMenuItem("Delete selected object(s)...");
        deleteObjectMenuItem.setActionCommand("DeleteObjects");
        deleteObjectMenuItem.addActionListener(this);
        objectMenu.add(deleteObjectMenuItem);

        viewObjectPropertiesMenuItem.setEnabled(false);
        refreshObjectMenuItem.setEnabled(false);
        updateObjectACLMenuItem.setEnabled(false);
        downloadObjectMenuItem.setEnabled(false);
        deleteObjectMenuItem.setEnabled(false);

        // Preferences menu.        
        JMenu preferencesMenu = new JMenu("Preferences");
        
        JMenuItem preferencesDirectoryMenuItem = new JMenuItem("Set location for preferences directory...");
        preferencesDirectoryMenuItem.setActionCommand("PreferenceSetPreferenceDirectory");
        preferencesDirectoryMenuItem.addActionListener(this);
        preferencesMenu.add(preferencesDirectoryMenuItem);

        preferencesMenu.add(new JSeparator());
        
        JRadioButtonMenuItem prefRestServiceMenuItem = new JRadioButtonMenuItem("Use REST/HTTP service");
        prefRestServiceMenuItem.setSelected(true);
        prefRestServiceMenuItem.setActionCommand("PreferenceRestService");
        prefRestServiceMenuItem.addActionListener(this);
        preferencesMenu.add(prefRestServiceMenuItem);
        
        JRadioButtonMenuItem prefSoapServiceMenuItem = new JRadioButtonMenuItem("Use SOAP/HTTP service");
        prefSoapServiceMenuItem.setActionCommand("PreferenceSoapService");
        prefSoapServiceMenuItem.addActionListener(this);
        preferencesMenu.add(prefSoapServiceMenuItem);
        
        ButtonGroup serviceTypeButtonGroup = new ButtonGroup();
        serviceTypeButtonGroup.add(prefRestServiceMenuItem);
        serviceTypeButtonGroup.add(prefSoapServiceMenuItem);
        
        preferencesMenu.add(new JSeparator());
        
        prefAutomaticGzip = new JCheckBoxMenuItem("Compress (gzip) uploaded files?");
        preferencesMenu.add(prefAutomaticGzip);

        prefAutomaticEncryption = new JCheckBoxMenuItem("Encrypt uploaded files?");
        prefAutomaticEncryption.setActionCommand("PreferenceEncryptFiles");
        prefAutomaticEncryption.addActionListener(this);
        preferencesMenu.add(prefAutomaticEncryption);
        
        prefEncryptionPassword = new JMenuItem("Set encryption password...");
        prefEncryptionPassword.setActionCommand("PreferenceSetEncryptionPassword");
        prefEncryptionPassword.setEnabled(prefAutomaticEncryption.isSelected());
        prefEncryptionPassword.addActionListener(this);
        preferencesMenu.add(prefEncryptionPassword);
        
        // Help menu.
        JMenu helpMenu = new JMenu("Help");
        
        
        // Build application menu bar.
        appMenuBar.add(serviceMenu);
        appMenuBar.add(bucketMenu);
        appMenuBar.add(objectMenu);
        appMenuBar.add(preferencesMenu);
        appMenuBar.add(helpMenu);
    }
    
    /**
     * Initialise the application's File drop targets for drag and drop copying of local files
     * to S3.
     * 
     * @param dropTargetComponents
     * the components files can be dropped on to transfer them to S3 
     */
    private void initDropTarget(JComponent[] dropTargetComponents) {
        DropTargetListener dropTargetListener = new DropTargetListener() {
            
            private boolean checkValidDrag(DropTargetDragEvent dtde) {
                if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
                    && (DnDConstants.ACTION_COPY == dtde.getDropAction() 
                        || DnDConstants.ACTION_MOVE == dtde.getDropAction())) 
                {
                    dtde.acceptDrag(dtde.getDropAction());
                    return true;
                } else {
                    dtde.rejectDrag();
                    return false;
                }                    
            }
            
            public void dragEnter(DropTargetDragEvent dtde) {
                if (checkValidDrag(dtde)) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            objectsTable.requestFocusInWindow();                                                
                        };
                    });                    
                } 
            }
            public void dragOver(DropTargetDragEvent dtde) {
                checkValidDrag(dtde);
            }
            public void dropActionChanged(DropTargetDragEvent dtde) {
                if (checkValidDrag(dtde)) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            objectsTable.requestFocusInWindow();                                                
                        };
                    });                    
                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            ownerFrame.requestFocusInWindow();                            
                        };
                    });                    
                }
            }
            public void dragExit(DropTargetEvent dte) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ownerFrame.requestFocusInWindow();                            
                    };
                });                    
            }
            
            public void drop(DropTargetDropEvent dtde) {
                if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
                    && (DnDConstants.ACTION_COPY == dtde.getDropAction() 
                        || DnDConstants.ACTION_MOVE == dtde.getDropAction()))
                {
                    dtde.acceptDrop(dtde.getDropAction());
                    try {
                        final List fileList = (List) dtde.getTransferable().getTransferData(
                            DataFlavor.javaFileListFlavor);
                        if (fileList != null && fileList.size() > 0) {
                            new Thread() {
                                public void run() {                           
                                    uploadFilesToS3((File[]) fileList.toArray(new File[] {}));
                                }
                            }.start();
                        }
                    } catch (Exception e) {
                        reportException(ownerFrame, "Unable to accept dropped item", e);
                    }
                } else {
                    dtde.rejectDrop();
                }    
            }
        };
        
        // Attach drop target listener to each target component.
        for (int i = 0; i < dropTargetComponents.length; i++) {
            new DropTarget(dropTargetComponents[i], DnDConstants.ACTION_COPY, dropTargetListener, true);
        }
    }
    
    /**
     * Starts a progress display dialog that cannot be cancelled. While the dialog is running the user 
     * cannot interact with the application.
     * 
     * @param statusText
     *        describes the status of a task in text meaningful to the user
     */
    private void startProgressDisplay(String statusText) {
        this.setEnabled(false);
        startProgressDisplay(statusText, 0, 0, null, null);
    }

    /**
     * Starts a progress display dialog. While the dialog is running the user cannot interact
     * with the application, except to cancel the task.
     * 
     * @param statusText
     *        describes the status of a task text meaningful to the user, such as "3 files of 7 uploaded"
     * @param minValue  the minimum progress value for a task, generally 0
     * @param maxValue  
     *        the maximum progress value for a task, such as the total number of threads or 100 if
     *        using percentage-complete as a metric.
     * @param cancelEventListener
     *        listener that is responsible for cancelling a long-lived task when the user clicks
     *        the cancel button. If a task cannot be cancelled this must be null.
     * @param cancelButtonText  
     *        text displayed in the cancel button if a task can be cancelled. This is only used if
     *        a cancel event listener is provided.
     */
    private void startProgressDisplay(final String statusText, final long minValue, final long maxValue, 
        final String cancelButtonText, final CancelEventListener cancelEventListener) 
    {
       if (progressDisplay == null || !progressDisplay.isActive()) {
           progressDisplay = new ProgressDisplay(ownerFrame, "Please wait...", statusText, 
               (int) minValue, (int) maxValue, cancelButtonText, cancelEventListener);
           progressDisplay.startDialog();
           
           this.getContentPane().setCursor(new Cursor(Cursor.WAIT_CURSOR));
       }
    }
    
    /**
     * Updates the status text and value of the progress display dialog.
     * @param statusText
     *        describes the status of a task text meaningful to the user, such as "3 files of 7 uploaded"
     * @param currentValue
     *        value representing how far through the task we are
     */
    private void updateProgressDisplay(final String statusText, final long currentValue) {
        if (progressDisplay != null && progressDisplay.isActive()) {
            if (currentValue > 0) {
                progressDisplay.updateProgress((int) currentValue);                
            }
            progressDisplay.updateStatusText(statusText);
        }
    }
    
    /**
     * Stops/halts the progress display dialog and allows the user to interact with the application.
     */
    private void stopProgressDisplay() {
        this.setEnabled(true);
        if (progressDisplay != null) {
            progressDisplay.haltDialog();
        }
        this.getContentPane().setCursor(null);
        
        // Block until the progress dialog is stopped.
        while (progressDisplay.isActive()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }            
    }
        
    /**
     * Event handler for this application. 
     */
    public void actionPerformed(ActionEvent event) {
        // Service Menu Events            
        if ("LoginEvent".equals(event.getActionCommand())) {
            loginEvent();
        } else if ("LogoutEvent".equals(event.getActionCommand())) {
            logoutEvent();
        } else if ("QuitEvent".equals(event.getActionCommand())) {
            ownerFrame.dispose();
        } 
        
        // Bucket Events.
        else if ("ViewBucketProperties".equals(event.getActionCommand())) {
            listBucketProperties();
        } else if ("RefreshBuckets".equals(event.getActionCommand())) {
            listAllBuckets();
        } else if ("CreateBucket".equals(event.getActionCommand())) {
            createBucket();
        } else if ("DeleteBucket".equals(event.getActionCommand())) {
            deleteSelectedBucket();
        } else if ("AddThirdPartyBucket".equals(event.getActionCommand())) {
            addThirdPartyBucket();
        } else if ("UpdateBucketACL".equals(event.getActionCommand())) {
            updateBucketAccessControlList();
        }
        
        // Object Events
        else if ("ViewObjectProperties".equals(event.getActionCommand())) {
            listObjectProperties();
        } else if ("RefreshObjects".equals(event.getActionCommand())) {
            listObjects();
        } else if ("UpdateObjectACL".equals(event.getActionCommand())) {
            lookupObjectsAccessControlLists();
        } else if ("DeleteObjects".equals(event.getActionCommand())) {
            deleteSelectedObjects();
        } else if ("DownloadObjects".equals(event.getActionCommand())) {
            try {
                downloadSelectedObjects();
            } catch (Exception ex) {
                reportException(ownerFrame, "Downloading S3 Objects", ex);
            }
        }
        
        // Preference Events        
        else if ("PreferenceSetPreferenceDirectory".equals(event.getActionCommand())) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setDialogTitle("Choose location for preferences directory");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setApproveButtonText("Select Directory");
            fileChooser.setCurrentDirectory(preferencesDirectory.getParentFile());
            
            int returnVal = fileChooser.showOpenDialog(ownerFrame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                log.debug("User has selected a new location for the preferences directory: "
                    + fileChooser.getSelectedFile());
                preferencesDirectory = Constants.getPreferencesDirectory(fileChooser.getSelectedFile());
            }

        } else if ("PreferenceRestService".equals(event.getActionCommand())) {
            preferenceServiceType = S3Service.SERVICE_TYPE_REST;
            try {
                AWSCredentials awsCredentials = (s3ServiceExecutor != null? 
                    s3ServiceExecutor.getAWSCredentials() : null);
                s3ServiceExecutor = S3ServiceExecutor.getExecutor(
                    preferenceServiceType, awsCredentials);
                s3ServiceExecutor.addServiceEventListener(this);
            } catch (Exception e) {
                reportException(ownerFrame, "Unable to load REST service", e);
            }
        } else if ("PreferenceSoapService".equals(event.getActionCommand())) {
            preferenceServiceType = S3Service.SERVICE_TYPE_SOAP;
            try {
                AWSCredentials awsCredentials = (s3ServiceExecutor != null? 
                    s3ServiceExecutor.getAWSCredentials() : null);
                s3ServiceExecutor = S3ServiceExecutor.getExecutor(
                    preferenceServiceType, awsCredentials);
                s3ServiceExecutor.addServiceEventListener(this);
            } catch (Exception e) {
                reportException(ownerFrame, "Unable to load SOAP service", e);
            }
        } else if ("PreferenceEncryptFiles".equals(event.getActionCommand())) {
            prefEncryptionPassword.setEnabled(prefAutomaticEncryption.isSelected());
        } else if ("PreferenceSetEncryptionPassword".equals(event.getActionCommand())) {
            try {
                initEncryptionUtility();
            } catch (Exception ex) {
                reportException(ownerFrame, "Unable to initialise encryption library", ex);
            }            
        }                        
        
        // Ooops...
        else {
            log.warn("Unrecognised ActionEvent command '" + event.getActionCommand() + "' in " + event);
        }
    }
    
    /**
     * Handles list selection events for this application.
     */
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        
        if (e.getSource().equals(bucketsTable.getSelectionModel())) {
            bucketSelectedEvent();
        } else if (e.getSource().equals(objectsTable.getSelectionModel())) {
            objectSelectedEvent();
        }
    }
    
    /**
     * Initialises an {@link EncryptionUtil} object to handle encryption/decryption of files, after
     * prompting the user to enter a password.
     * 
     * @return true if the encryption utility was created, false if it wasn't (such as when the user 
     *         cancels the password dialog box)
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     */
    private boolean initEncryptionUtility() throws InvalidKeyException, NoSuchAlgorithmException, 
        NoSuchPaddingException, InvalidKeySpecException 
    {
        preferenceEncryptionPassword = PasswordDialog.showDialog(
            ownerFrame, "Enter encryption password", 
            "Please enter your file encryption password", true);
        if (preferenceEncryptionPassword != null) {
            encryptionPasswordUtil = new EncryptionUtil(preferenceEncryptionPassword);
        }
        return (encryptionPasswordUtil != null);
    }
            
    /**
     * Displays the {@link LoginDialog} dialog and, if the user provides login credentials,
     * logs into the S3 service using those credentials.
     */
    private void loginEvent() {
        try {
            final AWSCredentials awsCredentials = 
                LoginDialog.showDialog(ownerFrame, preferencesDirectory);

            if (awsCredentials == null) {
                log.debug("Log in cancelled by user");
                s3ServiceExecutor = S3ServiceExecutor.getExecutor(preferenceServiceType, null);
                s3ServiceExecutor.addServiceEventListener(this);
                return;
            } 

            s3ServiceExecutor = S3ServiceExecutor.getExecutor(preferenceServiceType, awsCredentials);
            s3ServiceExecutor.addServiceEventListener(this);

            listAllBuckets(); // Doubles as check for valid credentials.            
            updateObjectsSummary();
            
            ownerFrame.setTitle(APPLICATION_TITLE + " : " + awsCredentials.getAccessKey());
            loginMenuItem.setEnabled(false);
            logoutMenuItem.setEnabled(true);
            
            refreshBucketMenuItem.setEnabled(true);
            createBucketMenuItem.setEnabled(true);
        } catch (Exception e) {
            reportException(ownerFrame, "Unable to Log in", e);
            try {
                s3ServiceExecutor = S3ServiceExecutor.getExecutor(preferenceServiceType, null);
                s3ServiceExecutor.addServiceEventListener(this);
            } catch (S3ServiceException e2) {
                reportException(ownerFrame, "Unable to revert to anonymous user", e2);
            }
        }
    }
    
    /**
     * Logs out of the S3 service by clearing all listed objects and buckets and resetting
     * the s3ServiceExecutor member variable.
     */
    private void logoutEvent() {
        log.debug("Logging out");
        try {
            s3ServiceExecutor = S3ServiceExecutor.getExecutor(preferenceServiceType, null);
            s3ServiceExecutor.addServiceEventListener(this);
            
            bucketsTable.clearSelection();
            ((BucketTableModel)bucketsTable.getModel()).removeAllBuckets();
            ((ObjectTableModel)objectsTable.getModel()).removeAllObjects();
            
            updateObjectsSummary();

            ownerFrame.setTitle(APPLICATION_TITLE);
            loginMenuItem.setEnabled(true);
            logoutMenuItem.setEnabled(false);
            
            refreshBucketMenuItem.setEnabled(false);
            createBucketMenuItem.setEnabled(false);
        } catch (Exception e) {
            reportException(ownerFrame, "Unable to Log out", e);            
        }
    }
    
    /**
     * Displays the currently selected bucket's properties in the dialog {@link PropertiesDialog}. 
     */
    private void listBucketProperties() {
        PropertiesDialog.showDialog(ownerFrame, getCurrentSelectedBucket());
    }
    
    /**
     * Displays the currently selected object's properties in the dialog {@link PropertiesDialog}. 
     * <p>
     * As detailed information about the object may not yet be available, this method works
     * indirectly via the {@link #retrieveObjectsDetails} method. The <code>retrieveObjectsDetails</code> 
     * method retrieves all the details for the currently selected objects, and once they are available
     * knows to display the <code>PropertiesDialog</code> as the {@link #viewingObjectProperties} flag
     * is set.
     */
    private void listObjectProperties() {
        retrieveObjectsDetails(getSelectedObjects());
        viewingObjectProperties = true;
    }
    
    /**
     * Starts a thread to run {@link S3ServiceExecutor#listAllBuckets}.
     */
    private void listAllBuckets() {
        new Thread() {
            public void run() {
                s3ServiceExecutor.listAllBuckets();
            };
        }.start();
    }
    
    /**
     * This method is an {@link S3ServiceEventListener} action method that is invoked when this 
     * application's <code>S3ServiceExecutor</code> triggers a <code>GetObjectsEvent</code>.
     * <p>
     * <b>This never happens in this application.</b>
     * 
     * @param event
     */
    public void s3ServiceEventPerformed(GetObjectsEvent event) {
        // Not used.
    }
        
    /**
     * This method is an {@link S3ServiceEventListener} action method that is invoked when this 
     * application's <code>S3ServiceExecutor</code> triggers a <code>ListAllBucketsEvent</code>.
     * <p>
     * When the bucket listing is complete the buckets are displayed in the buckets table.
     * <p>
     * This application always performs a bucket listing when the user logs into S3. If the 
     * bucket listing fails this is interpreted as a failure of the overall login attempt, 
     * so on an error event this method automatically logs the user out.
     * 
     * @param event
     */
    public void s3ServiceEventPerformed(ListAllBucketsEvent event) {        
        if (ServiceEvent.EVENT_STARTED == event.getEventStatus()) {
            startProgressDisplay("Listing buckets for " + s3ServiceExecutor.getAWSCredentials().getAccessKey());
            bucketsTable.clearSelection();            
            ((BucketTableModel)bucketsTable.getModel()).removeAllBuckets();
            ((ObjectTableModel)objectsTable.getModel()).removeAllObjects();            
        } 
        else if (ServiceEvent.EVENT_COMPLETED == event.getEventStatus()) {
            S3Bucket[] buckets = event.getBuckets();
            for (int i = 0; i < buckets.length; i++) {
                ((BucketTableModel)bucketsTable.getModel()).addBucket(buckets[i]);
            }            
            stopProgressDisplay();
        }
        else if (ServiceEvent.EVENT_ERROR == event.getEventStatus()) {
            stopProgressDisplay();
            logoutEvent();
            reportException(ownerFrame, "Unable to list your buckets", event.getErrorCause());
        }
    }
    
    private void bucketSelectedEvent() {
        S3Bucket newlySelectedBucket = getCurrentSelectedBucket();
        if (newlySelectedBucket == null) {
            viewBucketPropertiesMenuItem.setEnabled(false);
            refreshBucketMenuItem.setEnabled(false);
            updateBucketACLMenuItem.setEnabled(false);
            deleteBucketMenuItem.setEnabled(false);
            
            refreshObjectMenuItem.setEnabled(false);
            
            ((ObjectTableModel)objectsTable.getModel()).removeAllObjects();
            
            objectsTable.getDropTarget().setActive(false);
            objectsTableSP.getDropTarget().setActive(false);
            
            return;
        }
        
        viewBucketPropertiesMenuItem.setEnabled(true);
        refreshBucketMenuItem.setEnabled(true);
        updateBucketACLMenuItem.setEnabled(true);
        deleteBucketMenuItem.setEnabled(true);
        
        refreshObjectMenuItem.setEnabled(true);
        
        objectsTable.getDropTarget().setActive(true);
        objectsTableSP.getDropTarget().setActive(true);
        
        if (cachedBuckets.containsKey(newlySelectedBucket.getName())) {
            S3Object[] objects = (S3Object[]) cachedBuckets.get(newlySelectedBucket.getName());
            s3ServiceEventPerformed(new ListObjectsEvent(ServiceEvent.EVENT_STARTED, newlySelectedBucket));
            s3ServiceEventPerformed(new ListObjectsEvent(ServiceEvent.EVENT_COMPLETED, newlySelectedBucket, objects));
        } else {        
            listObjects();
        }
    }
    
    private void objectSelectedEvent() {
        if (getSelectedObjects().length == 0) {
            viewObjectPropertiesMenuItem.setEnabled(false);
            updateObjectACLMenuItem.setEnabled(false);
            downloadObjectMenuItem.setEnabled(false);
            deleteObjectMenuItem.setEnabled(false);
        } else {
            if (getSelectedObjects().length == 1) {
                viewObjectPropertiesMenuItem.setEnabled(true);
            }
            updateObjectACLMenuItem.setEnabled(true);
            downloadObjectMenuItem.setEnabled(true);
            deleteObjectMenuItem.setEnabled(true);
        }
    }

    private void listObjects() {
        new Thread() {
            public void run() {
                s3ServiceExecutor.listObjects(getCurrentSelectedBucket());
            }
        }.start();        
    }
    
    public void s3ServiceEventPerformed(ListObjectsEvent event) {
        if (ServiceEvent.EVENT_STARTED == event.getEventStatus()) {            
            startProgressDisplay("Listing objects in " + event.getBucket().getName());
            
            ((ObjectTableModel)objectsTable.getModel()).removeAllObjects();
        } 
        else if (ServiceEvent.EVENT_COMPLETED == event.getEventStatus()) {
            if (event.getObjects() == null) {
                // TODO Why does this happen sometimes?
                cachedBuckets.put(event.getBucket().getName(), new S3Object[] {});
                return;
            }
            for (int i = 0; i < event.getObjects().length; i++) {
                ((ObjectTableModel)objectsTable.getModel()).addObject(event.getObjects()[i], false);
            }
            cachedBuckets.put(event.getBucket().getName(), event.getObjects());

            updateObjectsSummary();

            stopProgressDisplay();
        }
        else if (ServiceEvent.EVENT_ERROR == event.getEventStatus()) {
            stopProgressDisplay();
            reportException(ownerFrame, "listObjects", event.getErrorCause());
        }
    }
    
    private void updateObjectsSummary() {
        S3Object[] objects = ((ObjectTableModel)objectsTable.getModel()).getObjects();
        
        try {
            String summary = "Please select a bucket";        
            long totalBytes = 0;
            if (objects != null) {
                summary = objects.length + " item" + (objects.length != 1? "s" : "");
                
                for (int i = 0; i < objects.length; i++) {
                    totalBytes += objects[i].getContentLength();
                }
                if (totalBytes > 0) {
                    summary += ", " + formatByteSize(totalBytes);
                }
            }        
            
            objectsSummaryLabel.setText(summary);
        } catch (Throwable t) {
            reportException(ownerFrame, "Unable to update object list summary", t);
        }
    }
    
    private void showBucketPopupMenu(JComponent invoker, int xPos, int yPos) {
        if (s3ServiceExecutor == null) {
            return;
        }
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem mi0 = new JMenuItem("View bucket properties...");
        mi0.setActionCommand("ViewBucketProperties");
        mi0.addActionListener(this);
        menu.add(mi0);

        JMenuItem mi3 = new JMenuItem("Update bucket's Access Control List...");
        mi3.setActionCommand("UpdateBucketACL");
        mi3.addActionListener(this);
        menu.add(mi3);
            
        menu.add(new JSeparator());
        
        JMenuItem mi4 = new JMenuItem("Delete bucket '" + getCurrentSelectedBucket().getName() + "'...");
        mi4.setActionCommand("DeleteBucket");
        mi4.addActionListener(this);
        menu.add(mi4);
        
        menu.show(invoker, xPos, yPos);
    }
    
    private S3Bucket getCurrentSelectedBucket() {
        if (bucketsTable.getSelectedRows().length == 0) {
            return null;
        } else {
            return ((BucketTableModel)bucketsTable.getModel()).getBucket(
                    bucketsTable.getSelectedRows()[0]);
        }
    }
    
    private void showObjectPopupMenu(JComponent invoker, int xPos, int yPos) {
        if (getCurrentSelectedBucket() == null || getSelectedObjects().length == 0) {
            return;
        }
        
        JPopupMenu menu = new JPopupMenu();
        
        if (objectsTable.getSelectedRows().length == 1) {
            JMenuItem mi0 = new JMenuItem("View object properties...");
            mi0.setActionCommand("ViewObjectProperties");
            mi0.addActionListener(this);
            menu.add(mi0);
        }
        
        JMenuItem mi2 = new JMenuItem("Update object's Access Control List...");
        mi2.setActionCommand("UpdateObjectACL");
        mi2.addActionListener(this);
        menu.add(mi2);

        JMenuItem mi3 = null;
        if (objectsTable.getSelectedRows().length == 1) {
            S3Object object = 
                ((ObjectTableModel)objectsTable.getModel()).getObject(
                objectsTable.getSelectedRows()[0]);
            mi3 = new JMenuItem("Download '" + object.getKey() + "'...");
        } else {
            mi3 = new JMenuItem("Download " + objectsTable.getSelectedRows().length + " object(s)...");
        }        
        mi3.setActionCommand("DownloadObjects");
        mi3.addActionListener(this);
        menu.add(mi3);
            
        menu.add(new JSeparator());

        JMenuItem mi4 = null;
        if (objectsTable.getSelectedRows().length == 1) {
            S3Object object = 
                ((ObjectTableModel)objectsTable.getModel()).getObject(
                objectsTable.getSelectedRows()[0]);
            mi4 = new JMenuItem("Delete '" + object.getKey() + "'...");
        } else {
            mi4 = new JMenuItem("Delete " + objectsTable.getSelectedRows().length + " objects...");
        }        
        mi4.setActionCommand("DeleteObjects");
        mi4.addActionListener(this);
        menu.add(mi4);
        
        menu.show(invoker, xPos, yPos);
    }
    
    private void createBucket() {
        String proposedNewName = 
                s3ServiceExecutor.getAWSCredentials().getAccessKey() + "." + "NewBucket";

        final String bucketName = (String) JOptionPane.showInputDialog(ownerFrame, 
            "Name for new bucket (no spaces allowed):",
            "Create a new bucket", JOptionPane.QUESTION_MESSAGE,
            null, null, proposedNewName);

        if (bucketName != null) {
            new Thread() {
                public void run() {
                    s3ServiceExecutor.createBucket(bucketName);
                }
            }.start();        
            
        }            
    }
    
    public void s3ServiceEventPerformed(CreateBucketsEvent event) {
        if (ServiceEvent.EVENT_STARTED == event.getEventStatus()) {    
            startProgressDisplay("Creating " + event.getProgressStatus().getThreadCount() + " buckets",                     
                0, event.getProgressStatus().getThreadCount(), 
                "Cancel bucket creation", event.getProgressStatus().getCancelEventListener());
        } 
        else if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventStatus()) {
            for (int i = 0; i < event.getBuckets().length; i++) {
                int insertRow = ((BucketTableModel) bucketsTable.getModel()).addBucket(
                    event.getBuckets()[i]);
                bucketsTable.setRowSelectionInterval(insertRow, insertRow);                
            }
            
            ProgressStatus progressStatus = event.getProgressStatus();
            String statusText = "Created " + progressStatus.getCompletedThreads() + " buckets of " + progressStatus.getThreadCount();
            updateProgressDisplay(statusText, progressStatus.getCompletedThreads());                
        }
        else if (ServiceEvent.EVENT_COMPLETED == event.getEventStatus()) { 
            stopProgressDisplay();
        }
        else if (ServiceEvent.EVENT_CANCELLED == event.getEventStatus()) {
            stopProgressDisplay();        
        }
        else if (ServiceEvent.EVENT_ERROR == event.getEventStatus()) {
            stopProgressDisplay();
            reportException(ownerFrame, "createBuckets", event.getErrorCause());
        }
    }

        
    private void deleteSelectedBucket() {
        S3Bucket currentBucket = getCurrentSelectedBucket();
        if (currentBucket == null) {
            log.debug("Ignoring delete bucket command, no currently selected bucket");
            return;
        }
        
        int response = JOptionPane.showConfirmDialog(ownerFrame, 
            "Are you sure you want to delete '" + currentBucket.getName() + "'?",  
            "Delete Bucket?", JOptionPane.YES_NO_OPTION);
        
        if (response == JOptionPane.NO_OPTION) {
            return;
        }
        
        try {
            s3ServiceExecutor.getS3Service().deleteBucket(currentBucket.getName());
            ((BucketTableModel)bucketsTable.getModel()).removeBucket(currentBucket);
        } catch (Exception e) {
            reportException(ownerFrame, "Unable to delete bucket", e);
        }
    }
    
    private void addThirdPartyBucket() {
        try {
            String bucketName = (String) JOptionPane.showInputDialog(ownerFrame, 
                "Name for third-party bucket:",
                "Add a third-party bucket", JOptionPane.QUESTION_MESSAGE);

            if (bucketName != null) {
                S3Bucket thirdPartyBucket = s3ServiceExecutor.getS3Service().getBucket(bucketName);
                ((BucketTableModel)bucketsTable.getModel()).addBucket(thirdPartyBucket);
            }            
        } catch (Exception e) {
            reportException(ownerFrame, "Unable to access third-party bucket", e);
        }        
    }
    
    private void updateBucketAccessControlList() {
        try {
            S3Bucket currentBucket = getCurrentSelectedBucket();
            AccessControlList bucketACL = s3ServiceExecutor.getS3Service().getAcl(currentBucket);
            
            AccessControlList updatedBucketACL = AccessControlDialog.showDialog(ownerFrame, new S3Bucket[] {currentBucket}, bucketACL);
            if (updatedBucketACL != null) {
                currentBucket.setAcl(updatedBucketACL);
                s3ServiceExecutor.getS3Service().putAcl(currentBucket);
            }
        } catch (Exception e) {
            reportException(ownerFrame, "Unable to update bucket Access Control", e);
        }        
    }    
    
    private S3Object[] getSelectedObjects() {
        int selRows[] = objectsTable.getSelectedRows();
        if (selRows.length == 0) {
            return new S3Object[] {};
        } else {
            S3Object objects[] = new S3Object[selRows.length];
            for (int i = 0; i < selRows.length; i++) {
                objects[i] = ((ObjectTableModel)objectsTable.getModel()).getObject(selRows[i]);
            }
            return objects;
        }
    }

    private void lookupObjectsAccessControlLists() {
        (new Thread() {
            public void run() {
                s3ServiceExecutor.getACLs(getCurrentSelectedBucket(), getSelectedObjects());
            }    
        }).start();        
    }
    
    public void s3ServiceEventPerformed(LookupACLEvent event) {
        if (ServiceEvent.EVENT_STARTED == event.getEventStatus()) {
            startProgressDisplay("Looking up ACL(s) for " + event.getProgressStatus().getThreadCount() + " object(s)", 
                    0, event.getProgressStatus().getThreadCount(), "Cancel Lookup",  
                    event.getProgressStatus().getCancelEventListener());
        } 
        else if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventStatus()) {
            ProgressStatus progressStatus = event.getProgressStatus();
            String statusText = "Retrieved " + progressStatus.getCompletedThreads() + " of " + progressStatus.getThreadCount() + " ACL(s)";
            updateProgressDisplay(statusText, progressStatus.getCompletedThreads());                    
        }
        else if (ServiceEvent.EVENT_COMPLETED == event.getEventStatus()) {
            stopProgressDisplay();                
            
            S3Object[] objectsWithACL = getSelectedObjects();
            
            // Build merged ACL containing ALL relevant permissions
            AccessControlList mergedACL = new AccessControlList();
            for (int i = 0; i < objectsWithACL.length; i++) {
                AccessControlList objectACL = objectsWithACL[i].getAcl();
                mergedACL.grantAllPermissions(objectACL.getGrants());
                if (mergedACL.getOwner() == null) { // TODO Better way of handling this?
                    mergedACL.setOwner(objectACL.getOwner());
                }
            }
            
            // Show ACL dialog box for user to change ACL settings for all objects.
            AccessControlList updatedObjectACL = AccessControlDialog.showDialog(ownerFrame, objectsWithACL, mergedACL);
            if (updatedObjectACL != null) {
                // Update ACLs for each object.
                for (int i = 0; i < objectsWithACL.length; i++) {
                    objectsWithACL[i].setAcl(updatedObjectACL);
                }
                
                // Perform ACL updates.
                updateObjectsAccessControlLists(getCurrentSelectedBucket(), objectsWithACL);
            }                        
        }
        else if (ServiceEvent.EVENT_CANCELLED == event.getEventStatus()) {
            stopProgressDisplay();        
        }
        else if (ServiceEvent.EVENT_ERROR == event.getEventStatus()) {
            stopProgressDisplay();
            reportException(ownerFrame, "lookupACLs", event.getErrorCause());
        }
    }
    
    private void updateObjectsAccessControlLists(final S3Bucket bucket, final S3Object[] objectsWithUpdatedACLs) {
        (new Thread() {
            public void run() {
                s3ServiceExecutor.putACLs(bucket, objectsWithUpdatedACLs);
            }    
        }).start();        
    }
    
    public void s3ServiceEventPerformed(UpdateACLEvent event) {
        if (ServiceEvent.EVENT_STARTED == event.getEventStatus()) {
            startProgressDisplay("Updating ACL(s) for " + event.getProgressStatus().getThreadCount() + " object(s)", 
                    0, event.getProgressStatus().getThreadCount(), "Cancel Update", 
                    event.getProgressStatus().getCancelEventListener());
        } 
        else if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventStatus()) {
            ProgressStatus progressStatus = event.getProgressStatus();
            String statusText = "Updated " + progressStatus.getCompletedThreads() + " of " + progressStatus.getThreadCount() + " ACL(s)";
            updateProgressDisplay(statusText, progressStatus.getCompletedThreads());                    
        }
        else if (ServiceEvent.EVENT_COMPLETED == event.getEventStatus()) {
            stopProgressDisplay();                            
        }
        else if (ServiceEvent.EVENT_CANCELLED == event.getEventStatus()) {
            stopProgressDisplay();        
        }
        else if (ServiceEvent.EVENT_ERROR == event.getEventStatus()) {
            stopProgressDisplay();
            reportException(ownerFrame, "lookupACLs", event.getErrorCause());
        }
    }

    
    private void downloadSelectedObjects() throws IOException {
        // Prompt user to choose directory location for downloaded files (or cancel download altogether)
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setDialogTitle("Choose directory to save S3 files in");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setApproveButtonText("Save files here");
        if (downloadDirectory != null && downloadDirectory.getParentFile() != null) {
            fileChooser.setCurrentDirectory(downloadDirectory.getParentFile());
        }
        
        int returnVal = fileChooser.showOpenDialog(ownerFrame);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
        }
        
        downloadDirectory = fileChooser.getSelectedFile();
        
        // Perform the download (via retrieving details with the downloadingObjects flag set)
        downloadingObjects = true;
        final S3Object[] objects = getSelectedObjects();
        retrieveObjectsDetails(objects);
    }
        
    private void performObjectsDownload() {
        try {

            // Build map of existing local files.
            Map filesInDownloadDirectoryMap = FileComparer.
                buildFileMap(downloadDirectory, null);
            
            // Build map of S3 Objects being downloaded. 
            Map s3DownloadObjectsMap = FileComparer.populateS3ObjectMap("", getSelectedObjects());
                            
            // Compare objects being downloaded and existing local files.
            FileComparerResults comparisonResults = 
                FileComparer.buildDiscrepancyLists(filesInDownloadDirectoryMap, s3DownloadObjectsMap);
    
            // Determine which files to download, prompting user whether to over-write existing files
            List objectKeysForDownload = new ArrayList();
            objectKeysForDownload.addAll(comparisonResults.onlyOnServerKeys);
    
            int newFiles = comparisonResults.onlyOnServerKeys.size();
            int unchangedFiles = comparisonResults.alreadySynchronisedKeys.size();
            int changedFiles = comparisonResults.updatedOnClientKeys.size() 
                + comparisonResults.updatedOnServerKeys.size();
    
            if (unchangedFiles > 0 || changedFiles > 0) {
                // Ask user whether to replace existing unchanged and/or existing changed files.
                log.debug("Files for download clash with existing local files, prompting user to choose which files to replace");
                List options = new ArrayList();
                String message = "Of the " + s3DownloadObjectsMap.keySet().size() 
                    + " object(s) being downloaded:\n\n";
                
                if (newFiles > 0) {
                    message += newFiles + " file(s) are new.\n\n";
                    options.add(DOWNLOAD_NEW_FILES_ONLY);                    
                }
                if (changedFiles > 0) {
                    message += changedFiles + " file(s) have changed and will be replaced.\n\n";
                    options.add(DOWNLOAD_NEW_AND_CHANGED_FILES);
                }
                if (unchangedFiles > 0) {
                    message += unchangedFiles + " file(s) already exist and are unchanged.\n\n";
                    options.add(DOWNLOAD_ALL_FILES);
                }
                message += "Please choose which file(s) you wish to download:";
                
                Object response = JOptionPane.showInputDialog(
                    ownerFrame, message, "Replace file(s)?", JOptionPane.QUESTION_MESSAGE, 
                    null, options.toArray(), DOWNLOAD_NEW_AND_CHANGED_FILES);
                
                if (DOWNLOAD_NEW_FILES_ONLY.equals(response)) {
                    // No change required to default objectKeysForDownload list.
                } else if (DOWNLOAD_ALL_FILES.equals(response)) {
                    objectKeysForDownload.addAll(comparisonResults.updatedOnClientKeys);
                    objectKeysForDownload.addAll(comparisonResults.updatedOnServerKeys);
                    objectKeysForDownload.addAll(comparisonResults.alreadySynchronisedKeys);
                } else if (DOWNLOAD_NEW_AND_CHANGED_FILES.equals(response)) {
                    objectKeysForDownload.addAll(comparisonResults.updatedOnClientKeys);
                    objectKeysForDownload.addAll(comparisonResults.updatedOnServerKeys);                    
                } else {
                    // Download cancelled.
                    return;
                }
            }

            log.debug("Downloading " + objectKeysForDownload.size() + " objects");
            if (objectKeysForDownload.size() == 0) {
                return;
            }
                        
            // Create array of objects for download.        
            S3Object[] objects = new S3Object[objectKeysForDownload.size()];
            int objectIndex = 0;
            for (Iterator iter = objectKeysForDownload.iterator(); iter.hasNext();) {
                objects[objectIndex++] = (S3Object) s3DownloadObjectsMap.get(iter.next()); 
            }
                        
            downloadObjectsToFileMap = new HashMap();
            ArrayList objAndOutList = new ArrayList();
        
            // Setup files to write to, creating parent directories when necessary.
            for (int i = 0; i < objects.length; i++) {
                File file = new File(downloadDirectory, objects[i].getKey());
                
                // Create directory corresponding to object, or parent directories of object.
                if (Mimetypes.MIMETYPE_JETS3T_DIRECTORY.equals(objects[i].getContentType())) {
                    file.mkdirs();
                    // No further data to download for directories...
                    continue;
                } else {
                    if (file.getParentFile() != null) {
                        file.getParentFile().mkdirs();
                    }
                }
                
                downloadObjectsToFileMap.put(objects[i].getKey(), file);

                OutputStream outputStream = new FileOutputStream(file);
                
                if (Mimetypes.MIMETYPE_GZIP.equals(objects[i].getContentType())) 
                {
                    // Automatically inflate gzipped data.
                    outputStream = new GZipInflatingOutputStream(outputStream);
                }
                if (objects[i].getMetadata().get(Constants.METADATA_JETS3T_ENCRYPTED) != null) 
                {
                    // Automatically decrypt encrypt files.
                    if (encryptionPasswordUtil == null) {
                        if (!initEncryptionUtility()) {
                            throw new S3ServiceException(
                                "Cannot download encrypted files without a password");
                        }
                    }
                    outputStream = encryptionPasswordUtil.decrypt(outputStream);                    
                }

                objAndOutList.add(s3ServiceExecutor.new S3ObjectAndOutputStream(
                    objects[i], outputStream));            
            }
            
            final S3ObjectAndOutputStream[] objAndOutArray = (S3ObjectAndOutputStream[])
                objAndOutList.toArray(new S3ObjectAndOutputStream[] {});        
            (new Thread() {
                public void run() {
                    s3ServiceExecutor.downloadObjects(getCurrentSelectedBucket(), objAndOutArray);
                }
            }).start();
        } catch (Exception e) {
            reportException(ownerFrame, "Failed to download objects", e);
        }
    }
    
    public void s3ServiceEventPerformed(DownloadObjectsEvent event) {
        if (ServiceEvent.EVENT_STARTED == event.getEventStatus()) {    
            // Show percentage of uploaded bytes if it's non-zero ...
            if (event.getBytesTotal() > 0) {
                startProgressDisplay("Downloaded " + formatByteSize(event.getBytesCompleted()) 
                    + " of " + formatByteSize(event.getBytesTotal()), 0, 100, "Cancel Download", 
                    event.getProgressStatus().getCancelEventListener());
            // ... otherwise show the number of completed threads.
            } else {
                startProgressDisplay("Downloaded " + event.getProgressStatus().getCompletedThreads()
                    + " of " + event.getProgressStatus().getThreadCount() + " objects", 
                    0, event.getProgressStatus().getThreadCount(),  "Cancel Download",
                    event.getProgressStatus().getCancelEventListener());
            }
        } 
        else if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventStatus()) {
            // Show percentage of uploaded bytes if it's non-zero ...
            if (event.getBytesTotal() > 0) {
                String bytesCompletedStr = formatByteSize(event.getBytesCompleted());
                String bytesTotalStr = formatByteSize(event.getBytesTotal());
                String statusText = "Downloaded " + bytesCompletedStr + " of " + bytesTotalStr;
                long percentage = (int) (((double)event.getBytesCompleted() / event.getBytesTotal()) * 100);
                updateProgressDisplay(statusText, percentage);
            }
            // ... otherwise show the number of completed threads.
            else {
                ProgressStatus progressStatus = event.getProgressStatus();
                String statusText = "Downloaded " + progressStatus.getCompletedThreads() 
                    + " of " + progressStatus.getThreadCount() + " objects";                    
                updateProgressDisplay(statusText, progressStatus.getCompletedThreads());                    
            }            
        } else if (ServiceEvent.EVENT_COMPLETED == event.getEventStatus()) {
            stopProgressDisplay();                
        }
        else if (ServiceEvent.EVENT_CANCELLED == event.getEventStatus()) {
            // Delete all incompletely downloaded object files.
            S3Object[] incompleteObjects = event.getObjects();
            for (int i = 0; i < incompleteObjects.length; i++) {
                File file = (File) downloadObjectsToFileMap.get(incompleteObjects[i].getKey());
                 if (file.length() != incompleteObjects[i].getContentLength()) {
                    log.debug("Deleting incomplete object file: " + file.getName());                
                    file.delete();
                 }
            }
            
            stopProgressDisplay();        
        }
        else if (ServiceEvent.EVENT_ERROR == event.getEventStatus()) {
            stopProgressDisplay();
            reportException(ownerFrame, "createObjects", event.getErrorCause());
        }
    }
    
    private File prepareUploadFile(final File originalFile, final S3Object newObject) throws Exception {
        if (!prefAutomaticGzip.isSelected() && !prefAutomaticEncryption.isSelected()) {
            // No file pre-processing required.
            return originalFile;
        }
        
        String actionText = "";
        
        // File must be pre-processed. Process data from original file 
        // and write it to a temporary one ready for upload.
        final File tempUploadFile = File.createTempFile("jets3tCockpit",".tmp");
        tempUploadFile.deleteOnExit();
        
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tempUploadFile));
        InputStream inputStream = new BufferedInputStream(new FileInputStream(originalFile));
        
        if (prefAutomaticGzip.isSelected()) {
            inputStream = new GZipDeflatingInputStream(inputStream);
            newObject.setContentType(Mimetypes.MIMETYPE_GZIP);
            actionText += "Compressing";                
        } 
        if (prefAutomaticEncryption.isSelected()) {
            inputStream = encryptionPasswordUtil.encrypt(inputStream);                        
            newObject.addMetadata(Constants.METADATA_JETS3T_ENCRYPTED, 
                encryptionPasswordUtil.getCipher().getAlgorithm()); 
            actionText += (actionText.length() == 0? "Encrypting" : " and encrypting");                
        }

        updateProgressDisplay(actionText + " '" + originalFile.getName() + "' for upload", 0);
        log.debug("Re-writing file data for '" + originalFile + "' to temporary file '" 
            + tempUploadFile.getAbsolutePath() + "': " + actionText);
        
        byte[] buffer = new byte[4096];
        int c = -1;
        while ((c = inputStream.read(buffer)) >= 0) {
            outputStream.write(buffer, 0, c);
        }
        inputStream.close();
        outputStream.close();
        
        return tempUploadFile;
    }
    
    private void uploadFilesToS3(final File files[]) {
        // Perform the upload (via retrieving details with the uploadingObjects flag set)
        uploadingObjects = true;
        uploadingFiles = files;
        retrieveObjectsDetails(((ObjectTableModel)objectsTable.getModel()).getObjects());
    }
        
    private void performObjectsUpload() {        
        try {
            // Build map of files proposed for upload.
            HashMap filesForUploadMap = new HashMap();
            for (int i = 0; i < uploadingFiles.length; i++) {
                // TODO FileComparisonUtilities.buildFileMap() should handle both cases automatically?
                if (uploadingFiles[i].isDirectory()) {
                    filesForUploadMap.putAll(FileComparer.
                        buildFileMap(uploadingFiles[i], uploadingFiles[i].getName()));
                } else {
                    filesForUploadMap.put(uploadingFiles[i].getName(), uploadingFiles[i]);
                }                
            }
            
            // Build map of objects already existing in target S3 bucket.
            Map s3ExistingObjectsMap = FileComparer.populateS3ObjectMap("",
                ((ObjectTableModel)objectsTable.getModel()).getObjects());
                            
            // Compare files being uploaded and existing S3 objects.
            FileComparerResults comparisonResults = 
                FileComparer.buildDiscrepancyLists(filesForUploadMap, s3ExistingObjectsMap);

            // Determine which files to upload, prompting user whether to over-write existing files
            List fileKeysForUpload = new ArrayList();
            fileKeysForUpload.addAll(comparisonResults.onlyOnClientKeys);

            int newFiles = comparisonResults.onlyOnClientKeys.size();
            int unchangedFiles = comparisonResults.alreadySynchronisedKeys.size();
            int changedFiles = comparisonResults.updatedOnClientKeys.size() 
                + comparisonResults.updatedOnServerKeys.size();

            if (unchangedFiles > 0 || changedFiles > 0) {
                // Ask user whether to replace existing unchanged and/or existing changed files.
                log.debug("Files for upload clash with existing S3 objects, prompting user to choose which files to replace");
                List options = new ArrayList();
                String message = "Of the " + filesForUploadMap.keySet().size() 
                    + " file(s) being uploaded:\n\n";
                
                if (newFiles > 0) {
                    message += newFiles + " file(s) are new.\n\n";
                    options.add(UPLOAD_NEW_FILES_ONLY);                    
                }
                if (changedFiles > 0) {
                    message += changedFiles + " file(s) have changed and will be replaced.\n\n";
                    options.add(UPLOAD_NEW_AND_CHANGED_FILES);
                }
                if (unchangedFiles > 0) {
                    message += unchangedFiles + " file(s) already exist and are unchanged.\n\n";
                    options.add(UPLOAD_ALL_FILES);
                }
                message += "Please choose which file(s) you wish to upload:";
                
                Object response = JOptionPane.showInputDialog(
                    ownerFrame, message, "Replace file(s)?", JOptionPane.QUESTION_MESSAGE, 
                    null, options.toArray(), UPLOAD_NEW_AND_CHANGED_FILES);
                
                if (UPLOAD_NEW_FILES_ONLY.equals(response)) {
                    // No change required to default fileKeysForUpload list.
                } else if (UPLOAD_ALL_FILES.equals(response)) {
                    fileKeysForUpload.addAll(comparisonResults.updatedOnClientKeys);
                    fileKeysForUpload.addAll(comparisonResults.updatedOnServerKeys);
                    fileKeysForUpload.addAll(comparisonResults.alreadySynchronisedKeys);
                } else if (UPLOAD_NEW_AND_CHANGED_FILES.equals(response)) {
                    fileKeysForUpload.addAll(comparisonResults.updatedOnClientKeys);
                    fileKeysForUpload.addAll(comparisonResults.updatedOnServerKeys);                    
                } else {
                    // Upload cancelled.
                    stopProgressDisplay();
                    return;
                }
            }

            if (fileKeysForUpload.size() == 0) {
                return;
            }
            
            // Make sure we have an encryption password, in case it's required.
            if (encryptionPasswordUtil == null && prefAutomaticEncryption.isSelected()) {
                if (!initEncryptionUtility()) {
                    throw new S3ServiceException(
                        "Cannot encrypt files for upload without a password");
                }
            }            

            startProgressDisplay("Preparing " + fileKeysForUpload.size() + " file(s) for upload");
            
            // Populate S3Objects representing upload files with metadata etc.
            final S3Object[] objects = new S3Object[fileKeysForUpload.size()];
            int objectIndex = 0;
            for (Iterator iter = fileKeysForUpload.iterator(); iter.hasNext();) {
                String fileKey = iter.next().toString();
                File file = (File) filesForUploadMap.get(fileKey);
                
                S3Object newObject = new S3Object();
                newObject.setKey(fileKey);
                if (file.isDirectory()) {
                    newObject.setContentType(Mimetypes.MIMETYPE_JETS3T_DIRECTORY);
                } else {     
                    newObject.setContentType(Mimetypes.getMimetype(file));
                    
                    updateProgressDisplay("Computing MD5 hash of '" + file + "'", 0);
                    // Store hash of original file data in metadata.
                    log.debug("Computing MD5 hash for file: " + file);
                    newObject.setMd5Hash(FileComparer.computeMD5Hash(
                        new FileInputStream(file)));
                    
                    // Do any necessary file pre-processing.
                    File fileToUpload = prepareUploadFile(file, newObject);
                    
                    newObject.addMetadata(Constants.METADATA_JETS3T_LOCAL_FILE_DATE, 
                        ServiceUtils.formatIso8601Date(new Date(file.lastModified())));
                    newObject.setContentLength(fileToUpload.length());
                    newObject.setDataInputStream(new FileInputStream(fileToUpload));    
                }
                objects[objectIndex++] = newObject;
            }

            
            stopProgressDisplay();
            
            // Upload the files.
            s3ServiceExecutor.createObjects(getCurrentSelectedBucket(), objects);

        } catch (Exception e) {
            reportException(ownerFrame, "Unable to upload file or directory", e);
        } 
    }
    
    public void s3ServiceEventPerformed(CreateObjectsEvent event) {
        if (ServiceEvent.EVENT_STARTED == event.getEventStatus()) {    
            // Show percentage of uploaded bytes if it's non-zero ...
            if (event.getBytesTotal() > 0) {
                startProgressDisplay("Uploading files to " + event.getBucket().getName(), 
                        0, 100, "Cancel file uploads", 
                        event.getProgressStatus().getCancelEventListener());
            } 
            // ... otherwise show the number of completed threads.
            else {
                startProgressDisplay("Uploading files to " + event.getBucket().getName(), 
                        0, event.getProgressStatus().getThreadCount(), "Cancel file uploads",  
                        event.getProgressStatus().getCancelEventListener());                
            }
        } 
        else if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventStatus()) {
            for (int i = 0; i < event.getObjects().length; i++) {
                ((ObjectTableModel)objectsTable.getModel()).addObject(event.getObjects()[i], false);
            }
            
            // Show percentage of uploaded bytes if it's non-zero ...
            if (event.getBytesTotal() > 0) {
                if (event.getBytesCompleted() == event.getBytesTotal()) {
                    // Upload is completed, just waiting on resonse from S3.
                    String statusText = "Upload completed, awaiting confirmation";
                    updateProgressDisplay(statusText, 100);
                } else {                    
                    String bytesCompletedStr = formatByteSize(event.getBytesCompleted());
                    String bytesTotalStr = formatByteSize(event.getBytesTotal());
                    String statusText = "Uploaded " + bytesCompletedStr + " of " + bytesTotalStr;
                    long percentage = (int) (((double)event.getBytesCompleted() / event.getBytesTotal()) * 100);
                    updateProgressDisplay(statusText, percentage);
                }
            }
            // ... otherwise show the number of completed threads.
            else {
                ProgressStatus progressStatus = event.getProgressStatus();
                String statusText = "Finished uploading file " + progressStatus.getCompletedThreads() + " of " + progressStatus.getThreadCount();                    
                updateProgressDisplay(statusText, progressStatus.getCompletedThreads());                    
            }
        }
        else if (ServiceEvent.EVENT_COMPLETED == event.getEventStatus()) {
            updateObjectsSummary();
            
            S3Object[] allObjects = ((ObjectTableModel)objectsTable.getModel()).getObjects();
            cachedBuckets.put(getCurrentSelectedBucket().getName(), allObjects);
            
            stopProgressDisplay();        
        }
        else if (ServiceEvent.EVENT_CANCELLED == event.getEventStatus()) {
            updateObjectsSummary();

            stopProgressDisplay();        
        }
        else if (ServiceEvent.EVENT_ERROR == event.getEventStatus()) {
            stopProgressDisplay();
            reportException(ownerFrame, "createObjects", event.getErrorCause());
        }
    }
        
    private void deleteSelectedObjects() {
        final S3Object[] objects = getSelectedObjects(); 

        if (objects.length == 0) {
            System.err.println("Ignoring delete object(s) command, no currently selected objects");
            return;            
        }

        int response = JOptionPane.showConfirmDialog(ownerFrame, 
            (objects.length == 1 ? 
                "Are you sure you want to delete '" + objects[0].getKey() + "'?" :
                "Are you sure you want to delete " + objects.length + " object(s)"
            ),  
            "Delete Object(s)?", JOptionPane.YES_NO_OPTION);
            
        if (response == JOptionPane.NO_OPTION) {
            return;
        }

        new Thread() {
            public void run() {
                s3ServiceExecutor.deleteObjects(getCurrentSelectedBucket(), objects);
            }
        }.start();        
    }
    
    public void s3ServiceEventPerformed(DeleteObjectsEvent event) {
        if (ServiceEvent.EVENT_STARTED == event.getEventStatus()) {    
            startProgressDisplay("Deleting " + event.getProgressStatus().getThreadCount() + " object(s) in "
                + getCurrentSelectedBucket().getName(), 
                0, event.getProgressStatus().getThreadCount(), "Cancel Delete Objects",  
                event.getProgressStatus().getCancelEventListener());
        } 
        else if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventStatus()) {
            for (int i = 0; i < event.getObjects().length; i++) {
                ((ObjectTableModel)objectsTable.getModel()).removeObject(
                    event.getObjects()[i]);
            }
            
            ProgressStatus progressStatus = event.getProgressStatus();
            String statusText = "Deleted " + progressStatus.getCompletedThreads() + " of " + progressStatus.getThreadCount() + " objects";
            updateProgressDisplay(statusText, progressStatus.getCompletedThreads());                
        }
        else if (ServiceEvent.EVENT_COMPLETED == event.getEventStatus()) {
            updateObjectsSummary();
            
            S3Object[] allObjects = ((ObjectTableModel)objectsTable.getModel()).getObjects();
            cachedBuckets.put(getCurrentSelectedBucket().getName(), allObjects);

            stopProgressDisplay();                
        }
        else if (ServiceEvent.EVENT_CANCELLED == event.getEventStatus()) {
            listObjects(); // Refresh object listing.
            stopProgressDisplay();        
        }
        else if (ServiceEvent.EVENT_ERROR == event.getEventStatus()) {
            listObjects(); // Refresh object listing.
            stopProgressDisplay();
            reportException(ownerFrame, "deleteObjects", event.getErrorCause());
        }
    }
    
    private void retrieveObjectsDetails(final S3Object[] candidateObjects) {
        // Identify which of the candidate objects have incomplete metadata.
        ArrayList s3ObjectsIncompleteList = new ArrayList();
        for (int i = 0; i < candidateObjects.length; i++) {
            if (!candidateObjects[i].isMetadataComplete()) {
                s3ObjectsIncompleteList.add(candidateObjects[i]);
            }
        }
        
        log.debug("Of " + candidateObjects.length + " object candidates for HEAD requests "
            + s3ObjectsIncompleteList.size() + " are incomplete, performing requests for these only");
        
        final S3Object[] incompleteObjects = (S3Object[]) s3ObjectsIncompleteList.toArray(new S3Object[] {});        
        (new Thread() {
            public void run() {
                s3ServiceExecutor.getObjectsHeads(getCurrentSelectedBucket(), incompleteObjects);
            };
        }).start();
    }
    
    public void s3ServiceEventPerformed(GetObjectHeadsEvent event) {
        if (ServiceEvent.EVENT_STARTED == event.getEventStatus()) {
            startProgressDisplay("Retrieving details of " + event.getProgressStatus().getThreadCount() 
                + " object(s) from " + getCurrentSelectedBucket().getName(), 
                0, event.getProgressStatus().getThreadCount(), "Cancel Retrieval", 
                event.getProgressStatus().getCancelEventListener());
        } 
        else if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventStatus()) {
            // Store detail-complete objects in table.
            for (int i = 0; i < event.getObjects().length; i++) {
                // Retain selected status of objects for downloads or properties 
                boolean highlightUpdatedObjects = downloadingObjects || viewingObjectProperties; 
                ((ObjectTableModel)objectsTable.getModel()).addObject(event.getObjects()[i], highlightUpdatedObjects);
                log.debug("Updated table with " + event.getObjects()[i].getKey() + ", content-type=" + event.getObjects()[i].getContentType());                
            }            
            
            // Update progress of GetObject requests.
            ProgressStatus progressStatus = event.getProgressStatus();
            String statusText = "Retrieved details of " + progressStatus.getCompletedThreads() 
                + " of " + progressStatus.getThreadCount() + " object(s)";
            updateProgressDisplay(statusText, progressStatus.getCompletedThreads());                    
        }
        else if (ServiceEvent.EVENT_COMPLETED == event.getEventStatus()) {
            // Stop GetObjectHead progress display.
            stopProgressDisplay();        
            
            if (downloadingObjects) {
                performObjectsDownload();
                downloadingObjects = false;
            } else if (uploadingObjects) {
                performObjectsUpload();
                uploadingObjects = false;
            } else if (viewingObjectProperties) {
                PropertiesDialog.showDialog(ownerFrame, getSelectedObjects()[0]);
                viewingObjectProperties = false;                    
            }            
        }
        else if (ServiceEvent.EVENT_CANCELLED == event.getEventStatus()) {
            stopProgressDisplay();        
        }
        else if (ServiceEvent.EVENT_ERROR == event.getEventStatus()) {
            stopProgressDisplay();
            reportException(ownerFrame, "getObjectHeads", event.getErrorCause());
        }
    }
    
    private String formatByteSize(long byteSize) {
        NumberFormatter nf = new NumberFormatter(new DecimalFormat("0.00"));
        
        String result = "???";
        try {
            if (byteSize > Math.pow(1024,3)) {
                // Report gigabytes
                result = nf.valueToString(new Double(byteSize / Math.pow(1024,3))) + " GB";
            } else if (byteSize > Math.pow(1024,2)) {
                // Report megabytes
                result = nf.valueToString(new Double(byteSize / Math.pow(1024,2))) + " MB";
            } else if (byteSize > 1024) {
                // Report kilobytes
                result = nf.valueToString(new Double(byteSize / Math.pow(1024,1))) + " KB";                    
            } else if (byteSize >= 0) {
                // Report bytes                
                result = byteSize + " byte" + (byteSize == 1? "" : "s");
            } 
        } catch (ParseException e) {
            reportException(ownerFrame, "Unable to format byte size " + byteSize, e);
        }
        return result;
    }    
    
        
    // TODO Remove
    public static void reportException(Frame ownerFrame, String message, Throwable t) {
        System.err.println(message);
        t.printStackTrace(System.err);

        // Show error dialog box.
        String detailsText = null;
        if (t instanceof S3ServiceException) {
            S3ServiceException s3se = (S3ServiceException) t;
            if (s3se.getErrorCode() != null) {
                detailsText = "S3 Error Code: " + s3se.getErrorCode();
            } else {
                detailsText = s3se.getMessage();
                
                Throwable cause = s3se.getCause();
                while (cause != null) {
                    detailsText += "\nCaused by: " + cause;
                    cause = cause.getCause();
                }
            }
        } else {
            detailsText = "Error details: " + t.getMessage();
        }
        JOptionPane.showMessageDialog(ownerFrame, message + "\n" + detailsText, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    
    private class BucketTableModel extends DefaultTableModel {
        ArrayList bucketList = new ArrayList();
        
        public BucketTableModel() {
            super(new String[] {"Buckets"}, 0);
        }
        
        public int addBucket(S3Bucket bucket) {
            int insertRow = 
                Collections.binarySearch(bucketList, bucket, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        String b1Name = ((S3Bucket)o1).getName();
                        String b2Name = ((S3Bucket)o2).getName();
                        int result =  b1Name.compareToIgnoreCase(b2Name);
                        return result;
                    }
                });
            if (insertRow >= 0) {
                // We already have an item with this key, replace it.
                bucketList.remove(insertRow);
                this.removeRow(insertRow);                
            } else {
                insertRow = (-insertRow) - 1;                
            }
            // New object to insert.
            bucketList.add(insertRow, bucket);
            this.insertRow(insertRow, new Object[] {bucket.getName()});
            return insertRow;
        }
        
        public void removeBucket(S3Bucket bucket) {
            int index = bucketList.indexOf(bucket);
            this.removeRow(index);
            bucketList.remove(bucket);
        }
        
        public void removeAllBuckets() {
            int rowCount = this.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                this.removeRow(0);
            }
            bucketList.clear();
        }
        
        public S3Bucket getBucket(int row) {
            return (S3Bucket) bucketList.get(row);
        }
        
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    public class ObjectTableModel extends DefaultTableModel {
        ArrayList objectList = new ArrayList();
        
        public ObjectTableModel() {
            super(new String[] {"Object Key","Size","Last Modified"}, 0);
        }
        
        public void addObject(S3Object object, boolean highlightNewObject) {
            synchronized (objectList) {
                int insertRow = 
                    Collections.binarySearch(objectList, object, new Comparator() {
                        public int compare(Object o1, Object o2) {
                            return ((S3Object)o1).getKey().compareToIgnoreCase(((S3Object)o2).getKey());
                        }
                    });
                if (insertRow >= 0) {
                    // We already have an item with this key, replace it.
                    objectList.remove(insertRow);
                    this.removeRow(insertRow);                
                } else {
                    insertRow = (-insertRow) - 1;                
                }
                // New object to insert.
                objectList.add(insertRow, object);
                this.insertRow(insertRow, new Object[] {object.getKey(), formatByteSize(object.getContentLength()), 
                    object.getLastModifiedDate() /*, object.getHash(), object.getStorageClass()*/});
                
                // TODO Better way to do this?
                if (highlightNewObject) {
                    // Give feedback on where the new items went.
//                    objectsTable.scrollToCell(insertRow, 0);
                    objectsTable.addRowSelectionInterval(insertRow, insertRow);
                }
            }
        }
        
        public void addObjects(S3Object[] objects, boolean highlightNewObject) {
            for (int i = 0; i < objects.length; i++) {
                addObject(objects[i], highlightNewObject);
            }
        }
        
        public void removeObject(S3Object object) {
            synchronized (objectList) {
                int index = objectList.indexOf(object);
                this.removeRow(index);
                objectList.remove(object);
            }
        }
        
        public void removeAllObjects() {
            synchronized (objectList) {
                int rowCount = this.getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    this.removeRow(0);
                }
                objectList.clear();
            }
        }
        
        public S3Object getObject(int row) {
            synchronized (objectList) {
                return (S3Object) objectList.get(row);
            }
        }
        
        public S3Object[] getObjects() {
            synchronized (objectList) {
                return (S3Object[]) objectList.toArray(new S3Object[] {});
            }            
        }
        
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
    
    private class ContextMenuListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            showContextMenu(e);
        }

        public void mouseReleased(MouseEvent e) {
            showContextMenu(e);
        }
        
        private void showContextMenu(MouseEvent e) {
            if (e.isPopupTrigger()) {
                // Select item under context-click.
                if (e.getSource() instanceof JList) {
                    JList jList = (JList) e.getSource();
                    int locIndex = jList.locationToIndex(e.getPoint());
                    if (locIndex >= 0) {
                        jList.setSelectedIndex(locIndex);
                    }
                } else if (e.getSource() instanceof JTable) {
                    JTable jTable = (JTable) e.getSource();
                    int rowIndex = jTable.rowAtPoint(e.getPoint());
                    if (rowIndex >= 0) {
                        jTable.addRowSelectionInterval(rowIndex, rowIndex);
                    }                
                }

                // Show context popup menu.                
                if (e.getSource().equals(bucketsTable)) {
                    showBucketPopupMenu((JComponent)e.getSource(), e.getX(), e.getY());
                } else if (e.getSource().equals(objectsTable)) {
                    showObjectPopupMenu((JComponent)e.getSource(), e.getX(), e.getY());
                }
            }
        }
    }
    
    
    public static void main(String args[]) throws Exception {
        JFrame ownerFrame = new JFrame("jetS3T Manager");
        ownerFrame.setName("jetS3T Manager");
        
        Cockpit cockpit = new Cockpit(ownerFrame);
        ownerFrame.getContentPane().add(cockpit);
        ownerFrame.setBounds(cockpit.getBounds());
        ownerFrame.setVisible(true);                        
    }

}
