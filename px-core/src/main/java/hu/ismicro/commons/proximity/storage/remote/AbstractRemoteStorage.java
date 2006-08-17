package hu.ismicro.commons.proximity.storage.remote;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.storage.AbstractStorage;

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
public abstract class AbstractRemoteStorage extends AbstractStorage implements RemoteStorage {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private URL remoteUrl;

    public URL getRemoteUrl() {
        return this.remoteUrl;
    }

    public void setRemoteUrl(URL url) throws MalformedURLException {
        this.remoteUrl = url;
    }

    public String getAbsoluteUrl(String path) {
        String urlstr = getRemoteUrlAsString();
        if (urlstr.endsWith(ItemProperties.PATH_SEPARATOR)) {
            return urlstr + path;
        } else {
            return urlstr + ItemProperties.PATH_SEPARATOR + path;
        }
    }

    protected String getRemoteUrlAsString() {
        return remoteUrl.toString();
    }

}
