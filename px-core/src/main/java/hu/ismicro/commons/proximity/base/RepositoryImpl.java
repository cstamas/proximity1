package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.AccessDeniedException;
import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.Repository;
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

    public String getURIPrefix() {
        return uriPrefix;
    }

    public void setURIPrefix(String prefix) {
        this.uriPrefix = prefix;
    }

    public boolean isListable() {
        return listable;
    }

    public void setListable(boolean listable) {
        this.listable = listable;
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

    public ProxiedItemProperties retrieveItemProperties(ProximityRequest request) throws ItemNotFoundException,
            StorageException, AccessDeniedException {
        accessManager.decide(request, null);
        return (ProxiedItemProperties) retrieveItem(true, request).getProperties();
    }

    public ProxiedItem retrieveItem(ProximityRequest request) throws ItemNotFoundException, StorageException,
            AccessDeniedException {
        accessManager.decide(request, null);
        return retrieveItem(false, request);
    }

    public void deleteItemProperties(String path) throws StorageException {
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

    public void deleteItem(String path) throws StorageException {
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

    public void storeItem(Item item) throws StorageException {
        if (getLocalStorage() != null && getLocalStorage().isWritable()) {
            getLocalStorage().storeItem(item);
        } else {
            throw new UnsupportedOperationException("The repository " + getId() + " have no local storage!");
        }
        if (getIndexer() != null && getRepositoryLogic().shouldIndex(item.getProperties())) {
            getIndexer().addItemProperties(getItemUid(item.getProperties()), item.getProperties());
        }
    }

    public List listItems(ProximityRequest request) throws StorageException {
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
        if (getURIPrefix() != null && getURIPrefix().length() != 0) {
            return path.substring(getURIPrefix().length());
        } else {
            return path;
        }
    }

    protected String putPathPrefix(String path) {
        if (getURIPrefix() != null && getURIPrefix().length() != 0) {
            return PathHelper.absolutizePathFromBase(getURIPrefix(), path);
        } else {
            return path;
        }
    }

    protected ProxiedItem retrieveItem(boolean propsOnly, ProximityRequest request) throws ItemNotFoundException,
            StorageException {
        ProxiedItem result = null;
        try {
            if (getLocalStorage() != null) {
                if (getRepositoryLogic().shouldCheckForLocalCopy(request)) {
                    if ((propsOnly && getLocalStorage().containsItemProperties(request.getPath()))
                            || (getLocalStorage().containsItem(request.getPath()))) {
                        logger.debug("Found " + request.getPath() + " item in storage of repository " + getId());
                        if (propsOnly) {
                            result = new ProxiedItem();
                            result.setProperties(getLocalStorage().retrieveItemProperties(request.getPath()));
                        } else {
                            result = getLocalStorage().retrieveItem(request.getPath());
                        }
                        result.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, getId());
                        if (getStatisticsGatherer() != null) {
                            getStatisticsGatherer().localHit(request, this, result.getProperties(), propsOnly);
                        }
                        result = getRepositoryLogic().afterLocalCopyFound(result, this);
                    } else {
                        logger.debug("Not found " + request.getPath() + " item in storage of repository " + getId());
                    }
                }
            }
            if (getRepositoryLogic().shouldCheckForRemoteCopy(request, result) && getRemoteStorage() != null) {
                if ((propsOnly && getRemoteStorage().containsItemProperties(request.getPath()))
                        || (getRemoteStorage().containsItem(request.getPath()))) {
                    logger.debug("Found " + request.getPath() + " item in remote storage of repository " + getId());
                    if (propsOnly) {
                        result = new ProxiedItem();
                        result.setProperties(getRemoteStorage().retrieveItemProperties(request.getPath()));
                    } else {
                        result = getRemoteStorage().retrieveItem(request.getPath());
                    }
                    result.getProperties().setMetadata(ItemProperties.METADATA_OWNING_REPOSITORY, getId());
                    if (getStatisticsGatherer() != null) {
                        getStatisticsGatherer().remoteHit(request, this, result.getProperties(), propsOnly);
                    }
                    result = getRepositoryLogic().afterRemoteCopyFound(result, this);
                    if (result != null && !result.getProperties().isDirectory() && getLocalStorage().isWritable()) {
                        if (getRepositoryLogic().shouldStoreLocallyAfterRemoteRetrieval(result.getProperties())) {
                            logger.info("Storing " + request.getPath() + " item in writable storage of repository "
                                    + getId());
                            storeItem(result);
                            if (propsOnly) {
                                result.setProperties(getLocalStorage().retrieveItemProperties(request.getPath()));
                            } else {
                                result = getLocalStorage().retrieveItem(request.getPath());
                            }
                        }
                    }
                } else {
                    logger.debug("Not found " + request.getPath() + " item in remote peer of repository " + getId());
                }

            }
            if (result == null) {
                throw new ItemNotFoundException(request.getPath());
            }
            logger
                    .info(propsOnly ? "Item " : "ItemProperties " + request.getPath() + " found in repository "
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
