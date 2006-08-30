package org.abstracthorizon.proximity.logic;


import java.io.IOException;
import java.util.List;

import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.impl.ItemImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default Proximity logic. It actually does not interfere with Proximity
 * operation known till now.
 * 
 * @author cstamas
 * 
 */
public class DefaultProximityLogic implements ProximityLogic {
    
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public boolean isGroupSearchNeeded(ProximityRequest request) {
        return false;
    }
    
    public ProximityRequest getGroupRequest(ProximityRequest request) {
        return request;
    }

    public ItemImpl postprocessItemList(ProximityRequest request, ProximityRequest groupRequest, List listOfProxiedItems) throws IOException {
        throw new UnsupportedOperationException("The DefaultProximityLogic does not implements postprocessing.");
    }

}
