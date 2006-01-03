package hu.ismicro.commons.proximity;


import hu.ismicro.commons.proximity.base.Indexer;
import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.ProxiedItemProperties;
import hu.ismicro.commons.proximity.base.RepositoryLogic;
import hu.ismicro.commons.proximity.base.StatisticsGatherer;
import hu.ismicro.commons.proximity.base.Storage;
import hu.ismicro.commons.proximity.base.StorageException;

import java.util.List;

/**
 * Repository interface used by proximity.
 * 
 * @author cstamas
 *
 */
public interface Repository {

	/**
	 * Returns the ID of the repository.
	 * 
	 * @return
	 */
    String getId();
    
    /**
     * Sets the local storage of the repository. May be null if this is 
     * an aggregating repos without caching function.
     *  
     * @param storage
     */
    void setLocalStorage(Storage storage);

    /**
     * Sets the remote storage of the repository. May be null if this is
     * a Local repository only.
     * 
     * @param storage
     */
    void setRemoteStorage(Storage storage);

    /**
     * Sets the logic to drive this repository. The repository by default
     * uses DefaultProxyingLogic class unless overridden. May not be
     * null. 
     * 
     * @param logic
     */
    void setRepositoryLogic(RepositoryLogic logic);
    
    /**
     * Sets the indexer used by repository. May be null, to switch
     * indexing off.
     * 
     * @param indexer
     */
    void setIndexer(Indexer indexer);
    
    /**
     * Forces repository reindexing. If there is no indexer supplied with repos,
     * this call will do nothing.
     *
     */
    void reindex();
    
    /**
     * Sets the statistics gatherer. May be null, to switch stats gathering
     * off.
     * 
     * @param stats
     */
    void setStatisticsGatherer(StatisticsGatherer stats);
    
    /**
     * Retrieves the item properties from the given path.
     * 
     * @param path
     * @return
     * @throws ItemNotFoundException
     * @throws StorageException
     */
    ProxiedItemProperties retrieveItemProperties(String path) throws ItemNotFoundException, StorageException;

    /**
     * Retrieves item with content from the path.
     * 
     * @param path
     * @return
     * @throws ItemNotFoundException
     * @throws StorageException
     */
    ProxiedItem retrieveItem(String path) throws ItemNotFoundException, StorageException;
    
    /**
     * Deletes item properties from the path.
     * 
     * @param path
     * @throws StorageException
     */
    void deleteItemProperties(String path) throws StorageException;

    /**
     * Deletes item from the path.
     * @param path
     * @throws StorageException
     */
    void deleteItem(String path) throws StorageException;

    /**
     * Stores item propeties. 
     * 
     * @param itemProps
     * @throws StorageException
     */
    void storeItemProperties(ItemProperties itemProps) throws StorageException;

    /**
     * Stores item.
     * 
     * @param item
     * @throws StorageException
     */
    void storeItem(Item item) throws StorageException;

    /**
     * List items on path.
     * 
     * @param path
     * @return
     * @throws StorageException
     */
    List listItems(String path) throws StorageException;

}
