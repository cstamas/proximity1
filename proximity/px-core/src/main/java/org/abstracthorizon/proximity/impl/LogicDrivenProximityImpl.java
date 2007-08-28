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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.NoSuchRepositoryException;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.logic.DefaultProximityLogic;
import org.abstracthorizon.proximity.logic.ProximityLogic;

// TODO: Auto-generated Javadoc
/**
 * The Class LogicDrivenProximityImpl.
 */
public class LogicDrivenProximityImpl
    extends AbstractProximity
{

    /** The proximity logic. */
    private ProximityLogic proximityLogic = new DefaultProximityLogic();

    /**
     * Gets the proximity logic.
     * 
     * @return the proximity logic
     */
    public ProximityLogic getProximityLogic()
    {
        return proximityLogic;
    }

    /**
     * Sets the proximity logic.
     * 
     * @param proximityLogic the new proximity logic
     */
    public void setProximityLogic( ProximityLogic proximityLogic )
    {
        this.proximityLogic = proximityLogic;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.impl.AbstractProximity#retrieveItemController(org.abstracthorizon.proximity.ProximityRequest)
     */
    protected Item retrieveItemController( ProximityRequest request )
        throws ItemNotFoundException,
            AccessDeniedException,
            NoSuchRepositoryException
    {

        Item item = null;

        try
        {

            if ( request.getTargetedReposId() != null )
            {

                logger.debug( "Going for targeted reposId {}", request.getTargetedReposId() );
                item = retrieveItemByRepoId( request.getTargetedReposId(), request );

            }
            else if ( request.getTargetedReposGroupId() != null )
            {

                logger.debug( "Going for targeted reposGroupId {}", request.getTargetedReposGroupId() );
                item = retrieveItemByRepoGroupId( request.getTargetedReposGroupId(), request );

            }
            else
            {

                logger.debug( "Going for by absolute order, no target" );
                item = retrieveItemByAbsoluteOrder( request );

            }

            // if not a targeted request that affects only one repos and
            // we need group search
            if ( request.getTargetedReposId() == null && proximityLogic.isGroupSearchNeeded( request ) )
            {

                ProximityRequest groupRequest = proximityLogic.getGroupRequest( request );

                List repositoryGroupOrder = (List) repositoryGroups.get( item.getProperties().getRepositoryGroupId() );
                List itemList = new ArrayList();

                for ( Iterator i = repositoryGroupOrder.iterator(); i.hasNext(); )
                {
                    String reposId = (String) i.next();
                    try
                    {
                        itemList.add( retrieveItemByRepoId( reposId, groupRequest ) );
                    }
                    catch ( ItemNotFoundException ex )
                    {
                        logger.debug( "[{}] not found in repository {}", groupRequest.getPath(), reposId );
                    }
                }

                item = proximityLogic.postprocessItemList( request, groupRequest, itemList );

            }

        }
        catch ( IOException ex )
        {
            logger.error( "Got IOException during retrieveItem.", ex );
        }
        catch ( ItemNotFoundException ex )
        {
            throw ex;
        }

        return item;

    }

}
