package hu.ismicro.commons.proximity.access;

import hu.ismicro.commons.proximity.ProximityRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Decide passes only if all voter votes ACCESS_APPROVED.
 * 
 * @author t.cservenak
 *
 */
public class AffirmativeAccessManager implements AccessManager {
	
	private List voters = new ArrayList();

	public void decide(Object grantee, ProximityRequest request, Map config) throws AccessDeniedException {
		for (Iterator i = voters.iterator(); i.hasNext(); ) {
			AccessDecisionVoter voter = (AccessDecisionVoter) i.next();
			if (voter.vote(grantee, request, config) != AccessDecisionVoter.ACCESS_APPROVED) {
				throw new AccessDeniedException(grantee, request, "Voter " + voter.getClass().getName() + " has voted against access.");
			}
		}

	}

}
