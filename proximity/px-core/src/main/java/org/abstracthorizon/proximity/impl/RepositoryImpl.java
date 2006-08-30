package org.abstracthorizon.proximity.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.RepositoryNotAvailableException;
import org.abstracthorizon.proximity.access.AccessManager;
import org.abstracthorizon.proximity.access.OpenAccessManager;
import org.abstracthorizon.proximity.indexer.Indexer;
import org.abstracthorizon.proximity.logic.DefaultProxyingRepositoryLogic;
import org.abstracthorizon.proximity.logic.RepositoryLogic;
import org.abstracthorizon.proximity.stats.StatisticsGatherer;
import org.abstracthorizon.proximity.storage.StorageException;
import org.abstracthorizon.proximity.storage.local.LocalStorage;
import org.abstracthorizon.proximity.storage.remote.RemoteStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryImpl implements Repository {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private String id;

    private String groupId = "default";

    private boolean available = true;

    private boolean offline = false;

    private boolean listable = true;

    private LocalStorage localStorage;

    private RemoteStorage remoteStorage;

    private Indexer indexer;

    private AccessManager accessManager = new OpenAccessManager();

    private StatisticsGatherer statisticsGatherer;

    private RepositoryLogic repositoryLogic = new DefaultProxyingRepositoryLogic();
    
    public void setProximity(Proximity proximity) {
        proximity.addRepository(this);
    }

    public StatisticsGatherer getStatisticsGatherer() {
        return statisticsGatherer;
    }

    public void setStatisticsGatherer(StatisticsGatherer statisticsGatherer) {
        this.statisticsGatherer = statisticsGatherer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isListable() {
        return listable;
    }

    public void setListable(boolean listable) {
        this.listable = listable;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public LocalStorage getLocalStorage() {
        return localStorage;
    }

    public void setLocalStorage(LocalStorage localStorage) {
        this.localStorage = localStorage;
    }

    public RemoteStorage getRemoteStorage() {
        return remoteStorage;
    }

    public void setRemoteStorage(RemoteStorage remoteStorage) {
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

    public AccessManager getAccessManager() {
        return accessManager;
    }

    public void setAccessManager(AccessManager accessManager) {
        this.accessManager = accessManager;
    }

    public void setRepositoryLogic(RepositoryLogic repositoryLogic) {
        if (repositoryLogic == null) {
            throw new IllegalArgumentException("The logic may be not null");
        }
        this.repositoryLogic = repositoryLogic;
    }

    // ---------------------------------------------------------------------------------
    // Entry methods

    public ItemImpl retrieveItem(ProximityRequest request) throws RepositoryNotAvailableException,
            ItemNotFoundException, StorageException, AccessDeniedException {
        if (!isAvailable()) {
            throw new RepositoryNotAvailableException("The repository " + getId() + " is NOT available!");
        }
        accessManager.decide(request, null);
        return retrieveItemController(request, false);
    }

    public ItemPropertiesImpl retrieveItemProperties(ProximityRequest request) throws RepositoryNotAvailableException,
            ItemNotFoundException, StorageException, AccessDeniedException {
        if (!isAvailable()) {
            throw new RepositoryNotAvailableException("The repository " + getId() + " is NOT available!");
        }
        accessManager.decide(request, null);
        return (ItemPropertiesImpl) retrieveItemController(request, true).getProperties();
    }

    public void deleteItem(ProximityRequest request) throws RepositoryNotAvailableException, StorageException {
        if (!isAvailable()) {
            throw new RepositoryNotAvailableException("The repository " + getId() + " is NOT available!");
        }
        if (getLocalStorage() != null) {
            if (getIndexer() != null) {
                try {
                    ItemProperties itemProps = getLocalStorage().retrieveItem(request.getPath(), true).getProperties();
                    itemProps.setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, getId());
                    itemProps.setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY_GROUP, getGroupId());
                    getIndexer().deleteItemProperties(itemProps);
                    getLocalStorage().deleteItem(request.getPath());
                } catch (ItemNotFoundException ex) {
                    logger.info("Path [{}] not found but deletion requested.", request.getPath(), ex);
                }
            }
        } else {
            throw new UnsupportedOperationException("The repository " + getId() + " have no local storage!");
        }
    }

    public void storeItem(ProximityRequest request, Item item) throws RepositoryNotAvailableException, StorageException {
        if (!isAvailable()) {
            throw new RepositoryNotAvailableException("The repository " + getId() + " is NOT available!");
        }
        if (getLocalStorage() != null && getLocalStorage().isWritable()) {
            item.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, getId());
            item.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY_GROUP, getGroupId());
            getLocalStorage().storeItem(item);
            if (getIndexer() != null && getRepositoryLogic().shouldIndex(item.getProperties())) {
                getIndexer().addItemProperties(item.getProperties());
            }
        } else {
            throw new UnsupportedOperationException("The repository " + getId() + " have no local storage!");
        }
    }

    public List listItems(ProximityRequest request) throws RepositoryNotAvailableException, StorageException {
        if (!isAvailable()) {
            throw new RepositoryNotAvailableException("The repository " + getId() + " is NOT available!");
        }
        List result = new ArrayList();
        if (isListable()) {
            if (getLocalStorage() != null) {
                List list = getLocalStorage().listItems(request.getPath());
                for (Iterator i = list.iterator(); i.hasNext();) {
                    ItemProperties ip = (ItemProperties) i.next();
                    ip.setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, this.getId());
                    ip.setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY_GROUP, this.getGroupId());
                    if (getRemoteStorage() != null) {
                        ip.setMetadata(ItemProperties.METADATA_ORIGINATING_URL, getRemoteStorage().getAbsoluteUrl(
                                ip.getPath()));
                    }
                }
                result.addAll(list);
            }
        }
        return result;
    }

    protected ItemImpl retrieveItemController(ProximityRequest request, boolean propsOnly) throws RepositoryNotAvailableException,
            ItemNotFoundException, StorageException {
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
                        localResult = getLocalStorage().retrieveItem(request.getPath(), propsOnly);
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
            if (!isOffline() && !request.isLocalOnly() && getRepositoryLogic().shouldCheckForRemoteCopy(request, localResult)
                    && getRemoteStorage() != null) {
                if (getRemoteStorage().containsItem(request.getPath())) {
                    logger.debug("Found [{}] item in remote storage of repository {}", request.getPath(), getId());
                    remoteResult = getRemoteStorage().retrieveItem(request.getPath(), propsOnly);
                    remoteResult.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, getId());
                    remoteResult.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY_GROUP,
                            getGroupId());
                    if (getStatisticsGatherer() != null) {
                        getStatisticsGatherer().remoteHit(request, this, remoteResult.getProperties());
                    }
                    remoteResult = getRepositoryLogic().afterRemoteCopyFound(request, localResult, remoteResult, this);
                    if (remoteResult != null && !remoteResult.getProperties().isDirectory()
                            && getLocalStorage().isWritable()) {
                        if (getRepositoryLogic().shouldStoreLocallyAfterRemoteRetrieval(request, localResult, remoteResult)) {
                            logger.debug("Storing [{}] item in writable storage of repository {}", request.getPath(),
                                    getId());
                            storeItem(request, remoteResult);
                            remoteResult = getLocalStorage().retrieveItem(request.getPath(), propsOnly);
                            remoteResult.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, getId());
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

    /**
     * Forces repository reindexing. If there is no indexer supplied with repos,
     * this call will do nothing.
     * 
     */
    public void reindex() {
        if (getLocalStorage() == null) {
            logger.info("Will NOT reindex nor recreateMetadata on repository {}, since it have no local storage defined.", getId());
            return;
        }
        if (getLocalStorage().isMetadataAware()) {
            logger.info("Recreating metadata on repository {}", getId());
            Map initialData = new HashMap();
            getLocalStorage().recreateMetadata(initialData);
        }
        if (getIndexer() == null) {
            logger.info("Will NOT reindex repository {}, since it have no indexer defined.", getId());
            return;
        }
        logger.info("Reindexing repository {}", getId());

        int indexed = 0;
        Stack stack = new Stack();
        List dir = getLocalStorage().listItems(ItemProperties.PATH_ROOT);
        List batch = new ArrayList();
        stack.push(dir);
        while (!stack.isEmpty()) {
            dir = (List) stack.pop();
            for (Iterator i = dir.iterator(); i.hasNext();) {
                ItemProperties ip = (ItemProperties) i.next();
                ip.setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, getId());
                ip.setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY_GROUP, getGroupId());
                // Who is interested in origin from index?
                // if (getRemoteStorage() != null) {
                // ip.setMetadata(ItemProperties.METADATA_ORIGINATING_URL,
                // getRemoteStorage().getAbsoluteUrl(
                // ip.getPath()), false);
                // }
                if (ip.isDirectory()) {
                    List subdir = getLocalStorage().listItems(ip.getPath());
                    stack.push(subdir);
                } else {
                    // TODO: possible memory problem here with large
                    // repositories!
                    batch.add(ip);
                    indexed++;
                }
            }
        }
        getIndexer().addItemProperties(batch);
        logger.info("Indexed {} items", Integer.toString(indexed));
    }

}
