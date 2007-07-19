package org.abstracthorizon.proximity.logic;

import java.io.IOException;

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;
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
    public boolean shouldCheckForLocalCopy(Repository repository, ProximityRequest request) {
	return true;
    }

    /**
         * Does nothing and returns item unmodified.
         */
    public Item afterLocalCopyFound(Repository repository, ProximityRequest request, Item item) {
	return item;
    }

    /**
         * Always returns !locallyExists.
         */
    public boolean shouldCheckForRemoteCopy(Repository repository, ProximityRequest request, Item localItem) {
	return localItem == null;
    }

    /**
         * Does nothing and returns item unmodified.
         */
    public Item afterRemoteCopyFound(Repository repository, ProximityRequest request, Item localItem, Item remoteItem) {
	return remoteItem;
    }

    /**
         * Always returns true.
         */
    public boolean shouldStoreLocallyAfterRemoteRetrieval(Repository repository, ProximityRequest request, Item localItem, Item remoteItem) {
	return true;
    }

    /**
         * Always give the best what we have.
         */
    public Item afterRetrieval(Repository repository, ProximityRequest request, Item localItem, Item remoteItem) {
	if (remoteItem != null) {
	    if (localItem != null) {
		try {
		    localItem.getStream().close();
		} catch (IOException ex) {
		    logger.warn("Had a problem trying to close a file: {}", localItem.getProperties(), ex);
		}
	    }
	    return remoteItem;
	}
	if (localItem != null) {
	    return localItem;
	}
	return null;
    }

}
