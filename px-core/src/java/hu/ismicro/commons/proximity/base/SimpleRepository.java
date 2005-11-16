package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.BrowsingNotAllowedException;
import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.ProximityException;
import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.ProximityResponse;
import hu.ismicro.commons.proximity.RemotePeer;
import hu.ismicro.commons.proximity.Repository;
import hu.ismicro.commons.proximity.RepositoryLogic;
import hu.ismicro.commons.proximity.Storage;
import hu.ismicro.commons.proximity.WritableRemotePeer;
import hu.ismicro.commons.proximity.WritableStorage;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimpleRepository implements Repository {

    protected Log logger = LogFactory.getLog(this.getClass());

    private String name;

    private Storage storage;
    
    private WritableStorage writableStorage;

    private RemotePeer remotePeer;
    
    private WritableRemotePeer writableRemotePeer;

    private RepositoryLogic repositoryLogic;

    private boolean browsingAllowed = true;

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

    public void setWritableStorage(WritableStorage writableStorage) {
        this.writableStorage = writableStorage;
        this.storage = writableStorage;
    }

    public WritableStorage getWritableStorage() {
        return writableStorage;
    }

    public void setRemotePeer(RemotePeer remotePeer) {
        this.remotePeer = remotePeer;
    }

    public RemotePeer getRemotePeer() {
        return remotePeer;
    }

    public void setWritableRemotePeer(WritableRemotePeer writableRemotePeer) {
        this.writableRemotePeer = writableRemotePeer;
        this.remotePeer = writableRemotePeer;
    }

    public WritableRemotePeer getWritableRemotePeer() {
        return writableRemotePeer;
    }

    public void setRepositoryLogic(RepositoryLogic repositoryLogic) {
        this.repositoryLogic = repositoryLogic;
    }

    public RepositoryLogic getRepositoryLogic() {
        return repositoryLogic;
    }

    public void setBrowsingAllowed(boolean browsingAllowed) {
        this.browsingAllowed = browsingAllowed;
    }

    public boolean isBrowsingAllowed() {
        return browsingAllowed;
    }

    public ProximityResponse handleRequest(ProximityRequest request) throws ProximityException {
        if (request.getPath().endsWith("/")) {
            if (isBrowsingAllowed()) {
                List items = null;
                if (getStorage() != null) {
                    items = getStorage().listItems(request.getPath());
                }
                SimpleProximityResponseList response = new SimpleProximityResponseList();
                response.setItems(items);
                return response;

            } else {
                throw new BrowsingNotAllowedException(getName());
            }
        } else {
            Item item = null;
            if (getStorage() != null) {
                if (getStorage().containsItem(request.getPath())) {
                    logger.info("Found " + request.getPath() + " item in storage of repository " + getName());
                    item = getStorage().retrieveItem(request.getPath());
                }
            }
            if (item == null && getRemotePeer() != null) {
                if (getRemotePeer().containsItem(request.getPath())) {
                    logger.info("Found " + request.getPath() + " item in remote peer of repository " + getName());
                    item = getRemotePeer().retrieveItem(request.getPath());
                    if (getWritableStorage() != null) {
                        getWritableStorage().storeItem(item);
                        handleRequest(request);
                    }
                }

            }
            if (item != null) {
                SimpleProximityResponse response = new SimpleProximityResponse();
                response.setItem(item);
                return response;
            } else {
                throw new ItemNotFoundException(request.getPath());
            }
        }
    }

}
