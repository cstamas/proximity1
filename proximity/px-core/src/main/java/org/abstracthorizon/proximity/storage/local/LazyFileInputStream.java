package org.abstracthorizon.proximity.storage.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class LazyFileInputStream extends InputStream {

    private File target;

    private FileInputStream fis = null;

    protected File getFile() {
	return this.target;
    }

    public LazyFileInputStream(File file) throws FileNotFoundException {
	super();
	if (file.exists()) {
	    this.target = file;
	} else {
	    throw new FileNotFoundException(file.getAbsolutePath());
	}
    }

    public int read() throws IOException {
	if (fis == null) {
	    fis = new FileInputStream(target);
	}
	return fis.read();
    }

    public void close() throws IOException {
	fis.close();
    }

}
