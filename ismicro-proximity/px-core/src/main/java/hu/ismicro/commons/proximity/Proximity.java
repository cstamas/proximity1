package hu.ismicro.commons.proximity;

import hu.ismicro.commons.proximity.access.AccessManager;
import hu.ismicro.commons.proximity.base.Indexer;
import hu.ismicro.commons.proximity.base.ProximityLogic;
import hu.ismicro.commons.proximity.base.StatisticsGatherer;

import java.util.List;
import java.util.Map;

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
     * Returns the current statistic Gatherer. May be null.
     * 
     * @return current statGatherer
     */
    StatisticsGatherer getStatisticsGatherer();

    /**
     * Sets the current statistic Gatherer.
     * 
     * @param statisticsGatherer
     */
    void setStatisticsGatherer(StatisticsGatherer statisticsGatherer);

    Indexer getIndexer();

    void setIndexer(Indexer indexer);

    AccessManager getAccessManager();

    void setAccessManager(AccessManager accessManager);

    void setRepositories(List reposList);

    List getRepositories();
    
    void addRepository(Repository repository);
    
    void removeRepository(String repoId) throws NoSuchRepositoryException;


    /**
     * Returns the list of known groupIds configured within Proximity.
     * 
     * @return List of known groupId.
     */
    List getRepositoryGroupIds();
    
    
    // ============================================================================================
    // Proxy stuff

    /**
     * Fetches a given item on the supplied path.
     * 
     * @param path
     * @return the wanted Item
     * @throws ItemNotFoundException
     *             if Proximity has not found item on the path
     */
    Item retrieveItem(ProximityRequest request) throws ItemNotFoundException, AccessDeniedException,
            NoSuchRepositoryException;

    
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
     * @return the list of keywords usable in queries.
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

    /**
     * Returns the statistics (if any).
     * 
     * @return
     */
    public Map getStatistics();

}
