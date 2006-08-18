package hu.ismicro.commons.proximity.xfire;


public interface MaintenanceService {

    /**
     * Forces reindex of repositories.
     * 
     */
    void reindex();

    /**
     * Forces reindex of repository.
     * 
     */
    void reindex(String repoId);

}
