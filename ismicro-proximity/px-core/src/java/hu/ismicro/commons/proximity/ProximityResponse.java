package hu.ismicro.commons.proximity;


public interface ProximityResponse {
    
    Item getItem();
    
    boolean isMergeableResponse();
    
    void mergeResponses(ProximityResponse another);

}
