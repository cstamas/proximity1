package org.abstracthorizon.proximity.events;

import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.ProximityRequest;

public class ItemDeleteEvent extends ProximityRequestEvent {
	
	private ItemProperties itemProperties;

	public ItemDeleteEvent(ProximityRequest req, ItemProperties itemProperties) {
		super(req);
		this.itemProperties = itemProperties;
	}

	public ItemProperties getItemProperties() {
		return itemProperties;
	}

}
