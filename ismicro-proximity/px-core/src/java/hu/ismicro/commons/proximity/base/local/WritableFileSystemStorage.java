package hu.ismicro.commons.proximity.base.local;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.base.PathHelper;
import hu.ismicro.commons.proximity.base.StorageException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

public class WritableFileSystemStorage extends ReadOnlyFileSystemStorage {

	public boolean isWritable() {
		return true;
	}

	public void storeItem(Item item) throws StorageException {
		if (!item.getProperties().isFile()) {
			throw new IllegalArgumentException("Only files can be stored!");
		}
		logger.info("Storing item in [" + item.getProperties().getAbsolutePath() + "] with name ["
				+ item.getProperties().getName() + "] in " + getStorageBaseDir());
		try {
			File file = new File(PathHelper.walkThePath(getStorageBaseDir(), PathHelper.changePathLevel(item
					.getProperties().getAbsolutePath(), item.getProperties().getName())));
			file.getParentFile().mkdirs();
			FileOutputStream os = new FileOutputStream(file);
			IOUtils.copy(item.getStream(), os);
			item.getStream().close();
			os.flush();
			os.close();
			file.setLastModified(item.getProperties().getLastModified().getTime());
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
		logger.info("Storing metadata in [" + iProps.getAbsolutePath() + "] with name ["
				+ iProps.getName() + "] in " + getMetadataBaseDir());
		try {
			String itemPath = PathHelper.changePathLevel(iProps.getAbsolutePath(), iProps.getName());
			File target = new File(PathHelper.walkThePath(getMetadataBaseDir(), itemPath));
			target.getParentFile().mkdirs();

			Properties metadata = new Properties();
			metadata.putAll(iProps.getAllMetadata());
			FileOutputStream os = new FileOutputStream(target);
			metadata.store(os, null);
			os.flush();
			os.close();
		} catch (IOException ex) {
			logger.error("IOException in FS storage " + getMetadataBaseDir(), ex);
			throw new StorageException("IOException in FS storage " + getMetadataBaseDir(), ex);
		}
	}

	public void deleteItem(String path) throws StorageException {
		logger.info("Deleting " + path + " in " + getStorageBaseDir());
		File file = new File(getStorageBaseDir(), path);
		if (file.exists()) {
			if (!file.delete()) {
				throw new StorageException("Unable to delete file " + file.getPath());
			}
			if (isMetadataAware()) {
				deleteMetadata(path);
			}
		}
	}

	public void deleteMetadata(String path) throws StorageException {
		logger.info("Deleting " + path + " metadata in " + getMetadataBaseDir());
		File file = new File(getMetadataBaseDir(), path);
		if (file.exists()) {
			if (!file.delete()) {
				throw new StorageException("Unable to delete file " + file.getPath());
			}
		}
	}

}
