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
package org.jets3t.service.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.Constants;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.executor.GetObjectHeadsEvent;
import org.jets3t.service.executor.S3ServiceEventAdaptor;
import org.jets3t.service.executor.S3ServiceExecutor;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;

public class FileComparer {
    private static final Log log = LogFactory.getLog(FileComparer.class);

    public static String toHex(byte[] data) {
        StringBuffer sb = new StringBuffer(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i]);
            if (hex.length() == 1) {
                // Append leading zero.
                sb.append("0");
            } else if (hex.length() == 8) {
                // Remove ff prefix from negative numbers.
                hex = hex.substring(6);
            }
            sb.append(hex);
        }
        return sb.toString().toLowerCase();
    }

    /**
     * Computes the MD5 hash of the data in the given input stream and returns it as a hex string.
     * 
     * @param is
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static String computeMD5Hash(InputStream is) throws NoSuchAlgorithmException, IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[16384];
            int bytesRead = -1;
            while ((bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
                messageDigest.update(buffer, 0, bytesRead);
            }
            byte[] digest = messageDigest.digest();
            return toHex(digest);
        } finally {
            try {
                bis.close();
            } catch (Exception e) {
                System.err.println("Unable to close input stream of hash candidate: " + e);
            }
        }
    }

    /**
     * Computes the MD5 hash of the given data and returns it as a hex string.
     * 
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static String computeMD5Hash(byte[] data) throws NoSuchAlgorithmException, IOException {
        return computeMD5Hash(new ByteArrayInputStream(data));
    }

    /**
     * Builds a File Map containing all the files and directories inside the given root directory,
     * where the map's key for each file is the relative path to the file.
     * 
     * File keys are delimited with '/' characters.
     * 
     * @see buildDiscrepancyLists
     * @see buildS3ObjectMap
     * 
     * @param rootDirectory
     * The root directory containing the files/directories of interest. The root directory is <b>not</b>
     * included in the result map.
     * @param fileKeyPrefix
     * A prefix added to each file path key in the map, e.g. the name of the root directory the
     * files belong to. If provided, a '/' suffix is always added to the end of the prefix. If null
     * or empty, no prefix is used.
     * 
     * @return A Map of file path keys to File objects.
     */
    public static Map buildFileMap(File rootDirectory, String fileKeyPrefix) {
        if (fileKeyPrefix == null || fileKeyPrefix.length() == 0) {
            fileKeyPrefix = "";
        } else {
            if (!fileKeyPrefix.endsWith(Constants.FILE_PATH_DELIM)) {
                fileKeyPrefix += Constants.FILE_PATH_DELIM;
            }
        }

        HashMap fileMap = new HashMap();
        buildFileMapImpl(rootDirectory, fileKeyPrefix, fileMap);
        return fileMap;
    }
    
    /**
     * Recursive function to build a file map.
     * 
     * @param dir
     * @param currentPath
     * @param fileMap
     */
    protected static void buildFileMapImpl(File dir, String currentPath, Map fileMap) {
        File children[] = dir.listFiles();
        for (int i = 0; i < children.length; i++) {
            fileMap.put(currentPath + children[i].getName(), children[i]);
            if (children[i].isDirectory()) {
                buildFileMapImpl(children[i], currentPath + children[i].getName() + "/", fileMap);
            } else {
                fileMap.put(currentPath + children[i].getName(), children[i]);
            }
        }
    }

    /**
     * Builds an S3 Object Map containing all the objects within the given target path,
     * where the map's key for each object is the relative path to the object.
     * 
     * @see buildDiscrepancyLists
     * @see buildFileMap
     * 
     * @param s3Service
     * @param bucket
     * @param targetPath
     * @return
     * @throws S3ServiceException
     */
    public static Map buildS3ObjectMap(S3Service s3Service, S3Bucket bucket, String targetPath)
        throws S3ServiceException
    {
        String prefix = (targetPath.length() > 0 ? targetPath : null);
        S3Object[] s3ObjectsIncomplete = s3Service.listObjects(bucket, prefix, null);

        // Retrieve the complete information about all objects listed via GetObjectsHeads.
        final ArrayList s3ObjectsCompleteList = new ArrayList(s3ObjectsIncomplete.length);
        final S3ServiceException s3ServiceExceptions[] = new S3ServiceException[1];
        S3ServiceExecutor executor = new S3ServiceExecutor(s3Service, new S3ServiceEventAdaptor() {
            public void s3ServiceEventPerformed(GetObjectHeadsEvent event) {
                if (GetObjectHeadsEvent.EVENT_IN_PROGRESS == event.getEventStatus()) {
                    S3Object[] finishedObjects = event.getObjects();
                    if (finishedObjects.length > 0) {
                        s3ObjectsCompleteList.addAll(Arrays.asList(finishedObjects));
                    }
                } else if (GetObjectHeadsEvent.EVENT_ERROR == event.getEventStatus()) {
                    s3ServiceExceptions[0] = new S3ServiceException(
                        "Failed to retrieve detailed information about all S3 objects", 
                        event.getErrorCause());                    
                }
            }
        });
        executor.getObjectsHeads(bucket, s3ObjectsIncomplete);
        if (s3ServiceExceptions[0] != null) {
            throw s3ServiceExceptions[0];
        }        
        S3Object[] s3Objects = (S3Object[]) s3ObjectsCompleteList.toArray(new S3Object[] {});
        
        return populateS3ObjectMap(targetPath, s3Objects);
    }

    public static Map populateS3ObjectMap(String targetPath, S3Object[] s3Objects) {
        HashMap map = new HashMap();
        for (int i = 0; i < s3Objects.length; i++) {
            String relativeKey = s3Objects[i].getKey();
            if (targetPath.length() > 0) {
                relativeKey = relativeKey.substring(targetPath.length());
                int slashIndex = relativeKey.indexOf(Constants.FILE_PATH_DELIM);
                if (slashIndex >= 0) {
                    relativeKey = relativeKey.substring(slashIndex + 1, relativeKey.length());
                } else {
                    // This relative key is part of a prefix search, the key does not point to a
                    // real S3 object.
                    relativeKey = "";
                }
            }
            if (relativeKey.length() > 0) {
                map.put(relativeKey, s3Objects[i]);
            }
        }
        return map;
    }

    public static FileComparerResults buildDiscrepancyLists(Map filesMap, Map s3ObjectsMap)
        throws NoSuchAlgorithmException, FileNotFoundException, IOException, ParseException
    {
        List onlyOnServerKeys = new ArrayList();
        List updatedOnServerKeys = new ArrayList();
        List updatedOnClientKeys = new ArrayList();
        List alreadySynchronisedKeys = new ArrayList();
        List onlyOnClientKeys = new ArrayList();

        // Check files on server against local client files.
        Iterator s3ObjectsMapIter = s3ObjectsMap.keySet().iterator();
        while (s3ObjectsMapIter.hasNext()) {
            String keyPath = (String) s3ObjectsMapIter.next();
            S3Object s3Object = (S3Object) s3ObjectsMap.get(keyPath);

            // Check whether local file is already on server
            if (filesMap.containsKey(keyPath)) {
                // File has been backed up in the past, is it still up-to-date?
                File file = (File) filesMap.get(keyPath);

                if (file.isDirectory()) {
                    // We don't care about directory date changes, as long as it's present.
                    alreadySynchronisedKeys.add(keyPath);
                } else {
                    // Compare file hashes.
                    String fileHash = computeMD5Hash(new FileInputStream(file));
                    String objectHash = s3Object.getMd5Hash();
                    String objectETag = s3Object.getETag();
                    if (objectHash == null) {
                        log.warn("Using S3 service's ETag as MD5 hash as the S3 object is missing "
                           + "the jetS3T-preferred metadata item " + S3Object.METADATA_HEADER_HASH_MD5);
                        objectHash = objectETag;
                    }
                    
                    if (fileHash.equals(objectHash) || fileHash.equals(objectETag)) {
                        // Hashes match so file is already synchronised.
                        alreadySynchronisedKeys.add(keyPath);
                    } else {
                        // File is out-of-synch. Check which version has the latest date.
                        Date s3ObjectLastModified = null;
                        String metadataLocalFileDate = (String) s3Object.getMetadata().get(
                            Constants.METADATA_JETS3T_LOCAL_FILE_DATE);
                        if (metadataLocalFileDate == null) {
                            // This is risky as local file times and S3 times don't match!
                            log.warn("Using S3 last modified date as file date. This is not reliable " 
                                + "as the time according to S3 can differ from your local system time. "
                                + "Please use the metadata item " 
                                + Constants.METADATA_JETS3T_LOCAL_FILE_DATE);
                            s3ObjectLastModified = s3Object.getLastModifiedDate();
                        } else {
                            s3ObjectLastModified = ServiceUtils
                                .parseIso8601Date(metadataLocalFileDate);
                        }
                        if (s3ObjectLastModified.getTime() > file.lastModified()) {
                            updatedOnServerKeys.add(keyPath);
                        } else if (s3ObjectLastModified.getTime() < file.lastModified()) {
                            updatedOnClientKeys.add(keyPath);
                        } else {
                            // Dates match exactly but the hash doesn't. Shouldn't ever happen!
                            throw new IOException("Backed-up S3Object " + s3Object.getKey()
                                + " and local file " + file.getName()
                                + " have the same date but different hash values. "
                                + "This shouldn't happen!");
                        }
                    }
                }
            } else {
                // File is not in local file system, so it's only on the S3
                // server.
                onlyOnServerKeys.add(keyPath);
            }
        }

        // Any local files not already put into another list only exist locally.
        onlyOnClientKeys.addAll(filesMap.keySet());
        onlyOnClientKeys.removeAll(updatedOnClientKeys);
        onlyOnClientKeys.removeAll(alreadySynchronisedKeys);
        onlyOnClientKeys.removeAll(updatedOnServerKeys);

        return new FileComparerResults(onlyOnServerKeys, updatedOnServerKeys, updatedOnClientKeys,
            onlyOnClientKeys, alreadySynchronisedKeys);
    }

}
