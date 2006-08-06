package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.Item;

import java.util.List;

public interface LocalStorage extends Storage {

    boolean isWritable();

    boolean isMetadataAware();
    
    void setProxiedItemPropertiesConstructor(ProxiedItemPropertiesConstructor pic);

    void recreateMetadata();

    List listItems(String path) throws StorageException;

    void storeItem(Item item) throws StorageException;

    void deleteItem(String path) throws StorageException;

}
