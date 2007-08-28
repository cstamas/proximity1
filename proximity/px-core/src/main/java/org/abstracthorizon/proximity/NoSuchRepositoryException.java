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
 * Thrown by proximity if the requested Repository does not exists.
 * 
 * @author cstamas
 */
public class NoSuchRepositoryException
    extends ProximityException
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4422677352666814031L;

    /**
     * Instantiates a new no such repository exception.
     * 
     * @param repoId the repo id
     */
    public NoSuchRepositoryException( String repoId )
    {
        super( "Repository with name " + repoId + " not found!" );
    }

}
