package org.abstracthorizon.proximity.stats;

import java.util.Map;

import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;

public interface StatisticsGatherer {

	void initialize();

	void incomingRequest(ProximityRequest req);

	void localHit(ProximityRequest req, Repository repo, ItemProperties ip);

	void remoteHit(ProximityRequest req, Repository repo, ItemProperties ip);

	Map getStatistics();

}
