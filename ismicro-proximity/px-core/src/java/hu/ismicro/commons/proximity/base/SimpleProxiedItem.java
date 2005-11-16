package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ProxiedItem;

import java.io.InputStream;
import java.net.URL;

public class SimpleProxiedItem implements ProxiedItem {

    private String repositoryName;

    private URL originatingUrl;

    private String path;

    private InputStream stream;

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getRepositoryName() {
        return repositoryName;
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

}
