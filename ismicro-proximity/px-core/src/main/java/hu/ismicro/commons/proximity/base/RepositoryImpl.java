package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.AccessDeniedException;
import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.Repository;
import hu.ismicro.commons.proximity.RepositoryNotAvailableException;
import hu.ismicro.commons.proximity.access.AccessManager;
import hu.ismicro.commons.proximity.access.OpenAccessManager;
import hu.ismicro.commons.proximity.base.logic.DefaultProxyingRepositoryLogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryImpl implements Repository {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private String id;

    private String groupId = "default";

    private boolean available = true;

    private boolean offline = false;

    private boolean listable = true;

    private boolean reindex = true;

    private boolean recreateMetadata = true;

    private LocalStorage localStorage;

    private RemoteStorage remoteStorage;

    private Indexer indexer;

    private AccessManager accessManager = new OpenAccessManager();

    private StatisticsGatherer statisticsGatherer;

    private RepositoryLogic repositoryLogic = new DefaultProxyingRepositoryLogic();

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

    public boolean isRecreateMetadata() {
        return recreateMetadata;
    }

    public void setRecreateMetadata(boolean recreateMetadata) {
        this.recreateMetadata = recreateMetadata;
    }

    public boolean isReindex() {
        return reindex;
    }

    public void setReindex(boolean reindex) {
        this.reindex = reindex;
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

    public ProxiedItem retrieveItem(ProximityRequest request) throws RepositoryNotAvailableException,
            ItemNotFoundException, StorageException, AccessDeniedException {
        if (!isAvailable()) {
            throw new RepositoryNotAvailableException("The repository " + getId() + " is NOT available!");
        }
        accessManager.decide(request, null);
        return retrieveItemController(request);
    }

    public void deleteItem(String path) throws RepositoryNotAvailableException, StorageException {
        if (!isAvailable()) {
            throw new RepositoryNotAvailableException("The repository " + getId() + " is NOT available!");
        }
        if (getLocalStorage() != null) {
            if (getIndexer() != null) {
                try {
                    ItemProperties itemProps = getLocalStorage().retrieveItem(path).getProperties();
                    getIndexer().deleteItemProperties(getItemUid(itemProps), itemProps);
                    getLocalStorage().deleteItem(path);
                } catch (ItemNotFoundException ex) {
                    logger.info("Path [" + path + "] not found but deletion requested.", ex);
                }
            }
        } else {
            throw new UnsupportedOperationException("The repository " + getId() + " have no local storage!");
        }
    }

    public void storeItem(Item item) throws RepositoryNotAvailableException, StorageException {
        if (!isAvailable()) {
            throw new RepositoryNotAvailableException("The repository " + getId() + " is NOT available!");
        }
        if (getLocalStorage() != null && getLocalStorage().isWritable()) {
            getLocalStorage().storeItem(item);
            if (getIndexer() != null && getRepositoryLogic().shouldIndex(item.getProperties())) {
                getIndexer().addItemProperties(getItemUid(item.getProperties()), item.getProperties());
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
                    ip.setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, this.getId(), true);
                    ip.setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY_GROUP, this.getGroupId(), true);
                    if (getRemoteStorage() != null) {
                        ip.setMetadata(ItemProperties.METADATA_ORIGINATING_URL, getRemoteStorage().getAbsoluteUrl(
                                ip.getPath()), false);
                    }
                }
                result.addAll(list);
            }
        }
        return result;
    }

    // ---------------------------------------------------------------------------------
    // Housekeeping

    public void initialize() {
        if (getIndexer() != null && getLocalStorage() != null) {
            getIndexer().registerLocalStorage(getLocalStorage());
            getIndexer().addSearchableKeywords(getLocalStorage().getProxiedItemPropertiesConstructor().getSearchableKeywords());
        }

        if (recreateMetadata) {
            logger.info("Recreating metadata " + getId());
            recreateMetadata();
        }
        if (reindex) {
            logger.info("Reindexing " + getId());
            reindex();
        }
    }

    // ---------------------------------------------------------------------------------
    // Protected

    protected ProxiedItem retrieveItemController(ProximityRequest request) throws RepositoryNotAvailableException,
            ItemNotFoundException, StorageException {
        ProxiedItem localResult = null;
        ProxiedItem remoteResult = null;
        try {
            if (getLocalStorage() != null) {
                if (getRepositoryLogic().shouldCheckForLocalCopy(request)) {
                    if (getLocalStorage().containsItem(request.getPath())) {
                        logger.debug("Found " + request.getPath() + " item in storage of repository " + getId());
                        localResult = getLocalStorage().retrieveItem(request.getPath());
                        localResult.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, getId(),
                                true);
                        localResult.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY_GROUP,
                                getGroupId(), true);
                        if (getStatisticsGatherer() != null) {
                            getStatisticsGatherer().localHit(request, this, localResult.getProperties());
                        }
                        localResult = getRepositoryLogic().afterLocalCopyFound(localResult, this);
                    } else {
                        logger.debug("Not found " + request.getPath() + " item in storage of repository " + getId());
                    }
                }
            }
            if (!isOffline() && getRepositoryLogic().shouldCheckForRemoteCopy(request, localResult)
                    && getRemoteStorage() != null) {
                if (getRemoteStorage().containsItem(request.getPath())) {
                    logger.debug("Found " + request.getPath() + " item in remote storage of repository " + getId());
                    remoteResult = getRemoteStorage().retrieveItem(request.getPath());
                    remoteResult.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, getId(), true);
                    remoteResult.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY_GROUP,
                            getGroupId(), true);
                    if (getStatisticsGatherer() != null) {
                        getStatisticsGatherer().remoteHit(request, this, remoteResult.getProperties());
                    }
                    remoteResult = getRepositoryLogic().afterRemoteCopyFound(localResult, remoteResult, this);
                    if (remoteResult != null && !remoteResult.getProperties().isDirectory()
                            && getLocalStorage().isWritable()) {
                        if (getRepositoryLogic().shouldStoreLocallyAfterRemoteRetrieval(localResult, remoteResult)) {
                            logger.debug("Storing " + request.getPath() + " item in writable storage of repository "
                                    + getId());
                            storeItem(remoteResult);
                            remoteResult = getLocalStorage().retrieveItem(request.getPath());
                        }
                    }
                } else {
                    logger.debug("Not found " + request.getPath() + " item in remote peer of repository " + getId());
                }

            }
            ProxiedItem result = getRepositoryLogic().afterRetrieval(localResult, remoteResult);
            if (result == null) {
                throw new ItemNotFoundException(request.getPath());
            }
            if (getRemoteStorage() != null) {
                result.getProperties().setMetadata(ItemProperties.METADATA_ORIGINATING_URL,
                        getRemoteStorage().getAbsoluteUrl(result.getProperties().getPath()), false);
            }
            logger.debug("Item " + request.getPath() + " found in repository " + getId());
            return result;
        } catch (ItemNotFoundException ex) {
            throw new ItemNotFoundException(request.getPath(), getId());
        }
    }

    protected String getItemUid(ItemProperties ip) {
        return getId() + ":" + PathHelper.concatPaths(ip.getAbsolutePath(), ip.getName());
    }

    /**
     * Forces metadata creation if the underlying storage is metadata aware.
     * Otherwise the call will do nothing.
     * 
     */
    public void recreateMetadata() {
        if (getLocalStorage() == null) {
            logger.info("Will NOT recreate metadata on " + getId() + ", since it have no local storage defined.");
            return;
        }
        if (!getLocalStorage().isMetadataAware()) {
            logger.info("Will NOT recreate metadata on " + getId()
                    + ", since it have no metadata-aware local storage defined.");
            return;
        }

        getLocalStorage().recreateMetadata(null);
    }

    /**
     * Forces repository reindexing. If there is no indexer supplied with repos,
     * this call will do nothing.
     * 
     */
    public void reindex() {
        if (getIndexer() == null) {
            logger.info("Will NOT reindex repository " + getId() + ", since it have no indexer defined.");
            return;
        }
        if (getLocalStorage() == null) {
            logger.info("Will NOT reindex repository " + getId() + ", since it have no local storage defined.");
            return;
        }
        int indexed = 0;
        Stack stack = new Stack();
        List dir = getLocalStorage().listItems(PathHelper.PATH_SEPARATOR);
        Map batch = new HashMap();
        stack.push(dir);
        while (!stack.isEmpty()) {
            dir = (List) stack.pop();
            for (Iterator i = dir.iterator(); i.hasNext();) {
                ItemProperties ip = (ItemProperties) i.next();
                ip.setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, getId(), true);
                ip.setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY_GROUP, getGroupId(), true);
                if (getRemoteStorage() != null) {
                    ip.setMetadata(ItemProperties.METADATA_ORIGINATING_URL, getRemoteStorage().getAbsoluteUrl(
                            ip.getPath()), false);
                }
                if (ip.isDirectory()) {
                    List subdir = getLocalStorage().listItems(
                            PathHelper.walkThePath(ip.getAbsolutePath(), ip.getName()));
                    stack.push(subdir);
                } else {
                    // TODO: possible memory problem here with large
                    // repositories!
                    batch.put(getItemUid(ip), ip);
                    indexed++;
                }
            }
        }
        getIndexer().addItemProperties(batch);
        logger.info("Indexed " + indexed + " items");
    }

}
