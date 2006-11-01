package org.abstracthorizon.proximity.storage.local;

import java.util.List;

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.storage.AbstractStorage;
import org.abstracthorizon.proximity.storage.StorageException;

/**
 * Abstract Storage class. It have ID and defines logger. Predefines all write
 * methods with throwing UnsupportedOperationException-s.
 * 
 * @author cstamas
 * 
 */
public abstract class AbstractLocalStorage extends AbstractStorage implements LocalStorage {

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
