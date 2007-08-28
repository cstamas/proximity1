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
package org.abstracthorizon.proximity.ws;

import org.abstracthorizon.proximity.NoSuchRepositoryException;

// TODO: Auto-generated Javadoc
/**
 * The Interface MaintenanceService.
 */
public interface MaintenanceService
{

    /**
     * Forces reindex of all repositories.
     */
    void reindexAll();

    /**
     * Forces reindex of repository with ID repoId.
     * 
     * @param repoId the repo id
     * 
     * @throws NoSuchRepositoryException the no such repository exception
     */
    void reindexRepository( String repoId )
        throws NoSuchRepositoryException;

}
