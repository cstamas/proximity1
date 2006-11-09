package org.abstracthorizon.proximity.ws;

import org.abstracthorizon.proximity.NoSuchRepositoryException;

public interface MaintenanceService {

	/**
	 * Forces reindex of all repositories.
	 * 
	 */
	void reindexAll();

	/**
	 * Forces reindex of repository with ID repoId.
	 * 
	 */
	void reindexRepository(String repoId) throws NoSuchRepositoryException;

}
