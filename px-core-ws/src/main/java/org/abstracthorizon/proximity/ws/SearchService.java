package org.abstracthorizon.proximity.ws;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.NoSuchRepositoryException;
import org.abstracthorizon.proximity.ProximityException;
import org.abstracthorizon.proximity.ProximityRequest;

public interface SearchService {

	/**
	 * Returns an aggregated List of all item properties in all configured
	 * Repositories. It will ALWAYS return List, at least a 0 length list. Will
	 * not return null or throw exception in normal circumstances.
	 * 
	 * @param path
	 * @return list of ItemProperties, possibly 0 length.
	 */
	ItemProperties[] listItems(ProximityRequest request) throws AccessDeniedException, NoSuchRepositoryException;

	/**
	 * Lists the searchable keywords as returned by Indexer.
	 * 
	 * @return the list of keywords usable in queries.
	 */
	String[] getSearchableKeywords();

	/**
	 * Searches for item.
	 * 
	 * @param example
	 * @return List of ItemProperties, possibly 0 length.
	 */
	ItemProperties[] searchItemByExample(ItemProperties example) throws ProximityException;

	/**
	 * Searches for item.
	 * 
	 * @param query,
	 *            dependent on indexer backend since Proximity just "passes" it.
	 * 
	 * @return List of ItemProperties, possibly 0 length.
	 */
	ItemProperties[] searchItemByQuery(String query) throws ProximityException;

}
