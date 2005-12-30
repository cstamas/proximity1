package hu.ismicro.commons.proximity.base;


import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.ItemProperties;

import java.util.List;

public interface Storage {
    
    String getId();
    
    boolean isWritable();
    
    boolean containsItem(String path) throws StorageException;

    ProxiedItemProperties retrieveItemProperties(String path) throws ItemNotFoundException, StorageException;
    
    ProxiedItem retrieveItem(String path) throws ItemNotFoundException, StorageException;

    List listItems(String path) throws StorageException;

    void storeItemProperties(ItemProperties item) throws StorageException;

    void storeItem(Item item) throws StorageException;

    void deleteItem(String path) throws StorageException;

}
