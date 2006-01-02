package hu.ismicro.commons.proximity;

/**
 * Thrown by proximity if the requested Repository does not exists.
 * 
 * @author cstamas
 *
 */
public class NoSuchRepositoryException extends ProximityException {

    public NoSuchRepositoryException(String repoId) {
        super("Repository with name " + repoId + " not found!");
    }

}
