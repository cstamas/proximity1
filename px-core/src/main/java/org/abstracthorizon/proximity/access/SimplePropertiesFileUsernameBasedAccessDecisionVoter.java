package org.abstracthorizon.proximity.access;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;

/**
 * <p>
 * Simple voter that allows/denies the repository access based on property file.
 * <p>
 * A property file looks like this:
 * 
 * <pre>
 * repoId1 = user1,user2,user3
 * repoId2 = user2,user3
 * </pre>
 * 
 * <p>
 * If the user is found on line for given repoId, the user has granted access, otherwise he is rejected.
 * <p>
 * This implementation does not takes permissions in account.
 * 
 * @author cstamas
 */
public class SimplePropertiesFileUsernameBasedAccessDecisionVoter
    implements UsernameBasedAccessDecisionVoter
{
    private Properties authProperties;

    public Properties getAuthProperties()
    {
        return authProperties;
    }

    public void setAuthProperties( Properties properties )
    {
        this.authProperties = properties;
    }

    public int vote( ProximityRequest request, Repository repository, RepositoryPermission permission )
    {
        if ( request.getAttributes().containsKey( REQUEST_USERNAME ) )
        {
            String allowedUsers = getAuthProperties().getProperty( repository.getId() );
            String username = (String) request.getAttributes().get( REQUEST_USERNAME );

            List usersList = Arrays.asList( allowedUsers.split( "," ) );
            if ( usersList.contains( username ) )
            {
                return ACCESS_APPROVED;
            }
            else
            {
                return ACCESS_DENIED;
            }
        }
        else
        {
            return ACCESS_DENIED;
        }
    }

}
