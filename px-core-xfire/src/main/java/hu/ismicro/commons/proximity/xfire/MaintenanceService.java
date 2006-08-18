package hu.ismicro.commons.proximity.xfire;

import java.util.Map;

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

    /**
     * Returns the statistics (if any).
     * 
     * @todo
     * @return
     */
    public Map getStatistics();

}
