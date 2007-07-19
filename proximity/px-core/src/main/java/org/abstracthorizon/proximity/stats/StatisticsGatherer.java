package org.abstracthorizon.proximity.stats;

import java.util.Map;

import org.abstracthorizon.proximity.Proximity;

public interface StatisticsGatherer {

    Proximity getProximity();

    void setProximity(Proximity proximity);

    void initialize();
    
    Map getStatistics();

}
