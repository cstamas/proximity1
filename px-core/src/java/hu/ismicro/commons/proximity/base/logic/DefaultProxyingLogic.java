package hu.ismicro.commons.proximity.base.logic;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.RepositoryLogic;

public class DefaultProxyingLogic implements RepositoryLogic {

	public boolean shouldCheckForLocalCopy(String path) {
		return true;
	}

	public boolean shouldCheckForRemoteCopy(String path, boolean locallyExists) {
		return !locallyExists;
	}

	public boolean shouldStoreLocallyAfterRemoteRetrieval(ItemProperties item) {
		return true;
	}

	public boolean shouldIndex(ItemProperties item) {
		return false;
	}

	public void fillInMetadata(ItemProperties props) {
	}

}
