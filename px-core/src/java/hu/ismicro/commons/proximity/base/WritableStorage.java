package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.Item;

public interface WritableStorage extends Storage {

    void storeItem(Item item);

    void deleteItem(Item item);

    void setStoreMetadata(boolean storeMetadata);

}
