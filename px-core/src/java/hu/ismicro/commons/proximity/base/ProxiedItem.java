package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.Item;

import java.net.URL;

public interface ProxiedItem extends Item {

    void setRepositoryName(String name);
    
    void setStorageName(String name);

    void setOriginatingUrl(URL url);
    
}
