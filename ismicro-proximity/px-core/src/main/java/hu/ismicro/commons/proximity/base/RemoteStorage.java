package hu.ismicro.commons.proximity.base;

import java.net.MalformedURLException;

/**
 * Remote storage.
 * 
 * @author cstamas
 * 
 */
public interface RemoteStorage extends Storage {

    void setRemoteUrl(String url) throws MalformedURLException;

}
