package org.abstracthorizon.proximity.events;

import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.ProximityRequest;

public class ItemStoreEvent extends ProximityRequestEvent {

	private ItemProperties itemProperties;

	public ItemStoreEvent(ProximityRequest req, ItemProperties itemProperties) {
		super(req);
		this.itemProperties = itemProperties;
	}

	public ItemProperties getItemProperties() {
		return itemProperties;
	}

}
