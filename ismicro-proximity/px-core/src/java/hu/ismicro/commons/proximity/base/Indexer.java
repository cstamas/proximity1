package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemProperties;

import java.util.List;

/**
 * Indexer, that keeps info and indexes items.
 * 
 * @author cstamas
 *
 */
public interface Indexer {
	
    void addItemProperties(ItemProperties ip);
    
    void deleteItemProperties(ItemProperties ip);

	List searchByItemPropertiesExample(ItemProperties ip);
    
}
