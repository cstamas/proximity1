package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.Repository;
import hu.ismicro.commons.proximity.base.logic.DefaultProxyingLogic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RepositoryImpl implements Repository {

	protected Log logger = LogFactory.getLog(this.getClass());

	private String id;

	private Storage localStorage;

	private Storage remoteStorage;

	private Indexer indexer;

	private RepositoryLogic repositoryLogic = new DefaultProxyingLogic();

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

	public Indexer getIndexer() {
		return indexer;
	}

	public void setIndexer(Indexer indexer) {
		this.indexer = indexer;
	}

	public void setRepositoryLogic(RepositoryLogic repositoryLogic) {
		if (repositoryLogic == null) {
			throw new IllegalArgumentException("The logic may be not null");
		}
		this.repositoryLogic = repositoryLogic;
	}

	public ProxiedItemProperties retrieveItemProperties(String path) throws ItemNotFoundException, StorageException {
		return (ProxiedItemProperties) retrieveItem(true, path).getProperties();
	}

	public ProxiedItem retrieveItem(String path) throws ItemNotFoundException, StorageException {
		return retrieveItem(false, path);
	}

	public void deleteItemProperties(String path) throws StorageException {
		if (getIndexer() != null) {
			try {
				ItemProperties itemProps = retrieveItemProperties(path);
				getIndexer().deleteItemProperties(itemProps);
			} catch (ItemNotFoundException ex) {
				logger.info("Path [" + path + "] not found but deletion requested.", ex);
			}
		}
		if (getLocalStorage() != null) {
			getLocalStorage().deleteItemProperties(path);
		} else {
			throw new UnsupportedOperationException("The repository " + getId() + " have no local storage!");
		}
	}

	public void deleteItem(String path) throws StorageException {
		if (getIndexer() != null) {
			try {
				ItemProperties itemProps = retrieveItemProperties(path);
				getIndexer().deleteItemProperties(itemProps);
			} catch (ItemNotFoundException ex) {
				logger.info("Path [" + path + "] not found but deletion requested.", ex);
			}
		}
		if (getLocalStorage() != null) {
			getLocalStorage().deleteItem(path);
		} else {
			throw new UnsupportedOperationException("The repository " + getId() + " have no local storage!");
		}
	}

	public void storeItemProperties(ItemProperties itemProps) throws StorageException {
		if (getLocalStorage() != null && getLocalStorage().isWritable()) {
			getLocalStorage().storeItemProperties(itemProps);
		} else {
			throw new UnsupportedOperationException("The repository " + getId() + " have no local storage!");
		}
		if (getIndexer() != null && getRepositoryLogic().shouldIndex(itemProps)) {
			getIndexer().addItemProperties(itemProps);
		}
	}

	public void storeItem(Item item) throws StorageException {
		if (getLocalStorage() != null && getLocalStorage().isWritable()) {
			getLocalStorage().storeItem(item);
		} else {
			throw new UnsupportedOperationException("The repository " + getId() + " have no local storage!");
		}
		if (getIndexer() != null && getRepositoryLogic().shouldIndex(item.getProperties())) {
			getIndexer().addItemProperties(item.getProperties());
		}
	}

	public List listItems(String path) throws StorageException {
		List result = new ArrayList();
		if (getLocalStorage() != null) {
			result.addAll(getLocalStorage().listItems(path));
		}
		return result;
	}

	protected ProxiedItem retrieveItem(boolean propsOnly, String path) throws ItemNotFoundException, StorageException {
		ProxiedItem result = null;
		try {
			if (getLocalStorage() != null) {
				if (getRepositoryLogic().shouldCheckForLocalCopy(path)) {
					if ((propsOnly && getLocalStorage().containsItemProperties(path))
							|| (getLocalStorage().containsItem(path))) {
						logger.info("Found " + path + " item in storage of repository " + getId());
						if (propsOnly) {
							result = new ProxiedItem();
							result.setProperties(getLocalStorage().retrieveItemProperties(path));
						} else {
							result = getLocalStorage().retrieveItem(path);
						}
						result = getRepositoryLogic().afterLocalCopyFound(result, this);
					} else {
						logger.info("Not found " + path + " item in storage of repository " + getId());
					}
				}
			}
			if (getRepositoryLogic().shouldCheckForRemoteCopy(path, result != null) && getRemoteStorage() != null) {
				if ((propsOnly && getRemoteStorage().containsItemProperties(path))
						|| (getRemoteStorage().containsItem(path))) {
					logger.info("Found " + path + " item in remote storage of repository " + getId());
					if (propsOnly) {
						result = new ProxiedItem();
						result.setProperties(getRemoteStorage().retrieveItemProperties(path));
					} else {
						result = getRemoteStorage().retrieveItem(path);
					}
					result.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, getId());
					result = getRepositoryLogic().afterRemoteCopyFound(result, this);
					if (result != null && !result.getProperties().isDirectory() && getLocalStorage().isWritable()) {
						if (getRepositoryLogic().shouldStoreLocallyAfterRemoteRetrieval(result.getProperties())) {
							logger.info("Storing " + path + " item in writable storage of repository " + getId());
							if (propsOnly) {
								storeItemProperties(result.getProperties());
								result.setProperties(getLocalStorage().retrieveItemProperties(path));
							} else {
								storeItem(result);
								result = getLocalStorage().retrieveItem(path);
							}
						}
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

}
