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
package org.abstracthorizon.proximity.webapp.webdav;

import java.io.InputStream;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.webdav.WebdavStore;
import net.sf.webdav.exceptions.WebdavException;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.HashMapItemPropertiesImpl;
import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.ProximityException;
import org.abstracthorizon.proximity.ProximityRequest;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class ProximityWebdavStorageAdapter.
 */
public class ProximityWebdavStorageAdapter
    implements WebdavStore
{

    /** The logger. */
    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    /** The proximity. */
    private Proximity proximity;

    /** The created but not stored resources. */
    private HashMap createdButNotStoredResources = new HashMap();

    /**
     * Gets the proximity.
     * 
     * @return the proximity
     */
    public Proximity getProximity()
    {
        return proximity;
    }

    /**
     * Sets the proximity.
     * 
     * @param proximity the new proximity
     */
    public void setProximity( Proximity proximity )
    {
        this.proximity = proximity;
    }

    /* (non-Javadoc)
     * @see net.sf.webdav.WebdavStore#begin(java.security.Principal)
     */
    public void begin( Principal principal )
        throws WebdavException
    {
        // nothing
    }

    /* (non-Javadoc)
     * @see net.sf.webdav.WebdavStore#checkAuthentication()
     */
    public void checkAuthentication()
        throws SecurityException
    {
        // nothing
    }

    /* (non-Javadoc)
     * @see net.sf.webdav.WebdavStore#commit()
     */
    public void commit()
        throws WebdavException
    {
        // nothing
    }

    /* (non-Javadoc)
     * @see net.sf.webdav.WebdavStore#rollback()
     */
    public void rollback()
        throws WebdavException
    {
        // nothing
    }

    /* (non-Javadoc)
     * @see net.sf.webdav.WebdavStore#createFolder(java.lang.String)
     */
    public void createFolder( String folderUri )
        throws WebdavException
    {
        // nothing
    }

    /* (non-Javadoc)
     * @see net.sf.webdav.WebdavStore#createResource(java.lang.String)
     */
    public void createResource( String resourceUri )
        throws WebdavException
    {
        createdButNotStoredResources.put( resourceUri, Boolean.TRUE );
        /*
         * try { ProximityRequest request = createRequest(resourceUri, false); Item item = new Item(); ItemProperties
         * itemProperties = new HashMapItemPropertiesImpl();
         * itemProperties.setDirectoryPath(FilenameUtils.separatorsToUnix(FilenameUtils
         * .getFullPathNoEndSeparator(resourceUri))); itemProperties.setName(FilenameUtils.getName(resourceUri));
         * itemProperties.setLastModified(new Date()); itemProperties.setDirectory(false); itemProperties.setFile(true);
         * item.setStream(new ByteArrayInputStream(new byte[0])); item.setProperties(itemProperties);
         * proximity.storeItem(request, item); } catch (ProximityException ex) { logger.error("Proximity throw
         * exception.", ex); throw new WebdavException("Proximity throw " + ex.getMessage()); }
         */
    }

    /* (non-Javadoc)
     * @see net.sf.webdav.WebdavStore#getChildrenNames(java.lang.String)
     */
    public String[] getChildrenNames( String folderUri )
        throws WebdavException
    {
        try
        {
            if ( objectExists( folderUri ) && isFolder( folderUri ) )
            {
                ProximityRequest request = new ProximityRequest( folderUri );
                List itemList = proximity.listItems( request );
                String[] result = new String[itemList.size()];
                for ( int i = 0; i < itemList.size(); i++ )
                {
                    result[i] = ( (ItemProperties) itemList.get( i ) ).getName();
                }
                return result;
            }
            else
            {
                return null;
            }
        }
        catch ( ProximityException ex )
        {
            logger.error( "Proximity thrown exception", ex );
            throw new WebdavException( "Proximity reported exception " + ex.getMessage() );
        }
    }

    /* (non-Javadoc)
     * @see net.sf.webdav.WebdavStore#getCreationDate(java.lang.String)
     */
    public Date getCreationDate( String uri )
        throws WebdavException
    {
        return getLastModified( uri );
    }

    /* (non-Javadoc)
     * @see net.sf.webdav.WebdavStore#getLastModified(java.lang.String)
     */
    public Date getLastModified( String uri )
        throws WebdavException
    {
        Item result = makeRequest( uri, true );
        if ( result == null )
        {
            return new Date();
        }
        if ( result.getProperties().getLastModified() == null )
        {
            return new Date();
        }
        else
        {
            return result.getProperties().getLastModified();
        }
    }

    /* (non-Javadoc)
     * @see net.sf.webdav.WebdavStore#getResourceContent(java.lang.String)
     */
    public InputStream getResourceContent( String resourceUri )
        throws WebdavException
    {
        Item result = makeRequest( resourceUri, true );
        if ( result == null )
        {
            throw new WebdavException( "URI " + resourceUri + " not found" );
        }
        return result.getStream();
    }

    /* (non-Javadoc)
     * @see net.sf.webdav.WebdavStore#getResourceLength(java.lang.String)
     */
    public long getResourceLength( String resourceUri )
        throws WebdavException
    {
        Item result = makeRequest( resourceUri, true );
        if ( result == null )
        {
            throw new WebdavException( "URI " + resourceUri + " not found" );
        }
        return result.getProperties().getSize();
    }

    /* (non-Javadoc)
     * @see net.sf.webdav.WebdavStore#isFolder(java.lang.String)
     */
    public boolean isFolder( String uri )
        throws WebdavException
    {
        Item result = makeRequest( uri, true );
        if ( result == null )
        {
            return false;
        }
        return result.getProperties().isDirectory();
    }

    /* (non-Javadoc)
     * @see net.sf.webdav.WebdavStore#isResource(java.lang.String)
     */
    public boolean isResource( String uri )
        throws WebdavException
    {
        Item result = makeRequest( uri, true );
        if ( result == null )
        {
            return false;
        }
        return result.getProperties().isFile();
    }

    /* (non-Javadoc)
     * @see net.sf.webdav.WebdavStore#objectExists(java.lang.String)
     */
    public boolean objectExists( String uri )
        throws WebdavException
    {
        Item result = makeRequest( uri, true );
        return result != null;
    }

    /* (non-Javadoc)
     * @see net.sf.webdav.WebdavStore#removeObject(java.lang.String)
     */
    public void removeObject( String uri )
        throws WebdavException
    {
        try
        {
            ProximityRequest request = createRequest( uri, false );
            proximity.deleteItem( request );
        }
        catch ( ProximityException ex )
        {
            logger.info( "Proximity throw exception, IGNORING it.", ex );
        }
    }

    /* (non-Javadoc)
     * @see net.sf.webdav.WebdavStore#setResourceContent(java.lang.String, java.io.InputStream, java.lang.String, java.lang.String)
     */
    public void setResourceContent( String resourceUri, InputStream content, String contentType,
        String characterEncoding )
        throws WebdavException
    {
        try
        {
            createdButNotStoredResources.remove( resourceUri );
            ProximityRequest request = createRequest( resourceUri, false );
            Item item = new Item();
            ItemProperties itemProperties = new HashMapItemPropertiesImpl();
            itemProperties.setDirectoryPath( FilenameUtils.separatorsToUnix( FilenameUtils
                .getFullPathNoEndSeparator( resourceUri ) ) );
            itemProperties.setName( FilenameUtils.getName( resourceUri ) );
            itemProperties.setLastModified( new Date() );
            itemProperties.setDirectory( false );
            itemProperties.setFile( true );
            item.setProperties( itemProperties );
            item.setStream( content );
            proximity.storeItem( request, item );
        }
        catch ( ProximityException ex )
        {
            logger.error( "Proximity throw exception.", ex );
            throw new WebdavException( "Proximity throw " + ex.getMessage() );
        }
    }

    /**
     * Creates the request.
     * 
     * @param uri the uri
     * @param propsOnly the props only
     * 
     * @return the proximity request
     */
    protected ProximityRequest createRequest( String uri, boolean propsOnly )
    {
        ProximityRequest req = new ProximityRequest( uri );
        req.setPropertiesOnly( propsOnly );
        return req;
    }

    /**
     * Make request.
     * 
     * @param uri the uri
     * @param propsOnly the props only
     * 
     * @return the item
     * 
     * @throws WebdavException the webdav exception
     */
    protected Item makeRequest( String uri, boolean propsOnly )
        throws WebdavException
    {
        try
        {
            ProximityRequest request = createRequest( uri, propsOnly );
            Item item = proximity.retrieveItem( request );
            return item;
        }
        catch ( ItemNotFoundException ex )
        {
            return null;
        }
        catch ( AccessDeniedException ex )
        {
            return null;
        }
        catch ( ProximityException ex )
        {
            logger.error( "Proximity thrown exception", ex );
            throw new WebdavException( "Proximity reported exception " + ex.getMessage() );
        }
    }

}
