package hu.ismicro.commons.proximity;

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
     * @return
     */
    InputStream getStream();
    
}
