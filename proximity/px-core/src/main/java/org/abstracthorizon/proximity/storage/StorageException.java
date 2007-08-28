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

// TODO: Auto-generated Javadoc
/**
 * Generic storage exception thrown by given storage implementation (like IOExceptions), and so. Denotes an
 * unrecoverable system error.
 * 
 * @author cstamas
 */
public class StorageException
    extends RuntimeException
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7119754988039787918L;

    /**
     * Instantiates a new storage exception.
     * 
     * @param msg the msg
     */
    public StorageException( String msg )
    {
        super( msg );
    }

    /**
     * Instantiates a new storage exception.
     * 
     * @param msg the msg
     * @param cause the cause
     */
    public StorageException( String msg, Throwable cause )
    {
        super( msg, cause );
    }

}
