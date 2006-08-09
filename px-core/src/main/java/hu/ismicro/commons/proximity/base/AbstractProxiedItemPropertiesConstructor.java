package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

public abstract class AbstractProxiedItemPropertiesConstructor implements ProxiedItemPropertiesConstructor {

    public final List getSearchableKeywords() {
        List result = getDefaultSearchableKeywords();
        getCustomSearchableKeywords(result);
        return result;
    }
    
    public final void expandItemProperties(ItemProperties ip, File file) {
        expandDefaultItemProperties(ip, file);
        expandCustomItemProperties(ip, file);
    }
    
    protected final List getDefaultSearchableKeywords() {
        List result = new ArrayList();
        // set the default ItemProperties
        result.add(ItemProperties.METADATA_NAME);
        result.add(ItemProperties.METADATA_OWNING_REPOSITORY);
        result.add(ItemProperties.METADATA_OWNING_REPOSITORY_GROUP);
        result.add(ItemProperties.METADATA_ABSOLUTE_PATH);
        result.add(ItemProperties.METADATA_FILESIZE);
        result.add(ItemProperties.METADATA_IS_DIRECTORY);
        result.add(ItemProperties.METADATA_IS_FILE);
        result.add(ItemProperties.METADATA_KIND);
        result.add(ItemProperties.METADATA_ORIGINATING_URL);
        return result;
    }

    protected final void expandDefaultItemProperties(ItemProperties ip, File file) {
        if (ip.isFile()) {
            String ext = FilenameUtils.getExtension(ip.getName());
            if (ext != null) {
                ip.setMetadata(ItemProperties.METADATA_KIND, ext, true);
            }
        }
    }
    
    protected abstract void getCustomSearchableKeywords(List defaults);

    protected abstract void expandCustomItemProperties(ItemProperties ip, File file);

}
