package org.abstracthorizon.proximity;

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

	public static final String REQUEST_REMOTE_ADDRESS = "request.remoteAddr";

	private static final long serialVersionUID = -3576402488409590517L;

	/**
	 * The grantee who is authenticated against infrastructure that holds
	 * Proximity Core
	 */
	private Object grantee;

	/** The Path of the request */
	private String path;

	/** If true, will not try to fetch from remote */
	private boolean localOnly = false;

	/** If true, no content will be supplied just ItemProperties */
	private boolean propertiesOnly = false;

	/** The ID of the targeted repos if any or null */
	private String targetedReposId = null;

	/** The ID of the targeted repos group if any or null */
	private String targetedReposGroupId = null;

	/** Map of attributes if any */
	private Map attributes = new HashMap();

	public ProximityRequest() {
		super();
	}

	public ProximityRequest(String path) {
		this();
		setPath(path);
	}

	public ProximityRequest(ProximityRequest rq) {
		this(rq.getPath());
		setGrantee(rq.getGrantee());
		setTargetedReposId(rq.getTargetedReposId());
		setTargetedReposGroupId(rq.getTargetedReposGroupId());
		getAttributes().putAll(rq.getAttributes());
	}

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

	public boolean isLocalOnly() {
		return localOnly;
	}

	public void setLocalOnly(boolean localOnly) {
		this.localOnly = localOnly;
	}

	public boolean isPropertiesOnly() {
		return propertiesOnly;
	}

	public void setPropertiesOnly(boolean propertiesOnly) {
		this.propertiesOnly = propertiesOnly;
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

	public String getTargetedReposGroupId() {
		return targetedReposGroupId;
	}

	public void setTargetedReposGroupId(String targetedReposGroupId) {
		this.targetedReposGroupId = targetedReposGroupId;
	}

	public String toString() {
		StringBuffer str = new StringBuffer("ProximityRequest[");
		str.append("grantee=");
		str.append(getGrantee());
		str.append(", path=");
		str.append(getPath());
		str.append(", targetedReposId=");
		str.append(getTargetedReposId());
		str.append(", targetedReposGroupId=");
		str.append(getTargetedReposGroupId());
		str.append(", attributes=");
		str.append(getAttributes());
		str.append("]");
		return str.toString();
	}

}
