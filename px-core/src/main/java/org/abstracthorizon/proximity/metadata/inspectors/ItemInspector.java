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
package org.abstracthorizon.proximity.metadata.inspectors;

import java.io.File;
import java.util.List;

import org.abstracthorizon.proximity.ItemProperties;

// TODO: Auto-generated Javadoc
/**
 * The Interface ItemInspector.
 */
public interface ItemInspector
{

    /**
     * Checks if is handled.
     * 
     * @param ip the ip
     * 
     * @return true, if is handled
     */
    boolean isHandled( ItemProperties ip );

    /**
     * Gets the indexable keywords.
     * 
     * @return the indexable keywords
     */
    List getIndexableKeywords();

    /**
     * Process item.
     * 
     * @param ip the ip
     * @param file the file
     */
    void processItem( ItemProperties ip, File file );

}
