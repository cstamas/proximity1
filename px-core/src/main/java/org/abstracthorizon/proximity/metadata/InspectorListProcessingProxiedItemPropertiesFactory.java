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
package org.abstracthorizon.proximity.metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.metadata.inspectors.ItemInspector;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating InspectorListProcessingProxiedItemProperties objects.
 */
public class InspectorListProcessingProxiedItemPropertiesFactory
    extends AbstractProxiedItemPropertiesFactory
{

    /** The item inspector list. */
    protected List itemInspectorList = new ArrayList();

    /**
     * Gets the item inspector list.
     * 
     * @return the item inspector list
     */
    public List getItemInspectorList()
    {
        return itemInspectorList;
    }

    /**
     * Sets the item inspector list.
     * 
     * @param itemInspectorList the new item inspector list
     */
    public void setItemInspectorList( List itemInspectorList )
    {
        this.itemInspectorList = itemInspectorList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.metadata.AbstractProxiedItemPropertiesFactory#getCustomSearchableKeywords(java.util.List)
     */
    protected void getCustomSearchableKeywords( List defaults )
    {
        for ( Iterator i = getItemInspectorList().iterator(); i.hasNext(); )
        {
            ItemInspector inspector = (ItemInspector) i.next();
            defaults.addAll( inspector.getIndexableKeywords() );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.metadata.AbstractProxiedItemPropertiesFactory#expandCustomItemProperties(org.abstracthorizon.proximity.ItemProperties,
     *      java.io.File)
     */
    protected void expandCustomItemProperties( ItemProperties ip, File file )
    {
        for ( Iterator i = getItemInspectorList().iterator(); i.hasNext(); )
        {
            ItemInspector inspector = (ItemInspector) i.next();
            if ( inspector.isHandled( ip ) )
            {
                try
                {
                    inspector.processItem( ip, file );
                }
                catch ( Exception ex )
                {
                    logger.error( "Inspector {} throw exception: {}", inspector, ex );
                }
            }
        }
    }
}
