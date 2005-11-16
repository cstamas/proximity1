package hu.ismicro.commons.proximity;

import java.util.List;

public interface Repository {

    String getName();

    void setStorage(Storage storage);

    void setRemotePeer(RemotePeer peer);

    void setRepositoryLogic(RepositoryLogic logic);

    ProxiedItem retrieveItem(String path);
    
    List listItems(String path);

}
