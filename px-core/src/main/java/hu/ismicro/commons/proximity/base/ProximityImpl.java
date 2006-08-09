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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProximityImpl implements Proximity {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /** The repo groups, [repo.getGroupId, List&lt;Repositories&gt;] */
    private Map repositoryGroups = new HashMap();

    /** The repo register, [repo.getId, repo] */
    private Map repositories = new HashMap();

    /** The overall order of repositories, List&lt;Repositories&gt;] */
    private List repositoryOrder = new ArrayList();

    /** Should Proximity emerge groupId's in front of real repos paths? */
    boolean emergeGroups = true;

    private ProximityLogic proximityLogic = new DefaultProximityLogic();

    private Indexer indexer;

    private AccessManager accessManager = new OpenAccessManager();

    private StatisticsGatherer statisticsGatherer;

    public boolean isEmergeRepositoryGroups() {
        return this.emergeGroups;
    }

    public void setEmergeRepositoryGroups(boolean emergeGroups) {
        this.emergeGroups = emergeGroups;
    }

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
            repo.setIndexer(getIndexer());
            logger.info("Initializing " + repoId);
            repo.initialize();
        }
    }

    public void reindex() {
        logger.info("Forced reindexing of all defined repositories.");
        for (Iterator i = repositories.keySet().iterator(); i.hasNext();) {
            String repoId = (String) i.next();
            Repository repo = (Repository) repositories.get(repoId);
            repo.reindex();
        }
    }

    public void reindex(String repoId) {
        logger.info("Forced reindexing of " + repoId + " repository.");
        Repository repo = (Repository) repositories.get(repoId);
        repo.reindex();
    }

    public void recreateMetadata() {
        logger.info("Forced metadata recreation of all defined repositories.");
        for (Iterator i = repositories.keySet().iterator(); i.hasNext();) {
            String repoId = (String) i.next();
            Repository repo = (Repository) repositories.get(repoId);
            repo.recreateMetadata();
        }
    }

    public void recreateMetadata(String repoId) {
        logger.info("Forced metadata recreation of " + repoId + " repository.");
        Repository repo = (Repository) repositories.get(repoId);
        repo.recreateMetadata();
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
        for (Iterator i = repositoryOrder.iterator(); i.hasNext();) {
            String repoKey = (String) i.next();
            result.add(repositories.get(repoKey));
        }
        return result;
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
    
    public Map getRepositoryGroups() {
        return repositoryGroups;
    }

    public List getRepositoryGroupIds() {
        return Arrays.asList(repositoryGroups.keySet().toArray());
    }

    public Item retrieveItem(ProximityRequest request) throws ItemNotFoundException, AccessDeniedException,
            NoSuchRepositoryException {

        logger.debug("Got retrieveItem with " + request);
        accessManager.decide(request, null);

        if (isEmergeRepositoryGroups()) {

            ProxiedItem item = null;
            ProxiedItemProperties itemProps = null;
            List pathList = PathHelper.explodePathToList(request.getPath());

            if (pathList.size() >= 1) {

                String groupId = (String) pathList.get(0);
                String originalRequestPath = request.getPath();
                ProximityRequest mangledRequest = new ProximityRequest(request);

                // remove prefix, set it as groupid and go
                if (pathList.size() == 1) {
                    mangledRequest.setPath(PathHelper.PATH_SEPARATOR);
                } else {
                    mangledRequest.setPath(request.getPath().substring(groupId.length() + 1));
                }
                mangledRequest.setTargetedReposGroupId(groupId);
                logger.debug("This is a request for reposGroupId {}. Mangled request and proceeding.", groupId);
                item = retrieveItemController(mangledRequest);
                // on the returns we have to fool the absolutePath
                itemProps = (ProxiedItemProperties) item.getProperties();
                itemProps.setAbsolutePath(originalRequestPath);

            } else {

                // Prepare as root is a dir
                item = new ProxiedItem();
                itemProps = new ProxiedItemProperties();
                itemProps.setAbsolutePath(PathHelper.PATH_SEPARATOR);
                itemProps.setDirectory(true);
                itemProps.setFile(false);
                itemProps.setName("");
                itemProps.setLastModified(null);
                itemProps.setSize(0);
                item.setProperties(itemProps);

            }
            return item;
        } else {
            return retrieveItemController(request);
        }
    }

    public List listItems(ProximityRequest request) throws AccessDeniedException, NoSuchRepositoryException {
        logger.debug("Got listItems with " + request);
        accessManager.decide(request, null);

        List response = new ArrayList();
        List pathList = PathHelper.explodePathToList(request.getPath());
        String groupId = null;

        if (isEmergeRepositoryGroups()) {
            if (pathList.size() >= 1) {
                groupId = (String) pathList.get(0);

                if (pathList.size() == 1) {
                    request.setPath(PathHelper.PATH_SEPARATOR);
                } else {
                    request.setPath(request.getPath().substring(groupId.length() + 1));
                }
                request.setTargetedReposGroupId(groupId);
            } else {
                // return groups as dirs

                if (request.getTargetedReposGroupId() != null) {

                    if (getRepositoryGroupIds().contains(request.getTargetedReposGroupId())) {
                        ProxiedItemProperties itemProps = null;
                        itemProps = new ProxiedItemProperties();
                        itemProps.setAbsolutePath(PathHelper.PATH_SEPARATOR);
                        itemProps.setDirectory(true);
                        itemProps.setFile(false);
                        itemProps.setName(groupId);
                        itemProps.setRepositoryGroupId(groupId);
                        itemProps.setLastModified(null);
                        itemProps.setSize(0);
                        response.add(itemProps);
                    } else {
                        throw new NoSuchRepositoryException("group " + request.getTargetedReposGroupId());
                    }

                } else {

                    ProxiedItemProperties itemProps = null;
                    for (Iterator i = getRepositoryGroupIds().iterator(); i.hasNext();) {

                        groupId = (String) i.next();
                        logger.debug("Adding response with " + groupId + " as directory.");

                        itemProps = new ProxiedItemProperties();
                        itemProps.setAbsolutePath(PathHelper.PATH_SEPARATOR);
                        itemProps.setDirectory(true);
                        itemProps.setFile(false);
                        itemProps.setName(groupId);
                        itemProps.setRepositoryGroupId(groupId);
                        itemProps.setLastModified(null);
                        itemProps.setSize(0);
                        response.add(itemProps);

                    }

                }
                return response;
            }

        }

        logger.debug("Group id is " + groupId);

        if (request.getTargetedReposId() != null) {

            if (repositories.containsKey(request.getTargetedReposId())) {
                Repository repo = (Repository) repositories.get(request.getTargetedReposId());
                try {
                    response.addAll(repo.listItems(request));
                } catch (RepositoryNotAvailableException ex) {
                    logger.info("Repository " + repo.getId() + " unavailable", ex);
                }
            } else {
                throw new NoSuchRepositoryException(request.getTargetedReposId());
            }

        } else if (request.getTargetedReposGroupId() != null) {

            if (repositoryGroups.containsKey(request.getTargetedReposGroupId())) {

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
            } else {
                throw new NoSuchRepositoryException("group " + request.getTargetedReposGroupId());
            }

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
        }

        if (isEmergeRepositoryGroups() && groupId != null) {
            mangleItemPathsForEmergeGroups(response);
        }
        return response;

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
            List result = getIndexer().searchByItemPropertiesExample(example);
            mangleItemPathsForEmergeGroups(result);
            return result;
        } else {
            logger.info("No indexer defined, but search request came in. Returning empty results.");
            return new ArrayList();
        }
    }

    public List searchItem(String query) throws IndexerException {
        logger.debug("Got searchItem with query " + query);
        if (getIndexer() != null) {
            List result = getIndexer().searchByQuery(query);
            mangleItemPathsForEmergeGroups(result);
            return result;
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

    protected ProxiedItem retrieveItemController(ProximityRequest request) throws ItemNotFoundException,
            AccessDeniedException, NoSuchRepositoryException {

        ProxiedItem item = null;

        try {

            if (request.getTargetedReposId() != null) {

                logger.debug("Going for targeted reposId {}", request.getTargetedReposId());
                item = retrieveItemByRepoId(request.getTargetedReposId(), request);

            } else if (request.getTargetedReposGroupId() != null) {

                logger.debug("Going for targeted reposGroupId {}", request.getTargetedReposGroupId());
                item = retrieveItemByRepoGroupId(request.getTargetedReposGroupId(), request);

            } else {

                logger.debug("Going for by absolute order, no target");
                item = retrieveItemByAbsoluteOrder(request);

            }

            // if not a targeted request that affects only one repos and
            // we need group search
            if (request.getTargetedReposId() == null && proximityLogic.isGroupSearchNeeded(request)) {

                List repositoryGroupOrder = (List) repositoryGroups.get(item.getProperties().getRepositoryGroupId());
                List itemList = new ArrayList();

                for (Iterator i = repositoryGroupOrder.iterator(); i.hasNext();) {
                    String reposId = (String) i.next();
                    try {
                        itemList.add(retrieveItemByRepoId(reposId, request));
                    } catch (ItemNotFoundException ex) {
                        logger.debug(request.getPath() + " not found in repository " + reposId);
                    }
                }

                item = proximityLogic.postprocessItemList(request, itemList);

            }

        } catch (IOException ex) {
            logger.error("Got IOException during retrieveItem.", ex);
        } catch (ItemNotFoundException ex) {
            logger.info("Item " + request.getPath() + " not found.");
            throw ex;
        }

        return item;

    }

    protected ProxiedItem retrieveItemByAbsoluteOrder(ProximityRequest request) throws ItemNotFoundException,
            AccessDeniedException, NoSuchRepositoryException {
        for (Iterator i = repositoryOrder.iterator(); i.hasNext();) {
            String reposId = (String) i.next();
            try {
                Repository repo = (Repository) repositories.get(reposId);
                ProxiedItem item = repo.retrieveItem(request);
                return item;
            } catch (RepositoryNotAvailableException ex) {
                logger.info("Repository unavailable", ex);
            } catch (ItemNotFoundException ex) {
                logger.debug(request.getPath() + " not found in repository " + reposId);
            }
        }
        throw new ItemNotFoundException(request.getPath());
    }

    protected ProxiedItem retrieveItemByRepoGroupId(String groupId, ProximityRequest request)
            throws ItemNotFoundException, AccessDeniedException, NoSuchRepositoryException {
        if (repositoryGroups.containsKey(groupId)) {
            List repositoryGroupOrder = (List) repositoryGroups.get(groupId);
            for (Iterator i = repositoryGroupOrder.iterator(); i.hasNext();) {
                String reposId = (String) i.next();
                try {
                    Repository repo = (Repository) repositories.get(reposId);
                    ProxiedItem item = repo.retrieveItem(request);
                    return item;
                } catch (RepositoryNotAvailableException ex) {
                    logger.info("Repository unavailable", ex);
                } catch (ItemNotFoundException ex) {
                    logger.debug(request.getPath() + " not found in repository " + reposId);
                }
            }
        }
        throw new ItemNotFoundException(request.getPath());
    }

    protected ProxiedItem retrieveItemByRepoId(String repoId, ProximityRequest request) throws ItemNotFoundException,
            AccessDeniedException, NoSuchRepositoryException {
        if (repositories.containsKey(repoId)) {
            Repository repo = (Repository) repositories.get(repoId);
            try {
                ProxiedItem item = repo.retrieveItem(request);
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

    protected void mangleItemPathsForEmergeGroups(List items) {
        if (isEmergeRepositoryGroups()) {
            for (Iterator i = items.iterator(); i.hasNext();) {
                ProxiedItemProperties ip = (ProxiedItemProperties) i.next();
                if (ip.getAbsolutePath().equals(PathHelper.PATH_SEPARATOR)) {
                    // make /groupId as path
                    ip.setAbsolutePath(PathHelper.PATH_SEPARATOR + ip.getRepositoryGroupId());
                } else {
                    // make /groupId/... as path WITHOUT trailing /
                    ip.setAbsolutePath(PathHelper.concatPaths(PathHelper.PATH_SEPARATOR + ip.getRepositoryGroupId(), ip
                            .getAbsolutePath()));
                }
            }
        }
    }

}
