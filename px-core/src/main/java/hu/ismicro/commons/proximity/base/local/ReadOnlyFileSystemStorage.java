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
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Stack;

/**
 * Read-only storage implemented on plain file system.
 * 
 * <p>
 * This implementation may be metadataAware, thus storing all metadata in the
 * configured workdir suffixed by "metadata" and actual artifacts in the
 * configured workdir suffixed by "storage" (these suffixes are customizable).
 * 
 * @author cstamas
 * 
 */
public class ReadOnlyFileSystemStorage extends AbstractLocalStorage {

    /**
     * The default METADATA prefix.
     */
    private static final String METADATA = "metadata";

    /**
     * The default STORAGE prefix.
     */
    private static final String STORAGE = "storage";

    /**
     * The configured baseDir.
     */
    private String baseDir;

    /**
     * Is this storage metadata aware? Default value is TRUE.
     */
    private boolean metadataAware = true;

    private String metadataPrefix = METADATA;

    private String storagePrefix = STORAGE;

    private File baseDirFile = null;

    private File metadataBaseDirFile = null;

    private File storageBaseDirFile = null;

    /**
     * Is this storage metadata aware?
     * 
     * @return
     */
    public boolean isMetadataAware() {
        return metadataAware;
    }

    public void setMetadataAware(boolean metadataAware) {
        this.metadataAware = metadataAware;
    }

    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    public void setMetadataPrefix(String metadataPrefix) {
        this.metadataPrefix = metadataPrefix;
    }

    public String getStoragePrefix() {
        return storagePrefix;
    }

    public void setStoragePrefix(String storagePrefix) {
        this.storagePrefix = storagePrefix;
    }

    /**
     * Sets the baseDir on the filesystem for this local FS storage. The
     * supplied String should be pointing to an existing directory.
     * 
     * @param baseDirPath
     */
    public void setBaseDir(String baseDirPath) {
        baseDirFile = new File(baseDirPath);
        if (!baseDirFile.exists()) {
            if (!baseDirFile.mkdirs()) {
                throw new IllegalArgumentException("The supplied directory parameter " + baseDirPath
                        + " does not exists and cannot be created!");
            } else {
                logger.info("Created basedir " + baseDirFile.getAbsolutePath());
            }
        }
        if (!baseDirFile.isDirectory()) {
            throw new IllegalArgumentException("The supplied parameter " + baseDirPath + " is not a directory!");
        }
        this.baseDir = baseDirFile.getAbsolutePath();
    }

    /**
     * It this storage is metadata aware, it returns baseDir appended by
     * metadata suffix, otherwise it will throw IllegalStateException because
     * the storage is metadata unaware.
     * 
     * @return
     */
    public File getMetadataBaseDir() {
        if (isMetadataAware()) {
            if (metadataBaseDirFile == null) {
                metadataBaseDirFile = new File(baseDir, getMetadataPrefix());
                if (!metadataBaseDirFile.exists()) {
                    metadataBaseDirFile.mkdirs();
                }
            }
            return metadataBaseDirFile;
        } else {
            throw new IllegalStateException("The storage is configured as metadata-unaware!");
        }
    }

    /**
     * If this storage is metadata aware, it returns baseDir appended by storage
     * suffix, otherwise it will return baseDir itself unmodified.
     * 
     * @return
     */
    public File getStorageBaseDir() {
        if (isMetadataAware()) {
            if (storageBaseDirFile == null) {
                storageBaseDirFile = new File(baseDir, getStoragePrefix());
                if (!storageBaseDirFile.exists()) {
                    storageBaseDirFile.mkdirs();
                }
            }
            return storageBaseDirFile;
        } else {
            return baseDirFile;
        }
    }

    public boolean containsItem(String path) {
        logger.debug("Checking for existence of " + path + " in " + getStorageBaseDir());
        return checkForExistence(getStorageBaseDir(), path);
    }

    public ProxiedItem retrieveItem(String path) throws ItemNotFoundException, StorageException {
        logger.debug("Retrieving " + path + " in " + getStorageBaseDir());
        try {
            File target = new File(getStorageBaseDir(), path);
            ProxiedItemProperties properties = constructItemProperties(target, path);
            ProxiedItem result = new ProxiedItem();
            result.setProperties(properties);
            if (result.getProperties().isFile()) {
                result.setStream(new FileInputStream(target));
            }
            return result;
        } catch (FileNotFoundException ex) {
            logger.error("FileNotFound in FS storage [" + getStorageBaseDir() + "] for path [" + path + "]", ex);
            throw new ItemNotFoundException("FileNotFound in FS storage [" + getStorageBaseDir() + "] for path ["
                    + path + "]");
        }
    }

    public List listItems(String path) {
        logger.debug("Listing " + path + " in " + getStorageBaseDir());
        List result = new ArrayList();
        File target = new File(getStorageBaseDir(), path);
        if (target.exists()) {
            if (target.isDirectory()) {
                File[] files = target.listFiles();
                for (int i = 0; i < files.length; i++) {
                    ProxiedItemProperties item = constructItemProperties(files[i], PathHelper.concatPaths(path,
                            files[i].getName()));
                    result.add(item);
                }
            } else {
                ProxiedItemProperties item = constructItemProperties(target, path);
                result.add(item);
            }
        }
        return result;
    }

    public void recreateMetadata() {
        int processed = 0;
        Stack stack = new Stack();
        List dir = listItems(PathHelper.PATH_SEPARATOR);
        stack.push(dir);
        while (!stack.isEmpty()) {
            dir = (List) stack.pop();
            for (Iterator i = dir.iterator(); i.hasNext();) {
                ItemProperties ip = (ItemProperties) i.next();
                if (ip.isDirectory()) {
                    List subdir = listItems(
                            PathHelper.walkThePath(ip.getAbsolutePath(), ip.getName()));
                    stack.push(subdir);
                } else {
                    storeItemProperties(ip);
                    processed++;
                }
            }
        }
        logger.info("Recreated metadata on " + processed + " items");
    }

    protected boolean checkForExistence(File baseDir, String path) {
        File target = new File(baseDir, path);
        return target.exists();
    }

    protected ProxiedItemProperties constructItemProperties(File target, String path) {
        ProxiedItemProperties result = new ProxiedItemProperties();
        result.setAbsolutePath(PathHelper.getDirName(path));
        result.setDirectory(target.isDirectory());
        result.setFile(target.isFile());
        result.setLastModified(new Date(target.lastModified()));
        result.setName(target.getName());
        if (target.isFile()) {
            if (isMetadataAware()) {
                // since basic props are also stored (eg. filesize)
                fillInMetadata(result);
            }
            // but we trust File and not stored filesize
            result.setSize(target.length());
        } else {
            result.setSize(0);
        }
        return result;
    }

    protected void fillInMetadata(ProxiedItemProperties iProps) {
        try {
            File target = new File(new File(getMetadataBaseDir(), iProps.getAbsolutePath()), iProps.getName());
            if (target.exists() && target.isFile()) {
                Properties metadata = new Properties();
                FileInputStream fis = new FileInputStream(target);
                metadata.load(fis);
                fis.close();
                for (Enumeration i = metadata.propertyNames(); i.hasMoreElements();) {
                    String key = (String) i.nextElement();
                    String value = metadata.getProperty(key);
                    iProps.setMetadata(key, value);
                }
            } else {
                logger.info("No metadata exists for [" + iProps.getName() + "] on path [" + iProps.getAbsolutePath()
                        + "]");
            }
        } catch (IOException ex) {
            logger.error("Got IOException during metadata retrieval.", ex);
        }
    }

    protected void storeItemProperties(ItemProperties iProps) throws StorageException {
        if (!iProps.isFile()) {
            throw new IllegalArgumentException("Only files can be stored!");
        }
        logger.debug("Storing metadata in [" + iProps.getAbsolutePath() + "] with name [" + iProps.getName() + "] in "
                + getMetadataBaseDir());
        try {
            File target = new File(new File(getMetadataBaseDir(), iProps.getAbsolutePath()), iProps.getName());
            target.getParentFile().mkdirs();

            Properties metadata = new Properties();
            metadata.putAll(iProps.getAllMetadata());
            FileOutputStream os = new FileOutputStream(target);
            metadata.store(os, null);
            os.flush();
            os.close();
            target.setLastModified(iProps.getLastModified().getTime());
        } catch (IOException ex) {
            logger.error("IOException in FS storage " + getMetadataBaseDir(), ex);
            throw new StorageException("IOException in FS storage " + getMetadataBaseDir(), ex);
        }
    }

}
