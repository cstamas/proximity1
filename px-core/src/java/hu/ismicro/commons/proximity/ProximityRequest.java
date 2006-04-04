package hu.ismicro.commons.proximity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Object encapsulating Proximity request.
 * 
 * @author t.cservenak
 *
 */
public class ProximityRequest implements Serializable {
	
	/** The grantee who is authenticated against infrastructure that holds Proximity Core */
	private Object grantee;
	
	/** The Path of the request */
	private String path;

	/** The ID of the targeted repos if any or null */
	private String targetedReposId = null;

	/** Map of attributes if any */
	private Map attributes = new HashMap();

	public Object getGrantee() {
		return grantee;
	}

	public void setGrantee(Object grantee) {
		this.grantee = grantee;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Map getAttributes() {
		return attributes;
	}

	public void setAttributes(Map attributes) {
		this.attributes = attributes;
	}

	public String getTargetedReposId() {
		return targetedReposId;
	}

	public void setTargetedReposId(String targetedReposId) {
		this.targetedReposId = targetedReposId;
	}

}
