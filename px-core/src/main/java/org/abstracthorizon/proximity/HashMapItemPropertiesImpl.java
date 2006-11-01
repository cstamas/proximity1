package org.abstracthorizon.proximity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class HashMapItemPropertiesImpl implements ItemProperties {

	private static final long serialVersionUID = 727307616865507746L;

	private Map metadataMap;

	private DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");

	public String getDirectoryPath() {
		return getMetadata(METADATA_DIRECTORY_PATH);
	}

	public void setDirectoryPath(String absolutePath) {
		setMetadata(METADATA_DIRECTORY_PATH, absolutePath);
	}

	public String getName() {
		return getMetadata(METADATA_NAME);
	}

	public void setName(String name) {
		setMetadata(METADATA_NAME, name);
	}

	public String getPath() {
		if (getName().equals(PATH_ROOT)) {
			return getName();
		}
		if (getDirectoryPath().endsWith(PATH_SEPARATOR)) {
			return getDirectoryPath() + getName();
		} else {
			return getDirectoryPath() + PATH_SEPARATOR + getName();
		}
	}

	public boolean isDirectory() {
		if (getMetadataMap().containsKey(METADATA_IS_DIRECTORY)) {
			return Boolean.valueOf(getMetadata(METADATA_IS_DIRECTORY)).booleanValue();
		} else {
			return false;
		}
	}

	public void setDirectory(boolean directory) {
		setMetadata(METADATA_IS_DIRECTORY, Boolean.toString(directory));
	}

	public boolean isFile() {
		if (getMetadataMap().containsKey(METADATA_IS_FILE)) {
			return Boolean.valueOf(getMetadata(METADATA_IS_FILE)).booleanValue();
		} else {
			return false;
		}
	}

	public void setFile(boolean file) {
		setMetadata(METADATA_IS_FILE, Boolean.toString(file));
	}

	public boolean isCached() {
		if (getMetadataMap().containsKey(METADATA_IS_CACHED)) {
			return Boolean.valueOf(getMetadata(METADATA_IS_CACHED)).booleanValue();
		} else {
			return false;
		}
	}

	public void setCached(boolean cached) {
		setMetadata(METADATA_IS_CACHED, Boolean.toString(cached));
	}

	public String getRemoteUrl() {
		return getMetadata(METADATA_REMOTE_URL);
	}

	public void setRemoteUrl(String remoteUrl) {
		setMetadata(METADATA_REMOTE_URL, remoteUrl);
	}

	public Date getLastModified() {
		String lmstr = getMetadata(METADATA_LAST_MODIFIED);
		if (lmstr == null) {
			return null;
		} else {
			try {
				return dateFormat.parse(lmstr);
			} catch (ParseException ex) {
				return null;
			}
		}
	}

	public void setLastModified(Date lastModified) {
		if (lastModified != null) {
			setMetadata(METADATA_LAST_MODIFIED, dateFormat.format(lastModified));
		}
	}

	public Date getLastScanned() {
		String lmstr = getMetadata(METADATA_SCANNED);
		if (lmstr == null) {
			return null;
		} else {
			try {
				return dateFormat.parse(lmstr);
			} catch (ParseException ex) {
				return null;
			}
		}
	}

	public void setLastScanned(Date lastScanned) {
		if (lastScanned != null) {
			setMetadata(METADATA_SCANNED, dateFormat.format(lastScanned));
		}
	}

	public Date getLastScannedExt() {
		String lmstr = getMetadata(METADATA_SCANNED_EXT);
		if (lmstr == null) {
			return null;
		} else {
			try {
				return dateFormat.parse(lmstr);
			} catch (ParseException ex) {
				return null;
			}
		}
	}

	public void setLastScannedExt(Date lastScanned) {
		if (lastScanned != null) {
			setMetadata(METADATA_SCANNED_EXT, dateFormat.format(lastScanned));
		}
	}

	public long getSize() {
		if (getMetadataMap().containsKey(METADATA_FILESIZE)) {
			return Long.parseLong(getMetadata(METADATA_FILESIZE));
		} else {
			return 0;
		}
	}

	public void setSize(long size) {
		setMetadata(METADATA_FILESIZE, Long.toString(size));
	}

	public String getRepositoryId() {
		return getMetadata(METADATA_OWNING_REPOSITORY);
	}

	public void setRepositoryId(String id) {
		setMetadata(METADATA_OWNING_REPOSITORY, id);
	}

	public String getRepositoryGroupId() {
		return getMetadata(METADATA_OWNING_REPOSITORY_GROUP);
	}

	public void setRepositoryGroupId(String id) {
		setMetadata(METADATA_OWNING_REPOSITORY_GROUP, id);
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

	public void putAllMetadata(Map md) {
		getMetadataMap().putAll(md);
	}

	protected Map getMetadataMap() {
		if (metadataMap == null) {
			metadataMap = new HashMap();
		}
		return metadataMap;
	}

	public String toString() {
		return getName();
	}

	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (null == getDirectoryPath() ? 0 : getDirectoryPath().hashCode());
		hash = 31 * hash + (null == getName() ? 0 : getName().hashCode());
		return hash;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (obj.getClass().isAssignableFrom(ItemProperties.class))) {
			return false;
		}
		ItemProperties test = (ItemProperties) obj;
		if (this.getDirectoryPath() == test.getDirectoryPath() && this.getName() == test.getName())
			return true;
		if ((this.getDirectoryPath() != null && this.getDirectoryPath().equals(test.getDirectoryPath()))
				&& (this.getName() != null && this.getName().equals(test.getName())))
			return true;
		return false;

	}

	public String getExtension() {
		return getMetadata(METADATA_EXT);
	}

	public void setExtension(String ext) {
		setMetadata(METADATA_EXT, ext);
	}

	public String getHashMd5() {
		return getMetadata(METADATA_HASH_MD5);
	}

	public void setHashMd5(String md5hash) {
		setMetadata(METADATA_HASH_MD5, md5hash);
	}

	public byte[] getHashMd5AsBytes() {
		String md5hash = getHashMd5();
		if (md5hash != null) {
			return decodeHex(md5hash);
		} else {
			return null;
		}
	}

	public void setHashMd5AsBytes(byte[] md5hash) {
		if (md5hash != null) {
			setHashMd5(encodeHex(md5hash));
		}
	}

	public String getHashSha1() {
		return getMetadata(METADATA_HASH_SHA1);
	}

	public void setHashSha1(String sha1hash) {
		setMetadata(METADATA_HASH_SHA1, sha1hash);
	}

	public byte[] getHashSha1AsBytes() {
		String sha1hash = getHashSha1();
		if (sha1hash != null) {
			return decodeHex(sha1hash);
		} else {
			return null;
		}
	}

	public void setHashSha1AsBytes(byte[] sha1hash) {
		if (sha1hash != null) {
			setHashSha1(encodeHex(sha1hash));
		}
	}

	protected byte[] decodeHex(String hexEncoded) {
		try {
			return Hex.decodeHex(hexEncoded.toCharArray());
		} catch (DecoderException ex) {
			return null;
		}
	}

	protected String encodeHex(byte[] data) {
		return new String(Hex.encodeHex(data));
	}

}
