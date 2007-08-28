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

// TODO: Auto-generated Javadoc
/**
 * Thrown when a Proximity request is not backed by trusted/granted authority.
 * 
 * @author cstamas
 */
public class AccessDeniedException
    extends ProximityException
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8341250956517740603L;

    /** The request. */
    private ProximityRequest request;

    /**
     * Instantiates a new access denied exception.
     * 
     * @param request the request
     * @param msg the msg
     */
    public AccessDeniedException( ProximityRequest request, String msg )
    {
        super( msg );
        this.request = request;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Throwable#toString()
     */
    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append( "Access " );
        str.append( "to resource " );
        str.append( request.getPath() );
        str.append( " has been forbidden because:" );
        str.append( super.toString() );
        return str.toString();
    }

}
