package hu.ismicro.commons.proximity;

public interface Repository {
    
    String getName();
    
    int getPriority();
    
    void setStorage(Storage storage);
    
    void setRemotePeer(RemotePeer peer);
    
    void setArtifactCache();

}
