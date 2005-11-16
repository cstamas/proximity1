package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.Repository;

public interface InnerRepository extends Repository {
    
    void setName(String name);
    
    Storage getStorage();
    
    WritableStorage getWritableStorage();
    
    RemotePeer getRemotePeer();
    
    WritableRemotePeer getWritableRemotePeer();

}
