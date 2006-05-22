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
	ItemProperties retrieveItemProperties(ProximityRequest request) throws ItemNotFoundException, AccessDeniedException, NoSuchRepositoryException;

	/**
	 * Fetches a given item on the supplied path.
	 * 
	 * @param path
	 * @return the wanted Item
	 * @throws ItemNotFoundException
	 *             if Proximity has not found item on the path
	 */
	Item retrieveItem(ProximityRequest request) throws ItemNotFoundException, AccessDeniedException, NoSuchRepositoryException;

	/**
	 * Returns an aggregated List of all item properties in all configured
	 * Repositories. It will ALWAYS return List, at least a 0 length list. Will
	 * not return null or throw exception in normal circumstances.
	 * 
	 * @param path
	 * @return list of ItemProperties, possibly 0 length.
	 */
	List listItems(ProximityRequest request) throws AccessDeniedException, NoSuchRepositoryException;

	/**
	 * Searches for item.
	 * 
	 * @param example
	 * @return List of ItemProperties, possibly 0 length.
	 */
	public List searchItem(ItemProperties example);

	/**
	 * Returns the statistics (if any).
	 * 
	 * @return
	 */
	public Map getStatistics();

}
