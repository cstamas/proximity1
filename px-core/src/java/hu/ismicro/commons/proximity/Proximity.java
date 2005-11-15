package hu.ismicro.commons.proximity;

import java.util.List;

public interface Proximity {
    
    void setRepositories(List repositories);
    
    ProximityResponse handleRequest(ProximityRequest request) throws ProximityException;
    
}
