package org.abstracthorizon.proximity;

import org.abstracthorizon.proximity.events.ProximityRequestEvent;

public interface ProximityRequestListener {
	
	public void proximityRequestEvent(ProximityRequestEvent event);

}
