package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.AccessDeniedException;
import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.NoSuchRepositoryException;
import hu.ismicro.commons.proximity.Proximity;
import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.Repository;
import hu.ismicro.commons.proximity.access.AccessManager;
import hu.ismicro.commons.proximity.access.OpenAccessManager;

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
    
    private AccessManager accessManager = new OpenAccessManager();

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

	public AccessManager getAccessManager() {
		return accessManager;
	}

	public void setAccessManager(AccessManager accessManager) {
		this.accessManager = accessManager;
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

    public ItemProperties retrieveItemProperties(ProximityRequest request) throws ItemNotFoundException, AccessDeniedException {
    	    accessManager.decide(request.getGrantee(), request, null);
        for (Iterator i = repositories.keySet().iterator(); i.hasNext();) {
            String reposId = (String) i.next();
            try {
                Repository repo = (Repository) repositories.get(reposId);
                // if repository has no Commitment to prefix or has Commitment to this prefix, try it
                if (repo.getURIPrefix() == null || request.getPath().startsWith(repo.getURIPrefix())) {
                    return repo.retrieveItemProperties(request);
                } else {
                    logger.info("Item " + request.getPath() + " not searched in repository " + reposId + " commited to URI prefix " + repo.getURIPrefix());
                }
            } catch (ItemNotFoundException ex) {
                logger.info("Item " + request.getPath() + " not found in repository " + reposId);
            }
        }
        throw new ItemNotFoundException(request.getPath());
    }

    public Item retrieveItem(ProximityRequest request) throws ItemNotFoundException, AccessDeniedException {
	    accessManager.decide(request.getGrantee(), request, null);
        for (Iterator i = repositories.keySet().iterator(); i.hasNext();) {
            String reposId = (String) i.next();
            try {
                Repository repo = (Repository) repositories.get(reposId);
                // if repository has no Commitment to prefix or has Commitment to this prefix, try it
                if (repo.getURIPrefix() == null || request.getPath().startsWith(repo.getURIPrefix())) {
                    return repo.retrieveItem(request);
                } else {
                    logger.info("Item " + request.getPath() + " not searched in repository " + reposId + " commited to URI prefix " + repo.getURIPrefix());
                }
            } catch (ItemNotFoundException ex) {
                logger.info("Item " + request.getPath() + " not found in repository " + reposId);
            }
        }
        throw new ItemNotFoundException(request.getPath());
    }

    public ItemProperties retrieveItemPropertiesFromRepository(ProximityRequest request)
            throws NoSuchRepositoryException, ItemNotFoundException, AccessDeniedException {
	    accessManager.decide(request.getGrantee(), request, null);
        if (repositories.containsKey(request.getTargetedReposId())) {
            Repository repo = (Repository) repositories.get(request.getTargetedReposId());
            return repo.retrieveItemProperties(request);
        }
        throw new NoSuchRepositoryException(request.getTargetedReposId());
    }

    public Item retrieveItemFromRepository(ProximityRequest request) throws NoSuchRepositoryException,
            ItemNotFoundException, AccessDeniedException {
	    accessManager.decide(request.getGrantee(), request, null);
        if (repositories.containsKey(request.getTargetedReposId())) {
            Repository repo = (Repository) repositories.get(request.getTargetedReposId());
            return repo.retrieveItem(request);
        }
        throw new NoSuchRepositoryException(request.getTargetedReposId());
    }

    public List listItems(ProximityRequest request) {
        List response = new ArrayList();
        for (Iterator i = repositories.keySet().iterator(); i.hasNext();) {
            String reposId = (String) i.next();
            Repository repo = (Repository) repositories.get(reposId);
            response.addAll(repo.listItems(request));
        }
        return response;
    }

    public List listItemsFromRepository(ProximityRequest request) throws NoSuchRepositoryException {
        if (repositories.containsKey(request.getTargetedReposId())) {
            Repository repo = (Repository) repositories.get(request.getTargetedReposId());
            return repo.listItems(request);
        }
        throw new NoSuchRepositoryException(request.getTargetedReposId());
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
