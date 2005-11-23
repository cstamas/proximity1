package hu.ismicro.commons.proximity.storage;

import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.SimpleProxiedItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileSystemStorage extends AbstractStorage {

    private File baseDir;

    public void setBaseDir(File baseDir) {
        if (!baseDir.isDirectory()) {
            throw new IllegalArgumentException("The supplied parameter is not a directory!");
        }
        this.baseDir = baseDir;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public boolean containsItem(String path) {
        logger.info("Checking for existence of " + path + " in " + getBaseDir());
        File target = new File(getBaseDir(), path);
        return target.exists();
    }

    public ProxiedItem retrieveItem(String path) {
        logger.info("Retrieving " + path + " in " + getBaseDir());
        try {
            File target = new File(getBaseDir(), path);
            SimpleProxiedItem result = new SimpleProxiedItem();
            result.setPath(path);
            result.setStorageName(getBaseDir().getPath());
            result.setOriginatingUrl(null);
            result.setLastModified(new Date(target.lastModified()));
            result.setSize(target.length());
            if (target.isDirectory()) {
                result.setDirectory(true);
                result.setStream(null);
            } else {
                result.setDirectory(false);
                result.setStream(new FileInputStream(target));
            }
            return result;
        } catch (FileNotFoundException ex) {
            logger.error("FileNotFound in FS storage " + getBaseDir(), ex);
            return null;
        }
    }

    public List listItems(String path) {
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        logger.info("Listing " + path + " in " + getBaseDir());
        List result = new ArrayList();
        File target = null;
        if (path.equals("/")) {
            target = getBaseDir();
        } else {
            target = new File(getBaseDir(), path);
        }
        if (target.exists()) {
            if (target.isDirectory()) {
                File[] files = target.listFiles();
                for (int i = 0; i < files.length; i++) {
                    SimpleProxiedItem item = new SimpleProxiedItem();
                    item.setPath(path + files[i].getName());
                    item.setStorageName(getBaseDir().getPath());
                    item.setOriginatingUrl(null);
                    item.setDirectory(files[i].isDirectory());
                    item.setSize(files[i].isDirectory() ? 0 : files[i].length());
                    item.setLastModified(new Date(files[i].lastModified()));
                    result.add(item);
                }
            } else {
                SimpleProxiedItem item = new SimpleProxiedItem();
                item.setPath(path);
                item.setStorageName(getBaseDir().getPath());
                item.setOriginatingUrl(null);
                item.setDirectory(true);
                item.setSize(target.length());
                item.setLastModified(new Date(target.lastModified()));
                result.add(item);
            }
        }
        return result;
    }

}
