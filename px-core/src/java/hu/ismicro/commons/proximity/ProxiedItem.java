package hu.ismicro.commons.proximity;

import java.net.URL;

public interface ProxiedItem extends Item {

    void setRepositoryName(String name);
    
    void setStorageName(String name);

    void setOriginatingUrl(URL url);
    
}
