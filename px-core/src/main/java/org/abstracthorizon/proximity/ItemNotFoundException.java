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
package org.abstracthorizon.proximity;

// TODO: Auto-generated Javadoc
/**
 * Thrown by repository if the requested item is not found.
 * 
 * @author cstamas
 */
public class ItemNotFoundException
    extends ProximityException
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4964273361722823796L;

    /**
     * Instantiates a new item not found exception.
     * 
     * @param path the path
     */
    public ItemNotFoundException( String path )
    {
        super( "Item not found on path " + path );
    }

    /**
     * Instantiates a new item not found exception.
     * 
     * @param path the path
     * @param repo the repo
     */
    public ItemNotFoundException( String path, String repo )
    {
        super( "Item not found on path " + path + " in repository " + repo );
    }

}
