package org.abstracthorizon.proximity.storage;

import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.impl.ItemImpl;
import org.abstracthorizon.proximity.metadata.ProxiedItemPropertiesFactory;

/**
 * A storage abstraction.
 * 
 * @author cstamas
 * 
 */
public interface Storage {

    boolean containsItem(String path) throws StorageException;

    ItemImpl retrieveItem(String path, boolean propsOnly) throws ItemNotFoundException, StorageException;

    ProxiedItemPropertiesFactory getProxiedItemPropertiesFactory();

    void setProxiedItemPropertiesFactory(ProxiedItemPropertiesFactory pic);
    
}
