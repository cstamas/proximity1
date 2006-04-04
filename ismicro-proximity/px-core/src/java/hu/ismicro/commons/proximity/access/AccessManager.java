package hu.ismicro.commons.proximity.access;

import hu.ismicro.commons.proximity.ProximityRequest;

import java.util.Map;

public interface AccessManager {
	
	void decide(Object grantee, ProximityRequest request, Map config) throws AccessDeniedException;

}
