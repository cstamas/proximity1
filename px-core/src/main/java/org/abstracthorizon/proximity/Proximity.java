package org.abstracthorizon.proximity;

import java.util.List;
import java.util.Map;

import org.abstracthorizon.proximity.access.AccessManager;
import org.abstracthorizon.proximity.indexer.Indexer;
import org.abstracthorizon.proximity.logic.ProximityLogic;

/**
 * The Proximity interface.
 * 
 * @author cstamas
 * 
 */
public interface Proximity {

    /**
     * Initializes Proximity. Call once, first.
     * 
     */
    void initialize();

    /**
     * Returns the current logic that drives Proximity.
     * 
     * @return logic
     */
    ProximityLogic getProximityLogic();

    /**
     * Sets the logic to drive proximity. The repository by default uses
     * DefaultProximityLogic class unless overridden. May not be null.
     * 
     * @param logic
     */
    void setProximityLogic(ProximityLogic logic);

    /**
     * Retrieve indexer.
     * 
     * @return
     */
    Indexer getIndexer();

    /**
     * Sets indexer.
     * 
     * @param indexer
     */
    void setIndexer(Indexer indexer);

    /**
     * Retrieve Proximity-level AccessManager.
     * 
     * @return
     */
    AccessManager getAccessManager();

    /**
     * Sets Proximity level AccessManager.
     * 
     * @param accessManager
     */
    void setAccessManager(AccessManager accessManager);

    /**
     * Returns the list of Repositories that serves Proximity.
     * 
     * @return
     */
    List getRepositories();

    /**
     * Adds single repository to Proximity.
     * 
     * @param repository
     */
    void addRepository(Repository repository);

    /**
     * Removes single repository from Proximity.
     * 
     * @param repoId
     * @throws NoSuchRepositoryException
     */
    void removeRepository(String repoId) throws NoSuchRepositoryException;

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
     * Is emerge active?
     * 
     * @return
     */
    boolean isEmergeRepositoryGroups();

    /**
     * Changes the way how Proximity functions. If emergingGroups, then the
     * repository groups will appear as root of offered items. If not, all
     * defined repositories are "flattened".
     * 
     * @param emergeGroups
     *            set to true if you want group emerge.
     */
    void setEmergeRepositoryGroups(boolean emergeGroups);

    // ============================================================================================
    // Proxy stuff

    /**
     * Retrieves an item from Proximity according to request.
     * 
     * @param request
     * @return item
     * @throws ItemNotFoundException
     * @throws AccessDeniedException
     * @throws NoSuchRepositoryException
     */
    Item retrieveItem(ProximityRequest request) throws ItemNotFoundException, AccessDeniedException,
            NoSuchRepositoryException;

    /**
     * Stores item on it's supplied path. Proximity does not handle directories
     * separatly, if you store item on path /a/b/c/some.jar, the dir structure
     * /a/b/c will be created if not exists. Only files can be stored,
     * directories not.
     * 
     * @param request
     * @param item
     * @throws AccessDeniedException
     * @throws NoSuchRepositoryException
     */
    void storeItem(ProximityRequest request, Item item) throws AccessDeniedException, NoSuchRepositoryException,
            RepositoryNotAvailableException;

    /**
     * Copies item from source to target location. Source should be retrievable
     * by retrieveItem(downloadable or cached).
     * 
     * @param source
     * @param target
     * @throws ItemNotFoundException
     * @throws AccessDeniedException
     * @throws NoSuchRepositoryException
     */
    void copyItem(ProximityRequest source, ProximityRequest target) throws ItemNotFoundException,
            AccessDeniedException, NoSuchRepositoryException, RepositoryNotAvailableException;

    /**
     * Like copyItem, except it removes source if local.
     * 
     * @param source
     * @param target
     * @throws ItemNotFoundException
     * @throws AccessDeniedException
     * @throws NoSuchRepositoryException
     */
    void moveItem(ProximityRequest source, ProximityRequest target) throws ItemNotFoundException,
            AccessDeniedException, NoSuchRepositoryException, RepositoryNotAvailableException;

    /**
     * Deletes item according to request if local.
     * 
     * @param request
     * @throws ItemNotFoundException
     * @throws AccessDeniedException
     * @throws NoSuchRepositoryException
     */
    void deleteItem(ProximityRequest request) throws ItemNotFoundException, AccessDeniedException,
            NoSuchRepositoryException, RepositoryNotAvailableException;

    // ============================================================================================
    // Search and list

    /**
     * Returns an aggregated List of all item properties in all configured
     * Repositories. It will ALWAYS return List, at least a 0 length list. Will
     * not return null or throw exception in normal circumstances.
     * 
     * @param path
     * @return list of ItemProperties, possibly 0 length.
     */
    List listItems(ProximityRequest request) throws AccessDeniedException, NoSuchRepositoryException;

    /**
     * Lists the searchable keywords as returned by Indexer.
     * 
     * @return the list of keywords (String) usable in queries.
     */
    List getSearchableKeywords();

    /**
     * Searches for item.
     * 
     * @param example
     * @return List of ItemProperties, possibly 0 length.
     */
    public List searchItem(ItemProperties example) throws ProximityException;

    /**
     * Searches for item.
     * 
     * @param query,
     *            dependent on indexer backend since Proximity just "passes" it
     *            out
     * @return List of ItemProperties, possibly 0 length.
     */
    public List searchItem(String query) throws ProximityException;

    // ============================================================================================
    // Maintenance

    /**
     * Forces reindex of repositories.
     * 
     */
    void reindex();

    /**
     * Forces reindex of repository.
     * 
     */
    void reindex(String repoId);

}
