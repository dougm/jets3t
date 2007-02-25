package org.jets3t.service.utils.gatekeeper;

public class IncompatibleClientException extends Exception {
    private static final long serialVersionUID = 227400247936901112L;
    
    public static final String INCOMPATIBLE_CLIENT_EXCEPTION_CODE = "IncompatibleClientException";

    public IncompatibleClientException() {
        super(INCOMPATIBLE_CLIENT_EXCEPTION_CODE);
    }
    
    public String getMessage() {
        return "This client application is not compatible with the Gatekeeper";
    }
    
}
