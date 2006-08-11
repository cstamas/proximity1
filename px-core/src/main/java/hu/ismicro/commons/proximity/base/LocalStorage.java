package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.Item;

import java.util.List;
import java.util.Map;

public interface LocalStorage extends Storage {

    boolean isWritable();

    boolean isMetadataAware();
    
    void recreateMetadata(Map additionalProps) throws StorageException;

    List listItems(String path) throws StorageException;

    void storeItem(Item item) throws StorageException;

    void deleteItem(String path) throws StorageException;

}
