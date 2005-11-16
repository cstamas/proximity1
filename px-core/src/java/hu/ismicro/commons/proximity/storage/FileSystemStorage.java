package hu.ismicro.commons.proximity.storage;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.base.SimpleProxiedItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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

    public Item retrieveItem(String path) {
        logger.info("Retrieving " + path + " in " + getBaseDir());
        try {
            File target = new File(getBaseDir(), path);
            SimpleProxiedItem result = new SimpleProxiedItem();
            result.setPath(path);
            result.setOriginatingUrl(null);
            result.setStream(new FileInputStream(target));
            return result;
        } catch (FileNotFoundException ex) {
            logger.error("FileNotFound in FS storage " + getBaseDir(), ex);
            return null;
        }
    }

    public List listItems(String path) {
        logger.info("Listing " + path + " in " + getBaseDir());
        if (!path.endsWith("/")) {
            throw new IllegalArgumentException("Cannot list a non-dir with FS Storage!");
        } else {
            List result = new ArrayList();
            File target = null;
            if (path.equals("/")) {
                target = getBaseDir();
            } else {
                target = new File(getBaseDir(), path);
            }
            if (target.exists()) {
                File[] files = target.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isFile()) {
                        SimpleProxiedItem item = new SimpleProxiedItem();
                        item.setPath(path + files[i].getName());
                        result.add(item);
                    }
                }
            }
            return result;
        }
    }

}
