package hu.ismicro.commons.proximity.access;

import hu.ismicro.commons.proximity.AccessDeniedException;
import hu.ismicro.commons.proximity.ProximityRequest;

import java.util.Map;

/**
 * A simple AccessManager implementation that allows everybody to access the Proximity core.
 *  
 * @author t.cservenak
 *
 */
public class OpenAccessManager implements AccessManager {

	public void decide(Object grantee, ProximityRequest request, Map config) throws AccessDeniedException {
		// this access manager is open, everybody has access to everything since it never throws AccessDeniedEx 
	}

}
