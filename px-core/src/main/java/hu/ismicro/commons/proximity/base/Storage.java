package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemNotFoundException;

/**
 * A storage abstraction.
 * 
 * @author cstamas
 * 
 */
public interface Storage {

    boolean containsItem(String path) throws StorageException;

    ProxiedItem retrieveItem(String path, boolean propsOnly) throws ItemNotFoundException, StorageException;

    ProxiedItemPropertiesFactory getProxiedItemPropertiesFactory();

    void setProxiedItemPropertiesFactory(ProxiedItemPropertiesFactory pic);
    
}
