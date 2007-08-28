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
package org.abstracthorizon.proximity.storage;

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.metadata.ProxiedItemPropertiesFactory;

// TODO: Auto-generated Javadoc
/**
 * A storage abstraction.
 * 
 * @author cstamas
 */
public interface Storage
{

    /**
     * Contains item.
     * 
     * @param path the path
     * 
     * @return true, if successful
     * 
     * @throws StorageException the storage exception
     */
    boolean containsItem( String path )
        throws StorageException;

    /**
     * Retrieve item.
     * 
     * @param path the path
     * @param propsOnly the props only
     * 
     * @return the item
     * 
     * @throws ItemNotFoundException the item not found exception
     * @throws StorageException the storage exception
     */
    Item retrieveItem( String path, boolean propsOnly )
        throws ItemNotFoundException,
            StorageException;

    /**
     * Gets the proxied item properties factory.
     * 
     * @return the proxied item properties factory
     */
    ProxiedItemPropertiesFactory getProxiedItemPropertiesFactory();

    /**
     * Sets the proxied item properties factory.
     * 
     * @param pic the new proxied item properties factory
     */
    void setProxiedItemPropertiesFactory( ProxiedItemPropertiesFactory pic );

}
