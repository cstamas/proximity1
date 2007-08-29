/*

   Copyright 2005-2007 Tamas Cservenak (t.cservenak@gmail.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.abstracthorizon.proximity.webapp.access;

import java.util.ArrayList;
import java.util.List;

import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.access.AccessDecisionVoter;
import org.abstracthorizon.proximity.access.RepositoryPermission;
import org.apache.commons.io.FilenameUtils;

// TODO: Auto-generated Javadoc
/**
 * AccessDecisionVoter created for Demo Proximity site. It will forbid JAR retrieval request unless coming from a known
 * site.
 * 
 * @author cstamas
 */
public class DemoSiteAccessDecisionVoter
    implements AccessDecisionVoter
{

    /** The allowed ips. */
    private List allowedIps = new ArrayList();

    /**
     * Gets the allowed ips.
     * 
     * @return the allowed ips
     */
    public List getAllowedIps()
    {
        return allowedIps;
    }

    /**
     * Sets the allowed ips.
     * 
     * @param allowedIps the new allowed ips
     */
    public void setAllowedIps( List allowedIps )
    {
        this.allowedIps = allowedIps;
    }

    /* (non-Javadoc)
     * @see org.abstracthorizon.proximity.access.AccessDecisionVoter#vote(org.abstracthorizon.proximity.ProximityRequest, java.util.Map)
     */
    public int vote( ProximityRequest request, Repository repository, RepositoryPermission permission )
    {
        // we are forbidding JAR download
        if ( request.getAttributes().containsKey( ProximityRequest.REQUEST_REMOTE_ADDRESS )
            && FilenameUtils.getExtension( request.getPath() ).equalsIgnoreCase( "jar" ) )
        {

            // but allowing it to known IPs
            if ( allowedIps.contains( (String) request.getAttributes().get( ProximityRequest.REQUEST_REMOTE_ADDRESS ) ) )
            {
                return ACCESS_APPROVED;
            }
            return ACCESS_DENIED;
        }
        else
        {
            return ACCESS_APPROVED;
        }
    }

}
