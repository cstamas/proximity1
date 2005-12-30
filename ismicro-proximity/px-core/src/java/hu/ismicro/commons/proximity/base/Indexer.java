package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemProperties;

public interface Indexer {
    
    void addItemProperties(ItemProperties ip);
    
    void deleteItemProperties(ItemProperties ip);

}
