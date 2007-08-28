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
 * The Class ProximityRequestEvent.
 */
public abstract class ProximityRequestEvent
    extends ProximityEvent
{

    /** The request. */
    private ProximityRequest request;

    /**
     * Instantiates a new proximity request event.
     * 
     * @param req the req
     */
    public ProximityRequestEvent( ProximityRequest req )
    {
        super();
        this.request = req;
    }

    /**
     * Gets the request.
     * 
     * @return the request
     */
    public ProximityRequest getRequest()
    {
        return request;
    }

}
