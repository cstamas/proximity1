package org.abstracthorizon.proximity.access;

/**
 * Repository permissions blatantly "borrowed" from com.sonatype.webdav.security.Permission.
 * 
 * @author cstamas
 */
public class RepositoryPermission
{
    public static final RepositoryPermission RETRIEVE = new RepositoryPermission( "proxy.retrieve" );

    public static final RepositoryPermission DELETE = new RepositoryPermission( "proxy.delete" );

    public static final RepositoryPermission STORE = new RepositoryPermission( "proxy.store" );

    public static final RepositoryPermission LIST = new RepositoryPermission( "proxy.list" );

    private String perm;

    public RepositoryPermission( String perm )
    {
        super();
        this.perm = perm;
    }

    public String getId()
    {
        return perm;
    }

    public String toString()
    {
        return perm;
    }

    public boolean equals( Object compare )
    {
        if ( !( compare instanceof RepositoryPermission ) )
        {
            return false;
        }

        return perm == null ? false : perm.equals( ( (RepositoryPermission) compare ).getId() );
    }

}
