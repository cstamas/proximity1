package org.abstracthorizon.proximity.access;

import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;

public interface AccessDecisionVoter
{

    /**
     * Vote for approval
     */
    static final int ACCESS_APPROVED = 1;

    /**
     * Vote for neutral status
     */
    static final int ACCESS_NEUTRAL = 0;

    /**
     * Vote for denial
     */
    static final int ACCESS_DENIED = -1;

    /**
     * The implementation of this method should return one of the ACCESS_APPROVED, ACCESS_NEUTRAL or ACCESS_DENIED
     * constants.
     * 
     * @param request
     * @param repository
     * @param permission
     * @return The vote for this access.
     */
    int vote( ProximityRequest request, Repository repository, RepositoryPermission permission );

}
