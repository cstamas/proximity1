package org.abstracthorizon.proximity.ws;

import org.abstracthorizon.proximity.Proximity;

public class MaintenanceServiceImpl implements MaintenanceService {

    private Proximity proximity;

    public Proximity getProximity() {
        return proximity;
    }

    public void setProximity(Proximity proximity) {
        this.proximity = proximity;
    }

    public void reindexAll() {
        proximity.reindex();
    }

    public void reindexRepository(String repoId) {
        proximity.reindex(repoId);
    }

}
