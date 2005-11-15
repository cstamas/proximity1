package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ProximityResponse;
import hu.ismicro.commons.proximity.ProximityResponseList;

import java.util.ArrayList;
import java.util.Iterator;
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

	public boolean isMergeableResponse() {
		return true;
	}

    public void mergeResponses(ProximityResponse another) {
        if (another instanceof ProximityResponseList) {
            this.getItems().addAll(((ProximityResponseList)another).getItems());
        } else {
            throw new IllegalArgumentException(this.getClass().getName() + " is not mergeable with class " + another.getClass().getName());
        }
    }
    
    public String toString() {
    	StringBuffer sb = new StringBuffer(this.getClass().getName() + "[");
    	for (Iterator i = getItems().iterator(); i.hasNext(); ) {
    		if (!sb.toString().endsWith("[")) {
    			sb.append(",");
    		}
    		sb.append(((Item)i.next()).getPath());
    	}
    	sb.append("]");
    	return sb.toString();
    }

}
