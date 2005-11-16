package hu.ismicro.commons.proximity.storage;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.StorageException;
import hu.ismicro.commons.proximity.WritableStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class WritableFileSystemStorage extends FileSystemStorage implements WritableStorage {

    private boolean storeMetadata = false;

    private boolean storeOriginatingUrls = false;

    public void setStoreMetadata(boolean storeMetadata) {
        this.storeMetadata = storeMetadata;
    }

    public boolean isStoreMetadata() {
        return storeMetadata;
    }

    public void setStoreOriginatingUrls(boolean storeOriginatingUrls) {
        this.storeOriginatingUrls = storeOriginatingUrls;
    }

    public boolean isStoreOriginatingUrls() {
        return storeOriginatingUrls;
    }

    public void storeItem(Item item) {
        logger.info("Storing " + item.getPath() + " in " + getBaseDir());
        try {
            File file = new File(getBaseDir(), item.getPath());
            file.getParentFile().mkdirs();
            FileOutputStream os = new FileOutputStream(file);
            byte[] buffer = new byte[8192];
            int read;
            while (item.getStream().available() != 0) {
                read = item.getStream().read(buffer);
                os.write(buffer, 0, read);
            }
            os.flush();
            os.close();
            item.getStream().close();
            if (isStoreMetadata()) {
                storeMetadata(item);
            }
        } catch (IOException ex) {
            logger.error("IOException in FS storage " + getBaseDir(), ex);
        }
    }

    public void deleteItem(Item item) {
        logger.info("Deleting " + item.getPath() + " in " + getBaseDir());
        File file = new File(getBaseDir(), item.getPath());
        if (file.exists()) {
            if (!file.delete()) {
                throw new StorageException("Unable to delete file " + file.getPath());
            }
            deleteMetadata(item);
        }
    }

    protected void storeMetadata(Item item) {
        try {
            if (isStoreOriginatingUrls() && item.getOriginatingUrl() != null) {
                logger.debug("Storing metadata for item " + item.getPath());
                PrintWriter fw = new PrintWriter(new File(getBaseDir(), item.getPath() + ".url"));
                fw.println(item.getOriginatingUrl().toString());
                fw.close();
            } else {
                logger.debug("Not storing metadata for item " + item.getPath() + " as currently configured.");
            }
        } catch (FileNotFoundException ex) {
            logger.error("Could not store metadata for item " + item.getPath() + ", continuing silently.", ex);
        }
    }

    protected void deleteMetadata(Item item) {
        File file = new File(getBaseDir(), item.getPath() + ".url");
        if (file.exists()) {
            if (!file.delete()) {
                logger.error("Could not delete metadata for item " + item.getPath() + ", continuing silently.");
            }
        }
    }

}
