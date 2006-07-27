package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.ItemProperties;

import java.util.List;
import java.util.Map;

/**
 * Indexer, that keeps info and indexes items.
 * 
 * @author cstamas
 * 
 */
public interface Indexer {

    void initialize();

    void registerLocalStorage(Storage storage);

    void addItemProperties(String UID, ItemProperties ip) throws StorageException;

    void addItemProperties(Map uidsWithItems) throws StorageException;

    void deleteItemProperties(String UID, ItemProperties ip) throws ItemNotFoundException, StorageException;

    List getSearchableKeywords();

    List searchByItemPropertiesExample(ItemProperties ip) throws StorageException;

    List searchByQuery(String query) throws IndexerException, StorageException;

}
