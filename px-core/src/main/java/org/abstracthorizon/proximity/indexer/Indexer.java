package org.abstracthorizon.proximity.indexer;


import java.util.List;

import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.storage.StorageException;

/**
 * Indexer, that keeps info and indexes items.
 * 
 * @author cstamas
 * 
 */
public interface Indexer {

    void initialize();

    void registerRepository(Repository repository);

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
