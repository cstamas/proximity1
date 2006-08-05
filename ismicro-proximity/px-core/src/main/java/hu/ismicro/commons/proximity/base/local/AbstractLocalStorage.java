package hu.ismicro.commons.proximity.base.local;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.base.LocalStorage;
import hu.ismicro.commons.proximity.base.StorageException;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract Storage class. It have ID and defines logger. Predefines all write
 * methods with throwing UnsupportedOperationException-s.
 * 
 * @author cstamas
 * 
 */
public abstract class AbstractLocalStorage implements LocalStorage {

    protected Log logger = LogFactory.getLog(this.getClass());

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
