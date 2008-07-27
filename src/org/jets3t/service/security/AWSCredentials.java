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
package org.jets3t.service.security;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.Constants;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.utils.ServiceUtils;

/**
 * Class to contain the Amazon Web Services (AWS) credentials of a user. This class also includes
 * utility methods to store credentials to password-encrypted files, and retrieve credentials from
 * these files.
 * 
 * @author James Murty
 */
public class AWSCredentials implements Serializable {
    private static final long serialVersionUID = -9198887548912640773L;

    private static final Log log = LogFactory.getLog(AWSCredentials.class);    
    
    protected static final String KEYS_DELIMITER = "AWSKEYS";
    protected static final String VERSION_PREFIX = "jets3t AWS Credentials, version: ";

    private String awsAccessKey = null;
    private String awsSecretAccessKey = null;
    private String friendlyName = null;

    /**
     * Construct credentials.
     * 
     * @param awsAccessKey
     * AWS access key for an Amazon S3 account.
     * @param awsSecretAccessKey
     * AWS secret key for an Amazon S3 acount.
     */
    public AWSCredentials(String awsAccessKey, String awsSecretAccessKey) {
        this.awsAccessKey = awsAccessKey;
        this.awsSecretAccessKey = awsSecretAccessKey;
    }

    /**
     * Construct credentials, and associate them with a human-friendly name.
     * 
     * @param awsAccessKey
     * AWS access key for an Amazon S3 account.
     * @param awsSecretAccessKey
     * AWS secret key for an Amazon S3 acount.
     * @param friendlyName
     * a name identifying the owner of the credentials, such as 'James'.
     */
    public AWSCredentials(String awsAccessKey, String awsSecretAccessKey, String friendlyName) {
        this(awsAccessKey, awsSecretAccessKey);
        this.friendlyName = friendlyName;
    }

    /**
     * @return
     * the AWS Access Key.
     */
    public String getAccessKey() {
        return awsAccessKey;
    }

    /**
     * @return
     * the AWS Secret Key.
     */
    public String getSecretKey() {
        return awsSecretAccessKey;
    }

    /**
     * @return
     * the friendly name associated with an AWS account, if available. 
     */
    public String getFriendlyName() {
        return friendlyName;
    }
    
    /**
     * Encrypts AWS Credentials with the given password and saves the encrypted data to a file.
     * 
     * @param password
     * the password used to encrypt the credentials.
     * @param file
     * the file to write the encrypted credentials data to.
     * 
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     * @throws IllegalStateException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws IOException
     */
    public void save(String password, File file) throws InvalidKeyException,
        NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException,
        IllegalStateException, IllegalBlockSizeException, BadPaddingException,
        InvalidAlgorithmParameterException, IOException
    {
        FileOutputStream fos = new FileOutputStream(file);
        save(password, fos);
        fos.close();
    }
    
    /**
     * Encrypts AWS Credentials with the given password and writes the encrypted data to an
     * output stream.
     * 
     * @param password
     * the password used to encrypt the credentials.
     * @param outputStream
     * the output stream to write the encrypted credentials data to, this stream must be closed by
     * the caller.
     * 
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     * @throws IllegalStateException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws IOException
     */
    public void save(String password, OutputStream outputStream) throws InvalidKeyException,
        NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException,
        IllegalStateException, IllegalBlockSizeException, BadPaddingException,
        InvalidAlgorithmParameterException, IOException
    {
        BufferedOutputStream bufferedOS = null;
        EncryptionUtil encryptionUtil = new EncryptionUtil(password);
        bufferedOS = new BufferedOutputStream(outputStream);

        // Encrypt AWS credentials
        String dataToEncrypt = getAccessKey() + KEYS_DELIMITER + getSecretKey();
        byte[] encryptedData = encryptionUtil.encrypt(dataToEncrypt);
        
        // Write plain-text header information to file.
        bufferedOS.write((VERSION_PREFIX + EncryptionUtil.DEFAULT_VERSION + "\n").getBytes(Constants.DEFAULT_ENCODING));
        bufferedOS.write((encryptionUtil.getAlgorithm() + "\n").getBytes(Constants.DEFAULT_ENCODING));
        bufferedOS.write(((friendlyName == null? "" : friendlyName) + "\n").getBytes(Constants.DEFAULT_ENCODING));
        
        bufferedOS.write(encryptedData);
        bufferedOS.flush();
    }

    /**
     * Loads encrypted credentials from a file.
     * 
     * @param password
     * the password used to decrypt the credentials. If null, the AWS Credentials are not decrypted
     * and only the version and friendly-name information is loaded.
     * @param file
     * a file containing an encrypted data encoding of an AWSCredentials object.
     * @return 
     * the decrypted credentials in an object.
     * 
     * @throws S3ServiceException
     */
    public static AWSCredentials load(String password, File file) throws S3ServiceException {
    	if (log.isDebugEnabled()) {
    		log.debug("Loading credentials from file: " + file.getAbsolutePath());
    	}
        BufferedInputStream fileIS = null;
        try {
            fileIS = new BufferedInputStream(new FileInputStream(file));
            return load(password, fileIS);
        } catch (Throwable t) {
            throw new S3ServiceException("Failed to load AWS credentials", t);
        } finally {
            if (fileIS != null) {
                try {
                    fileIS.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    /**
     * Loads encrypted credentials from a data input stream.
     * 
     * @param password
     * the password used to decrypt the credentials. If null, the AWS Credentials are not decrypted
     * and only the version and friendly-name information is loaded.
     * @param inputStream
     * an input stream containing an encrypted  data encoding of an AWSCredentials object.
     * @return 
     * the decrypted credentials in an object.
     * 
     * @throws S3ServiceException
     */
    public static AWSCredentials load(String password, BufferedInputStream inputStream) throws S3ServiceException {
        boolean partialReadOnly = (password == null);
        if (partialReadOnly) {
        	if (log.isDebugEnabled()) {
        		log.debug("Loading partial information about AWS Credentials from input stream");
        	}
        } else {
        	if (log.isDebugEnabled()) {
        		log.debug("Loading AWS Credentials from input stream");
        	}
        }
        
        try {
            EncryptionUtil encryptionUtil = null; 
            byte[] encryptedKeys = new byte[2048];
            int encryptedDataIndex = 0;
            
            String version = null;
            String algorithm = null;
            String friendlyName = null;
            
            // Read version information from AWS credentials file.
            version = ServiceUtils.readInputStreamLineToString(inputStream, Constants.DEFAULT_ENCODING);
            
            if (!version.startsWith(VERSION_PREFIX)) {
                // Either this is not a valid AWS Credentials file, or it's an obsolete version.
                // Try decrypting using the obsolete approach.
                friendlyName = version;
                
                if (!partialReadOnly) {
                    encryptionUtil = EncryptionUtil.getObsoleteEncryptionUtil(password);
                }
            } else {
                // Read algorithm and friendly name from file.
                algorithm = ServiceUtils.readInputStreamLineToString(inputStream, Constants.DEFAULT_ENCODING);
                friendlyName = ServiceUtils.readInputStreamLineToString(inputStream, Constants.DEFAULT_ENCODING);    
                
                if (!partialReadOnly) {
                    encryptionUtil = new EncryptionUtil(password, algorithm, EncryptionUtil.DEFAULT_VERSION);
                }
            }
            
            if (partialReadOnly) {
                return new AWSCredentials(null, null, friendlyName);                
            }            
            
            // Read encrypted data bytes from file.
            encryptedDataIndex = inputStream.read(encryptedKeys);
            
            // Decrypt data.
            String keys = encryptionUtil.decryptString(encryptedKeys, 0, encryptedDataIndex);

            int delimOffset = keys.indexOf(KEYS_DELIMITER);
            if (delimOffset < 0) {
                throw new Exception("Unable to load AWS keys. Is the password correct?");
            }

            AWSCredentials awsCredentials = new AWSCredentials(keys.substring(0, delimOffset), keys
                .substring(delimOffset + KEYS_DELIMITER.length()), friendlyName);
            
            return awsCredentials;
        } catch (BadPaddingException bpe) {
            throw new S3ServiceException("Unable to decrypt AWS credentials. Is your password correct?", bpe);
        } catch (Throwable t) {
            throw new S3ServiceException("Failed to load AWS credentials", t);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Console utility to store AWS credential information in an encrypted file in the toolkit's
     * default preferences directory.
     * <p>
     * This class can be run from the command line as:
     * <pre>
     * java org.jets3t.service.security.AWSCredentials &lt;friendlyName> &lt;credentialsFilename>
     * </pre>
     * When run it will prompt for the user's AWS access key,secret key and encryption password. 
     * It will then encode into the specified credentials file.  
     * 
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            printHelp();
            System.exit(1);
        }
        String userName = args[0];
        File encryptedFile = new File(args[1]);

        // Check arguments provided.
        try {
            FileOutputStream testFOS = new FileOutputStream(encryptedFile);
            testFOS.close();
        } catch (IOException e) {
            System.err.println("Unable to write to file: " + encryptedFile);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // Obtain credentials and password from user.
        System.out.println("Please enter your AWS Credentials");
        System.out.print("Access Key: ");
        String awsAccessKey = reader.readLine();
        System.out.print("Secret Key: ");
        String awsSecretKey = reader.readLine();
        System.out
            .println("Please enter a password to protect your credentials file (may be empty)");
        System.out.print("Password: ");
        String password = reader.readLine();

        // Create AWSCredentials object and save the details to an encrypted file.
        AWSCredentials awsCredentials = new AWSCredentials(awsAccessKey, awsSecretKey, userName);
        awsCredentials.save(password, encryptedFile);

        System.out.println("Successfully saved AWS Credentials to " + encryptedFile);
    }

    /**
     * Prints help for the use of this class from the console (via the main method).
     */
    private static void printHelp() {
        System.out.println("AWSCredentials <User Name> <File Path>");
        System.out.println();
        System.out
            .println("User Name: A human-friendly name for the owner of the credentials, e.g. Horace.");
        System.out
            .println("File Path: Path and name for the encrypted file. Will be replaced if it already exists.");
    }

}
