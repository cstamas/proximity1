package org.abstracthorizon.proximity.impl;

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.RepositoryNotAvailableException;
import org.abstracthorizon.proximity.logic.DefaultProxyingRepositoryLogic;
import org.abstracthorizon.proximity.logic.RepositoryLogic;
import org.abstracthorizon.proximity.storage.StorageException;

public class LogicDrivenRepositoryImpl extends AbstractRepository {

	private RepositoryLogic repositoryLogic = new DefaultProxyingRepositoryLogic();

	public RepositoryLogic getRepositoryLogic() {
		return repositoryLogic;
	}

	public void setRepositoryLogic(RepositoryLogic repositoryLogic) {
		if (repositoryLogic == null) {
			throw new IllegalArgumentException("The logic may be not null");
		}
		this.repositoryLogic = repositoryLogic;
	}

	protected Item doRetrieveItem(ProximityRequest request) throws RepositoryNotAvailableException,
			ItemNotFoundException, StorageException {
		Item localResult = null;
		Item remoteResult = null;

		if (getStatisticsGatherer() != null) {
			getStatisticsGatherer().incomingRequest(request);
		}

		try {
			if (getLocalStorage() != null) {
				if (getRepositoryLogic().shouldCheckForLocalCopy(this, request)) {
					if (getLocalStorage().containsItem(request.getPath())) {
						logger.debug("Item [{}] is contained in local storage of repository {}", request.getPath(),
								getId());
						localResult = getLocalStorage().retrieveItem(request.getPath(), request.isPropertiesOnly());
						localResult.getProperties().setRepositoryId(getId());
						localResult.getProperties().setRepositoryGroupId(getGroupId());
						if (getStatisticsGatherer() != null) {
							getStatisticsGatherer().localHit(request, this, localResult.getProperties());
						}
						logger.debug("Item [{}] fetched from local storage of repository {}", request.getPath(),
								getId());
						localResult = getRepositoryLogic().afterLocalCopyFound(this, request, localResult);
					} else {
						logger.debug("Not found [{}] item in storage of repository {}", request.getPath(), getId());
					}
				}
			}
			if (!isOffline() && !request.isLocalOnly()
					&& getRepositoryLogic().shouldCheckForRemoteCopy(this, request, localResult)
					&& getRemoteStorage() != null) {
				if (getRemoteStorage().containsItem(request.getPath())) {
					logger.debug("Found [{}] item in remote storage of repository {}", request.getPath(), getId());
					remoteResult = getRemoteStorage().retrieveItem(request.getPath(), request.isPropertiesOnly());
					remoteResult.getProperties().setRepositoryId(getId());
					remoteResult.getProperties().setRepositoryGroupId(getGroupId());
					if (getStatisticsGatherer() != null) {
						getStatisticsGatherer().remoteHit(request, this, remoteResult.getProperties());
					}
					logger.debug("Item [{}] fetched from remote storage of repository {}", request.getPath(), getId());
					remoteResult = getRepositoryLogic().afterRemoteCopyFound(this, request, localResult, remoteResult);
					if (remoteResult != null && !remoteResult.getProperties().isDirectory()
							&& getLocalStorage() != null && getLocalStorage().isWritable()) {
						if (getRepositoryLogic().shouldStoreLocallyAfterRemoteRetrieval(this, request, localResult,
								remoteResult)) {
							logger.debug("Storing [{}] item in writable storage of repository {}", request.getPath(),
									getId());
							storeItem(request, remoteResult);
							remoteResult = getLocalStorage()
									.retrieveItem(request.getPath(), request.isPropertiesOnly());
							remoteResult.getProperties().setRepositoryId(getId());
							remoteResult.getProperties().setRepositoryGroupId(getGroupId());
						}
					}
				} else {
					logger.debug("Not found [{}] item in remote peer of repository {}", request.getPath(), getId());
				}

			}
			Item result = getRepositoryLogic().afterRetrieval(this, request, localResult, remoteResult);
			if (result == null) {
				logger.debug("Item [{}] not found in repository {}", request.getPath(), getId());
				throw new ItemNotFoundException(request.getPath());
			}
			if (getRemoteStorage() != null) {
				result.getProperties()
						.setRemoteUrl(getRemoteStorage().getAbsoluteUrl(result.getProperties().getPath()));
			}
			logger.debug("Item [{}] found in repository {}", request.getPath(), getId());
			return result;
		} catch (ItemNotFoundException ex) {
			throw new ItemNotFoundException(request.getPath(), getId());
		}
	}

}
