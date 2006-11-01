package org.abstracthorizon.proximity.indexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.abstracthorizon.proximity.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractIndexer implements Indexer {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected Map repositories = new HashMap();

	public void initialize() {
		logger.info("Initializing indexer {}...", this.getClass().getName());
		doInitialize();
	}

	public void registerRepository(Repository repository) {
		logger.info("Registering repository {} into indexer {}...", repository.getId(), this.getClass().getName());
		repositories.put(repository.getId(), repository);
		repository.setIndexer(this);
	}

	public void unregisterRepository(Repository repository) {
		if (repositories.containsKey(repository.getId())) {
			logger
					.info("Unregistering repository {} from indexer {}...", repository.getId(), this.getClass()
							.getName());
			repositories.remove(repository.getId());
			repository.setIndexer(null);
		}
	}

	public List getSearchableKeywords() {
		Set searchableKeywords = new TreeSet();
		getIndexerSpecificSearchableKeywords(searchableKeywords);
		for (Iterator i = repositories.keySet().iterator(); i.hasNext();) {
			String repoId = (String) i.next();
			Repository repository = (Repository) repositories.get(repoId);
			if (repository.getLocalStorage() != null) {
				searchableKeywords.addAll(repository.getLocalStorage().getProxiedItemPropertiesFactory()
						.getSearchableKeywords());
			}
		}
		return new ArrayList(searchableKeywords);
	}

	protected abstract void getIndexerSpecificSearchableKeywords(Set kwSet);

	protected abstract void doInitialize();

}
