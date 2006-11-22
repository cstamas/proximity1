package org.abstracthorizon.proximity.storage.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.storage.StorageException;
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
		logger.debug("Storing item in [{}] in storage directory {}", item.getProperties().getPath(),
				getStorageBaseDir());
		try {
			if (item.getStream() != null) {
				File file = new File(getStorageBaseDir(), item.getProperties().getPath());
				file.getParentFile().mkdirs();
				FileOutputStream os = new FileOutputStream(file);
				IOUtils.copy(item.getStream(), os);
				item.getStream().close();
				os.flush();
				os.close();
				item.setStream(new FileInputStream(file));
				file.setLastModified(item.getProperties().getLastModified().getTime());
				if (isMetadataAware()) {
					getProxiedItemPropertiesFactory().expandItemProperties(item.getProperties().getPath(), file, false);
					storeItemProperties(item.getProperties());
				}
			}
		} catch (IOException ex) {
			throw new StorageException("IOException in FS storage " + getStorageBaseDir(), ex);
		}
	}

	public void deleteItem(String path) throws StorageException {
		logger.info("Deleting [{}] from storage directory {}", path, getStorageBaseDir());
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
		logger.debug("Deleting [{}] metadata from metadata directory {}", path, getMetadataBaseDir());
		File file = new File(getMetadataBaseDir(), path);
		if (file.exists() && !file.delete()) {
			throw new StorageException("Unable to delete file " + file.getPath());
		}
	}

}
