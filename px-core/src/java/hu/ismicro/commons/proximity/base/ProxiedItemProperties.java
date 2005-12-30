package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProxiedItemProperties implements ItemProperties {

	private String absolutePath;

	private String name;

	private boolean directory;

	private boolean file;

	private long size;

	private Date lastModified;

	private Map metadataMap;

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public boolean isDirectory() {
		return directory;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	public boolean isFile() {
		return file;
	}

	public void setFile(boolean file) {
		this.file = file;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
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

}
