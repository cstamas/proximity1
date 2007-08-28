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
import java.util.Set;

import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.storage.StorageException;

// TODO: Auto-generated Javadoc
/**
 * The Interface Searchable.
 */
public interface Searchable
{

    /**
     * Returns the list that this indexer have searchable.
     * 
     * @return list of keywords usable in searches.
     */
    Set getSearchableKeywords();

    /**
     * Performs a search using ip as "example".
     * 
     * @param ip the ip
     * 
     * @return the list
     * 
     * @throws StorageException the storage exception
     */
    List searchByItemPropertiesExample( ItemProperties ip )
        throws StorageException;

    /**
     * Indexer implementation dependent. Performs a search by some query.
     * 
     * @param query the query
     * 
     * @return the list
     * 
     * @throws IndexerException the indexer exception
     * @throws StorageException the storage exception
     */
    List searchByQuery( String query )
        throws IndexerException,
            StorageException;

}
