package hu.ismicro.commons.proximity.storage;

import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.impl.ItemImpl;
import hu.ismicro.commons.proximity.metadata.ProxiedItemPropertiesFactory;

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
