package org.abstracthorizon.proximity.logic;

import java.io.IOException;
import java.util.List;

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ProximityRequest;

public interface ProximityLogic {

    /**
         * Returns true if search should be conducted by repository groups and
         * not by absolute order.
         * 
         * @param request
         *                the current request.
         * @param propertiesOnly
         *                if true, this request is about Proximity metadata only
         *                and not content.
         * @return true, if search should be conducted by groups and not the
         *         absolute repository ordering.
         */
    boolean isGroupSearchNeeded(ProximityRequest request);

    ProximityRequest getGroupRequest(ProximityRequest request);

    Item postprocessItemList(ProximityRequest request, ProximityRequest groupRequest, List listOfProxiedItems) throws IOException;

}
