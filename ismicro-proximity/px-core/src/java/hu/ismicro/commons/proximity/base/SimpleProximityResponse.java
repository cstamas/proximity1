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

    public boolean isMergeableResponse() {
        return false;
    }

    public void mergeResponses(ProximityResponse another) {
        throw new UnsupportedOperationException("SimpleProximityResponse is not mergeable!");
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(this.getClass().getName() + "[");
        sb.append(getItem().getPath());
        sb.append("]");
        return sb.toString();
    }

}
