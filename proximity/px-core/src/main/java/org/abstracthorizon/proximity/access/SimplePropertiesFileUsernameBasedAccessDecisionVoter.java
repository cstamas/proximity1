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

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;

// TODO: Auto-generated Javadoc
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
    
    /** The auth properties. */
    private Properties authProperties;

    /**
     * Gets the auth properties.
     * 
     * @return the auth properties
     */
    public Properties getAuthProperties()
    {
        return authProperties;
    }

    /**
     * Sets the auth properties.
     * 
     * @param properties the new auth properties
     */
    public void setAuthProperties( Properties properties )
    {
        this.authProperties = properties;
    }

    /* (non-Javadoc)
     * @see org.abstracthorizon.proximity.access.AccessDecisionVoter#vote(org.abstracthorizon.proximity.ProximityRequest, org.abstracthorizon.proximity.Repository, org.abstracthorizon.proximity.access.RepositoryPermission)
     */
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
