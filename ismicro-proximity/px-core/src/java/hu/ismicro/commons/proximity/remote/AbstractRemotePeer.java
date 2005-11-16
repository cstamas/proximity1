package hu.ismicro.commons.proximity.remote;

import java.net.MalformedURLException;
import java.net.URL;

import hu.ismicro.commons.proximity.RemotePeer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractRemotePeer implements RemotePeer {

    protected Log logger = LogFactory.getLog(this.getClass());

    private URL remoteUrl;

    public String getRemoteUrl() {
        return this.remoteUrl.toString();
    }

    public void setRemoteUrl(String url) throws MalformedURLException {
        if (!url.endsWith("/")) {
            throw new IllegalArgumentException("The URL is not ending with '/' (slash)!");
        }
        this.remoteUrl = new URL(url.substring(0,url.length()-1));
    }

}
