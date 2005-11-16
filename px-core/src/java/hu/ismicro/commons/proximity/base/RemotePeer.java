package hu.ismicro.commons.proximity.base;

import java.net.MalformedURLException;

public interface RemotePeer extends Storage {

    void setRemoteUrl(String url) throws MalformedURLException;

}
