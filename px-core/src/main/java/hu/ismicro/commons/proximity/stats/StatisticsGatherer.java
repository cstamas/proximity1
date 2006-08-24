package hu.ismicro.commons.proximity.stats;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.Repository;

import java.util.Map;

public interface StatisticsGatherer {

    void initialize();

    void incomingRequest(ProximityRequest req);

    void localHit(ProximityRequest req, Repository repo, ItemProperties ip);

    void remoteHit(ProximityRequest req, Repository repo, ItemProperties ip);

    Map getStatistics();

}