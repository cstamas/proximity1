package hu.ismicro.commons.proximity.xfire;

import hu.ismicro.commons.proximity.AccessDeniedException;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.NoSuchRepositoryException;
import hu.ismicro.commons.proximity.Proximity;
import hu.ismicro.commons.proximity.ProximityException;
import hu.ismicro.commons.proximity.ProximityRequest;

import java.util.List;

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
