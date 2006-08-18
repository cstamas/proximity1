package hu.ismicro.commons.proximity.xfire;

import hu.ismicro.commons.proximity.Proximity;

public class MaintenanceServiceImpl implements MaintenanceService {
    
    private Proximity proximity;
    

    public Proximity getProximity() {
        return proximity;
    }

    public void setProximity(Proximity proximity) {
        this.proximity = proximity;
    }

    public void reindex() {
        proximity.reindex();
    }

    public void reindex(String repoId) {
        proximity.reindex(repoId);
    }

}
