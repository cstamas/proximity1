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
import hu.ismicro.commons.proximity.base.logic.DefaultProxyingLogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RepositoryImpl implements Repository {

    protected Log logger = LogFactory.getLog(this.getClass());

    private String id;

    private String uriPrefix = null;
    
    private boolean available = true;
    
    private boolean offline = false;
    
    private boolean listable = true;

    private boolean reindex = true;

    private boolean recreateMetadata = true;

    private Storage localStorage;

    private Storage remoteStorage;

    private Indexer indexer;

    private AccessManager accessManager = new OpenAccessManager();

    private StatisticsGatherer statisticsGatherer;

    private RepositoryLogic repositoryLogic = new DefaultProxyingLogic();

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

    public String getUriPrefix() {
        return uriPrefix;
    }

    public void setUriPrefix(String uriPrefix) {
        this.uriPrefix = uriPrefix;
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

    public ProxiedItemProperties retrieveItemProperties(ProximityRequest request) throws RepositoryNotAvailableException, ItemNotFoundException,
            StorageException, AccessDeniedException {
        if (!isAvailable()) {
            throw new RepositoryNotAvailableException("The repository " + getId() + " is NOT available!");
        }
        accessManager.decide(request, null);
        return (ProxiedItemProperties) retrieveItem(true, request).getProperties();
    }

    public ProxiedItem retrieveItem(ProximityRequest request) throws RepositoryNotAvailableException, ItemNotFoundException, StorageException,
            AccessDeniedException {
        if (!isAvailable()) {
            throw new RepositoryNotAvailableException("The repository " + getId() + " is NOT available!");
        }
        accessManager.decide(request, null);
        return retrieveItem(false, request);
    }

    public void deleteItemProperties(String path) throws RepositoryNotAvailableException, StorageException {
        if (!isAvailable()) {
            throw new RepositoryNotAvailableException("The repository " + getId() + " is NOT available!");
        }
        if (getLocalStorage() != null) {
            if (getIndexer() != null) {
                try {
                    ItemProperties itemProps = getLocalStorage().retrieveItemProperties(path);
                    getIndexer().deleteItemProperties(getItemUid(itemProps), itemProps);
                } catch (ItemNotFoundException ex) {
                    logger.info("Path [" + path + "] not found but deletion requested.", ex);
                }
            }
            getLocalStorage().deleteItemProperties(path);
        } else {
            throw new UnsupportedOperationException("The repository " + getId() + " have no local storage!");
        }
    }

    public void deleteItem(String path) throws RepositoryNotAvailableException, StorageException {
        if (!isAvailable()) {
            throw new RepositoryNotAvailableException("The repository " + getId() + " is NOT available!");
        }
        if (getLocalStorage() != null) {
            if (getIndexer() != null) {
                try {
                    ItemProperties itemProps = getLocalStorage().retrieveItemProperties(path);
                    getIndexer().deleteItemProperties(getItemUid(itemProps), itemProps);
                } catch (ItemNotFoundException ex) {
                    logger.info("Path [" + path + "] not found but deletion requested.", ex);
                }
            }
            getLocalStorage().deleteItem(path);
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
        } else {
            throw new UnsupportedOperationException("The repository " + getId() + " have no local storage!");
        }
        if (getIndexer() != null && getRepositoryLogic().shouldIndex(item.getProperties())) {
            getIndexer().addItemProperties(getItemUid(item.getProperties()), item.getProperties());
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
                }
                result.addAll(list);
            }
        }
        return result;
    }

    public void initialize() {
        logger.info("Initializing...");

        if (getLocalStorage() != null) {
            indexer.registerLocalStorage(getLocalStorage());
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

    protected String removePathPrefix(String path) {
        if (getUriPrefix() != null && getUriPrefix().length() != 0) {
            return path.substring(getUriPrefix().length());
        } else {
            return path;
        }
    }

    protected String putPathPrefix(String path) {
        if (getUriPrefix() != null && getUriPrefix().length() != 0) {
            return PathHelper.absolutizePathFromBase(getUriPrefix(), path);
        } else {
            return path;
        }
    }

    protected ProxiedItem retrieveItem(boolean propsOnly, ProximityRequest request) throws RepositoryNotAvailableException, ItemNotFoundException,
            StorageException {
        ProxiedItem localResult = null;
        ProxiedItem remoteResult = null;
        try {
            if (getLocalStorage() != null) {
                if (getRepositoryLogic().shouldCheckForLocalCopy(request)) {
                    if ((propsOnly && getLocalStorage().containsItemProperties(request.getPath()))
                            || (getLocalStorage().containsItem(request.getPath()))) {
                        logger.debug("Found " + request.getPath() + " item in storage of repository " + getId());
                        if (propsOnly) {
                            localResult = new ProxiedItem();
                            localResult.setProperties(getLocalStorage().retrieveItemProperties(request.getPath()));
                        } else {
                            localResult = getLocalStorage().retrieveItem(request.getPath());
                        }
                        localResult.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, getId());
                        if (getStatisticsGatherer() != null) {
                            getStatisticsGatherer().localHit(request, this, localResult.getProperties(), propsOnly);
                        }
                        localResult = getRepositoryLogic().afterLocalCopyFound(localResult, this);
                    } else {
                        logger.debug("Not found " + request.getPath() + " item in storage of repository " + getId());
                    }
                }
            }
            if (!isOffline() && getRepositoryLogic().shouldCheckForRemoteCopy(request, localResult) && getRemoteStorage() != null) {
                if ((propsOnly && getRemoteStorage().containsItemProperties(request.getPath()))
                        || (getRemoteStorage().containsItem(request.getPath()))) {
                    logger.debug("Found " + request.getPath() + " item in remote storage of repository " + getId());
                    if (propsOnly) {
                        remoteResult = new ProxiedItem();
                        remoteResult.setProperties(getRemoteStorage().retrieveItemProperties(request.getPath()));
                    } else {
                        remoteResult = getRemoteStorage().retrieveItem(request.getPath());
                    }
                    remoteResult.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, getId());
                    if (getStatisticsGatherer() != null) {
                        getStatisticsGatherer().remoteHit(request, this, remoteResult.getProperties(), propsOnly);
                    }
                    remoteResult = getRepositoryLogic().afterRemoteCopyFound(localResult, remoteResult, this);
                    if (remoteResult != null && !remoteResult.getProperties().isDirectory()
                            && getLocalStorage().isWritable()) {
                        if (getRepositoryLogic().shouldStoreLocallyAfterRemoteRetrieval(localResult, remoteResult)) {
                            logger.info("Storing " + request.getPath() + " item in writable storage of repository "
                                    + getId());
                            storeItem(remoteResult);
                            if (propsOnly) {
                                remoteResult.setProperties(getLocalStorage().retrieveItemProperties(request.getPath()));
                            } else {
                                remoteResult = getLocalStorage().retrieveItem(request.getPath());
                            }
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
            logger.info(((propsOnly) ? ("Item ") : ("ItemProperties ")) + request.getPath() + " found in repository "
                    + getId());
            return result;
        } catch (ItemNotFoundException ex) {
            throw new ItemNotFoundException(request.getPath(), getId());
        }
    }

    protected String getItemUid(ItemProperties ip) {
        return getId() + ":" + PathHelper.concatPaths(ip.getAbsolutePath(), ip.getName());
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
                ip.setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, getId());
                if (ip.isDirectory()) {
                    List subdir = getLocalStorage().listItems(
                            PathHelper.walkThePath(ip.getAbsolutePath(), ip.getName()));
                    stack.push(subdir);
                } else {
                    // TODO: possible problem here with large repositories!
                    batch.put(getItemUid(ip), ip);
                    // getIndexer().addItemProperties(getItemUid(ip), ip);
                    indexed++;
                }
            }
        }
        getIndexer().addItemProperties(batch);
        logger.info("Indexed " + indexed + " items");
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
        if (!getLocalStorage().isWritable()) {
            logger.info("Will NOT recreate metadata on " + getId()
                    + ", since it have no writable local storage defined.");
            return;
        }
        if (!getLocalStorage().isMetadataAware()) {
            logger.info("Will NOT recreate metadata on " + getId()
                    + ", since it have no metadata-aware local storage defined.");
            return;
        }
        int processed = 0;
        Stack stack = new Stack();
        List dir = getLocalStorage().listItems(PathHelper.PATH_SEPARATOR);
        stack.push(dir);
        while (!stack.isEmpty()) {
            dir = (List) stack.pop();
            for (Iterator i = dir.iterator(); i.hasNext();) {
                ItemProperties ip = (ItemProperties) i.next();
                ip.setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, getId());
                if (ip.isDirectory()) {
                    List subdir = getLocalStorage().listItems(
                            PathHelper.walkThePath(ip.getAbsolutePath(), ip.getName()));
                    stack.push(subdir);
                } else {
                    getLocalStorage().storeItemProperties(ip);
                    processed++;
                }
            }
        }
        logger.info("Recreated metadata on " + processed + " items");
    }

}
