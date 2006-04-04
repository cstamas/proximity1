package hu.ismicro.commons.proximity.access;

import hu.ismicro.commons.proximity.ProximityRequest;

import java.util.Map;

public interface AccessDecisionVoter {
	
	static final int ACCESS_APPROVED = 1;
	
	static final int ACCESS_NEUTRAL = 0;

	static final int ACCESS_DENIED = -1;
	
	int vote(Object grantee, ProximityRequest request, Map attribs);

}
