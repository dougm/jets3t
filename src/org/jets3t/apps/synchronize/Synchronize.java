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
package org.jets3t.apps.synchronize;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jets3t.service.Constants;
import org.jets3t.service.S3Service;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.io.GZipDeflatingInputStream;
import org.jets3t.service.io.GZipInflatingOutputStream;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.multithread.S3ObjectAndOutputStream;
import org.jets3t.service.multithread.S3ServiceEventAdaptor;
import org.jets3t.service.multithread.S3ServiceMulti;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.security.EncryptionUtil;
import org.jets3t.service.utils.FileComparer;
import org.jets3t.service.utils.FileComparerResults;
import org.jets3t.service.utils.Mimetypes;
import org.jets3t.service.utils.ServiceUtils;

/**
 * Console application to synchronize the local file system with Amazon S3.
 * <p>
 * Files are copied to S3 with an Upload operation, and copied from S3 with a Download.
 * Files are transferred only if they have changed on the server- or client-side.
 * <p>
 * Synchronize usage/help information is as follows:
 * <p>
 * <code>
 * Usage: Synchronize [options] UPLOAD &lt;Directory> &lt;S3Path> &lt;Properties File><br>
 *    or: Synchronize [options] DOWNLOAD &lt;Directory> &lt;S3Path> &lt;Properties File><br>
 * <br>
 * UPLOAD  : Synchronize the contents of the Local Directory with S3.<br>
 * DOWNLOAD : Synchronize the contents of S3 with the Local Directory<br>
 * S3Path  : A path to the resource in S3. This must include at least the<br>
 *           bucket name, but may also specify a path inside the bucket.<br>
 *           E.g. <bucketName>/Backups/Documents/20060623<br>
 * Directory : A directory on your computer<br>
 * Properties File : A properties file containing the following properties:<br>
 *           accesskey : Your AWS Access Key<br>
 *           secretkey : Your AWS Secret Key<br>
 *           password  : Encryption password (only required when using crypto)<br>
 * <br>
 * For more help : Synchronize --help
 * <br>
 * Options<br>
 * -------<br>
 * -h | --help<br>
 *    Displays this help message.<br>
 * <br>
 * -n | --noaction<br>
 *    No action taken. No files will be changed locally or on S3, instead<br>
 *    a report will be generating showing what will happen if the command<br>
 *    is run without the -n option.<br>
 * <br>
 * -q | --quiet<br>
 *    Runs quietly and does not report on each action performed. The action<br>
 *    summary is still displayed.<br>
 * <br>
 * -f | --force<br>
 *    Force tool to perform synchronization even when files are up-to-date.<br>
 *    This may be useful if you need to update metadata or timestamps in S3.<br>
 * <br>
 * -k | --keepold<br>
 *    Keep outdated files instead of replacing/removing them. This option<br>
 *    will prevent already backed-up files from being reverted or deleted.<br>
 * <br>
 * -g | --gzip<br>
 *    Compress (GZip) files when backing up and Decompress gzipped files<br>
 *    when restoring.<br>
 * <br>
 * -c | --crypto<br>
 *    Encrypt files when backing up and decrypt encrypted files when restoring. If<br>
 *    this option is specified the properties must contain a password.<br>
 * <br>
 * Report<br>
 * ------<br>
 * Report items are printed on a single line with an action flag followed by<br>
 * the relative path of the file or S3 object. The flag meanings are...<br>
 * N: A new file/object will be created<br>
 * U: An existing file/object has changed and will be updated<br>
 * D: A file/object existing on the target does not exist on the source and<br>
 *    will be deleted.<br>
 * d: A file/object existing on the target does not exist on the source but<br>
 *    because the --keepold option was set it was not deleted.<br>
 * R: An existing file/object has changed more recently on the target than on the<br>
 *    source. The target version will be reverted to the older source version<br>
 * r: An existing file/object has changed more recently on the target than on the<br>
 *    source but because the --keepold option was set it was not reverted.<br>
 * -: The file identical locally and in S3, no action is necessary.<br>
 * F: A file identical locally and in S3 was updated due to the Force option.<br>
 * </code>
 * <p>
 * This application should be useful in its own right, but is also intended to  
 * serve as an example of using the jets3t {@link S3Service} single-threaded interface.
 * 
 * @author James Murty
 */
public class Synchronize {
    private S3Service s3Service = null;
    private EncryptionUtil encryptionPasswordUtil = null;
    
    private boolean doAction = false; // Files will only be transferred if true. 
    private boolean isQuiet = false; // Report will only include summary of actions if true.
    private boolean isForce = false; // Files will be overwritten when unchanged if true.
    private boolean isKeepOld = false; // Obsolete files will not be deleted if true.
    private boolean isGzipEnabled = false; // Files will be gzipped prior to upload if true.
    private boolean isEncryptionEnabled = false; // Files will be encrypted prior to upload if true.
    
    /**
     * Constructs the application with a pre-initialised S3Service and the user-specified options.
     * 
     * @param s3Service     a pre-initialised S3Service (including AWS Authorization credentials)
     * @param doAction      
     * @param isQuiet       
     * @param isForce       
     * @param isKeepOld     
     * @param isGzipEnabled 
     * @param isEncryptionEnabled   
     */
    public Synchronize(S3Service s3Service, boolean doAction, boolean isQuiet, boolean isForce, 
        boolean isKeepOld, boolean isGzipEnabled, boolean isEncryptionEnabled) 
    {
        this.s3Service = s3Service;
        this.doAction = doAction;
        this.isQuiet = isQuiet;
        this.isForce = isForce;
        this.isKeepOld = isKeepOld;
        this.isGzipEnabled = isGzipEnabled;
        this.isEncryptionEnabled = isEncryptionEnabled;
    }
    
    /**
     * Prepares a file prior to upload by encrypting and/or gzipping it according to the
     * options specified by the user.
     * 
     * @param originalFile  the file to prepare for upload.
     * @param newObject     the object that will be created in S3 to store the file.
     * @return  the original file if no encryption/gzipping options are set, otherwise a 
     *          temporary file with encrypted and/or gzipped data from the original file. 
     * @throws Exception    exceptions could include IO failures, gzipping and encryption failures.
     */
    private File prepareUploadFile(final File originalFile, final S3Object newObject) throws Exception {
        if (!isGzipEnabled && !isEncryptionEnabled) {
            // No file pre-processing required.
            return originalFile;
        }
        
        // Create a temporary file to hold data transformed from the original file. 
        final File tempUploadFile = File.createTempFile("jets3t-Synchronize",".tmp");        
        tempUploadFile.deleteOnExit();
        
        // Transform data from original file, gzipping or encrypting as specified in user's options.
        InputStream inputStream = new BufferedInputStream(new FileInputStream(originalFile));       
        String contentEncoding = null;        
        if (isGzipEnabled) {
            inputStream = new GZipDeflatingInputStream(inputStream);
            contentEncoding = "gzip";
            newObject.addMetadata(Constants.METADATA_JETS3T_COMPRESSED, "gzip"); 
        } 
        if (isEncryptionEnabled) {
            inputStream = encryptionPasswordUtil.encrypt(inputStream);
            contentEncoding = null;
            newObject.setContentType(Mimetypes.MIMETYPE_OCTET_STREAM);
            newObject.addMetadata(Constants.METADATA_JETS3T_ENCRYPTED, 
                encryptionPasswordUtil.getAlgorithm()); 
        }
        if (contentEncoding != null) {
            newObject.addMetadata("Content-Encoding", contentEncoding);
        }

        // Write transformed data to temporary file.
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tempUploadFile));
        byte[] buffer = new byte[8192];
        int c = -1;
        while ((c = inputStream.read(buffer)) >= 0) {
            outputStream.write(buffer, 0, c);
        }
        inputStream.close();
        outputStream.close();
        
        return tempUploadFile;
    }

    /**
     * Prepares a file to be uploaded to S3, creating an S3Object with the 
     * appropriate key and with some jets3t-specific metadata items set.
     * 
     * @param bucket    the bucket to create the object in 
     * @param targetKey the key name for the object
     * @param file      the file to upload to S3
     * @throws Exception
     */
    private S3Object prepareUploadObject(String targetKey, File file) throws Exception {
        S3Object newObject = new S3Object();
        newObject.setKey(targetKey);
        newObject.addMetadata(Constants.METADATA_JETS3T_LOCAL_FILE_DATE, 
            ServiceUtils.formatIso8601Date(new Date(file.lastModified())));

        if (file.isDirectory()) {
            newObject.setContentLength(0);
            newObject.setContentType(Mimetypes.MIMETYPE_JETS3T_DIRECTORY);
        } else {
            newObject.setContentType(Mimetypes.getMimetype(file));

            // Compute the file's MD5 hash.
            newObject.setMd5Hash(FileComparer.computeMD5Hash(
                new FileInputStream(file)));   
            
            File uploadFile = prepareUploadFile(file, newObject);
            
            newObject.setContentLength(uploadFile.length());
            newObject.setDataInputStream(new FileInputStream(uploadFile));
            
            // TODO Delete temporary files created by this program, to free disk space ASAP.
        }
        return newObject;
    }
    
    /**
     * Downloads a file from S3, transferring an object's data to the local file.
     * 
     * @param targetKey the key name for the object
     * @param file      the file to upload to S3
     * 
     * @param bucket    the bucket to create the object in 
     * @param object    the object to download
     * @param fileTarget    the file to save the S3 object to (which may also become a directory) 
     * @throws Exception
     */
    private S3ObjectAndOutputStream prepareObjectForDownload(S3Object object, File fileTarget) 
        throws Exception 
    {
        if (!doAction) {
            return null;
        }
        
        if (Mimetypes.MIMETYPE_JETS3T_DIRECTORY.equals(object.getContentType())) {
            fileTarget.mkdir();
            return null;
        } else {
            if (fileTarget.getParentFile() != null) {
                fileTarget.getParentFile().mkdirs();
            }            
            
            OutputStream outputStream = new FileOutputStream(fileTarget);
                        
            if (isGzipEnabled && 
                ("gzip".equalsIgnoreCase(object.getContentEncoding())
                || null != object.getMetadata().get(Constants.METADATA_JETS3T_COMPRESSED)))
            {
                // Automatically inflate gzipped data.
                outputStream = new GZipInflatingOutputStream(outputStream);
            }
            if (isEncryptionEnabled 
                && object.getMetadata().get(Constants.METADATA_JETS3T_ENCRYPTED) != null)
            {
                // Automatically decrypt encrypted files.
                outputStream = encryptionPasswordUtil.decrypt(outputStream);                    
            }
            
            return new S3ObjectAndOutputStream(object, outputStream);                        
        }        
    }
    
    /**
     * Prints text to StdOut provided the isQuiet flag is not set.
     * 
     * @param line the text to print
     */
    private void printLine(String line) {
        if (!isQuiet) {
            System.out.println(line);
        }
    }
    
    /**
     * Copies the contents of a local directory to S3, storing them in the given root path.
     * <p>
     * A set of comparisons is used to determine exactly how the local files differ from the
     * contents of the S3 location, and files are transferred based on these comparisons and
     * options set by the user.
     * <p>
     * The following S3 Object properties are set when a file is uploaded:
     * <ul>
     * <li>The object's key name</li>
     * <li>Content-Length: The size of the uploaded file. This will be 0 for directories, and will
     *     differ from the original file if gzip or encryption options are set.</li>
     * <li>Content-Type: {@link Mimetypes#MIMETYPE_JETS3T_DIRECTORY} for directories, otherwise a
     *     mimetype determined by {@link Mimetypes#getMimetype} <b>unless</b> the gzip option is
     *     set, in which case the Content-Type is set to application/x-gzip.
     * </ul>
     * <p>
     * The following jets3t-specific metadata items are also set:
     * <ul>
     * <li>The local file's last-modified date, as {@link Constants#METADATA_JETS3T_LOCAL_FILE_DATE}</li>
     * <li>An MD5 hash of file data, as {@link S3Object#METADATA_HEADER_HASH_MD5}</li>
     * </ul>
     * 
     * @param disrepancyResults a set of comparisons of the local file system compared with S3
     * @param filesMap      a map of the local <code>File</code>s with '/'-delimited file paths as keys 
     * @param s3ObjectsMap  a map of <code>S3Object</code>s corresponding to local files with 
     *                      '/'-delimited file paths as keys
     * @param bucket        the bucket to put the objects in (will be created if necessary)
     * @param rootObjectPath    the root path where objects are put (will be created if necessary)
     * @throws Exception
     */
    public void uploadLocalDirectoryToS3(FileComparerResults disrepancyResults, Map filesMap, 
        Map s3ObjectsMap, S3Bucket bucket, String rootObjectPath) throws Exception 
    {        
        List objectsToUpload = new ArrayList();
        
        // Sort upload file candidates by path.
        ArrayList sortedFilesKeys = new ArrayList(filesMap.keySet());
        Collections.sort(sortedFilesKeys);
        
        // Iterate through local files and perform the necessary action to synchronise them with S3.
        Iterator fileKeyIter = sortedFilesKeys.iterator();
        while (fileKeyIter.hasNext()) {
            String keyPath = (String) fileKeyIter.next();
            File file = (File) filesMap.get(keyPath);
            
            String targetKey = keyPath;
            if (rootObjectPath.length() > 0) {
                targetKey = rootObjectPath + Constants.FILE_PATH_DELIM + targetKey; 
            }

            if (disrepancyResults.onlyOnClientKeys.contains(keyPath)) {
                printLine("N " + keyPath);
                objectsToUpload.add(prepareUploadObject(targetKey, file));
            } else if (disrepancyResults.updatedOnClientKeys.contains(keyPath)) {
                printLine("U " + keyPath);
                objectsToUpload.add(prepareUploadObject(targetKey, file));
            } else if (disrepancyResults.alreadySynchronisedKeys.contains(keyPath)) {
                if (isForce) {
                    printLine("F " + keyPath);
                    objectsToUpload.add(prepareUploadObject(targetKey, file));
                } else {
                    printLine("- " + keyPath);
                }
            } else if (disrepancyResults.updatedOnServerKeys.contains(keyPath)) {
                // This file has been updated on the server-side.
                if (isKeepOld) {
                    printLine("r " + keyPath);                    
                } else {
                    printLine("R " + keyPath);
                    objectsToUpload.add(prepareUploadObject(targetKey, file));
                }
            } else {
                // Uh oh, program error here. The safest thing to do is abort!
                throw new SynchronizeException("Invalid discrepancy comparison details for file " 
                    + file.getPath() 
                    + ". Sorry, this is a program error - aborting to keep your data safe");
            }
        }
        
        // Upload New/Updated/Forced/Replaced objects to S3.
        if (doAction && objectsToUpload.size() > 0) {
            S3Object[] objects = (S3Object[]) objectsToUpload.toArray(new S3Object[] {});
            S3ServiceEventAdaptor adaptor = new S3ServiceEventAdaptor();
            (new S3ServiceMulti(s3Service, adaptor)).putObjects(bucket, objects);
            if (adaptor.wasErrorThrown()) {
                if (adaptor.getErrorThrown() instanceof Exception) {
                    throw (Exception) adaptor.getErrorThrown();
                } else {
                    throw new Exception(adaptor.getErrorThrown());
                }
            }
        }
        
        // Delete objects on S3 that don't correspond with local files.
        Iterator serverOnlyIter = disrepancyResults.onlyOnServerKeys.iterator();
        while (serverOnlyIter.hasNext()) {
            String keyPath = (String) serverOnlyIter.next();
            S3Object s3Object = (S3Object) s3ObjectsMap.get(keyPath);

            if (isKeepOld) {
                printLine("d " + keyPath);                
            } else {
                printLine("D " + keyPath);
                if (doAction) {
                    s3Service.deleteObject(bucket, s3Object.getKey());
                }
            }
        }
        
        System.out.println(
            "New files: " + disrepancyResults.onlyOnClientKeys.size() +
            ", Updated: " + disrepancyResults.updatedOnClientKeys.size() +
            (isKeepOld?
                ", Kept though old: " + 
                (disrepancyResults.updatedOnServerKeys.size() + disrepancyResults.onlyOnServerKeys.size())                    
                :                 
                ", Reverted: " + disrepancyResults.updatedOnServerKeys.size() +
                ", Deleted: " + disrepancyResults.onlyOnServerKeys.size()
                ) +
            (isForce ?
                ", Forced updates: " + disrepancyResults.alreadySynchronisedKeys.size() :
                ", Unchanged: " + disrepancyResults.alreadySynchronisedKeys.size()
                )        
            );
    }
        
    /**
     * Copies the contents of a root path in S3 to the local file system.
     * <p>
     * A set of comparisons is used to determine exactly how the S3 objects/files differ from the
     * local target, and files are transferred based on these comparisons and options set by the user.
     * <p>
     * If an object is gzipped (according to its Content-Type) and the gzip option is set, the object
     * is inflated. If an object is encrypted (according to the metadata item 
     * {@link Constants#METADATA_JETS3T_ENCRYPTED}) and the crypt option is set, the object is decrypted.
     * If encrypted and/or gzipped objects are restored without the corresponding option being set, the
     * user will be responsible for inflating or decrypting the data.
     * <p>
     * <b>Note</b>: If a file was backed-up with both encryption and gzip options it cannot be 
     * restored with only the gzip option set, as files are gzipped prior to being encrypted and cannot
     * be inflated without first being decrypted.
     * 
     * @param disrepancyResults a set of comparisons of the local file system compared with S3
     * @param filesMap      a map of the local <code>File</code>s with '/'-delimited file paths as keys 
     * @param s3ObjectsMap  a map of <code>S3Object</code>s corresponding to local files with 
     *                      '/'-delimited file paths as keys
     * @param rootObjectPath    the root path in S3 where backed-up objects were stored
     * @param bucket        the bucket into which files were backed up
     * @throws Exception
     */
    public void restoreFromS3ToLocalDirectory(FileComparerResults disrepancyResults, Map filesMap, 
        Map s3ObjectsMap, String rootObjectPath, File localDirectory, S3Bucket bucket) throws Exception 
    {
        List itemsToDownload = new ArrayList();
        
        ArrayList sortedS3ObjectKeys = new ArrayList(s3ObjectsMap.keySet());
        Collections.sort(sortedS3ObjectKeys);
        
        // Upload/update files.
        Iterator s3KeyIter = sortedS3ObjectKeys.iterator();
        while (s3KeyIter.hasNext()) {
            String keyPath = (String) s3KeyIter.next();
            S3Object s3Object = (S3Object) s3ObjectsMap.get(keyPath);
            
            if (disrepancyResults.onlyOnServerKeys.contains(keyPath)) {
                printLine("N " + keyPath);
                S3ObjectAndOutputStream item = prepareObjectForDownload(
                    s3Object, new File(localDirectory, keyPath));
                if (item != null) {
                    itemsToDownload.add(item);
                }
            } else if (disrepancyResults.updatedOnServerKeys.contains(keyPath)) {
                printLine("U " + keyPath);
                S3ObjectAndOutputStream item = prepareObjectForDownload(
                    s3Object, new File(localDirectory, keyPath));
                if (item != null) {
                    itemsToDownload.add(item);
                }
            } else if (disrepancyResults.alreadySynchronisedKeys.contains(keyPath)) {
                if (isForce) {
                    printLine("F " + keyPath);
                    S3ObjectAndOutputStream item = prepareObjectForDownload(
                        s3Object, new File(localDirectory, keyPath));
                    if (item != null) {
                        itemsToDownload.add(item);
                    }
                } else {
                    printLine("- " + keyPath);
                }
            } else if (disrepancyResults.updatedOnClientKeys.contains(keyPath)) {
                // This file has been updated on the client-side.
                if (isKeepOld) {
                    printLine("r " + keyPath);                    
                } else {
                    printLine("R " + keyPath);
                    S3ObjectAndOutputStream item = prepareObjectForDownload(
                        s3Object, new File(localDirectory, keyPath));
                    if (item != null) {
                        itemsToDownload.add(item);
                    }
                }
            } else {
                // Uh oh, program error here. The safest thing to do is abort!
                throw new SynchronizeException("Invalid discrepancy comparison details for S3 object " 
                    + keyPath
                    + ". Sorry, this is a program error - aborting to keep your data safe");
            }
        }
        
        // Download New/Updated/Forced/Replaced objects from S3.
        if (doAction && itemsToDownload.size() > 0) {
            S3ObjectAndOutputStream[] items = (S3ObjectAndOutputStream[]) 
                itemsToDownload.toArray(new S3ObjectAndOutputStream[] {});
            S3ServiceEventAdaptor adaptor = new S3ServiceEventAdaptor();
            (new S3ServiceMulti(s3Service, adaptor)).downloadObjects(bucket, items);
            if (adaptor.wasErrorThrown()) {
                if (adaptor.getErrorThrown() instanceof Exception) {
                    throw (Exception) adaptor.getErrorThrown();
                } else {
                    throw new Exception(adaptor.getErrorThrown());
                }
            }
        }

        // Delete local files that don't correspond with S3 objects.
        ArrayList dirsToDelete = new ArrayList();
        Iterator clientOnlyIter = disrepancyResults.onlyOnClientKeys.iterator();
        while (clientOnlyIter.hasNext()) {
            String keyPath = (String) clientOnlyIter.next();
            File file = (File) filesMap.get(keyPath);
            
            if (isKeepOld) {
                printLine("d " + keyPath);                
            } else {
                printLine("D " + keyPath);
                if (doAction) {
                    if (file.isDirectory()) {
                        // Delete directories later, as they may still have files 
                        // inside until this loop completes.
                        dirsToDelete.add(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        Iterator dirIter = dirsToDelete.iterator();
        while (dirIter.hasNext()) {
            File dir = (File) dirIter.next();
            dir.delete();
        }
        
        System.out.println(
            "New files: " + disrepancyResults.onlyOnServerKeys.size() +
            ", Updated: " + disrepancyResults.updatedOnServerKeys.size() +
            (isKeepOld? 
                ", Kept though old: " + 
                (disrepancyResults.updatedOnClientKeys.size() + disrepancyResults.onlyOnClientKeys.size())                    
                : 
                ", Reverted: " + disrepancyResults.updatedOnClientKeys.size() +
                ", Deleted: " + disrepancyResults.onlyOnClientKeys.size()
                ) +
            (isForce ?
                ", Forced updates: " + disrepancyResults.alreadySynchronisedKeys.size() :
                ", Unchanged: " + disrepancyResults.alreadySynchronisedKeys.size()
                )        
            );
    }
    
    /**
     * Runs the application, performing the action specified on the given S3 and local directory paths.
     * 
     * @param s3Path    The path in S3 (including the bucket name) to which files are backed-up, or from
     *                  which files are restored.
     * @param localDirectory    A local directory where files are backed-up from, or restored to.
     * @param actionCommand     The action to perform, UPLOAD or DOWNLOAD
     * @param cryptoPassword      If non-null, an {@link EncryptionUtil} object is created with the provided
     *                      password to encrypt or decrypt files.
     * @throws Exception
     */
    public void run(String s3Path, File localDirectory, String actionCommand, String cryptoPassword) throws Exception 
    {
        String bucketName = null;
        String objectPath = "";        
        int slashIndex = s3Path.indexOf(Constants.FILE_PATH_DELIM);
        if (slashIndex >= 0) {
            // We have a bucket name and an object path. 
            bucketName = s3Path.substring(0, slashIndex);
            objectPath = s3Path.substring(slashIndex + 1, s3Path.length());
        } else {
            // We only have a bucket name.
            bucketName = s3Path;
        }
        
        // Describe the action that will be performed.
        if ("UPLOAD".equals(actionCommand)) {
            System.out.println("UPLOAD "
                + (doAction ? "" : "[No Action] ")
                + "Local(" + localDirectory + ") => S3(" + s3Path + ")");              
        } else if ("DOWNLOAD".equals(actionCommand)) {
            System.out.println("DOWNLOAD "
                + (doAction ? "" : "[No Action] ")
                + "S3(" + s3Path + ") => Local(" + localDirectory + ")");              
        } else {
            throw new SynchronizeException("Action string must be 'UPLOAD' or 'DOWNLOAD'");
        }        
        
        if (cryptoPassword != null) {
            encryptionPasswordUtil = new EncryptionUtil(cryptoPassword);
        } 
                
        S3Bucket bucket = null;
        try {
            // Create/connect to the S3 bucket.
            bucket = s3Service.createBucket(bucketName);
        } catch (Exception e) {
            throw new SynchronizeException("Unable to connect to S3 bucket: " + bucketName);
        }
        
        if (objectPath.length() > 0) {
            // Create the S3Path.
            try {
                String targetDirs[] = objectPath.split(Constants.FILE_PATH_DELIM);
                String currentDirPath = "";
                for (int i = 0; i < targetDirs.length; i++) {
                    currentDirPath += targetDirs[i];
                    
                    S3Object dirObject = new S3Object();
                    dirObject.setKey(currentDirPath);
                    dirObject.setContentType(Mimetypes.MIMETYPE_JETS3T_DIRECTORY);
                    s3Service.putObject(bucket, dirObject);
                    currentDirPath += Constants.FILE_PATH_DELIM;
                }
            } catch (Exception e) {
                throw new SynchronizeException("Unable to create S3 path: " + objectPath);
            }
        }
                
        // Compare contents of local directory with contents of S3 path and identify any disrepancies.
        Map filesMap = FileComparer.buildFileMap(localDirectory, null);

        S3ServiceEventAdaptor errorCatchingAdaptor = new S3ServiceEventAdaptor();
        Map s3ObjectsMap = FileComparer.buildS3ObjectMap(s3Service, bucket, objectPath, errorCatchingAdaptor);
        if (errorCatchingAdaptor.wasErrorThrown()) {
            throw new Exception("Unable to build map of S3 Objects", errorCatchingAdaptor.getErrorThrown());
        }
        
        FileComparerResults discrepancyResults = FileComparer.buildDiscrepancyLists(filesMap, s3ObjectsMap);

        // Perform the requested action on the set of disrepancies.
        if ("UPLOAD".equals(actionCommand)) {
            uploadLocalDirectoryToS3(discrepancyResults, filesMap, s3ObjectsMap, bucket, objectPath);
        } else if ("DOWNLOAD".equals(actionCommand)) {
            restoreFromS3ToLocalDirectory(discrepancyResults, filesMap, s3ObjectsMap, objectPath, localDirectory, bucket);
        }
    }

    /**
     * Prints usage/help information and forces the application to exit with errorcode 1. 
     */
    private static void printHelpAndExit(boolean fullHelp) {
        System.out.println();
        System.out.println("Usage: Synchronize [options] UPLOAD <Directory> <S3Path> <Properties File>");
        System.out.println("   or: Synchronize [options] DOWNLOAD <Directory> <S3Path> <Properties File>");
        System.out.println("");
        System.out.println("UPLOAD  : Synchronize the contents of the Local Directory with S3.");
        System.out.println("DOWNLOAD : Synchronize the contents of S3 with the Local Directory");
        System.out.println("S3Path  : A path to the resource in S3. This must include at least the");
        System.out.println("          bucket name, but may also specify a path inside the bucket.");
        System.out.println("          E.g. <bucketName>/Backups/Documents/20060623");
        System.out.println("Directory : A directory on your computer");
        System.out.println("Properties File : A properties file containing the following properties:");
        System.out.println("          accesskey : Your AWS Access Key");
        System.out.println("          secretkey : Your AWS Secret Key");
        System.out.println("          password  : Encryption password (only required when using crypto)");
        System.out.println("");
        System.out.println("For more help : Synchronize --help");
        if (!fullHelp)
            System.exit(1);        

        System.out.println("");
        System.out.println("Options");
        System.out.println("-------");
        System.out.println("-h | --help");
        System.out.println("   Displays this help message.");
        System.out.println("");
        System.out.println("-n | --noaction");
        System.out.println("   No action taken. No files will be changed locally or on S3, instead");
        System.out.println("   a report will be generating showing what will happen if the command");
        System.out.println("   is run without the -n option.");
        System.out.println("");
        System.out.println("-q | --quiet");
        System.out.println("   Runs quietly and does not report on each action performed. The action");
        System.out.println("   summary is still displayed.");
        System.out.println("");
        System.out.println("-f | --force");
        System.out.println("   Force tool to perform synchronization even when files are up-to-date.");
        System.out.println("   This may be useful if you need to update metadata or timestamps in S3.");
        System.out.println("");
        System.out.println("-k | --keepold");
        System.out.println("   Keep outdated files instead of replacing/removing them. This option");
        System.out.println("   will prevent already backed-up files from being reverted or deleted.");
        System.out.println("");
        System.out.println("-g | --gzip");
        System.out.println("   Compress (GZip) files when backing up and Decompress gzipped files");
        System.out.println("   when restoring.");
        System.out.println("");
        System.out.println("-c | --crypto");
        System.out.println("   Encrypt files when backing up and decrypt encrypted files when restoring. If");
        System.out.println("   this option is specified the properties must contain a password.");
        System.out.println("");
        System.out.println("Report");
        System.out.println("------");
        System.out.println("Report items are printed on a single line with an action flag followed by");
        System.out.println("the relative path of the file or S3 object. The flag meanings are...");
        System.out.println("N: A new file/object will be created");
        System.out.println("U: An existing file/object has changed and will be updated");
        System.out.println("D: A file/object existing on the target does not exist on the source and");
        System.out.println("   will be deleted.");
        System.out.println("d: A file/object existing on the target does not exist on the source but");
        System.out.println("   because the --keepold option was set it was not deleted.");
        System.out.println("R: An existing file/object has changed more recently on the target than on the");
        System.out.println("   source. The target version will be reverted to the older source version");
        System.out.println("r: An existing file/object has changed more recently on the target than on the");
        System.out.println("   source but because the --keepold option was set it was not reverted.");
        System.out.println("-: The file identical locally and in S3, no action is necessary.");
        System.out.println("F: A file identical locally and in S3 was updated due to the Force option.");
        System.out.println();
        System.exit(1);        
    }
    
    /**
     * Runs this application from the console, accepts and checks command-line parameters and runs an
     * upload or download operation when all the necessary parameters are provided.
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        // Required arguments
        String actionCommand = null;
        File localDirectory = null;
        String s3Path = null;
        File propertiesFile = null;
        int reqArgCount = 0;
        
        // Options
        boolean doAction = true;
        boolean isQuiet = false;
        boolean isForce = false;
        boolean isKeepOld = false;
        boolean isGzipEnabled = false;
        boolean isEncryptionEnabled = false;
        
        // Parse arguments.
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-")) {
                // Argument is an option.
                if (arg.equalsIgnoreCase("-h") || arg.equalsIgnoreCase("--help")) {
                    printHelpAndExit(true);
                } else if (arg.equalsIgnoreCase("-n") || arg.equalsIgnoreCase("--noaction")) {
                    doAction = false; 
                } else if (arg.equalsIgnoreCase("-q") || arg.equalsIgnoreCase("--quiet")) {
                    isQuiet = true; 
                } else if (arg.equalsIgnoreCase("-f") || arg.equalsIgnoreCase("--force")) {
                    isForce = true; 
                } else if (arg.equalsIgnoreCase("-k") || arg.equalsIgnoreCase("--keepold")) {
                    isKeepOld = true; 
                } else if (arg.equalsIgnoreCase("-g") || arg.equalsIgnoreCase("--gzip")) {
                    isGzipEnabled = true; 
                } else if (arg.equalsIgnoreCase("-c") || arg.equalsIgnoreCase("--crypto")) {
                    isEncryptionEnabled = true; 
                } else {
                    System.err.println("ERROR: Invalid option: " + arg);
                    printHelpAndExit(false);
                }
            } else {
                // Argument is one of the required parameters.
                if (reqArgCount == 0) {
                    actionCommand = arg.toUpperCase();
                    if (!"UPLOAD".equals(actionCommand) && !"DOWNLOAD".equals(actionCommand)) {
                        System.err.println("ERROR: Invalid action command " + actionCommand 
                            + ". Valid values are 'UPLOAD' or 'DOWNLOAD'");
                        printHelpAndExit(false);
                    }                    
                } else if (reqArgCount == 1) {
                    localDirectory = new File(arg);
                    if (!localDirectory.canRead() || !localDirectory.isDirectory()) {
                        localDirectory.mkdirs();
                    }
                } else if (reqArgCount == 2) {
                    s3Path = arg;
                } else if (reqArgCount == 3) {
                    propertiesFile = new File(arg);                    
                } else {
                    System.err.println("ERROR: Too many parameters");
                    printHelpAndExit(false);                    
                }
                reqArgCount++;
            }
        }
        
        if (reqArgCount < 4) {
            // Missing one or more required parameters.
            System.err.println("ERROR: Missing " + (4 - reqArgCount) + " required parameter(s)");
            printHelpAndExit(false);
        }
        
        // Read the Properties file, and make sure it contains everything we need.
        Properties properties = new Properties();
        properties.load(new FileInputStream(propertiesFile));
        if (!properties.containsKey("accesskey")) {
            System.err.println("ERROR: The properties file " + propertiesFile + " must contain the property: accesskey");
            System.exit(2);            
        } else if (!properties.containsKey("secretkey")) {
            System.err.println("ERROR: The properties file " + propertiesFile + " must contain the property: secretkey");
            System.exit(2);                        
        } else if (isEncryptionEnabled && !properties.containsKey("password")) {
            System.err.println("ERROR: You are using encryption, so the properties file " + propertiesFile + " must contain the property: password");
            System.exit(2);                        
        }
        
        // Load the AWS credentials from encrypted file.
        AWSCredentials awsCredentials = new AWSCredentials(
            properties.getProperty("accesskey"), properties.getProperty("secretkey"));        
         
        // Perform the UPLOAD/DOWNLOAD.
        Synchronize client = new Synchronize(
            new RestS3Service(awsCredentials),
            doAction, isQuiet, isForce, isKeepOld, isGzipEnabled, isEncryptionEnabled);
        client.run(s3Path, localDirectory, actionCommand, properties.getProperty("password"));
    }
        
}
