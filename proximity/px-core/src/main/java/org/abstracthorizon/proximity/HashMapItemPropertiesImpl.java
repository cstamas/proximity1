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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

// TODO: Auto-generated Javadoc
/**
 * The Class HashMapItemPropertiesImpl.
 */
public class HashMapItemPropertiesImpl
    implements ItemProperties
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 727307616865507746L;

    /** The metadata map. */
    private Map metadataMap;

    /** The date format. */
    private DateFormat dateFormat = new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss Z" );

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#getDirectoryPath()
     */
    public String getDirectoryPath()
    {
        return getMetadata( METADATA_DIRECTORY_PATH );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#setDirectoryPath(java.lang.String)
     */
    public void setDirectoryPath( String absolutePath )
    {
        setMetadata( METADATA_DIRECTORY_PATH, absolutePath );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#getName()
     */
    public String getName()
    {
        return getMetadata( METADATA_NAME );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#setName(java.lang.String)
     */
    public void setName( String name )
    {
        setMetadata( METADATA_NAME, name );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#getPath()
     */
    public String getPath()
    {
        if ( getName().equals( PATH_ROOT ) )
        {
            return getName();
        }
        if ( getDirectoryPath().endsWith( PATH_SEPARATOR ) )
        {
            return getDirectoryPath() + getName();
        }
        else
        {
            return getDirectoryPath() + PATH_SEPARATOR + getName();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#isDirectory()
     */
    public boolean isDirectory()
    {
        if ( getMetadataMap().containsKey( METADATA_IS_DIRECTORY ) )
        {
            return Boolean.valueOf( getMetadata( METADATA_IS_DIRECTORY ) ).booleanValue();
        }
        else
        {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#setDirectory(boolean)
     */
    public void setDirectory( boolean directory )
    {
        setMetadata( METADATA_IS_DIRECTORY, Boolean.toString( directory ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#isFile()
     */
    public boolean isFile()
    {
        if ( getMetadataMap().containsKey( METADATA_IS_FILE ) )
        {
            return Boolean.valueOf( getMetadata( METADATA_IS_FILE ) ).booleanValue();
        }
        else
        {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#setFile(boolean)
     */
    public void setFile( boolean file )
    {
        setMetadata( METADATA_IS_FILE, Boolean.toString( file ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#isCached()
     */
    public boolean isCached()
    {
        if ( getMetadataMap().containsKey( METADATA_IS_CACHED ) )
        {
            return Boolean.valueOf( getMetadata( METADATA_IS_CACHED ) ).booleanValue();
        }
        else
        {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#setCached(boolean)
     */
    public void setCached( boolean cached )
    {
        setMetadata( METADATA_IS_CACHED, Boolean.toString( cached ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#getRemoteUrl()
     */
    public String getRemoteUrl()
    {
        return getMetadata( METADATA_REMOTE_URL );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#setRemoteUrl(java.lang.String)
     */
    public void setRemoteUrl( String remoteUrl )
    {
        setMetadata( METADATA_REMOTE_URL, remoteUrl );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#getLastModified()
     */
    public Date getLastModified()
    {
        String lmstr = getMetadata( METADATA_LAST_MODIFIED );
        if ( lmstr == null )
        {
            return null;
        }
        else
        {
            try
            {
                return dateFormat.parse( lmstr );
            }
            catch ( ParseException ex )
            {
                return null;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#setLastModified(java.util.Date)
     */
    public void setLastModified( Date lastModified )
    {
        if ( lastModified != null )
        {
            setMetadata( METADATA_LAST_MODIFIED, dateFormat.format( lastModified ) );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#getLastScanned()
     */
    public Date getLastScanned()
    {
        String lmstr = getMetadata( METADATA_SCANNED );
        if ( lmstr == null )
        {
            return null;
        }
        else
        {
            try
            {
                return dateFormat.parse( lmstr );
            }
            catch ( ParseException ex )
            {
                return null;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#setLastScanned(java.util.Date)
     */
    public void setLastScanned( Date lastScanned )
    {
        if ( lastScanned != null )
        {
            setMetadata( METADATA_SCANNED, dateFormat.format( lastScanned ) );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#getLastScannedExt()
     */
    public Date getLastScannedExt()
    {
        String lmstr = getMetadata( METADATA_SCANNED_EXT );
        if ( lmstr == null )
        {
            return null;
        }
        else
        {
            try
            {
                return dateFormat.parse( lmstr );
            }
            catch ( ParseException ex )
            {
                return null;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#setLastScannedExt(java.util.Date)
     */
    public void setLastScannedExt( Date lastScanned )
    {
        if ( lastScanned != null )
        {
            setMetadata( METADATA_SCANNED_EXT, dateFormat.format( lastScanned ) );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#getSize()
     */
    public long getSize()
    {
        if ( getMetadataMap().containsKey( METADATA_FILESIZE ) )
        {
            return Long.parseLong( getMetadata( METADATA_FILESIZE ) );
        }
        else
        {
            return 0;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#setSize(long)
     */
    public void setSize( long size )
    {
        setMetadata( METADATA_FILESIZE, Long.toString( size ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#getRepositoryId()
     */
    public String getRepositoryId()
    {
        return getMetadata( METADATA_OWNING_REPOSITORY );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#setRepositoryId(java.lang.String)
     */
    public void setRepositoryId( String id )
    {
        setMetadata( METADATA_OWNING_REPOSITORY, id );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#getRepositoryGroupId()
     */
    public String getRepositoryGroupId()
    {
        return getMetadata( METADATA_OWNING_REPOSITORY_GROUP );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#setRepositoryGroupId(java.lang.String)
     */
    public void setRepositoryGroupId( String id )
    {
        setMetadata( METADATA_OWNING_REPOSITORY_GROUP, id );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#getMetadata(java.lang.String)
     */
    public String getMetadata( String key )
    {
        return (String) getMetadataMap().get( key );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#setMetadata(java.lang.String, java.lang.String)
     */
    public void setMetadata( String key, String value )
    {
        getMetadataMap().put( key, value );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#getAllMetadata()
     */
    public Map getAllMetadata()
    {
        return getMetadataMap();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#putAllMetadata(java.util.Map)
     */
    public void putAllMetadata( Map md )
    {
        getMetadataMap().putAll( md );
    }

    /**
     * Gets the metadata map.
     * 
     * @return the metadata map
     */
    protected Map getMetadataMap()
    {
        if ( metadataMap == null )
        {
            metadataMap = new HashMap();
        }
        return metadataMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getPath() + " " + getMetadataMap().toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        int hash = 7;
        hash = 31 * hash + ( null == getDirectoryPath() ? 0 : getDirectoryPath().hashCode() );
        hash = 31 * hash + ( null == getName() ? 0 : getName().hashCode() );
        return hash;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( ( obj == null ) || ( obj.getClass().isAssignableFrom( ItemProperties.class ) ) )
        {
            return false;
        }
        ItemProperties test = (ItemProperties) obj;
        if ( this.getDirectoryPath() == test.getDirectoryPath() && this.getName() == test.getName() )
            return true;
        if ( ( this.getDirectoryPath() != null && this.getDirectoryPath().equals( test.getDirectoryPath() ) )
            && ( this.getName() != null && this.getName().equals( test.getName() ) ) )
            return true;
        return false;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#getExtension()
     */
    public String getExtension()
    {
        return getMetadata( METADATA_EXT );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#setExtension(java.lang.String)
     */
    public void setExtension( String ext )
    {
        setMetadata( METADATA_EXT, ext );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#getHashMd5()
     */
    public String getHashMd5()
    {
        return getMetadata( METADATA_HASH_MD5 );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#setHashMd5(java.lang.String)
     */
    public void setHashMd5( String md5hash )
    {
        setMetadata( METADATA_HASH_MD5, md5hash );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#getHashMd5AsBytes()
     */
    public byte[] getHashMd5AsBytes()
    {
        String md5hash = getHashMd5();
        if ( md5hash != null )
        {
            return decodeHex( md5hash );
        }
        else
        {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#setHashMd5AsBytes(byte[])
     */
    public void setHashMd5AsBytes( byte[] md5hash )
    {
        if ( md5hash != null )
        {
            setHashMd5( encodeHex( md5hash ) );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#getHashSha1()
     */
    public String getHashSha1()
    {
        return getMetadata( METADATA_HASH_SHA1 );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#setHashSha1(java.lang.String)
     */
    public void setHashSha1( String sha1hash )
    {
        setMetadata( METADATA_HASH_SHA1, sha1hash );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#getHashSha1AsBytes()
     */
    public byte[] getHashSha1AsBytes()
    {
        String sha1hash = getHashSha1();
        if ( sha1hash != null )
        {
            return decodeHex( sha1hash );
        }
        else
        {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ItemProperties#setHashSha1AsBytes(byte[])
     */
    public void setHashSha1AsBytes( byte[] sha1hash )
    {
        if ( sha1hash != null )
        {
            setHashSha1( encodeHex( sha1hash ) );
        }
    }

    /**
     * Decode hex.
     * 
     * @param hexEncoded the hex encoded
     * 
     * @return the byte[]
     */
    protected byte[] decodeHex( String hexEncoded )
    {
        try
        {
            return Hex.decodeHex( hexEncoded.toCharArray() );
        }
        catch ( DecoderException ex )
        {
            return null;
        }
    }

    /**
     * Encode hex.
     * 
     * @param data the data
     * 
     * @return the string
     */
    protected String encodeHex( byte[] data )
    {
        return new String( Hex.encodeHex( data ) );
    }
    
}
