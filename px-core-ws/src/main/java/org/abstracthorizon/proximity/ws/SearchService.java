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

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.NoSuchRepositoryException;
import org.abstracthorizon.proximity.ProximityException;
import org.abstracthorizon.proximity.ProximityRequest;

// TODO: Auto-generated Javadoc
/**
 * The Interface SearchService.
 */
public interface SearchService
{

    /**
     * Returns an aggregated List of all item properties in all configured Repositories. It will ALWAYS return List, at
     * least a 0 length list. Will not return null or throw exception in normal circumstances.
     * 
     * @param request the request
     * 
     * @return list of ItemProperties, possibly 0 length.
     * 
     * @throws AccessDeniedException the access denied exception
     * @throws NoSuchRepositoryException the no such repository exception
     */
    ItemProperties[] listItems( ProximityRequest request )
        throws AccessDeniedException,
            NoSuchRepositoryException;

    /**
     * Lists the searchable keywords as returned by Indexer.
     * 
     * @return the list of keywords usable in queries.
     */
    String[] getSearchableKeywords();

    /**
     * Searches for item.
     * 
     * @param example the example
     * 
     * @return List of ItemProperties, possibly 0 length.
     * 
     * @throws ProximityException the proximity exception
     */
    ItemProperties[] searchItemByExample( ItemProperties example )
        throws ProximityException;

    /**
     * Searches for item.
     * 
     * @param query the query
     * 
     * @return List of ItemProperties, possibly 0 length.
     * 
     * @throws ProximityException the proximity exception
     */
    ItemProperties[] searchItemByQuery( String query )
        throws ProximityException;

}
