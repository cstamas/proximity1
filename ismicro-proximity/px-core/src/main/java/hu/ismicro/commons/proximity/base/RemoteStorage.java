package hu.ismicro.commons.proximity.base;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Remote storage.
 * 
 * @author cstamas
 * 
 */
public interface RemoteStorage extends Storage {

    URL getRemoteUrl();

    void setRemoteUrl(URL url) throws MalformedURLException;

    String getAbsoluteUrl(String path);

}
