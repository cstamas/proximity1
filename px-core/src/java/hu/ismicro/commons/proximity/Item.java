package hu.ismicro.commons.proximity;

import java.io.InputStream;
import java.net.URL;

public interface Item {

    String getPath();

    InputStream getStream();
    
    boolean isDirectory();

    String getRepositoryName();

    String getStorageName();

    URL getOriginatingUrl();

}
