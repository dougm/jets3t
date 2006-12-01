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
package org.jets3t.apps.uploader;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.NumberFormatter;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.CredentialsNotAvailableException;
import org.apache.commons.httpclient.auth.CredentialsProvider;
import org.apache.commons.httpclient.auth.NTLMScheme;
import org.apache.commons.httpclient.auth.RFC2617Scheme;
import org.apache.commons.httpclient.contrib.proxy.PluginProxyUtil;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.apps.uploader.skins.SkinnedMetalTheme;
import org.jets3t.apps.uploader.skins.SkinsFactory;
import org.jets3t.service.Jets3tProperties;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
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
import org.jets3t.service.multithread.S3ServiceEventListener;
import org.jets3t.service.multithread.S3ServiceMulti;
import org.jets3t.service.multithread.ServiceEvent;
import org.jets3t.service.multithread.ThreadWatcher;
import org.jets3t.service.multithread.UpdateACLEvent;
import org.jets3t.service.utils.ServiceUtils;

/**
 * Dual application and applet for uploading files and XML metadata information to the Amazon S3 
 * service.
 * <p>
 * This application presents a wizard GUI interface of multiple screens:
 * <ul>
 * <li>1: Displays a series of questions to the user to collect information. The answers to these
 * questions are included in the XML metadata document uploaded to S3 along with files.</li>
 * <li>2: The user chooses a file to upload using a File browser, or by drag-and-dropping a file
 * onto the application window.</li>
 * <li>3: A confirmation page displaying information about the file the user has chosen to 
 * upload, such as its name and size</li>
 * <li>4: The actual upload process with Status text messages and a progress bar to indicate
 * the progress of the upload</li>
 * <li>5: A "thankyou" page confirming that the user's file has been uploaded</li>
 * </ul>
 * The user may navigate forward or backward through the wizard using Back/Next buttons.
 * <p>
 * The Uploader is highly configurable through properties specified in a file
 * <tt>uploader.properties</tt> that must be available at the root of the classpath. Details of
 * the available properties are included in comments in this file.
 * <p> 
 * Properties set in <tt>uploader.properties</tt> configure:
 * <ul>
 * <li>constraints on the files that may be uploaded in the application, such as their 
 * maximum size and the filname extensions that are permitted</li>
 * <li>the content/mime types of files based on their file extension</li>
 * <li>the questions and GUI elements used to collect user information</li>
 * <li>text prompts and information displayed through the wizard process</li>
 * <li>whether the Back and Next navigation buttons are displayed, and the label and/or image 
 * displayed in the buttons if they are visible</li>
 * <li>whether the GUI is skinned to look more like a standard web page, and the colours and
 * font to use if so</li>
 * <li>the S3 bucket files are uploaded to</li>
 * </ul> 
 * 
 * @author James Murty
 */
public class Uploader extends JApplet implements S3ServiceEventListener, ActionListener, ListSelectionListener, HyperlinkActivatedListener, CredentialsProvider {
    private static final long serialVersionUID = -5687535269925501969L;

    private static final Log log = LogFactory.getLog(Uploader.class);
    
    public static final int WIZARD_START = 0;
    public static final int WIZARD_SCREEN_1 = 1;
    public static final int WIZARD_SCREEN_2 = 2;
    public static final int WIZARD_SCREEN_3 = 3;
    public static final int WIZARD_SCREEN_4 = 4;
    public static final int WIZARD_SCREEN_5 = 5;
    
    public static final String ERROR_CODE__MISSING_REQUIRED_PARAM = "100";
    public static final String ERROR_CODE__MISSING_FILE_EXTENSIONS = "101";
    public static final String ERROR_CODE__INVALID_PUT_URL = "102";
    public static final String ERROR_CODE__INIT_S3_SERVICE = "103";
    public static final String ERROR_CODE__MISSING_EXTERNAL_UUID = "104";
    public static final String ERROR_CODE__UUID_NOT_UNIQUE = "105";
    public static final String ERROR_CODE__S3_UPLOAD_FAILED = "106";
    
    
    private Frame ownerFrame = null;
    
    /**
     * The file to upload to S3.
     */
    private File fileToUpload = null;
    
    /**
     * The list of file extensions accepted by the Uploader.
     */
    private ArrayList validFileExtensions = new ArrayList();
    
    /**
     * The target S3 bucket for uploads.
     */
    private S3Bucket s3Bucket = new S3Bucket("1FMFX9QNQHMZ32MPA7G2.Test"); // TODO Remove this completely...
    
    /**
     * Uploader's properties.
     */
    private Jets3tProperties uploaderProperties = null;
    
    /**
     * Properties set in stand-alone application from the command line arguments.
     */
    private Properties standAlongArgumentProperties = null;
    
    /**
     * Stores cached authentication credentials provided by the user, with the Auth scheme
     * as key.
     */
    private HashMap cachedAuthCredentials = new HashMap();
    
    /*
     * Upload file constraints.
     */
    private int fileMaxCount = 0;
    private long fileMaxSizeMB = 0;
    private long fileMinSizeMB = 0;
    
    /*
     * Insets used throughout the application.
     */
    private final Insets insetsDefault = new Insets(5, 7, 5, 7);
    private final Insets insetsNone = new Insets(0, 0, 0, 0);

    private int currentState = WIZARD_START;
    
    private boolean isRunningAsApplet = false;
        
    private HashMap fieldComponentsMap = new HashMap();
    private HashMap parametersMap = new HashMap();
    
    private SkinsFactory skinsFactory = null;
    private final GridBagLayout GRID_BAG_LAYOUT = new GridBagLayout();
    
    /*
     * GUI elements that need to be referenced outside initGui method.
     */
    private JHtmlLabel userGuidanceLabel = null;
    private JPanel screen1Panel = null;
    private JPanel screen2Panel = null;
    private JPanel screen3Panel = null;
    private JPanel screen4Panel = null;
    private JPanel screen5Panel = null;
    private JPanel buttonsPanel = null;
    private JButton backButton = null;
    private JButton nextButton = null;
    private JButton cancelUploadButton = null;
    private JHtmlLabel dragDropTargetLabel = null;
    private JHtmlLabel fileToUploadLabel = null;
    private JHtmlLabel fileInformationLabel = null;
    private JHtmlLabel progressETATextLabel = null;
    private JProgressBar progressBar = null;
    private JHtmlLabel progressStatusTextLabel = null;
    private JHtmlLabel finalMessageLabel = null;
    private CancelEventTrigger uploadCancelEventTrigger = null;
    
    /**
     * Keep track of how long an upload has been running for.
     */
    private long uploadStartTime = 0;
    
    /**
     * Set to true when the object/file being uploaded is the final in a set, eg when
     * the XML metadata is being uploaded after a movie file.
     */
    private volatile boolean uploadingFinalObject = false;
    
    /**
     * Set to true when the last object/file uploaded was successfully uploaded.
     */
    private boolean lastUploadWasSuccessful = false;

    /**
     * Set to true if an upload failed due to a key name clash in S3, in which case an error
     * message is displayed in the final 'thankyou' screen.
     */
    private boolean fatalErrorOccurred = false;

    /**
     * Constructor to run this application as an Applet.
     */
    public Uploader() {
        isRunningAsApplet = true;
    }
            
    /**
     * Constructor to run this application in a stand-alone window.
     * 
     * @param ownerFrame the frame the application will be displayed in
     * @throws S3ServiceException
     */
    public Uploader(JFrame ownerFrame, Properties standAlongArgumentProperties) throws S3ServiceException {
        this.ownerFrame = ownerFrame;
        this.standAlongArgumentProperties = standAlongArgumentProperties;
        isRunningAsApplet = false;
                
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
        
        // Read properties from classpath.
        InputStream propertiesIS = getClass().getResourceAsStream("/uploader.properties");
        try {
            uploaderProperties = Jets3tProperties.getInstance(propertiesIS);
        } catch (IOException e) {
            log.error("Unable to load properties file as resource stream: /uploader.properties");
        }

        if (isRunningAsApplet) {
            // Read parameters for Applet, based on names specified in the uploader properties.
            String appletParamNames = uploaderProperties.getStringProperty("applet.params", "");
            StringTokenizer st = new StringTokenizer(appletParamNames, ",");
            while (st.hasMoreTokens()) {
                String paramName = st.nextToken();
                String paramValue = this.getParameter(paramName);

                // Fatal error if a parameter is missing.
                if (null == paramValue) {
                    log.error("Missing required applet parameter: " + paramName);
                    fatalError(ERROR_CODE__MISSING_REQUIRED_PARAM);               
                } else {
                    log.debug("Found applet parameter: " + paramName + "='" + paramValue + "'");
                }
                
                // Set params as properties in the central properties source for this application.
                // Note that parameter values will over-write properties with the same name.
                uploaderProperties.setProperty(paramName, paramValue);
                
                // Store params in a separate map, which is used to build XML document.
                parametersMap.put(paramName, paramValue);
            }
        } else {
            // Add application parameters properties.
            if (standAlongArgumentProperties != null) {
                Enumeration e = standAlongArgumentProperties.keys();
                while (e.hasMoreElements()) {
                    String propName = (String) e.nextElement();
                    Object propValue = standAlongArgumentProperties.getProperty(propName);
                    
                    // Fatal error if a parameter is missing.
                    if (null == propValue) {
                        log.error("Missing required command-line property: " + propValue);
                        fatalError(ERROR_CODE__MISSING_REQUIRED_PARAM);               
                    } else {
                        log.debug("Using command-line property: " + propName + "='" + propValue + "'");                    
                    }

                    // Set arguments as properties in the central properties source for this application.
                    // Note that argument values will over-write properties with the same name.
                    uploaderProperties.setProperty(propName, propValue);

                    // Store arguments in a separate map, which is used to build XML document.
                    parametersMap.put(propName, propValue);
                }                
            }
        }

        // Determine the file constraints. 
        fileMaxCount = uploaderProperties.getIntProperty("file.maxCount", 1);
        fileMaxSizeMB = uploaderProperties.getLongProperty("file.maxSizeMB", 200);
        fileMinSizeMB = uploaderProperties.getLongProperty("file.minSizeMB", 0);
        
        // Initialise the GUI.
        initGui();              

        // Determine valid file extensions.
        String validFileExtensionsStr = uploaderProperties.getStringProperty("file.types", "");
        if ("".equals(validFileExtensionsStr) || validFileExtensionsStr == null) {
            log.warn("No valid file types are specified with the property 'file.types'");
            fatalError(ERROR_CODE__MISSING_FILE_EXTENSIONS);
        }
        StringTokenizer st = new StringTokenizer(validFileExtensionsStr, ",");
        while (st.hasMoreTokens()) {
            validFileExtensions.add(st.nextToken().toLowerCase());
        }

    }    
    
    /**
     * Builds a user input panel matching the fields specified in the uploader.properties file.
     * 
     * @param fieldsPanel
     * the panel component to add prompt and user input components to.
     */
    private void buildFieldsPanel(JPanel fieldsPanel) {
        int fieldRow = 0;
        
        for (int fieldNo = 0; fieldNo < 100; fieldNo++) {
            String fieldName = uploaderProperties.getStringProperty("field." + fieldNo + ".name", null);
            String fieldType = uploaderProperties.getStringProperty("field." + fieldNo + ".type", null);
            String fieldPrompt = uploaderProperties.getStringProperty("field." + fieldNo + ".prompt", null);
            String fieldOptions = uploaderProperties.getStringProperty("field." + fieldNo + ".options", null);
            
            if (fieldName == null) {
                // Expect there to be at least one field at number 0 or 1. If not, stop looking.
                if (fieldNo > 1) {
                    return;
                } else {
                    continue;
                }
            } else {
                if (fieldType == null || fieldPrompt == null) {
                    log.warn("Field '" + fieldName + "' missing .type or .prompt properties");
                    continue;
                }
                if ("radio".equals(fieldType)) {
                    if (fieldOptions == null) {
                        log.warn("Radio button field '" + fieldName + "' is missing the required .options property");
                        continue;
                    }
                    
                    JHtmlLabel label = new JHtmlLabel(fieldPrompt, this);
                    fieldsPanel.add(label,
                        new GridBagConstraints(0, fieldRow++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));

                    JPanel optionsPanel = skinsFactory.createSkinnedJPanel("OptionsPanel");
                    optionsPanel.setLayout(GRID_BAG_LAYOUT);
                    int columnOffset = 0;
                    ButtonGroup buttonGroup = new ButtonGroup();                    
                    StringTokenizer st = new StringTokenizer(fieldOptions, ",");
                    while (st.hasMoreTokens()) {
                        String option = st.nextToken();
                        JRadioButton radioButton = new JRadioButton(option);
                        buttonGroup.add(radioButton);
                        if (buttonGroup.getButtonCount() == 1) {
                            // Make first button the default.
                            radioButton.setSelected(true);
                        }

                        optionsPanel.add(radioButton,
                            new GridBagConstraints(columnOffset++, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insetsDefault, 0, 0));
                    }
                    fieldsPanel.add(optionsPanel,
                        new GridBagConstraints(0, fieldRow++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insetsNone, 0, 0));

                    fieldComponentsMap.put(fieldName, buttonGroup);
                } else if ("selection".equals(fieldType)) {
                    if (fieldOptions == null) {
                        log.warn("Radio button field '" + fieldName + "' is missing the required .options property");
                        continue;
                    }
                    
                    JHtmlLabel label = new JHtmlLabel(fieldPrompt, this);
                    fieldsPanel.add(label,
                        new GridBagConstraints(0, fieldRow++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));

                    JComboBox comboBox = new JComboBox();
                    StringTokenizer st = new StringTokenizer(fieldOptions, ",");
                    while (st.hasMoreTokens()) {
                        String option = st.nextToken();
                        comboBox.addItem(option);
                    }
                    fieldsPanel.add(comboBox,
                        new GridBagConstraints(0, fieldRow++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        
                    fieldComponentsMap.put(fieldName, comboBox);
                } else if ("text".equals(fieldType)) {
                    JHtmlLabel label = new JHtmlLabel(fieldPrompt, this);
                    fieldsPanel.add(label,
                        new GridBagConstraints(0, fieldRow++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
                    JTextField textField = new JTextField();
                    fieldsPanel.add(textField,
                        new GridBagConstraints(0, fieldRow++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
                    
                    fieldComponentsMap.put(fieldName, textField);
                } else if ("password".equals(fieldType)) {
                    JHtmlLabel label = new JHtmlLabel(fieldPrompt, this);
                    fieldsPanel.add(label,
                        new GridBagConstraints(0, fieldRow++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
                    JPasswordField passwordField = new JPasswordField();
                    fieldsPanel.add(passwordField,
                        new GridBagConstraints(1, fieldRow++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
                    
                    fieldComponentsMap.put(fieldName, passwordField);
                } else if (fieldType.startsWith("textarea")) {
                    JHtmlLabel label = new JHtmlLabel(fieldPrompt, this);
                    fieldsPanel.add(label,
                        new GridBagConstraints(0, fieldRow++, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));                    
                    JTextArea textArea = new JTextArea();
                    fieldsPanel.add(new JScrollPane(textArea),
                        new GridBagConstraints(0, fieldRow++, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insetsDefault, 0, 0));
                    
                    fieldComponentsMap.put(fieldName, textArea);
                } else {
                    log.warn("Unrecognised .type setting for field '" + fieldName + "'");
                }                
            }
        }
    }
    
    /**
     * Initialises the application's GUI elements.
     */
    private void initGui() {
        // Initialise skins factory. 
        String skinName = uploaderProperties.getStringProperty("skinName", null);
        skinsFactory = SkinsFactory.getInstance(skinName, uploaderProperties.getProperties()); 
        
        
//        // Apply skinning.
//        boolean applySkin = uploaderProperties.getBoolProperty("gui.isSkinned", false);
//        if (applySkin) {
//            initSkin();
//        }
        
        // Apply branding
        String applicationTitle = uploaderProperties.getStringProperty("gui.applicationTitle", null);
        if (applicationTitle != null) {
            ownerFrame.setTitle(applicationTitle);
        }
        String applicationIconPath = uploaderProperties.getStringProperty("gui.applicationIcon", null);
        if (!isRunningAsApplet && applicationIconPath != null) {            
            try {
                URL iconUrl = getClass().getResource(applicationIconPath);
                ImageIcon icon = new ImageIcon(iconUrl);
                ownerFrame.setIconImage(icon.getImage());
            } catch (Exception e) {
                log.error("Failed to set application icon: " + applicationIconPath, e);
            }
        }
        String footerHtml = uploaderProperties.getStringProperty("gui.footerHtml", null);        
        String footerIconPath = uploaderProperties.getStringProperty("gui.footerIcon", null);        
        
        // Footer for branding        
        boolean includeFooter = false;
        JLabel footerLabel = new JHtmlLabel(this);
        footerLabel.setHorizontalAlignment(JLabel.CENTER);
        if (footerHtml != null) {
            footerLabel.setText(footerHtml);
            includeFooter = true;
        }
        if (footerIconPath != null) {
            try {
                URL iconUrl = getClass().getResource(footerIconPath);
                ImageIcon icon = new ImageIcon(iconUrl);
                footerLabel.setIcon(icon);
                includeFooter = true;
            } catch (Exception e) {
                log.error("Failed to set application icon: " + applicationIconPath, e);
            }
        }
        
        // Screeen 1 : User input fields.
        screen1Panel = skinsFactory.createSkinnedJPanel("Screen1Panel");
        screen1Panel.setLayout(GRID_BAG_LAYOUT);
        buildFieldsPanel(screen1Panel);
               
        // Screen 2 : Drag/drop panel.
        screen2Panel = skinsFactory.createSkinnedJPanel("Screen2Panel");
        screen2Panel.setLayout(GRID_BAG_LAYOUT);
        dragDropTargetLabel = new JHtmlLabel(this);
        dragDropTargetLabel.setHorizontalAlignment(JLabel.CENTER);
        dragDropTargetLabel.setVerticalAlignment(JLabel.CENTER);
        
        String browseButtonImagePath = uploaderProperties
            .getStringProperty("screen.2.browseButton.image", null);
        String browseButtonText = uploaderProperties
            .getStringProperty("screen.2.browseButton.text", null);
        String browseButtonTooltip = uploaderProperties
            .getStringProperty("screen.2.browseButton.tooltip", null);
        
        JButton chooseFileButton = new JButton();
        
        if (browseButtonImagePath != null) {
            URL iconURL = getClass().getResource(browseButtonImagePath);
            if (iconURL == null) {
                log.error("Unable to load image URL for browse button: " + browseButtonImagePath);
            } else {
                ImageIcon icon = new ImageIcon(iconURL);
                chooseFileButton.setIcon(icon);
            }
        }
        if (browseButtonText != null) {
            chooseFileButton.setText(replaceMessageVariables(browseButtonText));  
        }
        if (browseButtonTooltip != null) {
            chooseFileButton.setToolTipText(browseButtonTooltip);            
        }
        if (browseButtonImagePath == null && browseButtonText == null) {
            chooseFileButton.setVisible(false);
        } else {
            chooseFileButton.setVisible(true);
        }
        
        chooseFileButton.setBorder(BorderFactory.createEmptyBorder());
        chooseFileButton.setActionCommand("ChooseFile");
        chooseFileButton.addActionListener(this);
        
        // Skinning for Drag & Drop background.
        Color dndBackgroundColor = screen2Panel.getBackground();
        String dndBackgroundColorValue = uploaderProperties.getStringProperty(
            "gui.draganddrop.backgroundColor", null);
        if (dndBackgroundColorValue != null) {
            Color color = Color.decode(dndBackgroundColorValue);
            if (color == null) {
                log.error("Unable to set drag&drop background color with value: " 
                    + dndBackgroundColorValue);
            } else {
                dndBackgroundColor = color;
            }
        }
        screen2Panel.setBackground(dndBackgroundColor);

        screen2Panel.add(dragDropTargetLabel,
            new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsDefault, 0, 0));
        screen2Panel.add(chooseFileButton,
            new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsDefault, 0, 0));

        // Screen 3 : Information about the file to be uploaded.
        screen3Panel = skinsFactory.createSkinnedJPanel("Screen3Panel");
        screen3Panel.setLayout(GRID_BAG_LAYOUT);
        fileToUploadLabel = new JHtmlLabel(this);
        fileToUploadLabel.setHorizontalAlignment(JLabel.CENTER);
        fileToUploadLabel.setVerticalAlignment(JLabel.CENTER);
        screen3Panel.add(fileToUploadLabel,
            new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsDefault, 0, 0));        
        
        // Screen 4 : Upload progress.
        screen4Panel = skinsFactory.createSkinnedJPanel("Screen4Panel");
        screen4Panel.setLayout(GRID_BAG_LAYOUT);
        fileInformationLabel = new JHtmlLabel(" ", this);
        fileInformationLabel.setHorizontalAlignment(JLabel.CENTER);        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressStatusTextLabel = new JHtmlLabel(" ", this);
        progressStatusTextLabel.setHorizontalAlignment(JLabel.CENTER);
        progressETATextLabel = new JHtmlLabel(" ", this);
        progressETATextLabel.setHorizontalAlignment(JLabel.CENTER);

        // Skinning for ProgressBar foreground.
        Color pbForegroundColor = progressBar.getForeground();
        String pbForegroundColorValue = uploaderProperties.getStringProperty(
            "gui.progressbar.foregroundColor", null);
        if (pbForegroundColorValue != null) {
            Color color = Color.decode(pbForegroundColorValue);
            if (color == null) {
                log.error("Unable to set progressBar foreground color with value: "
                    + pbForegroundColorValue);
            } else {
                pbForegroundColor = color;
            }
        }
        progressBar.setForeground(pbForegroundColor);

        cancelUploadButton = skinsFactory.createSkinnedJButton("CancelUploadButton");
        
        String cancelUploadButtonImagePath = uploaderProperties
            .getStringProperty("screen.4.cancelButton.image", null);
        String cancelUploadButtonText = uploaderProperties
            .getStringProperty("screen.4.cancelButton.text", null);
        String cancelUploadButtonTooltip = uploaderProperties
            .getStringProperty("screen.4.cancelButton.tooltip", null);
        
        if (cancelUploadButtonImagePath != null) {
            URL iconURL = getClass().getResource(cancelUploadButtonImagePath);
            if (iconURL == null) {
                log.error("Unable to load image URL for cancel button: " + cancelUploadButtonImagePath);
            } else {
                ImageIcon icon = new ImageIcon(iconURL);
                cancelUploadButton.setIcon(icon);
            }
        }
        if (cancelUploadButtonText != null) {
            cancelUploadButton.setText(replaceMessageVariables(cancelUploadButtonText));
        }
        if (cancelUploadButtonImagePath == null && cancelUploadButtonText == null) {
            cancelUploadButton.setVisible(false);
        } else {
            cancelUploadButton.setVisible(true);
        }
        if (cancelUploadButtonTooltip != null) {
            cancelUploadButton.setToolTipText(cancelUploadButtonTooltip);
        }
        cancelUploadButton.setBorder(BorderFactory.createEmptyBorder());                
        cancelUploadButton.setActionCommand("CancelUpload");
        cancelUploadButton.addActionListener(this);
        screen4Panel.add(fileInformationLabel,
            new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        screen4Panel.add(progressBar,
            new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        screen4Panel.add(progressStatusTextLabel,
            new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        screen4Panel.add(progressETATextLabel,
            new GridBagConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        screen4Panel.add(cancelUploadButton,
            new GridBagConstraints(0, 4, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsDefault, 0, 0));

        // Screen 5 : Thankyou message.
        screen5Panel = skinsFactory.createSkinnedJPanel("Screen5Panel");
        screen5Panel.setLayout(GRID_BAG_LAYOUT);
        finalMessageLabel = new JHtmlLabel(this);
        finalMessageLabel.setHorizontalAlignment(JLabel.CENTER);
        screen5Panel.add(finalMessageLabel,
            new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsDefault, 0, 0));
        
        // Wizard Button panel.
        backButton = skinsFactory.createSkinnedJButton("backButton"); 
        backButton.setActionCommand("Back");
        backButton.addActionListener(this);
        nextButton = skinsFactory.createSkinnedJButton("nextButton");
        nextButton.setActionCommand("Next");
        nextButton.addActionListener(this);
        
        buttonsPanel = skinsFactory.createSkinnedJPanel("ButtonsPanel");
        buttonsPanel.setLayout(GRID_BAG_LAYOUT);
        buttonsPanel.add(backButton,
            new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insetsDefault, 0, 0));
        buttonsPanel.add(nextButton,
            new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insetsDefault, 0, 0));

        // Overall content panel.
        JPanel appContentPanel = skinsFactory.createSkinnedJPanel("ApplicationContentPanel");
        appContentPanel.setLayout(GRID_BAG_LAYOUT);
        JPanel userGuidancePanel = skinsFactory.createSkinnedJPanel("UserGuidancePanel");
        userGuidancePanel.setLayout(GRID_BAG_LAYOUT);
        JPanel primaryPanel = skinsFactory.createSkinnedJPanel("PrimaryPanel");
        primaryPanel.setLayout(GRID_BAG_LAYOUT);
        JPanel navigationPanel = skinsFactory.createSkinnedJPanel("NavigationPanel");
        navigationPanel.setLayout(GRID_BAG_LAYOUT);

        appContentPanel.add(userGuidancePanel,
            new GridBagConstraints(0, 0, 1, 1, 1, 0.2, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsNone, 0, 0));
        appContentPanel.add(primaryPanel,
            new GridBagConstraints(0, 1, 1, 1, 1, 0.6, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsNone, 0, 0));
        appContentPanel.add(navigationPanel,
            new GridBagConstraints(0, 2, 1, 1, 1, 0.2, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsNone, 0, 0));
        if (includeFooter) {
            log.debug("Adding footer for branding");
            appContentPanel.add(footerLabel, 
                new GridBagConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsNone, 0, 0));
        }
        this.getContentPane().add(appContentPanel);

        userGuidanceLabel = new JHtmlLabel(this);
        userGuidanceLabel.setHorizontalAlignment(JLabel.CENTER);
        
        userGuidancePanel.add(userGuidanceLabel, 
            new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsNone, 0, 0));
        navigationPanel.add(buttonsPanel, 
            new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsNone, 0, 0));

        // TODO Change all of this ugliness to a CardLayout.
        GridBagConstraints overlappingGBC = 
            new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, insetsNone, 0, 0);
        primaryPanel.add(screen1Panel, overlappingGBC);
        primaryPanel.add(screen2Panel, overlappingGBC);
        primaryPanel.add(screen3Panel, overlappingGBC);
        primaryPanel.add(screen4Panel, overlappingGBC);
        primaryPanel.add(screen5Panel, overlappingGBC);
        primaryPanel.add(screen5Panel, overlappingGBC);
                
        screen1Panel.setVisible(false);
        screen2Panel.setVisible(false);
        screen3Panel.setVisible(false);
        screen4Panel.setVisible(false);
        screen5Panel.setVisible(false);
        buttonsPanel.setVisible(false);
        
        // Set preferred sizes
        int preferredWidth = uploaderProperties.getIntProperty("gui.minSizeWidth", 400);
        int preferredHeight = uploaderProperties.getIntProperty("gui.minSizeHeight", 400);
        this.setBounds(new Rectangle(new Dimension(preferredWidth, preferredHeight)));
        
        // Initialize drop target.
        initDropTarget(new JComponent[] {screen2Panel} );
        screen2Panel.getDropTarget().setActive(true);
        
        wizardStepForward();
    }
    
    /**
     * Initialises the GUI skinning by applying configuration options to set the
     * background colour, text colour and text font to use for display. 
     */
    private void initSkin() {
        try {
            // Determine system defaults.
            JLabel defaultLabel = new JLabel();
            Color backgroundColor = defaultLabel.getBackground();
            Color textColor = defaultLabel.getForeground();
            Font font = defaultLabel.getFont();

            // Find skinning configurations.
            String backgroundColorValue = uploaderProperties.getStringProperty("gui.backgroundColor", null);
            String textColorValue = uploaderProperties.getStringProperty("gui.textColor", null);
            String fontValue = uploaderProperties.getStringProperty("gui.font", null);
            
            // Apply any configurations.
            if (backgroundColorValue != null) {
                Color color = Color.decode(backgroundColorValue);
                if (color == null) {
                    log.error("Unable to set background color with value: " + backgroundColorValue);
                } else {
                    backgroundColor = color;                    
                }
            }
            if (textColorValue != null) {
                Color color = Color.decode(textColorValue);
                if (color == null) {
                    log.error("Unable to set text color with value: " + backgroundColorValue);
                } else {
                    textColor = color;                    
                }
            }
            if (fontValue != null) {
                Font myFont = Font.decode(fontValue);
                if (font == null) {
                    log.error("Unable to set font with value: " + fontValue);
                } else {
                    font = myFont;                    
                }
            }
                     
            // Update metal theme with configured display properties.
            SkinnedMetalTheme skinnedTheme = new SkinnedMetalTheme(
                backgroundColor, textColor, font);            
            MetalLookAndFeel metalLookAndFeel = new MetalLookAndFeel();
            MetalLookAndFeel.setCurrentTheme(skinnedTheme);
            
            // Apply theme.
            UIManager.setLookAndFeel(metalLookAndFeel);
        } catch (Throwable t) {
            log.error("Unable to set skinnable metal theme", t);
        }   
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
                            screen2Panel.setBorder(BorderFactory.createBevelBorder(1));
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
                            screen2Panel.setBorder(BorderFactory.createBevelBorder(1));
                        };
                    });                    
                } 
            }

            public void dragExit(DropTargetEvent dte) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        screen2Panel.setBorder(BorderFactory.createEtchedBorder());
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
                            if (!checkProposedUploadFiles(fileList)) {
                                screen2Panel.setBorder(BorderFactory.createEtchedBorder());
                            }
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
     * Checks that all the files in a list are acceptable for uploading.
     * Files are checked against the following criteria:
     * <ul>
     * <li>There are not too many</li>
     * <li>The size is greater than the minimum size, and less that the maximum size.</li>
     * <li>The file has a file extension matching one of those explicitly allowed</li>
     * </ul>
     * Any deviations from the rules result in an error message, and the method returns false.
     * A side-effect of this method is that the wizard is moved forward one screen if the 
     * files are all valid.
     * 
     * @param fileList
     * A list of {@link File}s to check.
     * 
     * @return
     * true if the files in the list are all acceptable, false otherwise.
     */
    private boolean checkProposedUploadFiles(List fileList) {
        // Check number of files for upload is within constraints.
        if (fileMaxCount > 0 && fileList.size() > fileMaxCount) {
            reportException(ownerFrame, "You may only upload " + fileMaxCount 
                + (fileMaxCount == 1? " file" : " files"), null);
            return false;
        }
        
        // Check file size within constraints.
        Iterator iter = fileList.iterator();
        while (iter.hasNext()) {
            File file = (File) iter.next();
            long fileSizeMB = file.length() / (1024 * 1024);

            if (fileMinSizeMB > 0 && fileSizeMB < fileMinSizeMB) {
                reportException(ownerFrame, "File size must be greater than " + fileMinSizeMB + " MB", null);
                return false;
            }
            if (fileMaxSizeMB > 0 && fileSizeMB > fileMaxSizeMB) {
                reportException(ownerFrame, "File size must be less than " + fileMaxSizeMB + " MB", null);
                return false;
            }
        }        
        
        // Check file extension is acceptable.   
        iter = fileList.iterator();
        while (iter.hasNext()) {
            File file = (File) iter.next();
            String fileName = file.getName();
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (!validFileExtensions.contains(fileExtension.toLowerCase())) {
                String extList = validFileExtensions.toString();
                extList = extList.substring(1, extList.length() -1);
                extList = extList.replaceAll(",", " ");
                reportException(ownerFrame, "File name must end with one of the following extensions:\n" 
                    + extList, null);                                
                return false;
            }
        }
        
        // Note - from here on, we only handle a single file, not a list.
        fileToUpload = (File) fileList.get(0); 
        wizardStepForward();
        return true;
    }
    
    /**
     * Retrieves a signed PUT URL from the given URL address.
     * The URL must point at a server-side script or service that accepts POST messages.
     * The POST message will include parameters for all the items in uploaderProperties, 
     * that is everything in the file uploader.properties plus all the applet's parameters.
     * Based on this input, the server-side script decides whether to allow access and return
     * a signed PUT URL.
     * 
     * @param credsProviderParamName
     * the name of the parameter containing the server URL target for the PUT request.
     * @return
     * the AWS credentials provided by the server-side script if access was allowed, null otherwise.
     * 
     * @throws HttpException
     * @throws IOException
     */
    private UploadInformation retrieveUploadInformation(String credentialsProviderUrl, S3Object object) 
        throws HttpException, IOException 
    {
        // Add all properties/parameters to credentials POST request.
        PostMethod httpMethod = new PostMethod(credentialsProviderUrl);
        Properties props = uploaderProperties.getProperties();
        Iterator iter = props.keySet().iterator();
        while (iter.hasNext()) { 
            String fieldName = (String) iter.next();
            String fieldValue = (String) props.getProperty(fieldName);
            httpMethod.addParameter(fieldName, fieldValue);
        }
        httpMethod.addParameter("objectKey", object.getKey());
        
        // Try to detect any necessary proxy config.
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setParameter(
            CredentialsProvider.PROVIDER, this);     
        ProxyHost proxyHost = null;
        try {
            proxyHost = PluginProxyUtil.detectProxy(new URL(credentialsProviderUrl));
            if (proxyHost != null) {
                HostConfiguration hostConfig = new HostConfiguration();
                hostConfig.setProxyHost(proxyHost);
                httpClient.setHostConfiguration(hostConfig);
                httpClient.getParams().setAuthenticationPreemptive(true);
            }
        } catch (Throwable t) {
            log.error("Unable to set proxy", t);
        }

        log.debug("Retrieving credentials from url: " + credentialsProviderUrl);
        String signedPutUrl = null;
        try {                        
            int responseCode = httpClient.executeMethod(httpMethod);
            if (responseCode == 200) {
                // Consume response content and release connection.
                String responseBodyText = "";

                Header encodingHeader = httpMethod.getResponseHeader("Content-Encoding");
                if (encodingHeader != null && "gzip".equalsIgnoreCase(encodingHeader.getValue())) {
                    log.debug("Inflating gzip-encoded response");
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                        new GZIPInputStream(httpMethod.getResponseBodyAsStream())));
                    responseBodyText = br.readLine();
                } else {
                    responseBodyText = httpMethod.getResponseBodyAsString();
                }                
                
                if (!responseBodyText.startsWith("http")) {
                    log.debug("Response from credentials provider service does not include signed PUT URL: '" + responseBodyText + "'");
                    return null;
                }                
                
                // TODO Source all this information from service response.
                return new UploadInformation(responseBodyText, object.getBucketName(), object.getKey());
            } 
        } catch (Exception e) {
            log.error("Unable to retrieve credentials from credentials provider url: " + credentialsProviderUrl, e);
        } finally {
            httpMethod.releaseConnection();            
        }
        return null;
    }
    
    /**
     * Uploads to S3 the file referenced by the variable fileToUpload, providing
     * progress feedback to the user all the while.
     *
     */
    private void uploadFilesToS3() {        
        try {
            progressStatusTextLabel.setText(
                replaceMessageVariables(uploaderProperties.getStringProperty("screen.4.connectingMessage", 
                "Missing property 'screen.4.connectingMessage'")));
            
            // Retrieve credentials from URL location value by the property 'credentialsServiceUrl'.  
            String credentialsServiceUrl = uploaderProperties.getStringProperty(
                "credentialsServiceUrl", "missingCredentialsProviderUrl");
                       
            // Create file hash.
            progressStatusTextLabel.setText(replaceMessageVariables(
                uploaderProperties.getStringProperty("screen.4.hashingMessage", 
                "Missing property 'screen.4.hashingMessage'")));
            log.debug("Computing MD5 hash for file: " + fileToUpload);
            byte[] fileHash = ServiceUtils.computeMD5Hash(new FileInputStream(fileToUpload));

            // Create S3Objects representing file for upload and XML file.                
            /* Build upload file S3 object */
            S3Object uploadFileObject = new S3Object(s3Bucket, fileToUpload);
            // Store hash of original file data in metadata.
            uploadFileObject.setMd5Hash(fileHash);
            
            // Obtain signed PUT URLs from server-side credentials service.
            UploadInformation uploadInfoForFile = retrieveUploadInformation(credentialsServiceUrl, uploadFileObject);
            if (uploadInfoForFile == null) {
                fatalError(ERROR_CODE__INVALID_PUT_URL);                    
                return;
            }
            // TODO Update S3Object if service specifies different bucket/key names.

            // Generate XML document.
            XmlGenerator xmlGen = new XmlGenerator();
            Map filenamesMap = new HashMap();
//            filenamesMap.put("uploaderUUID", ); // TODO
            filenamesMap.put("originalFilename", fileToUpload.getName());
            filenamesMap.put("filename", uploadInfoForFile.objectKey);
            String xmlDocument = xmlGen.generateXml(
                uploaderProperties.getStringProperty("xml.version", "1.0"),
                fieldComponentsMap, parametersMap, filenamesMap);
            log.debug("XML document : \n" + xmlDocument);            

            /* Build xml document object */
            S3Object xmlDocObject = new S3Object(s3Bucket, uploadInfoForFile.objectKey + ".xml", xmlDocument);
            xmlDocObject.setContentType("application/xml");
                                            

            UploadInformation uploadInfoForXml = retrieveUploadInformation(credentialsServiceUrl, xmlDocObject);            
            if (uploadInfoForXml == null) {
                fatalError(ERROR_CODE__INVALID_PUT_URL);                    
                return;
            }
            // TODO Update S3Object if service specifies different bucket/key names.
            
            RestS3Service restS3Service = new RestS3Service(null, this);
            restS3Service.putObjectWithSignedUrl(
                uploadInfoForFile.signedPutUrl, uploadFileObject);
            restS3Service.putObjectWithSignedUrl(
                uploadInfoForXml.signedPutUrl, xmlDocObject);
            
            // TODO Need threading for PUT URL-based file uploads...
            wizardStepForward();
//            // Upload the objects, file first then XML document.
//            uploadingFinalObject = false;
//            progressBar.setVisible(true);
//            s3ServiceMulti.putObjects(s3Bucket, new S3Object[] {uploadFileObject});
//            if (lastUploadWasSuccessful) {
//                uploadingFinalObject = true;
//                s3ServiceMulti.putObjects(s3Bucket, new S3Object[] {xmlDocObject});
//            }

        } catch (Exception e) {
            wizardStepBackward();
            reportException(ownerFrame, "File upload failed, please try again", e);
        } 
    }
    
    /**
     * Listener method that responds to events from the jets3t toolkit when objects are
     * created in S3 - ie when files are uploaded.
     */
    public void s3ServiceEventPerformed(final CreateObjectsEvent event) {
        if (ServiceEvent.EVENT_STARTED == event.getEventCode()) {
            lastUploadWasSuccessful = false;

            uploadStartTime = System.currentTimeMillis();
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    cancelUploadButton.setVisible(true);
                }
            });
            
            ThreadWatcher watcher = event.getThreadWatcher();
            uploadCancelEventTrigger = watcher.getCancelEventListener();
            
            // Show percentage of bytes transferred.
            String bytesTotalStr = describeByteSize(watcher.getBytesTotal());
            final String statusText = "Uploaded 0 of " + bytesTotalStr;                

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    progressStatusTextLabel.setText(replaceMessageVariables(statusText));
                    progressBar.setValue(0);
                }
            });
        } 
        else if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventCode()) {
            ThreadWatcher watcher = event.getThreadWatcher();
            
            if (watcher.getBytesTransferred() >= watcher.getBytesTotal()) {
                // Upload is completed, just waiting on resonse from S3.
                String statusText = "Upload completed, awaiting confirmation";
                
                progressBar.setValue(100);
                progressStatusTextLabel.setText(replaceMessageVariables(statusText));
                progressETATextLabel.setText("");
            } else {                    
                String bytesCompletedStr = describeByteSize(watcher.getBytesTransferred());
                String bytesTotalStr = describeByteSize(watcher.getBytesTotal());
                String statusText = "Uploaded " + bytesCompletedStr + " of " + bytesTotalStr;
                int percentage = (int) 
                    (((double)watcher.getBytesTransferred() / watcher.getBytesTotal()) * 100);
                
                long bytesPerSecondEstimate = watcher.getBytesTransferred() * 1000 
                    / (System.currentTimeMillis() - uploadStartTime);
                String rateText = describeByteSize(bytesPerSecondEstimate) + "/s";
                
                progressBar.setValue(percentage);
                progressStatusTextLabel.setText(replaceMessageVariables(statusText));
                progressETATextLabel.setText(replaceMessageVariables(rateText));
            }
        }
        else if (ServiceEvent.EVENT_COMPLETED == event.getEventCode()) {
            if (uploadingFinalObject) {
                drawWizardScreen(WIZARD_SCREEN_5);
            }
            lastUploadWasSuccessful = true;
            progressBar.setValue(0);          
            progressStatusTextLabel.setText("");
            progressETATextLabel.setText("");
        }
        else if (ServiceEvent.EVENT_CANCELLED == event.getEventCode()) {
            progressBar.setValue(0);          
            progressStatusTextLabel.setText("");
            progressETATextLabel.setText("");
        }
        else if (ServiceEvent.EVENT_ERROR == event.getEventCode()) {
            progressBar.setValue(0);          
            progressStatusTextLabel.setText("");
            progressETATextLabel.setText("");
            fatalError(ERROR_CODE__S3_UPLOAD_FAILED);
            reportException(ownerFrame, "File upload failed", event.getErrorCause());
        }
    }
        
    /**
     * Draws the wizard screen appropriate to the stage in the wizard process the user has
     * reached.  
     * 
     * @param nextState
     * an integer detailing the screen the user is moving to.
     */
    private void drawWizardScreen(int nextState) {
        // Configure screen based on properties.
        String title = uploaderProperties.getStringProperty(
            "screen." + nextState + ".title", "");
        userGuidanceLabel.setText(replaceMessageVariables(title));
        
        String nextButtonImagePath = uploaderProperties
            .getStringProperty("screen." + nextState + ".nextButton.image", null);
        String nextButtonText = uploaderProperties
            .getStringProperty("screen." + nextState + ".nextButton.text", null);
        String nextButtonTooltip = uploaderProperties
            .getStringProperty("screen." + nextState + ".nextButton.tooltip", null);
        if (nextButtonImagePath != null) {
            URL iconURL = getClass().getResource(nextButtonImagePath);
            if (iconURL == null) {
                log.error("Unable to load image URL for next button: " + nextButtonImagePath);                        
            } else {
                ImageIcon icon = new ImageIcon(iconURL);
                nextButton.setIcon(icon);                    
            }
        }
        if (nextButtonText != null) {
            nextButton.setText(replaceMessageVariables(nextButtonText));                
        }
        if (nextButtonImagePath == null && nextButtonText == null) {
            nextButton.setVisible(false);
        } else {
            nextButton.setVisible(true);
        }
        if (nextButtonTooltip != null) {
            nextButton.setToolTipText(nextButtonTooltip);
        }
        String backButtonImagePath = uploaderProperties
            .getStringProperty("screen." + nextState + ".backButton.image", null);
        String backButtonText = uploaderProperties
            .getStringProperty("screen." + nextState + ".backButton.text", null);
        String backButtonTooltip = uploaderProperties
            .getStringProperty("screen." + nextState + ".backButton.tooltip", null);
        if (backButtonImagePath != null) {
            URL iconURL = getClass().getResource(backButtonImagePath);
            if (iconURL == null) {
                log.error("Unable to load image URL for back button: " + backButtonImagePath);                        
            } else {
                ImageIcon icon = new ImageIcon(iconURL);
                backButton.setIcon(icon);                    
            }
        }
        if (backButtonText != null) {
            backButton.setText(replaceMessageVariables(backButtonText));                
        }
        if (backButtonImagePath == null && backButtonText == null) {
            backButton.setVisible(false);
        } else {
            backButton.setVisible(true);
        }
        if (backButtonTooltip != null) {
            backButton.setToolTipText(backButtonTooltip);
        }

        
        if (nextState == WIZARD_START) {
            // This screen should never be reachable via user interaction.
        } else if (nextState == WIZARD_SCREEN_1) {
            screen1Panel.setVisible(true);
            screen2Panel.setVisible(false);
            buttonsPanel.setVisible(true);
        } else if (nextState == WIZARD_SCREEN_2) {
            screen1Panel.setVisible(false);
            screen2Panel.setVisible(true);             
            screen3Panel.setVisible(false);
            
            dragDropTargetLabel.setText(
                replaceMessageVariables(uploaderProperties.getStringProperty("screen.2.dragDropPrompt", 
                "Missing property 'screen.2.dragDropPrompt'")));
            screen2Panel.setBorder(BorderFactory.createEtchedBorder());
        } else if (nextState == WIZARD_SCREEN_3) {
            screen2Panel.setVisible(false);
            screen3Panel.setVisible(true);
            screen4Panel.setVisible(false);

            String fileInformation = uploaderProperties.getStringProperty("screen.3.fileInformation", 
                "Missing property 'screen.3.fileInformation'");
            fileToUploadLabel.setText(replaceMessageVariables(fileInformation));
        } else if (nextState == WIZARD_SCREEN_4) {
            screen3Panel.setVisible(false);
            screen4Panel.setVisible(true);
            screen5Panel.setVisible(false);

            String fileInformation = uploaderProperties.getStringProperty("screen.4.fileInformation", 
                "Missing property 'screen.4.fileInformation'");
            fileInformationLabel.setText(replaceMessageVariables(fileInformation));

            progressBar.setVisible(false);
            cancelUploadButton.setVisible(false);
            new Thread() {
                public void run() {                           
                    uploadFilesToS3();
                }
            }.start();                    
        } else if (nextState == WIZARD_SCREEN_5) {
            screen4Panel.setVisible(false);
            screen5Panel.setVisible(true);

            String finalMessage = null;
            if (fatalErrorOccurred) {
                finalMessage = uploaderProperties.getStringProperty("screen.5.errorMessage", 
                    "Missing property 'screen.5.errorMessage'");                
            } else {                
                finalMessage = uploaderProperties.getStringProperty("screen.5.thankyouMessage", 
                    "Missing property 'screen.5.thankyouMessage'");                
            }
            
            finalMessageLabel.setText(replaceMessageVariables(finalMessage));
        } else {
            log.error("Ignoring unexpected wizard screen number: " + nextState);
            return;
        }
        currentState = nextState;                   
    }
    
    /**
     * Move the wizard forward one step/screen.
     */
    private void wizardStepForward() {
        drawWizardScreen(currentState + 1);
    }
    
    /**
     * Move the wizard backward one step/screen.
     */
    private void wizardStepBackward() {
        drawWizardScreen(currentState - 1);
    }
    
    /**
     * When a fatal error occurs, go straight to last screen to display the error message
     * and make the error code available as a variable (<code>${errorCode}</code>) to be used
     * in the error message displayed to the user.
     * 
     * @param errorCode
     * the error code/message
     */
    private void fatalError(String errorCode) {
        uploaderProperties.setProperty("errorCode", errorCode);
        fatalErrorOccurred = true;
        drawWizardScreen(WIZARD_SCREEN_5);
    }

    /**
     * Replaces variables of the form ${variableName} in the input string with the value of that
     * variable name in the local uploaderProperties properties object, or with one of the
     * following special variables:
     * <ul>
     * <li>fileName : Name of file being uploaded</li>
     * <li>fileSize : Size of the file being uploaded, eg 1.04 MB</li>
     * <li>filePath : Absolute path of the file being uploaded</li>
     * <li>maxFileSize : The maxiumum allowed file size in MB</li>
     * <li>maxFileCount : The maximum number of files that may be uploaded</li>
     * <li>validFileExtensions : A list of the file extensions allowed</li>
     * </ul>
     * If the variable named in the input string is not available, or has no value, the variable
     * reference is left in the result.
     * 
     * @param message
     * string that may have variables to replace
     * @return
     * the input string with any variable referenced replaced with the variable's value. 
     */
    private String replaceMessageVariables(String message) {
        String result = message;
        // Replace upload file variables, if an upload file has been chosen.        
        if (fileToUpload != null) {
            result = result.replaceAll("\\$\\{fileName\\}", fileToUpload.getName());
            result = result.replaceAll("\\$\\{fileSize\\}", describeByteSize(fileToUpload.length()));                    
            result = result.replaceAll("\\$\\{filePath\\}", fileToUpload.getAbsolutePath());
        }
        result = result.replaceAll("\\$\\{maxFileSize\\}", String.valueOf(fileMaxSizeMB));
        result = result.replaceAll("\\$\\{maxFileCount\\}", String.valueOf(fileMaxCount));

        String extList = validFileExtensions.toString();
        extList = extList.substring(1, extList.length() -1);
        extList = extList.replaceAll(",", " ");
        result = result.replaceAll("\\$\\{validFileExtensions\\}", extList);        

        Pattern pattern = Pattern.compile("\\$\\{.+?\\}");
        Matcher matcher = pattern.matcher(result);
        int offset = 0;
        while (matcher.find(offset)) {
            String variable = matcher.group();
            String variableName = variable.substring(2, variable.length() - 1);
            String replacement = uploaderProperties.getStringProperty(variableName, null);
            if (replacement != null) {
                result = result.substring(0, matcher.start()) + replacement + 
                    result.substring(matcher.end());
                offset = matcher.start() + 1;
                matcher.reset(result);
            } else {
                offset = matcher.start() + 1;                
            }
        }
        if (!result.equals(message)) {
            log.debug("Replaced variables in text: " + message + " => " + result);
        }                            
        return result;
    }
    
    /**
     * Handles GUI actions.
     */
    public void actionPerformed(ActionEvent actionEvent) {
        if ("Next".equals(actionEvent.getActionCommand())) {
            wizardStepForward();
        } else if ("Back".equals(actionEvent.getActionCommand())) {
            wizardStepBackward();
        } else if ("ChooseFile".equals(actionEvent.getActionCommand())) {
            JFileChooser fileChooser = new JFileChooser();
            UploaderFileExtensionFilter filter = new UploaderFileExtensionFilter(
                "Movie files", validFileExtensions);
            fileChooser.setFileFilter(filter);            
            fileChooser.setDialogTitle("Choose your movie");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setApproveButtonText("Choose movie");
            
            int returnVal = fileChooser.showOpenDialog(ownerFrame);
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                    return;
            }
            
            List fileList = new ArrayList();
            if (fileChooser.getSelectedFiles().length > 0) {
                fileList.addAll(Arrays.asList(fileChooser.getSelectedFiles()));                
            } else {
                fileList.add(fileChooser.getSelectedFile());
            }
            if (!checkProposedUploadFiles(fileList)) {
                screen2Panel.setBorder(BorderFactory.createEtchedBorder());
            }
        } else if ("CancelUpload".equals(actionEvent.getActionCommand())) {
            if (uploadCancelEventTrigger != null) {
                uploadCancelEventTrigger.cancelTask(this);
                progressBar.setValue(0);
                wizardStepBackward();
            } else {
                log.warn("Ignoring attempt to cancel file upload when cancel trigger is not available");
            }
        } else {
            log.warn("Unrecognised action command, ignoring: " + actionEvent.getActionCommand());
        }
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
        if (isRunningAsApplet) {
            if (target == null) {
                getAppletContext().showDocument(url);                
            } else {
                getAppletContext().showDocument(url, target);
            }
        } else {
            log.warn("Uploader does not launch HTML links when running stand-alone");
        }
    }

    /**
     * Converts a byte size into a human-readable string, such as "1.43 MB" or "27 KB".
     * The values used are based on powers of 1024, ie 1 KB = 1024 bytes, not 1000 bytes.
     * 
     * @param byteSize
     * the byte size of some item
     * @return
     * a human-readable description of the byte size
     */
    private String describeByteSize(long byteSize) {
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
            log.error("Unable to format byte size " + byteSize, e);
            return String.valueOf(byteSize);
        }
        return result;
    }    

    /**
     * Based on sample code InteractiveAuthenticationExample from: 
     * http://svn.apache.org/viewvc/jakarta/commons/proper/httpclient/trunk/src/examples/InteractiveAuthenticationExample.java?view=markup
     */
    public Credentials getCredentials(AuthScheme authscheme, String host, int port, boolean proxy) throws CredentialsNotAvailableException {
        if (authscheme == null) {
            return null;
        }
        try {
            String realm = authscheme.getRealm();
            if (cachedAuthCredentials.containsKey(realm)) {
                log.debug("Using cached credentials for authentication realm '" + realm + "'");
                return (Credentials) cachedAuthCredentials.get(realm);
            }
            
            Credentials credentials = null;
            
            if (authscheme instanceof NTLMScheme) {
                AuthenticationDialog pwDialog = new AuthenticationDialog(
                    ownerFrame, "Proxy Authentication", 
                    host + ":" + port + " requires Windows authentication", true);
                pwDialog.setVisible(true);
                if (pwDialog.getUser() != null) {
                    credentials = new NTCredentials(pwDialog.getUser(), pwDialog.getPassword(), 
                        host, pwDialog.getDomain());
                }
                pwDialog.dispose();
            } else
            if (authscheme instanceof RFC2617Scheme) {
                AuthenticationDialog pwDialog = new AuthenticationDialog(
                    ownerFrame, "Proxy Authentication", host + ":" + port 
                    + " requires authentication for the realm '" + authscheme.getRealm() + "'", false);
                pwDialog.setVisible(true);
                if (pwDialog.getUser() != null) {
                    credentials = new UsernamePasswordCredentials(pwDialog.getUser(), pwDialog.getPassword());
                }
                pwDialog.dispose();
            } else {
                throw new CredentialsNotAvailableException("Unsupported authentication scheme: " +
                    authscheme.getSchemeName());
            }
            if (credentials != null) {
                log.debug("Caching credentials for realm '" + realm + "'");
                cachedAuthCredentials.put(realm, credentials);
            }
            return credentials;
        } catch (IOException e) {
            throw new CredentialsNotAvailableException(e.getMessage(), e);
        }
    }
    
    /**
     * Displays a rudimentary error message dialog box.
     * If the "debugMode" property is set to true, a detailed error message is displayed with
     * technical details. If it is false, or not set, only a simple error message is displayed. 
     * 
     * @param ownerFrame
     * @param message
     * @param t
     */
    private void reportException(Frame ownerFrame, String message, Throwable t) {
        log.error(message);
        String detailsText = "";

        boolean debugModeOn = uploaderProperties.getBoolProperty("debugMode", false);
        
        if (t != null && debugModeOn) {
            log.error(message, t);

            // Show error dialog box.
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
        }
        JOptionPane.showMessageDialog(ownerFrame, message + "\n" + detailsText, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // S3 Service events that are not used in this Uploader application.
    public void s3ServiceEventPerformed(CreateBucketsEvent event) {}
    public void s3ServiceEventPerformed(DeleteObjectsEvent event) {}
    public void s3ServiceEventPerformed(GetObjectsEvent event) {}
    public void s3ServiceEventPerformed(GetObjectHeadsEvent event) {}
    public void s3ServiceEventPerformed(LookupACLEvent event) {}
    public void s3ServiceEventPerformed(UpdateACLEvent event) {}
    public void s3ServiceEventPerformed(DownloadObjectsEvent event) {}
    public void valueChanged(ListSelectionEvent arg0) {}
    
    public class UploadInformation {
        public String signedPutUrl = null;
        public String bucketName = null;
        public String objectKey = null;
        
        public UploadInformation(String signedPutUrl, String bucketName, String objectKey) {
            this.signedPutUrl = signedPutUrl;
            this.bucketName = bucketName;
            this.objectKey = objectKey;
        }
    }
    
    /**
     * Run the Uploader as a stand-alone application.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        JFrame ownerFrame = new JFrame("Uploader");
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

        // Read arguments as properties of the form: <propertyName>'='<propertyValue>
        Properties argumentProperties = new Properties();
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                int delimIndex = arg.indexOf("="); 
                if (delimIndex >= 0) {
                    String name = arg.substring(0, delimIndex);
                    String value = arg.substring(delimIndex + 1);
                    argumentProperties.put(name, value);
                } else {
                    System.out.println("Ignoring property argument with incorrect format: " + arg);
                }
            }
        }
        
        Uploader uploader = new Uploader(ownerFrame, argumentProperties);
        ownerFrame.getContentPane().add(uploader);
        ownerFrame.setBounds(uploader.getBounds());
        ownerFrame.setVisible(true);                        
    }
    
}
