package hu.ismicro.commons.proximity.base;

import java.io.File;
import java.util.List;

public interface ItemInspector {
    
    boolean isHandled(ProxiedItemProperties ip);
    
    List getIndexableKeywords();
    
    void processItem(ProxiedItemProperties ip, File file);

}
