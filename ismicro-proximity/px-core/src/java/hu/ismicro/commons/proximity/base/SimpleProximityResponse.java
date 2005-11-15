package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ProximityResponse;

public class SimpleProximityResponse implements ProximityResponse {
    
    private Item item;

    public void setItem(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public void mergeResponses(ProximityResponse another) {
        throw new UnsupportedOperationException("SimpleProximityResponse is not mergeable!");
    }

}
