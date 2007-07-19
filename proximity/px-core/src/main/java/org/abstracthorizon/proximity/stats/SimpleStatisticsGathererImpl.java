package org.abstracthorizon.proximity.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.ProximityRequestListener;
import org.abstracthorizon.proximity.events.ItemCacheEvent;
import org.abstracthorizon.proximity.events.ItemRetrieveEvent;
import org.abstracthorizon.proximity.events.ProximityRequestEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleStatisticsGathererImpl implements StatisticsGatherer, ProximityRequestListener {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private List last10LocalHits = new ArrayList(10);

    private List last10RemoteHits = new ArrayList(10);

    private List last10Artifacts = new ArrayList(10);

    private List last10IpAddresses = new ArrayList(10);

    public void initialize() {
	logger.info("Initializing...");
	// nothing
    }

    public void incomingRequest(ProximityRequest req) {
	// nishta
    }

    public void proximityRequestEvent(ProximityRequestEvent event) {
	if (ItemCacheEvent.class.isAssignableFrom(event.getClass())) {
	    // delete from index
	    remoteHit(event.getRequest(), ((ItemCacheEvent) event).getItemProperties());
	} else if (ItemRetrieveEvent.class.isAssignableFrom(event.getClass())) {
	    // add to index
	    remoteHit(event.getRequest(), ((ItemRetrieveEvent) event).getItemProperties());
	}

    }

    public void localHit(ProximityRequest req, ItemProperties ip) {
	if (ip.isFile()) {
	    addMaxTen(last10Artifacts, ip);
	    addMaxTen(last10LocalHits, ip);
	    if (req.getAttributes().get(ProximityRequest.REQUEST_REMOTE_ADDRESS) != null) {
		addMaxTen(last10IpAddresses, req.getAttributes().get(ProximityRequest.REQUEST_REMOTE_ADDRESS));
	    }
	}
    }

    public void remoteHit(ProximityRequest req, ItemProperties ip) {
	if (ip.isFile()) {
	    addMaxTen(last10Artifacts, ip);
	    addMaxTen(last10RemoteHits, ip);
	    if (req.getAttributes().get(ProximityRequest.REQUEST_REMOTE_ADDRESS) != null) {
		addMaxTen(last10IpAddresses, req.getAttributes().get(ProximityRequest.REQUEST_REMOTE_ADDRESS));
	    }
	}
    }

    public Map getStatistics() {
	Map result = new HashMap();
	result.put("last10LocalHits", last10LocalHits);
	result.put("last10RemoteHits", last10RemoteHits);
	result.put("last10Artifacts", last10Artifacts);
	result.put("last10IpAddresses", last10IpAddresses);
	return new HashMap(result);
    }

    protected void addMaxTen(List list, Object obj) {
	while (list.size() > 10) {
	    list.remove(0);
	}
	if (!list.contains(obj)) {
	    list.add(obj);
	}
    }

}
