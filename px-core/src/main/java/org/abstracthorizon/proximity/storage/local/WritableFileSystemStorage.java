package org.abstracthorizon.proximity.storage.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.logic.DefaultExpiringProxyingRepositoryLogic;
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
		logger.debug("Storing item in [{}] in storage directory {}", item
				.getProperties().getPath(), getStorageBaseDir());
		try {
			if (item.getStream() != null) {
				File file = new File(getStorageBaseDir(), item.getProperties()
						.getPath());
				file.getParentFile().mkdirs();
				FileOutputStream os = new FileOutputStream(file);
				IOUtils.copy(item.getStream(), os);
				item.getStream().close();
				os.flush();
				os.close();
				item.setStream(new FileInputStream(file));
				file.setLastModified(item.getProperties().getLastModified()
						.getTime());
				if (isMetadataAware()) {
					item.getProperties().putAllMetadata(getProxiedItemPropertiesFactory().expandItemProperties(
							item.getProperties().getPath(), file, false).getAllMetadata());
					storeItemProperties(item.getProperties());
				}
			}
		} catch (IOException ex) {
			throw new StorageException("IOException in FS storage "
					+ getStorageBaseDir(), ex);
		}
	}

	public void deleteItem(String path) throws StorageException {
		logger.info("Deleting [{}] from storage directory {}", path,
				getStorageBaseDir());
		File file = new File(getStorageBaseDir(), path);
		if (file.exists()) {
			if (!file.delete()) {
				throw new StorageException("Unable to delete file "
						+ file.getPath());
			}
			if (isMetadataAware()) {
				deleteItemProperties(path);
			}
		}
	}

	public void deleteItemProperties(String path) throws StorageException {
		logger.debug("Deleting [{}] metadata from metadata directory {}", path,
				getMetadataBaseDir());
		File file = new File(getMetadataBaseDir(), path);
		if (file.exists() && !file.delete()) {
			throw new StorageException("Unable to delete file "
					+ file.getPath());
		}
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
					ItemProperties nip = getProxiedItemPropertiesFactory()
							.expandItemProperties(ip.getPath(), target, false);
					if (ip.getMetadata(DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES) != null) {
						logger.debug("We have an "
										+ DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES
										+ " property");
						nip.setMetadata(
							DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES,
							ip.getMetadata(DefaultExpiringProxyingRepositoryLogic.METADATA_EXPIRES));
					}
					logger.debug("Recreating metadata : adding " + extraProps
							+ " to " + nip.getAllMetadata());
					if (extraProps != null) {
						nip.getAllMetadata().putAll(extraProps);
					}
					storeItemProperties(nip);
					processed++;
				}
			}
		}
		logger.info("Recreated metadata on {} items.", Integer
				.toString(processed));
	}
}
