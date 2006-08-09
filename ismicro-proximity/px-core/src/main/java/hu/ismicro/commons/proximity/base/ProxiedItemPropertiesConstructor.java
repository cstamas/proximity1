package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemProperties;

import java.io.File;
import java.util.List;

public interface ProxiedItemPropertiesConstructor {

    /**
     * Returns the list that this constructor marks as indexable.
     * 
     * @return list of keywords usable in searches.
     */
    List getSearchableKeywords();

    /**
     * Returns filled in item properties for file that is located on path.
     * 
     * @param file
     *            The file about we need more to know.
     * @return filled up ProxiedItemProperties.
     */
    void expandItemProperties(ItemProperties ip, File file);

}
