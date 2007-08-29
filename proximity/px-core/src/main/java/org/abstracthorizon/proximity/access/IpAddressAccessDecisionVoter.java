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
package org.abstracthorizon.proximity.access;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;

// TODO: Auto-generated Javadoc
/**
 * The Class IpAddressAccessDecisionVoter.
 */
public class IpAddressAccessDecisionVoter
    implements AccessDecisionVoter
{
    
    /** The allow from pattern. */
    private String allowFromPattern;

    /** The deny from pattern. */
    private String denyFromPattern;

    /** The allow deny. */
    private boolean allowDeny;

    /** The allow from. */
    private Pattern allowFrom;

    /** The deny from. */
    private Pattern denyFrom;

    /**
     * Checks if is allow deny.
     * 
     * @return true, if is allow deny
     */
    public boolean isAllowDeny()
    {
        return allowDeny;
    }

    /**
     * Sets the allow deny.
     * 
     * @param allowDeny the new allow deny
     */
    public void setAllowDeny( boolean allowDeny )
    {
        this.allowDeny = allowDeny;
    }

    /**
     * Gets the allow from pattern.
     * 
     * @return the allow from pattern
     */
    public String getAllowFromPattern()
    {
        return allowFromPattern;
    }

    /**
     * Sets the allow from pattern.
     * 
     * @param allowFromPattern the new allow from pattern
     */
    public void setAllowFromPattern( String allowFromPattern )
    {
        this.allowFromPattern = allowFromPattern;
        allowFrom = Pattern.compile( this.allowFromPattern );
    }

    /**
     * Gets the deny from pattern.
     * 
     * @return the deny from pattern
     */
    public String getDenyFromPattern()
    {
        return denyFromPattern;
    }

    /**
     * Sets the deny from pattern.
     * 
     * @param denyFromPattern the new deny from pattern
     */
    public void setDenyFromPattern( String denyFromPattern )
    {
        this.denyFromPattern = denyFromPattern;
        denyFrom = Pattern.compile( this.denyFromPattern );
    }

    /* (non-Javadoc)
     * @see org.abstracthorizon.proximity.access.AccessDecisionVoter#vote(org.abstracthorizon.proximity.ProximityRequest, org.abstracthorizon.proximity.Repository, org.abstracthorizon.proximity.access.RepositoryPermission)
     */
    public int vote( ProximityRequest request, Repository repository, RepositoryPermission permission )
    {
        if ( request.getAttributes().containsKey( ProximityRequest.REQUEST_REMOTE_ADDRESS )
            && isAccessAllowed( (String) request.getAttributes().get( ProximityRequest.REQUEST_REMOTE_ADDRESS ) ) )
        {
            return ACCESS_APPROVED;
        }
        else
        {
            return ACCESS_DENIED;
        }
    }

    /**
     * Checks if is access allowed.
     * 
     * @param ipAddress the ip address
     * 
     * @return true, if is access allowed
     */
    private boolean isAccessAllowed( String ipAddress )
    {
        Matcher allowMatcher = allowFrom.matcher( ipAddress );
        Matcher denyMatcher = denyFrom.matcher( ipAddress );
        if ( isAllowDeny() )
        {
            if ( allowMatcher.matches() )
            {
                return true;
            }
            if ( denyMatcher.matches() )
            {
                return false;
            }
            return false;
        }
        else
        {
            if ( denyMatcher.matches() )
            {
                return false;
            }
            if ( allowMatcher.matches() )
            {
                return true;
            }
            return true;
        }
    }

}
