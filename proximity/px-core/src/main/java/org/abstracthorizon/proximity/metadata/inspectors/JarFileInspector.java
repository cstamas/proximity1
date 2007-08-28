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
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.abstracthorizon.proximity.ItemProperties;

// TODO: Auto-generated Javadoc
/**
 * The Class JarFileInspector.
 */
public class JarFileInspector
    extends AbstractItemInspector
{

    /** The JA r_ MF. */
    public static String JAR_MF = "jar.mf";

    /** The JA r_ DIRS. */
    public static String JAR_DIRS = "jar.dirs";

    /** The JA r_ FILES. */
    public static String JAR_FILES = "jar.files";

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
            return "jar".equals( ext ) || "war".equals( ext ) || "ear".equals( ext );
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
        result.add( JAR_FILES );
        result.add( JAR_DIRS );
        result.add( JAR_MF );
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

            JarFile jFile = new JarFile( file );
            try
            {
                StringBuffer dirs = new StringBuffer( jFile.size() );
                StringBuffer files = new StringBuffer( jFile.size() );

                for ( Enumeration e = jFile.entries(); e.hasMoreElements(); )
                {
                    JarEntry entry = (JarEntry) e.nextElement();
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

                ip.setMetadata( JAR_FILES, files.toString() );
                ip.setMetadata( JAR_DIRS, dirs.toString() );

                Manifest mf = jFile.getManifest();
                if ( mf != null )
                {
                    StringBuffer mfEntries = new StringBuffer( jFile.getManifest().getMainAttributes().size() );
                    Attributes mAttr = mf.getMainAttributes();
                    for ( Iterator i = mAttr.keySet().iterator(); i.hasNext(); )
                    {
                        Attributes.Name atrKey = (Attributes.Name) i.next();
                        mfEntries.append( mAttr.getValue( atrKey ) );
                        mfEntries.append( "\n" );
                    }
                    ip.setMetadata( JAR_MF, mfEntries.toString() );
                }
            }
            finally
            {
                jFile.close();
            }

        }
        catch ( IOException ex )
        {
            logger.info( "Got IOException while creating JarFile on file [{}].", file.getAbsolutePath(), ex );
        }
    }

}
