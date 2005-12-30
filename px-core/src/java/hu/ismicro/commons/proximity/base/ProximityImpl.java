package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.NoSuchRepositoryException;
import hu.ismicro.commons.proximity.Proximity;
import hu.ismicro.commons.proximity.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProximityImpl implements Proximity {

    protected Log logger = LogFactory.getLog(this.getClass());

    private Map repositories = new HashMap();

    public void setRepositories(List reposList) {
        logger.info("Received " + reposList.size() + " repositories in a List.");
        repositories.clear();
        for (Iterator i = reposList.iterator(); i.hasNext(); ) {
            Repository repo = (Repository) i.next();
            addRepository(repo);
        }
    }

    public List getRepositories() {
        List result = new ArrayList();
        for (Iterator i = repositories.keySet().iterator(); i.hasNext(); ) {
            String repoKey = (String) i.next();
            result.add(repositories.get(repoKey));
        }
        return result;
    }

    public void addRepository(Repository repository) {
        repositories.put(repository.getName(), repository);
        logger.info("Added repository " + repository.getName());
    }

    public void removeRepository(String repoName) {
        if (repositories.containsKey(repoName)) {
            repositories.remove(repoName);
            logger.info("Removed repository " + repoName);
        } else {
            throw new NoSuchRepositoryException(repoName);
        }
    }

    public Item retrieveItem(String path) {
        for (Iterator i = repositories.keySet().iterator(); i.hasNext();) {
            String repo = (String) i.next();
            try {
                Item item = retrieveItemFromRepository(path, repo);
                return item;
            } catch (ItemNotFoundException ex) {
                logger.info("Item " + path + " not found in repository " + repo);
            }
        }
        throw new ItemNotFoundException(path);
    }

    public Item retrieveItemFromRepository(String path, String repoName) {
        if (repositories.containsKey(repoName)) {
            Repository repo = (Repository) repositories.get(repoName);
            return repo.retrieveItem(path);
        }
        throw new NoSuchRepositoryException(repoName);
    }

    public List listItems(String path) {
        List response = new ArrayList();
        for (Iterator i = repositories.keySet().iterator(); i.hasNext();) {
            String repo = (String) i.next();
            try {
                response.addAll(listItemsFromRepository(path, repo));
            } catch (BrowsingNotAllowedException ex) {
                logger.info("Browsing of repository " + repo + " is forbidden!");
            }
        }
        return response;
    }

    public List listItemsFromRepository(String path, String repoName) {
        if (repositories.containsKey(repoName)) {
            Repository repo = (Repository) repositories.get(repoName);
            return repo.listItems(path);
        }
        throw new NoSuchRepositoryException(repoName);
    }

}
