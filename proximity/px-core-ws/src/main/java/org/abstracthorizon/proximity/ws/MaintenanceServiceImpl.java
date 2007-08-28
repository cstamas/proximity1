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
package org.abstracthorizon.proximity.ws;

import org.abstracthorizon.proximity.NoSuchRepositoryException;
import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.indexer.Indexer;

// TODO: Auto-generated Javadoc
/**
 * The Class MaintenanceServiceImpl.
 */
public class MaintenanceServiceImpl
    implements MaintenanceService
{

    /** The indexer. */
    private Indexer indexer;

    /** The proximity. */
    private Proximity proximity;

    /**
     * Gets the indexer.
     * 
     * @return the indexer
     */
    public Indexer getIndexer()
    {
        return indexer;
    }

    /**
     * Sets the indexer.
     * 
     * @param indexer the new indexer
     */
    public void setIndexer( Indexer indexer )
    {
        this.indexer = indexer;
    }

    /**
     * Gets the proximity.
     * 
     * @return the proximity
     */
    public Proximity getProximity()
    {
        return proximity;
    }

    /**
     * Sets the proximity.
     * 
     * @param proximity the new proximity
     */
    public void setProximity( Proximity proximity )
    {
        this.proximity = proximity;
    }

    /* (non-Javadoc)
     * @see org.abstracthorizon.proximity.ws.MaintenanceService#reindexAll()
     */
    public void reindexAll()
    {
        indexer.reindex();
    }

    /* (non-Javadoc)
     * @see org.abstracthorizon.proximity.ws.MaintenanceService#reindexRepository(java.lang.String)
     */
    public void reindexRepository( String repoId )
        throws NoSuchRepositoryException
    {
        indexer.reindex( repoId );
    }

}
