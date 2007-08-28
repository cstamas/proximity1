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
package org.abstracthorizon.proximity;

import java.io.InputStream;

// TODO: Auto-generated Javadoc
/**
 * An Item abstraction with properties and content.
 */
public class Item
{

    /** The properties. */
    private ItemProperties properties;

    /** The stream. */
    private InputStream stream;

    /**
     * Returns the item properties.
     * 
     * @return the properties
     */
    public ItemProperties getProperties()
    {
        return properties;
    }

    /**
     * Sets the item properties.
     * 
     * @param properties the properties
     */
    public void setProperties( ItemProperties properties )
    {
        this.properties = properties;
    }

    /**
     * Returns the item input stream.
     * 
     * @return the stream
     */
    public InputStream getStream()
    {
        return stream;
    }

    /**
     * Sets item input stream.
     * 
     * @param stream the stream
     */
    public void setStream( InputStream stream )
    {
        this.stream = stream;
    }

    /**
     * For Debug, returns getProperties.toString();
     * 
     * @return the string
     */
    public String toString()
    {
        if ( getProperties() != null )
        {
            return getProperties().toString();
        }
        else
        {
            return "Item";
        }
    }

}
