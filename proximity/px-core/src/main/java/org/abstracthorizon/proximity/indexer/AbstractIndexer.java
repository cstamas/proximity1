package org.abstracthorizon.proximity.indexer;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.abstracthorizon.proximity.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractIndexer implements Indexer {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected List searchableKeywords = new ArrayList();

    public void initialize() {
        logger.info("Initializing indexer {}...", this.getClass().getName());
        doInitialize();
    }

    protected abstract void doInitialize();

    public void registerRepository(Repository repository) {
        repository.setIndexer(this);
        addSearchableKeywords(repository.getLocalStorage().getProxiedItemPropertiesFactory().getSearchableKeywords());
        Collections.sort(searchableKeywords);
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
