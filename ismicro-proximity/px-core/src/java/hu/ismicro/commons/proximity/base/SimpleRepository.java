package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.BrowsingNotAllowedException;
import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.Repository;

import java.util.ArrayList;
import java.util.Iterator;
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

    public ProxiedItem retrieveItem(String path) {
        ProxiedItem item = null;
        if (getStorage() != null) {
            if (getStorage().containsItem(path)) {
                logger.info("Found " + path + " item in storage of repository " + getName());
                item = getStorage().retrieveItem(path);
            } else {
                logger.info("Not found " + path + " item in storage of repository " + getName());
            }
        }
        if (item == null && getRemotePeer() != null) {
            if (getRemotePeer().containsItem(path)) {
                logger.info("Found " + path + " item in remote peer of repository " + getName());
                item = getRemotePeer().retrieveItem(path);
                if (!item.isDirectory() && getWritableStorage() != null) {
                    getWritableStorage().storeItem(item);
                    ProxiedItem localItem = getWritableStorage().retrieveItem(item.getPath());
                    localItem.setOriginatingUrl(item.getOriginatingUrl());
                    item = localItem;
                }
            } else {
                logger.info("Not found " + path + " item in remote peer of repository " + getName());
            }

        }
        if (item != null) {
            item.setRepositoryName(getName());
            return item;
        } else {
            throw new ItemNotFoundException(path);
        }
    }

    public List listItems(String path) {
        if (isBrowsingAllowed()) {
            List items = new ArrayList();
            if (getStorage() != null) {
                items.addAll(getStorage().listItems(path));
            }
            for (Iterator i = items.iterator(); i.hasNext(); ) {
                ProxiedItem item = (ProxiedItem) i.next();
                item.setRepositoryName(getName());
            }
            return items;
        } else {
            throw new BrowsingNotAllowedException(getName());
        }
    }

}
