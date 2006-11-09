package org.abstracthorizon.proximity.events;

import org.abstracthorizon.proximity.ProximityRequest;

public abstract class ProximityRequestEvent extends ProximityEvent {
	
	private ProximityRequest request;
	
	public ProximityRequestEvent(ProximityRequest req) {
		super();
		this.request = req;
	}

	public ProximityRequest getRequest() {
		return request;
	}

}
