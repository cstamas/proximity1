package org.abstracthorizon.proximity.logic;

import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.impl.ItemImpl;

/**
 * Repository logic that drives repository's behaviour.
 * 
 * <p>
 * Asked on every request got by Repository, it have a few simple questions to
 * answer and postprocess items after local and remote retrieval.
 * 
 * @author cstamas
 * 
 */
public interface RepositoryLogic {

    /**
     * Return true if repository should check for local cached version of the
     * path.
     * 
     * @param path
     * @return
     */
    boolean shouldCheckForLocalCopy(ProximityRequest request);

    /**
     * Postprocess item if needed after local copy found.
     * 
     * @param item
     * @param repository
     * @return
     */
    ItemImpl afterLocalCopyFound(ProximityRequest request, ItemImpl item, Repository repository);

    /**
     * Return true if repository should initiate remote lookup.
     * 
     * @param path
     * @param localItemnot
     *            null if there is a local copy found, null otherwise.
     * @return true is there is need to check remote copy
     */
    boolean shouldCheckForRemoteCopy(ProximityRequest request, ItemImpl localItem);

    /**
     * Postprocess item if needed after remote retrieval.
     * 
     * @param localItem -
     *            the artifact found locally
     * @param repository -
     *            the artifact found remotely
     * @return
     */
    ItemImpl afterRemoteCopyFound(ProximityRequest request, ItemImpl localItem, ItemImpl remoteItem, Repository repository);

    /**
     * Return true if reposotiry should store the remote retrieved item in a
     * local writable store.
     * 
     * @param localItem
     * @param remoteItem
     * @return
     */
    boolean shouldStoreLocallyAfterRemoteRetrieval(ProximityRequest request, ItemImpl localItem, ItemImpl remoteItem);

    /**
     * Choose tha artifact to serve.
     */
    public ItemImpl afterRetrieval(ProximityRequest request, ItemImpl localItem, ItemImpl remoteItem);

    /**
     * Return true to index the given item.
     * 
     * @param item
     * @return
     */
    boolean shouldIndex(ItemProperties item);

}
