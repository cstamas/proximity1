/*

   Copyright 2005-2007 Tamas Cservenak (t.cservenak@gmail.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.abstracthorizon.proximity.storage.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

// TODO: Auto-generated Javadoc
/**
 * The Class LazyFileInputStream.
 */
public class LazyFileInputStream
    extends InputStream
{

    /** The target. */
    private File target;

    /** The fis. */
    private FileInputStream fis = null;

    /**
     * Gets the file.
     * 
     * @return the file
     */
    protected File getFile()
    {
        return this.target;
    }

    /**
     * Instantiates a new lazy file input stream.
     * 
     * @param file the file
     * 
     * @throws FileNotFoundException the file not found exception
     */
    public LazyFileInputStream( File file )
        throws FileNotFoundException
    {
        super();
        if ( file.exists() )
        {
            this.target = file;
        }
        else
        {
            throw new FileNotFoundException( file.getAbsolutePath() );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.InputStream#read()
     */
    public int read()
        throws IOException
    {
        if ( fis == null )
        {
            fis = new FileInputStream( target );
        }
        return fis.read();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.InputStream#close()
     */
    public void close()
        throws IOException
    {
        if ( fis != null )
        {
            fis.close();
        }
    }

}
