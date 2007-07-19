package org.abstracthorizon.proximity.metadata.inspectors;

import java.io.File;
import java.util.List;

import org.abstracthorizon.proximity.ItemProperties;

public interface ItemInspector {

    boolean isHandled(ItemProperties ip);

    List getIndexableKeywords();

    void processItem(ItemProperties ip, File file);

}
