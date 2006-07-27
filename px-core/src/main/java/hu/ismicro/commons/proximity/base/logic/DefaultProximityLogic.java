package hu.ismicro.commons.proximity.base.logic;

import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.ProximityLogic;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The default Proximity logic. It actually does not interfere with Proximity
 * operation known till now.
 * 
 * @author cstamas
 * 
 */
public class DefaultProximityLogic implements ProximityLogic {
    
    protected Log logger = LogFactory.getLog(this.getClass());

    public boolean isGroupSearchNeeded(ProximityRequest request, boolean propertiesOnly) {
        return false;
    }

    public ProxiedItem postprocessItemList(List listOfProxiedItems) throws IOException {
        throw new UnsupportedOperationException("The DefaultProximityLogic does not implements postprocessing.");
    }

}
