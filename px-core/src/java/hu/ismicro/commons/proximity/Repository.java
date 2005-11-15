package hu.ismicro.commons.proximity;

public interface Repository {
    
    String getName();
    
    void setStorage(Storage storage);
    
    void setRemotePeer(RemotePeer peer);
    
    void setRepositoryLogic(RepositoryLogic logic);
    
    ProximityResponse handleRequest(ProximityRequest request) throws ProximityException;
    
}
