package hu.ismicro.commons.proximity.storage;

/**
 * Generic storage exception thrown by given storage implementation (like
 * IOExceptions), and so. Denotes an unrecoverable system error.
 * 
 * @author cstamas
 * 
 */
public class StorageException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -7119754988039787918L;

    public StorageException(String msg) {
        super(msg);
    }

    public StorageException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
