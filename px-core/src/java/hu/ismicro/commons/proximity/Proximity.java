package hu.ismicro.commons.proximity;

import java.util.List;

public interface Proximity {

    void setRepositories(List repositories);
    
    void addRepository(Repository repository);
    
    void removeRepository(String repoName);

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
     * Fetches a given item from the given repository.
     * 
     * @param path
     * @param repo
     * @return the wanted item
     * @throws ItemNotFoundException
     *             if Proximity has not found the item in the given repos
     */
    Item retrieveItemFromRepository(String path, String repo) throws ItemNotFoundException;

    /**
     * Returns an aggregated List of all items in all configured Repositories.
     * It will ALWAYS return List, at least a 0 length list. Will not return
     * null or throw exception in normal circumstances.
     * 
     * @param path
     * @return Always returns List, at least an empty list.
     */
    List listItems(String path);

    /**
     * Lists a given repository. If the wanted repository is forbidden for
     * listing, the exception will be thrown. In any other cases, at least a 0
     * length list is returned.
     * 
     * @param path
     * @param repo
     * @return
     * @throws BrowsingNotAllowedException
     *             if the repos is forbidden for listing.
     */
    List listItemsFromRepository(String path, String repo) throws BrowsingNotAllowedException;

}
