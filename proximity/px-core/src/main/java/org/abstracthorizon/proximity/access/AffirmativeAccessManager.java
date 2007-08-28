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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.ProximityRequest;

// TODO: Auto-generated Javadoc
/**
 * Simple implementation of AccessManager that passes only if all voter votes ACCESS_APPROVED.
 * 
 * @author cstamas
 */
public class AffirmativeAccessManager
    implements AccessManager
{

    /** The voters. */
    private List voters = new ArrayList();

    /**
     * Gets the voters.
     * 
     * @return the voters
     */
    public List getVoters()
    {
        return voters;
    }

    /**
     * Sets the voters.
     * 
     * @param voters the new voters
     */
    public void setVoters( List voters )
    {
        this.voters = voters;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.access.AccessManager#decide(org.abstracthorizon.proximity.ProximityRequest,
     *      java.util.Map)
     */
    public void decide( ProximityRequest request, Map config )
        throws AccessDeniedException
    {
        for ( Iterator i = voters.iterator(); i.hasNext(); )
        {
            AccessDecisionVoter voter = (AccessDecisionVoter) i.next();
            if ( voter.vote( request, config ) != AccessDecisionVoter.ACCESS_APPROVED )
            {
                throw new AccessDeniedException( request, "Voter " + voter.getClass().getName()
                    + " has voted against access." );
            }
        }

    }

}
