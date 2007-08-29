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

import java.util.Date;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.RepositoryNotAvailableException;

// TODO: Auto-generated Javadoc
/**
 * Simple logic with expiration support. If expirationPeriod is not -1, it will apply expiration period onto items, and
 * will handle removal of them when they expires.
 * 
 * @author cstamas
 */
public class DefaultExpiringProxyingRepositoryLogic
    extends DefaultProxyingRepositoryLogic
{

    /** The Constant NO_EXPIRATION. */
    public static final long NO_EXPIRATION = -1000;

    /** The Constant METADATA_EXPIRES. */
    public static final String METADATA_EXPIRES = "item.expires";

    /** The item expiration period. */
    private long itemExpirationPeriod = 86400 * 1000; // 24 hours

    /**
     * Gets the item expiration period in seconds.
     * 
     * @return the item expiration period in seconds
     */
    public long getItemExpirationPeriodInSeconds()
    {
        return itemExpirationPeriod / 1000;
    }

    /**
     * Sets the item expiration period in seconds.
     * 
     * @param itemExpirationPeriod the new item expiration period in seconds
     */
    public void setItemExpirationPeriodInSeconds( long itemExpirationPeriod )
    {
        this.itemExpirationPeriod = itemExpirationPeriod * 1000;
    }

    /**
     * If item has defined EXPIRES metadata, will use it and remove item from repository if needed.
     * 
     * @param repository the repository
     * @param request the request
     * @param item the item
     * @return the item
     */
    public Item afterLocalCopyFound( Repository repository, ProximityRequest request, Item item )
    {
        if ( item.getProperties().getMetadata( DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES ) != null )
        {
            logger.debug( "Item has expiration, checking it." );
            Date expires = new Date( Long.parseLong( item.getProperties().getMetadata(
                DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES ) ) );
            if ( expires.before( new Date( System.currentTimeMillis() ) ) )
            {
                logger.info( "Item has expired on " + expires + ", DELETING it." );
                try
                {
                    repository.deleteItem( request );
                }
                catch ( RepositoryNotAvailableException ex )
                {
                    logger.warn( "Repository unavailable, cannot delete expired item.", ex );
                }
                catch ( AccessDeniedException ex )
                {
                    logger.warn( "Access is denied, cannot delete expired item.", ex );
                }
                return null;
            }
        }
        return item;
    }

    /**
     * If expiration period is not NO_EXPIRATION, it will apply it on all items.
     * 
     * @param repository the repository
     * @param request the request
     * @param localItem the local item
     * @param remoteItem the remote item
     * @return the item
     */
    public Item afterRemoteCopyFound( Repository repository, ProximityRequest request, Item localItem, Item remoteItem )
    {
        if ( itemExpirationPeriod != NO_EXPIRATION )
        {
            Date expires = new Date( System.currentTimeMillis() + itemExpirationPeriod );
            logger.info( "Setting expires on item  to " + expires.toString() );
            remoteItem.getProperties().setMetadata(
                DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES,
                Long.toString( expires.getTime() ) );
        }
        return remoteItem;
    }

}
