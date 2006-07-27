package hu.ismicro.commons.proximity.base.logic;

import java.util.List;

import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.ProximityLogic;

/**
 * The default Proximity logic. It actually does not interfere with Proximity
 * operation known till now.
 * 
 * @author cstamas
 * 
 */
public class DefaultProximityLogic implements ProximityLogic {

    public boolean isGroupSearchNeeded(ProximityRequest request) {
        return false;
    }

    public ProxiedItem postprocessItemList(List listOfProxiedItems) {
        throw new UnsupportedOperationException("The DefaultProximityLogic does not implements postprocessing.");
    }

}
