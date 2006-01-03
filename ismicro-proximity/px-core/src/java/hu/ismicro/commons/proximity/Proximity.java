package hu.ismicro.commons.proximity;

import java.util.List;

public interface Proximity {

    /**
     * Returns the List of Repositories configured within Proximity.
     * 
     * @return List of active repositories.
     */
    List getRepositories();

    /**
     * Fetches a given item properties on the supplied path.
     * 
     * @param path
     * @return the wanted ItemProperties
     * @throws ItemNotFoundException
     *             if Proximity has not found item on the path
     */
    ItemProperties retrieveItemProperties(String path) throws ItemNotFoundException;

    /**
     * Fetches a given item on the supplied path.
     * 
     * @param path
     * @return the wanted Item
     * @throws ItemNotFoundException
     *             if Proximity has not found item on the path
     */
    Item retrieveItem(String path) throws ItemNotFoundException;

    /**
     * Fetches a given item properties from the given repository.
     * 
     * @param path
     * @param reposId
     * @return the wanted item properties
     * @throws ItemNotFoundException
     *             if Proximity has not found the item in the given repos.
     * @throws NoSuchRepositoryException
     */
    ItemProperties retrieveItemPropertiesFromRepository(String path, String reposId) throws NoSuchRepositoryException,
            ItemNotFoundException;

    /**
     * Fetches a given item from the given repository.
     * 
     * @param path
     * @param reposId
     * @return the wanted item
     * @throws ItemNotFoundException
     *             if Proximity has not found the item in the given repos.
     * @throws NoSuchRepositoryException
     */
    Item retrieveItemFromRepository(String path, String reposId) throws NoSuchRepositoryException, ItemNotFoundException;

    /**
     * Returns an aggregated List of all item properties in all configured
     * Repositories. It will ALWAYS return List, at least a 0 length list. Will
     * not return null or throw exception in normal circumstances.
     * 
     * @param path
     * @return list of ItemProperties, possibly 0 length.
     */
    List listItems(String path);

    /**
     * Lists a given repository. If the wanted repository is forbidden for
     * listing, the exception will be thrown. In any other cases, at least a 0
     * length list is returned.
     * 
     * @param path
     * @param reposId
     * @return list of ItemProperties, possibly 0 length.
     * @throws BrowsingNotAllowedException
     *             if the repos is forbidden for listing.
     * @throws NoSuchRepositoryException
     */
    List listItemsFromRepository(String path, String reposId) throws NoSuchRepositoryException;
    
    /**
     * Searches for item.
     * 
     * @param example
     * @return List of ItemProperties, possibly 0 lenth.
     */
    public List searchItem(ItemProperties example);

    /**
     * Searches for Item in a given repos.
     * 
     * @param reposId
     * @param example
     * @return List of ItemProperties, possibly 0 lenth.
     */
    public List searchItemFromRepository(String reposId, ItemProperties example) throws NoSuchRepositoryException;

}
