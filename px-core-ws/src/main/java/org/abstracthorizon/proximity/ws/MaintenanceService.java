package org.abstracthorizon.proximity.ws;


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
    void reindexRepository(String repoId);

}
