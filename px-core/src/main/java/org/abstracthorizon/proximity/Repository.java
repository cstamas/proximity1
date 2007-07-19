package org.abstracthorizon.proximity;

import java.util.List;

import org.abstracthorizon.proximity.access.AccessManager;
import org.abstracthorizon.proximity.storage.StorageException;
import org.abstracthorizon.proximity.storage.local.LocalStorage;
import org.abstracthorizon.proximity.storage.remote.RemoteStorage;

/**
 * Repository interface used by proximity.
 * 
 * @author cstamas
 * 
 */
public interface Repository extends ProximityRequestMulticaster {

    /**
         * Returns the ID of the repository.
         * 
         * @return
         */
    String getId();

    /**
         * Sets the ID of the repository. It should be unique Proximity-wide.
         * 
         * @param id
         *                the ID of the repo.
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
         * The rank or repository. 1 goes for 1st position and so on. A linear
         * ordering actually. Smaller the rank, it gets closer to 1st place.
         * Equal ranks produces undefined order.
         * 
         * @return
         */
    int getRank();

    /**
         * Sets the rank of repository.
         * 
         * @param rank
         */
    void setRank(int rank);

    /**
         * Is Repository available? If no, it will reject all incoming requests.
         * 
         * @return
         */
    boolean isAvailable();

    /**
         * Sets the repos availability. If repo is unavailable, all requests
         * will be rejected.
         * 
         * @param val
         */
    void setAvailable(boolean val);

    /**
         * Is repository offline? If yes, it will not try to retrieve from
         * remote peer, it will serve only locally found stuff.
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
         * Sets the listable property of repository. If true, its content will
         * be returned by listItems method, otherwise not. The retrieveItem will
         * still function and return the requested item.
         * 
         * @param val
         */
    void setListable(boolean val);

    /**
         * Is Repository indexable?
         * 
         * @return true if is indexable, otherwise false.
         */
    boolean isIndexable();

    /**
         * Sets the indexable property of repository. If true, its content will
         * be indexed by Indexer, otherwise not.
         * 
         * @param val
         */
    void setIndexable(boolean val);

    /**
         * Is reindexing at startup/initialize on?
         * 
         * @return
         */
    boolean isReindexAtInitialize();

    /**
         * Sets reindexing and startup/initialize.
         * 
         * @param reindexAtInitialize
         */
    void setReindexAtInitialize(boolean reindexAtInitialize);

    /**
         * Returns the local storage of the repository. Per repository instance
         * may exists.
         * 
         * @return localStorage or null.
         */
    LocalStorage getLocalStorage();

    /**
         * Sets the local storage of the repository. May be null if this is an
         * aggregating repos without caching function. Per repository instance
         * may exists.
         * 
         * @param storage
         */
    void setLocalStorage(LocalStorage storage);

    /**
         * Returns the remoteStorage of the repository. Per repository instance
         * may exists.
         * 
         * @return remoteStorage or null.
         */
    RemoteStorage getRemoteStorage();

    /**
         * Sets the remote storage of the repository. May be null if this is a
         * Local repository only. Per repository instance may exists.
         * 
         * @param storage
         */
    void setRemoteStorage(RemoteStorage storage);

    /**
         * Returns the repository level AccessManager. Per repository instance
         * may exists.
         * 
         * @return
         */
    AccessManager getAccessManager();

    /**
         * Sets the repository level AccessManager. Per repository instance may
         * exists.
         * 
         * @param accessManager
         */
    void setAccessManager(AccessManager accessManager);

    /**
         * Retrieves item with content from the path.
         * 
         * @param request
         * @return
         * @throws ItemNotFoundException
         * @throws StorageException
         */
    Item retrieveItem(ProximityRequest request) throws RepositoryNotAvailableException, ItemNotFoundException, StorageException,
	    AccessDeniedException;

    /**
         * Deletes item from the path.
         * 
         * @param request
         * @throws StorageException
         */
    void deleteItem(ProximityRequest request) throws RepositoryNotAvailableException, StorageException;

    /**
         * Stores item.
         * 
         * @param item
         * @throws StorageException
         */
    void storeItem(ProximityRequest request, Item item) throws RepositoryNotAvailableException, StorageException;

    /**
         * List items on path. If not listable, an empty list.
         * 
         * @param path
         * @return list, with items or empty if not listable.
         * @throws StorageException
         */
    List listItems(ProximityRequest request) throws RepositoryNotAvailableException, StorageException;

}
