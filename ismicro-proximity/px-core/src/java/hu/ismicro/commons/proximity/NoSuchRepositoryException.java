package hu.ismicro.commons.proximity;

public class NoSuchRepositoryException extends ProximityException {

    public NoSuchRepositoryException(String repoId) {
        super("Repository with name " + repoId + " not found!");
    }

}
