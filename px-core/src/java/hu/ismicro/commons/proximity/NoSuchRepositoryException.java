package hu.ismicro.commons.proximity;

public class NoSuchRepositoryException extends ProximityException {

    public NoSuchRepositoryException(String repoName) {
        super("Repository with name " + repoName + " not found!");
    }

}
