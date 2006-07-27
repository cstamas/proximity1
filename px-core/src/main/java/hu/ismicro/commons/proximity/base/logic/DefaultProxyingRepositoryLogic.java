package hu.ismicro.commons.proximity.base.logic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.Repository;
import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.RepositoryLogic;

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

    protected Log logger = LogFactory.getLog(this.getClass());

    /**
     * Always returns true.
     */
    public boolean shouldCheckForLocalCopy(ProximityRequest request) {
        return true;
    }

    /**
     * Does nothing and returns item unmodified.
     */
    public ProxiedItem afterLocalCopyFound(ProxiedItem item, Repository repository) {
        return item;
    }

    /**
     * Always returns !locallyExists.
     */
    public boolean shouldCheckForRemoteCopy(ProximityRequest request, ProxiedItem localItem) {
        return localItem == null;
    }

    /**
     * Does nothing and returns item unmodified.
     */
    public ProxiedItem afterRemoteCopyFound(ProxiedItem localItem, ProxiedItem remoteItem, Repository repository) {
        return remoteItem;
    }

    /**
     * Always returns true.
     */
    public boolean shouldStoreLocallyAfterRemoteRetrieval(ProxiedItem localItem, ProxiedItem remoteItem) {
        return true;
    }

    /**
     * Always give the best what we have.
     */
    public ProxiedItem afterRetrieval(ProxiedItem localItem, ProxiedItem remoteItem) {
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
