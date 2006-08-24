package hu.ismicro.commons.proximity.impl;

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
import hu.ismicro.commons.proximity.indexer.Indexer;
import hu.ismicro.commons.proximity.indexer.IndexerException;
import hu.ismicro.commons.proximity.logic.DefaultProximityLogic;
import hu.ismicro.commons.proximity.logic.ProximityLogic;
import hu.ismicro.commons.proximity.stats.StatisticsGatherer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
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

            ItemImpl item = null;
            ItemPropertiesImpl itemProps = null;
            List pathList = explodePathToList(request.getPath());

            if (pathList.size() >= 1) {

                String groupId = (String) pathList.get(0);
                String originalRequestPath = request.getPath();
                ProximityRequest mangledRequest = new ProximityRequest(request);

                // remove prefix, set it as groupid and go
                if (pathList.size() == 1) {
                    mangledRequest.setPath(ItemProperties.PATH_ROOT);
                } else {
                    mangledRequest.setPath(request.getPath().substring(groupId.length() + 1));
                }
                mangledRequest.setTargetedReposGroupId(groupId);
                logger.debug("This is a request for reposGroupId {}. Mangled request to {} and proceeding.", groupId,
                        mangledRequest.getPath());
                item = retrieveItemController(mangledRequest);
                // on the returns we have to fool the absolutePath
                itemProps = (ItemPropertiesImpl) item.getProperties();
                itemProps.setAbsolutePath(originalRequestPath);

            } else {

                // Prepare as root is a dir
                item = new ItemImpl();
                itemProps = new ItemPropertiesImpl();
                itemProps.setAbsolutePath(ItemProperties.PATH_ROOT);
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
        List pathList = explodePathToList(request.getPath());
        String groupId = null;

        if (isEmergeRepositoryGroups()) {
            if (pathList.size() >= 1) {
                groupId = (String) pathList.get(0);

                // remove groupId prefix from path

                if (pathList.size() == 1) {
                    request.setPath(ItemProperties.PATH_ROOT);
                } else {
                    request.setPath(request.getPath().substring(groupId.length() + 1));
                }
                request.setTargetedReposGroupId(groupId);
            } else {
                // return groups as dirs

                if (request.getTargetedReposGroupId() != null) {

                    if (getRepositoryGroupIds().contains(request.getTargetedReposGroupId())) {
                        ItemPropertiesImpl itemProps = null;
                        itemProps = new ItemPropertiesImpl();
                        itemProps.setAbsolutePath(ItemProperties.PATH_ROOT);
                        itemProps.setDirectory(true);
                        itemProps.setFile(false);
                        itemProps.setName(request.getTargetedReposGroupId());
                        itemProps.setRepositoryGroupId(request.getTargetedReposGroupId());
                        itemProps.setLastModified(null);
                        itemProps.setSize(0);
                        response.add(itemProps);
                    } else {
                        throw new NoSuchRepositoryException("group " + request.getTargetedReposGroupId());
                    }

                } else {

                    ItemPropertiesImpl itemProps = null;
                    for (Iterator i = getRepositoryGroupIds().iterator(); i.hasNext();) {

                        groupId = (String) i.next();
                        logger.debug("Adding response with " + groupId + " as directory.");

                        itemProps = new ItemPropertiesImpl();
                        itemProps.setAbsolutePath(ItemProperties.PATH_ROOT);
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

        if (isEmergeRepositoryGroups()) {
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
            List idxresult = getIndexer().searchByItemPropertiesExample(example);
            return postprocessSearchResult(idxresult);
        } else {
            logger.info("No indexer defined, but search request came in. Returning empty results.");
            return new ArrayList();
        }
    }

    public List searchItem(String query) throws IndexerException {
        logger.debug("Got searchItem with query " + query);
        if (getIndexer() != null) {
            List idxresult = getIndexer().searchByQuery(query);
            return postprocessSearchResult(idxresult);
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

    protected ItemImpl retrieveItemController(ProximityRequest request) throws ItemNotFoundException,
            AccessDeniedException, NoSuchRepositoryException {

        ItemImpl item = null;

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

                ProximityRequest groupRequest = proximityLogic.getGroupRequest(request);

                List repositoryGroupOrder = (List) repositoryGroups.get(item.getProperties().getRepositoryGroupId());
                List itemList = new ArrayList();

                for (Iterator i = repositoryGroupOrder.iterator(); i.hasNext();) {
                    String reposId = (String) i.next();
                    try {
                        itemList.add(retrieveItemByRepoId(reposId, groupRequest));
                    } catch (ItemNotFoundException ex) {
                        logger.debug("[{}] not found in repository {}", groupRequest.getPath(), reposId);
                    }
                }

                item = proximityLogic.postprocessItemList(request, groupRequest, itemList);

            }

        } catch (IOException ex) {
            logger.error("Got IOException during retrieveItem.", ex);
        } catch (ItemNotFoundException ex) {
            logger.info("Item {} not found.", request.getPath());
            throw ex;
        }

        return item;

    }

    protected List postprocessSearchResult(List idxresult) {
        List result = new ArrayList(idxresult.size());
        if (idxresult.size() > 0) {
            ItemProperties ip = null;
            ProximityRequest rq = new ProximityRequest();
            for (Iterator i = idxresult.iterator(); i.hasNext();) {
                ip = (ItemProperties) i.next();
                rq.setPath(ip.getPath());
                rq.setTargetedReposId(ip.getRepositoryId());
                Repository repo = (Repository) repositories.get(ip.getRepositoryId());
                try {
                    result.add(repo.retrieveItemProperties(rq));
                } catch (AccessDeniedException ex) {
                    logger.debug("Access denied on repo {} for path [{}], ignoring it.", ip.getRepositoryId(), ip
                            .getPath());
                } catch (RepositoryNotAvailableException ex) {
                    logger.debug("Repo {} not available, ignoring it.", ip.getRepositoryId());
                } catch (ItemNotFoundException ex) {
                    logger.debug("ItemNotFound on repo {} for path [{}], ignoring it. Maybe repo needs a reindex?", ip
                            .getRepositoryId(), ip.getPath());
                }
            }
            mangleItemPathsForEmergeGroups(result);
        }
        return result;
    }

    protected ItemImpl retrieveItemByAbsoluteOrder(ProximityRequest request) throws ItemNotFoundException,
            AccessDeniedException, NoSuchRepositoryException {
        for (Iterator i = repositoryOrder.iterator(); i.hasNext();) {
            String reposId = (String) i.next();
            try {
                Repository repo = (Repository) repositories.get(reposId);
                ItemImpl item = repo.retrieveItem(request);
                return item;
            } catch (RepositoryNotAvailableException ex) {
                logger.info("Repository unavailable", ex);
            } catch (ItemNotFoundException ex) {
                logger.debug(request.getPath() + " not found in repository " + reposId);
            }
        }
        throw new ItemNotFoundException(request.getPath());
    }

    protected ItemImpl retrieveItemByRepoGroupId(String groupId, ProximityRequest request)
            throws ItemNotFoundException, AccessDeniedException, NoSuchRepositoryException {
        if (repositoryGroups.containsKey(groupId)) {
            List repositoryGroupOrder = (List) repositoryGroups.get(groupId);
            for (Iterator i = repositoryGroupOrder.iterator(); i.hasNext();) {
                String reposId = (String) i.next();
                try {
                    Repository repo = (Repository) repositories.get(reposId);
                    ItemImpl item = repo.retrieveItem(request);
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

    protected ItemImpl retrieveItemByRepoId(String repoId, ProximityRequest request) throws ItemNotFoundException,
            AccessDeniedException, NoSuchRepositoryException {
        if (repositories.containsKey(repoId)) {
            Repository repo = (Repository) repositories.get(repoId);
            try {
                ItemImpl item = repo.retrieveItem(request);
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
                ItemPropertiesImpl ip = (ItemPropertiesImpl) i.next();
                logger.debug("Mangling item path {} with repositoryGroupId {}...", ip.getAbsolutePath(), ip
                        .getRepositoryGroupId());
                if (ip.getAbsolutePath().equals(ItemProperties.PATH_ROOT)) {
                    // make /groupId as path
                    ip.setAbsolutePath(ItemProperties.PATH_ROOT + ip.getRepositoryGroupId());
                } else {
                    // make /groupId/... as path WITHOUT trailing /
                    ip.setAbsolutePath(FilenameUtils.normalizeNoEndSeparator(ItemProperties.PATH_ROOT + ip.getRepositoryGroupId()
                            + ip.getAbsolutePath()));
                }
                logger.debug("Mangled item path {} with repositoryGroupId {}...", ip.getAbsolutePath(), ip
                        .getRepositoryGroupId());
            }
        }
    }

    protected List explodePathToList(String path) {
        List result = new ArrayList();
        String[] explodedPath = path.split(ItemProperties.PATH_SEPARATOR);
        for (int i = 0; i < explodedPath.length; i++) {
            if (explodedPath[i].length() > 0) {
                result.add(explodedPath[i]);
            }
        }
        return result;
    }

}