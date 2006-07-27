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
import hu.ismicro.commons.proximity.base.logic.DefaultProximityLogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProximityImpl implements Proximity {

    protected Log logger = LogFactory.getLog(this.getClass());

    /** The repo groups, [repo.getGroupId, List&lt;Repositories&gt;] */
    private Map repositoryGroups = new HashMap();

    /** The repo register, [repo.getId, repo] */
    private Map repositories = new HashMap();

    /** The overall order of repositories, List&lt;Repositories&gt;] */
    private List repositoryOrder = new ArrayList();

    private ProximityLogic proximityLogic = new DefaultProximityLogic();

    private Indexer indexer;

    private AccessManager accessManager = new OpenAccessManager();

    private StatisticsGatherer statisticsGatherer;

    public ProximityLogic getProximityLogic() {
        return proximityLogic;
    }

    public void setProximityLogic(ProximityLogic proximityLogic) {
        this.proximityLogic = proximityLogic;
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

    public void initialize() {
        logger.info("Starting Initialization...");
        if (getStatisticsGatherer() != null) {
            getStatisticsGatherer().initialize();
        }
        if (getIndexer() != null) {
            getIndexer().initialize();
        }
        logger.info("Initializing all defined repositories");
        for (Iterator i = repositories.keySet().iterator(); i.hasNext();) {
            String repoId = (String) i.next();
            Repository repo = (Repository) repositories.get(repoId);
            logger.info("Initializing " + repoId);
            repo.initialize();
        }
    }

    public void reindex() {
        logger.info("Forced reindexing of all defined repositories.");
        for (Iterator i = repositories.keySet().iterator(); i.hasNext();) {
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
        repositoryGroups.clear();
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

    public List getRepositoryGroupIds() {
        return Arrays.asList(repositoryGroups.keySet().toArray());
    }

    public void addRepository(Repository repository) {
        repositories.put(repository.getId(), repository);
        repositoryOrder.add(repository.getId());

        if (!repositoryGroups.containsKey(repository.getGroupId())) {
            repositoryGroups.put(repository.getGroupId(), new ArrayList());
        }
        List repositoryGroupOrder = (List) repositoryGroups.get(repository.getGroupId());
        repositoryGroupOrder.add(repository.getId());
        logger.info("Added repository id=[" + repository.getId() + "], groupId=[" + repository.getGroupId() + "]");
    }

    public void removeRepository(String repoId) throws NoSuchRepositoryException {
        if (repositories.containsKey(repoId)) {
            Repository repository = (Repository) repositories.get(repoId);
            List repositoryOrder = (List) repositoryGroups.get(repository.getGroupId());
            repositories.remove(repository.getId());
            repositoryOrder.remove(repository);
            if (repositoryOrder.isEmpty()) {
                repositoryGroups.remove(repository.getGroupId());
            }
            logger
                    .info("Removed repository id=[" + repository.getId() + "], groupId=[" + repository.getGroupId()
                            + "]");
        } else {
            throw new NoSuchRepositoryException(repoId);
        }
    }

    public ItemProperties retrieveItemProperties(ProximityRequest request) throws ItemNotFoundException,
            AccessDeniedException, NoSuchRepositoryException {

        logger.debug("Got retrieveItemProperties with " + request);
        accessManager.decide(request, null);
        return retrieveItem(request, true).getProperties();
    }

    public Item retrieveItem(ProximityRequest request) throws ItemNotFoundException, AccessDeniedException,
            NoSuchRepositoryException {

        logger.debug("Got retrieveItem with " + request);
        accessManager.decide(request, null);
        return retrieveItem(request, false);
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
                    logger.info("Repository " + repo.getId() + " unavailable", ex);
                }
            }
            throw new NoSuchRepositoryException(request.getTargetedReposId());

        } else if (request.getTargetedReposGroupId() != null) {

            List repositoryGroupOrder = (List) repositoryGroups.get(request.getTargetedReposGroupId());
            for (Iterator i = repositoryGroupOrder.iterator(); i.hasNext();) {
                String reposId = (String) i.next();
                Repository repo = (Repository) repositories.get(reposId);
                try {
                    response.addAll(repo.listItems(request));
                } catch (RepositoryNotAvailableException ex) {
                    logger.info("Repository " + repo.getId() + " unavailable", ex);
                }
            }
            return response;

        } else {
            for (Iterator i = repositoryOrder.iterator(); i.hasNext();) {
                String reposId = (String) i.next();
                Repository repo = (Repository) repositories.get(reposId);
                try {
                    response.addAll(repo.listItems(request));
                } catch (RepositoryNotAvailableException ex) {
                    logger.info("Repository " + repo.getId() + " unavailable", ex);
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

    protected ProxiedItem retrieveItem(ProximityRequest request, boolean propertiesOnly) throws ItemNotFoundException,
            AccessDeniedException, NoSuchRepositoryException {

        ProxiedItem item = null;

        try {

            if (request.getTargetedReposId() != null) {

                item = retrieveItemByRepoId(request.getTargetedReposId(), request, propertiesOnly);

            } else if (request.getTargetedReposGroupId() != null) {

                item = retrieveItemByRepoGroupId(request.getTargetedReposGroupId(), request, propertiesOnly);

            } else {

                item = retrieveItemByAbsoluteOrder(request, propertiesOnly);

                if (proximityLogic.isGroupSearchNeeded(request, propertiesOnly)) {

                    List repositoryGroupOrder = (List) repositoryGroups
                            .get(item.getProperties().getRepositoryGroupId());
                    List itemList = new ArrayList();

                    for (Iterator i = repositoryGroupOrder.iterator(); i.hasNext();) {
                        String reposId = (String) i.next();
                        try {
                            itemList.add(retrieveItemByRepoId(reposId, request, propertiesOnly));
                        } catch (ItemNotFoundException ex) {
                            logger.debug(request.getPath() + " not found in repository " + reposId);
                        }
                    }

                    item = proximityLogic.postprocessItemList(itemList);

                }

            }

        } catch (IOException ex) {
            logger.error("Got IOException during retrieveItem.", ex);
        } catch (ItemNotFoundException ex) {
            logger.info(request.getPath() + " not found.", ex);
            throw ex;
        }

        return item;

    }

    protected ProxiedItem retrieveItemByAbsoluteOrder(ProximityRequest request, boolean propertiesOnly)
            throws ItemNotFoundException, AccessDeniedException, NoSuchRepositoryException {
        for (Iterator i = repositoryOrder.iterator(); i.hasNext();) {
            String reposId = (String) i.next();
            try {
                Repository repo = (Repository) repositories.get(reposId);
                ProxiedItem item = null;
                if (propertiesOnly) {
                    ItemProperties props = repo.retrieveItemProperties(request);
                    item = new ProxiedItem();
                    item.setProperties(props);
                } else {
                    item = repo.retrieveItem(request);
                }
                return item;
            } catch (RepositoryNotAvailableException ex) {
                logger.info("Repository unavailable", ex);
            } catch (ItemNotFoundException ex) {
                logger.debug(request.getPath() + " not found in repository " + reposId);
            }
        }
        throw new ItemNotFoundException(request.getPath());
    }

    protected ProxiedItem retrieveItemByRepoGroupId(String groupId, ProximityRequest request, boolean propertiesOnly)
            throws ItemNotFoundException, AccessDeniedException, NoSuchRepositoryException {
        List repositoryGroupOrder = (List) repositoryGroups.get(groupId);
        for (Iterator i = repositoryGroupOrder.iterator(); i.hasNext();) {
            String reposId = (String) i.next();
            try {
                Repository repo = (Repository) repositories.get(reposId);
                ProxiedItem item = null;
                if (propertiesOnly) {
                    ItemProperties props = repo.retrieveItemProperties(request);
                    item = new ProxiedItem();
                    item.setProperties(props);
                } else {
                    item = repo.retrieveItem(request);
                }
                return item;
            } catch (RepositoryNotAvailableException ex) {
                logger.info("Repository unavailable", ex);
            } catch (ItemNotFoundException ex) {
                logger.debug(request.getPath() + " not found in repository " + reposId);
            }
        }
        throw new ItemNotFoundException(request.getPath());
    }

    protected ProxiedItem retrieveItemByRepoId(String repoId, ProximityRequest request, boolean propertiesOnly)
            throws ItemNotFoundException, AccessDeniedException, NoSuchRepositoryException {
        if (repositories.containsKey(repoId)) {
            Repository repo = (Repository) repositories.get(repoId);
            try {
                ProxiedItem item = null;
                if (propertiesOnly) {
                    ItemProperties props = repo.retrieveItemProperties(request);
                    item = new ProxiedItem();
                    item.setProperties(props);
                } else {
                    item = repo.retrieveItem(request);
                }
                return item;
            } catch (RepositoryNotAvailableException ex) {
                logger.info("Repository unavailable", ex);
            } catch (ItemNotFoundException ex) {
                logger.debug(request.getPath() + " not found in targeted repository " + repo.getId());
                throw ex;
            }
        }
        throw new NoSuchRepositoryException(request.getTargetedReposId());
    }

}
