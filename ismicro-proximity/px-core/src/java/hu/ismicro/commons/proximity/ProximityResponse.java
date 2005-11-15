package hu.ismicro.commons.proximity;


public interface ProximityResponse {
    
    Item getItem();
    
    void mergeResponses(ProximityResponse another);

}
