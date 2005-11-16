package hu.ismicro.commons.proximity.logic;

import hu.ismicro.commons.proximity.base.RepositoryRetrievalLogic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractRepositoryRetrievalLogic implements RepositoryRetrievalLogic {
    
    protected Log logger = LogFactory.getLog(this.getClass());

}
