package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ItemProperties;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProxiedItem implements Item {

    protected Log logger = LogFactory.getLog(this.getClass());

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

    public void close() {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                logger.warn("Had a problem trying to close a file: " + e);
            }
        }
    }
}
