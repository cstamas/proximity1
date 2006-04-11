package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.ItemProperties;

import java.util.List;

/**
 * Indexer, that keeps info and indexes items.
 * 
 * @author cstamas
 *
 */
public interface Indexer {
	
	void reindexingStarted();
	
	void reindexingFinished();
	
    void addItemProperties(String UID, ItemProperties ip) throws StorageException;
    
    void deleteItemProperties(String UID, ItemProperties ip) throws ItemNotFoundException, StorageException;

	List searchByItemPropertiesExample(ItemProperties ip) throws StorageException;
    
}
