package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ProximityException;

public class StorageException extends ProximityException {
    
    public StorageException(String msg) {
        super(msg);
    }
    
    public StorageException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
