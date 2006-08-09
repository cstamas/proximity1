package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ProximityRequest;

import java.io.IOException;
import java.util.List;

public interface ProximityLogic {

    /**
     * Returns true if search should be conducted by repository groups and not
     * by absolute order.
     * 
     * @param request
     *            the current request.
     * @param propertiesOnly
     *            if true, this request is about Proximity metadata only and not
     *            content.
     * @return true, if search should be conducted by groups and not the
     *         absolute repository ordering.
     */
    boolean isGroupSearchNeeded(ProximityRequest request);

    ProxiedItem postprocessItemList(ProximityRequest request, List listOfProxiedItems) throws IOException;

}
