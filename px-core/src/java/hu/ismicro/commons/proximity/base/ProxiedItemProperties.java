package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProxiedItemProperties implements ItemProperties {

	protected static final String METADATA_ABSOLUTE_PATH = "item.absolutePath";

	protected static final String METADATA_NAME = "item.name";

	protected static final String METADATA_IS_DIRECTORY = "item.isDirectory";

	protected static final String METADATA_IS_FILE = "item.isFile";

	protected static final String METADATA_FILESIZE = "item.filesize";

	protected static final String METADATA_LAST_MODIFIED = "item.lastModified";

	private Map metadataMap;

	public String getAbsolutePath() {
		return getMetadata(METADATA_ABSOLUTE_PATH);
	}

	public void setAbsolutePath(String absolutePath) {
		setMetadata(METADATA_ABSOLUTE_PATH, absolutePath);
	}

	public String getName() {
		return getMetadata(METADATA_NAME);
	}

	public void setName(String name) {
		setMetadata(METADATA_NAME, name);
	}

	public boolean isDirectory() {
		return Boolean.parseBoolean(getMetadata(METADATA_IS_DIRECTORY));
	}

	public void setDirectory(boolean directory) {
		setMetadata(METADATA_IS_DIRECTORY, Boolean.toString(directory));
	}

	public boolean isFile() {
		return Boolean.parseBoolean(getMetadata(METADATA_IS_FILE));
	}

	public void setFile(boolean file) {
		setMetadata(METADATA_IS_FILE, Boolean.toString(file));
	}

	public Date getLastModified() {
		return new Date(Long.parseLong(getMetadata(METADATA_LAST_MODIFIED)));
	}

	public void setLastModified(Date lastModified) {
		setMetadata(METADATA_LAST_MODIFIED, Long.toString(lastModified.getTime()));
	}

	public long getSize() {
		return Long.parseLong(getMetadata(METADATA_FILESIZE));
	}

	public void setSize(long size) {
		setMetadata(METADATA_FILESIZE, Long.toString(size));
	}

	public String getMetadata(String key) {
		return (String) getMetadataMap().get(key);
	}

	public void setMetadata(String key, String value) {
		getMetadataMap().put(key, value);
	}

	public Map getAllMetadata() {
		return getMetadataMap();
	}

	protected Map getMetadataMap() {
		if (metadataMap == null) {
			metadataMap = new HashMap();
		}
		return metadataMap;
	}

	public String toString() {
		return getName() + "[" + getSize() + "]";
	}

}
