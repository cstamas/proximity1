package hu.ismicro.commons.proximity.base.remote;

import hu.ismicro.commons.proximity.base.PathHelper;
import hu.ismicro.commons.proximity.base.RemoteStorage;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is a base abstract class for remot storages.
 * 
 * @author cstamas
 * 
 */
public abstract class AbstractRemoteStorage implements RemoteStorage {

    protected Log logger = LogFactory.getLog(this.getClass());

    private URL remoteUrl;

    public URL getRemoteUrl() {
        return this.remoteUrl;
    }

    public void setRemoteUrl(URL url) throws MalformedURLException {
        this.remoteUrl = url;
    }

    protected String getAbsoluteUrl(String path) {
        return PathHelper.concatPaths(getRemoteUrlAsString(), path);
    }

    protected String getRemoteUrlAsString() {
        return remoteUrl.toString();
    }

}
