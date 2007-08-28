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

import java.util.Map;

import org.abstracthorizon.proximity.ProximityRequest;

// TODO: Auto-generated Javadoc
/**
 * The Interface AccessDecisionVoter.
 */
public interface AccessDecisionVoter
{

    /** Vote for approval. */
    static final int ACCESS_APPROVED = 1;

    /** Vote for neutral status. */
    static final int ACCESS_NEUTRAL = 0;

    /** Vote for denial. */
    static final int ACCESS_DENIED = -1;

    /**
     * The implementation of this method should return one of the ACCESS_APPROVED, ACCESS_NEUTRAL or ACCESS_DENIED
     * constants.
     * 
     * @param request the request
     * @param attribs the attribs
     * 
     * @return The vote for this access.
     */
    int vote( ProximityRequest request, Map attribs );

}
