package hu.ismicro.commons.proximity.base;

import java.io.File;

public interface ProxiedItemPropertiesConstructor {

    /**
     * Returns filled in item properties for file that is located on path.
     * 
     * @param file
     *            the file for which we constructs item properties.
     * 
     * @param path
     *            the repo path where this item is located.
     * @return filled up ProxiedItemProperties.
     */
    ProxiedItemProperties buildItemProperties(File file, String path);

}
