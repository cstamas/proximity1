package hu.ismicro.commons.proximity.base.local;

import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.base.AbstractStorage;
import hu.ismicro.commons.proximity.base.PathHelper;
import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.ProxiedItemProperties;
import hu.ismicro.commons.proximity.base.StorageException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class ReadOnlyFileSystemStorage extends AbstractStorage {

	private static final String METADATA = "metadata";

	private static final String STORAGE = "storage";

	private String baseDir;

	private String metadataPrefix = METADATA;

	private String storagePrefix = STORAGE;

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

	public void setBaseDir(String baseDirPath) {
		File baseDirFile = new File(baseDirPath);
		if (!(baseDirFile.exists() && baseDirFile.isDirectory())) {
			throw new IllegalArgumentException(
					"The supplied parameter does not exists or is not a directory!");
		}
		this.baseDir = baseDirPath;
	}

	public String getMetadataBaseDir() {
		if (isMetadataAware()) {
			return PathHelper.changePathLevel(baseDir, getMetadataPrefix());
		} else {
			throw new IllegalStateException(
					"The storage is configured as metadata-unaware!");
		}
	}

	public String getStorageBaseDir() {
		if (isMetadataAware()) {
			return PathHelper.changePathLevel(baseDir, getStoragePrefix());
		} else {
			return baseDir;
		}
	}

	public boolean containsItem(String path) {
		logger.info("Checking for existence of " + path + " in "
				+ getStorageBaseDir());
		File target = new File(PathHelper
				.walkThePath(getStorageBaseDir(), path));
		return target.exists();
	}

	public ProxiedItemProperties retrieveItemProperties(String path)
			throws StorageException {
		logger.info("Retrieving " + path + " properties in "
				+ getStorageBaseDir());
		File target = new File(PathHelper
				.walkThePath(getStorageBaseDir(), path));
		return constructItemProperties(target, path);
	}

	public ProxiedItem retrieveItem(String path) throws ItemNotFoundException,
			StorageException {
		logger.info("Retrieving " + path + " in " + getStorageBaseDir());
		try {
			File target = new File(PathHelper.walkThePath(getStorageBaseDir(),
					path));
			ProxiedItemProperties properties = constructItemProperties(target,
					path);
			ProxiedItem result = new ProxiedItem();
			result.setProperties(properties);
			result.setStream(new FileInputStream(target));
			return result;
		} catch (FileNotFoundException ex) {
			logger.error("FileNotFound in FS storage [" + getStorageBaseDir()
					+ "] for path [" + path + "]", ex);
			throw new ItemNotFoundException("FileNotFound in FS storage ["
					+ getStorageBaseDir() + "] for path [" + path + "]");
		}
	}

	public List listItems(String path) {
		logger.info("Listing " + path + " in " + getStorageBaseDir());
		List result = new ArrayList();
		String targetPath = PathHelper.absolutizePathFromBase(
				getStorageBaseDir(), path);
		File target = new File(targetPath);
		if (target.exists()) {
			if (target.isDirectory()) {
				File[] files = target.listFiles();
				for (int i = 0; i < files.length; i++) {
					ProxiedItemProperties item = constructItemProperties(
							files[i], PathHelper.changePathLevel(path,
									PathHelper.getFileName(files[i].getPath())));
					result.add(item);
				}
			} else {
				ProxiedItemProperties item = constructItemProperties(target,
						targetPath);
				result.add(item);
			}
		}
		return result;
	}

	protected ProxiedItemProperties constructItemProperties(File target,
			String path) {
		ProxiedItemProperties result = new ProxiedItemProperties();
		result.setAbsolutePath(PathHelper.absolutizePathFromBase(
				PathHelper.PATH_SEPARATOR, PathHelper.changePathLevel(path,
						PathHelper.PATH_PARENT)));
		result.setDirectory(target.isDirectory());
		result.setFile(target.isFile());
		result.setLastModified(new Date(target.lastModified()));
		result.setName(PathHelper.getFileName(path));
		result.setSize(target.length());
		if (isMetadataAware()) {
			fillInMetadata(result);
		}
		return result;
	}

	protected void fillInMetadata(ProxiedItemProperties iProps) {
		try {
			String itemPath = PathHelper.walkThePath(iProps.getAbsolutePath(),
					iProps.getName());
			File target = new File(PathHelper.walkThePath(getMetadataBaseDir(),
					itemPath));
			if (target.exists()) {
				Properties metadata = new Properties();
				metadata.load(new FileInputStream(target));
				for (Enumeration i = metadata.propertyNames(); i
						.hasMoreElements();) {
					String key = (String) i.nextElement();
					String value = metadata.getProperty(key);
					iProps.setMetadata(key, value);
				}
			} else {
				logger.info("No metadata exists for " + itemPath);
			}
		} catch (IOException ex) {
			logger.error("Got IOException during metadata retrieval.", ex);
		}
	}

}
