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
package org.abstracthorizon.proximity.logic;

import java.io.IOException;

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * Default implementation of RepositoryLogic. It always checks for local copy, does nothing after local copy is found,
 * forces remote retrieval if local copy is not found, does nothing after remote copy is found and always stores freshly
 * retrieved item. Index item that are not directorties.
 * 
 * @author cstamas
 */
public class DefaultProxyingRepositoryLogic
    implements RepositoryLogic
{

    /** The logger. */
    protected Logger logger = LoggerFactory.getLogger( this.getClass() );

    /**
     * Always returns true.
     * 
     * @param repository the repository
     * @param request the request
     * 
     * @return true, if should check for local copy
     */
    public boolean shouldCheckForLocalCopy( Repository repository, ProximityRequest request )
    {
        return true;
    }

    /**
     * Does nothing and returns item unmodified.
     * 
     * @param repository the repository
     * @param request the request
     * @param item the item
     * 
     * @return the item
     */
    public Item afterLocalCopyFound( Repository repository, ProximityRequest request, Item item )
    {
        return item;
    }

    /**
     * Always returns !locallyExists.
     * 
     * @param repository the repository
     * @param request the request
     * @param localItem the local item
     * 
     * @return true, if should check for remote copy
     */
    public boolean shouldCheckForRemoteCopy( Repository repository, ProximityRequest request, Item localItem )
    {
        return localItem == null;
    }

    /**
     * Does nothing and returns item unmodified.
     * 
     * @param repository the repository
     * @param request the request
     * @param localItem the local item
     * @param remoteItem the remote item
     * 
     * @return the item
     */
    public Item afterRemoteCopyFound( Repository repository, ProximityRequest request, Item localItem, Item remoteItem )
    {
        return remoteItem;
    }

    /**
     * Always returns true.
     * 
     * @param repository the repository
     * @param request the request
     * @param localItem the local item
     * @param remoteItem the remote item
     * 
     * @return true, if should store locally after remote retrieval
     */
    public boolean shouldStoreLocallyAfterRemoteRetrieval( Repository repository, ProximityRequest request,
        Item localItem, Item remoteItem )
    {
        return true;
    }

    /**
     * Always give the best what we have.
     * 
     * @param repository the repository
     * @param request the request
     * @param localItem the local item
     * @param remoteItem the remote item
     * 
     * @return the item
     */
    public Item afterRetrieval( Repository repository, ProximityRequest request, Item localItem, Item remoteItem )
    {
        if ( remoteItem != null )
        {
            if ( localItem != null )
            {
                try
                {
                    localItem.getStream().close();
                }
                catch ( IOException ex )
                {
                    logger.warn( "Had a problem trying to close a file: {}", localItem.getProperties(), ex );
                }
            }
            return remoteItem;
        }
        if ( localItem != null )
        {
            return localItem;
        }
        return null;
    }

}
