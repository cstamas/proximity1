package org.abstracthorizon.proximity.events;

import org.abstracthorizon.proximity.ProximityRequest;

public class ItemCopyEvent extends ProximityRequestEvent {
	
	private ProximityRequest target;

	public ItemCopyEvent(ProximityRequest source, ProximityRequest target) {
		super(source);
		this.target = target;
	}

	public ProximityRequest getSource() {
		return getRequest();
	}

	public ProximityRequest getTarget() {
		return target;
	}

}
