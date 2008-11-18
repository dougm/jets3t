package org.jets3t.service.model.cloudfront;

import java.util.Arrays;
import java.util.Date;

public class Distribution {
    private String id = null;
    private String status = null;        
    private Date lastModifiedTime = null;
    private String domainName = null;
    private String origin = null;
    private String cnames[] = new String[0];
    private String comment = null;
    private boolean enabled = false;
    private DistributionConfig config = null;
    
    public Distribution(String id, String status, Date lastModifiedDate,
        String domainName, String origin, String[] cnames, String comment, 
        boolean enabled) 
    {
        this.id = id;
        this.status = status;
        this.lastModifiedTime = lastModifiedDate;
        this.domainName = domainName;
        this.origin = origin;
        this.cnames = cnames;
        this.comment = comment;
        this.enabled = enabled;
    }
    
    public Distribution(String id, String status, Date lastModifiedDate,
        String domainName, DistributionConfig config) 
    {
        this.id = id;
        this.status = status;
        this.lastModifiedTime = lastModifiedDate;
        this.domainName = domainName;
        this.config = config;
    }

    public boolean isSummary() {
        return getConfig() == null;
    }
    
    public String getComment() {
        return comment;
    }
    public String getDomainName() {
        return domainName;
    }
    public String getId() {
        return id;
    }
    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }
    public String getOrigin() {
        return origin;
    }
    public String[] getCNAMEs() {
        return cnames;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public String getStatus() {
        return status;
    }

    public DistributionConfig getConfig() {
        return config;
    }
    
    public String toString() {
        return "CloudFrontDistribution: id=" + id + ", status=" + status + 
            ", domainName=" + domainName + ", lastModifiedTime=" + lastModifiedTime +
            (isSummary()
                ? ", origin=" + origin + ", comment=" + comment
                    + ", enabled=" + enabled + ", CNAMEs=" + Arrays.asList(cnames)                    
                : ", config=[" + config + "]");
    }

}
