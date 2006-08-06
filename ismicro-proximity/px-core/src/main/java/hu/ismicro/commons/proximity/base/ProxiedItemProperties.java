package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProxiedItemProperties implements ItemProperties {

    private Map metadataMap;
    
    private List indexableMetadataKeys;

    public String getAbsolutePath() {
        return getMetadata(METADATA_ABSOLUTE_PATH);
    }

    public void setAbsolutePath(String absolutePath) {
        setMetadata(METADATA_ABSOLUTE_PATH, absolutePath, true);
    }

    public String getName() {
        return getMetadata(METADATA_NAME);
    }

    public void setName(String name) {
        setMetadata(METADATA_NAME, name, true);
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
        setMetadata(METADATA_IS_DIRECTORY, Boolean.toString(directory), true);
    }

    public boolean isFile() {
        return Boolean.valueOf(getMetadata(METADATA_IS_FILE)).booleanValue();
    }

    public void setFile(boolean file) {
        setMetadata(METADATA_IS_FILE, Boolean.toString(file), true);
    }

    public boolean hasRemoteOrigin() {
        return getAllMetadata().containsKey(METADATA_ORIGINATING_URL);
    }

    public String getRemotePath() {
        return getMetadata(METADATA_ORIGINATING_URL);
    }

    public Date getLastModified() {
        return new Date(Long.parseLong(getMetadata(METADATA_LAST_MODIFIED)));
    }

    public void setLastModified(Date lastModified) {
        setMetadata(METADATA_LAST_MODIFIED, Long.toString(lastModified.getTime()), true);
    }

    public long getSize() {
        return Long.parseLong(getMetadata(METADATA_FILESIZE));
    }

    public void setSize(long size) {
        setMetadata(METADATA_FILESIZE, Long.toString(size), true);
    }

    public String getRepositoryId() {
        return getMetadata(METADATA_OWNING_REPOSITORY);
    }

    public void setRepositoryId(String id) {
        setMetadata(METADATA_OWNING_REPOSITORY, id, true);
    }

    public String getRepositoryGroupId() {
        return getMetadata(METADATA_OWNING_REPOSITORY_GROUP);
    }

    public void setRepositoryGroupId(String id) {
        setMetadata(METADATA_OWNING_REPOSITORY_GROUP, id, true);
    }

    public String getMetadata(String key) {
        return (String) getMetadataMap().get(key);
    }

    public void setMetadata(String key, String value, boolean indexable) {
        if (indexable) {
            getIndexableMetadataKeys().add(key);
        }
        getMetadataMap().put(key, value);
    }

    public void setMetadata(String key, String value) {
        setMetadata(key, value, false);
    }

    public Map getAllMetadata() {
        return getMetadataMap();
    }

    public Map getIndexableMetadata() {
        HashMap result = new HashMap();
        for (Iterator i = getIndexableMetadataKeys().iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            result.put(key, getMetadata(key));
        }
        return result;
    }

    public Map getNonIndexableMetadata() {
        HashMap result = new HashMap();
        for (Iterator i = getAllMetadata().keySet().iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            if (!getIndexableMetadataKeys().contains(key)) {
                result.put(key, getMetadata(key));
            }
        }
        return result;
    }

    public boolean isMetadataIndexable(String key) {
        return getIndexableMetadataKeys().contains(key);
    }

    protected Map getMetadataMap() {
        if (metadataMap == null) {
            metadataMap = new HashMap();
        }
        return metadataMap;
    }

    protected List getIndexableMetadataKeys() {
        if (indexableMetadataKeys == null) {
            indexableMetadataKeys = new ArrayList();
        }
        return indexableMetadataKeys;
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
