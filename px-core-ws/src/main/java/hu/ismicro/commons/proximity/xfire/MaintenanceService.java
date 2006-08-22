package hu.ismicro.commons.proximity.xfire;


public interface MaintenanceService {

    /**
     * Forces reindex of repositories.
     * 
     */
    void reindexAll();

    /**
     * Forces reindex of repository.
     * 
     */
    void reindexRepository(String repoId);

}
