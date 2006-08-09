package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemProperties;

import java.io.File;
import java.util.List;

public class DefaultProxiedItemPropertiesConstructor extends AbstractProxiedItemPropertiesConstructor {

    protected void getCustomSearchableKeywords(List defaults) {
        //nothing extra
    }

    protected void expandCustomItemProperties(ItemProperties ip, File file) {
        //nothing extra
    }

}
