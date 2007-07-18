package org.abstracthorizon.proximity.storage.remote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.abstracthorizon.proximity.storage.local.LazyFileInputStream;

/**
 * A simple FileInputStream wrapper that closes and deletes the file associated
 * with it. Usable for temporary files.
 * 
 * @author cstamas
 * 
 */
public class DeleteOnCloseFileInputStream extends LazyFileInputStream {

    public DeleteOnCloseFileInputStream(File file) throws FileNotFoundException {
	super(file);
    }

    public void close() throws IOException {
	super.close();
	getFile().delete();
    }

}
