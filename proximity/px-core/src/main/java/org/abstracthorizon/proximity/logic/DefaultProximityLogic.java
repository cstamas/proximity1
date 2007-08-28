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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The default Proximity logic. It actually does not interfere with Proximity operation known till now.
 * 
 * @author cstamas
 */
public class DefaultProximityLogic
    implements ProximityLogic
{

    /** The logger. */
    protected Logger logger = LoggerFactory.getLogger( this.getClass() );

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.logic.ProximityLogic#isGroupSearchNeeded(org.abstracthorizon.proximity.ProximityRequest)
     */
    public boolean isGroupSearchNeeded( ProximityRequest request )
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.logic.ProximityLogic#getGroupRequest(org.abstracthorizon.proximity.ProximityRequest)
     */
    public ProximityRequest getGroupRequest( ProximityRequest request )
    {
        return request;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.logic.ProximityLogic#postprocessItemList(org.abstracthorizon.proximity.ProximityRequest,
     *      org.abstracthorizon.proximity.ProximityRequest, java.util.List)
     */
    public Item postprocessItemList( ProximityRequest request, ProximityRequest groupRequest, List listOfProxiedItems )
        throws IOException
    {
        throw new UnsupportedOperationException( "The DefaultProximityLogic does not implements postprocessing." );
    }

}
