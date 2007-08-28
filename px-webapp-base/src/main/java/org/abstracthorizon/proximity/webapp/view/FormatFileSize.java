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

import java.text.DecimalFormat;

// TODO: Auto-generated Javadoc
/**
 * A stupid little class to format file sizes in "human readable" form. Used in Proximity webapp as part of Velocity
 * toolbox.
 * 
 * @author cstamas
 */
public class FormatFileSize
{

    /** The format integer. */
    private DecimalFormat formatInteger = new DecimalFormat( "0" );

    /** The format decimals. */
    private DecimalFormat formatDecimals = new DecimalFormat( "0.00" );

    /** The Bytes suffix. */
    private String BytesSuffix = "Byte";

    /** The k bytes suffix. */
    private String kBytesSuffix = "KB";

    /** The m bytes suffix. */
    private String mBytesSuffix = "MB";

    /** The g bytes suffix. */
    private String gBytesSuffix = "GB";

    /** The t bytes suffix. */
    private String tBytesSuffix = "TB";

    /** The base. */
    public double base = 1024;

    /**
     * Instantiates a new format file size.
     */
    public FormatFileSize()
    {
        super();
    }

    /**
     * Gets the file size as string.
     * 
     * @param sizeInBytesObj the size in bytes obj
     * 
     * @return the file size as string
     */
    public String getFileSizeAsString( Object sizeInBytesObj )
    {
        double sizeLong = 0;
        double base = getBase();
        String suffix = null;
        try
        {
            sizeLong = Double.parseDouble( sizeInBytesObj.toString() );
        }
        catch ( NullPointerException e )
        {
            return null;
        }
        catch ( NumberFormatException e )
        {
            return sizeInBytesObj.toString();
        }
        if ( Math.rint( sizeLong / base ) == 0 )
        {
            // we do not have decimals
            return formatInteger.format( sizeLong ) + " " + getBytesSuffix();
        }
        else if ( Math.rint( sizeLong / ( base * base ) ) == 0 )
        {
            suffix = getKBytesSuffix();
            sizeLong = sizeLong / base;
        }
        else if ( Math.rint( sizeLong / ( base * base * base ) ) == 0 )
        {
            suffix = getMBytesSuffix();
            sizeLong = sizeLong / ( base * base );
        }
        else if ( Math.rint( sizeLong / ( base * base * base * base ) ) == 0 )
        {
            suffix = getGBytesSuffix();
            sizeLong = sizeLong / ( base * base * base );
        }
        else if ( Math.rint( sizeLong / ( base * base * base * base * base ) ) == 0 )
        {
            suffix = getTBytesSuffix();
            sizeLong = sizeLong / ( base * base * base * base );
        }
        return formatDecimals.format( sizeLong ) + " " + suffix;
    }

    /**
     * Gets the base.
     * 
     * @return the base
     */
    public double getBase()
    {
        return base;
    }

    /**
     * Sets the base.
     * 
     * @param base the new base
     */
    public void setBase( double base )
    {
        this.base = base;
    }

    /**
     * Gets the bytes suffix.
     * 
     * @return the bytes suffix
     */
    public String getBytesSuffix()
    {
        return BytesSuffix;
    }

    /**
     * Sets the bytes suffix.
     * 
     * @param bytesSuffix the new bytes suffix
     */
    public void setBytesSuffix( String bytesSuffix )
    {
        BytesSuffix = bytesSuffix;
    }

    /**
     * Gets the g bytes suffix.
     * 
     * @return the g bytes suffix
     */
    public String getGBytesSuffix()
    {
        return gBytesSuffix;
    }

    /**
     * Sets the g bytes suffix.
     * 
     * @param bytesSuffix the new g bytes suffix
     */
    public void setGBytesSuffix( String bytesSuffix )
    {
        gBytesSuffix = bytesSuffix;
    }

    /**
     * Gets the k bytes suffix.
     * 
     * @return the k bytes suffix
     */
    public String getKBytesSuffix()
    {
        return kBytesSuffix;
    }

    /**
     * Sets the k bytes suffix.
     * 
     * @param bytesSuffix the new k bytes suffix
     */
    public void setKBytesSuffix( String bytesSuffix )
    {
        kBytesSuffix = bytesSuffix;
    }

    /**
     * Gets the m bytes suffix.
     * 
     * @return the m bytes suffix
     */
    public String getMBytesSuffix()
    {
        return mBytesSuffix;
    }

    /**
     * Sets the m bytes suffix.
     * 
     * @param bytesSuffix the new m bytes suffix
     */
    public void setMBytesSuffix( String bytesSuffix )
    {
        mBytesSuffix = bytesSuffix;
    }

    /**
     * Gets the t bytes suffix.
     * 
     * @return the t bytes suffix
     */
    public String getTBytesSuffix()
    {
        return tBytesSuffix;
    }

    /**
     * Sets the t bytes suffix.
     * 
     * @param bytesSuffix the new t bytes suffix
     */
    public void setTBytesSuffix( String bytesSuffix )
    {
        tBytesSuffix = bytesSuffix;
    }

}
