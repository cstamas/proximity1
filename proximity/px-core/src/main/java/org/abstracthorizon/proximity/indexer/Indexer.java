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
package org.abstracthorizon.proximity.indexer;

import java.util.List;

import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.NoSuchRepositoryException;
import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.storage.StorageException;

// TODO: Auto-generated Javadoc
/**
 * Indexer, a component that simply holds some index and maintenances it as instructed by subsequent method calls.
 * 
 * @author cstamas
 */
public interface Indexer
    extends Searchable
{

    /**
     * Returns the indexers instance of proximity.
     * 
     * @return the proximity
     */
    Proximity getProximity();

    /**
     * Sets the proximity instance.
     * 
     * @param proximity the proximity
     */
    void setProximity( Proximity proximity );

    /**
     * Initialize the current indexer implementation.
     */
    public void initialize();

    /**
     * Adds ItemProperties to index.
     * 
     * @param ip the ip
     * 
     * @throws StorageException the storage exception
     */
    void addItemProperties( ItemProperties ip )
        throws StorageException;

    /**
     * Adds a list of ItemProperties to index.
     * 
     * @param itemProperties the item properties
     * 
     * @throws StorageException the storage exception
     */
    void addItemProperties( List itemProperties )
        throws StorageException;

    /**
     * Removes ItemProperties from index.
     * 
     * @param ip the ip
     * 
     * @throws ItemNotFoundException *
     * @throws StorageException the storage exception
     */
    void deleteItemProperties( ItemProperties ip )
        throws StorageException;

    // ============================================================================================
    // Maintenance

    /**
     * Forces reindex of repositories.
     */
    void reindex();

    /**
     * Forces reindex of repository.
     * 
     * @param repoId the repo id
     * 
     * @throws NoSuchRepositoryException the no such repository exception
     */
    void reindex( String repoId )
        throws NoSuchRepositoryException;

}
