package hu.ismicro.commons.proximity;

import java.util.List;
import java.util.Map;

public interface Proximity {

	/**
	 * Returns the List of Repositories configured within Proximity.
	 * 
	 * @return List of active repositories.
	 */
	List getRepositories();

	/**
	 * Fetches a given item properties on the supplied path.
	 * 
	 * @param path
	 * @return the wanted ItemProperties
	 * @throws ItemNotFoundException
	 *             if Proximity has not found item on the path
	 */
	ItemProperties retrieveItemProperties(ProximityRequest request) throws ItemNotFoundException, AccessDeniedException;

	/**
	 * Fetches a given item on the supplied path.
	 * 
	 * @param path
	 * @return the wanted Item
	 * @throws ItemNotFoundException
	 *             if Proximity has not found item on the path
	 */
	Item retrieveItem(ProximityRequest request) throws ItemNotFoundException, AccessDeniedException;

	/**
	 * Fetches a given item properties from the given repository.
	 * 
	 * @param path
	 * @param reposId
	 * @return the wanted item properties
	 * @throws ItemNotFoundException
	 *             if Proximity has not found the item in the given repos.
	 * @throws NoSuchRepositoryException
	 */
	ItemProperties retrieveItemPropertiesFromRepository(ProximityRequest request) throws NoSuchRepositoryException,
			ItemNotFoundException, AccessDeniedException;

	/**
	 * Fetches a given item from the given repository.
	 * 
	 * @param path
	 * @param reposId
	 * @return the wanted item
	 * @throws ItemNotFoundException
	 *             if Proximity has not found the item in the given repos.
	 * @throws NoSuchRepositoryException
	 */
	Item retrieveItemFromRepository(ProximityRequest request) throws NoSuchRepositoryException, ItemNotFoundException,
			AccessDeniedException;

	/**
	 * Returns an aggregated List of all item properties in all configured
	 * Repositories. It will ALWAYS return List, at least a 0 length list. Will
	 * not return null or throw exception in normal circumstances.
	 * 
	 * @param path
	 * @return list of ItemProperties, possibly 0 length.
	 */
	List listItems(ProximityRequest request);

	/**
	 * Lists a given repository. If the wanted repository is forbidden for
	 * listing, the exception will be thrown. In any other cases, at least a 0
	 * length list is returned.
	 * 
	 * @param path
	 * @param reposId
	 * @return list of ItemProperties, possibly 0 length.
	 * @throws BrowsingNotAllowedException
	 *             if the repos is forbidden for listing.
	 * @throws NoSuchRepositoryException
	 */
	List listItemsFromRepository(ProximityRequest request) throws NoSuchRepositoryException;

	/**
	 * Searches for item.
	 * 
	 * @param example
	 * @return List of ItemProperties, possibly 0 lenth.
	 */
	public List searchItem(ItemProperties example);

	/**
	 * Returns the statistics (if any).
	 * 
	 * @return
	 */
	public Map getStatistics();

}