package hu.ismicro.commons.proximity.storage;

import hu.ismicro.commons.proximity.base.Storage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractStorage implements Storage {

    protected Log logger = LogFactory.getLog(this.getClass());
    
}
