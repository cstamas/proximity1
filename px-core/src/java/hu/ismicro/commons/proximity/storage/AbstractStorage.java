package hu.ismicro.commons.proximity.storage;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.base.Storage;
import hu.ismicro.commons.proximity.base.StorageException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractStorage implements Storage {

    protected Log logger = LogFactory.getLog(this.getClass());

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isWritable() {
        return false;
    }

    public void storeItem(Item item) throws StorageException {
        throw new UnsupportedOperationException("The " + getClass().getName() + " storage is ReadOnly!");
    }

    public void deleteItem(String path) throws StorageException {
        throw new UnsupportedOperationException("The " + getClass().getName() + " storage is ReadOnly!");
    }

}
