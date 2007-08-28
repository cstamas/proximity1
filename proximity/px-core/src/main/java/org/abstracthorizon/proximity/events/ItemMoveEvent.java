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
package org.abstracthorizon.proximity.events;

import org.abstracthorizon.proximity.ProximityRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class ItemMoveEvent.
 */
public class ItemMoveEvent
    extends ProximityRequestEvent
{

    /** The target. */
    private ProximityRequest target;

    /**
     * Instantiates a new item move event.
     * 
     * @param source the source
     * @param target the target
     */
    public ItemMoveEvent( ProximityRequest source, ProximityRequest target )
    {
        super( source );
        this.getTarget();
    }

    /**
     * Gets the source.
     * 
     * @return the source
     */
    public ProximityRequest getSource()
    {
        return getRequest();
    }

    /**
     * Gets the target.
     * 
     * @return the target
     */
    public ProximityRequest getTarget()
    {
        return target;
    }

}
