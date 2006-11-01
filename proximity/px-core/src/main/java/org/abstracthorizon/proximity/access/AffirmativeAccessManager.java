package org.abstracthorizon.proximity.access;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.ProximityRequest;

/**
 * Simple implementation of AccessManager that passes only if all voter votes
 * ACCESS_APPROVED.
 * 
 * @author t.cservenak
 * 
 */
public class AffirmativeAccessManager implements AccessManager {

	private List voters = new ArrayList();

	public List getVoters() {
		return voters;
	}

	public void setVoters(List voters) {
		this.voters = voters;
	}

	public void decide(ProximityRequest request, Map config) throws AccessDeniedException {
		for (Iterator i = voters.iterator(); i.hasNext();) {
			AccessDecisionVoter voter = (AccessDecisionVoter) i.next();
			if (voter.vote(request, config) != AccessDecisionVoter.ACCESS_APPROVED) {
				throw new AccessDeniedException(request, "Voter " + voter.getClass().getName()
						+ " has voted against access.");
			}
		}

	}

}
