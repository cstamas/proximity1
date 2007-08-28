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
package org.abstracthorizon.proximity.metadata.inspectors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.abstracthorizon.proximity.ItemProperties;

// TODO: Auto-generated Javadoc
/**
 * The Class ZipFileInspector.
 */
public class ZipFileInspector
    extends AbstractItemInspector
{

    /** The ZI p_ FILES. */
    public static String ZIP_FILES = "zip.files";

    /** The ZI p_ DIRS. */
    public static String ZIP_DIRS = "zip.dirs";

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.metadata.inspectors.ItemInspector#isHandled(org.abstracthorizon.proximity.ItemProperties)
     */
    public boolean isHandled( ItemProperties ip )
    {
        try
        {
            String ext = ip.getExtension().toLowerCase();
            return "zip".equals( ext );
        }
        catch ( NullPointerException ex )
        {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.metadata.inspectors.ItemInspector#getIndexableKeywords()
     */
    public List getIndexableKeywords()
    {
        List result = new ArrayList( 1 );
        result.add( ZIP_DIRS );
        result.add( ZIP_FILES );
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.metadata.inspectors.ItemInspector#processItem(org.abstracthorizon.proximity.ItemProperties,
     *      java.io.File)
     */
    public void processItem( ItemProperties ip, File file )
    {
        try
        {
            ZipFile zFile = new ZipFile( file );
            try
            {
                StringBuffer files = new StringBuffer( zFile.size() );
                StringBuffer dirs = new StringBuffer( zFile.size() );

                for ( Enumeration e = zFile.entries(); e.hasMoreElements(); )
                {
                    ZipEntry entry = (ZipEntry) e.nextElement();
                    if ( entry.isDirectory() )
                    {
                        dirs.append( entry.getName() );
                        dirs.append( "\n" );
                    }
                    else
                    {
                        files.append( entry.getName() );
                        files.append( "\n" );
                    }
                }
                ip.setMetadata( ZIP_FILES, files.toString() );
                ip.setMetadata( ZIP_DIRS, dirs.toString() );

            }
            finally
            {
                zFile.close();
            }
        }
        catch ( IOException ex )
        {
            logger.info( "Got IOException while creating ZipFile on file [{}].", file.getAbsolutePath(), ex );
        }
    }

}
