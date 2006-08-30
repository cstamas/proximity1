package org.abstracthorizon.proximity.ws;


import java.util.List;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.NoSuchRepositoryException;
import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.ProximityException;
import org.abstracthorizon.proximity.ProximityRequest;

public class SearchServiceImpl implements SearchService {
    
    private Proximity proximity;

    public Proximity getProximity() {
        return proximity;
    }

    public void setProximity(Proximity proximity) {
        this.proximity = proximity;
    }

    public ItemProperties[] listItems(ProximityRequest request) throws AccessDeniedException, NoSuchRepositoryException {
        List result = proximity.listItems(request);
        return (ItemProperties[]) result.toArray(new ItemProperties[result.size()]);
    }

    public String[] getSearchableKeywords() {
        List result = proximity.getSearchableKeywords();
        return (String[]) result.toArray(new String[result.size()]);
    }

    public ItemProperties[] searchItemByExample(ItemProperties example) throws ProximityException {
        List result = proximity.searchItem(example);
        return (ItemProperties[]) result.toArray(new ItemProperties[result.size()]);
    }

    public ItemProperties[] searchItemByQuery(String query) throws ProximityException {
        List result = proximity.searchItem(query);
        return (ItemProperties[]) result.toArray(new ItemProperties[result.size()]);
    }

}
