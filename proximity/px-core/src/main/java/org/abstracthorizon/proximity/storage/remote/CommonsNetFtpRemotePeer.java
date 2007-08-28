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
package org.abstracthorizon.proximity.storage.remote;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;

import org.abstracthorizon.proximity.HashMapItemPropertiesImpl;
import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.storage.StorageException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;

// TODO: Auto-generated Javadoc
/**
 * Naive remote storage implementation based on Apache Commons Net FTPClient's library. It uses FTP LIST method for
 * existence checking and FTP GET for item retrieval. B R O K E N
 * 
 * @author cstamas
 */
public class CommonsNetFtpRemotePeer
    extends AbstractRemoteStorage
{

    // TODO: CommonsNetFtpRemotePeer is curently BROKEN!

    /** The ftp client config. */
    private FTPClientConfig ftpClientConfig = new FTPClientConfig( FTPClientConfig.SYST_UNIX );

    /** The connection timeout. */
    private int connectionTimeout = 5000;

    /** The retrieval retry count. */
    private int retrievalRetryCount = 3;

    /** The proxy host. */
    private String proxyHost = null;

    /** The proxy port. */
    private int proxyPort = 8080;

    /** The proxy username. */
    private String proxyUsername = null;

    /** The proxy password. */
    private String proxyPassword = null;

    /** The ftp username. */
    private String ftpUsername = null;

    /** The ftp password. */
    private String ftpPassword = null;

    /**
     * Gets the ftp client config.
     * 
     * @return the ftp client config
     */
    public FTPClientConfig getFtpClientConfig()
    {
        return ftpClientConfig;
    }

    /**
     * Sets the ftp client config.
     * 
     * @param ftpClientConfig the new ftp client config
     */
    public void setFtpClientConfig( FTPClientConfig ftpClientConfig )
    {
        this.ftpClientConfig = ftpClientConfig;
    }

    /**
     * Gets the ftp username.
     * 
     * @return the ftp username
     */
    public String getFtpUsername()
    {
        return ftpUsername;
    }

    /**
     * Sets the ftp username.
     * 
     * @param ftpUsername the new ftp username
     */
    public void setFtpUsername( String ftpUsername )
    {
        this.ftpUsername = ftpUsername;
    }

    /**
     * Gets the ftp password.
     * 
     * @return the ftp password
     */
    public String getFtpPassword()
    {
        return ftpPassword;
    }

    /**
     * Sets the ftp password.
     * 
     * @param ftpPassword the new ftp password
     */
    public void setFtpPassword( String ftpPassword )
    {
        this.ftpPassword = ftpPassword;
    }

    /**
     * Gets the proxy host.
     * 
     * @return the proxy host
     */
    public String getProxyHost()
    {
        return proxyHost;
    }

    /**
     * Sets the proxy host.
     * 
     * @param proxyHost the new proxy host
     */
    public void setProxyHost( String proxyHost )
    {
        this.proxyHost = proxyHost;
    }

    /**
     * Gets the proxy password.
     * 
     * @return the proxy password
     */
    public String getProxyPassword()
    {
        return proxyPassword;
    }

    /**
     * Sets the proxy password.
     * 
     * @param proxyPassword the new proxy password
     */
    public void setProxyPassword( String proxyPassword )
    {
        this.proxyPassword = proxyPassword;
    }

    /**
     * Gets the proxy port.
     * 
     * @return the proxy port
     */
    public int getProxyPort()
    {
        return proxyPort;
    }

    /**
     * Sets the proxy port.
     * 
     * @param proxyPort the new proxy port
     */
    public void setProxyPort( int proxyPort )
    {
        this.proxyPort = proxyPort;
    }

    /**
     * Gets the proxy username.
     * 
     * @return the proxy username
     */
    public String getProxyUsername()
    {
        return proxyUsername;
    }

    /**
     * Sets the proxy username.
     * 
     * @param proxyUsername the new proxy username
     */
    public void setProxyUsername( String proxyUsername )
    {
        this.proxyUsername = proxyUsername;
    }

    /**
     * Sets the connection timeout.
     * 
     * @param connectionTimeout the new connection timeout
     */
    public void setConnectionTimeout( int connectionTimeout )
    {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Gets the connection timeout.
     * 
     * @return the connection timeout
     */
    public int getConnectionTimeout()
    {
        return connectionTimeout;
    }

    /**
     * Gets the retrieval retry count.
     * 
     * @return the retrieval retry count
     */
    public int getRetrievalRetryCount()
    {
        return retrievalRetryCount;
    }

    /**
     * Sets the retrieval retry count.
     * 
     * @param retrievalRetryCount the new retrieval retry count
     */
    public void setRetrievalRetryCount( int retrievalRetryCount )
    {
        this.retrievalRetryCount = retrievalRetryCount;
    }

    /**
     * Contains item properties.
     * 
     * @param path the path
     * 
     * @return true, if successful
     */
    public boolean containsItemProperties( String path )
    {
        return containsItem( path );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.storage.Storage#containsItem(java.lang.String)
     */
    public boolean containsItem( String path )
        throws StorageException
    {
        FTPClient client = null;
        try
        {
            client = getFTPClient();
            try
            {
                if ( client
                    .changeWorkingDirectory( concatPaths( getRemoteUrl().getPath(), FilenameUtils.getPath( path ) ) ) )
                {
                    FTPFile[] fileList = client.listFiles( FilenameUtils.getName( path ) );
                    if ( fileList.length == 1 )
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }
            catch ( IOException ex )
            {
                throw new StorageException( "Cannot execute FTP operation on remote peer.", ex );
            }
        }
        finally
        {
            try
            {
                if ( client.isConnected() )
                {
                    client.disconnect();
                }
            }
            catch ( IOException ex )
            {
                logger.warn( "Could not disconnect FTPClient", ex );
            }
        }
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
        String originatingUrlString = getAbsoluteUrl( path );
        FTPClient client = null;
        try
        {
            client = getFTPClient();
            try
            {
                if ( client
                    .changeWorkingDirectory( concatPaths( getRemoteUrl().getPath(), FilenameUtils.getPath( path ) ) ) )
                {
                    FTPFile[] fileList = client.listFiles( FilenameUtils.getName( path ) );
                    if ( fileList.length == 1 )
                    {
                        FTPFile ftpFile = fileList[0];
                        ItemProperties properties = constructItemPropertiesFromGetResponse(
                            path,
                            originatingUrlString,
                            ftpFile );
                        Item result = new Item();
                        if ( properties.isFile() )
                        {
                            // TODO: Solve this in a better way
                            File tmpFile = File.createTempFile( FilenameUtils.getName( path ), null );
                            tmpFile.deleteOnExit();
                            FileOutputStream fos = new FileOutputStream( tmpFile );
                            try
                            {
                                client.retrieveFile( FilenameUtils.getName( path ), fos );
                                fos.flush();
                            }
                            finally
                            {
                                fos.close();
                            }
                            result.setStream( new DeleteOnCloseFileInputStream( tmpFile ) );
                        }
                        else
                        {
                            result.setStream( null );
                        }
                        result.setProperties( properties );
                        return result;
                    }
                    else
                    {
                        throw new ItemNotFoundException( "Item " + path + " not found in FTP remote peer of "
                            + getRemoteUrl() );
                    }
                }
                else
                {
                    throw new ItemNotFoundException( "Path " + FilenameUtils.getPath( path )
                        + " not found in FTP remote peer of " + getRemoteUrl() );
                }
            }
            catch ( IOException ex )
            {
                throw new StorageException( "Cannot execute FTP operation on remote peer.", ex );
            }
        }
        finally
        {
            try
            {
                if ( client.isConnected() )
                {
                    client.disconnect();
                }
            }
            catch ( IOException ex )
            {
                logger.warn( "Could not disconnect FTPClient", ex );
            }
        }
    }

    /**
     * Gets the FTP client.
     * 
     * @return the FTP client
     */
    public FTPClient getFTPClient()
    {
        try
        {
            logger.info( "Creating CommonsNetFTPClient instance" );
            FTPClient ftpc = new FTPClient();
            ftpc.configure( ftpClientConfig );
            ftpc.connect( getRemoteUrl().getHost() );
            ftpc.login( getFtpUsername(), getFtpPassword() );
            ftpc.setFileType( FTPClient.BINARY_FILE_TYPE );
            return ftpc;
        }
        catch ( SocketException ex )
        {
            throw new StorageException( "Got SocketException while creating FTPClient", ex );
        }
        catch ( IOException ex )
        {
            throw new StorageException( "Got IOException while creating FTPClient", ex );
        }
    }

    /**
     * Concat paths.
     * 
     * @param path1 the path1
     * @param path2 the path2
     * 
     * @return the string
     */
    protected String concatPaths( String path1, String path2 )
    {
        String result = FilenameUtils.concat( path1, path2 );
        return FilenameUtils.separatorsToUnix( result );
    }

    /**
     * Construct item properties from get response.
     * 
     * @param path the path
     * @param originatingUrlString the originating url string
     * @param remoteFile the remote file
     * 
     * @return the item properties
     * 
     * @throws MalformedURLException the malformed URL exception
     */
    protected ItemProperties constructItemPropertiesFromGetResponse( String path, String originatingUrlString,
        FTPFile remoteFile )
        throws MalformedURLException
    {
        URL originatingUrl = new URL( originatingUrlString );
        ItemProperties result = new HashMapItemPropertiesImpl();
        result.setDirectoryPath( FilenameUtils.separatorsToUnix( FilenameUtils.getPath( FilenameUtils
            .getFullPath( path ) ) ) );
        result.setDirectory( remoteFile.isDirectory() );
        result.setFile( remoteFile.isFile() );
        result.setLastModified( remoteFile.getTimestamp().getTime() );
        result.setName( FilenameUtils.getName( originatingUrl.getPath() ) );
        if ( result.isFile() )
        {
            result.setSize( remoteFile.getSize() );
        }
        else
        {
            result.setSize( 0 );
        }
        result.setRemoteUrl( originatingUrl.toString() );
        return result;
    }

}
