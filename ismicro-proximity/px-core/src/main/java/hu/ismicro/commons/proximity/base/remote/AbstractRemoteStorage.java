package hu.ismicro.commons.proximity.base.remote;

import hu.ismicro.commons.proximity.base.AbstractStorage;
import hu.ismicro.commons.proximity.base.PathHelper;
import hu.ismicro.commons.proximity.base.RemoteStorage;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class is a base abstract class for remot storages.
 * 
 * @author cstamas
 *
 */
public abstract class AbstractRemoteStorage extends AbstractStorage implements RemoteStorage {

    private URL remoteUrl;

    public String getRemoteUrl() {
        return this.remoteUrl.toString();
    }
    
    public boolean isMetadataAware() {
    		return false;
    }

    public void setRemoteUrl(String url) throws MalformedURLException {
        this.remoteUrl = new URL(url);
    }

    protected String getAbsoluteUrl(String path) {
        return PathHelper.walkThePath(getRemoteUrl(), path);
    }
    
    protected URL getRemoteUrlAsUrl() {
        return remoteUrl;
    }

}
