package org.abstracthorizon.proximity.webapp.access;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.access.AccessDecisionVoter;
import org.apache.commons.io.FilenameUtils;

/**
 * AccessDecisionVoter created for Demo Proximity site. It will forbid JAR
 * retrieval request unless coming from a known site.
 * 
 * @author cstamas
 * 
 */
public class DemoSiteAccessDecisionVoter implements AccessDecisionVoter {

    private List allowedIps = new ArrayList();

    public List getAllowedIps() {
        return allowedIps;
    }

    public void setAllowedIps(List allowedIps) {
        this.allowedIps = allowedIps;
    }

    public int vote(ProximityRequest request, Map attribs) {
        // we are forbidding JAR download
        if (request.getAttributes().containsKey(ProximityRequest.REQUEST_REMOTE_ADDRESS)
                && FilenameUtils.getExtension(request.getPath()).equalsIgnoreCase("jar")) {
            
            // but allowing it to known IPs
            if (allowedIps.contains((String) request.getAttributes().get(ProximityRequest.REQUEST_REMOTE_ADDRESS))) {
                return ACCESS_APPROVED;
            }
            return ACCESS_DENIED;
        } else {
            return ACCESS_APPROVED;
        }
    }

}
