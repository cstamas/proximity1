package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.Repository;
import hu.ismicro.commons.proximity.RepositoryLogic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RepositoryImpl implements Repository {

	protected Log logger = LogFactory.getLog(this.getClass());

	private String id;

	private Storage localStorage;

	private Storage remoteStorage;

	private RepositoryLogic repositoryLogic;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Storage getLocalStorage() {
		return localStorage;
	}

	public void setLocalStorage(Storage localStorage) {
		this.localStorage = localStorage;
	}

	public Storage getRemoteStorage() {
		return remoteStorage;
	}

	public void setRemoteStorage(Storage remoteStorage) {
		this.remoteStorage = remoteStorage;
	}

	public RepositoryLogic getRepositoryLogic() {
		return repositoryLogic;
	}

	public void setRepositoryLogic(RepositoryLogic repositoryLogic) {
		this.repositoryLogic = repositoryLogic;
	}

	public ProxiedItemProperties retrieveItemProperties(String path) throws ItemNotFoundException, StorageException {
		ProxiedItemProperties result = null;
		try {
			if (getLocalStorage() != null) {
				if (getLocalStorage().containsItem(path)) {
					logger.info("Found " + path + " item in storage of repository " + getId());
					result = getLocalStorage().retrieveItemProperties(path);
				} else {
					logger.info("Not found " + path + " item in storage of repository " + getId());
				}
			}
			if (result == null && getRemoteStorage() != null) {
				if (getRemoteStorage().containsItem(path)) {
					logger.info("Found " + path + " item in remote storage of repository " + getId());
					result = getRemoteStorage().retrieveItemProperties(path);
				} else {
					logger.info("Not found " + path + " item in remote peer of repository " + getId());
				}

			}
			if (result == null) {
				throw new ItemNotFoundException(path);
			}
			return result;
		} catch (ItemNotFoundException ex) {
			throw new ItemNotFoundException(path, getId());
		}
	}

	public ProxiedItem retrieveItem(String path) throws ItemNotFoundException, StorageException {
		ProxiedItem result = null;
		try {
			if (getLocalStorage() != null) {
				if (getLocalStorage().containsItem(path)) {
					logger.info("Found " + path + " item in storage of repository " + getId());
					result = getLocalStorage().retrieveItem(path);
				} else {
					logger.info("Not found " + path + " item in storage of repository " + getId());
				}
			}
			if (result == null && getRemoteStorage() != null) {
				if (getRemoteStorage().containsItem(path)) {
					logger.info("Found " + path + " item in remote storage of repository " + getId());
					result = getRemoteStorage().retrieveItem(path);
					result.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, getId());
					if (!result.getProperties().isDirectory() && getLocalStorage().isWritable()) {
						logger.info("Storing " + path + " item in writable storage of repository " + getId());
						getLocalStorage().storeItem(result);
						ProxiedItem localItem = getLocalStorage().retrieveItem(
								PathHelper.walkThePath(result.getProperties().getAbsolutePath(), result.getProperties()
										.getName()));
						result = localItem;
					}
				} else {
					logger.info("Not found " + path + " item in remote peer of repository " + getId());
				}

			}
			if (result == null) {
				throw new ItemNotFoundException(path);
			}
			return result;
		} catch (ItemNotFoundException ex) {
			throw new ItemNotFoundException(path, getId());
		}
	}

	public List listItems(String path) throws StorageException {
		if (getLocalStorage() != null) {
			List result = getLocalStorage().listItems(path);
			return result;
		}
		return new ArrayList();
	}

}
