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
package org.abstracthorizon.proximity.storage.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.storage.StorageException;
import org.apache.commons.io.FilenameUtils;

// TODO: Auto-generated Javadoc
/**
 * Read-only storage implemented on plain file system.
 * <p>
 * This implementation may be metadataAware, thus storing all metadata in the configured metadata dir actual artifacts
 * in the configured storage dir.
 * 
 * @author cstamas
 */
public class ReadOnlyFileSystemStorage
    extends AbstractLocalStorage
{

    /** The metadata aware. */
    private boolean metadataAware = true;

    /** The storage dir file. */
    private File storageDirFile = null;

    /** The metadata dir file. */
    private File metadataDirFile = null;

    /**
     * Gets the metadata dir file.
     * 
     * @return the metadata dir file
     */
    public File getMetadataDirFile()
    {
        return metadataDirFile;
    }

    /**
     * Sets the metadata dir file.
     * 
     * @param metadataDirFile the new metadata dir file
     */
    public void setMetadataDirFile( File metadataDirFile )
    {
        if ( !metadataDirFile.exists() )
        {
            if ( !metadataDirFile.mkdirs() )
            {
                throw new IllegalArgumentException( "Cannot create directories " + metadataDirFile.getAbsolutePath() );
            }
        }
        if ( metadataDirFile.isDirectory() )
        {
            this.metadataDirFile = metadataDirFile;
        }
        else
        {
            throw new IllegalArgumentException( "The " + metadataDirFile.getAbsolutePath() + " is not a directory!" );
        }
    }

    /**
     * Gets the storage dir file.
     * 
     * @return the storage dir file
     */
    public File getStorageDirFile()
    {
        return storageDirFile;
    }

    /**
     * Sets the storage dir file.
     * 
     * @param storageDirFile the new storage dir file
     */
    public void setStorageDirFile( File storageDirFile )
    {
        if ( !storageDirFile.exists() )
        {
            if ( !storageDirFile.mkdirs() )
            {
                throw new IllegalArgumentException( "Cannot create directories " + storageDirFile.getAbsolutePath() );
            }
        }
        if ( storageDirFile.isDirectory() )
        {
            this.storageDirFile = storageDirFile;
        }
        else
        {
            throw new IllegalArgumentException( "The " + storageDirFile.getAbsolutePath() + " is not a directory!" );
        }
    }

    /**
     * Gets the metadata base dir.
     * 
     * @return the metadata base dir
     */
    public File getMetadataBaseDir()
    {
        if ( isMetadataAware() )
        {
            return metadataDirFile;
        }
        else
        {
            throw new IllegalStateException( "The storage is configured as metadata-unaware!" );
        }
    }

    /**
     * Gets the storage base dir.
     * 
     * @return the storage base dir
     */
    public File getStorageBaseDir()
    {
        return storageDirFile;
    }

    // ===================================================================================================
    // Local Storage iface

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.storage.local.LocalStorage#isMetadataAware()
     */
    public boolean isMetadataAware()
    {
        return metadataAware;
    }

    /**
     * Sets the metadata aware.
     * 
     * @param metadataAware the new metadata aware
     */
    public void setMetadataAware( boolean metadataAware )
    {
        this.metadataAware = metadataAware;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.storage.Storage#containsItem(java.lang.String)
     */
    public boolean containsItem( String path )
    {
        logger.debug( "Checking for existence of {} in {}", path, getStorageBaseDir() );
        return checkForExistence( getStorageBaseDir(), path );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.storage.Storage#retrieveItem(java.lang.String, boolean)
     */
    public Item retrieveItem( String path, boolean propsOnly )
        throws ItemNotFoundException,
            StorageException
    {
        logger.debug( "Retrieving {} from storage directory {}", path, getStorageBaseDir() );
        try
        {
            ItemProperties properties = loadItemProperties( path );
            Item result = new Item();
            result.setProperties( properties );
            if ( !propsOnly )
            {
                File target = new File( getStorageBaseDir(), path );
                if ( target.isFile() )
                {
                    result.setStream( new LazyFileInputStream( target ) );
                    properties.setSize( target.length() );
                    properties.setLastModified( new Date( target.lastModified() ) );
                }
            }
            return result;
        }
        catch ( FileNotFoundException ex )
        {
            throw new ItemNotFoundException( "FileNotFound in FS storage [" + getStorageBaseDir() + "] for path ["
                + path + "]" );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.storage.local.AbstractLocalStorage#listItems(java.lang.String)
     */
    public List listItems( String path )
    {
        logger.debug( "Listing {} in storage directory {}", path, getStorageBaseDir() );
        List result = new ArrayList();
        File target = new File( getStorageBaseDir(), path );
        if ( target.exists() )
        {
            if ( target.isDirectory() )
            {
                File[] files = target.listFiles();
                for ( int i = 0; i < files.length; i++ )
                {
                    ItemProperties item = loadItemProperties( FilenameUtils.concat( path, files[i].getName() ) );
                    result.add( item );
                }
            }
            else
            {
                ItemProperties item = loadItemProperties( path );
                result.add( item );
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.storage.local.LocalStorage#recreateMetadata(java.util.Map)
     */
    public void recreateMetadata( Map extraProps )
        throws StorageException
    {

        // issue #44, we will not delete existing metadata,
        // instead, we will force to "recreate" those the properties factory
        // eventually appending it with new ones.

        int processed = 0;
        Stack stack = new Stack();
        List dir = listItems( ItemProperties.PATH_ROOT );
        stack.push( dir );
        while ( !stack.isEmpty() )
        {
            dir = (List) stack.pop();
            for ( Iterator i = dir.iterator(); i.hasNext(); )
            {
                ItemProperties ip = (ItemProperties) i.next();

                if ( ip.isDirectory() )
                {
                    List subdir = listItems( ip.getPath() );
                    stack.push( subdir );
                }
                else
                {
                    logger.debug( "**** {}", ip.getPath() );
                    File target = new File( getStorageBaseDir(), ip.getPath() );
                    ItemProperties nip = getProxiedItemPropertiesFactory().expandItemProperties(
                        ip.getPath(),
                        target,
                        false );
                    if ( extraProps != null )
                    {
                        nip.getAllMetadata().putAll( extraProps );
                    }
                    storeItemProperties( nip );
                    processed++;
                }
            }
        }
        logger.info( "Recreated metadata on {} items.", Integer.toString( processed ) );
    }

    /**
     * Check for existence.
     * 
     * @param baseDir the base dir
     * @param path the path
     * 
     * @return true, if successful
     */
    protected boolean checkForExistence( File baseDir, String path )
    {
        File target = new File( baseDir, path );
        return target.exists();
    }

    /**
     * Load item properties.
     * 
     * @param path the path
     * 
     * @return the item properties
     */
    protected ItemProperties loadItemProperties( String path )
    {
        File target = new File( getStorageBaseDir(), path );
        File mdTarget = new File( getMetadataBaseDir(), path );
        ItemProperties ip = getProxiedItemPropertiesFactory().expandItemProperties( path, target, true );
        if ( target.isFile() && isMetadataAware() )
        {
            try
            {
                if ( mdTarget.exists() && mdTarget.isFile() )
                {
                    Properties metadata = new Properties();
                    FileInputStream fis = new FileInputStream( mdTarget );
                    try
                    {
                        metadata.load( fis );
                    }
                    finally
                    {
                        fis.close();
                    }
                    ip.getAllMetadata().putAll( metadata );
                }
                else
                {
                    logger.debug( "No metadata exists for [{}] on path [{}] " + "-- recreating the default ones. "
                        + "Reindex operation may be needed to recreate/reindex them completely.", ip.getName(), ip
                        .getDirectoryPath() );
                    ip = getProxiedItemPropertiesFactory().expandItemProperties( path, target, true );
                    storeItemProperties( ip );
                }
            }
            catch ( IOException ex )
            {
                logger.error( "Got IOException during metadata retrieval.", ex );
            }
        }
        return ip;
    }

    /**
     * Store item properties.
     * 
     * @param iProps the i props
     * 
     * @throws StorageException the storage exception
     */
    protected void storeItemProperties( ItemProperties iProps )
        throws StorageException
    {
        if ( !iProps.isFile() )
        {
            throw new IllegalArgumentException( "Only files can be stored!" );
        }
        logger.debug( "Storing metadata in [{}] in storage directory {}", iProps.getPath(), getMetadataBaseDir() );
        try
        {

            File target = new File( getMetadataBaseDir(), iProps.getPath() );
            target.getParentFile().mkdirs();
            Properties metadata = new Properties();
            metadata.putAll( iProps.getAllMetadata() );
            FileOutputStream os = new FileOutputStream( target );
            try
            {
                metadata.store( os, "Written by " + this.getClass() );
                os.flush();
            }
            finally
            {
                os.close();
            }
            target.setLastModified( iProps.getLastModified().getTime() );

        }
        catch ( IOException ex )
        {
            throw new StorageException( "IOException in FS storage " + getMetadataBaseDir(), ex );
        }
    }

}
