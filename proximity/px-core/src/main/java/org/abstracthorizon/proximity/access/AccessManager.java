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

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;

// TODO: Auto-generated Javadoc
/**
 * Interface for access manager.
 * 
 * @author t.cservenak
 */
public interface AccessManager
{

    /**
     * The implementation of this method should throw AccessDeniedException or any subclass if it denies access.
     * 
     * @param request the request
     * @param repository the repository
     * @param permission the permission
     * 
     * @throws AccessDeniedException the access denied exception
     */
    void decide( ProximityRequest request, Repository repository, RepositoryPermission permission )
        throws AccessDeniedException;

}
