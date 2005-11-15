package hu.ismicro.commons.proximity;

import java.io.InputStream;

public interface Item {
    
    String getPath();
    
    InputStream getStream();

}
