package org.abstracthorizon.proximity.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.RepositoryNotAvailableException;
import org.abstracthorizon.proximity.access.AccessManager;
import org.abstracthorizon.proximity.access.OpenAccessManager;
import org.abstracthorizon.proximity.stats.StatisticsGatherer;
import org.abstracthorizon.proximity.storage.StorageException;
import org.abstracthorizon.proximity.storage.local.LocalStorage;
import org.abstracthorizon.proximity.storage.remote.RemoteStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRepository implements Repository {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private String id;

	private String groupId = "default";

	private LocalStorage localStorage;

	private RemoteStorage remoteStorage;

	private AccessManager accessManager = new OpenAccessManager();

	private StatisticsGatherer statisticsGatherer;

	private int rank = 999;

	private boolean available = true;

	private boolean offline = false;

	private boolean listable = true;

	private boolean indexable = true;

	private boolean reindexAtInitialize = true;

	private long notFoundCachePeriod = 86400 * 1000; // 24 hours

	private Map notFoundCache = Collections.synchronizedMap(new HashMap());

	public AccessManager getAccessManager() {
		return accessManager;
	}

	public void setAccessManager(AccessManager accessManager) {
		this.accessManager = accessManager;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
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

	public LocalStorage getLocalStorage() {
		return localStorage;
	}

	public void setLocalStorage(LocalStorage localStorage) {
		this.localStorage = localStorage;
	}

	public boolean isOffline() {
		return offline;
	}

	public void setOffline(boolean offline) {
		this.offline = offline;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public boolean isReindexAtInitialize() {
		return reindexAtInitialize;
	}

	public void setReindexAtInitialize(boolean reindexAtInitialize) {
		this.reindexAtInitialize = reindexAtInitialize;
	}

	public boolean isIndexable() {
		return indexable;
	}

	public void setIndexable(boolean indexable) {
		this.indexable = indexable;
	}

	public RemoteStorage getRemoteStorage() {
		return remoteStorage;
	}

	public void setRemoteStorage(RemoteStorage remoteStorage) {
		this.remoteStorage = remoteStorage;
	}

	public StatisticsGatherer getStatisticsGatherer() {
		return statisticsGatherer;
	}

	public void setStatisticsGatherer(StatisticsGatherer statisticsGatherer) {
		this.statisticsGatherer = statisticsGatherer;
	}

	public long getNotFoundCachePeriodInSeconds() {
		return notFoundCachePeriod / 1000;
	}

	public void setNotFoundCachePeriodInSeconds(long notFoundCachePeriod) {
		this.notFoundCachePeriod = notFoundCachePeriod * 1000;
	}

	// ---------------------------------------------------------------------------------
	// Repository Iface

	public Item retrieveItem(ProximityRequest request) throws RepositoryNotAvailableException, ItemNotFoundException,
			StorageException, AccessDeniedException {
		if (!isAvailable()) {
			throw new RepositoryNotAvailableException("The repository " + getId() + " is NOT available!");
		}
		getAccessManager().decide(request, null);
		try {
			String requestKey = getRepositoryRequestAsKey(this, request);
			if (notFoundCache.containsKey(requestKey)) {
				// it is in cache, check when it got in
				Date lastRequest = (Date) notFoundCache.get(requestKey);
				if (lastRequest.before(new Date(System.currentTimeMillis() - notFoundCachePeriod))) {
					// the notFoundCache record expired, remove it and check its
					// existence
					logger.debug("n-cache record expired, will go again remote to fetch.");
					notFoundCache.remove(requestKey);
					request.setLocalOnly(false);
				} else {
					// the notFoundCache record is still valid, do not check its
					// existence
					logger.debug("n-cache record still active, will not go remote to fetch.");
					request.setLocalOnly(true);
				}
			} else {
				// it is not in notFoundCache, check its existence
				request.setLocalOnly(false);
			}
			return doRetrieveItem(request);
		} catch (ItemNotFoundException ex) {
			// we have not found it
			// put the path into not found cache
			String requestPath = getRepositoryRequestAsKey(this, request);
			if (!notFoundCache.containsKey(requestPath)) {
				logger.debug("Caching failed request [{}] to n-cache.", requestPath);
				notFoundCache.put(requestPath, new Date());
			}
			throw ex;
		}
	}

	public void deleteItem(ProximityRequest request) throws RepositoryNotAvailableException, StorageException {
		if (!isAvailable()) {
			throw new RepositoryNotAvailableException("The repository " + getId() + " is NOT available!");
		}
		if (getLocalStorage() != null) {
			try {
				ItemProperties itemProps = getLocalStorage().retrieveItem(request.getPath(), true).getProperties();
				itemProps.setRepositoryId(getId());
				itemProps.setRepositoryGroupId(getGroupId());
				getLocalStorage().deleteItem(request.getPath());

				// remove it from n-cache also
				String requestKey = getRepositoryRequestAsKey(this, request);
				if (notFoundCache.containsKey(requestKey)) {
					notFoundCache.remove(requestKey);
				}
			} catch (ItemNotFoundException ex) {
				logger.info("Path [{}] not found but deletion requested.", request.getPath());
			}
		} else {
			throw new UnsupportedOperationException("The repository " + getId() + " have no local storage!");
		}
	}

	public void storeItem(ProximityRequest request, Item item) throws RepositoryNotAvailableException, StorageException {
		if (!isAvailable()) {
			throw new RepositoryNotAvailableException("The repository " + getId() + " is NOT available!");
		}
		if (getLocalStorage() != null && getLocalStorage().isWritable()) {
			item.getProperties().setRepositoryId(getId());
			item.getProperties().setRepositoryGroupId(getGroupId());
			getLocalStorage().storeItem(item);

			// remove it from n-cache also
			String requestKey = getRepositoryRequestAsKey(this, request);
			if (notFoundCache.containsKey(requestKey)) {
				notFoundCache.remove(requestKey);
			}
		} else {
			throw new UnsupportedOperationException("The repository " + getId()
					+ " have no writable local storage defined!");
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
					ip.setRepositoryId(getId());
					ip.setRepositoryGroupId(getGroupId());
					if (getRemoteStorage() != null) {
						ip.setRemoteUrl(getRemoteStorage().getAbsoluteUrl(ip.getPath()));
					}
				}
				result.addAll(list);
			}
		}
		return result;
	}

	protected abstract Item doRetrieveItem(ProximityRequest request) throws RepositoryNotAvailableException,
			ItemNotFoundException, StorageException;

	/**
	 * Constructs a unique request key using repoId and request path.
	 * 
	 * @param repository
	 * @param request
	 * @return a unique key in form "repoId:/path/to/artifact"
	 */
	protected String getRepositoryRequestAsKey(Repository repository, ProximityRequest request) {
		StringBuffer sb = new StringBuffer(repository.getId());
		sb.append(":");
		sb.append(request.getPath());
		return sb.toString();
	}

}
