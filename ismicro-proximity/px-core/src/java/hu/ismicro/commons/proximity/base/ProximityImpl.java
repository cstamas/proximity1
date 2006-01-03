package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.NoSuchRepositoryException;
import hu.ismicro.commons.proximity.Proximity;
import hu.ismicro.commons.proximity.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProximityImpl implements Proximity {

    protected Log logger = LogFactory.getLog(this.getClass());

    private Map repositories = new HashMap();

    private Indexer indexer;

    private StatisticsGatherer statisticsGatherer;
    
    void afterPropertiesSet() {
        logger.info("Initializing all defined repositories");
        for (Iterator i = repositories.keySet().iterator(); i.hasNext(); ) {
            String repoId = (String) i.next();
            Repository repo = (Repository) repositories.get(repoId);
            logger.info("  Initializing " + repoId);
            repo.initialize();
        }
    }

    public StatisticsGatherer getStatisticsGatherer() {
        return statisticsGatherer;
    }

    public void setStatisticsGatherer(StatisticsGatherer statisticsGatherer) {
        this.statisticsGatherer = statisticsGatherer;
    }

    public Indexer getIndexer() {
        return indexer;
    }

    public void setIndexer(Indexer indexer) {
        this.indexer = indexer;
    }

    public void setRepositories(List reposList) {
        logger.info("Received " + reposList.size() + " repositories in a List.");
        repositories.clear();
        for (Iterator i = reposList.iterator(); i.hasNext();) {
            Repository repo = (Repository) i.next();
            addRepository(repo);
        }
    }

    public List getRepositories() {
        List result = new ArrayList();
        for (Iterator i = repositories.keySet().iterator(); i.hasNext();) {
            String repoKey = (String) i.next();
            result.add(repositories.get(repoKey));
        }
        return result;
    }

    public void addRepository(Repository repository) {
        repositories.put(repository.getId(), repository);
        logger.info("Added repository " + repository.getId());
    }

    public void removeRepository(String repoId) throws NoSuchRepositoryException {
        if (repositories.containsKey(repoId)) {
            repositories.remove(repoId);
            logger.info("Removed repository " + repoId);
        } else {
            throw new NoSuchRepositoryException(repoId);
        }
    }

    public ItemProperties retrieveItemProperties(String path) throws ItemNotFoundException {
        for (Iterator i = repositories.keySet().iterator(); i.hasNext();) {
            String reposId = (String) i.next();
            try {
                Repository repo = (Repository) repositories.get(reposId);
                return repo.retrieveItemProperties(path);
            } catch (ItemNotFoundException ex) {
                logger.info("Item " + path + " not found in repository " + reposId);
            }
        }
        throw new ItemNotFoundException(path);
    }

    public Item retrieveItem(String path) throws ItemNotFoundException {
        for (Iterator i = repositories.keySet().iterator(); i.hasNext();) {
            String reposId = (String) i.next();
            try {
                Repository repo = (Repository) repositories.get(reposId);
                return repo.retrieveItem(path);
            } catch (ItemNotFoundException ex) {
                logger.info("Item " + path + " not found in repository " + reposId);
            }
        }
        throw new ItemNotFoundException(path);
    }

    public ItemProperties retrieveItemPropertiesFromRepository(String path, String reposId)
            throws NoSuchRepositoryException, ItemNotFoundException {
        if (repositories.containsKey(reposId)) {
            Repository repo = (Repository) repositories.get(reposId);
            return repo.retrieveItemProperties(path);
        }
        throw new NoSuchRepositoryException(reposId);
    }

    public Item retrieveItemFromRepository(String path, String reposId) throws NoSuchRepositoryException,
            ItemNotFoundException {
        if (repositories.containsKey(reposId)) {
            Repository repo = (Repository) repositories.get(reposId);
            return repo.retrieveItem(path);
        }
        throw new NoSuchRepositoryException(reposId);
    }

    public List listItems(String path) {
        List response = new ArrayList();
        for (Iterator i = repositories.keySet().iterator(); i.hasNext();) {
            String reposId = (String) i.next();
            Repository repo = (Repository) repositories.get(reposId);
            response.addAll(repo.listItems(path));
        }
        return response;
    }

    public List listItemsFromRepository(String path, String reposId) throws NoSuchRepositoryException {
        if (repositories.containsKey(reposId)) {
            Repository repo = (Repository) repositories.get(reposId);
            return repo.listItems(path);
        }
        throw new NoSuchRepositoryException(reposId);
    }

    public List searchItem(ItemProperties example) {
    		if (getIndexer() != null) {
    	        return indexer.searchByItemPropertiesExample(example);
    		} else {
    			logger.info("No indexer defined, but search request came in. Returning empty results.");
    			return new ArrayList();
    		}
    }

    public Map getStatistics() {
        if (getStatisticsGatherer() != null) {
            return getStatisticsGatherer().getStatistics();
        } else {
			logger.info("No statistics gatherer defined, but stats request came in. Returning empty results.");
            return new HashMap();
        }
    }

}
