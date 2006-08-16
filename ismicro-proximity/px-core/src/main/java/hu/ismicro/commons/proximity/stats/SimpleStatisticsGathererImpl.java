package hu.ismicro.commons.proximity.stats;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleStatisticsGathererImpl implements StatisticsGatherer {

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

    public void localHit(ProximityRequest req, Repository repo, ItemProperties ip) {
        if (ip.isFile()) {
            addMaxTen(last10Artifacts, ip);
            addMaxTen(last10LocalHits, ip);
            if (req.getAttributes().get(ProximityRequest.REQUEST_REMOTE_ADDRESS) != null) {
                addMaxTen(last10IpAddresses, req.getAttributes().get(ProximityRequest.REQUEST_REMOTE_ADDRESS));
            }
        }
    }

    public void remoteHit(ProximityRequest req, Repository repo, ItemProperties ip) {
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
