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
 * A simple AccessManager implementation that allows everybody to access the Proximity core.
 * 
 * @author t.cservenak
 */
public class OpenAccessManager
    implements AccessManager
{

    /* (non-Javadoc)
     * @see org.abstracthorizon.proximity.access.AccessManager#decide(org.abstracthorizon.proximity.ProximityRequest, org.abstracthorizon.proximity.Repository, org.abstracthorizon.proximity.access.RepositoryPermission)
     */
    public void decide( ProximityRequest request, Repository repository, RepositoryPermission permission )
        throws AccessDeniedException
    {
        // this access manager is open, everybody has access to everything since
        // it never throws AccessDeniedEx
    }

}
