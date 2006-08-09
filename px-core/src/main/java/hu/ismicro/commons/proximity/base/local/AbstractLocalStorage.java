package hu.ismicro.commons.proximity.base.local;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.base.DefaultProxiedItemPropertiesConstructor;
import hu.ismicro.commons.proximity.base.LocalStorage;
import hu.ismicro.commons.proximity.base.ProxiedItemPropertiesConstructor;
import hu.ismicro.commons.proximity.base.StorageException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Storage class. It have ID and defines logger. Predefines all write
 * methods with throwing UnsupportedOperationException-s.
 * 
 * @author cstamas
 * 
 */
public abstract class AbstractLocalStorage implements LocalStorage {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected ProxiedItemPropertiesConstructor proxiedItemPropertiesConstructor = new DefaultProxiedItemPropertiesConstructor();

    public void setProxiedItemPropertiesConstructor(ProxiedItemPropertiesConstructor pic) {
        this.proxiedItemPropertiesConstructor = pic;
    }
    
    public ProxiedItemPropertiesConstructor getProxiedItemPropertiesConstructor() {
        return this.proxiedItemPropertiesConstructor;
    }
    
    public boolean isWritable() {
        return false;
    }

    public List listItems(String path) throws StorageException {
        throw new UnsupportedOperationException("The " + getClass().getName() + " storage is not listable!");
    }

    public void storeItem(Item item) throws StorageException {
        throw new UnsupportedOperationException("The " + getClass().getName() + " storage is ReadOnly!");
    }

    public void deleteItem(String path) throws StorageException {
        throw new UnsupportedOperationException("The " + getClass().getName() + " storage is ReadOnly!");
    }

}
