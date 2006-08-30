package org.abstracthorizon.proximity.metadata.inspectors;


import java.io.File;
import java.util.List;

import org.abstracthorizon.proximity.impl.ItemPropertiesImpl;

public interface ItemInspector {
    
    boolean isHandled(ItemPropertiesImpl ip);
    
    List getIndexableKeywords();
    
    void processItem(ItemPropertiesImpl ip, File file);

}
