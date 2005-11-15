package hu.ismicro.commons.proximity.remote;

import hu.ismicro.commons.proximity.RemotePeer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractRemotePeer implements RemotePeer {
    
    protected Log logger = LogFactory.getLog(this.getClass());

}
