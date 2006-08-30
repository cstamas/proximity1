package org.abstracthorizon.proximity.impl;


import java.io.InputStream;

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemImpl implements Item {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

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
