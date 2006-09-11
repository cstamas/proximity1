package org.abstracthorizon.proximity.webapp.access;


import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.access.AccessDecisionVoter;

public class IpAddressAccessDecisionVoter implements AccessDecisionVoter {

    private String allowFromPattern;

    private String denyFromPattern;

    private boolean allowDeny;

    private Pattern allowFrom;

    private Pattern denyFrom;

    public boolean isAllowDeny() {
        return allowDeny;
    }

    public void setAllowDeny(boolean allowDeny) {
        this.allowDeny = allowDeny;
    }

    public String getAllowFromPattern() {
        return allowFromPattern;
    }

    public void setAllowFromPattern(String allowFromPattern) {
        this.allowFromPattern = allowFromPattern;
        allowFrom = Pattern.compile(this.allowFromPattern);
    }

    public String getDenyFromPattern() {
        return denyFromPattern;
    }

    public void setDenyFromPattern(String denyFromPattern) {
        this.denyFromPattern = denyFromPattern;
        denyFrom = Pattern.compile(this.denyFromPattern);
    }

    public int vote(ProximityRequest request, Map attribs) {
        if (request.getAttributes().containsKey(ProximityRequest.REQUEST_REMOTE_ADDRESS)
                && isAccessAllowed((String) request.getAttributes().get(ProximityRequest.REQUEST_REMOTE_ADDRESS))) {
            return ACCESS_APPROVED;
        } else {
            return ACCESS_DENIED;
        }
    }

    private boolean isAccessAllowed(String ipAddress) {
        Matcher allowMatcher = allowFrom.matcher(ipAddress);
        Matcher denyMatcher = denyFrom.matcher(ipAddress);
        if (isAllowDeny()) {
            if (allowMatcher.matches()) {
                return true;
            }
            if (denyMatcher.matches()) {
                return false;
            }
            return false;
        } else {
            if (denyMatcher.matches()) {
                return false;
            }
            if (allowMatcher.matches()) {
                return true;
            }
            return true;
        }
    }

}
