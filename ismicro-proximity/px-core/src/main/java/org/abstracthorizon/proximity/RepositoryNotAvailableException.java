package org.abstracthorizon.proximity;

public class RepositoryNotAvailableException extends ProximityException {

    private static final long serialVersionUID = 6414483658234772613L;

    public RepositoryNotAvailableException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RepositoryNotAvailableException(String msg) {
        super(msg);
    }

}
