package org.jets3t.servlets.gatekeeper;

import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

public abstract class TransactionIdProvider {

    public TransactionIdProvider(ServletConfig servletConfig) throws ServletException {
    }
    
    public abstract String getTransactionId(Properties applicationProperties, 
        Properties messageProperties, ClientInformation clientInformation);
    
}
