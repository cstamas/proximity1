package org.abstracthorizon.proximity.storage.remote;

import java.net.MalformedURLException;
import java.net.URL;

import org.abstracthorizon.proximity.storage.Storage;

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
