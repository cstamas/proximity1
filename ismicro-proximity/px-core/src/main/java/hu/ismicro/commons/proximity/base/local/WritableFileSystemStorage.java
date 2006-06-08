package hu.ismicro.commons.proximity.base.local;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.base.StorageException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

/**
 * Writable local storage. It overrides the "unsupported" implementations from
 * the AbstractStorage.
 * 
 * @author cstamas
 * 
 */
public class WritableFileSystemStorage extends ReadOnlyFileSystemStorage {

    public boolean isWritable() {
        return true;
    }

    public void storeItem(Item item) throws StorageException {
        if (!item.getProperties().isFile()) {
            throw new IllegalArgumentException("Only files can be stored!");
        }
        logger.debug("Storing item in [" + item.getProperties().getAbsolutePath() + "] with name ["
                + item.getProperties().getName() + "] in " + getStorageBaseDir());
        try {
            if (item.getStream() != null) {
                File file = new File(new File(getStorageBaseDir(), item.getProperties().getAbsolutePath()), item
                        .getProperties().getName());
                file.getParentFile().mkdirs();
                FileOutputStream os = new FileOutputStream(file);
                IOUtils.copy(item.getStream(), os);
                item.getStream().close();
                os.flush();
                os.close();
                file.setLastModified(item.getProperties().getLastModified().getTime());
            }
            if (isMetadataAware()) {
                storeItemProperties(item.getProperties());
            }
        } catch (IOException ex) {
            logger.error("IOException in FS storage " + getStorageBaseDir(), ex);
            throw new StorageException("IOException in FS storage " + getStorageBaseDir(), ex);
        }
    }

    public void storeItemProperties(ItemProperties iProps) throws StorageException {
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

    public void deleteItem(String path) throws StorageException {
        logger.debug("Deleting " + path + " in " + getStorageBaseDir());
        File file = new File(getStorageBaseDir(), path);
        if (file.exists()) {
            if (!file.delete()) {
                throw new StorageException("Unable to delete file " + file.getPath());
            }
            if (isMetadataAware()) {
                deleteItemProperties(path);
            }
        }
    }

    public void deleteItemProperties(String path) throws StorageException {
        logger.debug("Deleting " + path + " metadata in " + getMetadataBaseDir());
        File file = new File(getMetadataBaseDir(), path);
        if (file.exists()) {
            if (!file.delete()) {
                throw new StorageException("Unable to delete file " + file.getPath());
            }
        }
    }

}
