package hu.ismicro.commons.proximity;


import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.RemotePeer;
import hu.ismicro.commons.proximity.base.Storage;

import java.util.List;

public interface Repository {

    String getId();
    
    String getName();

    void setStorage(Storage storage);

    void setRemotePeer(RemotePeer peer);

    void setRepositoryRetrievalLogic(RepositoryRetrievalLogic logic);
    
    ProxiedItem retrieveItem(String path);
    
    List listItems(String path);

}
