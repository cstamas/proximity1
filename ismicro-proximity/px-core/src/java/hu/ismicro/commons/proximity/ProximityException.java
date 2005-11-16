package hu.ismicro.commons.proximity;

public abstract class ProximityException extends RuntimeException {
    
    public ProximityException(String msg) {
        super(msg);
    }
    
    public ProximityException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
