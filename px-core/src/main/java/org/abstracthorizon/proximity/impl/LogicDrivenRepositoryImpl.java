package org.abstracthorizon.proximity.impl;

import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ItemProperties;
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

    protected ItemImpl doRetrieveItem(ProximityRequest request)
            throws RepositoryNotAvailableException, ItemNotFoundException, StorageException {
        ItemImpl localResult = null;
        ItemImpl remoteResult = null;

        if (getStatisticsGatherer() != null) {
            getStatisticsGatherer().incomingRequest(request);
        }

        try {
            if (getLocalStorage() != null) {
                if (getRepositoryLogic().shouldCheckForLocalCopy(request)) {
                    if (getLocalStorage().containsItem(request.getPath())) {
                        logger.debug("Found [{}] item in local storage of repository {}", request.getPath(), getId());
                        localResult = getLocalStorage().retrieveItem(request.getPath(), request.isPropertiesOnly());
                        localResult.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, getId());
                        localResult.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY_GROUP,
                                getGroupId());
                        if (getStatisticsGatherer() != null) {
                            getStatisticsGatherer().localHit(request, this, localResult.getProperties());
                        }
                        localResult = getRepositoryLogic().afterLocalCopyFound(request, localResult, this);
                    } else {
                        logger.debug("Not found [{}] item in storage of repository {}", request.getPath(), getId());
                    }
                }
            }
            if (!isOffline() && !request.isLocalOnly()
                    && getRepositoryLogic().shouldCheckForRemoteCopy(request, localResult)
                    && getRemoteStorage() != null) {
                if (getRemoteStorage().containsItem(request.getPath())) {
                    logger.debug("Found [{}] item in remote storage of repository {}", request.getPath(), getId());
                    remoteResult = getRemoteStorage().retrieveItem(request.getPath(), request.isPropertiesOnly());
                    remoteResult.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, getId());
                    remoteResult.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY_GROUP,
                            getGroupId());
                    if (getStatisticsGatherer() != null) {
                        getStatisticsGatherer().remoteHit(request, this, remoteResult.getProperties());
                    }
                    remoteResult = getRepositoryLogic().afterRemoteCopyFound(request, localResult, remoteResult, this);
                    if (remoteResult != null && !remoteResult.getProperties().isDirectory()
                            && getLocalStorage().isWritable()) {
                        if (getRepositoryLogic().shouldStoreLocallyAfterRemoteRetrieval(request, localResult,
                                remoteResult)) {
                            logger.debug("Storing [{}] item in writable storage of repository {}", request.getPath(),
                                    getId());
                            storeItem(request, remoteResult);
                            remoteResult = getLocalStorage().retrieveItem(request.getPath(), request.isPropertiesOnly());
                            remoteResult.getProperties()
                                    .setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, getId());
                            remoteResult.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY_GROUP,
                                    getGroupId());
                        }
                    }
                } else {
                    logger.debug("Not found [{}] item in remote peer of repository {}", request.getPath(), getId());
                }

            }
            ItemImpl result = getRepositoryLogic().afterRetrieval(request, localResult, remoteResult);
            if (result == null) {
                throw new ItemNotFoundException(request.getPath());
            }
            if (getRemoteStorage() != null) {
                result.getProperties().setMetadata(ItemProperties.METADATA_ORIGINATING_URL,
                        getRemoteStorage().getAbsoluteUrl(result.getProperties().getPath()));
            }
            logger.debug("Item [{}] found in repository {}", request.getPath(), getId());
            return result;
        } catch (ItemNotFoundException ex) {
            throw new ItemNotFoundException(request.getPath(), getId());
        }
    }

}
