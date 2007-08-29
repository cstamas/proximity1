package org.abstracthorizon.proximity.access;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;

/**
 * Simple implementation of AccessManager that passes only if all voter votes ACCESS_APPROVED.
 * 
 * @author t.cservenak
 */
public class AffirmativeAccessManager
    implements AccessManager
{

    private List voters = new ArrayList();

    public List getVoters()
    {
        return voters;
    }

    public void setVoters( List voters )
    {
        this.voters = voters;
    }

    public void decide( ProximityRequest request, Repository repository, RepositoryPermission permission )
        throws AccessDeniedException
    {
        for ( Iterator i = voters.iterator(); i.hasNext(); )
        {
            AccessDecisionVoter voter = (AccessDecisionVoter) i.next();
            if ( voter.vote( request, repository, permission ) != AccessDecisionVoter.ACCESS_APPROVED )
            {
                throw new AccessDeniedException( request, "Voter " + voter.getClass().getName()
                    + " has voted against access." );
            }
        }

    }

}
