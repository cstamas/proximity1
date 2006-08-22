package hu.ismicro.commons.proximity.xfire;

import hu.ismicro.commons.proximity.AccessDeniedException;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.NoSuchRepositoryException;
import hu.ismicro.commons.proximity.ProximityException;
import hu.ismicro.commons.proximity.ProximityRequest;

public interface SearchService {

    /**
     * Returns an aggregated List of all item properties in all configured
     * Repositories. It will ALWAYS return List, at least a 0 length list. Will
     * not return null or throw exception in normal circumstances.
     * 
     * @param path
     * @return list of ItemProperties, possibly 0 length.
     */
    ItemProperties[] listItems(ProximityRequest request) throws AccessDeniedException, NoSuchRepositoryException;

    /**
     * Lists the searchable keywords as returned by Indexer.
     * 
     * @return the list of keywords usable in queries.
     */
    String[] getSearchableKeywords();

    /**
     * Searches for item.
     * 
     * @param example
     * @return List of ItemProperties, possibly 0 length.
     */
    ItemProperties[] searchItemByExample(ItemProperties example) throws ProximityException;

    /**
     * Searches for item.
     * 
     * @param query,
     *            dependent on indexer backend since Proximity just "passes" it
     *            out
     * @return List of ItemProperties, possibly 0 length.
     */
    ItemProperties[] searchItemByQuery(String query) throws ProximityException;

}
