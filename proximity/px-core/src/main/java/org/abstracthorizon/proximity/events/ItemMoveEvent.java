package org.abstracthorizon.proximity.events;

import org.abstracthorizon.proximity.ProximityRequest;

public class ItemMoveEvent extends ProximityRequestEvent {

    private ProximityRequest target;

    public ItemMoveEvent(ProximityRequest source, ProximityRequest target) {
	super(source);
	this.getTarget();
    }

    public ProximityRequest getSource() {
	return getRequest();
    }

    public ProximityRequest getTarget() {
	return target;
    }

}
