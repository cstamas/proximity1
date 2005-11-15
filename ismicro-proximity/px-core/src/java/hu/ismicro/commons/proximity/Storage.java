package hu.ismicro.commons.proximity;

import java.util.List;

public interface Storage {

    boolean containsItem(String path);

    Item retrieveItem(String path);
    
    void storeItem(Item item);
    
    List listItems(String path);

}
