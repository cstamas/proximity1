package org.abstracthorizon.proximity;

import java.io.InputStream;

/**
 * The Item abstraction, with content.
 * 
 * @author cstamas
 * 
 */
public interface Item {

    /**
     * Returns the Item properties.
     * 
     * @return
     */
    ItemProperties getProperties();

    /**
     * Returns the content of the Item.
     * 
     * @return
     */
    InputStream getStream();

    /**
     * Sets the content stream of the item.
     * 
     * @param stream
     */
    void setStream(InputStream stream);

}
