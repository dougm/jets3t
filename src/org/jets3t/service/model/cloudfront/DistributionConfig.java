package org.jets3t.service.model.cloudfront;

import java.util.Arrays;


public class DistributionConfig {
    private String origin = null;
    private String callerReference = null;
    private String[] cnames = new String[0];
    private String comment = null;
    private boolean enabled = false;       
    private String etag = null;
    
    public DistributionConfig(String origin, String callerReference, 
        String[] cnames, String comment, boolean enabled) 
    {
        this.origin = origin;
        this.callerReference = callerReference;
        this.cnames = cnames;
        this.comment = comment;        
        this.enabled = enabled;
    }
    
    public String getOrigin() {
        return origin;
    }

    public String getCallerReference() {
        return callerReference;
    }
    
    public String[] getCNAMEs() {
        return this.cnames;
    }

    public String getComment() {
        return comment;
    }

    public boolean isEnabled() {
        return enabled;
    }
    
    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }
    
    public String toString() {
        return "CloudFrontDistributionConfig: origin=" + origin +
            ", callerReference=" + callerReference + ", comment=" + comment +
            ", enabled=" + enabled +
            (etag != null ? ", etag=" + etag : "") +
            ", CNAMEs=" + Arrays.asList(cnames);
    }

}
