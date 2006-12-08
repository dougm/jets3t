package org.jets3t.servlets.gatekeeper.impl;

import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.jets3t.servlets.gatekeeper.ClientInformation;
import org.jets3t.servlets.gatekeeper.TransactionIdProvider;
import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;

public class DefaultTransactionIdProvider extends TransactionIdProvider {

    public DefaultTransactionIdProvider(ServletConfig servletConfig) throws ServletException {
        super(servletConfig);
    }

    public String getTransactionId(Properties applicationProperties, 
        Properties messageProperties, ClientInformation clientInformation)
    {
        // Generate a UUID based on a random generation, followed by filename-seeded generation.
        UUID uuid = UUIDGenerator.getInstance().generateRandomBasedUUID();
        return uuid.toString();
    }

}
