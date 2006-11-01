package org.abstracthorizon.proximity.access;

import java.util.Map;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.ProximityRequest;

/**
 * A simple AccessManager implementation that allows everybody to access the
 * Proximity core.
 * 
 * @author t.cservenak
 * 
 */
public class OpenAccessManager implements AccessManager {

	public void decide(ProximityRequest request, Map config) throws AccessDeniedException {
		// this access manager is open, everybody has access to everything since
		// it never throws AccessDeniedEx
	}

}
