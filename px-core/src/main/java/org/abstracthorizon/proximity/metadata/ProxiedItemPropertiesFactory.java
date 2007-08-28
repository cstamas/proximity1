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
import java.util.List;

import org.abstracthorizon.proximity.ItemProperties;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating ProxiedItemProperties objects.
 */
public interface ProxiedItemPropertiesFactory
{

    /**
     * Returns the list that this constructor marks as indexable.
     * 
     * @return list of keywords usable in searches.
     */
    List getSearchableKeywords();

    /**
     * Returns filled in item properties for file that is located on path.
     * 
     * @param file The file about we need more to know.
     * @param path the path
     * @param defaultOnly the default only
     * 
     * @return filled up ProxiedItemProperties.
     */
    ItemProperties expandItemProperties( String path, File file, boolean defaultOnly );

}
