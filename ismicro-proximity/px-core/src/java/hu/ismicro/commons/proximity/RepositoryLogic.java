package hu.ismicro.commons.proximity;

public interface RepositoryLogic {
	
	boolean shouldCheckForLocalCopy(String path);
	
	boolean shouldCheckForRemoteCopy(String path, boolean locallyExists);
	
	boolean shouldStoreLocallyAfterRemoteRetrieval(ItemProperties item);

	boolean shouldIndex(ItemProperties item);
	
	void fillInMetadata(ItemProperties props);

}
