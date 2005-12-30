package hu.ismicro.commons.proximity.storage;

import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.base.PathHelper;
import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.ProxiedItemProperties;
import hu.ismicro.commons.proximity.base.StorageException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReadOnlyFileSystemStorage extends AbstractStorage {

    private String baseDir;

    public void setBaseDir(String baseDirPath) {
        File baseDirFile = new File(baseDirPath);
        if (!(baseDirFile.exists() && baseDirFile.isDirectory())) {
            throw new IllegalArgumentException("The supplied parameter does not exists or is not a directory!");
        }
        this.baseDir = baseDirPath;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public boolean containsItem(String path) {
        logger.info("Checking for existence of " + path + " in " + getBaseDir());
        File target = new File(PathHelper.walkThePath(getBaseDir(), path));
        return target.exists();
    }

    public ProxiedItemProperties retrieveItemProperties(String path) throws StorageException {
        logger.info("Retrieving " + path + " properties in " + getBaseDir());
        File target = new File(PathHelper.walkThePath(getBaseDir(), path));
        return constructItemProperties(target, path);
    }

    public ProxiedItem retrieveItem(String path) throws ItemNotFoundException, StorageException {
        logger.info("Retrieving " + path + " in " + getBaseDir());
        try {
            File target = new File(PathHelper.walkThePath(getBaseDir(), path));
            ProxiedItemProperties properties = constructItemProperties(target, path);
            ProxiedItem result = new ProxiedItem();
            result.setProperties(properties);
            result.setStream(new FileInputStream(target));
            return result;
        } catch (FileNotFoundException ex) {
            logger.error("FileNotFound in FS storage [" + getBaseDir() + "] for path [" + path + "]", ex);
            throw new ItemNotFoundException("FileNotFound in FS storage [" + getBaseDir() + "] for path [" + path + "]");
        }
    }

    public List listItems(String path) {
        logger.info("Listing " + path + " in " + getBaseDir());
        List result = new ArrayList();
        String targetPath = PathHelper.absolutizePathFromBase(getBaseDir(), path);
        File target = new File(targetPath);
        if (target.exists()) {
            if (target.isDirectory()) {
                File[] files = target.listFiles();
                for (int i = 0; i < files.length; i++) {
                    ProxiedItemProperties item = constructItemProperties(files[i], PathHelper.changePathLevel(path, PathHelper.getFileName(files[i].getPath())));
                    result.add(item);
                }
            } else {
                ProxiedItemProperties item = constructItemProperties(target, targetPath);
                result.add(item);
            }
        }
        return result;
    }
    
    protected ProxiedItemProperties constructItemProperties(File target, String path) {
        ProxiedItemProperties result = new ProxiedItemProperties();
        result.setAbsolutePath(PathHelper.absolutizePathFromBase(PathHelper.PATH_SEPARATOR, PathHelper.changePathLevel(
                path, PathHelper.PATH_PARENT)));
        result.setDirectory(target.isDirectory());
        result.setFile(target.isFile());
        result.setLastModified(new Date(target.lastModified()));
        result.setName(PathHelper.getFileName(path));
        result.setOriginatingUrl(null);
        result.setSize(target.length());
        return result;
    }

}
