package hu.ismicro.commons.proximity.base.remote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A simple FileInputStream wrapper that closes and deletes the file associated with it.
 * Usable for temporary files.
 * 
 * @author cstamas
 *
 */
public class DeleteOnCloseFileInputStream extends FileInputStream {
    
    private File file;

    public DeleteOnCloseFileInputStream(File file) throws FileNotFoundException {
        super(file);
        this.file = file;
    }

    public void close() throws IOException {
        super.close();
        file.delete();
    }
    
}
