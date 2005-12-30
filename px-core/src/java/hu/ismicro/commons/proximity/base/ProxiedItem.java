package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ItemProperties;

import java.io.InputStream;

public class ProxiedItem implements Item {
    
    private ItemProperties properties;
    
    private InputStream stream;

    public ItemProperties getProperties() {
        return properties;
    }

    public void setProperties(ItemProperties properties) {
        this.properties = properties;
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }
    
    public String toString() {
    		return getProperties().toString();
    }

}
