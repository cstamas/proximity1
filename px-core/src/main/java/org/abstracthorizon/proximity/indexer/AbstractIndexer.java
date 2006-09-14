package org.abstracthorizon.proximity.indexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.abstracthorizon.proximity.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractIndexer implements Indexer {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected Set searchableKeywords = Collections.synchronizedSortedSet(new TreeSet());

    public void initialize() {
        logger.info("Initializing indexer {}...", this.getClass().getName());
        doInitialize();
    }

    public void registerRepository(Repository repository) {
        logger.info("Registering repository {} into indexer {}...", repository.getId(), this.getClass().getName());
        repository.setIndexer(this);
        if (repository.getLocalStorage() != null) {
            searchableKeywords.addAll(repository.getLocalStorage().getProxiedItemPropertiesFactory()
                    .getSearchableKeywords());
        }
    }

    public void unregisterRepository(Repository repository) {
        logger.info("Unregistering repository {} from indexer {}...", repository.getId(), this.getClass().getName());
        // TODO: what happens whit searchable kw's unique to the just removed
        // repo?
        // The index will still contain the removed repo until next full reindex
    }

    public List getSearchableKeywords() {
        return new ArrayList(searchableKeywords);
    }

    protected void addSearchableKeyword(String kw) {
        searchableKeywords.add(kw);
    }

    protected abstract void doInitialize();

}
