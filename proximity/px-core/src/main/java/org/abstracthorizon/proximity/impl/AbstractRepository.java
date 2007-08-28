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
package org.abstracthorizon.proximity.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.ProximityRequestListener;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.RepositoryNotAvailableException;
import org.abstracthorizon.proximity.access.AccessManager;
import org.abstracthorizon.proximity.access.OpenAccessManager;
import org.abstracthorizon.proximity.events.ItemDeleteEvent;
import org.abstracthorizon.proximity.events.ItemRetrieveEvent;
import org.abstracthorizon.proximity.events.ItemStoreEvent;
import org.abstracthorizon.proximity.events.ProximityRequestEvent;
import org.abstracthorizon.proximity.storage.StorageException;
import org.abstracthorizon.proximity.storage.local.LocalStorage;
import org.abstracthorizon.proximity.storage.remote.RemoteStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractRepository.
 */
public abstract class AbstractRepository
    implements Repository
{

    /** The logger. */
    protected Logger logger = LoggerFactory.getLogger( this.getClass() );

    /** The request listeners. */
    private Vector requestListeners = new Vector();

    /** The id. */
    private String id;

    /** The group id. */
    private String groupId = "default";

    /** The local storage. */
    private LocalStorage localStorage;

    /** The remote storage. */
    private RemoteStorage remoteStorage;

    /** The access manager. */
    private AccessManager accessManager = new OpenAccessManager();

    /** The rank. */
    private int rank = 999;

    /** The available. */
    private boolean available = true;

    /** The offline. */
    private boolean offline = false;

    /** The listable. */
    private boolean listable = true;

    /** The indexable. */
    private boolean indexable = true;

    /** The reindex at initialize. */
    private boolean reindexAtInitialize = true;

    /** The not found cache period. */
    private long notFoundCachePeriod = 86400 * 1000; // 24 hours

    /** The not found cache. */
    private Map notFoundCache = Collections.synchronizedMap( new HashMap() );

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#getAccessManager()
     */
    public AccessManager getAccessManager()
    {
        return accessManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#setAccessManager(org.abstracthorizon.proximity.access.AccessManager)
     */
    public void setAccessManager( AccessManager accessManager )
    {
        this.accessManager = accessManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#isAvailable()
     */
    public boolean isAvailable()
    {
        return available;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#setAvailable(boolean)
     */
    public void setAvailable( boolean available )
    {
        this.available = available;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#getGroupId()
     */
    public String getGroupId()
    {
        return groupId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#setGroupId(java.lang.String)
     */
    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#getId()
     */
    public String getId()
    {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#setId(java.lang.String)
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#isListable()
     */
    public boolean isListable()
    {
        return listable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#setListable(boolean)
     */
    public void setListable( boolean listable )
    {
        this.listable = listable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#getLocalStorage()
     */
    public LocalStorage getLocalStorage()
    {
        return localStorage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#setLocalStorage(org.abstracthorizon.proximity.storage.local.LocalStorage)
     */
    public void setLocalStorage( LocalStorage localStorage )
    {
        this.localStorage = localStorage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#isOffline()
     */
    public boolean isOffline()
    {
        return offline;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#setOffline(boolean)
     */
    public void setOffline( boolean offline )
    {
        this.offline = offline;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#getRank()
     */
    public int getRank()
    {
        return rank;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#setRank(int)
     */
    public void setRank( int rank )
    {
        this.rank = rank;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#isReindexAtInitialize()
     */
    public boolean isReindexAtInitialize()
    {
        return reindexAtInitialize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#setReindexAtInitialize(boolean)
     */
    public void setReindexAtInitialize( boolean reindexAtInitialize )
    {
        this.reindexAtInitialize = reindexAtInitialize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#isIndexable()
     */
    public boolean isIndexable()
    {
        return indexable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#setIndexable(boolean)
     */
    public void setIndexable( boolean indexable )
    {
        this.indexable = indexable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#getRemoteStorage()
     */
    public RemoteStorage getRemoteStorage()
    {
        return remoteStorage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#setRemoteStorage(org.abstracthorizon.proximity.storage.remote.RemoteStorage)
     */
    public void setRemoteStorage( RemoteStorage remoteStorage )
    {
        this.remoteStorage = remoteStorage;
    }

    /**
     * Gets the not found cache period in seconds.
     * 
     * @return the not found cache period in seconds
     */
    public long getNotFoundCachePeriodInSeconds()
    {
        return notFoundCachePeriod / 1000;
    }

    /**
     * Sets the not found cache period in seconds.
     * 
     * @param notFoundCachePeriod the new not found cache period in seconds
     */
    public void setNotFoundCachePeriodInSeconds( long notFoundCachePeriod )
    {
        this.notFoundCachePeriod = notFoundCachePeriod * 1000;
    }

    // ---------------------------------------------------------------------------------
    // ProximityRequestMulticaster Iface

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ProximityRequestMulticaster#addProximityRequestListener(org.abstracthorizon.proximity.ProximityRequestListener)
     */
    public void addProximityRequestListener( ProximityRequestListener o )
    {
        requestListeners.add( o );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ProximityRequestMulticaster#removeProximityRequestListener(org.abstracthorizon.proximity.ProximityRequestListener)
     */
    public void removeProximityRequestListener( ProximityRequestListener o )
    {
        requestListeners.remove( o );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ProximityRequestMulticaster#notifyProximityRequestListeners(org.abstracthorizon.proximity.events.ProximityRequestEvent)
     */
    public void notifyProximityRequestListeners( ProximityRequestEvent event )
    {
        synchronized ( requestListeners )
        {
            for ( Iterator i = requestListeners.iterator(); i.hasNext(); )
            {
                ProximityRequestListener l = (ProximityRequestListener) i.next();
                try
                {
                    l.proximityRequestEvent( event );
                }
                catch ( Exception e )
                {
                    logger.info( "Unexpected exception in listener", e );
                    i.remove();
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------
    // Repository Iface

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#retrieveItem(org.abstracthorizon.proximity.ProximityRequest)
     */
    public Item retrieveItem( ProximityRequest request )
        throws RepositoryNotAvailableException,
            ItemNotFoundException,
            StorageException,
            AccessDeniedException
    {
        if ( !isAvailable() )
        {
            throw new RepositoryNotAvailableException( "The repository " + getId() + " is NOT available!" );
        }
        getAccessManager().decide( request, null );
        try
        {
            String requestKey = getRepositoryRequestAsKey( this, request );
            if ( notFoundCache.containsKey( requestKey ) )
            {
                // it is in cache, check when it got in
                Date lastRequest = (Date) notFoundCache.get( requestKey );
                if ( lastRequest.before( new Date( System.currentTimeMillis() - notFoundCachePeriod ) ) )
                {
                    // the notFoundCache record expired, remove it and check
                    // its
                    // existence
                    logger.debug( "n-cache record expired, will go again remote to fetch." );
                    notFoundCache.remove( requestKey );
                    request.setLocalOnly( false );
                }
                else
                {
                    // the notFoundCache record is still valid, do not check
                    // its
                    // existence
                    logger.debug( "n-cache record still active, will not go remote to fetch." );
                    request.setLocalOnly( true );
                }
            }
            else
            {
                // it is not in notFoundCache, check its existence
                request.setLocalOnly( false );
            }
            Item result = doRetrieveItem( request );
            notifyProximityRequestListeners( new ItemRetrieveEvent( request, result.getProperties() ) );
            return result;
        }
        catch ( ItemNotFoundException ex )
        {
            // we have not found it
            // put the path into not found cache
            String requestPath = getRepositoryRequestAsKey( this, request );
            if ( !notFoundCache.containsKey( requestPath ) )
            {
                logger.debug( "Caching failed request [{}] to n-cache.", requestPath );
                notFoundCache.put( requestPath, new Date() );
            }
            throw ex;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#deleteItem(org.abstracthorizon.proximity.ProximityRequest)
     */
    public void deleteItem( ProximityRequest request )
        throws RepositoryNotAvailableException,
            StorageException
    {
        if ( !isAvailable() )
        {
            throw new RepositoryNotAvailableException( "The repository " + getId() + " is NOT available!" );
        }
        if ( getLocalStorage() != null )
        {
            try
            {
                ItemProperties itemProps = getLocalStorage().retrieveItem( request.getPath(), true ).getProperties();
                itemProps.setRepositoryId( getId() );
                itemProps.setRepositoryGroupId( getGroupId() );
                getLocalStorage().deleteItem( request.getPath() );
                notifyProximityRequestListeners( new ItemDeleteEvent( request, itemProps ) );

                // remove it from n-cache also
                String requestKey = getRepositoryRequestAsKey( this, request );
                if ( notFoundCache.containsKey( requestKey ) )
                {
                    notFoundCache.remove( requestKey );
                }
            }
            catch ( ItemNotFoundException ex )
            {
                logger.info( "Path [{}] not found but deletion requested.", request.getPath() );
            }
        }
        else
        {
            throw new UnsupportedOperationException( "The repository " + getId() + " have no local storage!" );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#storeItem(org.abstracthorizon.proximity.ProximityRequest,
     *      org.abstracthorizon.proximity.Item)
     */
    public void storeItem( ProximityRequest request, Item item )
        throws RepositoryNotAvailableException,
            StorageException
    {
        if ( !isAvailable() )
        {
            throw new RepositoryNotAvailableException( "The repository " + getId() + " is NOT available!" );
        }
        doStoreItem( request, item );
        notifyProximityRequestListeners( new ItemStoreEvent( request, item.getProperties() ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.Repository#listItems(org.abstracthorizon.proximity.ProximityRequest)
     */
    public List listItems( ProximityRequest request )
        throws RepositoryNotAvailableException,
            StorageException
    {
        if ( !isAvailable() )
        {
            throw new RepositoryNotAvailableException( "The repository " + getId() + " is NOT available!" );
        }
        List result = new ArrayList();
        if ( isListable() )
        {
            if ( getLocalStorage() != null )
            {
                List list = getLocalStorage().listItems( request.getPath() );
                for ( Iterator i = list.iterator(); i.hasNext(); )
                {
                    ItemProperties ip = (ItemProperties) i.next();
                    ip.setRepositoryId( getId() );
                    ip.setRepositoryGroupId( getGroupId() );
                    if ( getRemoteStorage() != null )
                    {
                        ip.setRemoteUrl( getRemoteStorage().getAbsoluteUrl( ip.getPath() ) );
                    }
                }
                result.addAll( list );
            }
        }
        return result;
    }

    /**
     * Do store item.
     * 
     * @param request the request
     * @param item the item
     * 
     * @throws RepositoryNotAvailableException the repository not available exception
     * @throws StorageException the storage exception
     */
    protected final void doStoreItem( ProximityRequest request, Item item )
        throws RepositoryNotAvailableException,
            StorageException
    {
        if ( getLocalStorage() != null && getLocalStorage().isWritable() )
        {
            item.getProperties().setRepositoryId( getId() );
            item.getProperties().setRepositoryGroupId( getGroupId() );
            getLocalStorage().storeItem( item );
            // remove it from n-cache also
            String requestKey = getRepositoryRequestAsKey( this, request );
            if ( notFoundCache.containsKey( requestKey ) )
            {
                notFoundCache.remove( requestKey );
            }
        }
        else
        {
            throw new UnsupportedOperationException( "The repository " + getId()
                + " have no writable local storage defined!" );
        }
    }

    /**
     * Do retrieve item.
     * 
     * @param request the request
     * 
     * @return the item
     * 
     * @throws RepositoryNotAvailableException the repository not available exception
     * @throws ItemNotFoundException the item not found exception
     * @throws StorageException the storage exception
     */
    protected abstract Item doRetrieveItem( ProximityRequest request )
        throws RepositoryNotAvailableException,
            ItemNotFoundException,
            StorageException;

    /**
     * Constructs a unique request key using repoId and request path.
     * 
     * @param repository the repository
     * @param request the request
     * 
     * @return a unique key in form "repoId:/path/to/artifact"
     */
    protected String getRepositoryRequestAsKey( Repository repository, ProximityRequest request )
    {
        StringBuffer sb = new StringBuffer( repository.getId() );
        sb.append( ":" );
        sb.append( request.getPath() );
        return sb.toString();
    }

}
