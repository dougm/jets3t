package org.jets3t.servlets.gatekeeper.impl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.jets3t.service.utils.gatekeeper.GatekeeperMessage;
import org.jets3t.servlets.gatekeeper.ClientInformation;
import org.jets3t.servlets.gatekeeper.TransactionIdProvider;
import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;

/**
 * Default TransactionIdProvider implementation that generated random-based UUIDs using the
 * <a href="http://jug.safehaus.org/Home">Java Uuid Generator</a>. 
 *  
 * @author James Murty
 */
public class DefaultTransactionIdProvider extends TransactionIdProvider {

    /**
     * Constructs the TransactionIdProvider - no configuration parameters are required. 
     * 
     * @param servletConfig
     * @throws ServletException
     */
    public DefaultTransactionIdProvider(ServletConfig servletConfig) throws ServletException {
        super(servletConfig);
    }

    /**
     * Returns a random-based UUID.
     */
    public String getTransactionId(GatekeeperMessage requestMessage, ClientInformation clientInformation)
    {
        // Generate a UUID based on a random generation.
        UUID uuid = UUIDGenerator.getInstance().generateRandomBasedUUID();
        return uuid.toString();
    }

}
