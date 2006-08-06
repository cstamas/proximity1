package hu.ismicro.commons.proximity.base;

import java.io.File;

public interface ProxiedItemPropertiesConstructor {

    /**
     * Returns filled in item properties for file that is located on path.
     * 
     * @param file
     *            The file about we need more to know.
     * @return filled up ProxiedItemProperties.
     */
    void expandItemProperties(ProxiedItemProperties ip, File file);

}
