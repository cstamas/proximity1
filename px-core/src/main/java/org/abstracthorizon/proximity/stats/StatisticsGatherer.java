package org.abstracthorizon.proximity.stats;

import java.util.Map;

public interface StatisticsGatherer {

    void initialize();

    Map getStatistics();

}
