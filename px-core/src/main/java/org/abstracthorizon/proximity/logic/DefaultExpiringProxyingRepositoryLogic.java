package org.abstracthorizon.proximity.logic;


import java.util.Date;

import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.RepositoryNotAvailableException;
import org.abstracthorizon.proximity.impl.ItemImpl;

/**
 * Simple logic with expiration support. If expirationPeriod is not -1, it will
 * apply expiration period onto items, and will handle removal of them when they
 * expires.
 * 
 * @author cstamas
 * 
 */
public class DefaultExpiringProxyingRepositoryLogic extends DefaultProxyingRepositoryLogic {

    public static final long NO_EXPIRATION = -1000;

    public static final String METADATA_EXPIRES = "item.expires";

    private long itemExpirationPeriod = 86400 * 1000; // 24 hours

    public long getItemExpirationPeriodInSeconds() {
        return itemExpirationPeriod / 1000;
    }

    public void setItemExpirationPeriodInSeconds(long itemExpirationPeriod) {
        this.itemExpirationPeriod = itemExpirationPeriod * 1000;
    }

    /**
     * If item has defined EXPIRES metadata, will use it and remove item from
     * repository if needed.
     */
    public ItemImpl afterLocalCopyFound(ProximityRequest request, ItemImpl item, Repository repository) {
        if (item.getProperties().getMetadata(DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES) != null) {
            logger.debug("Item has expiration, checking it.");
            Date expires = new Date(Long.parseLong(item.getProperties().getMetadata(
                    DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES)));
            if (expires.before(new Date(System.currentTimeMillis()))) {
                logger.info("Item has expired on " + expires + ", DELETING it.");
                try {
                    repository.deleteItem(request);
                } catch (RepositoryNotAvailableException ex) {
                    logger.info("Repository unavailable, cannot delete expired item.", ex);
                }
                return null;
            }
        }
        return item;
    }

    /**
     * If expiration period is not NO_EXPIRATION, it will apply it on all items.
     */
    public ItemImpl afterRemoteCopyFound(ProximityRequest request, ItemImpl localItem, ItemImpl remoteItem, Repository repository) {
        if (itemExpirationPeriod != NO_EXPIRATION) {
            Date expires = new Date(System.currentTimeMillis() + itemExpirationPeriod);
            logger.info("Setting expires on item  to " + expires.toString());
            remoteItem.getProperties().setMetadata(DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES,
                    Long.toString(expires.getTime()));
        }
        return remoteItem;
    }

}
