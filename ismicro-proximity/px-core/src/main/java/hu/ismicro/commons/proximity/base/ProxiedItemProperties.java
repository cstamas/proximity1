package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProxiedItemProperties implements ItemProperties {

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

    public String getPath() {
        if (getName().equals("/")) {
            return getName();
        }
        if (getAbsolutePath().endsWith("/")) {
            return getAbsolutePath() + getName();
        } else {
            return getAbsolutePath() + "/" + getName();
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

    public String getRepositoryId() {
        return getMetadata(METADATA_OWNING_REPOSITORY);
    }

    public void setRepositoryId(String id) {
        setMetadata(METADATA_OWNING_REPOSITORY, id);
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
        ProxiedItemProperties test = (ProxiedItemProperties) obj;
        if (this.getAbsolutePath() == test.getAbsolutePath() && this.getName() == test.getName())
            return true;
        if ((this.getAbsolutePath() != null && this.getAbsolutePath().equals(test.getAbsolutePath()))
                && (this.getName() != null && this.getName().equals(test.getName())))
            return true;
        return false;

    }

}
