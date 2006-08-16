package hu.ismicro.commons.proximity.indexer;

import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.storage.StorageException;
import hu.ismicro.commons.proximity.storage.local.LocalStorage;

import java.util.List;

/**
 * Indexer, that keeps info and indexes items.
 * 
 * @author cstamas
 * 
 */
public interface Indexer {

    void initialize();

    void registerLocalStorage(LocalStorage storage);

    /**
     * Returns the list that this indexer thinks is searchable.
     * 
     * @return list of keywords usable in searches.
     */
    List getSearchableKeywords();
    
    void addSearchableKeywords(List keywords);
    
    void addItemProperties(ItemProperties ip) throws StorageException;

    void addItemProperties(List itemProperties) throws StorageException;

    void deleteItemProperties(ItemProperties ip) throws ItemNotFoundException, StorageException;

    List searchByItemPropertiesExample(ItemProperties ip) throws StorageException;

    List searchByQuery(String query) throws IndexerException, StorageException;

}
