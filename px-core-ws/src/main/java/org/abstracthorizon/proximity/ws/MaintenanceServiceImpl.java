package org.abstracthorizon.proximity.ws;

import org.abstracthorizon.proximity.NoSuchRepositoryException;
import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.indexer.Indexer;

public class MaintenanceServiceImpl implements MaintenanceService {

	private Indexer indexer;

	private Proximity proximity;

	public Indexer getIndexer() {
		return indexer;
	}

	public void setIndexer(Indexer indexer) {
		this.indexer = indexer;
	}

	public Proximity getProximity() {
		return proximity;
	}

	public void setProximity(Proximity proximity) {
		this.proximity = proximity;
	}

	public void reindexAll() {
		indexer.reindex();
	}

	public void reindexRepository(String repoId) throws NoSuchRepositoryException {
		indexer.reindex(repoId);
	}

}
