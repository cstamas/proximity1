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
package org.abstracthorizon.proximity.storage.local;

import java.util.List;
import java.util.Map;

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.storage.Storage;
import org.abstracthorizon.proximity.storage.StorageException;

// TODO: Auto-generated Javadoc
/**
 * The Interface LocalStorage.
 */
public interface LocalStorage
    extends Storage
{

    /**
     * Checks if is writable.
     * 
     * @return true, if is writable
     */
    boolean isWritable();

    /**
     * Checks if is metadata aware.
     * 
     * @return true, if is metadata aware
     */
    boolean isMetadataAware();

    /**
     * Recreate metadata.
     * 
     * @param additionalProps the additional props
     * 
     * @throws StorageException the storage exception
     */
    void recreateMetadata( Map additionalProps )
        throws StorageException;

    /**
     * List items.
     * 
     * @param path the path
     * 
     * @return the list
     * 
     * @throws StorageException the storage exception
     */
    List listItems( String path )
        throws StorageException;

    /**
     * Store item.
     * 
     * @param item the item
     * 
     * @throws StorageException the storage exception
     */
    void storeItem( Item item )
        throws StorageException;

    /**
     * Delete item.
     * 
     * @param path the path
     * 
     * @throws StorageException the storage exception
     */
    void deleteItem( String path )
        throws StorageException;

}
