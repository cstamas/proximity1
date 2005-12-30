package hu.ismicro.commons.proximity.remote;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.base.PathHelper;
import hu.ismicro.commons.proximity.base.RemotePeer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractRemotePeer implements RemotePeer {

    protected Log logger = LogFactory.getLog(this.getClass());

    private String id;

    private URL remoteUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRemoteUrl() {
        return this.remoteUrl.toString();
    }

    public void setRemoteUrl(String url) throws MalformedURLException {
        this.remoteUrl = new URL(url);
    }

    public boolean isWritable() {
        return false;
    }

    public List listItems(String path) {
        throw new UnsupportedOperationException("The " + getClass().getName() + " RemotePeer is not listable!");
    }

    public void storeItem(Item item) {
        throw new UnsupportedOperationException("The " + getClass().getName() + " RemotePeer is ReadOnly!");
    }

    public void deleteItem(String path) {
        throw new UnsupportedOperationException("The " + getClass().getName() + " RemotePeer is ReadOnly!");
    }

    protected String getAbsoluteUrl(String path) {
        return PathHelper.walkThePath(getRemoteUrl(), path);
    }

}
