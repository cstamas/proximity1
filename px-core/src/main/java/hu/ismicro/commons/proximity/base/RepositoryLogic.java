package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.Repository;

/**
 * Repository logic that drives repository's behaviour.
 * 
 * <p>
 * Asked on every request got by Repository, it have a few simple questions
 * to answer and postprocess items after local and remote retrieval.
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
	ProxiedItem afterLocalCopyFound(ProxiedItem item, Repository repository);
	
	/**
	 * Return true if repository should initiate remote lookup.
	 * 
	 * @param path
	 * @param locallyExists true if there is a local copy found.
	 * @return
	 */
	boolean shouldCheckForRemoteCopy(ProximityRequest request, boolean locallyExists);
	
	/**
	 * Postprocess item if needed after remote retrieval.
	 * 
	 * @param item
	 * @param repository
	 * @return
	 */
	ProxiedItem afterRemoteCopyFound(ProxiedItem item, Repository repository);
	
	/**
	 * Return true if reposotiry should store the remote retrieved item in a 
	 * local writable store.
	 * 
	 * @param item
	 * @return
	 */
	boolean shouldStoreLocallyAfterRemoteRetrieval(ItemProperties item);

	/**
	 * Return true to index the given item.
	 * 
	 * @param item
	 * @return
	 */
	boolean shouldIndex(ItemProperties item);
	
}
