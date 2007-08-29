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
import java.util.Map;

import org.abstracthorizon.proximity.mapping.GroupRequestMapper;

// TODO: Auto-generated Javadoc
/**
 * The Proximity interface.
 * 
 * @author cstamas
 */
public interface Proximity
    extends ProximityRequestMulticaster
{

    /**
     * Adds single repository to Proximity.
     * 
     * @param repository the repository
     */
    void addRepository( Repository repository );

    /**
     * Removes single repository from Proximity.
     * 
     * @param repoId the repo id
     * 
     * @throws NoSuchRepositoryException the no such repository exception
     */
    void removeRepository( String repoId )
        throws NoSuchRepositoryException;

    /**
     * Returns the requested Repository by ID.
     * 
     * @param repoId the repo id
     * 
     * @return the repository
     * 
     * @throws NoSuchRepositoryException the no such repository exception
     */
    Repository getRepository( String repoId )
        throws NoSuchRepositoryException;

    /**
     * Adds list of repositories to Proximity. Used during startup.
     * 
     * @param repositories the repositories
     */
    void setRepositories( List repositories );

    /**
     * Returns the list of Repositories that serves Proximity.
     * 
     * @return a List<Repository>
     */
    List getRepositories();

    /**
     * Returns existing Repository groups.
     * 
     * @return a map (String grouId, List<Repository>).
     */
    Map getRepositoryGroups();

    /**
     * Returns the list of Repositor ID's known by Proximity.
     * 
     * @return List(<String>)
     */
    List getRepositoryIds();

    /**
     * Returns the list of known groupIds configured within Proximity.
     * 
     * @return List of Strings, the known groupIds.
     */
    List getRepositoryGroupIds();

    /**
     * Is emerge active?.
     * 
     * @return true, if is emerge repository groups
     */
    boolean isEmergeRepositoryGroups();

    /**
     * Changes the way how Proximity functions. If emergingGroups, then the repository groups will appear as root of
     * offered items. If not, all defined repositories are "flattened".
     * 
     * @param emergeGroups set to true if you want group emerge.
     */

    void setEmergeRepositoryGroups( boolean emergeGroups );

    /**
     * Mapping of the group based requests to repositories.
     * 
     * @return the actual mapping
     */
    public GroupRequestMapper getGroupRequestMapper();

    /**
     * Mapping of the group based requests to repositories.
     * 
     * @param groupRequestMapper the group request mapper
     */
    public void setGroupRequestMapper( GroupRequestMapper groupRequestMapper );

    // ============================================================================================
    // Proxy stuff

    /**
     * Retrieves an item from Proximity according to request.
     * 
     * @param request the request
     * 
     * @return item
     * 
     * @throws ItemNotFoundException the item not found exception
     * @throws AccessDeniedException the access denied exception
     * @throws NoSuchRepositoryException the no such repository exception
     */
    Item retrieveItem( ProximityRequest request )
        throws ItemNotFoundException,
            AccessDeniedException,
            NoSuchRepositoryException;

    /**
     * Stores item on it's supplied path. Proximity does not handle directories separatly, if you store item on path
     * /a/b/c/some.jar, the dir structure /a/b/c will be created if not exists. Only files can be stored, directories
     * not.
     * 
     * @param request the request
     * @param item the item
     * 
     * @throws AccessDeniedException the access denied exception
     * @throws NoSuchRepositoryException the no such repository exception
     * @throws RepositoryNotAvailableException the repository not available exception
     */
    void storeItem( ProximityRequest request, Item item )
        throws AccessDeniedException,
            NoSuchRepositoryException,
            RepositoryNotAvailableException;

    /**
     * Copies item from source to target location. Source should be retrievable by retrieveItem(downloadable or cached).
     * 
     * @param source the source
     * @param target the target
     * 
     * @throws ItemNotFoundException the item not found exception
     * @throws AccessDeniedException the access denied exception
     * @throws NoSuchRepositoryException the no such repository exception
     * @throws RepositoryNotAvailableException the repository not available exception
     */
    void copyItem( ProximityRequest source, ProximityRequest target )
        throws ItemNotFoundException,
            AccessDeniedException,
            NoSuchRepositoryException,
            RepositoryNotAvailableException;

    /**
     * Like copyItem, except it removes source if local.
     * 
     * @param source the source
     * @param target the target
     * 
     * @throws ItemNotFoundException the item not found exception
     * @throws AccessDeniedException the access denied exception
     * @throws NoSuchRepositoryException the no such repository exception
     * @throws RepositoryNotAvailableException the repository not available exception
     */
    void moveItem( ProximityRequest source, ProximityRequest target )
        throws ItemNotFoundException,
            AccessDeniedException,
            NoSuchRepositoryException,
            RepositoryNotAvailableException;

    /**
     * Deletes item according to request if local.
     * 
     * @param request the request
     * 
     * @throws ItemNotFoundException the item not found exception
     * @throws AccessDeniedException the access denied exception
     * @throws NoSuchRepositoryException the no such repository exception
     * @throws RepositoryNotAvailableException the repository not available exception
     */
    void deleteItem( ProximityRequest request )
        throws ItemNotFoundException,
            AccessDeniedException,
            NoSuchRepositoryException,
            RepositoryNotAvailableException;

    // ============================================================================================
    // List

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
    List listItems( ProximityRequest request )
        throws AccessDeniedException,
            NoSuchRepositoryException;

}
