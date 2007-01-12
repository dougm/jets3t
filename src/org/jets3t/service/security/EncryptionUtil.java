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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.Jets3tProperties;

/**
 * Utility class to handle encryption and decryption in the jets3t toolkit. 
 * 
 * <p><b>Properties</b></p>
 * <p>The following properties, obtained through {@link Jets3tProperties}, are used by this class:</p>
 * <table>
 * <tr><th>Property</th><th>Description</th><th>Default</th></tr>
 * <tr><td>crypto.algorithm</td>
 *   <td>Name of the PBE cryptographic algorithm to use. The algorithms available will depend on 
 *       the crypto providers installed on a system, and the system's policy file settings.</td> 
 *   <td>PBEWithMD5AndDES</td></tr>
 * </table>
 * 
 * @author James Murty
 */
public class EncryptionUtil {
    private static final Log log = LogFactory.getLog(EncryptionUtil.class);
    
    public static final String UNICODE_FORMAT = "UTF8";
    public static final String VERSION = "2";

    private String algorithm = null;
    private SecretKey key = null;
    private AlgorithmParameterSpec algParamSpec = null;
    
    int ITERATION_COUNT = 5000;
    byte[] salt = {
        (byte)0xA4, (byte)0x0B, (byte)0xC8, (byte)0x34,
        (byte)0xD6, (byte)0x95, (byte)0xF3, (byte)0x13
    };

    /**
     * Constructs class configured with the provided password, and set up to use the encryption
     * method specified.  
     * 
     * @param encryptionKey
     *        the password to use for encryption/decryption.
     * @param algorithm
     *        the Java name of an encryption algorithm to use, eg PBEWithMD5AndDES
     * 
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     */
    public EncryptionUtil(String encryptionKey, String algorithm) throws 
        InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException 
    {
        log.debug("Cryptographic algorithm: " + algorithm);
        this.algorithm = algorithm;
            
        PBEKeySpec keyspec = new PBEKeySpec(encryptionKey.toCharArray(), salt, ITERATION_COUNT, 32);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm);
        key = skf.generateSecret(keyspec);
        algParamSpec = new PBEParameterSpec(salt, ITERATION_COUNT);
    }

    /**
     * Constructs class configured with the provided password, and set up to use the default encryption
     * algorith PBEWithMD5AndDES.
     * 
     * @param encryptionKey
     *        the password to use for encryption/decryption.
     *        
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     */
    public EncryptionUtil(String encryptionKey) throws InvalidKeyException,
        NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException 
    {
        this(encryptionKey, "PBEWithMD5AndDES");
    }
        
    /**
     * Constructs the class using the obsolete methodology from 0.4.0, which used the 
     * DESede algorithm instead of a PBE version.
     * 
     * @param encryptionKey
     * @param encryptionScheme
     * @param blockMode
     * @param paddingMode
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     * @throws UnsupportedEncodingException 
     * @throws  
     * 
     * @deprecated
     */
    private EncryptionUtil(String encryptionKey, String encryptionScheme, String blockMode,
        String paddingMode) throws InvalidKeyException, NoSuchAlgorithmException,
        NoSuchPaddingException, InvalidKeySpecException, UnsupportedEncodingException 
    {
        byte[] KEY_BASE_BYTES = new byte[] {
            (byte)0xC2, (byte)0xAB, (byte)0xE2, (byte)0x80, (byte)0xA0, (byte)0x0E, 
            (byte)0xC2, (byte)0xBB, (byte)0xC3, (byte)0xA6, (byte)0xE2, (byte)0x80, 
            (byte)0x94, (byte)0x72, (byte)0x3D, (byte)0xC3, (byte)0xB4, (byte)0x18, 
            (byte)0x14, (byte)0x51, (byte)0xC5, (byte)0x92, (byte)0x20, (byte)0x79, 
            (byte)0xE2, (byte)0x84, (byte)0xA2, (byte)0x53, (byte)0x34, (byte)0x43, 
            (byte)0x2E, (byte)0x24, (byte)0x53, (byte)0xC3, (byte)0xBF, (byte)0xC3, 
            (byte)0x92, (byte)0x74, (byte)0xCB, (byte)0x9A, (byte)0xC3, (byte)0xA0, 
            (byte)0xC3, (byte)0xA1, (byte)0x49, (byte)0xC3, (byte)0x8C, (byte)0x5B, 
            (byte)0xE2, (byte)0x88, (byte)0x86, (byte)0x4F, (byte)0xC3, (byte)0x8B, 
            (byte)0xC3, (byte)0x96, (byte)0xC3, (byte)0x84, (byte)0x75, (byte)0x40, 
            (byte)0xE2, (byte)0x80, (byte)0x9D, (byte)0x1C, (byte)0xC2, (byte)0xA9, 
            (byte)0x64, (byte)0x46, (byte)0x16, (byte)0x54, (byte)0x17, (byte)0x03, 
            (byte)0xC2, (byte)0xB4, (byte)0xC3, (byte)0xA4, (byte)0xE2, (byte)0x81, 
            (byte)0x84, (byte)0xC3, (byte)0xA1, (byte)0x4E, (byte)0x68, (byte)0xC3, 
            (byte)0x8B, (byte)0xC3, (byte)0xAA, (byte)0x76, (byte)0xC2, (byte)0xAB, 
            (byte)0x1D, (byte)0xE2, (byte)0x80, (byte)0x94, (byte)0xC2, (byte)0xA3, 
            (byte)0x6C, (byte)0xC3, (byte)0xBB, (byte)0x5E, (byte)0x75, (byte)0xE2, 
            (byte)0x80, (byte)0x9D, (byte)0xC3, (byte)0x83, (byte)0x10, (byte)0xCF, 
            (byte)0x80, (byte)0x2B, (byte)0x74, (byte)0xC5, (byte)0x93, (byte)0x3A, 
            (byte)0xC3, (byte)0x80, (byte)0x4B, (byte)0x37, (byte)0x51, (byte)0xC2, 
            (byte)0xA7, (byte)0xE2, (byte)0x88, (byte)0x9E, (byte)0x48, (byte)0x3E, 
            (byte)0xE2, (byte)0x80, (byte)0x9E, (byte)0x3A, (byte)0x69, (byte)0xC3, 
            (byte)0x8A, (byte)0x75, (byte)0x11, (byte)0xE2, (byte)0x80, (byte)0xB0, 
            (byte)0xC3, (byte)0x94, (byte)0xC3, (byte)0xBC, (byte)0x51, (byte)0xC3, 
            (byte)0x93, (byte)0x23, (byte)0xE2, (byte)0x80, (byte)0xBA, (byte)0xC2, 
            (byte)0xA5, (byte)0x31, (byte)0xE2, (byte)0x80, (byte)0x94, (byte)0x7A, 
            (byte)0x6A, (byte)0xC2, (byte)0xB5, (byte)0xE2, (byte)0x81, (byte)0x84, 
            (byte)0xE2, (byte)0x80, (byte)0xB9, (byte)0x29, (byte)0x31, (byte)0x6F, 
            (byte)0xE2, (byte)0x80, (byte)0xB0, (byte)0xC3, (byte)0xB7, (byte)0x4D, 
            (byte)0xC3, (byte)0x98, (byte)0x35, (byte)0x44, (byte)0x46, (byte)0xC3, 
            (byte)0xAD, (byte)0xC2, (byte)0xAB, (byte)0xC5, (byte)0xB8, (byte)0x2E, 
            (byte)0x23, (byte)0x63, (byte)0x3B, (byte)0xC2, (byte)0xAF, (byte)0xC2, 
            (byte)0xB7, (byte)0xEF, (byte)0xA3, (byte)0xBF, (byte)0xCB, (byte)0x86, 
            (byte)0xC3, (byte)0x8C, (byte)0x42, (byte)0xCE, (byte)0xA9, (byte)0xE2, 
            (byte)0x88, (byte)0x86, (byte)0x76, (byte)0xE2, (byte)0x84, (byte)0xA2
        };

        algorithm = "DESede/CBC/PKCS5Padding";        
        encryptionKey = encryptionKey + new String(KEY_BASE_BYTES, "UTF8");
    
        int keyOffset = 0;
        byte spec[] = new byte[8];
        for (int specOffset = 0; specOffset < spec.length; specOffset++) {
            keyOffset = (keyOffset + 7) % encryptionKey.length();
            spec[specOffset] = encryptionKey.getBytes()[keyOffset];
        }
        
        KeySpec keySpec = new DESedeKeySpec(encryptionKey.getBytes());
        algParamSpec = new IvParameterSpec(spec);
        key = SecretKeyFactory.getInstance(encryptionScheme).generateSecret(keySpec);        
    }

    /**
     * 
     * @param encryptionKey
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     * @throws UnsupportedEncodingException 
     * 
     * @deprecated
     */
    public static EncryptionUtil getObsoleteEncryptionUtil(String encryptionKey) throws 
        InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, 
        UnsupportedEncodingException 
    {
        return new EncryptionUtil(encryptionKey, "DESede", "CBC", "PKCS5Padding");
    }
    
    protected Cipher initEncryptModeCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, 
        InvalidKeyException, InvalidAlgorithmParameterException 
    {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, algParamSpec);
        return cipher;
    }

    protected Cipher initDecryptModeCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, 
        InvalidKeyException, InvalidAlgorithmParameterException 
    {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, algParamSpec);
        return cipher;
    }

    /**
     * Encrypts a UTF8 string to byte data.
     * 
     * @param data
     * @return
     * @throws IllegalStateException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public byte[] encrypt(String data) throws IllegalStateException, IllegalBlockSizeException,
        BadPaddingException, UnsupportedEncodingException, InvalidKeySpecException,
        InvalidKeyException, InvalidAlgorithmParameterException, 
        NoSuchAlgorithmException, NoSuchPaddingException
    {
        Cipher cipher = initEncryptModeCipher();
        return cipher.doFinal(data.getBytes(UNICODE_FORMAT));
    }

    /**
     * Decrypts byte data to a UTF8 string.
     * 
     * @param data
     * @return
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws UnsupportedEncodingException
     * @throws IllegalStateException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public String decryptString(byte[] data) throws InvalidKeyException,
        InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalStateException,
        IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException
    {
        Cipher cipher = initEncryptModeCipher();
        return new String(cipher.doFinal(data), UNICODE_FORMAT);
    }

    /**
     * Decrypts a UTF8 string.
     * 
     * @param data
     * @param startIndex
     * @param endIndex
     * @return
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws UnsupportedEncodingException
     * @throws IllegalStateException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public String decryptString(byte[] data, int startIndex, int endIndex)
        throws InvalidKeyException, InvalidAlgorithmParameterException,
        UnsupportedEncodingException, IllegalStateException, IllegalBlockSizeException,
        BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException
    {
        Cipher cipher = initDecryptModeCipher();
        return new String(cipher.doFinal(data, startIndex, endIndex), UNICODE_FORMAT);
    }

    /**
     * Encrypts byte data to bytes.
     * 
     * @param data
     * @return
     * @throws IllegalStateException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public byte[] encrypt(byte[] data) throws IllegalStateException, IllegalBlockSizeException,
        BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, 
        NoSuchAlgorithmException, NoSuchPaddingException
    {
        Cipher cipher = initEncryptModeCipher();
        return cipher.doFinal(data);
    }

    /**
     * Decrypts byte data to bytes.
     * 
     * @param data
     * @return
     * decrypted data.
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalStateException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public byte[] decrypt(byte[] data) throws InvalidKeyException,
        InvalidAlgorithmParameterException, IllegalStateException, IllegalBlockSizeException,
        BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException
    {
        Cipher cipher = initDecryptModeCipher();
        return cipher.doFinal(data);
    }

    /**
     * Decrypts a byte data range to bytes.
     *  
     * @param data
     * @param startIndex
     * @param endIndex
     * @return
     * decrypted data.
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalStateException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public byte[] decrypt(byte[] data, int startIndex, int endIndex) throws InvalidKeyException,
        InvalidAlgorithmParameterException, IllegalStateException, IllegalBlockSizeException,
        BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException
    {
        Cipher cipher = initDecryptModeCipher();
        return cipher.doFinal(data, startIndex, endIndex);
    }

    /**
     * Wraps an input stream in an encrypting cipher stream. 
     * 
     * @param is
     * @return
     * encrypting cipher input stream.
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public CipherInputStream encrypt(InputStream is) throws InvalidKeyException,
        InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException
    {
        Cipher cipher = initEncryptModeCipher();
        return new CipherInputStream(is, cipher);
    }

    /**
     * Wraps an input stream in an decrypting cipher stream.
     * 
     * @param is
     * @return
     * decrypting cipher input stream.
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public CipherInputStream decrypt(InputStream is) throws InvalidKeyException,
        InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException
    {
        Cipher cipher = initDecryptModeCipher();
        return new CipherInputStream(is, cipher);
    }

    /**
     * Wraps an output stream in an encrypting cipher stream.
     * 
     * @param os
     * @return
     * encrypting cipher output stream.
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public CipherOutputStream encrypt(OutputStream os) throws InvalidKeyException,
        InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException
    {
        Cipher cipher = initEncryptModeCipher();
        return new CipherOutputStream(os, cipher);
    }

    /**
     * Wraps an output stream in a decrypting cipher stream.
     * 
     * @param os
     * @return
     * decrypting cipher output stream.
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public CipherOutputStream decrypt(OutputStream os) throws InvalidKeyException,
        InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException
    {
        Cipher cipher = initDecryptModeCipher();
        return new CipherOutputStream(os, cipher);
    }

    /**
     * @return
     * the Java name of the cipher algorithm being used by this class.
     */
    public String getAlgorithm() {
        return algorithm;
    }


    public static String[] listAvailableCiphers() {
        Set ciphers = Security.getAlgorithms("Cipher");
        return (String[]) ciphers.toArray(new String[] {});           
    }
    
    public static String[] listAvailableAlgorithms() {
        Set ciphers = Security.getAlgorithms("SecretKeyAlgorithm");
        return (String[]) ciphers.toArray(new String[] {});           
    }

    // TODO Remove
    public static void main(String[] args) throws Exception {
        String[] ciphers = EncryptionUtil.listAvailableCiphers();
        System.out.println("Ciphers:");
        for (int i = 0; i < ciphers.length; i++) {
            System.out.println(ciphers[i]);
        }

        String[] algorithms = EncryptionUtil.listAvailableCiphers();
        System.out.println("Algorithms:");
        for (int i = 0; i < algorithms.length; i++) {
            System.out.println(algorithms[i]);
        }
        
//        AWSCredentials creds = AWSCredentials.load("please", 
//            new java.io.File("/Users/jmurty/.jets3t/James.enc"));
//        System.out.println(creds.getFriendlyName() + ": " + creds.getAccessKey());
        
//        AWSCredentials creds = new AWSCredentials("AccessKey", "SecretKey", "JamŽs Mžrty");
//        creds.save("please", new java.io.File("/Users/jmurty/Desktop/Test.enc"));
//        creds = AWSCredentials.load("please", new java.io.File("/Users/jmurty/Desktop/Test.enc"));
//        System.out.println(creds.getFriendlyName() + ": " + creds.getAccessKey() + "/" + creds.getSecretKey());
    }

}
