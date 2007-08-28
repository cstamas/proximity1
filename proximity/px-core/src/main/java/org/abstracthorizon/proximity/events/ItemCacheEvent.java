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

import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.ProximityRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class ItemCacheEvent.
 */
public class ItemCacheEvent
    extends ProximityRequestEvent
{

    /** The item properties. */
    private ItemProperties itemProperties;

    /**
     * Instantiates a new item cache event.
     * 
     * @param req the req
     * @param itemProperties the item properties
     */
    public ItemCacheEvent( ProximityRequest req, ItemProperties itemProperties )
    {
        super( req );
        this.itemProperties = itemProperties;
    }

    /**
     * Gets the item properties.
     * 
     * @return the item properties
     */
    public ItemProperties getItemProperties()
    {
        return itemProperties;
    }

}
