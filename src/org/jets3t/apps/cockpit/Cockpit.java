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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.apps.cockpit.gui.AccessControlDialog;
import org.jets3t.apps.cockpit.gui.BucketLoggingDialog;
import org.jets3t.apps.cockpit.gui.BucketTableModel;
import org.jets3t.apps.cockpit.gui.ItemPropertiesDialog;
import org.jets3t.apps.cockpit.gui.ObjectTableModel;
import org.jets3t.apps.cockpit.gui.PreferencesDialog;
import org.jets3t.apps.cockpit.gui.StartupDialog;
import org.jets3t.gui.ErrorDialog;
import org.jets3t.gui.HyperlinkActivatedListener;
import org.jets3t.gui.JHtmlLabel;
import org.jets3t.gui.TableSorter;
import org.jets3t.service.Constants;
import org.jets3t.service.Jets3tProperties;
import org.jets3t.service.S3ObjectsChunk;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.io.GZipDeflatingInputStream;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.multithread.CancelEventTrigger;
import org.jets3t.service.multithread.CreateBucketsEvent;
import org.jets3t.service.multithread.CreateObjectsEvent;
import org.jets3t.service.multithread.DeleteObjectsEvent;
import org.jets3t.service.multithread.DownloadObjectsEvent;
import org.jets3t.service.multithread.DownloadPackage;
import org.jets3t.service.multithread.GetObjectHeadsEvent;
import org.jets3t.service.multithread.GetObjectsEvent;
import org.jets3t.service.multithread.LookupACLEvent;
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
    
    public static final String APPLICATION_TITLE = "JetS3t Cockpit";
    private static final int BUCKET_LIST_CHUNKING_SIZE = 1000;
    
    private File cockpitHomeDirectory = Constants.DEFAULT_PREFERENCES_DIRECTORY;
    private CockpitPreferences cockpitPreferences = new CockpitPreferences();
    
    private final Insets insetsZero = new Insets(0, 0, 0, 0);
    private final Insets insetsDefault = new Insets(5, 7, 5, 7);

    private final ByteFormatter byteFormatter = new ByteFormatter();
    private final TimeFormatter timeFormatter = new TimeFormatter();
    private final SimpleDateFormat yearAndTimeSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
    private JPopupMenu bucketActionMenu = null;
    private JMenuItem viewBucketPropertiesMenuItem = null;
    private JMenuItem refreshBucketMenuItem = null; 
    private JMenuItem createBucketMenuItem = null;
    private JMenuItem updateBucketACLMenuItem = null;
    private JMenuItem deleteBucketMenuItem = null;
    
    // Object main menu items
    private JPopupMenu objectActionMenu = null;
    private JMenuItem viewObjectPropertiesMenuItem = null;
    private JMenuItem refreshObjectMenuItem = null;
    private JMenuItem updateObjectACLMenuItem = null;
    private JMenuItem downloadObjectMenuItem = null;
    private JMenuItem uploadFilesMenuItem = null;
    private JMenuItem generatePublicGetUrl = null;
    private JMenuItem generateTorrentUrl = null;
    private JMenuItem deleteObjectMenuItem = null;
    
    // Tools menu items.
    private JMenuItem bucketLoggingMenuItem = null;    

    // Preference menu items.
    private JMenuItem preferencesDialogMenuItem = null;
    
    // Help menu items.
    private JMenuItem cockpitHelpMenuItem = null;
    private JMenuItem amazonS3HelpMenuItem = null;
    
    // Tables
    private JTable bucketsTable = null;
    private JTable objectsTable = null;
    private JScrollPane objectsTableSP = null;
    private BucketTableModel bucketTableModel =  null;
    private TableSorter bucketTableModelSorter = null;
    private ObjectTableModel objectTableModel =  null;
    private TableSorter objectTableModelSorter = null;
    
    private JLabel objectsSummaryLabel = null;
        
    private HashMap cachedBuckets = new HashMap();
    private ProgressDisplay progressDisplay = null;
        
    // Class variables used for uploading or downloading files.
    private File downloadDirectory = null;
    private Map downloadObjectsToFileMap = null;
    private boolean isDownloadingObjects = false;   
    private Map filesInDownloadDirectoryMap = null;
    private Map s3DownloadObjectsMap = null;

    
    private JPanel filterObjectsPanel = null;
    private JCheckBox filterObjectsCheckBox = null;
    private JTextField filterObjectsPrefix = null;
    private JComboBox filterObjectsDelimiter = null;
    
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
                
        // Initialise the GUI.
        initGui();      

        // Initialise a non-authenticated service.
        try {
            // Revert to anonymous service.
            s3ServiceMulti = new S3ServiceMulti(
                new RestS3Service(null, APPLICATION_DESCRIPTION, null), this);
        } catch (S3ServiceException e) {
            String message = "Unable to start anonymous service";
            log.error(message, e);
            ErrorDialog.showDialog(ownerFrame, this, message, e);
        }
        
        // Ensure cockpit's home directory exists.
        if (!cockpitHomeDirectory.exists()) {
            log.info("Creating home directory for Cockpit: " + cockpitHomeDirectory);
            cockpitHomeDirectory.mkdirs();
        }
        
        // Load Cockpit configuration files from cockpit's home directory.
        File mimeTypesFile = new File(cockpitHomeDirectory, "mime.types");
        if (mimeTypesFile.exists()) {
            try {
                Mimetypes.getInstance().loadAndReplaceMimetypes(
                    new FileInputStream(mimeTypesFile));
            } catch (IOException e) {
                String message = "Unable to load mime.types file: " + mimeTypesFile;
                log.error(message, e);
                ErrorDialog.showDialog(ownerFrame, this, message, e);
            }
        }
        File jets3tPropertiesFile = new File(cockpitHomeDirectory, "jets3t.properties");
        if (jets3tPropertiesFile.exists()) {
            try {
                Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
                    .loadAndReplaceProperties(new FileInputStream(jets3tPropertiesFile),
                        "jets3t.properties in Cockpit's home folder " + cockpitHomeDirectory);
            } catch (IOException e) {
                String message = "Unable to load jets3t.properties file: " + jets3tPropertiesFile;
                log.error(message, e);
                ErrorDialog.showDialog(ownerFrame, this, message, e);
            }
        }        
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                loginEvent();                
            }
        });
    }    
    
    /**
     * Initialises the application's GUI elements.
     */
    private void initGui() {        
        initMenus();
        
        JPanel appContent = new JPanel(new GridBagLayout());
        this.getContentPane().add(appContent);
                
        // Buckets panel.        
        JPanel bucketsPanel = new JPanel(new GridBagLayout());
        
        JButton bucketActionButton = new JButton();
        bucketActionButton.setToolTipText("Bucket actions menu");
        applyIcon(bucketActionButton, "/images/nuvola/16x16/actions/misc.png");
        bucketActionButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
                JButton sourceButton = (JButton) e.getSource();
                bucketActionMenu.show(sourceButton, 0, sourceButton.getHeight());
           } 
        });                        
        bucketsPanel.add(new JHtmlLabel("<html><b>Buckets</b></html>", this), 
            new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));
        bucketsPanel.add(bucketActionButton, 
            new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));

        bucketTableModel = new BucketTableModel();
        bucketTableModelSorter = new TableSorter(bucketTableModel);        
        bucketsTable = new JTable(bucketTableModelSorter);
        bucketTableModelSorter.setTableHeader(bucketsTable.getTableHeader());        
        bucketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bucketsTable.getSelectionModel().addListSelectionListener(this);
        bucketsTable.setShowHorizontalLines(true);
        bucketsTable.setShowVerticalLines(true);
        bucketsTable.addMouseListener(new ContextMenuListener());
        bucketsPanel.add(new JScrollPane(bucketsTable), 
            new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsZero, 0, 0));
        bucketsPanel.add(new JLabel(" "), 
            new GridBagConstraints(0, 2, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insetsDefault, 0, 0));
        
        // Filter panel.
        filterObjectsPanel = new JPanel(new GridBagLayout());
        filterObjectsPrefix = new JTextField();
        filterObjectsPrefix.setToolTipText("Only show objects with this prefix");
        filterObjectsPrefix.addActionListener(this);
        filterObjectsPrefix.setActionCommand("RefreshObjects");
        filterObjectsDelimiter = new JComboBox(new String[] {"", "/", "?", "\\"});
        filterObjectsDelimiter.setEditable(true);
        filterObjectsDelimiter.setToolTipText("Object name delimiter");
        filterObjectsDelimiter.addActionListener(this);
        filterObjectsDelimiter.setActionCommand("RefreshObjects");
        filterObjectsPanel.add(new JHtmlLabel("Prefix:", this), 
            new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insetsZero, 0, 0));
        filterObjectsPanel.add(filterObjectsPrefix, 
            new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        filterObjectsPanel.add(new JHtmlLabel("Delimiter:", this), 
            new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insetsDefault, 0, 0));
        filterObjectsPanel.add(filterObjectsDelimiter, 
            new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insetsZero, 0, 0));
        filterObjectsPanel.setVisible(false);
        
        // Objects panel.
        JPanel objectsPanel = new JPanel(new GridBagLayout());
        int row = 0;
        filterObjectsCheckBox = new JCheckBox("Filter objects");
        filterObjectsCheckBox.addActionListener(this);
        filterObjectsCheckBox.setToolTipText("Check this option to filter the objects listed");
        objectsPanel.add(new JHtmlLabel("<html><b>Objects</b></html>", this), 
            new GridBagConstraints(0, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));
        objectsPanel.add(filterObjectsCheckBox, 
            new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));
                        
        JButton objectActionButton = new JButton();
        objectActionButton.setToolTipText("Object actions menu");
        applyIcon(objectActionButton, "/images/nuvola/16x16/actions/misc.png");
        objectActionButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
                JButton sourceButton = (JButton) e.getSource();
                objectActionMenu.show(sourceButton, 0, sourceButton.getHeight());
           } 
        });                        
        objectsPanel.add(objectActionButton, 
            new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));
        
        objectsPanel.add(filterObjectsPanel, 
            new GridBagConstraints(0, ++row, 3, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsZero, 0, 0));
                
        objectsTable = new JTable();
        objectTableModel = new ObjectTableModel();
        objectTableModelSorter = new TableSorter(objectTableModel);        
        objectTableModelSorter.setTableHeader(objectsTable.getTableHeader());        
        objectsTable.setModel(objectTableModelSorter);        
        objectsTable.setDefaultRenderer(Long.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String formattedSize = byteFormatter.formatByteSize(((Long)value).longValue()); 
                return super.getTableCellRendererComponent(table, formattedSize, isSelected, hasFocus, row, column);
            }
        });
        objectsTable.setDefaultRenderer(Date.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Date date = (Date) value;
                return super.getTableCellRendererComponent(table, yearAndTimeSDF.format(date), isSelected, hasFocus, row, column);
            }
        });        
        objectsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        objectsTable.getSelectionModel().addListSelectionListener(this);
        objectsTable.setShowHorizontalLines(true);
        objectsTable.setShowVerticalLines(true);
        objectsTable.addMouseListener(new ContextMenuListener());
        objectsTableSP = new JScrollPane(objectsTable);
        objectsPanel.add(objectsTableSP, 
                new GridBagConstraints(0, ++row, 3, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsZero, 0, 0));
        objectsSummaryLabel = new JHtmlLabel("Please select a bucket", this);
        objectsSummaryLabel.setHorizontalAlignment(JLabel.CENTER);
        objectsSummaryLabel.setFocusable(false);
        objectsPanel.add(objectsSummaryLabel, 
                new GridBagConstraints(0, ++row, 3, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        
        // Combine sections.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                bucketsPanel, objectsPanel);
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
    
    private void applyIcon(JMenuItem menuItem, String iconResourcePath) {
        URL iconUrl = getClass().getResource(iconResourcePath);
        if (iconUrl != null) {
            ImageIcon icon = new ImageIcon(iconUrl);
            menuItem.setIcon(icon);
        } else {
            log.warn("Unable to load menu icon with resource path: " + iconResourcePath);
        }
    }
    
    private void applyIcon(JButton button, String iconResourcePath) {
        URL iconUrl = getClass().getResource(iconResourcePath);
        if (iconUrl != null) {
            ImageIcon icon = new ImageIcon(iconUrl);
            button.setIcon(icon);
        } else {
            log.warn("Unable to load button icon with resource path: " + iconResourcePath);
        }
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
        applyIcon(loginMenuItem, "/images/nuvola/16x16/actions/connect_creating.png");
        serviceMenu.add(loginMenuItem);
        
        logoutMenuItem = new JMenuItem("Log out");
        logoutMenuItem.setActionCommand("LogoutEvent");
        logoutMenuItem.addActionListener(this);
        applyIcon(logoutMenuItem, "/images/nuvola/16x16/actions/connect_no.png");
        serviceMenu.add(logoutMenuItem);

        if (isStandAloneApplication) {
            serviceMenu.add(new JSeparator());
            
            JMenuItem quitMenuItem = new JMenuItem("Quit");
            quitMenuItem.setActionCommand("QuitEvent");
            quitMenuItem.addActionListener(this);
            applyIcon(quitMenuItem, "/images/nuvola/16x16/actions/exit.png");
            serviceMenu.add(quitMenuItem);
        }

        loginMenuItem.setEnabled(true);
        logoutMenuItem.setEnabled(false);

        // Bucket action menu.
        bucketActionMenu = new JPopupMenu();
        
        refreshBucketMenuItem = new JMenuItem("Refresh bucket listing");
        refreshBucketMenuItem.setActionCommand("RefreshBuckets");
        refreshBucketMenuItem.addActionListener(this);
        applyIcon(refreshBucketMenuItem, "/images/nuvola/16x16/actions/reload.png");
        bucketActionMenu.add(refreshBucketMenuItem);
        
        viewBucketPropertiesMenuItem = new JMenuItem("View bucket properties...");
        viewBucketPropertiesMenuItem.setActionCommand("ViewBucketProperties");
        viewBucketPropertiesMenuItem.addActionListener(this);
        applyIcon(viewBucketPropertiesMenuItem, "/images/nuvola/16x16/actions/viewmag.png");
        bucketActionMenu.add(viewBucketPropertiesMenuItem);
        
        updateBucketACLMenuItem = new JMenuItem("Update bucket's Access Control List...");
        updateBucketACLMenuItem.setActionCommand("UpdateBucketACL");
        updateBucketACLMenuItem.addActionListener(this);
        applyIcon(updateBucketACLMenuItem, "/images/nuvola/16x16/actions/encrypted.png");
        bucketActionMenu.add(updateBucketACLMenuItem);
        
        bucketActionMenu.add(new JSeparator());

        createBucketMenuItem = new JMenuItem("Create new bucket...");
        createBucketMenuItem.setActionCommand("CreateBucket");
        createBucketMenuItem.addActionListener(this);
        applyIcon(createBucketMenuItem, "/images/nuvola/16x16/actions/viewmag+.png");
        bucketActionMenu.add(createBucketMenuItem);

        JMenuItem thirdPartyBucketMenuItem = new JMenuItem("Add third-party bucket...");
        thirdPartyBucketMenuItem.setActionCommand("AddThirdPartyBucket");
        thirdPartyBucketMenuItem.addActionListener(this);
        applyIcon(thirdPartyBucketMenuItem, "/images/nuvola/16x16/actions/viewmagfit.png");
        bucketActionMenu.add(thirdPartyBucketMenuItem);

        bucketActionMenu.add(new JSeparator());
        
        deleteBucketMenuItem = new JMenuItem("Delete bucket...");
        deleteBucketMenuItem.setActionCommand("DeleteBucket");
        deleteBucketMenuItem.addActionListener(this);
        applyIcon(deleteBucketMenuItem, "/images/nuvola/16x16/actions/cancel.png");
        bucketActionMenu.add(deleteBucketMenuItem);
        
        viewBucketPropertiesMenuItem.setEnabled(false);
        refreshBucketMenuItem.setEnabled(false);
        createBucketMenuItem.setEnabled(false);
        updateBucketACLMenuItem.setEnabled(false);
        deleteBucketMenuItem.setEnabled(false);

        // Object action menu.
        objectActionMenu = new JPopupMenu();
        
        refreshObjectMenuItem = new JMenuItem("Refresh object listing");
        refreshObjectMenuItem.setActionCommand("RefreshObjects");
        refreshObjectMenuItem.addActionListener(this);
        applyIcon(refreshObjectMenuItem, "/images/nuvola/16x16/actions/reload.png");
        objectActionMenu.add(refreshObjectMenuItem);
        
        viewObjectPropertiesMenuItem = new JMenuItem("View object properties...");
        viewObjectPropertiesMenuItem.setActionCommand("ViewObjectProperties");
        viewObjectPropertiesMenuItem.addActionListener(this);
        applyIcon(viewObjectPropertiesMenuItem, "/images/nuvola/16x16/actions/viewmag.png");
        objectActionMenu.add(viewObjectPropertiesMenuItem);
        
        updateObjectACLMenuItem = new JMenuItem("Update object(s) Access Control List(s)...");
        updateObjectACLMenuItem.setActionCommand("UpdateObjectACL");
        updateObjectACLMenuItem.addActionListener(this);
        applyIcon(updateObjectACLMenuItem, "/images/nuvola/16x16/actions/encrypted.png");
        objectActionMenu.add(updateObjectACLMenuItem);

        downloadObjectMenuItem = new JMenuItem("Download object(s)...");
        downloadObjectMenuItem.setActionCommand("DownloadObjects");
        downloadObjectMenuItem.addActionListener(this);
        applyIcon(downloadObjectMenuItem, "/images/nuvola/16x16/actions/1downarrow.png");
        objectActionMenu.add(downloadObjectMenuItem);
            
        uploadFilesMenuItem = new JMenuItem("Upload file(s)...");
        uploadFilesMenuItem.setActionCommand("UploadFiles");
        uploadFilesMenuItem.addActionListener(this);
        applyIcon(uploadFilesMenuItem, "/images/nuvola/16x16/actions/1uparrow.png");
        objectActionMenu.add(uploadFilesMenuItem);
        
        objectActionMenu.add(new JSeparator());

        generatePublicGetUrl = new JMenuItem("Generate Public GET URL...");
        generatePublicGetUrl.setActionCommand("GeneratePublicGetURL");
        generatePublicGetUrl.addActionListener(this);
        applyIcon(generatePublicGetUrl, "/images/nuvola/16x16/actions/wizard.png");
        objectActionMenu.add(generatePublicGetUrl);        
        
        generateTorrentUrl = new JMenuItem("Generate Torrent URL...");
        generateTorrentUrl.setActionCommand("GenerateTorrentURL");
        generateTorrentUrl.addActionListener(this);
        applyIcon(generateTorrentUrl, "/images/nuvola/16x16/actions/wizard.png");
        objectActionMenu.add(generateTorrentUrl);        

        objectActionMenu.add(new JSeparator());

        deleteObjectMenuItem = new JMenuItem("Delete object(s)...");
        deleteObjectMenuItem.setActionCommand("DeleteObjects");
        deleteObjectMenuItem.addActionListener(this);
        applyIcon(deleteObjectMenuItem, "/images/nuvola/16x16/actions/cancel.png");
        objectActionMenu.add(deleteObjectMenuItem);
        
        viewObjectPropertiesMenuItem.setEnabled(false);
        refreshObjectMenuItem.setEnabled(false);
        updateObjectACLMenuItem.setEnabled(false);
        downloadObjectMenuItem.setEnabled(false);
        uploadFilesMenuItem.setEnabled(false);
        generatePublicGetUrl.setEnabled(false);
        generateTorrentUrl.setEnabled(false);
        deleteObjectMenuItem.setEnabled(false);
        
        // Tools menu.
        JMenu toolsMenu = new JMenu("Tools");
        
        bucketLoggingMenuItem = new JMenuItem("Configure Bucket logging...");
        bucketLoggingMenuItem.setActionCommand("BucketLogging");
        bucketLoggingMenuItem.addActionListener(this);
        bucketLoggingMenuItem.setEnabled(false);
        applyIcon(bucketLoggingMenuItem, "/images/nuvola/16x16/actions/toggle_log.png");
        toolsMenu.add(bucketLoggingMenuItem);
        
        toolsMenu.add(new JSeparator());
        
        preferencesDialogMenuItem = new JMenuItem("Preferences...");
        preferencesDialogMenuItem.setActionCommand("PreferencesDialog");
        preferencesDialogMenuItem.addActionListener(this);
        applyIcon(preferencesDialogMenuItem, "/images/nuvola/16x16/actions/configure.png");
        toolsMenu.add(preferencesDialogMenuItem);
        
        // Help menu.
        JMenu helpMenu = new JMenu("Help");
        cockpitHelpMenuItem = new JMenuItem("Cockpit Guide");
        cockpitHelpMenuItem.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               try {
                   followHyperlink(new URL(Constants.JETS3T_COCKPIT_HELP_PAGE), "_blank");
               } catch (MalformedURLException ex) {
                   throw new IllegalStateException("Invalid URL embedded in program: " 
                       + Constants.JETS3T_COCKPIT_HELP_PAGE);
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
                    throw new IllegalStateException("Invalid URL embedded in program: " 
                        + Constants.AMAZON_S3_PAGE);
                }
            } 
         });
        helpMenu.add(amazonS3HelpMenuItem);
        
        
        // Build application menu bar.
        appMenuBar.add(serviceMenu);
        appMenuBar.add(toolsMenu);
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
                        String message = "Unable to start accept dropped item(s)";
                        log.error(message, e);
                        ErrorDialog.showDialog(ownerFrame, null, message, e);
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
     *        describes the status of a task in more detail, such as the current transfer rate and Time remaining.
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
       if (progressDisplay == null) {
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
     *        describes the status of a task in more detail, such as the current transfer rate and time remaining.
     * @param currentValue
     *        value representing how far through the task we are (relative to min and max values)
     */
    private void updateProgressDisplay(final String statusText, final String detailsText, final long currentValue) {
        if (progressDisplay != null) {
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
            try {
                listAllBuckets();
            } catch (S3ServiceException ex) {
                String message = "Unable to list your buckets in S3";
                log.error(message, ex);
                ErrorDialog.showDialog(ownerFrame, null, message, ex);                
            }
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
        } else if ("GeneratePublicGetURL".equals(event.getActionCommand())) {
            generatePublicGetUrl();
        } else if ("GenerateTorrentURL".equals(event.getActionCommand())) {
            generateTorrentUrl();
        } else if ("DeleteObjects".equals(event.getActionCommand())) {
            deleteSelectedObjects();
        } else if ("DownloadObjects".equals(event.getActionCommand())) {
            try {
                downloadSelectedObjects();
            } catch (Exception ex) {
                String message = "Unable to download objects from S3";
                log.error(message, ex);
                ErrorDialog.showDialog(ownerFrame, this, message, ex);
            }
        } else if ("UploadFiles".equals(event.getActionCommand())) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setDialogTitle("Choose file(s) to upload");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setApproveButtonText("Upload files");
            
            int returnVal = fileChooser.showOpenDialog(ownerFrame);
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                    return;
            }
            
            final File[] uploadFiles = fileChooser.getSelectedFiles();
            if (uploadFiles.length == 0) {
                return;
            }

            final Frame myOwnerFrame = ownerFrame;
            final HyperlinkActivatedListener hyperLinkListener = this;
            new Thread() {
                public void run() {                           
                    try {
                        uploadFilesToS3(uploadFiles);
                    } catch (Exception ex) {
                        String message = "Unable to upload files to S3";
                        log.error(message, ex);
                        ErrorDialog.showDialog(myOwnerFrame, hyperLinkListener, message, ex);                
                    }
                }
            }.start();
        } else if (event.getSource().equals(filterObjectsCheckBox)) {
            if (filterObjectsCheckBox.isSelected()) {
                filterObjectsPanel.setVisible(true);                 
            } else {
                filterObjectsPanel.setVisible(false);
                filterObjectsPrefix.setText("");
                if (filterObjectsDelimiter.getSelectedIndex() != 0) {
                    filterObjectsDelimiter.setSelectedIndex(0);
                }
            }
        }
        
        // Tools events
        else if ("BucketLogging".equals(event.getActionCommand())) {
            S3Bucket[] buckets = bucketTableModel.getBuckets();
            BucketLoggingDialog.showDialog(ownerFrame, s3ServiceMulti.getS3Service(), buckets, this);
        }
        
        // Preference Events        
        else if ("PreferencesDialog".equals(event.getActionCommand())) {
            PreferencesDialog.showDialog(cockpitPreferences, ownerFrame, this);
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
     * Displays the {@link StartupDialog} dialog and, if the user provides login credentials,
     * logs into the S3 service using those credentials.
     */
    private void loginEvent() {
        try {
            final AWSCredentials awsCredentials = 
                StartupDialog.showDialog(ownerFrame, this);

            s3ServiceMulti = new S3ServiceMulti(
                new RestS3Service(awsCredentials, APPLICATION_DESCRIPTION, null), this);

            if (awsCredentials == null) {
                log.debug("Log in cancelled by user");
                return;
            } 

            listAllBuckets();            
            updateObjectsSummary(false);
            
            ownerFrame.setTitle(APPLICATION_TITLE + " : " + awsCredentials.getAccessKey());
            loginMenuItem.setEnabled(false);
            logoutMenuItem.setEnabled(true);
            
            refreshBucketMenuItem.setEnabled(true);
            createBucketMenuItem.setEnabled(true);
            bucketLoggingMenuItem.setEnabled(true);
        } catch (Exception e) {
            String message = "Unable to log in to S3";
            log.error(message, e);
            ErrorDialog.showDialog(ownerFrame, this, message, e);
            
            logoutEvent();
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
            bucketTableModel.removeAllBuckets();
            objectTableModel.removeAllObjects();
                        
            updateObjectsSummary(false);

            ownerFrame.setTitle(APPLICATION_TITLE);
            loginMenuItem.setEnabled(true);
            logoutMenuItem.setEnabled(false);
            
            refreshBucketMenuItem.setEnabled(false);
            createBucketMenuItem.setEnabled(false);
            bucketLoggingMenuItem.setEnabled(false);            
        } catch (Exception e) {
            String message = "Unable to log out from S3";
            log.error(message, e);
            ErrorDialog.showDialog(ownerFrame, this, message, e);
        }
    }
    
    /**
     * Displays the currently selected bucket's properties in the dialog {@link ItemPropertiesDialog}. 
     */
    private void listBucketProperties() {
        ItemPropertiesDialog.showDialog(ownerFrame, getCurrentSelectedBucket());
    }
    
    /**
     * Displays the currently selected object's properties in the dialog {@link ItemPropertiesDialog}. 
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
    private void listAllBuckets() throws S3ServiceException {
        // This is all very convoluted, it was done this way to ensure we can display the dialog box.
        
        new Thread() {
            public void run() {
                try {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            startProgressDisplay("Listing buckets for " + s3ServiceMulti.getAWSCredentials().getAccessKey());
                            cachedBuckets.clear();
                            bucketsTable.clearSelection();       
                            bucketTableModel.removeAllBuckets();
                            objectTableModel.removeAllObjects();   
                        }
                    });                   
                    
                    final S3Bucket[] buckets = s3ServiceMulti.getS3Service().listAllBuckets();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            for (int i = 0; i < buckets.length; i++) {
                                bucketTableModel.addBucket(buckets[i]);
                            }
                        }
                    });
                } catch (final Exception e) {
                    stopProgressDisplay();
                    logoutEvent();

                    String message = "Unable to list your buckets in S3, please log in again";
                    log.error(message, e);
                    ErrorDialog.showDialog(ownerFrame, null, message, e);

                    loginEvent();
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
            uploadFilesMenuItem.setEnabled(false);
            
            objectTableModel.removeAllObjects();
            
            objectsTable.getDropTarget().setActive(false);
            objectsTableSP.getDropTarget().setActive(false);
            
            return;
        }
        
        viewBucketPropertiesMenuItem.setEnabled(true);
        refreshBucketMenuItem.setEnabled(true);
        updateBucketACLMenuItem.setEnabled(true);
        deleteBucketMenuItem.setEnabled(true);
        
        refreshObjectMenuItem.setEnabled(true);
        uploadFilesMenuItem.setEnabled(true);
        
        objectsTable.getDropTarget().setActive(true);
        objectsTableSP.getDropTarget().setActive(true);
        
        if (cachedBuckets.containsKey(newlySelectedBucket.getName())) {
            S3Object[] objects = (S3Object[]) cachedBuckets.get(newlySelectedBucket.getName());
            
            objectTableModel.removeAllObjects();                    
            objectTableModel.addObjects(objects);
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
        generatePublicGetUrl.setEnabled(count == 1);
        generateTorrentUrl.setEnabled(count == 1);
    }

    /**
     * Starts a thread to run {@link S3ServiceMulti#listObjects}.
     */
    private void listObjects() {
        if (getCurrentSelectedBucket() == null) {
            // Oops, better do nothing.
            return;
        }
        
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
                            objectTableModel.removeAllObjects();                                                
                        }
                    });
                    
                    final String prefix = filterObjectsPrefix.getText();
                    final String delimiter = (String) filterObjectsDelimiter.getSelectedItem();

                    final ArrayList allObjects = new ArrayList();
                    String priorLastKey = null;
                    do {
                        S3ObjectsChunk chunk = s3ServiceMulti.getS3Service().listObjectsChunked(
                            getCurrentSelectedBucket().getName(), prefix, delimiter, 
                            BUCKET_LIST_CHUNKING_SIZE, priorLastKey);
                        
                        final S3Object[] objects = chunk.getObjects();
                        priorLastKey = chunk.getPriorLastKey();
                        allObjects.addAll(Arrays.asList(objects));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                objectTableModel.addObjects(objects);
                                updateObjectsSummary(true);
                                updateProgressDisplay("Listed " + allObjects.size() + " objects in " 
                                    + getCurrentSelectedBucket().getName(), 0);
                            }
                        });                        
                    } while (!listingCancelled[0] && priorLastKey != null);

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            updateObjectsSummary(listingCancelled[0]);
                            S3Object[] allObjects = objectTableModel.getObjects();
                            cachedBuckets.put(getCurrentSelectedBucket().getName(), allObjects);
                        }
                    });                        

                } catch (final Exception e) {
                    stopProgressDisplay();
                    
                    String message = "Unable to list objects";
                    log.error(message, e);
                    ErrorDialog.showDialog(ownerFrame, null, message, e);
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
        S3Object[] objects = objectTableModel.getObjects();
        
        try {
            String summary = "Please select a bucket";        
            long totalBytes = 0;
            if (objects != null) {
                summary = "<html>" + objects.length + " item" + (objects.length != 1? "s" : "");
                
                for (int i = 0; i < objects.length; i++) {
                    totalBytes += objects[i].getContentLength();
                }
                if (totalBytes > 0) {
                    summary += ", " + byteFormatter.formatByteSize(totalBytes);
                }
                summary += " @ " + timeSDF.format(new Date());
                
                if (isObjectFilteringActive()) {
                    summary += " - <font color=\"blue\">Filtered</font>";                    
                }
                if (isIncompleteListing) {
                    summary += " - <font color=\"red\">Incomplete</font>";
                }     
                summary += "</html>";
            }        
            
            objectsSummaryLabel.setText(summary);
        } catch (Throwable t) {
            String message = "Unable to update object list summary";
            log.error(message, t);
            ErrorDialog.showDialog(ownerFrame, this, message, t);
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
        bucketActionMenu.show(invoker, xPos, yPos);
    }
    
    /**
     * @return the bucket currently selected in the gui, null if no bucket is selected.
     */
    private S3Bucket getCurrentSelectedBucket() {
        if (bucketsTable.getSelectedRows().length == 0) {
            return null;
        } else {
            return bucketTableModel.getBucket(
                bucketTableModelSorter.modelIndex(
                    bucketsTable.getSelectedRows()[0]));
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
        objectActionMenu.show(invoker, xPos, yPos);
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
                        int insertRow = bucketTableModel.addBucket(
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
            
            String message = "Unable to create a bucket";
            log.error(message, event.getErrorCause());
            ErrorDialog.showDialog(ownerFrame, this, message, event.getErrorCause());
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
            bucketTableModel.removeBucket(currentBucket);
        } catch (Exception e) {
            String message = "Unable to delete bucket";
            log.error(message, e);
            ErrorDialog.showDialog(ownerFrame, this, message, e);
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
                    bucketTableModel.addBucket(thirdPartyBucket);
                } else {
                    String message = "Unable to access third-party bucket: " + bucketName;
                    log.error(message);
                    ErrorDialog.showDialog(ownerFrame, this, message, null);
                }
            }            
        } catch (Exception e) {
            String message = "Unable to access third-party bucket";
            log.error(message, e);
            ErrorDialog.showDialog(ownerFrame, this, message, e);
        }        
    }
    
    /**
     * Updates the ACL settings for the currently selected bucket.
     */
    private void updateBucketAccessControlList() {
        try {
            S3Bucket currentBucket = getCurrentSelectedBucket();
            AccessControlList bucketACL = s3ServiceMulti.getS3Service().getBucketAcl(currentBucket);
            
            AccessControlList updatedBucketACL = AccessControlDialog.showDialog(
                ownerFrame, new S3Bucket[] {currentBucket}, bucketACL, this);
            if (updatedBucketACL != null) {
                currentBucket.setAcl(updatedBucketACL);
                s3ServiceMulti.getS3Service().putBucketAcl(currentBucket);
            }
        } catch (Exception e) {
            String message = "Unable to update bucket's Access Control List";
            log.error(message, e);
            ErrorDialog.showDialog(ownerFrame, this, message, e);
        }        
    }    
    
    /**
     * @return the set of objects currently selected in the gui, or an empty array if none are selected.
     */
    private S3Object[] getSelectedObjects() {
        int viewRows[] = objectsTable.getSelectedRows();
        if (viewRows.length == 0) {
            return new S3Object[] {};
        } else {
            S3Object objects[] = new S3Object[viewRows.length];
            for (int i = 0; i < viewRows.length; i++) {
                int modelRow = objectTableModelSorter.modelIndex(viewRows[i]);
                objects[i] = objectTableModel.getObject(modelRow);
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
            final HyperlinkActivatedListener hyperlinkListener = this;
            
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
                    AccessControlList updatedObjectACL = AccessControlDialog.showDialog(
                        ownerFrame, objectsWithACL, mergedACL, hyperlinkListener);
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
            
            String message = "Unable to lookup Access Control list for object(s)";
            log.error(message, event.getErrorCause());
            ErrorDialog.showDialog(ownerFrame, this, message, event.getErrorCause());
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
     * application's <code>S3ServiceMulti</code> triggers a <code>UpdateACLEvent</code>.
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
            
            String message = "Unable to update Access Control List(s)";
            log.error(message, event.getErrorCause());
            ErrorDialog.showDialog(ownerFrame, this, message, event.getErrorCause());
        }
    }

    /**
     * Prepares to perform a download of objects from S3 by prompting the user for a directory
     * to store the files in, then performing the download.
     * 
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
        
        prepareForObjectsDownload();
    }
    
    private void prepareForObjectsDownload() {
        // Build map of existing local files.
        filesInDownloadDirectoryMap = FileComparer.
            buildFileMap(downloadDirectory, null);
        
        // Build map of S3 Objects being downloaded. 
        s3DownloadObjectsMap = FileComparer.populateS3ObjectMap("", getSelectedObjects());

        // Identify objects that may clash with existing files, or may be directories,
        // and retrieve details for these.
        ArrayList potentialClashingObjects = new ArrayList();
        Set existingFilesObjectKeys = filesInDownloadDirectoryMap.keySet();
        Iterator objectKeyIter = s3DownloadObjectsMap.keySet().iterator();
        while (objectKeyIter.hasNext()) {
            String objectKey = (String) objectKeyIter.next();
            S3Object object = (S3Object) s3DownloadObjectsMap.get(objectKey);
            if (object.getContentLength() == 0 || existingFilesObjectKeys.contains(objectKey)) {
                potentialClashingObjects.add(object);
            }
        }
        
        if (potentialClashingObjects.size() > 0) {
            // Retrieve details of potential clashes.
            final S3Object[] clashingObjects = (S3Object[])
                potentialClashingObjects.toArray(new S3Object[] {});
            (new Thread() {
                public void run() {
                    isDownloadingObjects = true;
                    retrieveObjectsDetails(clashingObjects);
                }
            }).start();
        } else {
            performObjectsDownload();
        }
    }
    
    /**
     * Performs the real work of downloading files by comparing the download candidates against
     * existing files, prompting the user whether to overwrite any pre-existing file versions, 
     * and starting {@link S3ServiceMulti#downloadObjects} where the real work is done.
     *
     */
    private void performObjectsDownload() {        
        try {
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
            ArrayList downloadPackageList = new ArrayList();
        
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

                boolean isZipped = false;
                EncryptionUtil encryptionUtil = null;
                
                if ("gzip".equalsIgnoreCase(objects[i].getContentEncoding())
                    || objects[i].containsMetadata(Constants.METADATA_JETS3T_COMPRESSED))
                {
                    // Automatically inflate gzipped data.
                    isZipped = true;
                }
                if (objects[i].containsMetadata(Constants.METADATA_JETS3T_CRYPTO_ALGORITHM) 
                    || objects[i].containsMetadata(Constants.METADATA_JETS3T_ENCRYPTED_OBSOLETE))
                {
                    log.debug("Decrypting encrypted data for object: " + objects[i].getKey());
                    
                    // Prompt user for the password, if necessary.
                    if (!cockpitPreferences.isEncryptionPasswordSet()) {
                        throw new S3ServiceException(
                            "One or more objects are encrypted. Cockpit cannot download encrypted "
                            + "objects unless the encyption password is set in Preferences");
                    }

                    if (objects[i].containsMetadata(Constants.METADATA_JETS3T_ENCRYPTED_OBSOLETE)) {
                        // Item is encrypted with obsolete crypto.
                        log.warn("Object is encrypted with out-dated crypto version, please update it when possible: " 
                            + objects[i].getKey());
                        encryptionUtil = EncryptionUtil.getObsoleteEncryptionUtil(
                            cockpitPreferences.getEncryptionPassword());                                            
                    } else {
                        String algorithm = (String) objects[i].getMetadata(
                            Constants.METADATA_JETS3T_CRYPTO_ALGORITHM);
                        String version = (String) objects[i].getMetadata(
                            Constants.METADATA_JETS3T_CRYPTO_VERSION);
                        encryptionUtil = new EncryptionUtil(
                            cockpitPreferences.getEncryptionPassword(), algorithm);                                            
                    }                    
                }
                
                downloadPackageList.add(new DownloadPackage(
                    objects[i], file, isZipped, encryptionUtil));            
            }
            
            final DownloadPackage[] downloadPackagesArray = (DownloadPackage[])
                downloadPackageList.toArray(new DownloadPackage[] {});        
            (new Thread() {
                public void run() {
                    s3ServiceMulti.downloadObjects(getCurrentSelectedBucket(), downloadPackagesArray);
                }
            }).start();
        } catch (Exception e) {
            String message = "Unable to download objects";
            log.error(message, e);
            ErrorDialog.showDialog(ownerFrame, this, message, e);
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
            stopProgressDisplay();        
        }
        else if (ServiceEvent.EVENT_ERROR == event.getEventCode()) {
            stopProgressDisplay();
            
            String message = "Unable to download object(s)";
            log.error(message, event.getErrorCause());
            ErrorDialog.showDialog(ownerFrame, this, message, event.getErrorCause());
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
        if (!cockpitPreferences.isUploadCompressionActive() 
            && !cockpitPreferences.isUploadEncryptionActive()) 
        {
            // No file pre-processing required.
            return originalFile;
        }
        
        String actionText = "";
        
        // File must be pre-processed. Process data from original file 
        // and write it to a temporary one ready for upload.
        final File tempUploadFile = File.createTempFile("JetS3tCockpit",".tmp");
        tempUploadFile.deleteOnExit();
        
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tempUploadFile));
        InputStream inputStream = new BufferedInputStream(new FileInputStream(originalFile));
        
        String contentEncoding = null;
        if (cockpitPreferences.isUploadCompressionActive()) {
            inputStream = new GZipDeflatingInputStream(inputStream);
            contentEncoding = "gzip";
            newObject.addMetadata(Constants.METADATA_JETS3T_COMPRESSED, "gzip"); 
            actionText += "Compressing";                
        } 
        if (cockpitPreferences.isUploadEncryptionActive()) {
            EncryptionUtil encryptionUtil = new EncryptionUtil(
                cockpitPreferences.getEncryptionPassword());
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
            // Fail if encryption is turned on but no password is available.
            if (cockpitPreferences.isUploadEncryptionActive()
                && !cockpitPreferences.isEncryptionPasswordSet())
            {
                ErrorDialog.showDialog(ownerFrame, this, 
                    "Cockpit cannot upload encrypted "
                    + "objects unless the encyption password is set in Preferences", null);
                return;

            }

            // Build map of files proposed for upload.
            Map filesForUploadMap = FileComparer.buildFileMap(uploadingFiles);
            
            // Build map of objects already existing in target S3 bucket with keys
            // matching the proposed upload keys.
            List objectsWithExistingKeys = new ArrayList();
            S3Object[] existingObjects = objectTableModel.getObjects();
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
            
            startProgressDisplay("Prepared 0 of " + fileKeysForUpload.size() 
                + " file(s) for upload", 0, fileKeysForUpload.size(), null, null);
            
            // Populate S3Objects representing upload files with metadata etc.
            final S3Object[] objects = new S3Object[fileKeysForUpload.size()];
            int objectIndex = 0;
            for (Iterator iter = fileKeysForUpload.iterator(); iter.hasNext();) {
                String fileKey = iter.next().toString();
                File file = (File) filesForUploadMap.get(fileKey);
                
                S3Object newObject = new S3Object(fileKey);
                
                String aclPreferenceString = cockpitPreferences.getUploadACLPermission();
                if (CockpitPreferences.UPLOAD_ACL_PERMISSION_PRIVATE.equals(aclPreferenceString)) {
                    // Objects are private by default, nothing more to do.
                } else if (CockpitPreferences.UPLOAD_ACL_PERMISSION_PUBLIC_READ.equals(aclPreferenceString)) {
                    newObject.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
                } else if (CockpitPreferences.UPLOAD_ACL_PERMISSION_PUBLIC_READ_WRITE.equals(aclPreferenceString)) {
                    newObject.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ_WRITE);                    
                } else {
                    log.warn("Ignoring unrecognised upload ACL permission setting: " + aclPreferenceString);                    
                }
                
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
            String message = "Unable to upload object(s)";
            log.error(message, e);
            ErrorDialog.showDialog(ownerFrame, this, message, e);
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
                        objectTableModel.addObject(event.getCreatedObjects()[i]);
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
                    S3Object[] allObjects = objectTableModel.getObjects();
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
            
            String message = "Unable to upload object(s)";
            log.error(message, event.getErrorCause());
            ErrorDialog.showDialog(ownerFrame, this, message, event.getErrorCause());
        }
    }
    
    private void generatePublicGetUrl() {
        final S3Object[] objects = getSelectedObjects(); 

        if (objects.length != 1) {
            log.warn("Ignoring Generate Public URL object command, can only operate on a single object");
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
                s3ServiceMulti.getAWSCredentials(), cal.getTime());
            
            // Display signed URL
            JOptionPane.showInputDialog(ownerFrame,
                "URL for '" + currentObject.getKey() + "'."
                + "\n This URL will be valid until approximately " + cal.getTime() 
                + "\n(Amazon's server time may be ahead of or behind your computer's clock)",
                "Signed URL", JOptionPane.INFORMATION_MESSAGE, null, null, signedUrl);

        } catch (NumberFormatException e) {
            String message = "Hours must be a valid decimal value; eg 3, 0.1";
            log.error(message, e);
            ErrorDialog.showDialog(ownerFrame, this, message, e);
        } catch (S3ServiceException e) {
            String message = "Unable to generate public GET URL";
            log.error(message, e);
            ErrorDialog.showDialog(ownerFrame, this, message, e);
        }
    }    
        
    private void generateTorrentUrl() {
        final S3Object[] objects = getSelectedObjects(); 

        if (objects.length != 1) {
            log.warn("Ignoring Generate Public URL object command, can only operate on a single object");
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
            log.warn("Ignoring delete object(s) command, no currently selected objects");
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
                        objectTableModel.removeObject(
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
                    S3Object[] allObjects = objectTableModel.getObjects();
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
            
            String message = "Unable to delete object(s)";
            log.error(message, event.getErrorCause());
            ErrorDialog.showDialog(ownerFrame, this, message, event.getErrorCause());
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
                        int modelIndex = objectTableModel.addObject(object);
                        log.debug("Updated table with " + object.getKey() + ", content-type=" + object.getContentType());

                        if (isDownloadingObjects) {
                            s3DownloadObjectsMap.put(object.getKey(), object);
                            log.debug("Updated object download list with " + object.getKey() 
                                + ", content-type=" + object.getContentType());
                        }
                        
                        int viewIndex = objectTableModelSorter.viewIndex(modelIndex);
                        if (isDownloadingObjects || viewingObjectProperties) {
                            objectsTable.addRowSelectionInterval(viewIndex, viewIndex);
                        }
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
            
            if (isDownloadingObjects) {
                performObjectsDownload();
                isDownloadingObjects = false;
            } else if (viewingObjectProperties) {
                ItemPropertiesDialog.showDialog(ownerFrame, getSelectedObjects()[0]);
                viewingObjectProperties = false;                    
            }            
        }
        else if (ServiceEvent.EVENT_CANCELLED == event.getEventCode()) {
            stopProgressDisplay();        
        }
        else if (ServiceEvent.EVENT_ERROR == event.getEventCode()) {
            stopProgressDisplay();
            
            String message = "Unable to retrieve object(s) details";
            log.error(message, event.getErrorCause());
            ErrorDialog.showDialog(ownerFrame, this, message, event.getErrorCause());
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
            if (detailsText.trim().length() > 0) {
                detailsText += " - ";
            }
            long secondsRemaining = watcher.getTimeRemaining();
            detailsText += "Time remaining: " + timeFormatter.formatTime(secondsRemaining);
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
    
    private boolean isObjectFilteringActive() {
        if (!filterObjectsCheckBox.isSelected()) {
            return false;
        } else {
            String delimiter = (String) filterObjectsDelimiter.getSelectedItem();
            if (filterObjectsPrefix.getText().length() > 0 
                || delimiter.length() > 0) 
            {
                return true;
            } else {
                return false;
            }
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
        JFrame ownerFrame = new JFrame("JetS3t Cockpit");
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
