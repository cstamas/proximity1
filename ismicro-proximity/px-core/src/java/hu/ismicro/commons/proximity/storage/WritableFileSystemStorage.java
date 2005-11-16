package hu.ismicro.commons.proximity.storage;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.StorageException;
import hu.ismicro.commons.proximity.WritableStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WritableFileSystemStorage extends FileSystemStorage implements WritableStorage {

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
        }
    }

}
