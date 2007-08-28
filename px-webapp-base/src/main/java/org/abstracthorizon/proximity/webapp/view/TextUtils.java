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
package org.abstracthorizon.proximity.webapp.view;

// TODO: Auto-generated Javadoc
/**
 * The Class TextUtils.
 */
public class TextUtils
{

    /** The max length. */
    private int maxLength = 40;

    /**
     * Instantiates a new text utils.
     */
    public TextUtils()
    {
        super();
    }

    /**
     * Gets the as label.
     * 
     * @param string the string
     * 
     * @return the as label
     */
    public String getAsLabel( Object string )
    {
        String label = string.toString();
        if ( label.length() < maxLength )
        {
            return label;
        }
        else
        {
            return label.substring( 0, maxLength / 2 ) + "...."
                + label.substring( label.length() - maxLength / 2, label.length() );
        }
    }

}
