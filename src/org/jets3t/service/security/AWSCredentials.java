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
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.jets3t.service.S3ServiceException;

/**
 * Class to contain the Amazon Web Services (AWS) credentials of a user. 
 * This class also includes utility methods to store credentials to password-encrypted files, and
 * retrieve credentials from these files.
 * 
 * @author James Murty
 */
public class AWSCredentials {
	private static String KEYS_DELIMITER = "AWSKEYS"; 
	
	private String awsAccessKey = null;
	private String awsSecretAccessKey = null;
	private String friendlyName = null;
	
    /**
     * Construct credentials.
     * 
     * @param awsAccessKey
     *        AWS access key for an Amazon S3 account.
     * @param awsSecretAccessKey
     *        AWS secret key for an Amazon S3 acount.
     */
	public AWSCredentials(String awsAccessKey, String awsSecretAccessKey) {
		this.awsAccessKey = awsAccessKey;
		this.awsSecretAccessKey = awsSecretAccessKey;		
	}

    /**
     * Construct credentials, and associate them with a human-friendly name.
     * 
     * @param awsAccessKey
     *        AWS access key for an Amazon S3 account.
     * @param awsSecretAccessKey
     *        AWS secret key for an Amazon S3 acount.
     * @param friendlyName
     *        a name identifying the owner of the credentials, such as 'James'.
     */
	public AWSCredentials(String awsAccessKey, String awsSecretAccessKey, String friendlyName) {
		this(awsAccessKey, awsSecretAccessKey);
		this.friendlyName = friendlyName;
	}

	public String getAccessKey() {
		return awsAccessKey;
	}
	
	public String getSecretKey() {
		return awsSecretAccessKey;
	}
	
	public String getFriendlyName() {
		return friendlyName;
	}
	
    /**
     * Encrypts files with the given password and saves them to a file.
     * 
     * @param password
     *        the password used to encrypt the credentials.
     * @param file
     *        the file to write the encrypted credentials data to.
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
	public void save(String password, File file) 
		throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, 
		IllegalStateException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException 
	{
		BufferedOutputStream fileOS = null;
		try {
			EncryptionUtil encryptionUtil = new EncryptionUtil(password);
			fileOS = new BufferedOutputStream(new FileOutputStream(file));
			
			if (friendlyName == null) {
				friendlyName = "";
			} 
			
			String dataToEncrypt = getAccessKey() + KEYS_DELIMITER + getSecretKey();
			byte[] encryptedData = encryptionUtil.encrypt(dataToEncrypt);
			
			fileOS.write((friendlyName + "\n").getBytes());
			fileOS.write(encryptedData);
			fileOS.close();
		} finally {
			if (fileOS != null) {
				try {
					fileOS.close();
				} catch (IOException e) {					
				}
			}
		}
	}
	
    /**
     * Loads encrypted credentials from a file.
     * 
     * @param password
     *        the password used to decrypt the credentials.
     * @param file
     *        the file in which encrypted credential data is stored.
     * @return
     * the decrypted credentials in an object.
     * @throws S3ServiceException
     */
	public static AWSCredentials load(String password, File file) throws S3ServiceException {
		BufferedInputStream fileIS = null;
		try {
			EncryptionUtil encryptionUtil = new EncryptionUtil(password);
			fileIS = new BufferedInputStream(new FileInputStream(file));
			
			StringBuffer friendlyName = new StringBuffer();
			byte[] encryptedKeys = new byte[2048];
			
			int b = -1;
	
			// First '\n'-delimited line is human-friendly name of owner.
			while ((b = fileIS.read()) != -1) {
				if ('\n' == (char)b) {
					break;
				} else {
					friendlyName.append((char)b);
				}
			}
			
			// Second line is encrypted access/secret keys.
			int encryptedDataIndex = 0;
			while ((b = fileIS.read()) != -1) {
				if (encryptedDataIndex >= encryptedKeys.length) {
					throw new Exception("Encrypted data line in file " + file.getAbsolutePath() + " is too long");
				}
				encryptedKeys[encryptedDataIndex++] = (byte) b;
			}
	
			String keys = encryptionUtil.decryptString(encryptedKeys, 0, encryptedDataIndex);
			
			int delimOffset = keys.indexOf(KEYS_DELIMITER);
			if (delimOffset < 0) {
				throw new Exception("Unable to load AWS keys from file " 
                    + file.getAbsolutePath() + ". Is the password correct?");
			}
			
			AWSCredentials awsCredentials = new AWSCredentials(
				keys.substring(0, delimOffset), 
				keys.substring(delimOffset + KEYS_DELIMITER.length()),
				friendlyName.toString());
			return awsCredentials;
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
	 * Console utility to store AWS credential information in an encrypted file
	 * in the toolkit's default preferences directory.
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
		System.out.println("Please enter a password to protect your credentials file (may be empty)");
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
		System.out.println("User Name: A human-friendly name for the owner of the credentials, e.g. Horace.");
		System.out.println("File Path: Path and name for the encrypted file. Will be replaced if it already exists.");
	}

}
