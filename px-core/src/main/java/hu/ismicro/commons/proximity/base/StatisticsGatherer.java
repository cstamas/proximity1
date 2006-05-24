package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.Repository;

import java.util.Map;

public interface StatisticsGatherer {
    
    void initialize();
    
    void localHit(ProximityRequest req, Repository repo, ItemProperties ip, boolean propsOnly);

    void remoteHit(ProximityRequest req, Repository repo, ItemProperties ip, boolean propsOnly);
    
    Map getStatistics();

}
