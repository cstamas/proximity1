package org.abstracthorizon.proximity.indexer;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.abstracthorizon.proximity.storage.local.LocalStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractIndexer implements Indexer {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected List storages = new ArrayList();
    
    protected List searchableKeywords = new ArrayList();

    public void initialize() {
        logger.info("Initializing indexer {}...", this.getClass().getName());
        doInitialize();
    }

    protected abstract void doInitialize();

    public void registerLocalStorage(LocalStorage storage) {
        storages.add(storage);
        addSearchableKeywords(storage.getProxiedItemPropertiesFactory().getSearchableKeywords());
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

}
