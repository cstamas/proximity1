package org.abstracthorizon.proximity.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.HashMapItemPropertiesImpl;
import org.abstracthorizon.proximity.NoSuchRepositoryException;
import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.RepositoryNotAvailableException;
import org.abstracthorizon.proximity.access.AccessManager;
import org.abstracthorizon.proximity.access.OpenAccessManager;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractProximity implements Proximity {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /** The repo groups, [repo.getGroupId, List&lt;Repositories&gt;] */
    protected Map repositoryGroups = new HashMap();

    /** The repo register, [repo.getId, repo] */
    protected Map repositories = new HashMap();

    /** The overall order of repositories, List&lt;Repositories&gt;] */
    protected List repositoryOrder = new ArrayList();

    /** Should Proximity emerge groupId's in front of real repos paths? */
    protected boolean emergeGroups = true;

    private AccessManager accessManager = new OpenAccessManager();

    public boolean isEmergeRepositoryGroups() {
        return this.emergeGroups;
    }

    public void setEmergeRepositoryGroups(boolean emergeGroups) {
        this.emergeGroups = emergeGroups;
    }

    public AccessManager getAccessManager() {
        return accessManager;
    }

    public void setAccessManager(AccessManager accessManager) {
        this.accessManager = accessManager;
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
        logger.info("Forced reindexing of {} repository", repoId);
        Repository repo = (Repository) repositories.get(repoId);
        repo.reindex();
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
        addToRepositoryIdToRankedList(repositoryOrder, repository);

        if (!repositoryGroups.containsKey(repository.getGroupId())) {
            repositoryGroups.put(repository.getGroupId(), new ArrayList());
        }
        List repositoryGroupOrder = (List) repositoryGroups.get(repository.getGroupId());
        addToRepositoryIdToRankedList(repositoryGroupOrder, repository);

        logger.info("Added repository id=[{}], groupId=[{}].", repository.getId(), repository.getGroupId());
    }

    public void removeRepository(String repoId) throws NoSuchRepositoryException {
        if (repositories.containsKey(repoId)) {
            Repository repository = (Repository) repositories.get(repoId);
            List repositoryGroupOrder = (List) repositoryGroups.get(repository.getGroupId());
            repositoryGroupOrder.remove(repository.getId());
            if (repositoryGroupOrder.isEmpty()) {
                repositoryGroups.remove(repository.getGroupId());
            }
            repositoryOrder.remove(repository.getId());
            repositories.remove(repository.getId());

            logger.info("Removed repository id=[{}], groupId=[{}]", repository.getId(), repository.getGroupId());
        } else {
            throw new NoSuchRepositoryException(repoId);
        }
    }

    public Map getRepositoryGroups() {
        return repositoryGroups;
    }

    public List getRepositoryIds() {
        List ids = new ArrayList(repositoryOrder.size());
        ids.addAll(repositoryOrder);
        Collections.sort(ids);
        return ids;
    }

    public List getRepositoryGroupIds() {
        Object[] groupIds = repositoryGroups.keySet().toArray();
        Arrays.sort(groupIds);
        return Arrays.asList(groupIds);
    }

    public Item retrieveItem(ProximityRequest request) throws ItemNotFoundException, AccessDeniedException,
            NoSuchRepositoryException {

        logger.debug("Got retrieveItem with {}", request);
        accessManager.decide(request, null);

        if (isEmergeRepositoryGroups()) {

            Item item = null;
            ItemProperties itemProps = null;
            List pathList = explodePathToList(request.getPath());

            if (pathList.size() >= 1) {

                mangleItemRequest(request);

                String originalRequestPath = request.getPath();
                ProximityRequest mangledRequest = mangleItemRequest(request);

                logger.debug("This is a request for reposGroupId {}. Mangled request to {} and proceeding.",
                        mangledRequest.getTargetedReposGroupId(), mangledRequest.getPath());
                item = retrieveItemController(mangledRequest);
                // on the returns we have to fool the absolutePath
                itemProps = (ItemProperties) item.getProperties();
                itemProps.setDirectoryPath(originalRequestPath);

            } else {

                // Prepare as root is a dir
                item = new Item();
                itemProps = new HashMapItemPropertiesImpl();
                itemProps.setDirectoryPath(ItemProperties.PATH_ROOT);
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

    public void copyItem(ProximityRequest source, ProximityRequest target) throws ItemNotFoundException,
            AccessDeniedException, NoSuchRepositoryException, RepositoryNotAvailableException {
        logger.debug("Got copyItem with {} -> {}", source, target);
        accessManager.decide(source, null);
        accessManager.decide(target, null);
        Item item = retrieveItem(source);
        ItemProperties itemProps = item.getProperties();
        itemProps.setDirectoryPath(FilenameUtils.separatorsToUnix(FilenameUtils.getFullPathNoEndSeparator(target.getPath())));
        itemProps.setName(FilenameUtils.getName(target.getPath()));
        itemProps.setRepositoryId(target.getTargetedReposId());
        itemProps.setRepositoryGroupId(target.getTargetedReposGroupId());
        storeItem(target, item);
    }

    public void deleteItem(ProximityRequest request) throws ItemNotFoundException, AccessDeniedException,
            NoSuchRepositoryException, RepositoryNotAvailableException {
        logger.debug("Got deleteItem with {}", request);
        accessManager.decide(request, null);
        Item item = retrieveItem(request);
        Repository repo = (Repository) repositories.get(item.getProperties().getRepositoryId());
        repo.deleteItem(mangleItemRequest(request));
    }

    public void moveItem(ProximityRequest source, ProximityRequest target) throws ItemNotFoundException,
            AccessDeniedException, NoSuchRepositoryException, RepositoryNotAvailableException {
        logger.debug("Got moveItem with {} -> {}", source, target);
        accessManager.decide(source, null);
        accessManager.decide(target, null);
        copyItem(source, target);
        deleteItem(source);
    }

    public void storeItem(ProximityRequest request, Item item) throws AccessDeniedException, NoSuchRepositoryException,
            RepositoryNotAvailableException {
        logger.debug("Got storeItem for {}", request);
        accessManager.decide(request, null);

        String targetRepoId = request.getTargetedReposId();
        List pathList = explodePathToList(request.getPath());

        if (targetRepoId == null) {

            // first repo if not targeted and store
            if (isEmergeRepositoryGroups()) {
                if (pathList.size() == 0) {
                    // cannot store on root if emergeGroups, error
                    throw new AccessDeniedException(request,
                            "Cannot store item on the root when emergeRepositoryGroups are enabled!");
                }
                // get group, get first repo of the group and store
                String groupId = (String) pathList.get(0);
                if (repositoryGroups.containsKey(groupId)) {
                    List repositoryGroupOrder = (List) repositoryGroups.get(groupId);
                    targetRepoId = (String) repositoryGroupOrder.get(0);
                } else {
                    throw new NoSuchRepositoryException("group " + request.getTargetedReposGroupId());
                }
            } else {
                // get first repo and store
                targetRepoId = (String) repositoryOrder.get(0);
            }
        }

        if (repositories.containsKey(targetRepoId)) {
            Repository repo = (Repository) repositories.get(targetRepoId);
            ProximityRequest mangledRequest = mangleItemRequest(request);
            ItemProperties itemProperties = item.getProperties();
            // set the mangled path for store
            itemProperties.setDirectoryPath(FilenameUtils.separatorsToUnix(FilenameUtils.getFullPathNoEndSeparator(mangledRequest.getPath())));
            repo.storeItem(mangledRequest, item);
        } else {
            throw new NoSuchRepositoryException(targetRepoId);
        }
    }

    public List listItems(ProximityRequest request) throws AccessDeniedException, NoSuchRepositoryException {
        logger.debug("Got listItems with {}", request);
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
                        ItemProperties itemProps = null;
                        itemProps = new HashMapItemPropertiesImpl();
                        itemProps.setDirectoryPath(ItemProperties.PATH_ROOT);
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

                    HashMapItemPropertiesImpl itemProps = null;
                    for (Iterator i = getRepositoryGroupIds().iterator(); i.hasNext();) {

                        groupId = (String) i.next();
                        logger.debug("Adding response with {} as directory.", groupId);

                        itemProps = new HashMapItemPropertiesImpl();
                        itemProps.setDirectoryPath(ItemProperties.PATH_ROOT);
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

        logger.debug("Group id is {}", groupId);

        if (request.getTargetedReposId() != null) {

            if (repositories.containsKey(request.getTargetedReposId())) {
                Repository repo = (Repository) repositories.get(request.getTargetedReposId());
                try {
                    response.addAll(repo.listItems(request));
                } catch (RepositoryNotAvailableException ex) {
                    logger.info("Repository {} unavailable!", repo.getId(), ex);
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
                        logger.info("Repository {} unavailable!", repo.getId(), ex);
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
                    logger.info("Repository {} unavailable!", repo.getId(), ex);
                }
            }
        }

        if (isEmergeRepositoryGroups()) {
            mangleItemPathsForEmergeGroups(response);
        }
        return response;

    }

    protected abstract Item retrieveItemController(ProximityRequest request) throws ItemNotFoundException,
            AccessDeniedException, NoSuchRepositoryException;

    protected List postprocessSearchResult(List idxresult) {
        List result = new ArrayList(idxresult.size());
        if (idxresult.size() > 0) {
            ItemProperties ip = null;
            ProximityRequest rq = new ProximityRequest();
            rq.setLocalOnly(true);
            rq.setPropertiesOnly(true);
            for (Iterator i = idxresult.iterator(); i.hasNext();) {
                ip = (ItemProperties) i.next();
                rq.setPath(ip.getPath());
                rq.setTargetedReposId(ip.getRepositoryId());
                Repository repo = (Repository) repositories.get(ip.getRepositoryId());
                try {
                    result.add(repo.retrieveItem(rq));
                } catch (AccessDeniedException ex) {
                    logger.debug("Access denied on repo {} for path [{}], ignoring it.", ip.getRepositoryId(), ip
                            .getPath());
                } catch (RepositoryNotAvailableException ex) {
                    logger.debug("Repo {} not available, ignoring it.", ip.getRepositoryId());
                } catch (ItemNotFoundException ex) {
                    logger
                            .info(
                                    "ItemNotFound on repo {} for path [{}] but index contains it, ignoring. Maybe repo needs a reindex?",
                                    ip.getRepositoryId(), ip.getPath());
                }
            }
            mangleItemPathsForEmergeGroups(result);
        }
        return result;
    }

    /**
     * If emergeGroups is on, then the first element of request path is actually
     * a repos group. This method processes the request in a proper way. If
     * emergeRepositoryGroups are off, it returns the original request. If the
     * request have no first element (eg. "/"), the originial request is
     * returned. Othwerwise, it constructs another request, strips of the first
     * element from path, sets it as targetedRepositoryGroupId and returns a
     * this modified request. In all other cases, the same instance of request
     * is returned!
     * 
     * @param request
     * @return
     */
    protected ProximityRequest mangleItemRequest(ProximityRequest request) {
        if (!isEmergeRepositoryGroups()) {
            return request;
        }
        List pathList = explodePathToList(request.getPath());
        if (pathList.size() < 1) {
            return request;
        } else {

            String groupId = (String) pathList.get(0);
            ProximityRequest mangledRequest = new ProximityRequest(request);

            // remove prefix, set it as groupid and go
            if (pathList.size() == 1) {
                mangledRequest.setPath(ItemProperties.PATH_ROOT);
            } else {
                mangledRequest.setPath(request.getPath().substring(groupId.length() + 1));
            }
            mangledRequest.setTargetedReposGroupId(groupId);
            return mangledRequest;
        }

    }

    protected void mangleItemPathsForEmergeGroups(List items) {
        if (isEmergeRepositoryGroups()) {
            for (Iterator i = items.iterator(); i.hasNext();) {
                ItemProperties ip = (ItemProperties) i.next();
                logger.debug("Mangling item path {} with repositoryGroupId {}...", ip.getDirectoryPath(), ip
                        .getRepositoryGroupId());
                if (ip.getDirectoryPath().equals(ItemProperties.PATH_ROOT)) {
                    // make /groupId as path
                    ip.setDirectoryPath(ItemProperties.PATH_ROOT + ip.getRepositoryGroupId());
                } else {
                    // make /groupId/... as path WITHOUT trailing /
                    ip.setDirectoryPath(FilenameUtils.separatorsToUnix(FilenameUtils.normalizeNoEndSeparator(ItemProperties.PATH_ROOT
                            + ip.getRepositoryGroupId() + ip.getDirectoryPath())));
                }
                logger.debug("Mangled item path {} with repositoryGroupId {}...", ip.getDirectoryPath(), ip
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

    protected void addToRepositoryIdToRankedList(List listOfReposes, Repository repo) {
        if (listOfReposes.size() > 0) {
            Repository current = (Repository) repositories.get((String) listOfReposes.get(0));
            Iterator i = listOfReposes.iterator();
            while (i.hasNext() && current.getRank() < repo.getRank()) {
                current = (Repository) repositories.get((String) i.next());
            }
            if (current.getRank() < repo.getRank()) {
                listOfReposes.add(repo.getId());
            } else {
                listOfReposes.add(listOfReposes.indexOf(current.getId()), repo.getId());
            }
        } else {
            listOfReposes.add(repo.getId());
        }
    }

}
