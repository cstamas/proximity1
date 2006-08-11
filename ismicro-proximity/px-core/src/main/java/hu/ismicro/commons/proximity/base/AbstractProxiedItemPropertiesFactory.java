package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractProxiedItemPropertiesFactory implements ProxiedItemPropertiesFactory {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public final List getSearchableKeywords() {
        List result = getDefaultSearchableKeywords();
        getCustomSearchableKeywords(result);
        return result;
    }

    public final ProxiedItemProperties expandItemProperties(File file, boolean defaultOnly) {
        ProxiedItemProperties ip = new ProxiedItemProperties();
        expandDefaultItemProperties(ip, file);
        if (!defaultOnly) {
            expandCustomItemProperties(ip, file);
        }
        return ip;
    }

    protected final List getDefaultSearchableKeywords() {
        List result = new ArrayList();
        // set the default ItemProperties
        result.add(ItemProperties.METADATA_ABSOLUTE_PATH);
        result.add(ItemProperties.METADATA_NAME);
        result.add(ItemProperties.METADATA_FILESIZE);
        result.add(ItemProperties.METADATA_IS_DIRECTORY);
        result.add(ItemProperties.METADATA_IS_FILE);
        result.add(ItemProperties.METADATA_EXT);
        result.add(ItemProperties.METADATA_HASH_MD5);
        result.add(ItemProperties.METADATA_HASH_SHA1);
        result.add(ItemProperties.METADATA_OWNING_REPOSITORY);
        result.add(ItemProperties.METADATA_OWNING_REPOSITORY_GROUP);
        result.add(ItemProperties.METADATA_ORIGINATING_URL);
        return result;
    }

    protected final void expandDefaultItemProperties(ProxiedItemProperties ip, File file) {
        ip.setAbsolutePath(PathHelper.getDirName(file.getAbsolutePath()));
        ip.setName(file.getName());
        ip.setDirectory(file.isDirectory());
        ip.setFile(file.isFile());
        ip.setLastModified(new Date(file.lastModified()));
        if (file.isFile()) {
            String ext = FilenameUtils.getExtension(file.getName());
            if (ext != null) {
                ip.setMetadata(ItemProperties.METADATA_EXT, ext);
            }
            ip.setSize(file.length());
            ip.setMetadata(ItemProperties.METADATA_HASH_MD5, getFileDigest(file, "md5"));
            ip.setMetadata(ItemProperties.METADATA_HASH_SHA1, getFileDigest(file, "sha1"));
        } else {
            ip.setSize(0);
        }
    }

    protected String getFileDigest(File file, String alg) {
        try {
            return "dummy";
        } catch (Exception ex) {
            return "dummy";
        }
    }

    protected abstract void getCustomSearchableKeywords(List defaults);

    protected abstract void expandCustomItemProperties(ProxiedItemProperties ip, File file);

}
