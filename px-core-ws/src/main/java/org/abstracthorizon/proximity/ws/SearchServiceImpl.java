package org.abstracthorizon.proximity.ws;

import java.util.List;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.NoSuchRepositoryException;
import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.ProximityException;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.indexer.Indexer;

public class SearchServiceImpl implements SearchService {

    private Proximity proximity;

    private Indexer indexer;

    public Proximity getProximity() {
        return proximity;
    }

    public void setProximity(Proximity proximity) {
        this.proximity = proximity;
    }

    public Indexer getIndexer() {
        return indexer;
    }

    public void setIndexer(Indexer indexer) {
        this.indexer = indexer;
    }

    public ItemProperties[] listItems(ProximityRequest request) throws AccessDeniedException, NoSuchRepositoryException {
        List result = proximity.listItems(request);
        return (ItemProperties[]) result.toArray(new ItemProperties[result.size()]);
    }

    public String[] getSearchableKeywords() {
        List result = indexer.getSearchableKeywords();
        return (String[]) result.toArray(new String[result.size()]);
    }

    public ItemProperties[] searchItemByExample(ItemProperties example) throws ProximityException {
        List result = indexer.searchByItemPropertiesExample(example);
        return (ItemProperties[]) result.toArray(new ItemProperties[result.size()]);
    }

    public ItemProperties[] searchItemByQuery(String query) throws ProximityException {
        List result = indexer.searchByQuery(query);
        return (ItemProperties[]) result.toArray(new ItemProperties[result.size()]);
    }

}
