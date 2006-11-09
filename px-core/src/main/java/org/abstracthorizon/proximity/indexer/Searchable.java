package org.abstracthorizon.proximity.indexer;

import java.util.List;
import java.util.Set;

import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.storage.StorageException;

public interface Searchable {

	/**
	 * Returns the list that this indexer have searchable.
	 * 
	 * @return list of keywords usable in searches.
	 */
	Set getSearchableKeywords();

	/**
	 * Performs a search using ip as "example".
	 * 
	 * @param ip
	 * @return
	 * @throws StorageException
	 */
	List searchByItemPropertiesExample(ItemProperties ip) throws StorageException;

	/**
	 * Indexer implementation dependent. Performs a search by some query.
	 * 
	 * @param query
	 * @return
	 * @throws IndexerException
	 * @throws StorageException
	 */
	List searchByQuery(String query) throws IndexerException, StorageException;

}
