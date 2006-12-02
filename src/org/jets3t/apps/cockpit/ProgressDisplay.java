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

import java.awt.Cursor;
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
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.jets3t.service.multithread.CancelEventTrigger;

/**
 * Class that provides access to and controls a dialog that shows progress information
 * to the user for long-lived tasks.
 * <p>
 * This class wraps the dialog rather than making the dialog directly available in order 
 * to maintain more control over when and how the dialog is updated. 
 * 
 * @author James Murty
 */
public class ProgressDisplay {
    private final ProgressDialog progressDialog;
    
    /**
     * Constructor for creating a progress dialog.
     *  
     * @param owner         the Frame over which the progress dialog will be displayed and centred
     * @param title         the title for the progress dialog
     * @param statusText
     *        describes the status of a task text meaningful to the user, such as "3 files of 7 uploaded"
     * @param detailsText
     *        describes the status of a task in more detail, such as the current transfer rate and ETA.
     * @param minTaskValue  the minimum progress value for a task, generally 0
     * @param maxTaskValue  
     *        the maximum progress value for a task, such as the total number of threads or 100 if
     *        using percentage-complete as a metric.
     * @param cancelEventListener
     *        listener that is responsible for cancelling a long-lived task when the user clicks
     *        the cancel button. If a task cannot be cancelled this must be null.
     * @param cancelButtonText  
     *        text displayed in the cancel button if a task can be cancelled. This is only used if
     *        a cancel event listener is provided.
     */
    public ProgressDisplay(Frame owner, String title, String statusText, String detailsText, 
        int minTaskValue, int maxTaskValue, String cancelButtonText, CancelEventTrigger cancelEventListener) 
    {
        progressDialog = new ProgressDialog(owner, title, statusText, detailsText, 
            minTaskValue, maxTaskValue, cancelEventListener, cancelButtonText);
    }
    
    /**
     * Displays the progress dialog.
     */
    public synchronized void startDialog() {
        SwingUtilities.invokeLater(new Runnable(){
           public void run() {
               progressDialog.show();                           
           }
        });
    }
    
    /**
     * Updates the dialog's progress value, which is reflected in a percentage-complete
     * progress bar.
     * 
     * @param progressValue 
     *        value representing how far through the task we are, somewhere between 
     *        minTaskValue and maxTaskValue as set in the constructor.
     */
    public synchronized void updateProgress(final int progressValue) {
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                progressDialog.updateProgress(progressValue);
            }
        });
    }
    
    /**
     * Updates the dialog's status messages.
     *  
     * @param text
     *        describes the status of a task text meaningful to the user, such as "3 files of 7 uploaded"
     */
    public synchronized void updateStatusMessages(final String statusMessage, final String detailsText) {
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                progressDialog.updateStatusMessages(statusMessage, detailsText);
            }
        });
    }
    
    /**
     * @return true if the user cancelled the task (with the cancel button), false otherwise.
     */
    public boolean getCancelClicked() {
        return progressDialog.getCancelClicked();
    }
    
    /**
     * @return true if the dialog is active (currently displayed), false otherwise.
     */
    public boolean isActive() {
        return progressDialog.isActive();
    }
        
    public void dispose() {
        progressDialog.dispose();
    }
    
    /**
     * A modal dialog box to display progress information to the user when a long-lived task
     * is running. If the long-lived task can be cancelled by the user, this dialog will invoke
     * the {@link CancelEventTrigger} when the user clicks the cancel button.
     * 
     * @author James Murty
     */
    private class ProgressDialog extends JDialog implements ActionListener {
        private final Insets insetsDefault = new Insets(5, 7, 5, 7);

        private JLabel statusMessageLabel = null;
        private JLabel detailsTextLabel = null;
        private JProgressBar progressBar = null;
        private boolean wasCancelClicked = false;
        private CancelEventTrigger cancelEventListener = null;
        private int longestStatusTextLength = 0;
        private int longestDetailsTextLength = 0;

        /**
         * Constructs the progress dialog box.
         * 
         * @param owner         the Frame over which the progress dialog will be displayed and centred
         * @param title         the title for the progress dialog
         * @param statusMessage
         *        describes the status of a task text meaningful to the user, such as "3 files of 7 uploaded"
         * @param detailsText
         *        describes the status of a task in more detail, such as the current transfer rate and ETA.
         * @param minTaskValue  the minimum progress value for a task, generally 0
         * @param maxTaskValue  
         *        the maximum progress value for a task, such as the total number of threads or 100 if
         *        using percentage-complete as a metric.
         * @param cancelEventListener
         *        listener that is responsible for cancelling a long-lived task when the user clicks
         *        the cancel button. If a task cannot be cancelled this must be null.
         * @param cancelButtonText  
         *        text displayed in the cancel button if a task can be cancelled
         */
        public ProgressDialog(Frame owner, String title, String statusMessage, String detailsText, 
            int minTaskValue, int maxTaskValue, CancelEventTrigger cancelEventListener, String cancelButtonText) 
        {
            super(owner, title, true);
            this.cancelEventListener = cancelEventListener;
            initGui(statusMessage, detailsText, cancelButtonText, minTaskValue, maxTaskValue);
        }

        private void initGui(String statusMessage, String detailsText, String cancelButtonText, int min, int max) {
            this.setResizable(false);
            this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            
            JPanel container = new JPanel(new GridBagLayout());
            
            statusMessageLabel = new JLabel(statusMessage);
            statusMessageLabel.setHorizontalAlignment(JLabel.CENTER);
            container.add(statusMessageLabel, 
                new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            
            progressBar = new JProgressBar(min, max);
            progressBar.setStringPainted(true);

            if (max > min) {
                progressBar.setValue(min);
            } else {
                progressBar.setIndeterminate(true);
                progressBar.setString("");
            }
            
            container.add(progressBar, 
                new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));

            if (detailsText == null) {
                detailsText = "";
            }
            detailsTextLabel = new JLabel(detailsText);
            detailsTextLabel.setHorizontalAlignment(JLabel.CENTER);
            container.add(detailsTextLabel, 
                new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsDefault, 0, 0));
            
            // Display the cancel button if a cancel event listener is available.
            if (this.cancelEventListener != null) {
                JButton cancel = new JButton(cancelButtonText);
                cancel.setActionCommand("Cancel");
                cancel.addActionListener(this);
                cancel.setDefaultCapable(true);
                            
                container.add(cancel, 
                    new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsDefault, 0, 0));
            } else {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));                
            }

            this.getContentPane().add(container);
            this.pack();
            this.setLocationRelativeTo(this.getOwner());
        }
        
        public void actionPerformed(ActionEvent e) {
            if ("Cancel".equals(e.getActionCommand())) {
                wasCancelClicked = true;
                if (cancelEventListener != null) {
                    cancelEventListener.cancelTask(this);
                }
                this.dispose();
            }
        }
        
        public void dispose() {
            // Progress bar must be set to it's maximum value or it won't clean itself up properly.
            progressBar.setIndeterminate(false);
            progressBar.setValue(progressBar.getMaximum());
            super.dispose();
        }
        
        public void updateStatusMessages(String statusMessage, String detailsText) {
            if (statusMessageLabel.getText().equals(statusMessage)) {
                // Nothing to do.
                return;
            }
            
            statusMessageLabel.setText(statusMessage);
            if (detailsText != null) {
                detailsTextLabel.setText(detailsText);
            }
            
            boolean repack = false;
            if (statusMessage.length() > longestStatusTextLength) {
                longestStatusTextLength = statusMessage.length();
                repack = true;
            }
            if (detailsText != null && detailsText.length() > longestDetailsTextLength) {
                longestDetailsTextLength = detailsText.length();
                repack = true;
            }            
            if (repack) {
                progressDialog.pack();
                progressDialog.setLocationRelativeTo(this.getOwner());
            }

        }
        
        public void updateProgress(int progressValue) {
            progressBar.setValue(progressValue);
        }
        
        public boolean getCancelClicked() {
            return wasCancelClicked;
        }        
    }
    
}
