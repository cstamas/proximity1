package org.abstracthorizon.proximity;

import org.abstracthorizon.proximity.events.ProximityRequestEvent;

public interface ProximityRequestMulticaster {

    public void addProximityRequestListener(ProximityRequestListener o);

    public void removeProximityRequestListener(ProximityRequestListener o);

    public void notifyProximityRequestListeners(ProximityRequestEvent event);

}
