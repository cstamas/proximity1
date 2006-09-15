package org.abstracthorizon.proximity.webapp.webdav;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import net.sf.webdav.IWebdavStorage;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.HashMapItemPropertiesImpl;
import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.ProximityException;
import org.abstracthorizon.proximity.ProximityRequest;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProximityWebdavStorageAdapter implements IWebdavStorage {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Proximity proximity;

    public Proximity getProximity() {
        return proximity;
    }

    public void setProximity(Proximity proximity) {
        this.proximity = proximity;
    }

    public void begin(Principal principal, Hashtable parameters) throws Exception {
        // nothing
    }

    public void checkAuthentication() throws SecurityException {
        // nothing
    }

    public void commit() throws IOException {
        // nothing
    }

    public void rollback() throws IOException {
        // nothing
    }

    public void createFolder(String folderUri) throws IOException {
        // nothing
    }

    public void createResource(String resourceUri) throws IOException {
        // we have to store something.... it will be zero byte
        try {
            ProximityRequest request = createRequest(resourceUri, false);
            Item item = new Item();
            ItemProperties itemProperties = new HashMapItemPropertiesImpl();
            itemProperties.setDirectoryPath(FilenameUtils.getFullPathNoEndSeparator(resourceUri));
            itemProperties.setName(FilenameUtils.getName(resourceUri));
            itemProperties.setLastModified(new Date());
            item.setStream(new ByteArrayInputStream(new byte[0]));
            item.setProperties(itemProperties);
            proximity.storeItem(request, item);
        } catch (ProximityException ex) {
            logger.error("Proximity throw exception.", ex);
            throw new IOException("Proximity throw " + ex.getMessage());
        }
    }

    public String[] getChildrenNames(String folderUri) throws IOException {
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
            throw new IOException("Proximity reported exception " + ex.getMessage());
        }
    }

    public Date getCreationDate(String uri) throws IOException {
        return getLastModified(uri);
    }

    public Date getLastModified(String uri) throws IOException {
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

    public InputStream getResourceContent(String resourceUri) throws IOException {
        Item result = makeRequest(resourceUri, true);
        if (result == null) {
            throw new IOException("URI " + resourceUri + " not found");
        }
        return result.getStream();
    }

    public long getResourceLength(String resourceUri) throws IOException {
        Item result = makeRequest(resourceUri, true);
        if (result == null) {
            throw new IOException("URI " + resourceUri + " not found");
        }
        return result.getProperties().getSize();
    }

    public boolean isFolder(String uri) throws IOException {
        Item result = makeRequest(uri, true);
        if (result == null) {
            return false;
        }
        return result.getProperties().isDirectory();
    }

    public boolean isResource(String uri) throws IOException {
        Item result = makeRequest(uri, true);
        if (result == null) {
            return false;
        }
        return result.getProperties().isFile();
    }

    public boolean objectExists(String uri) throws IOException {
        Item result = makeRequest(uri, true);
        return result != null;
    }

    public void removeObject(String uri) throws IOException {
        try {
            ProximityRequest request = createRequest(uri, false);
            proximity.deleteItem(request);
        } catch (ProximityException ex) {
            logger.info("Proximity throw exception, IGNORING it.", ex);
        }
    }

    public void setResourceContent(String resourceUri, InputStream content, String contentType, String characterEncoding)
            throws IOException {
        try {
            ProximityRequest request = createRequest(resourceUri, false);
            Item item = new Item();
            ItemProperties itemProperties = new HashMapItemPropertiesImpl();
            itemProperties.setDirectoryPath(FilenameUtils.getFullPathNoEndSeparator(resourceUri));
            itemProperties.setName(FilenameUtils.getName(resourceUri));
            itemProperties.setLastModified(new Date());
            item.setStream(content);
            item.setProperties(itemProperties);
            proximity.storeItem(request, item);
        } catch (ProximityException ex) {
            logger.error("Proximity throw exception.", ex);
            throw new IOException("Proximity throw " + ex.getMessage());
        }
    }

    protected ProximityRequest createRequest(String uri, boolean propsOnly) {
        ProximityRequest req = new ProximityRequest(uri);
        req.setPropertiesOnly(propsOnly);
        return req;
    }

    protected Item makeRequest(String uri, boolean propsOnly) throws IOException {
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
            throw new IOException("Proximity reported exception " + ex.getMessage());
        }
    }

}
