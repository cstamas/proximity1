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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.storage.StorageException;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

// TODO: Auto-generated Javadoc
/**
 * Naive remote storage implementation based on Apache Commons HttpClient's library. It uses HTTP HEAD method for
 * existence checking and HTTP GET for item retrieval.
 * 
 * @author cstamas
 */
public class CommonsHttpClientRemotePeer
    extends AbstractRemoteStorage
{

    /** The http retry handler. */
    private HttpMethodRetryHandler httpRetryHandler = null;

    /** The http configuration. */
    private HostConfiguration httpConfiguration = null;

    /** The http client. */
    private HttpClient httpClient = null;

    /** The query string. */
    private String queryString = null;

    /** The user agent string. */
    private String userAgentString = null;

    /** The connection timeout. */
    private int connectionTimeout = 5000;

    /** The retrieval retry count. */
    private int retrievalRetryCount = 3;

    /** The proxy host. */
    private String proxyHost = null;

    /** The proxy port. */
    private int proxyPort = 8080;

    /** The username. */
    private String username = null;

    /** The password. */
    private String password = null;

    /** The ntlm domain. */
    private String ntlmDomain = null;

    /** The ntlm host. */
    private String ntlmHost = null;

    /** The proxy username. */
    private String proxyUsername = null;

    /** The proxy password. */
    private String proxyPassword = null;

    /** The proxy ntlm domain. */
    private String proxyNtlmDomain = null;

    /** The proxy ntlm host. */
    private String proxyNtlmHost = null;

    /**
     * Gets the user agent string.
     * 
     * @return the user agent string
     */
    public String getUserAgentString()
    {
        return userAgentString;
    }

    /**
     * Sets the user agent string.
     * 
     * @param userAgentString the new user agent string
     */
    public void setUserAgentString( String userAgentString )
    {
        this.userAgentString = userAgentString;
    }

    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Sets the password.
     * 
     * @param password the new password
     */
    public void setPassword( String password )
    {
        this.password = password;
    }

    /**
     * Gets the username.
     * 
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Sets the username.
     * 
     * @param username the new username
     */
    public void setUsername( String username )
    {
        this.username = username;
    }

    /**
     * Gets the ntlm domain.
     * 
     * @return the ntlm domain
     */
    public String getNtlmDomain()
    {
        return ntlmDomain;
    }

    /**
     * Sets the ntlm domain.
     * 
     * @param ntlmDomain the new ntlm domain
     */
    public void setNtlmDomain( String ntlmDomain )
    {
        this.ntlmDomain = ntlmDomain;
    }

    /**
     * Gets the ntlm host.
     * 
     * @return the ntlm host
     */
    public String getNtlmHost()
    {
        return ntlmHost;
    }

    /**
     * Sets the ntlm host.
     * 
     * @param ntlmHost the new ntlm host
     */
    public void setNtlmHost( String ntlmHost )
    {
        this.ntlmHost = ntlmHost;
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
     * Gets the proxy ntlm domain.
     * 
     * @return the proxy ntlm domain
     */
    public String getProxyNtlmDomain()
    {
        return proxyNtlmDomain;
    }

    /**
     * Sets the proxy ntlm domain.
     * 
     * @param proxyNtlmDomain the new proxy ntlm domain
     */
    public void setProxyNtlmDomain( String proxyNtlmDomain )
    {
        this.proxyNtlmDomain = proxyNtlmDomain;
    }

    /**
     * Gets the proxy ntlm host.
     * 
     * @return the proxy ntlm host
     */
    public String getProxyNtlmHost()
    {
        return proxyNtlmHost;
    }

    /**
     * Sets the proxy ntlm host.
     * 
     * @param proxyNtlmHost the new proxy ntlm host
     */
    public void setProxyNtlmHost( String proxyNtlmHost )
    {
        this.proxyNtlmHost = proxyNtlmHost;
    }

    /**
     * Gets the query string.
     * 
     * @return the query string
     */
    public String getQueryString()
    {
        return queryString;
    }

    /**
     * Sets the query string.
     * 
     * @param queryString the new query string
     */
    public void setQueryString( String queryString )
    {
        this.queryString = queryString;
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
    {
        HeadMethod head = new HeadMethod( getAbsoluteUrl( path ) );
        int response = executeMethod( head );
        return response == HttpStatus.SC_OK;
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
        // TODO: propsOnly is ignored, use HTTP HEAD?
        String originatingUrlString = getAbsoluteUrl( path );
        GetMethod get = new GetMethod( originatingUrlString );
        try
        {
            try
            {
                logger.info( "Fetching item [{}] from remote location {}", path, originatingUrlString );
                int response = executeMethod( get );
                if ( response == HttpStatus.SC_OK )
                {
                    // ProxiedItemProperties properties =
                    // constructItemPropertiesFromGetResponse(path,
                    // originatingUrlString, get);

                    Item result = new Item();
                    ItemProperties ip = null;

                    // is it a file?
                    // TODO: fix for #93 ticket?
                    // Asking GET methods getPath() after execution will
                    // result
                    // in ACTUAL
                    // path after eventual redirection. So, it will end with
                    // "/"
                    // if it is a dir.
                    if ( !get.getPath().endsWith( ItemProperties.PATH_SEPARATOR ) )
                    {
                        // if (get.getResponseHeader("last-modified") != null) {
                        File tmpFile = File.createTempFile( FilenameUtils.getName( path ), null );
                        tmpFile.deleteOnExit();
                        FileOutputStream fos = new FileOutputStream( tmpFile );
                        try
                        {
                            InputStream is = get.getResponseBodyAsStream();
                            if ( get.getResponseHeader( "Content-Encoding" ) != null
                                && "gzip".equals( get.getResponseHeader( "Content-Encoding" ).getValue() ) )
                            {
                                is = new GZIPInputStream( is );
                            }

                            IOUtils.copy( is, fos );
                            fos.flush();
                        }
                        finally
                        {
                            fos.close();
                        }
                        tmpFile.setLastModified( makeDateFromHeader( get.getResponseHeader( "last-modified" ) ) );
                        ip = getProxiedItemPropertiesFactory().expandItemProperties( path, tmpFile, true );
                        result.setStream( new DeleteOnCloseFileInputStream( tmpFile ) );
                    }
                    else
                    {
                        // TODO: dirty hack, I am creating a dir named after the
                        // directory retrieval just to get item properties!!!
                        // Fix this!
                        File tmpdir = new File( System.getProperty( "java.io.tmpdir" ), FilenameUtils.getName( path ) );
                        tmpdir.mkdir();
                        ip = getProxiedItemPropertiesFactory().expandItemProperties( path, tmpdir, true );
                        tmpdir.delete();
                        result.setStream( null );
                    }
                    result.setProperties( ip );
                    result.getProperties().setRemoteUrl( originatingUrlString );
                    return result;
                }
                else
                {
                    if ( response == HttpStatus.SC_NOT_FOUND )
                    {
                        throw new ItemNotFoundException( path );
                    }
                    else
                    {
                        throw new StorageException( "The method execution returned result code " + response );
                    }
                }
            }
            catch ( MalformedURLException ex )
            {
                throw new StorageException( "The path " + path + " is malformed!", ex );
            }
            catch ( IOException ex )
            {
                throw new StorageException( "IO Error during response stream handling!", ex );
            }
        }
        finally
        {
            get.releaseConnection();
        }
    }

    /**
     * Gets the http client.
     * 
     * @return the http client
     */
    public HttpClient getHttpClient()
    {
        if ( httpClient == null )
        {
            logger.info( "Creating CommonsHttpClient instance" );
            httpRetryHandler = new DefaultHttpMethodRetryHandler( retrievalRetryCount, true );
            httpClient = new HttpClient( new MultiThreadedHttpConnectionManager() );
            httpClient.getParams().setConnectionManagerTimeout( getConnectionTimeout() );

            httpConfiguration = httpClient.getHostConfiguration();

            // BASIC and DIGEST auth only
            if ( getUsername() != null )
            {

                List authPrefs = new ArrayList( 2 );
                authPrefs.add( AuthPolicy.DIGEST );
                authPrefs.add( AuthPolicy.BASIC );

                if ( getNtlmDomain() != null )
                {
                    // Using NTLM auth, adding it as first in policies
                    authPrefs.add( 0, AuthPolicy.NTLM );

                    logger.info(
                        "... authentication setup for NTLM domain {}, username {}",
                        getNtlmDomain(),
                        getUsername() );
                    httpConfiguration.setHost( getNtlmHost() );

                    httpClient.getState().setCredentials(
                        AuthScope.ANY,
                        new NTCredentials( getUsername(), getPassword(), getNtlmHost(), getNtlmDomain() ) );
                }
                else
                {

                    // Using Username/Pwd auth, will not add NTLM
                    logger.info(
                        "... setting authentication setup for remote peer {}, with username {}",
                        getRemoteUrl(),
                        getUsername() );

                    httpClient.getState().setCredentials(
                        AuthScope.ANY,
                        new UsernamePasswordCredentials( getUsername(), getPassword() ) );

                }
                httpClient.getParams().setParameter( AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs );
            }

            if ( getProxyHost() != null )
            {
                logger.info( "... proxy setup with host {}", getProxyHost() );
                httpConfiguration.setProxy( getProxyHost(), getProxyPort() );

                if ( getProxyUsername() != null )
                {

                    List authPrefs = new ArrayList( 2 );
                    authPrefs.add( AuthPolicy.DIGEST );
                    authPrefs.add( AuthPolicy.BASIC );

                    if ( getProxyNtlmDomain() != null )
                    {

                        // Using NTLM auth, adding it as first in policies
                        authPrefs.add( 0, AuthPolicy.NTLM );

                        if ( getNtlmHost() != null )
                        {
                            logger.warn( "... CommonsHttpClient is unable to use NTLM auth scheme\n"
                                + " for BOTH server side and proxy side authentication!\n"
                                + " You MUST reconfigure server side auth and use BASIC/DIGEST scheme\n"
                                + " if you have to use NTLM proxy, since otherwise it will not work!\n"
                                + " *** SERVER SIDE AUTH OVERRIDDEN" );
                        }
                        logger.info(
                            "... proxy authentication setup for NTLM domain {}, username {}",
                            getProxyNtlmDomain(),
                            getProxyUsername() );
                        httpConfiguration.setHost( getProxyNtlmHost() );

                        httpClient.getState().setProxyCredentials(
                            AuthScope.ANY,
                            new NTCredentials(
                                getProxyUsername(),
                                getProxyPassword(),
                                getProxyNtlmHost(),
                                getProxyNtlmDomain() ) );
                    }
                    else
                    {

                        // Using Username/Pwd auth, will not add NTLM
                        logger.info(
                            "... proxy authentication setup for http proxy {}, username {}",
                            getProxyHost(),
                            getProxyUsername() );

                        httpClient.getState().setProxyCredentials(
                            AuthScope.ANY,
                            new UsernamePasswordCredentials( getProxyUsername(), getProxyPassword() ) );

                    }
                    httpClient.getParams().setParameter( AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs );
                }

            }
        }
        return httpClient;
    }

    /**
     * Execute method.
     * 
     * @param method the method
     * 
     * @return the int
     */
    protected int executeMethod( HttpMethod method )
    {
        if ( getUserAgentString() != null )
        {
            method.setRequestHeader( new Header( "user-agent", getUserAgentString() ) );
        }
        method.setRequestHeader( new Header( "accept", "*/*" ) );
        method.setRequestHeader( new Header( "accept-language", "en-us" ) );
        method.setRequestHeader( new Header( "accept-encoding", "gzip, identity" ) );
        method.setRequestHeader( new Header( "connection", "Keep-Alive" ) );
        method.setRequestHeader( new Header( "cache-control", "no-cache" ) );
        // TODO: fix for #93
        // method.setFollowRedirects(isFollowRedirection());
        method.setFollowRedirects( true );
        method.getParams().setParameter( HttpMethodParams.RETRY_HANDLER, httpRetryHandler );
        method.setQueryString( getQueryString() );
        int resultCode = 0;
        try
        {
            resultCode = getHttpClient().executeMethod( httpConfiguration, method );
        }
        catch ( HttpException ex )
        {
            logger.error( "Protocol error while executing " + method.getName() + " method", ex );
        }
        catch ( IOException ex )
        {
            logger.error( "Tranport error while executing " + method.getName() + " method", ex );
        }
        return resultCode;
    }

    /**
     * Make date from header.
     * 
     * @param date the date
     * 
     * @return the long
     */
    protected long makeDateFromHeader( Header date )
    {
        Date result = null;
        if ( date != null )
        {
            try
            {
                result = DateUtil.parseDate( date.getValue() );
            }
            catch ( DateParseException ex )
            {
                logger.warn(
                    "Could not parse date {} because of {}, using system current time as item creation time.",
                    date,
                    ex );
                result = new Date();
            }
            catch ( NullPointerException ex )
            {
                logger.warn( "Parsed date is null, using system current time as item creation time." );
                result = new Date();
            }
        }
        else
        {
            result = new Date();
        }
        return result.getTime();
    }

}
