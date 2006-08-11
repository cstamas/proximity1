package hu.ismicro.commons.proximity.base.local;

import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.base.PathHelper;
import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.ProxiedItemProperties;
import hu.ismicro.commons.proximity.base.StorageException;

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

import org.apache.commons.io.FileUtils;

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

    public ProxiedItem retrieveItem(String path) throws ItemNotFoundException, StorageException {
        logger.debug("Retrieving {} from storage directory {}", path, getStorageBaseDir());
        try {
            ProxiedItemProperties properties = loadItemProperties(path);
            ProxiedItem result = new ProxiedItem();
            result.setProperties(properties);
            File target = new File(getStorageBaseDir(), path);
            if (result.getProperties().isFile()) {
                result.setStream(new FileInputStream(target));
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
                    ProxiedItemProperties item = loadItemProperties(PathHelper.concatPaths(path,
                            files[i].getName()));
                    result.add(item);
                }
            } else {
                ProxiedItemProperties item = loadItemProperties(path);
                result.add(item);
            }
        }
        return result;
    }

    public void recreateMetadata(Map extraProps) throws StorageException {

        try {
            FileUtils.deleteDirectory(getMetadataBaseDir());
        } catch (IllegalArgumentException ex) {
            logger.debug("Could not delete the " + getMetadataBaseDir() + " directory, is this new instance?");
        } catch (IOException ex) {
            logger.warn("Could not delete the " + getMetadataBaseDir()
                    + " directory, is storage on Network volume? Ignoring error...", ex);
        }

        // the fact that we deleted all metadata will cause to be recreated by loadMD,
        // so we now just make a recursive listing?
        
        int processed = 0;
        Stack stack = new Stack();
        List dir = listItems(PathHelper.PATH_SEPARATOR);
        stack.push(dir);
        while (!stack.isEmpty()) {
            dir = (List) stack.pop();
            for (Iterator i = dir.iterator(); i.hasNext();) {
                ItemProperties ip = (ItemProperties) i.next();
                if (extraProps != null) {
                    ip.getAllMetadata().putAll(extraProps);
                }
                if (ip.isDirectory()) {
                    List subdir = listItems(PathHelper.walkThePath(ip.getAbsolutePath(), ip.getName()));
                    stack.push(subdir);
                } else {
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

    protected ProxiedItemProperties loadItemProperties(String path) {
        File target = new File(getStorageBaseDir(), path);
        File mdTarget = new File(getMetadataBaseDir(), path);
        ProxiedItemProperties ip = getProxiedItemPropertiesFactory().expandItemProperties(path, target, true);
        if (target.isFile() && isMetadataAware()) {
            try {
                if (mdTarget.exists() && mdTarget.isFile()) {
                    Properties metadata = new Properties();
                    FileInputStream fis = new FileInputStream(mdTarget);
                    metadata.load(fis);
                    fis.close();
                    ip.getAllMetadata().putAll(metadata);
                } else {
                    logger.debug("No metadata exists for [{}] on path [{}] -- RECREATING", ip.getName(), ip
                            .getAbsolutePath());
                    ip = getProxiedItemPropertiesFactory().expandItemProperties(path, target, false);
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
