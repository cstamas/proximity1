package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ProxiedItem;

import java.io.InputStream;
import java.net.URL;

public class SimpleProxiedItem implements ProxiedItem {
    
    private String repositoryName;

    private String storageName;

    private URL originatingUrl;

    private String path;

    private InputStream stream;
    
    private boolean directory;

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public String getStorageName() {
        return storageName;
    }

    public void setOriginatingUrl(URL originatingUrl) {
        this.originatingUrl = originatingUrl;
    }

    public URL getOriginatingUrl() {
        return originatingUrl;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    public InputStream getStream() {
        return stream;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public boolean isDirectory() {
        return directory;
    }

    public String toString() {
        return "["+getStorageName()+(isDirectory() ? ",D" : ",F") +"]:"+getPath();
    }

}
