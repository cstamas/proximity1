package org.abstracthorizon.proximity.metadata;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Sends bytes to nowhere.
 * 
 * @author cstamas
 *
 */
public class DevNullOutputStream extends OutputStream {

    public DevNullOutputStream() {
        super();
    }

    public void write(int b) throws IOException {
        //nothing
    }

}
