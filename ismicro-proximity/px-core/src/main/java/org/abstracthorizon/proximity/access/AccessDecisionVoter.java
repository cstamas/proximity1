package org.abstracthorizon.proximity.access;


import java.util.Map;

import org.abstracthorizon.proximity.ProximityRequest;

public interface AccessDecisionVoter {

    /** Vote for approval */
    static final int ACCESS_APPROVED = 1;

    /** Vote for neutral status */
    static final int ACCESS_NEUTRAL = 0;

    /** Vote for denial */
    static final int ACCESS_DENIED = -1;

    /**
     * The implementation of this method should return one of the
     * ACCESS_APPROVED, ACCESS_NEUTRAL or ACCESS_DENIED constants.
     * 
     * @return The vote for this access.
     */
    int vote(ProximityRequest request, Map attribs);

}
