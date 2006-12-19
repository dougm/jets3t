package org.jets3t.gui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jets3t.service.Constants;
import org.jets3t.service.Jets3tProperties;
import org.jets3t.service.S3ServiceException;

public class ErrorDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = -1913801256028107392L;

    private Jets3tProperties jets3tProperties = 
        Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME);
    
    private HyperlinkActivatedListener hyperlinkListener = null;
    
    private final Insets insetsZero = new Insets(0, 0, 0, 0);
    private final Insets insetsDefault = new Insets(3, 5, 3, 5);

    
    private ErrorDialog(Frame ownerFrame, HyperlinkActivatedListener hyperlinkListener) {
        super(ownerFrame, "Error Message", true);
        this.hyperlinkListener = hyperlinkListener;
    }
    
    private ErrorDialog(JDialog ownerDialog, HyperlinkActivatedListener hyperlinkListener) {
        super(ownerDialog, "Error Message", true);
        this.hyperlinkListener = hyperlinkListener;
    }
    
    /**
     * Initialises all GUI elements.
     */
    private void initGui(String message, String details) {
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

        JHtmlLabel messageLabel = new JHtmlLabel(message, hyperlinkListener);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        JHtmlLabel detailsLabel = new JHtmlLabel(details, hyperlinkListener);

        JButton okButton = new JButton("OK");
        okButton.setName("OK");
        okButton.addActionListener(this);

        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.add(okButton, new GridBagConstraints(0, 0, 
            1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsZero, 0, 0));
        
        int row = 0;
        this.getContentPane().setLayout(new GridBagLayout());
        this.getContentPane().add(messageLabel, new GridBagConstraints(0, row++, 
            1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(12, 5, 12, 5), 0, 0));
        this.getContentPane().add(detailsLabel, new GridBagConstraints(0, row++, 
            1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insetsDefault, 0, 0));
        this.getContentPane().add(buttonsPanel, new GridBagConstraints(0, row++, 
            1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
        
        this.pack();
        this.setLocationRelativeTo(this.getOwner());
    }
    
    public void actionPerformed(ActionEvent e) {
        if ("OK".equals(e.getActionCommand())) {
            this.hide();
        }
    }
    
    private String buildDetailedText(Throwable throwable) {
        if (!jets3tProperties.getBoolProperty("gui.verboseErrorDialog", true)) {
            return null;
        }
        
        String detailsText = "";
        if (throwable instanceof S3ServiceException) {
            detailsText += "<html><table border=\"0\">";
            
            S3ServiceException s3se = (S3ServiceException) throwable;
            
            if (s3se.getS3ErrorCode() != null) {
                detailsText += "<tr><td><b>S3 Error Code</b></td><td>" + s3se.getS3ErrorCode() + "</td></tr>";
            } else {
                detailsText += "<tr><td><b>Exception message</b></td></tr><tr><td>" + throwable.getMessage() + "</td></tr>";
            }
            
            if (s3se.getS3ErrorMessage() != null) {
                detailsText += "<tr><td><b>S3 Message</b></td><td>" + s3se.getS3ErrorMessage() + "</td></tr>";
            }
            
            if (s3se.getS3ErrorRequestId() != null) {
                detailsText += "<tr><td><b>S3 Request Id</b></td><td>" + s3se.getS3ErrorRequestId() + "</td></tr>";
            }

            if (s3se.getS3ErrorHostId() != null) {
                detailsText += "<tr><td><b>S3 Host Id</b></td><td>" + s3se.getS3ErrorHostId() + "</td></tr>";
            }

            boolean firstCause = true;
            Throwable cause = s3se.getCause();
            while (cause != null && cause.getMessage() != null) {
                if (firstCause) {
                    detailsText += "<tr><td><b>Cause</b></td></tr>";
                }
                detailsText += "<tr><td>" + cause.getMessage() + "</td></tr>";
                firstCause = false;
                cause = cause.getCause();
            }
            
            detailsText += "</table></html>";
        } else {
            if (throwable != null && throwable.getMessage() != null) {
                detailsText = "<html><b>Cause</b><br>" + throwable.getMessage() + "</html>";
            }
        }
        return detailsText;
    }
    
    public static void showDialog(Frame ownerFrame, HyperlinkActivatedListener hyperlinkListener,
        String message, Throwable throwable) 
    {
        ErrorDialog dialog = new ErrorDialog(ownerFrame, hyperlinkListener);
        dialog.initGui(message, dialog.buildDetailedText(throwable));
        dialog.setVisible(true);
        dialog.dispose();
    }

    public static void showDialog(JDialog ownerDialog, HyperlinkActivatedListener hyperlinkListener,
        String message, Throwable throwable) 
    {
        ErrorDialog dialog = new ErrorDialog(ownerDialog, hyperlinkListener);
        dialog.initGui(message, dialog.buildDetailedText(throwable));
        dialog.setVisible(true);
        dialog.dispose();
    }
    
}
