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
package org.abstracthorizon.proximity.logic;

import java.io.IOException;
import java.util.List;

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ProximityRequest;

// TODO: Auto-generated Javadoc
/**
 * The Interface ProximityLogic.
 */
public interface ProximityLogic
{

    /**
     * Returns true if search should be conducted by repository groups and not by absolute order.
     * 
     * @param request the current request.
     * 
     * @return true, if search should be conducted by groups and not the absolute repository ordering.
     */
    boolean isGroupSearchNeeded( ProximityRequest request );

    /**
     * Gets the group request.
     * 
     * @param request the request
     * 
     * @return the group request
     */
    ProximityRequest getGroupRequest( ProximityRequest request );

    /**
     * Postprocess item list.
     * 
     * @param request the request
     * @param groupRequest the group request
     * @param listOfProxiedItems the list of proxied items
     * 
     * @return the item
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     */
    Item postprocessItemList( ProximityRequest request, ProximityRequest groupRequest, List listOfProxiedItems )
        throws IOException;

}
