package org.abstracthorizon.proximity.ws;

import java.util.List;
import java.util.Set;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.NoSuchRepositoryException;
import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.ProximityException;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.indexer.Indexer;

public class SearchServiceImpl implements SearchService {

	private Indexer indexer;

	private Proximity proximity;

	public Indexer getIndexer() {
		return indexer;
	}

	public void setIndexer(Indexer indexer) {
		this.indexer = indexer;
	}

	public Proximity getProximity() {
		return proximity;
	}

	public void setProximity(Proximity proximity) {
		this.proximity = proximity;
	}

	public ItemProperties[] listItems(ProximityRequest request) throws AccessDeniedException, NoSuchRepositoryException {
		List result = getProximity().listItems(request);
		return (ItemProperties[]) result.toArray(new ItemProperties[result.size()]);
	}

	public String[] getSearchableKeywords() {
		Set result = getIndexer().getSearchableKeywords();
		return (String[]) result.toArray(new String[result.size()]);
	}

	public ItemProperties[] searchItemByExample(ItemProperties example) throws ProximityException {
		List result = getIndexer().searchByItemPropertiesExample(example);
		return (ItemProperties[]) result.toArray(new ItemProperties[result.size()]);
	}

	public ItemProperties[] searchItemByQuery(String query) throws ProximityException {
		List result = getIndexer().searchByQuery(query);
		return (ItemProperties[]) result.toArray(new ItemProperties[result.size()]);
	}

}
