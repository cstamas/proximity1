package hu.ismicro.commons.proximity.base.indexer;

import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.base.Indexer;
import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.Storage;
import hu.ismicro.commons.proximity.base.StorageException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractIndexer implements Indexer {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected List storages = new ArrayList();
    
    protected List searchableKeywords = new ArrayList();

    public void initialize() {
        logger.info("Initializing indexer " + this.getClass().getName() + "...");
        doInitialize();
    }

    protected abstract void doInitialize();

    public void registerLocalStorage(Storage storage) {
        storages.add(storage);
    }

    public List getSearchableKeywords() {
        return searchableKeywords;
    }
    
    public void addSearchableKeywords(List kws) {
        for (Iterator i = kws.iterator(); i.hasNext(); ) {
            String kw = (String) i.next();
            if (!searchableKeywords.contains(kw)) {
                searchableKeywords.add(kw);
            }
        }
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
