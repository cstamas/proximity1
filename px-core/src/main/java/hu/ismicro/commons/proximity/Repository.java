package hu.ismicro.commons.proximity;

import hu.ismicro.commons.proximity.access.AccessManager;
import hu.ismicro.commons.proximity.base.Indexer;
import hu.ismicro.commons.proximity.base.LocalStorage;
import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.RemoteStorage;
import hu.ismicro.commons.proximity.base.RepositoryLogic;
import hu.ismicro.commons.proximity.base.StatisticsGatherer;
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
     * Sets the ID of the repository. It should be unique Proximity-wide.
     * 
     * @param id the ID of the repo.
     */
    void setId(String id);

    /**
     * Returns the group ID of the repository.
     * 
     * @return
     */
    String getGroupId();

    /**
     * Sets the groupID of the repository.
     * 
     * @param groupId
     */
    void setGroupId(String groupId);

    /**
     * Is Repository available? If no, it will reject all incoming requests.
     * 
     * @return
     */
    boolean isAvailable();

    /**
     * Sets the repos availability. If repo is unavailable, all requests will be rejected.
     * 
     * @param val
     */
    void setAvailable(boolean val);

    /**
     * Is repository offline? If yes, it will not try to retrieve from remote
     * peer, it will serve only locally found stuff.
     * 
     * @return
     */
    boolean isOffline();

    /**
     * Sets the offline status of the repository.
     * 
     * @param val
     */
    void setOffline(boolean val);

    /**
     * Is Repository listable?
     * 
     * @return true if is listable, otherwise false.
     */
    boolean isListable();

    /**
     * Sets the listable property of repository. If true, its content will be returned
     * by listItems method, otherwise not. The retrieveItem will still function and
     * return the requested item.
     * 
     * @param val
     */
    void setListable(boolean val);

    /**
     * Returns the local storage of the repository.
     * 
     * @return localStorage or null.
     */
    LocalStorage getLocalStorage();

    /**
     * Sets the local storage of the repository. May be null if this is an
     * aggregating repos without caching function.
     * 
     * @param storage
     */
    void setLocalStorage(LocalStorage storage);

    /**
     * Returns the remoteStorage of the reposity.
     * 
     * @return remoteStorage or null. 
     */
    RemoteStorage getRemoteStorage();

    /**
     * Sets the remote storage of the repository. May be null if this is a Local
     * repository only.
     * 
     * @param storage
     */
    void setRemoteStorage(RemoteStorage storage);

    /**
     * Sets the repository logic to drive this repository.
     * 
     * @return
     */
    RepositoryLogic getRepositoryLogic();

    /**
     * Sets the logic to drive this repository. The repository by default uses
     * DefaultProxyingLogic class unless overridden. May not be null.
     * 
     * @param logic
     */
    void setRepositoryLogic(RepositoryLogic logic);

    /**
     * Returns the indexer used by this repository.
     * 
     * @return
     */
    Indexer getIndexer();

    /**
     * Sets the indexer used by repository. May be null, to switch indexing off.
     * 
     * @param indexer
     */
    void setIndexer(Indexer indexer);

    /**
     * Returns the repository level AccessManager.
     * 
     * @return
     */
    AccessManager getAccessManager();

    /**
     * Sets the repository level AccessManager.
     * 
     * @param accessManager
     */
    void setAccessManager(AccessManager accessManager);

    /**
     * Initializes repository. Reindexing, recreating metadata, etc...
     * 
     */
    void initialize();

    /**
     * Forces reindex of repository.
     * 
     */
    void reindex();

    /**
     * Gets the stats gatherer of repository.
     * 
     * @return
     */
    StatisticsGatherer getStatisticsGatherer();
    
    /**
     * Sets the statistics gatherer. May be null, to switch stats gathering off.
     * 
     * @param stats
     */
    void setStatisticsGatherer(StatisticsGatherer stats);

    /**
     * Retrieves item with content from the path.
     * 
     * @param path
     * @return
     * @throws ItemNotFoundException
     * @throws StorageException
     */
    ProxiedItem retrieveItem(ProximityRequest request) throws RepositoryNotAvailableException, ItemNotFoundException,
            StorageException, AccessDeniedException;

    /**
     * Deletes item from the path.
     * 
     * @param path
     * @throws StorageException
     */
    void deleteItem(String path) throws RepositoryNotAvailableException, StorageException;

    /**
     * Stores item.
     * 
     * @param item
     * @throws StorageException
     */
    void storeItem(Item item) throws RepositoryNotAvailableException, StorageException;

    /**
     * List items on path. If not listable, an empty list.
     * 
     * @param path
     * @return list, with items or empty if not listable.
     * @throws StorageException
     */
    List listItems(ProximityRequest request) throws RepositoryNotAvailableException, StorageException;

}
