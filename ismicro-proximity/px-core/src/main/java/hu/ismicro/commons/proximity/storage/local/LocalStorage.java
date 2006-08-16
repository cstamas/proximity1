package hu.ismicro.commons.proximity.storage.local;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.storage.Storage;
import hu.ismicro.commons.proximity.storage.StorageException;

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
