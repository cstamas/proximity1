package hu.ismicro.commons.proximity.xfire;

import hu.ismicro.commons.proximity.AccessDeniedException;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.NoSuchRepositoryException;
import hu.ismicro.commons.proximity.Proximity;
import hu.ismicro.commons.proximity.ProximityException;
import hu.ismicro.commons.proximity.ProximityRequest;

public class SearchServiceImpl implements SearchService {
    
    private Proximity proximity;

    public Proximity getProximity() {
        return proximity;
    }

    public void setProximity(Proximity proximity) {
        this.proximity = proximity;
    }

    public ItemProperties[] listItems(ProximityRequest request) throws AccessDeniedException, NoSuchRepositoryException {
        return (ItemProperties[]) proximity.listItems(request).toArray();
    }

    public ItemProperties[] getSearchableKeywords() {
        return (ItemProperties[]) proximity.getSearchableKeywords().toArray();
    }

    public ItemProperties[] searchItem(ItemProperties example) throws ProximityException {
        return (ItemProperties[]) proximity.searchItem(example).toArray();
    }

    public ItemProperties[] searchItem(String query) throws ProximityException {
        return (ItemProperties[]) proximity.searchItem(query).toArray();
    }

}
