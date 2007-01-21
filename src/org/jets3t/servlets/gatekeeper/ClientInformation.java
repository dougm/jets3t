package org.jets3t.servlets.gatekeeper;

import java.security.Principal;

import javax.servlet.http.HttpSession;

/**
 * Stores information about the HTTP client that submitted a request to the Gatekeeper.
 * <p>
 * The information available about a client will depend on the server and client configuration,
 * such as whether the client is identified with an existing HttpSession or Principal. It must
 * be assumed that much of the information stored in this class will have a null value in many
 * cases.
 * <p>
 * All information in this class is sourced from equivalent methods in 
 * {@link javax.servlet.http.HttpServletRequest}.
 * 
 * @author James Murty
 */
public class ClientInformation {
    private String remoteAddress = null;
    private String remoteHost = null;
    private String remoteUser = null;
    private int remotePort = -1;
    private HttpSession session = null;
    private Principal userPrincipal = null;
    private String userAgent = null;
    
    public ClientInformation(String remoteAddress, String remoteHost, String remoteUser,
        int remotePort, HttpSession session, Principal userPrincipal, String userAgent) 
    {
        this.remoteAddress = remoteAddress;
        this.remoteHost = remoteHost;
        this.remoteUser = remoteUser;
        this.remotePort = remotePort;
        this.session = session;
        this.userPrincipal = userPrincipal;
        this.userAgent = userAgent;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public HttpSession getSession() {
        return session;
    }

    public Principal getUserPrincipal() {
        return userPrincipal;
    }

    public String getUserAgent() {
        return userAgent;
    }

}
