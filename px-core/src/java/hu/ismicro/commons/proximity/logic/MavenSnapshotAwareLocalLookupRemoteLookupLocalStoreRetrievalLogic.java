package hu.ismicro.commons.proximity.logic;

import hu.ismicro.commons.proximity.base.InnerRepository;
import hu.ismicro.commons.proximity.base.ProxiedItem;

public class MavenSnapshotAwareLocalLookupRemoteLookupLocalStoreRetrievalLogic extends AbstractRepositoryRetrievalLogic {

    public ProxiedItem orchestrateItemRequest(String path, InnerRepository repository) {
        logger.info("Invoked LocalLookupRemoteLookupLocalStoreRetrievalLogic retrieval logic.");
        ProxiedItem item = null;
        if (repository.getStorage() != null) {
            if (repository.getStorage().containsItem(path)) {
                logger.info("Found " + path + " item in storage of repository " + repository.getName());
                item = repository.getStorage().retrieveItem(path);
            } else {
                logger.info("Not found " + path + " item in storage of repository " + repository.getName());
            }
        }
        if ((item == null || (item != null && item .getPath().contains("SNAPSHOT"))) && repository.getRemotePeer() != null) {
            if (repository.getRemotePeer().containsItem(path)) {
                logger.info("Found " + path + " item in remote peer of repository " + repository.getName());
                item = repository.getRemotePeer().retrieveItem(path);
                if (!item.isDirectory() && repository.getWritableStorage() != null) {
                    repository.getWritableStorage().storeItem(item);
                    ProxiedItem localItem = repository.getWritableStorage().retrieveItem(item.getPath());
                    localItem.setOriginatingUrl(item.getOriginatingUrl());
                    item = localItem;
                }
            } else {
                logger.info("Not found " + path + " item in remote peer of repository " + repository.getName());
            }

        }
        return item;
    }

}
