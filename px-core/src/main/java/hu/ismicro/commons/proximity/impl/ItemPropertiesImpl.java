package hu.ismicro.commons.proximity.impl;

import hu.ismicro.commons.proximity.ItemProperties;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ItemPropertiesImpl implements ItemProperties, Serializable {

    private static final long serialVersionUID = 727307616865507746L;

    private Map metadataMap;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");

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

    public String getPath() {
        if (getName().equals(PATH_ROOT)) {
            return getName();
        }
        if (getAbsolutePath().endsWith(PATH_SEPARATOR)) {
            return getAbsolutePath() + getName();
        } else {
            return getAbsolutePath() + PATH_SEPARATOR + getName();
        }
    }

    public boolean isDirectory() {
        return Boolean.valueOf(getMetadata(METADATA_IS_DIRECTORY)).booleanValue();
    }

    public void setDirectory(boolean directory) {
        setMetadata(METADATA_IS_DIRECTORY, Boolean.toString(directory));
    }

    public boolean isFile() {
        return Boolean.valueOf(getMetadata(METADATA_IS_FILE)).booleanValue();
    }

    public void setFile(boolean file) {
        setMetadata(METADATA_IS_FILE, Boolean.toString(file));
    }

    public boolean hasRemoteOrigin() {
        return getAllMetadata().containsKey(METADATA_ORIGINATING_URL);
    }

    public String getRemotePath() {
        return getMetadata(METADATA_ORIGINATING_URL);
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
        return Long.parseLong(getMetadata(METADATA_FILESIZE));
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
        hash = 31 * hash + (null == getAbsolutePath() ? 0 : getAbsolutePath().hashCode());
        hash = 31 * hash + (null == getName() ? 0 : getName().hashCode());
        return hash;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }
        ItemPropertiesImpl test = (ItemPropertiesImpl) obj;
        if (this.getAbsolutePath() == test.getAbsolutePath() && this.getName() == test.getName())
            return true;
        if ((this.getAbsolutePath() != null && this.getAbsolutePath().equals(test.getAbsolutePath()))
                && (this.getName() != null && this.getName().equals(test.getName())))
            return true;
        return false;

    }

}
