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
package org.abstracthorizon.proximity.mapping;

import java.util.List;

import org.abstracthorizon.proximity.ProximityRequest;

// TODO: Auto-generated Javadoc
/**
 * The Interface GroupRequestMapper.
 */
public interface GroupRequestMapper
{

    /**
     * Gets the mapped repositories.
     * 
     * @param groupId the group id
     * @param request the request
     * @param originalRepositoryGroupOrder the original repository group order
     * 
     * @return the mapped repositories
     */
    List getMappedRepositories( String groupId, ProximityRequest request, List originalRepositoryGroupOrder );

}
