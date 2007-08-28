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
package org.abstracthorizon.proximity;

import org.abstracthorizon.proximity.events.ProximityRequestEvent;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving proximityRequest events. The class that is interested in processing a
 * proximityRequest event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addProximityRequestListener<code> method. When
 * the proximityRequest event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ProximityRequestEvent
 */
public interface ProximityRequestListener
{

    /**
     * Proximity request event.
     * 
     * @param event the event
     */
    public void proximityRequestEvent( ProximityRequestEvent event );

}
