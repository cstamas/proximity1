package org.abstracthorizon.proximity.metadata;


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.abstracthorizon.proximity.impl.ItemPropertiesImpl;
import org.abstracthorizon.proximity.metadata.inspectors.ItemInspector;

public class InspectorListProcessingProxiedItemPropertiesFactory extends AbstractProxiedItemPropertiesFactory {

    protected List itemInspectorList = new ArrayList();

    public List getItemInspectorList() {
        return itemInspectorList;
    }

    public void setItemInspectorList(List itemInspectorList) {
        this.itemInspectorList = itemInspectorList;
    }

    protected void getCustomSearchableKeywords(List defaults) {
        for (Iterator i = getItemInspectorList().iterator(); i.hasNext();) {
            ItemInspector inspector = (ItemInspector) i.next();
            defaults.addAll(inspector.getIndexableKeywords());
        }
    }

    protected void expandCustomItemProperties(ItemPropertiesImpl ip, File file) {
        for (Iterator i = getItemInspectorList().iterator(); i.hasNext();) {
            ItemInspector inspector = (ItemInspector) i.next();
            if (inspector.isHandled(ip)) {
                try {
                    inspector.processItem(ip, file);
                } catch (Exception ex) {
                    logger.error("Inspector {} throw exception: {}", inspector, ex);
                }
            }
        }
    }
}
