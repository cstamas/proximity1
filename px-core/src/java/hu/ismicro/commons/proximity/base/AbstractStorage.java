package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ItemProperties;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractStorage implements Storage {

	protected Log logger = LogFactory.getLog(this.getClass());

	private String id;
	
	private boolean metadataAware = true;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isMetadataAware() {
		return metadataAware;
	}

	public void setMetadataAware(boolean metadataAware) {
		this.metadataAware = metadataAware;
	}

	public boolean isWritable() {
		return false;
	}

	public List listItems(String path) throws StorageException {
        throw new UnsupportedOperationException("The " + getClass().getName() + " storage is not listable!");
	}

	public void storeItemProperties(ItemProperties item) throws StorageException {
        throw new UnsupportedOperationException("The " + getClass().getName() + " storage is ReadOnly!");
	}

	public void storeItem(Item item) throws StorageException {
        throw new UnsupportedOperationException("The " + getClass().getName() + " storage is ReadOnly!");
	}

	public void deleteItem(String path) throws StorageException {
        throw new UnsupportedOperationException("The " + getClass().getName() + " storage is ReadOnly!");
	}

}
