package hu.ismicro.commons.proximity.storage;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.base.PathHelper;
import hu.ismicro.commons.proximity.base.StorageException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class WritableFileSystemStorage extends ReadOnlyFileSystemStorage {

    public void storeItem(Item item) throws StorageException {
        logger.info("Storing item in [" + item.getProperties().getAbsolutePath() + "] with name ["
                + item.getProperties().getName() + "] in " + getBaseDir());
        try {
            File file = new File(PathHelper.walkThePath(getBaseDir(), PathHelper.changePathLevel(item.getProperties().getAbsolutePath(), item
                    .getProperties().getName())));
            file.getParentFile().mkdirs();
            FileOutputStream os = new FileOutputStream(file);
            IOUtils.copy(item.getStream(), os);
            item.getStream().close();
            os.flush();
            os.close();
            file.setLastModified(item.getProperties().getLastModified().getTime());
        } catch (IOException ex) {
            logger.error("IOException in FS storage " + getBaseDir(), ex);
            throw new StorageException("IOException in FS storage " + getBaseDir(), ex);
        }
    }

    public void deleteItem(String path) throws StorageException {
        logger.info("Deleting " + path + " in " + getBaseDir());
        File file = new File(getBaseDir(), path);
        if (file.exists()) {
            if (!file.delete()) {
                throw new StorageException("Unable to delete file " + file.getPath());
            }
        }
    }

}
