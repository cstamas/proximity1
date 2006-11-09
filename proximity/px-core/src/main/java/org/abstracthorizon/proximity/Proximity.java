package org.abstracthorizon.proximity;

import java.util.List;
import java.util.Map;

import org.abstracthorizon.proximity.access.AccessManager;

/**
 * The Proximity interface.
 * 
 * @author cstamas
 * 
 */
public interface Proximity extends ProximityRequestMulticaster {

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
	 * Returns the requested Repository by ID.
	 * 
	 * @param repoId
	 * @return
	 * @throws NoSuchRepositoryException
	 */
	Repository getRepository(String repoId) throws NoSuchRepositoryException;

	/**
	 * Adds list of repositories to Proximity. Used during startup.
	 * 
	 * @param repositories
	 */
	void setRepositories(List repositories);

	/**
	 * Returns the list of Repositories that serves Proximity.
	 * 
	 * @return a List<Repository>
	 */
	List getRepositories();

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
	// List

	/**
	 * Returns an aggregated List of all item properties in all configured
	 * Repositories. It will ALWAYS return List, at least a 0 length list. Will
	 * not return null or throw exception in normal circumstances.
	 * 
	 * @param path
	 * @return list of ItemProperties, possibly 0 length.
	 */
	List listItems(ProximityRequest request) throws AccessDeniedException, NoSuchRepositoryException;

}
