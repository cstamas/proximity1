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

import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.storage.AbstractStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * This class is a base abstract class for remot storages.
 * 
 * @author cstamas
 */
public abstract class AbstractRemoteStorage
    extends AbstractStorage
    implements RemoteStorage
{

    /** The logger. */
    protected Logger logger = LoggerFactory.getLogger( this.getClass() );

    /** The remote url. */
    private URL remoteUrl;

    /** The remote url as string. */
    private String remoteUrlAsString;

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.storage.remote.RemoteStorage#getRemoteUrl()
     */
    public URL getRemoteUrl()
    {
        return this.remoteUrl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.storage.remote.RemoteStorage#setRemoteUrl(java.net.URL)
     */
    public void setRemoteUrl( URL url )
        throws MalformedURLException
    {
        this.remoteUrl = url;
        this.remoteUrlAsString = remoteUrl.toString();
        if ( remoteUrlAsString.endsWith( ItemProperties.PATH_SEPARATOR ) )
        {
            remoteUrlAsString = remoteUrlAsString.substring( 0, remoteUrlAsString.length()
                - ItemProperties.PATH_SEPARATOR.length() );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.storage.remote.RemoteStorage#getAbsoluteUrl(java.lang.String)
     */
    public String getAbsoluteUrl( String path )
    {
        if ( path.startsWith( ItemProperties.PATH_SEPARATOR ) )
        {
            return remoteUrlAsString + path;
        }
        else
        {
            return remoteUrlAsString + ItemProperties.PATH_SEPARATOR + path;
        }
    }

}
