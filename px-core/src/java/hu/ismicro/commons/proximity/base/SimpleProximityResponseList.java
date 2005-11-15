package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ProximityResponseList;

import java.util.ArrayList;
import java.util.List;

public class SimpleProximityResponseList implements ProximityResponseList {
    
    private List items; 

    public Item getItem() {
        throw new UnsupportedOperationException("SimpleProximityResponseList does not support getItem() method.");
    }

    public void setItems(List items) {
        this.items = items;
    }

    public List getItems() {
        if (this.items == null) {
            this.items = new ArrayList();
        }
        return items;
    }

}
