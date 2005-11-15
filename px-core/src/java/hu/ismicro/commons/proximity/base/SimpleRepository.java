package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ProximityException;
import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.ProximityResponse;
import hu.ismicro.commons.proximity.RemotePeer;
import hu.ismicro.commons.proximity.Repository;
import hu.ismicro.commons.proximity.RepositoryLogic;
import hu.ismicro.commons.proximity.Storage;

import java.util.List;

public class SimpleRepository implements Repository {

    private String name;

    private Storage storage;

    private RemotePeer remotePeer;

    private RepositoryLogic repositoryLogic;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setRemotePeer(RemotePeer remotePeer) {
        this.remotePeer = remotePeer;
    }

    public RemotePeer getRemotePeer() {
        return remotePeer;
    }

    public void setRepositoryLogic(RepositoryLogic repositoryLogic) {
        this.repositoryLogic = repositoryLogic;
    }

    public RepositoryLogic getRepositoryLogic() {
        return repositoryLogic;
    }

    public ProximityResponse handleRequest(ProximityRequest request) throws ProximityException {
        if (request.getPath().endsWith("/")) {
            List items = null;
            if (getStorage() != null) {
                items = getStorage().listItems(request.getPath());
            }
            SimpleProximityResponseList response = new SimpleProximityResponseList();
            response.setItems(items);
            return response;
        } else {
            Item item = null;
            if (getStorage() != null) {
                if (getStorage().containsItem(request.getPath())) {
                    item = getStorage().retrieveItem(request.getPath());
                }
            }
            if (item == null && getRemotePeer() != null) {
                if (getRemotePeer().containsItem(request.getPath())) {
                    item = getRemotePeer().retrieveItem(request.getPath());
                    if (getStorage() != null) {
                        getStorage().storeItem(item);
                        handleRequest(request);
                    }
                }

            }
            if (item != null) {
                SimpleProximityResponse response = new SimpleProximityResponse();
                response.setItem(item);
                return response;
            } else {
                return null;
            }
        }
    }

}
