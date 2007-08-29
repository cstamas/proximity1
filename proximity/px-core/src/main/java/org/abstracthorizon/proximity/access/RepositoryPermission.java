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
package org.abstracthorizon.proximity.access;

// TODO: Auto-generated Javadoc
/**
 * Repository permissions blatantly "borrowed" from com.sonatype.webdav.security.Permission.
 * 
 * @author cstamas
 */
public class RepositoryPermission
{
    
    /** The Constant RETRIEVE. */
    public static final RepositoryPermission RETRIEVE = new RepositoryPermission( "proxy.retrieve" );

    /** The Constant DELETE. */
    public static final RepositoryPermission DELETE = new RepositoryPermission( "proxy.delete" );

    /** The Constant STORE. */
    public static final RepositoryPermission STORE = new RepositoryPermission( "proxy.store" );

    /** The Constant LIST. */
    public static final RepositoryPermission LIST = new RepositoryPermission( "proxy.list" );

    /** The perm. */
    private String perm;

    /**
     * Instantiates a new repository permission.
     * 
     * @param perm the perm
     */
    public RepositoryPermission( String perm )
    {
        super();
        this.perm = perm;
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId()
    {
        return perm;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return perm;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object compare )
    {
        if ( !( compare instanceof RepositoryPermission ) )
        {
            return false;
        }

        return perm == null ? false : perm.equals( ( (RepositoryPermission) compare ).getId() );
    }

}
