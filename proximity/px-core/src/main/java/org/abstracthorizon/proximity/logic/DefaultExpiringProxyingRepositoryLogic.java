package org.abstracthorizon.proximity.logic;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.RepositoryNotAvailableException;

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

    private long notFoundCachePeriod = 86400 * 1000; // 24 hours

    private Map notFoundCache = Collections.synchronizedMap(new HashMap());

    public long getNotFoundCachePeriodInSeconds() {
        return notFoundCachePeriod / 1000;
    }

    public void setNotFoundCachePeriodInSeconds(long notFoundCachePeriod) {
        this.notFoundCachePeriod = notFoundCachePeriod * 1000;
    }

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
    public Item afterLocalCopyFound(ProximityRequest request, Item item, Repository repository) {
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

    public boolean shouldCheckForRemoteCopy(ProximityRequest request, Item localItem) {
        return shouldCheckByNotFoundCache(request) && localItem == null;
    }

    /**
     * If expiration period is not NO_EXPIRATION, it will apply it on all items.
     */
    public Item afterRemoteCopyFound(ProximityRequest request, Item localItem, Item remoteItem,
            Repository repository) {
        if (itemExpirationPeriod != NO_EXPIRATION) {
            Date expires = new Date(System.currentTimeMillis() + itemExpirationPeriod);
            logger.info("Setting expires on item  to " + expires.toString());
            remoteItem.getProperties().setMetadata(DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES,
                    Long.toString(expires.getTime()));
        }
        return remoteItem;
    }

    public Item afterRetrieval(ProximityRequest request, Item localItem, Item remoteItem) {
        Item item = super.afterRetrieval(request, localItem, remoteItem);
        if (item == null) {
            // we have not found it
            // put the path into not found cache
        	if (!notFoundCache.containsKey(request.getPath())) {
                logger.info("Storing failed request [{}] to neg-cache.", request.getPath());
                notFoundCache.put(request.getPath(), new Date());
        	}
        }
        return item;
    }

    protected boolean shouldCheckByNotFoundCache(ProximityRequest request) {
        if (notFoundCache.containsKey(request.getPath())) {
            // it is in cache, check when it got in
            Date lastRequest = (Date) notFoundCache.get(request.getPath());
            if (lastRequest.before(new Date(System.currentTimeMillis() - notFoundCachePeriod))) {
                // the notFoundCache record expired, remove it and check its
                // existence
                logger.debug("n-cache record expired, will go again remote to fetch.");
                notFoundCache.remove(request.getPath());
                return true;
            } else {
                // the notFoundCache record is still valid, do not check its
                // existence
                logger.debug("n-cache record still active, will not go remote to fetch.");
                return false;
            }
        } else {
            // it is not in notFoundCache, check its existence
            return true;
        }
    }

}
