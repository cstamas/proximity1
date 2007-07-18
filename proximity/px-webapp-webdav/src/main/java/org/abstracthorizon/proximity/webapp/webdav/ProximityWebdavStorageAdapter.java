package org.abstracthorizon.proximity.webapp.webdav;

import java.io.InputStream;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.webdav.WebdavStore;
import net.sf.webdav.exceptions.WebdavException;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.HashMapItemPropertiesImpl;
import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.ProximityException;
import org.abstracthorizon.proximity.ProximityRequest;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProximityWebdavStorageAdapter implements WebdavStore {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Proximity proximity;

    private HashMap createdButNotStoredResources = new HashMap();

    public Proximity getProximity() {
	return proximity;
    }

    public void setProximity(Proximity proximity) {
	this.proximity = proximity;
    }

    public void begin(Principal principal) throws WebdavException {
	// nothing
    }

    public void checkAuthentication() throws SecurityException {
	// nothing
    }

    public void commit() throws WebdavException {
	// nothing
    }

    public void rollback() throws WebdavException {
	// nothing
    }

    public void createFolder(String folderUri) throws WebdavException {
	// nothing
    }

    public void createResource(String resourceUri) throws WebdavException {
	createdButNotStoredResources.put(resourceUri, Boolean.TRUE);
	/*
         * try { ProximityRequest request = createRequest(resourceUri, false);
         * Item item = new Item(); ItemProperties itemProperties = new
         * HashMapItemPropertiesImpl();
         * itemProperties.setDirectoryPath(FilenameUtils.separatorsToUnix(FilenameUtils
         * .getFullPathNoEndSeparator(resourceUri)));
         * itemProperties.setName(FilenameUtils.getName(resourceUri));
         * itemProperties.setLastModified(new Date());
         * itemProperties.setDirectory(false); itemProperties.setFile(true);
         * item.setStream(new ByteArrayInputStream(new byte[0]));
         * item.setProperties(itemProperties); proximity.storeItem(request,
         * item); } catch (ProximityException ex) { logger.error("Proximity
         * throw exception.", ex); throw new WebdavException("Proximity throw " +
         * ex.getMessage()); }
         */
    }

    public String[] getChildrenNames(String folderUri) throws WebdavException {
	try {
	    if (objectExists(folderUri) && isFolder(folderUri)) {
		ProximityRequest request = new ProximityRequest(folderUri);
		List itemList = proximity.listItems(request);
		String[] result = new String[itemList.size()];
		for (int i = 0; i < itemList.size(); i++) {
		    result[i] = ((ItemProperties) itemList.get(i)).getName();
		}
		return result;
	    } else {
		return null;
	    }
	} catch (ProximityException ex) {
	    logger.error("Proximity thrown exception", ex);
	    throw new WebdavException("Proximity reported exception " + ex.getMessage());
	}
    }

    public Date getCreationDate(String uri) throws WebdavException {
	return getLastModified(uri);
    }

    public Date getLastModified(String uri) throws WebdavException {
	Item result = makeRequest(uri, true);
	if (result == null) {
	    return new Date();
	}
	if (result.getProperties().getLastModified() == null) {
	    return new Date();
	} else {
	    return result.getProperties().getLastModified();
	}
    }

    public InputStream getResourceContent(String resourceUri) throws WebdavException {
	Item result = makeRequest(resourceUri, true);
	if (result == null) {
	    throw new WebdavException("URI " + resourceUri + " not found");
	}
	return result.getStream();
    }

    public long getResourceLength(String resourceUri) throws WebdavException {
	Item result = makeRequest(resourceUri, true);
	if (result == null) {
	    throw new WebdavException("URI " + resourceUri + " not found");
	}
	return result.getProperties().getSize();
    }

    public boolean isFolder(String uri) throws WebdavException {
	Item result = makeRequest(uri, true);
	if (result == null) {
	    return false;
	}
	return result.getProperties().isDirectory();
    }

    public boolean isResource(String uri) throws WebdavException {
	Item result = makeRequest(uri, true);
	if (result == null) {
	    return false;
	}
	return result.getProperties().isFile();
    }

    public boolean objectExists(String uri) throws WebdavException {
	Item result = makeRequest(uri, true);
	return result != null;
    }

    public void removeObject(String uri) throws WebdavException {
	try {
	    ProximityRequest request = createRequest(uri, false);
	    proximity.deleteItem(request);
	} catch (ProximityException ex) {
	    logger.info("Proximity throw exception, IGNORING it.", ex);
	}
    }

    public void setResourceContent(String resourceUri, InputStream content, String contentType, String characterEncoding) throws WebdavException {
	try {
	    createdButNotStoredResources.remove(resourceUri);
	    ProximityRequest request = createRequest(resourceUri, false);
	    Item item = new Item();
	    ItemProperties itemProperties = new HashMapItemPropertiesImpl();
	    itemProperties.setDirectoryPath(FilenameUtils.separatorsToUnix(FilenameUtils.getFullPathNoEndSeparator(resourceUri)));
	    itemProperties.setName(FilenameUtils.getName(resourceUri));
	    itemProperties.setLastModified(new Date());
	    itemProperties.setDirectory(false);
	    itemProperties.setFile(true);
	    item.setProperties(itemProperties);
	    item.setStream(content);
	    proximity.storeItem(request, item);
	} catch (ProximityException ex) {
	    logger.error("Proximity throw exception.", ex);
	    throw new WebdavException("Proximity throw " + ex.getMessage());
	}
    }

    protected ProximityRequest createRequest(String uri, boolean propsOnly) {
	ProximityRequest req = new ProximityRequest(uri);
	req.setPropertiesOnly(propsOnly);
	return req;
    }

    protected Item makeRequest(String uri, boolean propsOnly) throws WebdavException {
	try {
	    ProximityRequest request = createRequest(uri, propsOnly);
	    Item item = proximity.retrieveItem(request);
	    return item;
	} catch (ItemNotFoundException ex) {
	    return null;
	} catch (AccessDeniedException ex) {
	    return null;
	} catch (ProximityException ex) {
	    logger.error("Proximity thrown exception", ex);
	    throw new WebdavException("Proximity reported exception " + ex.getMessage());
	}
    }

}
