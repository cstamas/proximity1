package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.AccessDeniedException;
import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.NoSuchRepositoryException;
import hu.ismicro.commons.proximity.Proximity;
import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.Repository;
import hu.ismicro.commons.proximity.RepositoryNotAvailableException;
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

    private List repositoryOrder = new ArrayList();

    private Map repositories = new HashMap();

    private Indexer indexer;

    private AccessManager accessManager = new OpenAccessManager();

    private StatisticsGatherer statisticsGatherer;

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

    public void initialize() {
        logger.info("Starting Initialization...");
        if (getStatisticsGatherer() != null) {
            getStatisticsGatherer().initialize();
        }
        if (getIndexer() != null) {
            getIndexer().initialize();
        }
        logger.info("Initializing all defined repositories");
        for (Iterator i = repositoryOrder.iterator(); i.hasNext();) {
            String repoId = (String) i.next();
            Repository repo = (Repository) repositories.get(repoId);
            logger.info("Initializing " + repoId);
            repo.initialize();
        }
    }

    public void reindex() {
        logger.info("Forced reindexing of all defined repositories.");
        for (Iterator i = repositoryOrder.iterator(); i.hasNext();) {
            String repoId = (String) i.next();
            Repository repo = (Repository) repositories.get(repoId);
            logger.info("Reindexing " + repoId);
            repo.reindex();
        }
    }

    public void reindex(String repoId) {
        logger.info("Forced reindexing of " + repoId + " repository.");
        Repository repo = (Repository) repositories.get(repoId);
        repo.reindex();
    }

    public void setRepositories(List reposList) {
        logger.info("Received " + reposList.size() + " repositories in a List.");
        repositories.clear();
        repositoryOrder.clear();
        for (Iterator i = reposList.iterator(); i.hasNext();) {
            Repository repo = (Repository) i.next();
            addRepository(repo);
        }
    }

    public List getRepositories() {
        List result = new ArrayList();
        for (Iterator i = repositoryOrder.iterator(); i.hasNext();) {
            String repoKey = (String) i.next();
            result.add(repositories.get(repoKey));
        }
        return result;
    }

    public void addRepository(Repository repository) {
        repositories.put(repository.getId(), repository);
        repositoryOrder.add(repository.getId());
        logger.info("Added repository " + repository.getId());
    }

    public void removeRepository(String repoId) throws NoSuchRepositoryException {
        if (repositories.containsKey(repoId)) {
            repositories.remove(repoId);
            repositoryOrder.remove(repoId);
            logger.info("Removed repository " + repoId);
        } else {
            throw new NoSuchRepositoryException(repoId);
        }
    }

    public ItemProperties retrieveItemProperties(ProximityRequest request) throws ItemNotFoundException,
            AccessDeniedException, NoSuchRepositoryException {
        logger.debug("Got retrieveItemProperties with " + request);
        accessManager.decide(request, null);

        if (request.getTargetedReposId() != null) {
            if (repositories.containsKey(request.getTargetedReposId())) {
                Repository repo = (Repository) repositories.get(request.getTargetedReposId());
                try {
                    return repo.retrieveItemProperties(request);
                } catch (RepositoryNotAvailableException ex) {
                    logger.info("Repository unavailable", ex);
                } catch (ItemNotFoundException ex) {
                    logger.info("ItemProperties " + request.getPath() + " not found in targeted repository " + repo.getId());
                    throw ex;
                }
            }
            throw new NoSuchRepositoryException(request.getTargetedReposId());

        } else {

            for (Iterator i = repositoryOrder.iterator(); i.hasNext();) {
                String reposId = (String) i.next();
                try {
                    Repository repo = (Repository) repositories.get(reposId);
                    return repo.retrieveItemProperties(request);
                } catch (RepositoryNotAvailableException ex) {
                    logger.info("Repository unavailable", ex);
                } catch (ItemNotFoundException ex) {
                    logger.info("ItemPropeties " + request.getPath() + " not found in repository " + reposId);
                }
            }
            throw new ItemNotFoundException(request.getPath());
        }
    }

    public Item retrieveItem(ProximityRequest request) throws ItemNotFoundException, AccessDeniedException,
            NoSuchRepositoryException {
        logger.debug("Got retrieveItem with " + request);
        accessManager.decide(request, null);

        if (request.getTargetedReposId() != null) {
            if (repositories.containsKey(request.getTargetedReposId())) {
                Repository repo = (Repository) repositories.get(request.getTargetedReposId());
                try {
                    return repo.retrieveItem(request);
                } catch (RepositoryNotAvailableException ex) {
                    logger.info("Repository unavailable", ex);
                } catch (ItemNotFoundException ex) {
                    logger.info("Item " + request.getPath() + " not found in targeted repository " + repo.getId());
                    throw ex;
                }
            }
            throw new NoSuchRepositoryException(request.getTargetedReposId());

        } else {

            for (Iterator i = repositoryOrder.iterator(); i.hasNext();) {
                String reposId = (String) i.next();
                try {
                    Repository repo = (Repository) repositories.get(reposId);
                    return repo.retrieveItem(request);
                } catch (RepositoryNotAvailableException ex) {
                    logger.info("Repository unavailable", ex);
                } catch (ItemNotFoundException ex) {
                    logger.info("Item " + request.getPath() + " not found in repository " + reposId);
                }
            }
            throw new ItemNotFoundException(request.getPath());
        }
    }

    public List listItems(ProximityRequest request) throws AccessDeniedException, NoSuchRepositoryException {
        logger.debug("Got listItems with " + request);
        accessManager.decide(request, null);
        List response = new ArrayList();
        if (request.getTargetedReposId() != null) {
            if (repositories.containsKey(request.getTargetedReposId())) {
                Repository repo = (Repository) repositories.get(request.getTargetedReposId());
                try {
                    return repo.listItems(request);
                } catch (RepositoryNotAvailableException ex) {
                    logger.info("Repository unavailable", ex);
                }
            }
            throw new NoSuchRepositoryException(request.getTargetedReposId());
        } else {
            for (Iterator i = repositoryOrder.iterator(); i.hasNext();) {
                String reposId = (String) i.next();
                Repository repo = (Repository) repositories.get(reposId);
                try {
                    response.addAll(repo.listItems(request));
                } catch (RepositoryNotAvailableException ex) {
                    logger.info("Repository unavailable", ex);
                }
            }
            return response;
        }
    }

    public List getSearchableKeywords() {
        logger.debug("Got getSearchableKeywords");
        if (getIndexer() != null) {
            return getIndexer().getSearchableKeywords();
        } else {
            logger.info("No indexer defined, but getSearchableKeywords request came in. Returning empty results.");
            return new ArrayList();
        }
    }

    public List searchItem(ItemProperties example) {
        logger.debug("Got searchItem with example " + example);
        if (getIndexer() != null) {
            return getIndexer().searchByItemPropertiesExample(example);
        } else {
            logger.info("No indexer defined, but search request came in. Returning empty results.");
            return new ArrayList();
        }
    }
    
    public List searchItem(String query) throws IndexerException {
        logger.debug("Got searchItem with query " + query);
        if (getIndexer() != null) {
            return getIndexer().searchByQuery(query);
        } else {
            logger.info("No indexer defined, but search request came in. Returning empty results.");
            return new ArrayList();
        }
    }

    public Map getStatistics() {
        logger.debug("Got getStatistics");
        if (getStatisticsGatherer() != null) {
            return getStatisticsGatherer().getStatistics();
        } else {
            logger.info("No statistics gatherer defined, but stats request came in. Returning empty results.");
            return new HashMap();
        }
    }

}
