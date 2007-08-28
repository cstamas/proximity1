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
package org.abstracthorizon.proximity.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.abstracthorizon.proximity.ItemProperties;
import org.apache.commons.io.FilenameUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class ProximityUtils.
 */
public class ProximityUtils
{

    /**
     * Explode path to list.
     * 
     * @param path the path
     * 
     * @return the list
     */
    public static List explodePathToList( String path )
    {
        List result = new ArrayList();
        String[] explodedPath = path.split( ItemProperties.PATH_SEPARATOR );
        for ( int i = 0; i < explodedPath.length; i++ )
        {
            if ( explodedPath[i].length() > 0 )
            {
                result.add( explodedPath[i] );
            }
        }
        return result;
    }

    /**
     * Mangle item paths for emerge groups.
     * 
     * @param items the items
     */
    public static void mangleItemPathsForEmergeGroups( List items )
    {
        for ( Iterator i = items.iterator(); i.hasNext(); )
        {
            ItemProperties ip = (ItemProperties) i.next();
            if ( ip.getDirectoryPath().equals( ItemProperties.PATH_ROOT ) )
            {
                // make /groupId as path
                ip.setDirectoryPath( ItemProperties.PATH_ROOT + ip.getRepositoryGroupId() );
            }
            else
            {
                // make /groupId/... as path WITHOUT trailing /
                ip.setDirectoryPath( FilenameUtils.separatorsToUnix( FilenameUtils
                    .normalizeNoEndSeparator( ItemProperties.PATH_ROOT + ip.getRepositoryGroupId()
                        + ip.getDirectoryPath() ) ) );
            }
        }
    }

}
