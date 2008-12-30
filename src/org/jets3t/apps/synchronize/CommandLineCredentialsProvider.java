/*
 * jets3t : Java Extra-Tasty S3 Toolkit (for Amazon S3 online storage service)
 * This is a java.net project, see https://jets3t.dev.java.net/
 * 
 * Copyright 2008 James Murty
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.CredentialsNotAvailableException;
import org.apache.commons.httpclient.auth.CredentialsProvider;
import org.apache.commons.httpclient.auth.NTLMScheme;
import org.apache.commons.httpclient.auth.RFC2617Scheme;

/**
 * Prompts for the user to enter HTTP Proxy authentication credentials via the 
 * command line.
 * 
 * @author James Murty
 */
public class CommandLineCredentialsProvider implements CredentialsProvider {
    
    /**
     * Implementation method for the CredentialsProvider interface.
     * <p>
     * Based on sample code:  
     * <a href="http://svn.apache.org/viewvc/jakarta/commons/proper/httpclient/trunk/src/examples/InteractiveAuthenticationExample.java?view=markup">InteractiveAuthenticationExample</a> 
     * 
     */
    public Credentials getCredentials(AuthScheme authscheme, String host, int port, boolean proxy) throws CredentialsNotAvailableException {
        if (authscheme == null) {
            return null;
        }
        try {
            Credentials credentials = null;            
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

            if (authscheme instanceof NTLMScheme) {
                System.out.println("Proxy Authentication Required -- " +
                    "Host " + host + ":" + port + " requires Windows authentication");
                System.out.print("Username: ");
                String username = inputReader.readLine();
                System.out.print("Password: ");
                String password = inputReader.readLine();
                System.out.print("Domain: ");
                String domain = inputReader.readLine();
                
                credentials = new NTCredentials(username, password, host, domain);
            } else
            if (authscheme instanceof RFC2617Scheme) {
                System.out.println("Proxy Authentication Required -- " +
                    "Host " + host + ":" + port + " requires authentication for the realm: " + authscheme.getRealm());
                System.out.print("Username: ");
                String username = inputReader.readLine();
                System.out.print("Password: ");
                String password = inputReader.readLine();
                                
                credentials = new UsernamePasswordCredentials(username, password);
            } else {
                throw new CredentialsNotAvailableException("Unsupported authentication scheme: " +
                    authscheme.getSchemeName());
            }
            return credentials;
        } catch (IOException e) {
            throw new CredentialsNotAvailableException(e.getMessage(), e);
        }
    }           

}
