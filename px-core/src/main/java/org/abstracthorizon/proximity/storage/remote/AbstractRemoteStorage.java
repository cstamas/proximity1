package org.abstracthorizon.proximity.storage.remote;

import java.net.MalformedURLException;
import java.net.URL;

import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.storage.AbstractStorage;
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

	private String remoteUrlAsString;

	public URL getRemoteUrl() {
		return this.remoteUrl;
	}

	public void setRemoteUrl(URL url) throws MalformedURLException {
		this.remoteUrl = url;
		this.remoteUrlAsString = remoteUrl.toString();
		if (remoteUrlAsString.endsWith(ItemProperties.PATH_SEPARATOR)) {
			remoteUrlAsString = remoteUrlAsString.substring(0, remoteUrlAsString.length()
					- ItemProperties.PATH_SEPARATOR.length());
		}
	}

	public String getAbsoluteUrl(String path) {
		if (path.startsWith(ItemProperties.PATH_SEPARATOR)) {
			return remoteUrlAsString + path;
		} else {
			return remoteUrlAsString + ItemProperties.PATH_SEPARATOR + path;
		}
	}

}
