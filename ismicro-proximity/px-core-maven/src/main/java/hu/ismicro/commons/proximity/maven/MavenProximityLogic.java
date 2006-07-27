package hu.ismicro.commons.proximity.maven;

import java.util.List;

import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.logic.DefaultProximityLogic;

public class MavenProximityLogic extends DefaultProximityLogic {
    
    public boolean isGroupSearchNeeded(ProximityRequest request) {
        return MavenArtifactRecognizer.isMetadata(request.getPath());
    }

    public ProxiedItem postprocessItemList(List listOfProxiedItems) {
        throw new UnsupportedOperationException("The MavenProximityLogic does not implements postprocessing.");
    }
    

}
