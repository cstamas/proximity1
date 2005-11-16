package hu.ismicro.commons.proximity.base;


import java.io.InputStream;
import java.net.URL;
import java.util.Date;

public class SimpleProxiedItem implements ProxiedItem {
    
    private String repositoryName;

    private String storageName;

    private URL originatingUrl;

    private String path;

    private InputStream stream;
    
    private boolean directory;
    
    private long size;
    
    private Date lastModified;

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
    
    public String getName() {
        if (!"/".equals(getPath())) {
            String path = getPath();
            if (path.endsWith("/")) {
                path = path.substring(0, path.length()-1);
            }
            String[] explodedPath = path.split("/");
            return explodedPath[explodedPath.length-1];
        } else {
            return getPath();
        }
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

    public void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public String toString() {
        return "["+getRepositoryName()+":"+getStorageName()+(isDirectory() ? ":D" : ":F") +"]:"+getPath();
    }

}
