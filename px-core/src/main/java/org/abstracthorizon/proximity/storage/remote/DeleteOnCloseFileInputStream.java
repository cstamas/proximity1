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
package org.abstracthorizon.proximity.storage.remote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.abstracthorizon.proximity.storage.local.LazyFileInputStream;

// TODO: Auto-generated Javadoc
/**
 * A simple FileInputStream wrapper that closes and deletes the file associated with it. Usable for temporary files.
 * 
 * @author cstamas
 */
public class DeleteOnCloseFileInputStream
    extends LazyFileInputStream
{

    /**
     * Instantiates a new delete on close file input stream.
     * 
     * @param file the file
     * 
     * @throws FileNotFoundException the file not found exception
     */
    public DeleteOnCloseFileInputStream( File file )
        throws FileNotFoundException
    {
        super( file );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.storage.local.LazyFileInputStream#close()
     */
    public void close()
        throws IOException
    {
        super.close();
        getFile().delete();
    }

}
