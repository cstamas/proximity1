package org.abstracthorizon.proximity.metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.abstracthorizon.proximity.HashMapItemPropertiesImpl;
import org.abstracthorizon.proximity.ItemProperties;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractProxiedItemPropertiesFactory that gathers Proximity standard item
 * properties.
 * 
 * @author cstamas
 * 
 */
public abstract class AbstractProxiedItemPropertiesFactory implements ProxiedItemPropertiesFactory {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public final List getSearchableKeywords() {
		List result = getDefaultSearchableKeywords();
		getCustomSearchableKeywords(result);
		return result;
	}

	public final ItemProperties expandItemProperties(String path, File file, boolean defaultOnly) {
		ItemProperties ip = new HashMapItemPropertiesImpl();
		expandDefaultItemProperties(path, ip, file);
		if (!defaultOnly) {
			expandItemHashProperties(path, ip, file);
			expandCustomItemProperties(ip, file);
			ip.setLastScannedExt(new Date());
		}
		return ip;
	}

	protected final List getDefaultSearchableKeywords() {
		List result = new ArrayList();
		// set the default ItemProperties
		result.add(ItemProperties.METADATA_DIRECTORY_PATH);
		result.add(ItemProperties.METADATA_NAME);
		result.add(ItemProperties.METADATA_FILESIZE);
		result.add(ItemProperties.METADATA_LAST_MODIFIED);
		result.add(ItemProperties.METADATA_IS_DIRECTORY);
		result.add(ItemProperties.METADATA_IS_FILE);
		result.add(ItemProperties.METADATA_IS_CACHED);
		result.add(ItemProperties.METADATA_EXT);
		result.add(ItemProperties.METADATA_HASH_MD5);
		result.add(ItemProperties.METADATA_HASH_SHA1);
		result.add(ItemProperties.METADATA_OWNING_REPOSITORY);
		result.add(ItemProperties.METADATA_OWNING_REPOSITORY_GROUP);
		result.add(ItemProperties.METADATA_REMOTE_URL);
		return result;
	}

	protected final void expandDefaultItemProperties(String path, ItemProperties ip, File file) {
		ip.setDirectoryPath(FilenameUtils.separatorsToUnix(FilenameUtils.getFullPathNoEndSeparator(path)));
		if ("".equals(ip.getDirectoryPath())) {
			ip.setDirectoryPath(ItemProperties.PATH_ROOT);
		}
		ip.setName(FilenameUtils.getName(path));
		ip.setDirectory(file.isDirectory());
		ip.setFile(file.isFile());
		ip.setLastModified(new Date(file.lastModified()));
		if (file.isFile()) {
			String ext = FilenameUtils.getExtension(path);
			if (ext != null) {
				ip.setExtension(ext);
			}
			ip.setSize(file.length());
		} else {
			ip.setSize(0);
		}
		ip.setLastScanned(new Date());
	}

	protected final void expandItemHashProperties(String path, ItemProperties ip, File file) {
		if (file.isFile()) {
			String digest = FileDigest.getFileDigestAsString(file, "md5");
			if (digest != null) {
				ip.setHashMd5(digest);
			}
			digest = FileDigest.getFileDigestAsString(file, "sha1");
			if (digest != null) {
				ip.setHashSha1(digest);
			}
		}
	}

	protected abstract void getCustomSearchableKeywords(List defaults);

	protected abstract void expandCustomItemProperties(ItemProperties ip, File file);

}
