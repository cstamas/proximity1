package org.abstracthorizon.proximity.storage;

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.metadata.ProxiedItemPropertiesFactory;

/**
 * A storage abstraction.
 * 
 * @author cstamas
 * 
 */
public interface Storage {

    boolean containsItem(String path) throws StorageException;

    Item retrieveItem(String path, boolean propsOnly) throws ItemNotFoundException, StorageException;

    ProxiedItemPropertiesFactory getProxiedItemPropertiesFactory();

    void setProxiedItemPropertiesFactory(ProxiedItemPropertiesFactory pic);

}
