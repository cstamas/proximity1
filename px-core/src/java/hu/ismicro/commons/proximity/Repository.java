package hu.ismicro.commons.proximity;


import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.RemotePeer;
import hu.ismicro.commons.proximity.base.RepositoryRetrievalLogic;
import hu.ismicro.commons.proximity.base.Storage;
import hu.ismicro.commons.proximity.base.WritableRemotePeer;
import hu.ismicro.commons.proximity.base.WritableStorage;

import java.util.List;

public interface Repository {

    String getName();

    void setStorage(Storage storage);

    void setWritableStorage(WritableStorage storage);

    void setRemotePeer(RemotePeer peer);

    void setWritableRemotePeer(WritableRemotePeer peer);

    void setRepositoryRetrievalLogic(RepositoryRetrievalLogic logic);
    
    ProxiedItem retrieveItem(String path);
    
    List listItems(String path);

}
