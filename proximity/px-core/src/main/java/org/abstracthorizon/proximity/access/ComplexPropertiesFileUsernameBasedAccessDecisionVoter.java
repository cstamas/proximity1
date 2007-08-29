package org.abstracthorizon.proximity.access;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Quasi complex voter that allows/denies the repository access based on property files. The property files are searched
 * on the propertiesBase and are loaded with Classloader (for demo purposes).
 * <p>
 * The name of the property file to be looked up is formed as:
 * 
 * <pre>
 * propertiesBase + &lt;username&gt;-&lt;repositoryId&gt;.properties
 * </pre>
 * 
 * <p>
 * A property file looks like this:
 * 
 * <pre>
 * path1 = perm1,perm2
 * path2 = perm3,perm4
 * </pre>
 * 
 * <p>
 * If the property file is not found for given repoId and username, the user has access as defined in default rights
 * properties.
 * 
 * @author cstamas
 */
public class ComplexPropertiesFileUsernameBasedAccessDecisionVoter
    implements UsernameBasedAccessDecisionVoter
{
    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    private Properties defaultRights;

    private String propertiesBase;

    public String getPropertiesBase()
    {
        if ( this.propertiesBase == null )
        {
            return "/";
        }
        else
        {
            return propertiesBase;
        }
    }

    public void setPropertiesBase( String propertiesBase )
    {
        this.propertiesBase = propertiesBase;
        if ( !this.propertiesBase.endsWith( "/" ) )
        {
            this.propertiesBase = this.propertiesBase + "/";
        }
    }

    public Properties getDefaultRights()
    {
        if ( defaultRights == null )
        {
            this.defaultRights = new Properties();
        }
        return defaultRights;
    }

    public void setDefaultRights( Properties defaultRights )
    {
        this.defaultRights = defaultRights;
    }

    public int vote( ProximityRequest request, Repository repository, RepositoryPermission permission )
    {
        Properties props = getProperties( (String) request.getAttributes().get( REQUEST_USERNAME ), repository.getId() );
        return authorizeTree( props, request.getPath(), permission );
    }

    private int authorizeTree( Properties path2rights, String path, RepositoryPermission permission )
    {
        String pathRights = path2rights.getProperty( path );

        if ( pathRights != null )
        {
            List userList = Arrays.asList( pathRights.split( "," ) );

            if ( userList.contains( permission.getId() ) )
            {
                return ACCESS_APPROVED;
            }
        }

        String parent = ( new File( path ) ).getParent();
        if ( parent == null )
        {
            return ACCESS_DENIED;
        }

        return authorizeTree( path2rights, parent, permission );
    }

    protected Properties getProperties( String username, String repositoryId )
    {
        Properties props = new Properties( getDefaultRights() );
        if ( username != null && repositoryId != null )
        {

            String propsName = getPropertiesBase() + username + "-" + repositoryId + ".properties";
            try
            {
                InputStream is = this.getClass().getResourceAsStream( propsName );
                if ( is != null )
                {
                    props.load( is );
                }
                else
                {
                    throw new IOException( "Resource not found by Classloader." );
                }
            }
            catch ( IOException e )
            {
                logger.info( "There is no properties file with name {}. Using defaults.", propsName );
            }
        }
        else
        {
            logger.info( "There is no security information in request. Using defaults." );
        }
        return props;
    }

}
