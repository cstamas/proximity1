package hu.ismicro.commons.proximity.logic;

import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.impl.ItemImpl;

import java.io.IOException;
import java.util.List;

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

    public ItemImpl postprocessItemList(ProximityRequest request, List listOfProxiedItems) throws IOException {
        throw new UnsupportedOperationException("The DefaultProximityLogic does not implements postprocessing.");
    }

}
