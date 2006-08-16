package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ItemProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractProxiedItemPropertiesFactory that gathers Proximity standard item
 * properties.
 * 
 * @author cstamas
 *
 */
public abstract class AbstractProxiedItemPropertiesFactory implements ProxiedItemPropertiesFactory {
    
    /**
     * As it's name says, output goes to /dev/null :)
     * 
     * @author cstamas
     *
     */
    private class DevNullOutputStream extends OutputStream {
        public void write(int b) throws IOException {
            //nothing
        }
    }

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public final List getSearchableKeywords() {
        List result = getDefaultSearchableKeywords();
        getCustomSearchableKeywords(result);
        return result;
    }

    public final ProxiedItemProperties expandItemProperties(String path, File file, boolean defaultOnly) {
        ProxiedItemProperties ip = new ProxiedItemProperties();
        expandDefaultItemProperties(path, ip, file);
        if (!defaultOnly) {
            expandItemHashProperties(path, ip, file);
            expandCustomItemProperties(ip, file);
            ip.setLastScannedExt(new Date());
        }
        return ip;
    }

    protected final List getDefaultSearchableKeywords() {
        List result = new ArrayList();
        // set the default ItemProperties
        result.add(ItemProperties.METADATA_ABSOLUTE_PATH);
        result.add(ItemProperties.METADATA_NAME);
        result.add(ItemProperties.METADATA_FILESIZE);
        result.add(ItemProperties.METADATA_LAST_MODIFIED);
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

    protected final void expandDefaultItemProperties(String path, ProxiedItemProperties ip, File file) {
        ip.setAbsolutePath(PathHelper.getDirName(path));
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
        } else {
            ip.setSize(0);
        }
        ip.setLastScanned(new Date());
    }
    
    protected final void expandItemHashProperties(String path, ProxiedItemProperties ip, File file) {
        if (file.isFile()) {
            String digest = getFileDigest(file, "md5");
            if (digest != null) {
                ip.setMetadata(ItemProperties.METADATA_HASH_MD5, digest);
            }
            digest = getFileDigest(file, "sha1");
            if (digest != null) {
                ip.setMetadata(ItemProperties.METADATA_HASH_SHA1, digest);
            }
        }
    }

    protected String getFileDigest(File file, String alg) {
        //TODO: cleanup this mess!
        try {
            String digestStr = null;
            MessageDigest dalg = MessageDigest.getInstance(alg);
            FileInputStream fis = null;
            DigestInputStream dis = null;
            try {
                fis = new FileInputStream(file);
                DevNullOutputStream fos = new DevNullOutputStream();
                dis = new DigestInputStream(fis, dalg);
                IOUtils.copy(dis, fos);
                digestStr = new String(Hex.encodeHex(dalg.digest()));
            } finally {
                if (dis != null) {
                    dis.close();
                    fis.close();
                }
            }
            return digestStr;
        } catch (Exception ex) {
            return null;
        }
    }

    protected abstract void getCustomSearchableKeywords(List defaults);

    protected abstract void expandCustomItemProperties(ProxiedItemProperties ip, File file);

}
