package hu.ismicro.commons.proximity;

public class BrowsingNotAllowedException extends ProximityException {
    
    public BrowsingNotAllowedException(String repositoryName) {
        super("Browsing of repository " + repositoryName + " is forbidden!");
    }

}
