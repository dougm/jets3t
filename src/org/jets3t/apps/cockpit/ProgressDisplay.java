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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.jets3t.service.executor.CancelEventListener;

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
    public ProgressDisplay(Frame owner, String title, String statusText, int minTaskValue, 
        int maxTaskValue, String cancelButtonText, CancelEventListener cancelEventListener) 
    {
        progressDialog = new ProgressDialog(owner, title, statusText, minTaskValue, maxTaskValue, 
            cancelEventListener, cancelButtonText);
    }
    
    /**
     * Displays the progress dialog.
     * <p>
     * After the progress dialog has been started it <b>must</b> be stopped eventually
     * by calling {@link #haltDialog}.
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
     * Updates the dialog's status text.
     *  
     * @param text
     *        describes the status of a task text meaningful to the user, such as "3 files of 7 uploaded"
     */
    public synchronized void updateStatusText(final String text) {
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                progressDialog.updateStatusText(text);
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
    
    /**
     * Halts the progress dialog, returning control of the GUI to the user.
     * <p>
     * This method is also responsible for performing any dialog cleanup necessary.
     */
    public synchronized void haltDialog() {
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                if (progressDialog.isActive()) {
                    progressDialog.hide();
                }
                progressDialog.dispose();
            }
        });
    }
    
    /**
     * A modal dialog box to display progress information to the user when a long-lived task
     * is running. If the long-lived task can be cancelled by the user, this dialog will invoke
     * the {@link CancelEventListener} when the user clicks the cancel button.
     * 
     * @author James Murty
     */
    private class ProgressDialog extends JDialog implements ActionListener {
        private final Insets insetsDefault = new Insets(5, 7, 5, 7);

        private JLabel statusMessageLabel = null;
        private JProgressBar progressBar = null;
        private boolean wasCancelClicked = false;
        private CancelEventListener cancelEventListener = null;
        private int longestStatusTextLength = 0;

        /**
         * Constructs the progress dialog box.
         * 
         * @param owner         the Frame over which the progress dialog will be displayed and centred
         * @param title         the title for the progress dialog
         * @param statusMessage
         *        describes the status of a task text meaningful to the user, such as "3 files of 7 uploaded"
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
        public ProgressDialog(Frame owner, String title, String statusMessage, int minTaskValue, 
            int maxTaskValue, CancelEventListener cancelEventListener, String cancelButtonText) 
        {
            super(owner, title, true);
            this.cancelEventListener = cancelEventListener;
            initGui(statusMessage, cancelButtonText, minTaskValue, maxTaskValue);
        }

        private void initGui(String statusMessage, String cancelButtonText, int min, int max) {
            this.setResizable(false);
            this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            
            JPanel container = new JPanel(new GridBagLayout());
            
            statusMessageLabel = new JLabel(statusMessage);
            container.add(statusMessageLabel, 
                new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insetsDefault, 0, 0));
            
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

            // Display the cancel button if a cancel event listener is available.
            if (this.cancelEventListener != null) {
                JButton cancel = new JButton(cancelButtonText);
                cancel.setActionCommand("Cancel");
                cancel.addActionListener(this);
                cancel.setDefaultCapable(true);
                            
                container.add(cancel, 
                    new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, insetsDefault, 0, 0));
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
        
        public void updateStatusText(String text) {
            if (statusMessageLabel.getText().equals(text)) {
                // Nothing to do.
                return;
            }
            
            statusMessageLabel.setText(text);
            
            if (text.length() > longestStatusTextLength) {
                longestStatusTextLength = text.length();
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
    
        
    /**
     * Creates stand-alone dialog box for testing only.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {        
        JFrame f = new JFrame();
        f.setSize(new Dimension(800, 600));
        f.setVisible(true);
        
        int iterCount = 20; // How many iterations to run?
        int iterIndex = 0;
        final boolean[] notCancelled = new boolean[] { true };
        
        ProgressDisplay progressDialog = 
            new ProgressDisplay(f, "Testing", "Iteration " + iterIndex + " of " + iterCount, 0, iterCount,
            "Cancel test task", new CancelEventListener() {
                public void cancelTask(Object eventSource) {
                    System.out.println("User hit the cancel button!");
                    notCancelled[0] = false;
                }
            });
        progressDialog.startDialog();
        
        // Update status every 1 seconds.
        long startTime = System.currentTimeMillis();
        while (notCancelled[0] && iterIndex < iterCount) {
            System.out.println("Task running for " + (System.currentTimeMillis() - startTime) + " ms");
            
            Thread.sleep(1000);
            ++iterIndex;

            if (!progressDialog.getCancelClicked()) {
                progressDialog.updateStatusText("Iteration " + iterIndex + " of " + iterCount);
                progressDialog.updateProgress(iterIndex);
            }
        }
        
        System.out.println("Did the user cancel the dialog? " + progressDialog.getCancelClicked());
        
        progressDialog.haltDialog();
        
        f.dispose();
    }

}
