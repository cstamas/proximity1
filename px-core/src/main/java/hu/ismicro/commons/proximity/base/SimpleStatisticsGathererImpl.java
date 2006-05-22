package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimpleStatisticsGathererImpl implements StatisticsGatherer {

    protected Log logger = LogFactory.getLog(this.getClass());

    private static final String LOCAL_STATS = "local";

    private static final String REMOTE_STATS = "remote";

    private static final String PROPS_RETRIEVAL = "propertiesRetrieval";

    private static final String ITEM_RETRIEVAL = "retrieval";

    private static final String REQUEST_REMOTE_ADDRESS_LIST = "remoteAddressList";

    private Map stats = new HashMap();

    public void localHit(ProximityRequest req, Repository repo, ItemProperties ip, boolean propsOnly) {
        if (ip.isFile()) {
            addHit(LOCAL_STATS, req, ip, repo, propsOnly);
        }
    }

    public void remoteHit(ProximityRequest req, Repository repo, ItemProperties ip, boolean propsOnly) {
        if (ip.isFile()) {
            addHit(REMOTE_STATS, req, ip, repo, propsOnly);
        }
    }

    public Map getStatistics() {
        return new HashMap(stats);
    }

    protected void addHit(String LOCAL_OR_REMOTE_KEY, ProximityRequest req, ItemProperties ip, Repository repo,
            boolean propsOnly) {
        // TODO: Very very primitive!
        if (!stats.containsKey(ip)) {
            stats.put(ip, new HashMap());
        }
        Map ipStats = (Map) stats.get(ip);
        if (!ipStats.containsKey(repo.getId())) {
            ipStats.put(repo.getId(), new HashMap());
        }
        Map repoStats = (Map) ipStats.get(repo.getId());
        if (!repoStats.containsKey(LOCAL_OR_REMOTE_KEY)) {
            repoStats.put(LOCAL_OR_REMOTE_KEY, new HashMap());
        }
        Map localStats = (Map) repoStats.get(LOCAL_OR_REMOTE_KEY);
        if (!localStats.containsKey(propsOnly ? PROPS_RETRIEVAL : ITEM_RETRIEVAL)) {
            localStats.put(propsOnly ? PROPS_RETRIEVAL : ITEM_RETRIEVAL, new Integer(0));
        }
        int count = ((Integer) localStats.get(propsOnly ? PROPS_RETRIEVAL : ITEM_RETRIEVAL)).intValue() + 1;
        logger.debug("Increasing hit for " + ip.getPath() + " to " + count);
        localStats.put(propsOnly ? PROPS_RETRIEVAL : ITEM_RETRIEVAL, new Integer(count));

        // if we have front-end that records this
        if (req.getAttributes().get(ProximityRequest.REQUEST_REMOTE_ADDRESS) != null) {
            if (!localStats.containsKey(REQUEST_REMOTE_ADDRESS_LIST)) {
                localStats.put(REQUEST_REMOTE_ADDRESS_LIST, new ArrayList());
            }
            ArrayList remoteAddresses = (ArrayList) localStats.get(REQUEST_REMOTE_ADDRESS_LIST);
            remoteAddresses.add(req.getAttributes().get(ProximityRequest.REQUEST_REMOTE_ADDRESS));
        }
    }

}
