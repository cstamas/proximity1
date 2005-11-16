package hu.ismicro.commons.proximity;

/**
 * Thrown by repository if browsing of it is not allowed.
 * 
 * @author cstamas
 *
 */
public class BrowsingNotAllowedException extends ProximityException {
    
    public BrowsingNotAllowedException(String repositoryName) {
        super("Browsing of repository " + repositoryName + " is forbidden!");
    }

}
