package org.abstracthorizon.proximity.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.HashMapItemPropertiesImpl;
import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.NoSuchRepositoryException;
import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.ProximityRequestListener;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.RepositoryNotAvailableException;
import org.abstracthorizon.proximity.access.AccessManager;
import org.abstracthorizon.proximity.access.OpenAccessManager;
import org.abstracthorizon.proximity.events.ProximityRequestEvent;
import org.abstracthorizon.proximity.mapping.GroupRequestMapper;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractProximity implements Proximity, ProximityRequestListener {

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

    private GroupRequestMapper groupRequestMapper;

    private Vector requestListeners = new Vector();

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

    public GroupRequestMapper getGroupRequestMapper() {
	return groupRequestMapper;
    }

    public void setGroupRequestMapper(GroupRequestMapper groupRequestMapper) {
	this.groupRequestMapper = groupRequestMapper;
    }

    public List getRepositories() {
	List result = new ArrayList();
	for (Iterator i = repositoryOrder.iterator(); i.hasNext();) {
	    String repoKey = (String) i.next();
	    result.add(repositories.get(repoKey));
	}
	return result;
    }

    public void setRepositories(List repositories) {
	for (Iterator i = repositories.iterator(); i.hasNext();) {
	    addRepository((Repository) i.next());
	}
    }

    public void addRepository(Repository repository) {
	repositories.put(repository.getId(), repository);
	addToRepositoryIdToRankedList(repositoryOrder, repository);

	if (!repositoryGroups.containsKey(repository.getGroupId())) {
	    repositoryGroups.put(repository.getGroupId(), new ArrayList());
	}
	List repositoryGroupOrder = (List) repositoryGroups.get(repository.getGroupId());
	addToRepositoryIdToRankedList(repositoryGroupOrder, repository);

	repository.addProximityRequestListener(this);

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

	    repository.removeProximityRequestListener(this);

	    logger.info("Removed repository id=[{}], groupId=[{}]", repository.getId(), repository.getGroupId());
	} else {
	    throw new NoSuchRepositoryException(repoId);
	}
    }

    public Map getRepositoryGroups() {
	return repositoryGroups;
    }

    public Repository getRepository(String repoId) throws NoSuchRepositoryException {
	if (repositories.containsKey(repoId)) {
	    Repository repository = (Repository) repositories.get(repoId);
	    return repository;
	} else {
	    throw new NoSuchRepositoryException(repoId);
	}

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

    public Item retrieveItem(ProximityRequest request) throws ItemNotFoundException, AccessDeniedException, NoSuchRepositoryException {

	logger.debug("Got retrieveItem with {}", request);
	accessManager.decide(request, null);

	Item item = null;

	if (isEmergeRepositoryGroups()) {

	    ItemProperties itemProps = null;
	    List pathList = ProximityUtils.explodePathToList(request.getPath());

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
	} else {
	    item = retrieveItemController(request);
	}
	return item;
    }

    public void copyItem(ProximityRequest source, ProximityRequest target) throws ItemNotFoundException, AccessDeniedException,
	    NoSuchRepositoryException, RepositoryNotAvailableException {
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

    public void deleteItem(ProximityRequest request) throws ItemNotFoundException, AccessDeniedException, NoSuchRepositoryException,
	    RepositoryNotAvailableException {
	logger.debug("Got deleteItem with {}", request);
	accessManager.decide(request, null);
	Item item = retrieveItem(request);
	Repository repo = (Repository) repositories.get(item.getProperties().getRepositoryId());
	repo.deleteItem(mangleItemRequest(request));
    }

    public void moveItem(ProximityRequest source, ProximityRequest target) throws ItemNotFoundException, AccessDeniedException,
	    NoSuchRepositoryException, RepositoryNotAvailableException {
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
	List pathList = ProximityUtils.explodePathToList(request.getPath());

	if (targetRepoId == null) {

	    // first repo if not targeted and store
	    if (isEmergeRepositoryGroups()) {
		if (pathList.size() == 0) {
		    // cannot store on root if emergeGroups, error
		    throw new AccessDeniedException(request, "Cannot store item on the root when emergeRepositoryGroups are enabled!");
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
	List pathList = ProximityUtils.explodePathToList(request.getPath());
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
	    mangleItemPathsForEmergeGroupsIfNeeded(response);
	}
	return response;

    }

    public void addProximityRequestListener(ProximityRequestListener o) {
	requestListeners.add(o);
    }

    public void removeProximityRequestListener(ProximityRequestListener o) {
	requestListeners.remove(o);
    }

    public void proximityRequestEvent(ProximityRequestEvent event) {
	// we are just proxying the events for all our reposes
	notifyProximityRequestListeners(event);
    }

    public void notifyProximityRequestListeners(ProximityRequestEvent event) {
	synchronized (requestListeners) {
	    for (Iterator i = requestListeners.iterator(); i.hasNext();) {
		ProximityRequestListener l = (ProximityRequestListener) i.next();
		try {
		    l.proximityRequestEvent(event);
		} catch (Exception e) {
		    logger.info("Unexpected exception in listener", e);
		    i.remove();
		}
	    }
	}
    }

    protected abstract Item retrieveItemController(ProximityRequest request) throws ItemNotFoundException, AccessDeniedException,
	    NoSuchRepositoryException;

    protected Item retrieveItemByAbsoluteOrder(ProximityRequest request) throws ItemNotFoundException, AccessDeniedException,
	    NoSuchRepositoryException {
	for (Iterator i = repositoryOrder.iterator(); i.hasNext();) {
	    String reposId = (String) i.next();
	    try {
		Repository repo = (Repository) repositories.get(reposId);
		Item item = repo.retrieveItem(request);
		return item;
	    } catch (RepositoryNotAvailableException ex) {
		logger.info("Repository unavailable", ex);
	    } catch (ItemNotFoundException ex) {
		logger.debug(request.getPath() + " not found in repository " + reposId);
	    }
	}
	throw new ItemNotFoundException(request.getPath());
    }

    protected Item retrieveItemByRepoGroupId(String groupId, ProximityRequest request) throws ItemNotFoundException, AccessDeniedException,
	    NoSuchRepositoryException {
	if (repositoryGroups.containsKey(groupId)) {
	    List repositoryGroupOrder = (List) repositoryGroups.get(groupId);

	    if (getGroupRequestMapper() != null) {
		repositoryGroupOrder = getGroupRequestMapper().getMappedRepositories(groupId, request, repositoryGroupOrder);
	    }

	    for (Iterator i = repositoryGroupOrder.iterator(); i.hasNext();) {
		String reposId = (String) i.next();
		try {
		    Repository repo = (Repository) repositories.get(reposId);
		    Item item = repo.retrieveItem(request);
		    return item;
		} catch (RepositoryNotAvailableException ex) {
		    logger.info("Repository {} unavailable, skipping it.", reposId);
		} catch (ItemNotFoundException ex) {
		    logger.debug(request.getPath() + " not found in repository " + reposId);
		}
	    }
	}
	throw new ItemNotFoundException(request.getPath());
    }

    protected Item retrieveItemByRepoId(String repoId, ProximityRequest request) throws ItemNotFoundException, AccessDeniedException,
	    NoSuchRepositoryException {
	if (repositories.containsKey(repoId)) {
	    Repository repo = (Repository) repositories.get(repoId);
	    try {
		Item item = repo.retrieveItem(request);
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

    /**
         * If emergeGroups is on, then the first element of request path is
         * actually a repos group. This method processes the request in a proper
         * way. If emergeRepositoryGroups are off, it returns the original
         * request. If the request have no first element (eg. "/"), the
         * originial request is returned. Othwerwise, it constructs another
         * request, strips of the first element from path, sets it as
         * targetedRepositoryGroupId and returns a this modified request. In all
         * other cases, the same instance of request is returned!
         * 
         * @param request
         * @return
         */
    protected ProximityRequest mangleItemRequest(ProximityRequest request) {
	if (!isEmergeRepositoryGroups()) {
	    return request;
	}
	List pathList = ProximityUtils.explodePathToList(request.getPath());
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

    protected void mangleItemPathsForEmergeGroupsIfNeeded(List items) {
	if (isEmergeRepositoryGroups()) {
	    ProximityUtils.mangleItemPathsForEmergeGroups(items);
	}
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
