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
package org.abstracthorizon.proximity.storage.remote;

import java.net.MalformedURLException;
import java.net.URL;

import org.abstracthorizon.proximity.storage.Storage;

// TODO: Auto-generated Javadoc
/**
 * Remote storage.
 * 
 * @author cstamas
 */
public interface RemoteStorage
    extends Storage
{

    /**
     * Gets the remote url.
     * 
     * @return the remote url
     */
    URL getRemoteUrl();

    /**
     * Sets the remote url.
     * 
     * @param url the new remote url
     * 
     * @throws MalformedURLException the malformed URL exception
     */
    void setRemoteUrl( URL url )
        throws MalformedURLException;

    /**
     * Gets the absolute url.
     * 
     * @param path the path
     * 
     * @return the absolute url
     */
    String getAbsoluteUrl( String path );

}
