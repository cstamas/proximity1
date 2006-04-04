package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.Repository;

import java.util.Map;

public interface StatisticsGatherer {
    
    void localHit(Repository repo, ItemProperties ip, boolean propsOnly);

    void remoteHit(Repository repo, ItemProperties ip, boolean propsOnly);
    
    Map getStatistics();

}
