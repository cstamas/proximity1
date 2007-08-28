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

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.RepositoryNotAvailableException;
import org.abstracthorizon.proximity.events.ItemCacheEvent;
import org.abstracthorizon.proximity.logic.DefaultProxyingRepositoryLogic;
import org.abstracthorizon.proximity.logic.RepositoryLogic;
import org.abstracthorizon.proximity.storage.StorageException;

// TODO: Auto-generated Javadoc
/**
 * The Class LogicDrivenRepositoryImpl.
 */
public class LogicDrivenRepositoryImpl
    extends AbstractRepository
{

    /** The repository logic. */
    private RepositoryLogic repositoryLogic = new DefaultProxyingRepositoryLogic();

    /**
     * Gets the repository logic.
     * 
     * @return the repository logic
     */
    public RepositoryLogic getRepositoryLogic()
    {
        return repositoryLogic;
    }

    /**
     * Sets the repository logic.
     * 
     * @param repositoryLogic the new repository logic
     */
    public void setRepositoryLogic( RepositoryLogic repositoryLogic )
    {
        if ( repositoryLogic == null )
        {
            throw new IllegalArgumentException( "The logic may be not null" );
        }
        this.repositoryLogic = repositoryLogic;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.impl.AbstractRepository#doRetrieveItem(org.abstracthorizon.proximity.ProximityRequest)
     */
    protected Item doRetrieveItem( ProximityRequest request )
        throws RepositoryNotAvailableException,
            ItemNotFoundException,
            StorageException
    {
        Item localResult = null;
        Item remoteResult = null;

        try
        {
            if ( getLocalStorage() != null )
            {
                if ( getRepositoryLogic().shouldCheckForLocalCopy( this, request ) )
                {
                    if ( getLocalStorage().containsItem( request.getPath() ) )
                    {
                        logger.debug(
                            "Item [{}] is contained in local storage of repository {}",
                            request.getPath(),
                            getId() );
                        localResult = getLocalStorage().retrieveItem( request.getPath(), request.isPropertiesOnly() );
                        localResult.getProperties().setRepositoryId( getId() );
                        localResult.getProperties().setRepositoryGroupId( getGroupId() );
                        logger.debug(
                            "Item [{}] fetched from local storage of repository {}",
                            request.getPath(),
                            getId() );
                        localResult = getRepositoryLogic().afterLocalCopyFound( this, request, localResult );
                    }
                    else
                    {
                        logger.debug( "Not found [{}] item in storage of repository {}", request.getPath(), getId() );
                    }
                }
            }
            if ( !isOffline() && !request.isLocalOnly()
                && getRepositoryLogic().shouldCheckForRemoteCopy( this, request, localResult )
                && getRemoteStorage() != null )
            {
                if ( getRemoteStorage().containsItem( request.getPath() ) )
                {
                    logger.debug( "Found [{}] item in remote storage of repository {}", request.getPath(), getId() );
                    remoteResult = getRemoteStorage().retrieveItem( request.getPath(), request.isPropertiesOnly() );
                    remoteResult.getProperties().setRepositoryId( getId() );
                    remoteResult.getProperties().setRepositoryGroupId( getGroupId() );
                    logger.debug( "Item [{}] fetched from remote storage of repository {}", request.getPath(), getId() );
                    remoteResult = getRepositoryLogic().afterRemoteCopyFound( this, request, localResult, remoteResult );
                    if ( remoteResult != null && !remoteResult.getProperties().isDirectory()
                        && getLocalStorage() != null && getLocalStorage().isWritable() )
                    {
                        if ( getRepositoryLogic().shouldStoreLocallyAfterRemoteRetrieval(
                            this,
                            request,
                            localResult,
                            remoteResult ) )
                        {
                            logger.debug(
                                "Storing [{}] item in writable storage of repository {}",
                                request.getPath(),
                                getId() );
                            doStoreItem( request, remoteResult );
                            notifyProximityRequestListeners( new ItemCacheEvent( request, remoteResult.getProperties() ) );

                            remoteResult = getLocalStorage().retrieveItem(
                                request.getPath(),
                                request.isPropertiesOnly() );
                            remoteResult.getProperties().setRepositoryId( getId() );
                            remoteResult.getProperties().setRepositoryGroupId( getGroupId() );
                        }
                    }
                }
                else
                {
                    logger.debug( "Not found [{}] item in remote peer of repository {}", request.getPath(), getId() );
                }

            }
            Item result = getRepositoryLogic().afterRetrieval( this, request, localResult, remoteResult );
            if ( result == null )
            {
                logger.debug( "Item [{}] not found in repository {}", request.getPath(), getId() );
                throw new ItemNotFoundException( request.getPath() );
            }
            if ( getRemoteStorage() != null )
            {
                result.getProperties().setRemoteUrl(
                    getRemoteStorage().getAbsoluteUrl( result.getProperties().getPath() ) );
            }
            logger.debug( "Item [{}] found in repository {}", request.getPath(), getId() );
            return result;
        }
        catch ( ItemNotFoundException ex )
        {
            throw new ItemNotFoundException( request.getPath(), getId() );
        }
    }

}
