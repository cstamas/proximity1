package hu.ismicro.commons.proximity;

public interface WritableStorage extends Storage {

    void storeItem(Item item);

    void deleteItem(Item item);

}
