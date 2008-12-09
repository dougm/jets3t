/*
 * jets3t : Java Extra-Tasty S3 Toolkit (for Amazon S3 online storage service)
 * This is a java.net project, see https://jets3t.dev.java.net/
 * 
 * Copyright 2006 James Murty, 2008 Zmanda Inc.
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

/**
 * Class to contain the Amazon Web Services (AWS) credentials of a user,
 * with bits needed for DevPay.
 * 
 * @author Nikolas Coukouma
 */
public class AWSDevPayCredentials extends AWSCredentials {
    private static final long serialVersionUID = -8971737296373324817L;

    private String awsUserToken = null;
    private String awsProductToken = null;

    protected AWSDevPayCredentials(String awsAccessKey, String awsSecretAccessKey) {
        super(awsAccessKey, awsSecretAccessKey);
    }
    
    protected AWSDevPayCredentials(String awsAccessKey, String awsSecretAccessKey, String friendlyName) {
        super(awsAccessKey, awsSecretAccessKey, friendlyName);
    }

    /**
     * Construct credentials.
     * 
     * @param awsAccessKey
     * AWS access key for an Amazon S3 account.
     * @param awsSecretAccessKey
     * AWS secret key for an Amazon S3 acount.
     * @param awsUserToken
     * DevPay user token for an Amazon S3 acount.
     * @param awsProductToken
     * DevPay product token for an Amazon S3 acount.
     */
    public AWSDevPayCredentials(String awsAccessKey, String awsSecretAccessKey, String awsUserToken, String awsProductToken) {
        super(awsAccessKey, awsSecretAccessKey);
        this.awsUserToken = awsUserToken;
        this.awsProductToken = awsProductToken;
    }

    /**
     * Construct credentials.
     * 
     * @param awsAccessKey
     * AWS access key for an Amazon S3 account.
     * @param awsSecretAccessKey
     * AWS secret key for an Amazon S3 acount.
     * @param awsUserToken
     * DevPay user token for an Amazon S3 acount.
     * @param awsProductToken
     * DevPay product token for an Amazon S3 acount.
     * @param friendlyName
     * a name identifying the owner of the credentials, such as 'James'.
     */
    public AWSDevPayCredentials(String awsAccessKey, String awsSecretAccessKey, String awsUserToken, String awsProductToken, String friendlyName) {
        super(awsAccessKey, awsSecretAccessKey, friendlyName);
        this.awsUserToken = awsUserToken;
        this.awsProductToken = awsProductToken;
    }

    /**
     * @return
     * the AWS User Token
     */
    public String getUserToken() {
        return awsUserToken;
    }

    /**
     * @return
     * the AWS Product Token
     */
    public String getProductToken() {
        return awsProductToken;
    }

    /**
     * @return
     * a string summarizing these credentials
     */
    public String getLogString() {
        return super.getLogString() + " : " + getUserToken() + " : " + getProductToken();
    }

    /**
     * @return
     * string representing this credential type's name (for serialization)
     */
    protected String getTypeName() {
        return DEVPAY_TYPE_NAME;
    }

    /**
     * @return
     * the string of data that needs to be encrypted (for serialization)
     */
    protected String getDataToEncrypt() {
        return getAccessKey() + V3_KEYS_DELIMITER + 
            getSecretKey() + V3_KEYS_DELIMITER + 
            getUserToken() + V3_KEYS_DELIMITER + getProductToken();
    }
}
