package org.abstracthorizon.proximity.indexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.NoSuchRepositoryException;
import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.ProximityRequestListener;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.RepositoryNotAvailableException;
import org.abstracthorizon.proximity.events.ItemDeleteEvent;
import org.abstracthorizon.proximity.events.ItemStoreEvent;
import org.abstracthorizon.proximity.events.ProximityRequestEvent;
import org.abstracthorizon.proximity.impl.ProximityUtils;
import org.abstracthorizon.proximity.storage.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractIndexer implements Indexer, ProximityRequestListener {

    public static String DOC_PATH = "_path";

    public static String DOC_NAME = "_name";

    public static String DOC_REPO = "_repo";

    public static String DOC_GROUP = "_group";

    private Proximity proximity;

    private boolean recreateIndexes = true;

    private int reindexBatchSize = 1000;

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public int getReindexBatchSize() {
	return reindexBatchSize;
    }

    public void setReindexBatchSize(int reindexBatchSize) {
	this.reindexBatchSize = reindexBatchSize;
    }

    public void initialize() {
	if (getProximity() != null) {
	    logger.info("Initializing indexer {}...", this.getClass().getName());
	    doInitialize();
	    logger.info("* Reindexing all repositories marked as (reindexAtInitialize = true)");
	    for (Iterator i = getProximity().getRepositories().iterator(); i.hasNext();) {
		Repository repo = (Repository) i.next();
		if (repo.isReindexAtInitialize()) {
		    reindex(repo);
		}
	    }
	} else {
	    throw new IllegalStateException("Indexer has no Proximity instance!");
	}
    }

    public void setProximity(Proximity proximity) {
	this.proximity = proximity;
	getProximity().addProximityRequestListener(this);
    }

    public Proximity getProximity() {
	return this.proximity;
    }

    public void addItemProperties(ItemProperties ip) throws StorageException {
	doAddItemProperties(ip);
    }

    public void addItemProperties(List itemProperties) throws StorageException {
	doAddItemProperties(itemProperties);
    }

    public void deleteItemProperties(ItemProperties ip) throws StorageException {
	doDeleteItemProperties(ip);
    }

    public Set getSearchableKeywords() {
	Set kws = new HashSet();
	List reposes = getProximity().getRepositories();
	for (Iterator i = reposes.iterator(); i.hasNext();) {
	    Repository repo = (Repository) i.next();
	    if (repo.getLocalStorage() != null && repo.isIndexable()) {
		kws.addAll(repo.getLocalStorage().getProxiedItemPropertiesFactory().getSearchableKeywords());
	    }
	}
	// add constant kws
	kws.add(DOC_PATH);
	kws.add(DOC_NAME);
	kws.add(DOC_REPO);
	kws.add(DOC_GROUP);
	return kws;
    }

    public List searchByItemPropertiesExample(ItemProperties ip) throws StorageException {
	return postprocessSearchResult(doSearchByItemPropertiesExample(ip));
    }

    public List searchByQuery(String queryStr) throws IndexerException, StorageException {
	return postprocessSearchResult(doSearchByQuery(queryStr));
    }

    public void proximityRequestEvent(ProximityRequestEvent event) {
	if (ItemDeleteEvent.class.isAssignableFrom(event.getClass())) {
	    // delete from index
	    logger.debug("Deleting item [{}] from index.", event.getRequest().getPath());
	    deleteItemProperties(((ItemDeleteEvent) event).getItemProperties());
	} else if (ItemStoreEvent.class.isAssignableFrom(event.getClass())) {
	    // add to index
	    logger.debug("Adding item [{}] to index.", event.getRequest().getPath());
	    addItemProperties(((ItemStoreEvent) event).getItemProperties());
	}

    }

    public boolean isRecreateIndexes() {
	return recreateIndexes;
    }

    public void setRecreateIndexes(boolean recreateIndexes) {
	this.recreateIndexes = recreateIndexes;
    }

    protected List postprocessSearchResult(List idxresult) {
	List result = new ArrayList(idxresult.size());
	if (idxresult.size() > 0) {
	    ItemProperties ip = null;
	    ProximityRequest rq = new ProximityRequest();
	    rq.setLocalOnly(true);
	    rq.setPropertiesOnly(true);
	    for (Iterator i = idxresult.iterator(); i.hasNext();) {
		ip = (ItemProperties) i.next();
		rq.setPath(ip.getPath());
		rq.setTargetedReposId(ip.getRepositoryId());
		try {
		    Repository repo = (Repository) getProximity().getRepository(ip.getRepositoryId());
		    result.add(repo.retrieveItem(rq).getProperties());
		} catch (AccessDeniedException ex) {
		    logger.debug("Access denied on repo {} for path [{}], ignoring it.", ip.getRepositoryId(), ip.getPath());
		} catch (RepositoryNotAvailableException ex) {
		    logger.debug("Repo {} not available, ignoring it.", ip.getRepositoryId());
		} catch (NoSuchRepositoryException ex) {
		    logger.info("Item returned on index shows to an unexistent repo, ignoring. " + "Maybe repo needs a reindex?", ip
			    .getRepositoryId(), ip.getPath());
		} catch (ItemNotFoundException ex) {
		    logger.info("Item not found in repo {} on path [{}] but index contains it, ignoring. " + "Maybe repo needs a reindex?", ip
			    .getRepositoryId(), ip.getPath());
		}
	    }
	    if (getProximity().isEmergeRepositoryGroups()) {
		ProximityUtils.mangleItemPathsForEmergeGroups(result);
	    }
	}
	return result;
    }

    public void reindex() {
	logger.info("Reindexing of all defined repositories requested.");
	for (Iterator i = getProximity().getRepositories().iterator(); i.hasNext();) {
	    Repository repo = (Repository) i.next();
	    reindex(repo);
	}
    }

    public void reindex(String repoId) throws NoSuchRepositoryException {
	logger.info("Reindexing of {} repository requested", repoId);
	Repository repo = getProximity().getRepository(repoId);
	reindex(repo);
    }

    protected void reindex(Repository repository) {
	if (repository.getLocalStorage() == null) {
	    logger.info("Will NOT reindex nor recreateMetadata on repository {}, since it have no local storage defined.", repository.getId());
	    return;
	}
	if (repository.getLocalStorage().isMetadataAware()) {
	    logger.info("Recreating metadata on repository {}", repository.getId());
	    Map initialData = new HashMap();
	    repository.getLocalStorage().recreateMetadata(initialData);
	}
	if (!repository.isIndexable()) {
	    logger.info("Will NOT reindex repository {}, since it is not indexable.", repository.getId());
	    return;
	}
	logger.info("Reindexing repository {}", repository.getId());

	int indexed = 0;
	Stack stack = new Stack();
	List dir = repository.getLocalStorage().listItems(ItemProperties.PATH_ROOT);
	List batch = new ArrayList(getReindexBatchSize());
	stack.push(dir);
	while (!stack.isEmpty()) {
	    dir = (List) stack.pop();
	    for (Iterator i = dir.iterator(); i.hasNext();) {
		ItemProperties ip = (ItemProperties) i.next();
		// Who is interested in origin from index?
		// if (getRemoteStorage() != null) {
		// ip.setMetadata(ItemProperties.METADATA_ORIGINATING_URL,
		// getRemoteStorage().getAbsoluteUrl(
		// ip.getPath()), false);
		// }
		if (ip.isDirectory()) {
		    stack.push(repository.getLocalStorage().listItems(ip.getPath()));
		} else {
		    ip.setRepositoryId(repository.getId());
		    ip.setRepositoryGroupId(repository.getGroupId());
		    batch.add(ip);
		    indexed++;
		}
	    }
	    if (batch.size() > getReindexBatchSize()) {
		addItemProperties(batch);
		batch.clear();
	    }
	}
	if (batch.size() > 0) {
	    addItemProperties(batch);
	}
	logger.info("Indexed {} items", Integer.toString(indexed));

    }

    protected abstract void doInitialize();

    protected abstract List doSearchByItemPropertiesExample(ItemProperties ip) throws StorageException;

    protected abstract List doSearchByQuery(String queryStr) throws IndexerException, StorageException;

    protected abstract void doAddItemProperties(ItemProperties ip) throws StorageException;

    protected abstract void doAddItemProperties(List itemProperties) throws StorageException;

    protected abstract void doDeleteItemProperties(ItemProperties ip) throws StorageException;

}
