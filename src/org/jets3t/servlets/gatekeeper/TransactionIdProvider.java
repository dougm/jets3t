package org.jets3t.servlets.gatekeeper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.jets3t.service.utils.gatekeeper.GatekeeperMessage;

/**
 * Provides a transaction ID that uniquely identifies a Gatekeeper transaction - that is, a request
 * and response interaction.
 * <p>
 * A transaction ID could be based on a user's session ID (available in the client information), 
 * come from a database sequence, or any other mechanism that is likely to generate unique IDs.
 * 
 * @author James Murty
 */
public abstract class TransactionIdProvider {

    /**
     * Constructs a TransactionIdProvider.
     * 
     * @param servletConfig
     * @throws ServletException
     */
    public TransactionIdProvider(ServletConfig servletConfig) throws ServletException {
    }
   
    /**
     * Returns a transaction ID to uniquely identify the Gatekeeper transaction - if transaction
     * tracking is not required this method can return an empty string.
     * 
     * @param requestMessage
     * @param clientInformation
     * 
     * @return
     * an ID unique to this transaction.
     */
    public abstract String getTransactionId(GatekeeperMessage requestMessage,  
        ClientInformation clientInformation);
    
}
