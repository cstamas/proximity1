package hu.ismicro.commons.proximity.base.logic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.Repository;
import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.RepositoryLogic;

/**
 * Default implementation of RepositoryLogic. It always checks for local
 * copy, does nothing after local copy is found, forces remote retrieval
 * if local copy is not found, does nothing after remote copy is found
 * and always stores freshly retrieved item. Index item that are not
 * directorties.
 * 
 * @author cstamas
 *
 */
public class DefaultProxyingLogic implements RepositoryLogic {

	protected Log logger = LogFactory.getLog(this.getClass());

	public boolean shouldCheckForLocalCopy(String path) {
		return true;
	}

	public ProxiedItem afterLocalCopyFound(ProxiedItem item, Repository repository) {
		return item;
	}

	public boolean shouldCheckForRemoteCopy(String path, boolean locallyExists) {
		return !locallyExists;
	}

	public ProxiedItem afterRemoteCopyFound(ProxiedItem item, Repository repository) {
		return item;
	}

	public boolean shouldStoreLocallyAfterRemoteRetrieval(ItemProperties item) {
		return true;
	}

	public boolean shouldIndex(ItemProperties item) {
		return !item.isDirectory();
	}

}
