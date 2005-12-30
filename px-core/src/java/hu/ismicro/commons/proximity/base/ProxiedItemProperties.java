package hu.ismicro.commons.proximity.base;

import java.net.URL;
import java.util.Date;

import hu.ismicro.commons.proximity.ItemProperties;

public class ProxiedItemProperties implements ItemProperties {
    
    private String absolutePath;
    
    private String name;
    
    private boolean directory;
    
    private boolean file;
    
    private long size;
    
    private Date lastModified;
    
    private URL originatingUrl;
    
    private String repositoryId;

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

    public URL getOriginatingUrl() {
        return originatingUrl;
    }

    public void setOriginatingUrl(URL originatingUrl) {
        this.originatingUrl = originatingUrl;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

}
