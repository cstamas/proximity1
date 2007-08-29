/*

   Copyright 2005-2007 Tamas Cservenak (t.cservenak@gmail.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.abstracthorizon.proximity;

import java.util.List;

import org.abstracthorizon.proximity.access.AccessManager;
import org.abstracthorizon.proximity.storage.StorageException;
import org.abstracthorizon.proximity.storage.local.LocalStorage;
import org.abstracthorizon.proximity.storage.remote.RemoteStorage;

// TODO: Auto-generated Javadoc
/**
 * Repository interface used by proximity.
 * 
 * @author cstamas
 */
public interface Repository
    extends ProximityRequestMulticaster
{

    /**
     * Returns the ID of the repository.
     * 
     * @return the id
     */
    String getId();

    /**
     * Sets the ID of the repository. It should be unique Proximity-wide.
     * 
     * @param id the ID of the repo.
     */
    void setId( String id );

    /**
     * Returns the group ID of the repository.
     * 
     * @return the group id
     */
    String getGroupId();

    /**
     * Sets the groupID of the repository.
     * 
     * @param groupId the group id
     */
    void setGroupId( String groupId );

    /**
     * The rank or repository. 1 goes for 1st position and so on. A linear ordering actually. Smaller the rank, it gets
     * closer to 1st place. Equal ranks produces undefined order.
     * 
     * @return the rank
     */
    int getRank();

    /**
     * Sets the rank of repository.
     * 
     * @param rank the rank
     */
    void setRank( int rank );

    /**
     * Is Repository available? If no, it will reject all incoming requests.
     * 
     * @return true, if is available
     */
    boolean isAvailable();

    /**
     * Sets the repos availability. If repo is unavailable, all requests will be rejected.
     * 
     * @param val the val
     */
    void setAvailable( boolean val );

    /**
     * Is repository offline? If yes, it will not try to retrieve from remote peer, it will serve only locally found
     * stuff.
     * 
     * @return true, if is offline
     */
    boolean isOffline();

    /**
     * Sets the offline status of the repository.
     * 
     * @param val the val
     */
    void setOffline( boolean val );

    /**
     * Is Repository listable?.
     * 
     * @return true if is listable, otherwise false.
     */
    boolean isListable();

    /**
     * Sets the listable property of repository. If true, its content will be returned by listItems method, otherwise
     * not. The retrieveItem will still function and return the requested item.
     * 
     * @param val the val
     */
    void setListable( boolean val );

    /**
     * Is Repository indexable?.
     * 
     * @return true if is indexable, otherwise false.
     */
    boolean isIndexable();

    /**
     * Sets the indexable property of repository. If true, its content will be indexed by Indexer, otherwise not.
     * 
     * @param val the val
     */
    void setIndexable( boolean val );

    /**
     * Is reindexing at startup/initialize on?.
     * 
     * @return true, if is reindex at initialize
     */
    boolean isReindexAtInitialize();

    /**
     * Sets reindexing and startup/initialize.
     * 
     * @param reindexAtInitialize the reindex at initialize
     */
    void setReindexAtInitialize( boolean reindexAtInitialize );

    /**
     * Returns the local storage of the repository. Per repository instance may exists.
     * 
     * @return localStorage or null.
     */
    LocalStorage getLocalStorage();

    /**
     * Sets the local storage of the repository. May be null if this is an aggregating repos without caching function.
     * Per repository instance may exists.
     * 
     * @param storage the storage
     */
    void setLocalStorage( LocalStorage storage );

    /**
     * Returns the remoteStorage of the repository. Per repository instance may exists.
     * 
     * @return remoteStorage or null.
     */
    RemoteStorage getRemoteStorage();

    /**
     * Sets the remote storage of the repository. May be null if this is a Local repository only. Per repository
     * instance may exists.
     * 
     * @param storage the storage
     */
    void setRemoteStorage( RemoteStorage storage );

    /**
     * Returns the repository level AccessManager. Per repository instance may exists.
     * 
     * @return the access manager
     */
    AccessManager getAccessManager();

    /**
     * Sets the repository level AccessManager. Per repository instance may exists.
     * 
     * @param accessManager the access manager
     */
    void setAccessManager( AccessManager accessManager );

    /**
     * Retrieves item with content from the path.
     * 
     * @param request the request
     * 
     * @return the item
     * 
     * @throws ItemNotFoundException the item not found exception
     * @throws StorageException the storage exception
     * @throws RepositoryNotAvailableException the repository not available exception
     * @throws AccessDeniedException the access denied exception
     */
    Item retrieveItem( ProximityRequest request )
        throws RepositoryNotAvailableException,
            ItemNotFoundException,
            StorageException,
            AccessDeniedException;

    /**
     * Deletes item from the path.
     * 
     * @param request the request
     * 
     * @throws StorageException the storage exception
     * @throws RepositoryNotAvailableException the repository not available exception
     * @throws AccessDeniedException the access denied exception
     */
    void deleteItem( ProximityRequest request )
        throws RepositoryNotAvailableException,
            StorageException,
            AccessDeniedException;

    /**
     * Stores item.
     * 
     * @param item the item
     * @param request the request
     * 
     * @throws StorageException the storage exception
     * @throws RepositoryNotAvailableException the repository not available exception
     * @throws AccessDeniedException the access denied exception
     */
    void storeItem( ProximityRequest request, Item item )
        throws RepositoryNotAvailableException,
            StorageException,
            AccessDeniedException;

    /**
     * List items on path. If not listable, an empty list.
     * 
     * @param request the request
     * 
     * @return list, with items or empty if not listable.
     * 
     * @throws StorageException the storage exception
     * @throws RepositoryNotAvailableException the repository not available exception
     * @throws AccessDeniedException the access denied exception
     */
    List listItems( ProximityRequest request )
        throws RepositoryNotAvailableException,
            StorageException,
            AccessDeniedException;

}
