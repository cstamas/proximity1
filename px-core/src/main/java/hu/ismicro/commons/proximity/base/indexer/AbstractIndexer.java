package hu.ismicro.commons.proximity.base.indexer;

import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.base.Indexer;
import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.Storage;
import hu.ismicro.commons.proximity.base.StorageException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractIndexer implements Indexer {

    protected Log logger = LogFactory.getLog(this.getClass());

    protected List storages = new ArrayList();

    public void initialize() {
        logger.info("Initializing indexer " + this.getClass().getName() + "...");
        doInitialize();
    }

    protected abstract void doInitialize();

    public void registerLocalStorage(Storage storage) {
        storages.add(storage);
    }

    public List getSearchableKeywords() {
        List result = new ArrayList();
        // set the default ItemProperties
        result.add(ItemProperties.METADATA_NAME);
        result.add(ItemProperties.METADATA_OWNING_REPOSITORY);
        result.add(ItemProperties.METADATA_ABSOLUTE_PATH);
        result.add(ItemProperties.METADATA_FILESIZE);
        result.add(ItemProperties.METADATA_IS_DIRECTORY);
        result.add(ItemProperties.METADATA_IS_FILE);
        result.add(ItemProperties.METADATA_ORIGINATING_URL);
        return result;
    }

    protected ProxiedItem retrieveItemFromStorages(String path) throws ItemNotFoundException {
        Storage storage = null;
        for (Iterator i = storages.iterator(); i.hasNext();) {
            storage = (Storage) i.next();
            if (storage.containsItem(path)) {
                try {
                    return storage.retrieveItem(path);
                } catch (ItemNotFoundException ex) {
                    // ignore this and go away
                } catch (StorageException ex) {
                    logger.error("Got error during artifact search in registered storages.", ex);
                }
            }
        }
        throw new ItemNotFoundException("Could not find " + path + " in registered LocalStorages.");
    }

}
