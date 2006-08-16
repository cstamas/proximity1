package hu.ismicro.commons.proximity.metadata.inspectors;

import hu.ismicro.commons.proximity.impl.ItemPropertiesImpl;

import java.io.File;
import java.util.List;

public interface ItemInspector {
    
    boolean isHandled(ItemPropertiesImpl ip);
    
    List getIndexableKeywords();
    
    void processItem(ItemPropertiesImpl ip, File file);

}
