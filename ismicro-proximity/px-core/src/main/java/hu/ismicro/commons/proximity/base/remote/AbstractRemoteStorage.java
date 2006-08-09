package hu.ismicro.commons.proximity.base.remote;

import hu.ismicro.commons.proximity.base.PathHelper;
import hu.ismicro.commons.proximity.base.RemoteStorage;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a base abstract class for remot storages.
 * 
 * @author cstamas
 * 
 */
public abstract class AbstractRemoteStorage implements RemoteStorage {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private URL remoteUrl;

    public URL getRemoteUrl() {
        return this.remoteUrl;
    }

    public void setRemoteUrl(URL url) throws MalformedURLException {
        this.remoteUrl = url;
    }

    public String getAbsoluteUrl(String path) {
        return PathHelper.concatPaths(getRemoteUrlAsString(), path);
    }

    protected String getRemoteUrlAsString() {
        return remoteUrl.toString();
    }

}
