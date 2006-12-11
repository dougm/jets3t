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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.NoSuchPaddingException;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.gui.HyperlinkActivatedListener;
import org.jets3t.service.Constants;
import org.jets3t.service.S3ObjectsChunk;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.io.GZipDeflatingInputStream;
import org.jets3t.service.io.GZipInflatingOutputStream;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.multithread.CancelEventTrigger;
import org.jets3t.service.multithread.CreateBucketsEvent;
import org.jets3t.service.multithread.CreateObjectsEvent;
import org.jets3t.service.multithread.DeleteObjectsEvent;
import org.jets3t.service.multithread.DownloadObjectsEvent;
import org.jets3t.service.multithread.GetObjectHeadsEvent;
import org.jets3t.service.multithread.GetObjectsEvent;
import org.jets3t.service.multithread.LookupACLEvent;
import org.jets3t.service.multithread.S3ObjectAndOutputStream;
import org.jets3t.service.multithread.S3ServiceEventListener;
import org.jets3t.service.multithread.S3ServiceMulti;
import org.jets3t.service.multithread.ServiceEvent;
import org.jets3t.service.multithread.ThreadWatcher;
import org.jets3t.service.multithread.UpdateACLEvent;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.security.EncryptionUtil;
import org.jets3t.service.utils.ByteFormatter;
import org.jets3t.service.utils.FileComparer;
import org.jets3t.service.utils.FileComparerResults;
import org.jets3t.service.utils.Mimetypes;
import org.jets3t.service.utils.ServiceUtils;
import org.jets3t.service.utils.TimeFormatter;

import com.centerkey.utils.BareBonesBrowserLaunch;

/**
 * Cockpit is a graphical Java application for viewing and managing the contents of an Amazon S3 account.
 * For more information and help please see the 
 * <a href="http://jets3t.dev.java.net/cockpit.html">Cockpit Guide</a>.
 * <p>
 * This is the Cockpit application class; it may be run as a stand-alone application or as an Applet.
 * 
 * @author jmurty
 */
public class Cockpit extends JApplet implements S3ServiceEventListener, ActionListener, ListSelectionListener, HyperlinkActivatedListener {
    private static final long serialVersionUID = 8122461453115708538L;

    private static final Log log = LogFactory.getLog(Cockpit.class);
    
    public static final String APPLICATION_DESCRIPTION = "Cockpit/0.5.0";
    
    public static final String APPLICATION_TITLE = "jets3t Cockpit";
    private static final int BUCKET_LIST_CHUNKING_SIZE = 500;
    
    private File rememberedLoginsDirectory = Constants.DEFAULT_PREFERENCES_DIRECTORY;
    
    private final Insets insetsZero = new Insets(0, 0, 0, 0);
    private final Insets insetsDefault = new Insets(5, 7, 5, 7);

    private final ByteFormatter byteFormatter = new ByteFormatter();
    private final TimeFormatter timeFormatter = new TimeFormatter();
    private final SimpleDateFormat yearAndTimeSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private final SimpleDateFormat timeSDF = new SimpleDateFormat("HH:mm:ss");
    
    /**
     * Multi-threaded S3 service used by the application.
     */
    private S3ServiceMulti s3ServiceMulti = null;
    
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
    private JMenuItem generatePublicUrl = null;
    private JMenuItem generateTorrentUrl = null;
    private JMenuItem deleteObjectMenuItem = null;

    // Preference menu items.
    private JCheckBoxMenuItem prefAutomaticGzip = null;
    private JCheckBoxMenuItem prefAutomaticEncryption = null;
    private JMenuItem prefEncryptionPassword = null;
    
    // Help menu items.
    private JMenuItem cockpitHelpMenuItem = null;
    private JMenuItem amazonS3HelpMenuItem = null;
    
    // Tables
    private JTable bucketsTable = null;
    private JTable objectsTable = null;
    private JScrollPane objectsTableSP = null;
    
    private JLabel objectsSummaryLabel = null;
        
    private HashMap cachedBuckets = new HashMap();
    private ProgressDisplay progressDisplay = null;
    
    // Preferences selected.
    private String preferenceEncryptionPassword = null;
    
    // Class variables used for uploading or downloading files.
    private File downloadDirectory = null;
    private Map downloadObjectsToFileMap = null;
    private boolean downloadingObjects = false;    
    
    // File comparison options
    private final String UPLOAD_NEW_FILES_ONLY = "Only upload new file(s)";
    private final String UPLOAD_NEW_AND_CHANGED_FILES = "Upload new and changed file(s)";
    private final String UPLOAD_ALL_FILES = "Upload all files";
    private final String DOWNLOAD_NEW_FILES_ONLY = "Only download new file(s)";
    private final String DOWNLOAD_NEW_AND_CHANGED_FILES = "Download new and changed file(s)";
    private final String DOWNLOAD_ALL_FILES = "Download all files";
    
    /**
     * Flag used to indicate the "viewing objects" application state.
     */
    private boolean viewingObjectProperties = false;
    
    
    /**
     * Constructor to run this application as an Applet.
     */
    public Cockpit() {
    }
            
    /**
     * Constructor to run this application in a stand-alone window.
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
     * Prepares application to run as a GUI by finding/creating a root owner JFrame, and 
     * (if necessary) creating a directory for storing remembered logins.
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
        if (!rememberedLoginsDirectory.exists()) {
            log.info("Creating remembered logins directory " + rememberedLoginsDirectory);
            rememberedLoginsDirectory.mkdir();
        }
        
        // Initialise the GUI.
        initGui();      

        // Initialise a non-authenticated service.
        try {
            // Revert to anonymous service.
            s3ServiceMulti = new S3ServiceMulti(
                new RestS3Service(null, APPLICATION_DESCRIPTION, null), this);
        } catch (S3ServiceException e2) {
            reportException(ownerFrame, "Unable to start anonymous service", e2);
        }
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
            private static final long serialVersionUID = 8990149746208400183L;

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Date) {
                    Date date = (Date) value;
                    return super.getTableCellRendererComponent(table, yearAndTimeSDF.format(date), isSelected, hasFocus, row, column);                    
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

        generatePublicUrl = new JMenuItem("Generate Public URL...");
        generatePublicUrl.setActionCommand("GeneratePublicURL");
        generatePublicUrl.addActionListener(this);
        objectMenu.add(generatePublicUrl);        
        
        generateTorrentUrl = new JMenuItem("Generate Torrent URL...");
        generateTorrentUrl.setActionCommand("GenerateTorrentURL");
        generateTorrentUrl.addActionListener(this);
        objectMenu.add(generateTorrentUrl);        

        objectMenu.add(new JSeparator());

        deleteObjectMenuItem = new JMenuItem("Delete selected object(s)...");
        deleteObjectMenuItem.setActionCommand("DeleteObjects");
        deleteObjectMenuItem.addActionListener(this);
        objectMenu.add(deleteObjectMenuItem);

        viewObjectPropertiesMenuItem.setEnabled(false);
        refreshObjectMenuItem.setEnabled(false);
        updateObjectACLMenuItem.setEnabled(false);
        downloadObjectMenuItem.setEnabled(false);
        generatePublicUrl.setEnabled(false);
        generateTorrentUrl.setEnabled(false);
        deleteObjectMenuItem.setEnabled(false);

        // Preferences menu.        
        JMenu preferencesMenu = new JMenu("Preferences");
        
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
        cockpitHelpMenuItem = new JMenuItem("Cockpit Guide");
        cockpitHelpMenuItem.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               try {
                   followHyperlink(new URL(Constants.JETS3T_COCKPIT_HELP_PAGE), "_blank");
               } catch (MalformedURLException ex) {
                   reportException(ownerFrame, "Unable to follow hyperlink to invalid URL", ex);
               }
           } 
        });
        helpMenu.add(cockpitHelpMenuItem);
        amazonS3HelpMenuItem = new JMenuItem("Amazon S3");
        amazonS3HelpMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    followHyperlink(new URL(Constants.AMAZON_S3_PAGE), "_blank");
                } catch (MalformedURLException ex) {
                    reportException(ownerFrame, "Unable to follow hyperlink to invalid URL", ex);
                }
            } 
         });
        helpMenu.add(amazonS3HelpMenuItem);
        
        
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
        startProgressDisplay(statusText, null, 0, 0, null, null);
    }

    /**
     * Starts a progress display dialog. While the dialog is running the user cannot interact
     * with the application, except to cancel the task.
     * 
     * @param statusText
     *        describes the status of a task text meaningful to the user, such as "3 files of 7 uploaded"
     * @param detailsText
     *        describes the status of a task in more detail, such as the current transfer rate and ETA.
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
    private void startProgressDisplay(final String statusText, final String detailsText, 
        final long minValue, final long maxValue, final String cancelButtonText, 
        final CancelEventTrigger cancelEventListener) 
    {
       if (progressDisplay == null || !progressDisplay.isActive()) {
           progressDisplay = new ProgressDisplay(ownerFrame, "Please wait...", statusText, 
               null, (int) minValue, (int) maxValue, cancelButtonText, cancelEventListener);
           progressDisplay.startDialog();
           
           this.getContentPane().setCursor(new Cursor(Cursor.WAIT_CURSOR));
       }
    }
    
    private void startProgressDisplay(final String statusText, final long minValue, 
        final long maxValue, final String cancelButtonText, 
        final CancelEventTrigger cancelEventListener) 
    {
        startProgressDisplay(statusText, null, minValue, maxValue, cancelButtonText, cancelEventListener);
    }
    
    /**
     * Updates the status text and value of the progress display dialog.
     * @param statusText
     *        describes the status of a task text meaningful to the user, such as "3 files of 7 uploaded"
     * @param detailsText
     *        describes the status of a task in more detail, such as the current transfer rate and ETA.
     * @param currentValue
     *        value representing how far through the task we are (relative to min and max values)
     */
    private void updateProgressDisplay(final String statusText, final String detailsText, final long currentValue) {
        if (progressDisplay != null && progressDisplay.isActive()) {
            if (currentValue > 0) {
                progressDisplay.updateProgress((int) currentValue);                
            }
            progressDisplay.updateStatusMessages(statusText, detailsText);
        }
    }
    
    private void updateProgressDisplay(final String statusText, final long currentValue) {
        this.updateProgressDisplay(statusText, null, currentValue);
    }
    
    /**
     * Stops/halts the progress display dialog and allows the user to interact with the application.
     */
    private void stopProgressDisplay() {
        if (progressDisplay != null) {
            progressDisplay.dispose();
            progressDisplay = null;
        }
        
        this.setEnabled(true);
        this.getContentPane().setCursor(null);        
    }
        
    /**
     * Event handler for this application, handles all menu items.
     */
    public void actionPerformed(ActionEvent event) {
        // Service Menu Events            
        if ("LoginEvent".equals(event.getActionCommand())) {
            loginEvent();
        } else if ("LogoutEvent".equals(event.getActionCommand())) {
            logoutEvent();
        } else if ("QuitEvent".equals(event.getActionCommand())) {
            ownerFrame.dispose();
            System.exit(0);
        } 
        
        // Bucket Events.
        else if ("ViewBucketProperties".equals(event.getActionCommand())) {
            listBucketProperties();
        } else if ("RefreshBuckets".equals(event.getActionCommand())) {
            listAllBuckets();
        } else if ("CreateBucket".equals(event.getActionCommand())) {
            createBucketAction();
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
        } else if ("GeneratePublicURL".equals(event.getActionCommand())) {
            generatePublicUrl();
        } else if ("GenerateTorrentURL".equals(event.getActionCommand())) {
            generateTorrentUrl();
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
        else if ("PreferenceEncryptFiles".equals(event.getActionCommand())) {
            prefEncryptionPassword.setEnabled(prefAutomaticEncryption.isSelected());
        } else if ("PreferenceSetEncryptionPassword".equals(event.getActionCommand())) {
            promptForPassword();
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
            bucketSelectedAction();
        } else if (e.getSource().equals(objectsTable.getSelectionModel())) {
            objectSelectedAction();
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
    private void promptForPassword() {
        preferenceEncryptionPassword = PasswordDialog.showDialog(
            ownerFrame, "Enter encryption password", 
            "Please enter your file encryption password", true);
    }
            
    /**
     * Displays the {@link LoginDialog} dialog and, if the user provides login credentials,
     * logs into the S3 service using those credentials.
     */
    private void loginEvent() {
        try {
            final AWSCredentials awsCredentials = 
                LoginDialog.showDialog(ownerFrame, rememberedLoginsDirectory);

            s3ServiceMulti = new S3ServiceMulti(
                new RestS3Service(awsCredentials, APPLICATION_DESCRIPTION, null), this);

            if (awsCredentials == null) {
                log.debug("Log in cancelled by user");
                return;
            } 

            listAllBuckets(); // Doubles as check for valid credentials.            
            updateObjectsSummary(false);
            
            ownerFrame.setTitle(APPLICATION_TITLE + " : " + awsCredentials.getAccessKey());
            loginMenuItem.setEnabled(false);
            logoutMenuItem.setEnabled(true);
            
            refreshBucketMenuItem.setEnabled(true);
            createBucketMenuItem.setEnabled(true);
        } catch (Exception e) {
            reportException(ownerFrame, "Unable to Log in", e);
            try {
                // Revert to anonymous service.
                s3ServiceMulti = new S3ServiceMulti(
                    new RestS3Service(null, APPLICATION_DESCRIPTION, null), this);
            } catch (S3ServiceException e2) {
                reportException(ownerFrame, "Unable to revert to anonymous user", e2);
            }
        }
    }
    
    /**
     * Logs out of the S3 service by clearing all listed objects and buckets and resetting
     * the s3ServiceMulti member variable.
     */
    private void logoutEvent() {
        log.debug("Logging out");
        try {
            // Revert to anonymous service.
            s3ServiceMulti = new S3ServiceMulti(
                new RestS3Service(null, APPLICATION_DESCRIPTION, null), this);
            
            bucketsTable.clearSelection();
            ((BucketTableModel)bucketsTable.getModel()).removeAllBuckets();
            ((ObjectTableModel)objectsTable.getModel()).removeAllObjects();
                        
            updateObjectsSummary(false);

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
     * Starts a thread to run {@link S3ServiceMulti#listAllBuckets}.
     */
    private void listAllBuckets() {
        // This is all very convoluted, it was done this way to ensure we can display the dialog box.
        
        new Thread() {
            public void run() {
                try {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            startProgressDisplay("Listing buckets for " + s3ServiceMulti.getAWSCredentials().getAccessKey());
                            cachedBuckets.clear();
                            bucketsTable.clearSelection();       
                            ((BucketTableModel)bucketsTable.getModel()).removeAllBuckets();
                            ((ObjectTableModel)objectsTable.getModel()).removeAllObjects();   
                        }
                    });                   
                    
                    final S3Bucket[] buckets = s3ServiceMulti.getS3Service().listAllBuckets();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            for (int i = 0; i < buckets.length; i++) {
                                ((BucketTableModel)bucketsTable.getModel()).addBucket(buckets[i]);
                            }
                        }
                    });
                } catch (final Exception e) {
                    logoutEvent();
                    reportException(ownerFrame, "Unable to list your buckets", e);
                } finally {
                    stopProgressDisplay();                    
                }
            };
        }.start();        
    }
    
    /**
     * This method is an {@link S3ServiceEventListener} action method that is invoked when this 
     * application's <code>S3ServiceMulti</code> triggers a <code>GetObjectsEvent</code>.
     * <p>
     * <b>This never happens in this application.</b>
     * 
     * @param event
     */
    public void s3ServiceEventPerformed(GetObjectsEvent event) {
        // Not used.
    }
            
    /**
     * Actions performed when a bucket is selected in the bucket list table.
     */
    private void bucketSelectedAction() {
        S3Bucket newlySelectedBucket = getCurrentSelectedBucket();
        if (newlySelectedBucket == null) {
            viewBucketPropertiesMenuItem.setEnabled(false);
            refreshBucketMenuItem.setEnabled(true);
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
            
            ((ObjectTableModel)objectsTable.getModel()).removeAllObjects();                    
            ((ObjectTableModel)objectsTable.getModel()).addObjects(objects, false);
            updateObjectsSummary(false);
        } else {        
            listObjects();
        }
    }
    
    /**
     * Actions performed when an object is selected in the objects list table.
     */
    private void objectSelectedAction() {
        int count = getSelectedObjects().length;
        
        updateObjectACLMenuItem.setEnabled(count > 0);
        downloadObjectMenuItem.setEnabled(count > 0);
        deleteObjectMenuItem.setEnabled(count > 0);
        viewObjectPropertiesMenuItem.setEnabled(count == 1);
        generatePublicUrl.setEnabled(count == 1);
        generateTorrentUrl.setEnabled(count == 1);
    }

    /**
     * Starts a thread to run {@link S3ServiceMulti#listObjects}.
     */
    private void listObjects() {
        // This is all very convoluted, it was done this way to ensure we can display the dialog box.
        
        new Thread() {
            public void run() {
                try {
                    final boolean listingCancelled[] = new boolean[1]; // Default to false.
                    final CancelEventTrigger cancelListener = new CancelEventTrigger() {
                        public void cancelTask(Object eventSource) {
                            listingCancelled[0] = true;                            
                        }                        
                    };
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            startProgressDisplay("Listing objects in " + getCurrentSelectedBucket().getName(),
                                0, 0, "Cancel bucket listing", cancelListener);
                            ((ObjectTableModel)objectsTable.getModel()).removeAllObjects();                                                
                        }
                    });

                    final ArrayList allObjects = new ArrayList();
                    String priorLastKey = null;
                    do {
                        S3ObjectsChunk chunk = s3ServiceMulti.getS3Service().listObjectsChunked(
                            getCurrentSelectedBucket().getName(), null, null, 
                            BUCKET_LIST_CHUNKING_SIZE, priorLastKey);
                        
                        final S3Object[] objects = chunk.getObjects();
                        priorLastKey = chunk.getPriorLastKey();
                        allObjects.addAll(Arrays.asList(objects));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                ((ObjectTableModel)objectsTable.getModel()).addObjects(objects, false);
                                updateObjectsSummary(true);
                                updateProgressDisplay("Listed " + allObjects.size() + " objects in " 
                                    + getCurrentSelectedBucket().getName(), 0);
                            }
                        });                        
                    } while (!listingCancelled[0] && priorLastKey != null);

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            updateObjectsSummary(listingCancelled[0]);
                            S3Object[] allObjects = ((ObjectTableModel)objectsTable.getModel()).getObjects();
                            cachedBuckets.put(getCurrentSelectedBucket().getName(), allObjects);
                        }
                    });                        

                } catch (final Exception e) {
                    logoutEvent();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            reportException(ownerFrame, "listObjects", e);
                        }
                    });                    
                } finally {
                    stopProgressDisplay();
                }
            };
        }.start();                
    }
    
    /**
     * Updates the summary text shown below the listing of objects, which details the
     * number and total size of the objects. 
     *
     */
    private void updateObjectsSummary(boolean isIncompleteListing) {
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
                    summary += ", " + byteFormatter.formatByteSize(totalBytes);
                }
                summary += "  @ " + timeSDF.format(new Date());
                if (isIncompleteListing) {
                    summary += " - INCOMPLETE";
                }
            }        
            
            objectsSummaryLabel.setText(summary);
        } catch (Throwable t) {
            reportException(ownerFrame, "Unable to update object list summary", t);
        }
    }
    
    /**
     * Displays bucket-specific actions in a popup menu.
     * @param invoker the component near which the popup menu will be displayed
     * @param xPos the mouse's horizontal co-ordinate when the popup menu was invoked 
     * @param yPos the mouse's vertical co-ordinate when the popup menu was invoked
     */
    private void showBucketPopupMenu(JComponent invoker, int xPos, int yPos) {
        if (s3ServiceMulti == null) {
            return;
        }
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem mi0 = new JMenuItem("View bucket properties...");
        mi0.setActionCommand("ViewBucketProperties");
        mi0.addActionListener(this);
        menu.add(mi0);
        
        JMenuItem mi1 = new JMenuItem("Refresh object listing");
        mi1.setActionCommand("RefreshObjects");
        mi1.addActionListener(this);
        menu.add(mi1);

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
    
    /**
     * @return the bucket currently selected in the gui, null if no bucket is selected.
     */
    private S3Bucket getCurrentSelectedBucket() {
        if (bucketsTable.getSelectedRows().length == 0) {
            return null;
        } else {
            return ((BucketTableModel)bucketsTable.getModel()).getBucket(
                    bucketsTable.getSelectedRows()[0]);
        }
    }
    
    /**
     * Displays object-specific actions in a popup menu.
     * @param invoker the component near which the popup menu will be displayed
     * @param xPos the mouse's horizontal co-ordinate when the popup menu was invoked 
     * @param yPos the mouse's vertical co-ordinate when the popup menu was invoked
     */
    private void showObjectPopupMenu(JComponent invoker, int xPos, int yPos) {
        if (getCurrentSelectedBucket() == null || getSelectedObjects().length == 0) {
            return;
        }
        
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem mi0 = new JMenuItem("View object properties...");
        mi0.setActionCommand("ViewObjectProperties");
        mi0.addActionListener(this);
        menu.add(mi0);
        if (objectsTable.getSelectedRows().length != 1) {
            mi0.setEnabled(false);
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

        JMenuItem mi4 = new JMenuItem("Generate Public URL...");
        mi4.setActionCommand("GeneratePublicURL");
        mi4.addActionListener(this);
        menu.add(mi4);
        if (objectsTable.getSelectedRows().length != 1) {
            mi4.setEnabled(false);
        }        

        JMenuItem mi5 = new JMenuItem("Generate Torrent URL...");
        mi5.setActionCommand("GenerateTorrentURL");
        mi5.addActionListener(this);
        menu.add(mi5);
        if (objectsTable.getSelectedRows().length != 1) {
            mi5.setEnabled(false);
        }        

        menu.add(new JSeparator());

        JMenuItem mi6 = null;
        if (objectsTable.getSelectedRows().length == 1) {
            S3Object object = 
                ((ObjectTableModel)objectsTable.getModel()).getObject(
                objectsTable.getSelectedRows()[0]);
            mi6 = new JMenuItem("Delete '" + object.getKey() + "'...");
        } else {
            mi6 = new JMenuItem("Delete " + objectsTable.getSelectedRows().length + " objects...");
        }        
        mi6.setActionCommand("DeleteObjects");
        mi6.addActionListener(this);
        menu.add(mi6);
        
        menu.show(invoker, xPos, yPos);
    }
    
    /**
     * Action to create a new bucket in S3 after prompting the user for a bucket name.
     *
     */
    private void createBucketAction() {
        String proposedNewName = 
                s3ServiceMulti.getAWSCredentials().getAccessKey() + "." + "NewBucket";

        final String bucketName = (String) JOptionPane.showInputDialog(ownerFrame, 
            "Name for new bucket (no spaces allowed):",
            "Create a new bucket", JOptionPane.QUESTION_MESSAGE,
            null, null, proposedNewName);

        if (bucketName != null) {
            new Thread() {
                public void run() {
                    s3ServiceMulti.createBuckets(
                        new S3Bucket[] { new S3Bucket(bucketName) });
                }
            }.start();        
            
        }            
    }
        
    /**
     * This method is an {@link S3ServiceEventListener} action method that is invoked when this 
     * application's <code>S3ServiceMulti</code> triggers a <code>CreateBucketsEvent</code>.
     * <p>
     * When a bucket is successfully created it is added to the listing of buckets.
     * 
     * @param event
     */
    public void s3ServiceEventPerformed(final CreateBucketsEvent event) {
        if (ServiceEvent.EVENT_STARTED == event.getEventCode()) {    
            startProgressDisplay("Creating " + event.getThreadWatcher().getThreadCount() + " buckets",                     
                0, event.getThreadWatcher().getThreadCount(), 
                "Cancel bucket creation", event.getThreadWatcher().getCancelEventListener());
        } 
        else if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventCode()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    for (int i = 0; i < event.getCreatedBuckets().length; i++) {                
                        int insertRow = ((BucketTableModel) bucketsTable.getModel()).addBucket(
                            event.getCreatedBuckets()[i]);
                        bucketsTable.setRowSelectionInterval(insertRow, insertRow);                
                    }
                }
            });
            
            ThreadWatcher progressStatus = event.getThreadWatcher();
            String statusText = "Created " + progressStatus.getCompletedThreads() + " buckets of " + progressStatus.getThreadCount();
            updateProgressDisplay(statusText, progressStatus.getCompletedThreads());                
        }
        else if (ServiceEvent.EVENT_COMPLETED == event.getEventCode()) { 
            stopProgressDisplay();
        }
        else if (ServiceEvent.EVENT_CANCELLED == event.getEventCode()) {
            stopProgressDisplay();        
        }
        else if (ServiceEvent.EVENT_ERROR == event.getEventCode()) {
            stopProgressDisplay();
            reportException(ownerFrame, "createBuckets", event.getErrorCause());
        }
    }

    /**
     * Deletes the bucket currently selected in the gui.
     *
     */
    private void deleteSelectedBucket() {
        S3Bucket currentBucket = getCurrentSelectedBucket();
        if (currentBucket == null) {
            log.warn("Ignoring delete bucket command, no currently selected bucket");
            return;
        }
        
        int response = JOptionPane.showConfirmDialog(ownerFrame, 
            "Are you sure you want to delete '" + currentBucket.getName() + "'?",  
            "Delete Bucket?", JOptionPane.YES_NO_OPTION);
        
        if (response == JOptionPane.NO_OPTION) {
            return;
        }
        
        try {
            s3ServiceMulti.getS3Service().deleteBucket(currentBucket.getName());
            ((BucketTableModel)bucketsTable.getModel()).removeBucket(currentBucket);
        } catch (Exception e) {
            reportException(ownerFrame, "Unable to delete bucket", e);
        }
    }
    
    /**
     * Adds a bucket not owned by the current S3 user to the bucket listing, after
     * prompting the user for the name of the bucket to add. 
     * To be added in this way, the third-party bucket must be publicly available. 
     *
     */
    private void addThirdPartyBucket() {
        try {
            String bucketName = (String) JOptionPane.showInputDialog(ownerFrame, 
                "Name for third-party bucket:",
                "Add a third-party bucket", JOptionPane.QUESTION_MESSAGE);

            if (bucketName != null) {
                if (s3ServiceMulti.getS3Service().isBucketAccessible(bucketName)) {
                    S3Bucket thirdPartyBucket = new S3Bucket(bucketName);
                    ((BucketTableModel)bucketsTable.getModel()).addBucket(thirdPartyBucket);
                }
            }            
        } catch (Exception e) {
            reportException(ownerFrame, "Unable to access third-party bucket", e);
        }        
    }
    
    /**
     * Updates the ACL settings for the currently selected bucket.
     */
    private void updateBucketAccessControlList() {
        try {
            S3Bucket currentBucket = getCurrentSelectedBucket();
            AccessControlList bucketACL = s3ServiceMulti.getS3Service().getBucketAcl(currentBucket);
            
            AccessControlList updatedBucketACL = AccessControlDialog.showDialog(ownerFrame, new S3Bucket[] {currentBucket}, bucketACL);
            if (updatedBucketACL != null) {
                currentBucket.setAcl(updatedBucketACL);
                s3ServiceMulti.getS3Service().putBucketAcl(currentBucket);
            }
        } catch (Exception e) {
            reportException(ownerFrame, "Unable to update bucket Access Control", e);
        }        
    }    
    
    /**
     * @return the set of objects currently selected in the gui, or an empty array if none are selected.
     */
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

    /**
     * Retrieves ACL settings for the currently selected objects. The actual action is performed 
     * in the <code>s3ServiceEventPerformed</code> method specific to <code>LookupACLEvent</code>s.
     *
     */
    private void lookupObjectsAccessControlLists() {
        (new Thread() {
            public void run() {
                s3ServiceMulti.getObjectACLs(getCurrentSelectedBucket(), getSelectedObjects());
            }    
        }).start();        
    }
    
    /**
     * This method is an {@link S3ServiceEventListener} action method that is invoked when this 
     * application's <code>S3ServiceMulti</code> triggers a <code>LookupACLEvent</code>.
     * <p>
     * The ACL details are retrieved for the currently selected objects in the gui, then the
     * {@link AccessControlDialog} is displayed to allow the user to update the ACL settings
     * for these objects.
     * 
     * @param event
     */
    public void s3ServiceEventPerformed(LookupACLEvent event) {
        if (ServiceEvent.EVENT_STARTED == event.getEventCode()) {
            startProgressDisplay("Retrieved 0 of " + event.getThreadWatcher().getThreadCount() + " ACL(s)", 
                    0, event.getThreadWatcher().getThreadCount(), "Cancel Lookup",  
                    event.getThreadWatcher().getCancelEventListener());
        } 
        else if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventCode()) {
            ThreadWatcher progressStatus = event.getThreadWatcher();
            String statusText = "Retrieved " + progressStatus.getCompletedThreads() + " of " + progressStatus.getThreadCount() + " ACL(s)";
            updateProgressDisplay(statusText, progressStatus.getCompletedThreads());                    
        }
        else if (ServiceEvent.EVENT_COMPLETED == event.getEventCode()) {
            stopProgressDisplay();                
            
            final S3Object[] objectsWithACL = getSelectedObjects();
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // Build merged ACL containing ALL relevant permissions
                    AccessControlList mergedACL = new AccessControlList();
                    for (int i = 0; i < objectsWithACL.length; i++) {
                        AccessControlList objectACL = objectsWithACL[i].getAcl();
                        mergedACL.grantAllPermissions(objectACL.getGrants());
                        
                        // BEWARE! Here we assume that all the objects have the same owner...
                        if (mergedACL.getOwner() == null) { 
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
            });
        }
        else if (ServiceEvent.EVENT_CANCELLED == event.getEventCode()) {
            stopProgressDisplay();        
        }
        else if (ServiceEvent.EVENT_ERROR == event.getEventCode()) {
            stopProgressDisplay();
            reportException(ownerFrame, "lookupACLs", event.getErrorCause());
        }
    }
    
    /**
     * Updates ACL settings for the currently selected objects. The actual action is performed 
     * in the <code>s3ServiceEventPerformed</code> method specific to <code>UpdateACLEvent</code>s.
     *
     */
    private void updateObjectsAccessControlLists(final S3Bucket bucket, final S3Object[] objectsWithUpdatedACLs) {
        (new Thread() {
            public void run() {
                s3ServiceMulti.putACLs(bucket, objectsWithUpdatedACLs);
            }    
        }).start();        
    }
    
    /**
     * This method is an {@link S3ServiceEventListener} action method that is invoked when this 
     * application's <code>S3ServiceMulti</code> triggers a <code>LookupACLEvent</code>.
     * <p>
     * The only actions performed as ACL settings are updated is the update of the progress
     * dialog box.
     * 
     * @param event
     */
    public void s3ServiceEventPerformed(UpdateACLEvent event) {
        if (ServiceEvent.EVENT_STARTED == event.getEventCode()) {
            startProgressDisplay("Updated 0 of " + event.getThreadWatcher().getThreadCount() + " ACL(s)", 
                    0, event.getThreadWatcher().getThreadCount(), "Cancel Update", 
                    event.getThreadWatcher().getCancelEventListener());
        } 
        else if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventCode()) {
            ThreadWatcher progressStatus = event.getThreadWatcher();
            String statusText = "Updated " + progressStatus.getCompletedThreads() + " of " + progressStatus.getThreadCount() + " ACL(s)";
            updateProgressDisplay(statusText, progressStatus.getCompletedThreads());                    
        }
        else if (ServiceEvent.EVENT_COMPLETED == event.getEventCode()) {
            stopProgressDisplay();                            
        }
        else if (ServiceEvent.EVENT_CANCELLED == event.getEventCode()) {
            stopProgressDisplay();        
        }
        else if (ServiceEvent.EVENT_ERROR == event.getEventCode()) {
            stopProgressDisplay();
            reportException(ownerFrame, "lookupACLs", event.getErrorCause());
        }
    }

    /**
     * Prepares to perform a download of objects from S3 by prompting the user for a directory
     * to store the files in, setting the {@#downloadingObjects} flag, and handing on control
     * to the method {@#retrieveObjectsDetails}.
     * @throws IOException
     */
    private void downloadSelectedObjects() throws IOException {
        // Prompt user to choose directory location for downloaded files (or cancel download altogether)
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setDialogTitle("Choose directory to save S3 files in");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setApproveButtonText("Choose directory");
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
        
    /**
     * Performs the real work of downloading files by comparing the download candidates against
     * existing files, prompting the user whether to overwrite any pre-existing file versions, 
     * and starting {@link S3ServiceMulti#downloadObjects} where the real work is done.
     *
     */
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
                    message += changedFiles + " file(s) have changed.\n\n";
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
                
                if (response == null) {
                    return;
                }                
                
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
                
                if ("gzip".equalsIgnoreCase(objects[i].getContentEncoding())
                    || objects[i].containsMetadata(Constants.METADATA_JETS3T_COMPRESSED))
                {
                    // Automatically inflate gzipped data.
                    log.debug("Inflating gzipped data for object: " + objects[i].getKey());                    
                    outputStream = new GZipInflatingOutputStream(outputStream);
                }
                if (objects[i].containsMetadata(Constants.METADATA_JETS3T_CRYPTO_ALGORITHM) 
                    || objects[i].containsMetadata(Constants.METADATA_JETS3T_ENCRYPTED_OBSOLETE))
                {
                    log.debug("Decrypting encrypted data for object: " + objects[i].getKey());
                    
                    // Prompt user for the password, if necessary.
                    if (preferenceEncryptionPassword == null) {
                        promptForPassword();
                        if (preferenceEncryptionPassword == null) {
                            throw new S3ServiceException(
                                "Cannot download encrypted files without a password");
                        }
                    }

                    if (objects[i].containsMetadata(Constants.METADATA_JETS3T_ENCRYPTED_OBSOLETE)) {
                        // Item is encrypted with obsolete crypto.
                        log.warn("Object is encrypted with out-dated crypto version, please update it when possible: " 
                            + objects[i].getKey());
                        outputStream = EncryptionUtil.getObsoleteEncryptionUtil(
                            preferenceEncryptionPassword).decrypt(outputStream);                                            
                    } else {
                        String algorithm = (String) objects[i].getMetadata(
                            Constants.METADATA_JETS3T_CRYPTO_ALGORITHM);
                        String version = (String) objects[i].getMetadata(
                            Constants.METADATA_JETS3T_CRYPTO_VERSION);
                        outputStream = new EncryptionUtil(preferenceEncryptionPassword, algorithm).
                            decrypt(outputStream);                                            
                    }                    
                }

                objAndOutList.add(new S3ObjectAndOutputStream(objects[i], outputStream));            
            }
            
            final S3ObjectAndOutputStream[] objAndOutArray = (S3ObjectAndOutputStream[])
                objAndOutList.toArray(new S3ObjectAndOutputStream[] {});        
            (new Thread() {
                public void run() {
                    s3ServiceMulti.downloadObjects(getCurrentSelectedBucket(), objAndOutArray);
                }
            }).start();
        } catch (Exception e) {
            reportException(ownerFrame, "Failed to download objects", e);
        }
    }
    
    /**
     * This method is an {@link S3ServiceEventListener} action method that is invoked when this 
     * application's <code>S3ServiceMulti</code> triggers a <code>DownloadObjectsEvent</code>.
     * <p>
     * The only actions performed here as part of object downloads is the updating of the progress
     * dialog box.
     * 
     * @param event
     */
    public void s3ServiceEventPerformed(DownloadObjectsEvent event) {
        if (ServiceEvent.EVENT_STARTED == event.getEventCode()) {    
            ThreadWatcher watcher = event.getThreadWatcher();
            
            // Show percentage of bytes transferred, if this info is available.
            if (watcher.isBytesTransferredInfoAvailable()) {
                startProgressDisplay("Downloaded " + 
                    byteFormatter.formatByteSize(watcher.getBytesTransferred()) 
                    + " of " + byteFormatter.formatByteSize(watcher.getBytesTotal()), 0, 100, "Cancel Download", 
                    event.getThreadWatcher().getCancelEventListener());
            // ... otherwise just show the number of completed threads.
            } else {
                startProgressDisplay("Downloaded " + event.getThreadWatcher().getCompletedThreads()
                    + " of " + event.getThreadWatcher().getThreadCount() + " objects", " ",
                    0, event.getThreadWatcher().getThreadCount(),  "Cancel Download",
                    event.getThreadWatcher().getCancelEventListener());
            }
        } 
        else if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventCode()) {
            ThreadWatcher watcher = event.getThreadWatcher();
            
            // Show percentage of bytes transferred, if this info is available.
            if (watcher.isBytesTransferredInfoAvailable()) {
                String bytesCompletedStr = byteFormatter.formatByteSize(watcher.getBytesTransferred());
                String bytesTotalStr = byteFormatter.formatByteSize(watcher.getBytesTotal());
                String statusText = "Downloaded " + bytesCompletedStr + " of " + bytesTotalStr;
                
                String detailsText = formatTransferDetails(watcher);
                
                long percentage = (int) 
                    (((double)watcher.getBytesTransferred() / watcher.getBytesTotal()) * 100);
                updateProgressDisplay(statusText, detailsText, percentage);
            }
            // ... otherwise just show the number of completed threads.
            else {
                ThreadWatcher progressStatus = event.getThreadWatcher();
                String statusText = "Downloaded " + progressStatus.getCompletedThreads() 
                    + " of " + progressStatus.getThreadCount() + " objects";                    
                updateProgressDisplay(statusText, progressStatus.getCompletedThreads());                    
            }            
        } else if (ServiceEvent.EVENT_COMPLETED == event.getEventCode()) {
            stopProgressDisplay();                
        }
        else if (ServiceEvent.EVENT_CANCELLED == event.getEventCode()) {
            // Delete all incompletely downloaded object files.
            S3Object[] incompleteObjects = event.getCancelledObjects();
            for (int i = 0; i < incompleteObjects.length; i++) {
                File file = (File) downloadObjectsToFileMap.get(incompleteObjects[i].getKey());
                 if (file.length() != incompleteObjects[i].getContentLength()) {
                    log.debug("Deleting incomplete object file: " + file.getName());                
                    file.delete();
                 }
            }
            
            stopProgressDisplay();        
        }
        else if (ServiceEvent.EVENT_ERROR == event.getEventCode()) {
            stopProgressDisplay();
            reportException(ownerFrame, "createObjects", event.getErrorCause());
        }
    }
    
    /**
     * Prepares to upload files to S3 
     * @param originalFile
     * @param newObject
     * @return
     * @throws Exception
     */
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
        
        String contentEncoding = null;
        if (prefAutomaticGzip.isSelected()) {
            inputStream = new GZipDeflatingInputStream(inputStream);
            contentEncoding = "gzip";
            newObject.addMetadata(Constants.METADATA_JETS3T_COMPRESSED, "gzip"); 
            actionText += "Compressing";                
        } 
        if (prefAutomaticEncryption.isSelected()) {
            EncryptionUtil encryptionUtil = new EncryptionUtil(preferenceEncryptionPassword);
            inputStream = encryptionUtil.encrypt(inputStream);
            contentEncoding = null;
            newObject.setContentType(Mimetypes.MIMETYPE_OCTET_STREAM);
            newObject.addMetadata(Constants.METADATA_JETS3T_CRYPTO_ALGORITHM, 
                encryptionUtil.getAlgorithm()); 
            newObject.addMetadata(Constants.METADATA_JETS3T_CRYPTO_VERSION, 
                EncryptionUtil.VERSION); 
            actionText += (actionText.length() == 0? "Encrypting" : " and encrypting");                
        }
        if (contentEncoding != null) {
            newObject.addMetadata("Content-Encoding", contentEncoding);
        }

        // updateProgressDisplay(actionText + " '" + originalFile.getName() + "' for upload", 0);
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
    
    private void uploadFilesToS3(final File uploadingFiles[]) {
        try {
            // Build map of files proposed for upload.
            Map filesForUploadMap = FileComparer.buildFileMap(uploadingFiles);
            
            // Build map of objects already existing in target S3 bucket with keys
            // matching the proposed upload keys.
            List objectsWithExistingKeys = new ArrayList();
            S3Object[] existingObjects = ((ObjectTableModel)objectsTable.getModel()).getObjects();
            for (int i = 0; i < existingObjects.length; i++) {
                if (filesForUploadMap.keySet().contains(existingObjects[i].getKey()))
                {
                    objectsWithExistingKeys.add(existingObjects[i]);
                }
            }
            existingObjects = (S3Object[]) objectsWithExistingKeys.toArray(new S3Object[] {});
            
            Map s3ExistingObjectsMap = FileComparer.buildS3ObjectMap(s3ServiceMulti.getS3Service(),
                getCurrentSelectedBucket(), "", existingObjects, this); 
                            
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
                    message += changedFiles + " file(s) have changed.\n\n";
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
                
                if (response == null) {
                    return;
                }
                
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
            if (preferenceEncryptionPassword == null && prefAutomaticEncryption.isSelected()) {
                promptForPassword();
                if (preferenceEncryptionPassword == null) {
                    throw new S3ServiceException(
                        "Cannot encrypt files for upload without a password");
                }
            }            

            startProgressDisplay("Prepared 0 of " + fileKeysForUpload.size() 
                + " file(s) for upload", 0, fileKeysForUpload.size(), null, null);
            
            // Populate S3Objects representing upload files with metadata etc.
            final S3Object[] objects = new S3Object[fileKeysForUpload.size()];
            int objectIndex = 0;
            for (Iterator iter = fileKeysForUpload.iterator(); iter.hasNext();) {
                String fileKey = iter.next().toString();
                File file = (File) filesForUploadMap.get(fileKey);
                
                S3Object newObject = new S3Object(fileKey);
                if (file.isDirectory()) {
                    newObject.setContentType(Mimetypes.MIMETYPE_JETS3T_DIRECTORY);
                } else {     
                    newObject.setContentType(Mimetypes.getInstance().getMimetype(file));
                    
                    // Do any necessary file pre-processing.
                    File fileToUpload = prepareUploadFile(file, newObject);
                    
                    newObject.addMetadata(Constants.METADATA_JETS3T_LOCAL_FILE_DATE, 
                        ServiceUtils.formatIso8601Date(new Date(file.lastModified())));
                    newObject.setContentLength(fileToUpload.length());
                    newObject.setDataInputFile(fileToUpload);
                    
                    // Compute the upload file's MD5 hash.
                    newObject.setMd5Hash(ServiceUtils.computeMD5Hash(
                        new FileInputStream(fileToUpload)));
                    
                    if (!fileToUpload.equals(file)) {
                        // Compute the MD5 hash of the *original* file, if upload file has been altered
                        // through encryption or gzipping.
                        newObject.addMetadata(
                            S3Object.METADATA_HEADER_ORIGINAL_HASH_MD5,
                            ServiceUtils.toBase64(ServiceUtils.computeMD5Hash(new FileInputStream(file))));
                    }

                    updateProgressDisplay("Prepared " + (objectIndex + 1) 
                        + " of " + fileKeysForUpload.size() + " file(s) for upload", 
                        (objectIndex + 1));
                }
                objects[objectIndex++] = newObject;
            }

            
            stopProgressDisplay();
            
            // Upload the files.
            s3ServiceMulti.putObjects(getCurrentSelectedBucket(), objects);

        } catch (Exception e) {
            reportException(ownerFrame, "Unable to upload file or directory", e);
        } 
    }
    
    public void s3ServiceEventPerformed(final CreateObjectsEvent event) {
        if (ServiceEvent.EVENT_STARTED == event.getEventCode()) {    
            ThreadWatcher watcher = event.getThreadWatcher();
            
            // Show percentage of bytes transferred, if this info is available.
            if (watcher.isBytesTransferredInfoAvailable()) {
                String bytesTotalStr = byteFormatter.formatByteSize(watcher.getBytesTotal());
                String statusText = "Uploaded 0 of " + bytesTotalStr;                
                startProgressDisplay(statusText, " ", 0, 100, "Cancel upload", 
                    event.getThreadWatcher().getCancelEventListener());
            } 
            // ... otherwise show the number of completed threads.
            else {
                startProgressDisplay("Uploading file 0 of " + getCurrentSelectedBucket().getName(), 
                    watcher.getCompletedThreads(), watcher.getThreadCount(), "Cancel upload",  
                    event.getThreadWatcher().getCancelEventListener());                
            }
        } 
        else if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventCode()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {            
                    for (int i = 0; i < event.getCreatedObjects().length; i++) {
                        ((ObjectTableModel)objectsTable.getModel()).addObject(
                            event.getCreatedObjects()[i], false);
                    }
                }
            });
            
            ThreadWatcher watcher = event.getThreadWatcher();
            
            // Show percentage of bytes transferred, if this info is available.
            if (watcher.isBytesTransferredInfoAvailable()) {
                if (watcher.getBytesTransferred() >= watcher.getBytesTotal()) {
                    // Upload is completed, just waiting on resonse from S3.
                    String statusText = "Upload completed, awaiting confirmation";
                    updateProgressDisplay(statusText, 100);
                } else {                    
                    String bytesCompletedStr = byteFormatter.formatByteSize(watcher.getBytesTransferred());
                    String bytesTotalStr = byteFormatter.formatByteSize(watcher.getBytesTotal());
                    String statusText = "Uploaded " + bytesCompletedStr + " of " + bytesTotalStr;
                    long percentage = (int) 
                        (((double)watcher.getBytesTransferred() / watcher.getBytesTotal()) * 100);
                    
                    String detailsText = formatTransferDetails(watcher);

                    updateProgressDisplay(statusText, detailsText, percentage);
                }
            }
            // ... otherwise show the number of completed threads.
            else {
                ThreadWatcher progressStatus = event.getThreadWatcher();
                String statusText = "Uploaded file " + progressStatus.getCompletedThreads() + " of " + progressStatus.getThreadCount();                    
                updateProgressDisplay(statusText, progressStatus.getCompletedThreads());                    
            }
        }
        else if (ServiceEvent.EVENT_COMPLETED == event.getEventCode()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateObjectsSummary(false);
                    S3Object[] allObjects = ((ObjectTableModel)objectsTable.getModel()).getObjects();
                    cachedBuckets.put(getCurrentSelectedBucket().getName(), allObjects);
                }
            });
                        
            stopProgressDisplay();        
        }
        else if (ServiceEvent.EVENT_CANCELLED == event.getEventCode()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateObjectsSummary(false);
                }
            });

            stopProgressDisplay();        
        }
        else if (ServiceEvent.EVENT_ERROR == event.getEventCode()) {
            stopProgressDisplay();
            reportException(ownerFrame, "createObjects", event.getErrorCause());
        }
    }
    
    private void generatePublicUrl() {
        final S3Object[] objects = getSelectedObjects(); 

        if (objects.length != 1) {
            System.err.println("Ignoring Generate Public URL object command, can only operate on a single object");
            return;            
        }
        S3Object currentObject = objects[0];

        Object response = JOptionPane.showInputDialog(ownerFrame,
            "For how many hours should '" + currentObject.getKey() + "' be accessible by the URL?",
            "Generate Public URL", JOptionPane.QUESTION_MESSAGE, null, null, "0.5");
            
        if (response == null) {
            return;
        }
        
        try {
            // Determine expiry time for URL
            double hoursFromNow = Double.parseDouble(response.toString());
            int secondsFromNow = (int) (hoursFromNow * 60 * 60);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, secondsFromNow);

            // Generate URL
            String signedUrl = S3Service.createSignedGetUrl(
                getCurrentSelectedBucket().getName(), currentObject.getKey(),
                s3ServiceMulti.getAWSCredentials(), cal.getTime(), S3Service.DEFAULT_S3_URL_SECURE);
            
            // Display signed URL
            JOptionPane.showInputDialog(ownerFrame,
                "URL for '" + currentObject.getKey() + "'."
                + "\n This URL will be valid until approximately " + cal.getTime() 
                + "\n(Amazon's server time may be ahead of or behind your computer's clock)",
                "Signed URL", JOptionPane.INFORMATION_MESSAGE, null, null, signedUrl);

        } catch (NumberFormatException e) {
            reportException(ownerFrame, "Hours must be a valid decimal value; eg 3, 0.1", e);
        } catch (S3ServiceException e) {
            reportException(ownerFrame, "Unable to generate public URL", e);
        }
    }    
        
    private void generateTorrentUrl() {
        final S3Object[] objects = getSelectedObjects(); 

        if (objects.length != 1) {
            System.err.println("Ignoring Generate Public URL object command, can only operate on a single object");
            return;            
        }
        S3Object currentObject = objects[0];

        // Generate URL
        String torrentUrl = S3Service.createTorrentUrl(
            getCurrentSelectedBucket().getName(), currentObject.getKey());
        
        // Display signed URL
        JOptionPane.showInputDialog(ownerFrame,
            "Torrent URL for '" + currentObject.getKey() + "'.",
            "Torrent URL", JOptionPane.INFORMATION_MESSAGE, null, null, torrentUrl);
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
                s3ServiceMulti.deleteObjects(getCurrentSelectedBucket(), objects);
            }
        }.start();        
    }
    
    public void s3ServiceEventPerformed(final DeleteObjectsEvent event) {
        if (ServiceEvent.EVENT_STARTED == event.getEventCode()) {    
            startProgressDisplay("Deleted 0 of " + event.getThreadWatcher().getThreadCount() + " object(s)", 
                0, event.getThreadWatcher().getThreadCount(), "Cancel Delete Objects",  
                event.getThreadWatcher().getCancelEventListener());
        } 
        else if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventCode()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {            
                    for (int i = 0; i < event.getDeletedObjects().length; i++) {
                        ((ObjectTableModel)objectsTable.getModel()).removeObject(
                            event.getDeletedObjects()[i]);
                    }
                }
            });
            
            ThreadWatcher progressStatus = event.getThreadWatcher();
            String statusText = "Deleted " + progressStatus.getCompletedThreads() 
                + " of " + progressStatus.getThreadCount() + " object(s)";
            updateProgressDisplay(statusText, progressStatus.getCompletedThreads());                
        }
        else if (ServiceEvent.EVENT_COMPLETED == event.getEventCode()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateObjectsSummary(false);
                    S3Object[] allObjects = ((ObjectTableModel)objectsTable.getModel()).getObjects();
                    cachedBuckets.put(getCurrentSelectedBucket().getName(), allObjects);
                }
            });
            
            stopProgressDisplay();                
        }
        else if (ServiceEvent.EVENT_CANCELLED == event.getEventCode()) {
            listObjects(); // Refresh object listing.
            stopProgressDisplay();        
        }
        else if (ServiceEvent.EVENT_ERROR == event.getEventCode()) {
            listObjects(); // Refresh object listing.
            stopProgressDisplay();
            reportException(ownerFrame, "deleteObjects", event.getErrorCause());
        }
    }
    
    /**
     * Retrieves details about objects including metadata etc by invoking the method
     * {@link S3ServiceMulti#getObjectsHeads}. 
     * 
     * This is generally done as a prelude
     * to some further action, such as displaying the objects' details or downloading the objects.
     * The real action occurs in the method <code>s3ServiceEventPerformed</code> for handling 
     * <code>GetObjectHeadsEvent</code> events.
     * @param candidateObjects
     */
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
                s3ServiceMulti.getObjectsHeads(getCurrentSelectedBucket(), incompleteObjects);
            };
        }).start();
    }
    
    public void s3ServiceEventPerformed(final GetObjectHeadsEvent event) {
        if (ServiceEvent.EVENT_STARTED == event.getEventCode()) {
            startProgressDisplay("Retrieved details for 0 of " 
                + event.getThreadWatcher().getThreadCount() + " object(s)", 
                0, event.getThreadWatcher().getThreadCount(), "Cancel Retrieval", 
                event.getThreadWatcher().getCancelEventListener());
        } 
        else if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventCode()) {
            ThreadWatcher progressStatus = event.getThreadWatcher();

            // Store detail-complete objects in table.
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // Retain selected status of objects for downloads or properties 
                    for (int i = 0; i < event.getCompletedObjects().length; i++) {
                        S3Object object = event.getCompletedObjects()[i];
                        boolean highlightUpdatedObjects = downloadingObjects || viewingObjectProperties; 
                        ((ObjectTableModel)objectsTable.getModel()).addObject(object, highlightUpdatedObjects);
                        log.debug("Updated table with " + object.getKey() + ", content-type=" + object.getContentType());                
                    }
                }
            });
            
            // Update progress of GetObject requests.
            String statusText = "Retrieved details for " + progressStatus.getCompletedThreads() 
                + " of " + progressStatus.getThreadCount() + " object(s)";
            updateProgressDisplay(statusText, progressStatus.getCompletedThreads());                    
        }
        else if (ServiceEvent.EVENT_COMPLETED == event.getEventCode()) {
            // Stop GetObjectHead progress display.
            stopProgressDisplay();        
            
            if (downloadingObjects) {
                performObjectsDownload();
                downloadingObjects = false;
            } else if (viewingObjectProperties) {
                PropertiesDialog.showDialog(ownerFrame, getSelectedObjects()[0]);
                viewingObjectProperties = false;                    
            }            
        }
        else if (ServiceEvent.EVENT_CANCELLED == event.getEventCode()) {
            stopProgressDisplay();        
        }
        else if (ServiceEvent.EVENT_ERROR == event.getEventCode()) {
            stopProgressDisplay();
            reportException(ownerFrame, "getObjectHeads", event.getErrorCause());
        }
    }
           
    private String formatTransferDetails(ThreadWatcher watcher) {
        String detailsText = null;
        if (watcher.isBytesTransferredInfoAvailable()) {
            detailsText = " ";
        }
        if (watcher.isBytesPerSecondAvailable()) {
            long bytesPerSecond = watcher.getBytesPerSecond();
            detailsText = byteFormatter.formatByteSize(bytesPerSecond) + "/s";
        }
        if (watcher.isTimeRemainingAvailable()) {
            long secondsRemaining = watcher.getTimeRemaining();
            detailsText += " - ETA " + timeFormatter.formatTime(secondsRemaining);
        }
        return detailsText;
    }
    
    /**
     * Opens hyperlinks if the Uploader application is running as an Applet. If the uploader is
     * not running as an applet, the action is ignored with a warning message.
     * @param url
     * the url to open
     * @param target
     * the target pane to open the url in, eg "_blank". This may be null.
     */
    public void followHyperlink(URL url, String target) {
        if (!isStandAloneApplication) {
            if (target == null) {
                getAppletContext().showDocument(url);                
            } else {
                getAppletContext().showDocument(url, target);
            }
        } else {
            BareBonesBrowserLaunch.openURL(url.toString());
        }
    }
        
    /**
     * Displays a rudimentary error message dialog box.
     * @param ownerFrame
     * @param message
     * @param t
     */
    public static void reportException(Frame ownerFrame, String message, Throwable t) {
        System.err.println(message);
        t.printStackTrace(System.err);

        // Show error dialog box.
        String detailsText = null;
        if (t instanceof S3ServiceException) {
            S3ServiceException s3se = (S3ServiceException) t;
            if (s3se.getErrorCode() != null) {
                detailsText = "S3 Error Code: " + s3se.getErrorCode();
            } 
            
            if (s3se.getMessage() != null) {
                detailsText += "\n" + s3se.getMessage();
            }
            
            Throwable cause = s3se.getCause();
            while (cause != null) {
                detailsText += "\nCaused by: " + cause;
                cause = cause.getCause();
            }
        } else {
            detailsText = "Error details: " + t.getMessage();
        }
        JOptionPane.showMessageDialog(ownerFrame, message + "\n" + detailsText, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    
    private class BucketTableModel extends DefaultTableModel {
        private static final long serialVersionUID = -2316561957299358428L;
        
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
        private static final long serialVersionUID = -8168111242655844228L;
        
        ArrayList objectList = new ArrayList();
        
        public ObjectTableModel() {
            super(new String[] {"Object Key","Size","Last Modified"}, 0);
        }
        
        public void addObject(S3Object object, boolean highlightNewObject) {
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
            this.insertRow(insertRow, new Object[] {object.getKey(), 
                byteFormatter.formatByteSize(object.getContentLength()), 
                object.getLastModifiedDate() /*, object.getHash(), object.getStorageClass()*/});
            
            // Automatically select (highlight) a newly aded object, if required.
            if (highlightNewObject) {
                objectsTable.addRowSelectionInterval(insertRow, insertRow);
            }
        }
        
        public void addObjects(S3Object[] objects, boolean highlightNewObject) {
            for (int i = 0; i < objects.length; i++) {
                addObject(objects[i], highlightNewObject);
            }
        }
        
        public void removeObject(S3Object object) {
            int index = objectList.indexOf(object);
            this.removeRow(index);
            objectList.remove(object);
        }
        
        public void removeAllObjects() {
            int rowCount = this.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                this.removeRow(0);
            }
            objectList.clear();
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
        JFrame ownerFrame = new JFrame("jets3t Cockpit");
        ownerFrame.setName("jets3t Cockpit");
        ownerFrame.addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent e) {
            }
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
            }
            public void windowClosed(WindowEvent e) {
            }
            public void windowIconified(WindowEvent e) {
            }
            public void windowDeiconified(WindowEvent e) {
            }
            public void windowActivated(WindowEvent e) {
            }
            public void windowDeactivated(WindowEvent e) {
            }           
        });
        
        Cockpit cockpit = new Cockpit(ownerFrame);
        ownerFrame.getContentPane().add(cockpit);
        ownerFrame.setBounds(cockpit.getBounds());
        ownerFrame.setVisible(true);                        
    }

}
