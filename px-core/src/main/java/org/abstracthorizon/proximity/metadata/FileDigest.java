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
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class FileDigest.
 */
public class FileDigest
{

    /**
     * Instantiates a new file digest.
     */
    private FileDigest()
    {
        super();
    }

    /**
     * Gets the file digest.
     * 
     * @param file the file
     * @param alg the alg
     * 
     * @return the file digest
     */
    public static byte[] getFileDigest( File file, String alg )
    {
        byte[] result;
        try
        {
            MessageDigest dalg = MessageDigest.getInstance( alg );
            FileInputStream fis = null;
            DigestInputStream dis = null;
            try
            {
                fis = new FileInputStream( file );
                DevNullOutputStream fos = new DevNullOutputStream();
                dis = new DigestInputStream( fis, dalg );
                IOUtils.copy( dis, fos );
                result = dalg.digest();
            }
            finally
            {
                if ( dis != null )
                {
                    dis.close();
                }
                if ( fis != null )
                {
                    fis.close();
                }
            }
            return result;
        }
        catch ( Exception ex )
        {
            return null;
        }
    }

    /**
     * Gets the file digest as string.
     * 
     * @param file the file
     * @param alg the alg
     * 
     * @return the file digest as string
     */
    public static String getFileDigestAsString( File file, String alg )
    {
        try
        {
            return new String( Hex.encodeHex( getFileDigest( file, alg ) ) );
        }
        catch ( Exception ex )
        {
            return null;
        }
    }

}
