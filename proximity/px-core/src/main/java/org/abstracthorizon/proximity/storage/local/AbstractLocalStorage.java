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

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.storage.AbstractStorage;
import org.abstracthorizon.proximity.storage.StorageException;

// TODO: Auto-generated Javadoc
/**
 * Abstract Storage class. It have ID and defines logger. Predefines all write methods with throwing
 * UnsupportedOperationException-s.
 * 
 * @author cstamas
 */
public abstract class AbstractLocalStorage
    extends AbstractStorage
    implements LocalStorage
{

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.storage.local.LocalStorage#isWritable()
     */
    public boolean isWritable()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.storage.local.LocalStorage#listItems(java.lang.String)
     */
    public List listItems( String path )
        throws StorageException
    {
        throw new UnsupportedOperationException( "The " + getClass().getName() + " storage is not listable!" );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.storage.local.LocalStorage#storeItem(org.abstracthorizon.proximity.Item)
     */
    public void storeItem( Item item )
        throws StorageException
    {
        throw new UnsupportedOperationException( "The " + getClass().getName() + " storage is ReadOnly!" );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.storage.local.LocalStorage#deleteItem(java.lang.String)
     */
    public void deleteItem( String path )
        throws StorageException
    {
        throw new UnsupportedOperationException( "The " + getClass().getName() + " storage is ReadOnly!" );
    }

}
