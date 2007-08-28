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

import org.abstracthorizon.proximity.metadata.InspectorListProcessingProxiedItemPropertiesFactory;
import org.abstracthorizon.proximity.metadata.ProxiedItemPropertiesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractStorage.
 */
public abstract class AbstractStorage
    implements Storage
{

    /** The logger. */
    protected Logger logger = LoggerFactory.getLogger( this.getClass() );

    /** The proxied item properties constructor. */
    protected ProxiedItemPropertiesFactory proxiedItemPropertiesConstructor = new InspectorListProcessingProxiedItemPropertiesFactory();

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.storage.Storage#setProxiedItemPropertiesFactory(org.abstracthorizon.proximity.metadata.ProxiedItemPropertiesFactory)
     */
    public void setProxiedItemPropertiesFactory( ProxiedItemPropertiesFactory pic )
    {
        this.proxiedItemPropertiesConstructor = pic;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.storage.Storage#getProxiedItemPropertiesFactory()
     */
    public ProxiedItemPropertiesFactory getProxiedItemPropertiesFactory()
    {
        return this.proxiedItemPropertiesConstructor;
    }

}
