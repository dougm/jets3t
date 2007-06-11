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

/**
 * An Error dialog that displays information about an error that has occurred.
 * 
 * @author James Murty
 */
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
        this.getRootPane().setDefaultButton(okButton);

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
            this.setVisible(false);
        }
    }
    
    private String buildDetailedText(Throwable throwable) {
        if (!jets3tProperties.getBoolProperty("gui.verboseErrorDialog", true)) {
            return null;
        }
        
        StringBuffer detailsText = new StringBuffer();
        if (throwable instanceof S3ServiceException) {
            detailsText.append("<html><table border=\"0\">");
            
            S3ServiceException s3se = (S3ServiceException) throwable;
            
            if (s3se.getS3ErrorCode() != null) {
                detailsText.append("<tr><td><b>S3 Error Code</b></td><td>").append(s3se.getS3ErrorCode()).append("</td></tr>");
            } else {
                detailsText.append("<tr><td><b>Exception message</b></td></tr><tr><td>")
                    .append(throwable.getMessage()).append("</td></tr>");
            }
            
            if (s3se.getS3ErrorMessage() != null) {
                detailsText.append("<tr><td><b>S3 Message</b></td><td>")
                    .append(s3se.getS3ErrorMessage()).append("</td></tr>");
            }
            
            if (s3se.getS3ErrorRequestId() != null) {
                detailsText.append("<tr><td><b>S3 Request Id</b></td><td>")
                    .append(s3se.getS3ErrorRequestId()).append("</td></tr>");
            }

            if (s3se.getS3ErrorHostId() != null) {
                detailsText.append("<tr><td><b>S3 Host Id</b></td><td>")
                    .append(s3se.getS3ErrorHostId()).append("</td></tr>");
            }

            boolean firstCause = true;
            Throwable cause = s3se.getCause();
            while (cause != null && cause.getMessage() != null) {
                if (firstCause) {
                    detailsText.append("<tr><td><b>Cause</b></td></tr>");
                }
                detailsText.append("<tr><td>").append(cause.getMessage()).append("</td></tr>");
                firstCause = false;
                cause = cause.getCause();
            }
            
            detailsText.append("</table></html>");
        } else {
            detailsText.append("<html>");
            
            boolean firstCause = true;
            Throwable cause = throwable;
            while (cause != null && cause.getMessage() != null) {
                if (firstCause) {
                    detailsText.append("<tr><td><b>Cause</b></td></tr>");
                }
                detailsText.append("<tr><td>").append(cause.getMessage()).append("</td></tr>");
                firstCause = false;
                cause = cause.getCause();
            }
            detailsText.append("</html>");
        }
        return detailsText.toString();
    }
    
    /**
     * Shows the error dialog and waits for the user to acknowledge the dialog.
     * <p>
     * If the JetS3t property <tt>gui.verboseErrorDialog</tt> is set to true, this dialog will 
     * display detailed information about the root cause of the error (the throwable, if provided)
     * </p>
     *
     * @param ownerFrame
     * the frame that will own the dialog
     * @param hyperlinkListener
     * the listener that will act on any hyperlink events triggered by the user clicking on HTTP links.
     * @param message
     * a general error message, which should ideally be somewhat user-friendly.
     * @param throwable
     * the underlying exception that caused the error. 
     */
    public static void showDialog(Frame ownerFrame, HyperlinkActivatedListener hyperlinkListener,
        String message, Throwable throwable) 
    {
        ErrorDialog dialog = new ErrorDialog(ownerFrame, hyperlinkListener);
        dialog.initGui(message, dialog.buildDetailedText(throwable));
        dialog.setVisible(true);
        dialog.dispose();
    }

    /**
     * Shows the error dialog and waits for the user to acknowledge the dialog.
     * <p>
     * If the JetS3t property <tt>gui.verboseErrorDialog</tt> is set to true, this dialog will 
     * display detailed information about the root cause of the error (the throwable, if provided)
     * </p>
     *
     * @param ownerDialog
     * the dialog that will own this dialog
     * @param hyperlinkListener
     * the listener that will act on any hyperlink events triggered by the user clicking on HTTP links.
     * @param message
     * a general error message, which should ideally be somewhat user-friendly.
     * @param throwable
     * the underlying exception that caused the error. 
     */
    public static void showDialog(JDialog ownerDialog, HyperlinkActivatedListener hyperlinkListener,
        String message, Throwable throwable) 
    {
        ErrorDialog dialog = new ErrorDialog(ownerDialog, hyperlinkListener);
        dialog.initGui(message, dialog.buildDetailedText(throwable));
        dialog.setVisible(true);
        dialog.dispose();
    }
    
}
