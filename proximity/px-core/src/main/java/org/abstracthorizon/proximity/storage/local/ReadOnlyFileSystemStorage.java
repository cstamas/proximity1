package org.abstracthorizon.proximity.storage.local;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;

import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.impl.ItemImpl;
import org.abstracthorizon.proximity.impl.ItemPropertiesImpl;
import org.abstracthorizon.proximity.storage.StorageException;
import org.apache.commons.io.FilenameUtils;

/**
 * Read-only storage implemented on plain file system.
 * 
 * <p>
 * This implementation may be metadataAware, thus storing all metadata in the
 * configured metadata dir actual artifacts in the configured storage dir.
 * 
 * @author cstamas
 * 
 */
public class ReadOnlyFileSystemStorage extends AbstractLocalStorage {

    private boolean metadataAware = true;

    private File storageDirFile = null;

    private File metadataDirFile = null;

    public File getMetadataDirFile() {
        return metadataDirFile;
    }

    public void setMetadataDirFile(File metadataDirFile) {
        if (!metadataDirFile.exists()) {
            if (!metadataDirFile.mkdirs()) {
                throw new IllegalArgumentException("Cannot create directories " + metadataDirFile.getAbsolutePath());
            }
        }
        if (metadataDirFile.isDirectory()) {
            this.metadataDirFile = metadataDirFile;
        } else {
            throw new IllegalArgumentException("The " + metadataDirFile.getAbsolutePath() + " is not a directory!");
        }
    }

    public File getStorageDirFile() {
        return storageDirFile;
    }

    public void setStorageDirFile(File storageDirFile) {
        if (!storageDirFile.exists()) {
            if (!storageDirFile.mkdirs()) {
                throw new IllegalArgumentException("Cannot create directories " + storageDirFile.getAbsolutePath());
            }
        }
        if (storageDirFile.isDirectory()) {
            this.storageDirFile = storageDirFile;
        } else {
            throw new IllegalArgumentException("The " + storageDirFile.getAbsolutePath() + " is not a directory!");
        }
    }

    public File getMetadataBaseDir() {
        if (isMetadataAware()) {
            return metadataDirFile;
        } else {
            throw new IllegalStateException("The storage is configured as metadata-unaware!");
        }
    }

    public File getStorageBaseDir() {
        return storageDirFile;
    }

    // ===================================================================================================
    // Local Storage iface

    public boolean isMetadataAware() {
        return metadataAware;
    }

    public void setMetadataAware(boolean metadataAware) {
        this.metadataAware = metadataAware;
    }

    public boolean containsItem(String path) {
        logger.debug("Checking for existence of {} in {}", path, getStorageBaseDir());
        return checkForExistence(getStorageBaseDir(), path);
    }

    public ItemImpl retrieveItem(String path, boolean propsOnly) throws ItemNotFoundException, StorageException {
        logger.debug("Retrieving {} from storage directory {}", path, getStorageBaseDir());
        try {
            ItemPropertiesImpl properties = loadItemProperties(path);
            ItemImpl result = new ItemImpl();
            result.setProperties(properties);
            if (!propsOnly) {
                File target = new File(getStorageBaseDir(), path);
                if (target.isFile()) {
                    result.setStream(new FileInputStream(target));
                    properties.setSize(target.length());
                }
            }
            return result;
        } catch (FileNotFoundException ex) {
            throw new ItemNotFoundException("FileNotFound in FS storage [" + getStorageBaseDir() + "] for path ["
                    + path + "]");
        }
    }

    public List listItems(String path) {
        logger.debug("Listing {} in storage directory {}", path, getStorageBaseDir());
        List result = new ArrayList();
        File target = new File(getStorageBaseDir(), path);
        if (target.exists()) {
            if (target.isDirectory()) {
                File[] files = target.listFiles();
                for (int i = 0; i < files.length; i++) {
                    ItemPropertiesImpl item = loadItemProperties(FilenameUtils.concat(path, files[i].getName()));
                    result.add(item);
                }
            } else {
                ItemPropertiesImpl item = loadItemProperties(path);
                result.add(item);
            }
        }
        return result;
    }

    public void recreateMetadata(Map extraProps) throws StorageException {

        // issue #44, we will not delete existing metadata,
        // instead, we will force to "recreate" those the properties factory
        // eventually appending it with new ones.

        int processed = 0;
        Stack stack = new Stack();
        List dir = listItems(ItemProperties.PATH_ROOT);
        stack.push(dir);
        while (!stack.isEmpty()) {
            dir = (List) stack.pop();
            for (Iterator i = dir.iterator(); i.hasNext();) {
                ItemProperties ip = (ItemProperties) i.next();

                if (ip.isDirectory()) {
                    List subdir = listItems(ip.getPath());
                    stack.push(subdir);
                } else {
                    logger.debug("**** {}", ip.getPath());
                    File target = new File(getStorageBaseDir(), ip.getPath());
                    ItemProperties nip = getProxiedItemPropertiesFactory().expandItemProperties(ip.getPath(), target, false);
                    if (extraProps != null) {
                        nip.getAllMetadata().putAll(extraProps);
                    }
                    storeItemProperties(nip);
                    processed++;
                }
            }
        }
        logger.info("Recreated metadata on {} items.", Integer.toString(processed));
    }

    protected boolean checkForExistence(File baseDir, String path) {
        File target = new File(baseDir, path);
        return target.exists();
    }

    protected ItemPropertiesImpl loadItemProperties(String path) {
        File target = new File(getStorageBaseDir(), path);
        File mdTarget = new File(getMetadataBaseDir(), path);
        ItemPropertiesImpl ip = getProxiedItemPropertiesFactory().expandItemProperties(path, target, true);
        if (target.isFile() && isMetadataAware()) {
            try {
                if (mdTarget.exists() && mdTarget.isFile()) {
                    Properties metadata = new Properties();
                    FileInputStream fis = new FileInputStream(mdTarget);
                    metadata.load(fis);
                    fis.close();
                    ip.getAllMetadata().putAll(metadata);
                } else {
                    logger.info("No metadata exists for [{}] on path [{}] -- recreating the default ones. Reindex operation may be needed to recreate/reindex them completely.", ip.getName(), ip
                            .getDirectory());
                    ip = getProxiedItemPropertiesFactory().expandItemProperties(path, target, true);
                    storeItemProperties(ip);
                }
            } catch (IOException ex) {
                logger.error("Got IOException during metadata retrieval.", ex);
            }
        }
        return ip;
    }

    protected void storeItemProperties(ItemProperties iProps) throws StorageException {
        if (!iProps.isFile()) {
            throw new IllegalArgumentException("Only files can be stored!");
        }
        logger.debug("Storing metadata in [{}] in storage directory {}", iProps.getPath(), getMetadataBaseDir());
        try {

            File target = new File(getMetadataBaseDir(), iProps.getPath());
            target.getParentFile().mkdirs();
            Properties metadata = new Properties();
            metadata.putAll(iProps.getAllMetadata());
            FileOutputStream os = new FileOutputStream(target);
            metadata.store(os, "Written by " + this.getClass());
            os.flush();
            os.close();
            target.setLastModified(iProps.getLastModified().getTime());

        } catch (IOException ex) {
            throw new StorageException("IOException in FS storage " + getMetadataBaseDir(), ex);
        }
    }

}
