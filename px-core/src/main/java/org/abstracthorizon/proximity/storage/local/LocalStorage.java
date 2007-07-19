package org.abstracthorizon.proximity.storage.local;

import java.util.List;
import java.util.Map;

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.storage.Storage;
import org.abstracthorizon.proximity.storage.StorageException;

public interface LocalStorage extends Storage {

    boolean isWritable();

    boolean isMetadataAware();

    void recreateMetadata(Map additionalProps) throws StorageException;

    List listItems(String path) throws StorageException;

    void storeItem(Item item) throws StorageException;

    void deleteItem(String path) throws StorageException;

}
