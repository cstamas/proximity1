package hu.ismicro.commons.proximity.access;

import hu.ismicro.commons.proximity.AccessDeniedException;
import hu.ismicro.commons.proximity.ProximityRequest;

import java.util.Map;

/**
 * Interface for access manager.
 * 
 * @author t.cservenak
 *
 */
public interface AccessManager {
	
	/**
	 * The implementation of this method should throw AccessDeniedException or any
	 * subclass if it denies access.
	 * 
	 * @param grantee
	 * @param request
	 * @param config
	 * @throws AccessDeniedException
	 */
	void decide(ProximityRequest request, Map config) throws AccessDeniedException;

}
