package org.abstracthorizon.proximity.indexer;

import java.util.List;

import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.NoSuchRepositoryException;
import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.storage.StorageException;

/**
 * Indexer, a component that simply holds some index and maintenances it as
 * instructed by subsequent method calls.
 * 
 * @author cstamas
 * 
 */
public interface Indexer extends Searchable {

	/**
	 * Returns the indexers instance of proximity.
	 * @return
	 */
	Proximity getProximity();
	
	/**
	 * Sets the proximity instance.
	 * 
	 * @param proximity
	 */
	void setProximity(Proximity proximity);

	/**
	 * Initialize the current indexer implementation.
	 * 
	 */
	public void initialize();

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
	void deleteItemProperties(ItemProperties ip) throws StorageException;

	// ============================================================================================
	// Maintenance

	/**
	 * Forces reindex of repositories.
	 * 
	 */
	void reindex();

	/**
	 * Forces reindex of repository.
	 * 
	 */
	void reindex(String repoId) throws NoSuchRepositoryException;

}
