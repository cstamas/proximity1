package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.Repository;

public interface RepositoryLogic {
	
	boolean shouldCheckForLocalCopy(String path);
	
	ProxiedItem afterLocalCopyFound(ProxiedItem item, Repository repository);
	
	boolean shouldCheckForRemoteCopy(String path, boolean locallyExists);
	
	ProxiedItem afterRemoteCopyFound(ProxiedItem item, Repository repository);
	
	boolean shouldStoreLocallyAfterRemoteRetrieval(ItemProperties item);

	boolean shouldIndex(ItemProperties item);
	
}
