package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ProximityException;

public class IndexerException extends ProximityException {

    private static final long serialVersionUID = -834393066001736350L;

    public IndexerException(String msg) {
        super(msg);
    }
    
    public IndexerException(String msg, Throwable thr) {
        super(msg, thr);
    }

}
