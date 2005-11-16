package hu.ismicro.commons.proximity;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

public interface Item {

    String getPath();
    
    String getName();

    InputStream getStream();
    
    boolean isDirectory();

    String getRepositoryName();

    String getStorageName();

    URL getOriginatingUrl();
    
    long getSize();
    
    Date getLastModified();

}
