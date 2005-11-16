package hu.ismicro.commons.proximity;

import java.util.List;

public interface Storage {
    
    boolean containsItem(String path);

    ProxiedItem retrieveItem(String path);

    List listItems(String path);

}
