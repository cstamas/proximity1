package org.abstracthorizon.proximity;

import java.io.InputStream;

/**
 * An Item abstraction with properties and content.
 * 
 */
public class Item {

    private ItemProperties properties;

    private InputStream stream;

    /**
         * Returns the item properties.
         * 
         * @return
         */
    public ItemProperties getProperties() {
	return properties;
    }

    /**
         * Sets the item properties.
         * 
         * @param properties
         */
    public void setProperties(ItemProperties properties) {
	this.properties = properties;
    }

    /**
         * Returns the item input stream.
         * 
         * @return
         */
    public InputStream getStream() {
	return stream;
    }

    /**
         * Sets item input stream.
         * 
         * @param stream
         */
    public void setStream(InputStream stream) {
	this.stream = stream;
    }

    /**
         * For Debug, returns getProperties.toString();
         * 
         */
    public String toString() {
	if (getProperties() != null) {
	    return getProperties().toString();
	} else {
	    return "Item";
	}
    }

}
