package org.abstracthorizon.proximity.logic;


import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.impl.ItemImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of RepositoryLogic. It always checks for local copy,
 * does nothing after local copy is found, forces remote retrieval if local copy
 * is not found, does nothing after remote copy is found and always stores
 * freshly retrieved item. Index item that are not directorties.
 * 
 * @author cstamas
 * 
 */
public class DefaultProxyingRepositoryLogic implements RepositoryLogic {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Always returns true.
     */
    public boolean shouldCheckForLocalCopy(ProximityRequest request) {
        return true;
    }

    /**
     * Does nothing and returns item unmodified.
     */
    public ItemImpl afterLocalCopyFound(ItemImpl item, Repository repository) {
        return item;
    }

    /**
     * Always returns !locallyExists.
     */
    public boolean shouldCheckForRemoteCopy(ProximityRequest request, ItemImpl localItem) {
        return localItem == null;
    }

    /**
     * Does nothing and returns item unmodified.
     */
    public ItemImpl afterRemoteCopyFound(ItemImpl localItem, ItemImpl remoteItem, Repository repository) {
        return remoteItem;
    }

    /**
     * Always returns true.
     */
    public boolean shouldStoreLocallyAfterRemoteRetrieval(ItemImpl localItem, ItemImpl remoteItem) {
        return true;
    }

    /**
     * Always give the best what we have.
     */
    public ItemImpl afterRetrieval(ItemImpl localItem, ItemImpl remoteItem) {
        if (remoteItem != null) {
            if (localItem != null) {
                localItem.close();
            }
            return remoteItem;
        }
        if (localItem != null) {
            return localItem;
        }
        return null;
    }

    /**
     * Always returns !item.isDirectory().
     */
    public boolean shouldIndex(ItemProperties item) {
        return !item.isDirectory();
    }

}
