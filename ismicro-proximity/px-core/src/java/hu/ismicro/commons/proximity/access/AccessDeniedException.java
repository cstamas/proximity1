package hu.ismicro.commons.proximity.access;

import hu.ismicro.commons.proximity.ProximityException;
import hu.ismicro.commons.proximity.ProximityRequest;

/**
 * Thrown when a Proximity request is not backed by trusted/granted authority.
 * 
 * @author t.cservenak
 *
 */
public class AccessDeniedException extends ProximityException {
	
	private Object grantee;
	
	private ProximityRequest request;

	public AccessDeniedException(Object grantee, ProximityRequest request, String msg) {
		super(msg);
	}
	
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("Access for ");
		str.append(grantee.toString());
		str.append(" to resource ");
		str.append(request.getPath());
		str.append(" has been forbidden because:");
		str.append(super.toString());
		return str.toString();
	}

}
