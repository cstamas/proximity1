package org.abstracthorizon.proximity.indexer;

import java.util.List;

import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.storage.StorageException;

/**
 * Indexer, a component that simply holds some index and maintenances it as
 * instructed by subsequent method calls.
 * 
 * @author cstamas
 * 
 */
public interface Indexer {

	/**
	 * Initializes indexer. Implementation dependent.
	 * 
	 */
	void initialize();

	/**
	 * Regiters a repository with indexer.
	 * 
	 * @param repository
	 */
	void registerRepository(Repository repository);

	/**
	 * Unregisters a repositort from indexer.
	 * 
	 * @param repository
	 */
	void unregisterRepository(Repository repository);

	/**
	 * Returns the list that this indexer have searchable.
	 * 
	 * @return list of keywords usable in searches.
	 */
	List getSearchableKeywords();

	/**
	 * Adds ItemProperties to index.
	 * 
	 * @param ip
	 * @throws StorageException
	 */
	void addItemProperties(ItemProperties ip) throws StorageException;

	/**
	 * Adds a list of ItemProperties to index.
	 * 
	 * @param itemProperties
	 * @throws StorageException
	 */
	void addItemProperties(List itemProperties) throws StorageException;

	/**
	 * Removes ItemProperties from index.
	 * 
	 * @param ip
	 * @throws ItemNotFoundException
	 * @throws StorageException
	 */
	void deleteItemProperties(ItemProperties ip) throws ItemNotFoundException, StorageException;

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
