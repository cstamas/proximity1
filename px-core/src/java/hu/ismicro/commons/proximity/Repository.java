package hu.ismicro.commons.proximity;


import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.ProxiedItemProperties;
import hu.ismicro.commons.proximity.base.RepositoryLogic;
import hu.ismicro.commons.proximity.base.Storage;
import hu.ismicro.commons.proximity.base.StorageException;

import java.util.List;

public interface Repository {

    String getId();
    
    void setLocalStorage(Storage storage);

    void setRemoteStorage(Storage storage);

    void setRepositoryLogic(RepositoryLogic logic);
    
    ProxiedItemProperties retrieveItemProperties(String path) throws ItemNotFoundException, StorageException;

    ProxiedItem retrieveItem(String path) throws ItemNotFoundException, StorageException;
    
    void deleteItemProperties(String path) throws StorageException;

    void deleteItem(String path) throws StorageException;

    void storeItemProperties(ItemProperties itemProps) throws StorageException;

    void storeItem(Item item) throws StorageException;

    List listItems(String path) throws StorageException;

}
