package hu.ismicro.commons.proximity;

public interface RepositoryLogic {
	
	boolean shouldCheckForLocalCopy(String path);
	
	boolean shouldCheckForRemoteCopy(String path, boolean locallyExists);
	
	boolean shouldStoreLocallyAfterRemoteRetrieval(Item item);
	
	void fillInMetadata(ItemProperties props);

}
