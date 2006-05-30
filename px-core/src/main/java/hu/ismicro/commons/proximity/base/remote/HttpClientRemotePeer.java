package hu.ismicro.commons.proximity.base.remote;

import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.base.PathHelper;
import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.ProxiedItemProperties;
import hu.ismicro.commons.proximity.base.StorageException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.io.IOUtils;

/**
 * Naive remote storage implementation based on Apache Commons HttpClient's
 * library. It uses HTTP HEAD method for existence checking and HTTP GET for
 * item retrieval.
 * 
 * @author cstamas
 * 
 */
public class HttpClientRemotePeer extends AbstractRemoteStorage {

    private HttpMethodRetryHandler httpRetryHandler = new DefaultHttpMethodRetryHandler();

    private HttpClient httpClient = null;

    private boolean followRedirection = true;

    private String queryString = null;
    
    private int connectionTimeout = 5000;

    private String proxyHost = null;
    
    private int proxyPort = 8080;
    
    private String proxyRealm = null;
    
    private String proxyUsername = null;
    
    private String proxyPassword = null;

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyRealm() {
        return proxyRealm;
    }

    public void setProxyRealm(String proxyRealm) {
        this.proxyRealm = proxyRealm;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setFollowRedirection(boolean followRedirection) {
        this.followRedirection = followRedirection;
    }

    public boolean isFollowRedirection() {
        return followRedirection;
    }

    public boolean containsItemProperties(String path) {
        return containsItem(path);
    }

    public boolean containsItem(String path) {
        HeadMethod head = new HeadMethod(getAbsoluteUrl(path));
        int response = executeMethod(head);
        return response == HttpStatus.SC_OK;
    }

    public ProxiedItemProperties retrieveItemProperties(String path) throws ItemNotFoundException, StorageException {
        String originatingUrlString = getAbsoluteUrl(path);
        HeadMethod method = new HeadMethod(originatingUrlString);
        try {
            try {
                int response = executeMethod(method);
                if (response == HttpStatus.SC_OK) {
                    logger.info("Item " + path + " properties fetched from remote peer of " + getRemoteUrl());
                    return constructItemPropertiesFromGetResponse(path, originatingUrlString, method);
                } else {
                    logger.error("The method execution returned result code " + response);
                    throw new StorageException("The method execution returned result code " + response);
                }
            } catch (MalformedURLException ex) {
                logger.error("The path " + path + " is malformed!", ex);
                throw new StorageException("The method execution got MalformedURLException!", ex);
            }
        } finally {
            method.releaseConnection();
        }
    }

    public ProxiedItem retrieveItem(String path) throws ItemNotFoundException, StorageException {
        String originatingUrlString = getAbsoluteUrl(path);
        GetMethod get = new GetMethod(originatingUrlString);
        try {
            try {
                int response = executeMethod(get);
                if (response == HttpStatus.SC_OK) {
                    logger.info("Item " + path + " fetched from remote peer of " + getRemoteUrl());
                    logger.debug("Constructing ProxiedItemProperties");
                    ProxiedItemProperties properties = constructItemPropertiesFromGetResponse(path,
                            originatingUrlString, get);

                    logger.debug("Constructing ProxiedItem");
                    ProxiedItem result = new ProxiedItem();
                    if (properties.isFile()) {
                        // TODO: Solve this in  a better way
                        File tmpFile = File.createTempFile(PathHelper.getFileName(path), null);
                        FileOutputStream fos = new FileOutputStream(tmpFile);
                        int bytes = IOUtils.copy(get.getResponseBodyAsStream(), fos);
                        fos.flush();
                        fos.close();
                        properties.setSize(bytes); // set the actual size in bytes we received
                        InputStream is = new FileInputStream(tmpFile);
                        result.setStream(is);
                    } else {
                        result.setStream(null);
                    }
                    result.setProperties(properties);
                    return result;
                } else {
                    if (response == HttpStatus.SC_NOT_FOUND) {
                        logger.error("The path " + path + " is not found on " + getRemoteUrl() + "!");
                        throw new ItemNotFoundException(path);
                    } else {
                        logger.error("The method execution returned result code " + response);
                        throw new StorageException("The method execution returned result code " + response);
                    }
                }
            } catch (MalformedURLException ex) {
                logger.error("The path " + path + " is malformed!", ex);
                throw new StorageException("The path " + path + " is malformed!", ex);
            } catch (IOException ex) {
                logger.error("IO Error during response stream handling!", ex);
                throw new StorageException("IO Error during response stream handling!", ex);
            }
        } finally {
            get.releaseConnection();
        }
    }

    public HttpClient getHttpClient() {
        if (httpClient == null) {
            logger.info("Creating CommonsHttpClient instance");
            httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
            httpClient.getParams().setConnectionManagerTimeout(getConnectionTimeout());
            if (getProxyHost() != null) {
                logger.info("... proxy setup with host " + getProxyHost() + ", port "+ getProxyPort());
                httpClient.getHostConfiguration().setProxy(getProxyHost(), getProxyPort());
                if (getProxyRealm() != null) {
                    logger.info("... proxy authenticationsetup for realm " + getProxyRealm() + " and username " + getProxyUsername());
                    httpClient.getState().setProxyCredentials(new AuthScope(proxyHost, proxyPort, proxyRealm), new UsernamePasswordCredentials(proxyUsername, proxyPassword));
                }
            }
        }
        return httpClient;
    }

    protected int executeMethod(HttpMethod method) {
        method.setFollowRedirects(isFollowRedirection());
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, httpRetryHandler);
        method.setQueryString(getQueryString());
        int resultCode = 0;
        try {
            logger.debug("Executing " + method + " on URI " + method.getURI());
            resultCode = getHttpClient().executeMethod(method);
        } catch (HttpException ex) {
            logger.error("Protocol error while executing " + method.getName() + " method with query string "
                    + method.getQueryString(), ex);
        } catch (IOException ex) {
            logger.error("Tranport error while executing " + method.getName() + " method with query string "
                    + method.getQueryString(), ex);
        }
        logger.debug("Received response code [" + resultCode + "] for executing [" + method.getName()
                + "] method with path [" + method.getPath() + "]");
        return resultCode;
    }

    protected Date makeDateFromString(String date) {
        Date result = null;
        if (date != null) {
            try {
                result = DateUtil.parseDate(date);
            } catch (DateParseException ex) {
                logger.warn("Could not parse date " + date + ", using NOW");
                result = new Date();
            }
        } else {
            result = new Date();
        }
        return result;
    }

    protected ProxiedItemProperties constructItemPropertiesFromGetResponse(String path, String originatingUrlString,
            HttpMethod executedMethod) throws MalformedURLException {
        Header locationHeader = executedMethod.getResponseHeader("location");
        Header contentLength = executedMethod.getRequestHeader("content-length");
        Header lastModifiedHeader = executedMethod.getResponseHeader("last-modified");
        if (locationHeader != null) {
            // we may had redirection
            logger.debug("We got location header " + locationHeader.getValue());
            originatingUrlString = locationHeader.getValue();
        }
        URL originatingUrl = new URL(originatingUrlString);

        ProxiedItemProperties result = new ProxiedItemProperties();
        result.setAbsolutePath(PathHelper.changePathLevel(path, PathHelper.PATH_PARENT));
        if (contentLength != null) {
            result.setSize(Long.parseLong(contentLength.getValue()));
        }
        // TODO: ibiblio behaves like this, check for others
        result.setDirectory(lastModifiedHeader == null);
        result.setFile(lastModifiedHeader != null);
        if (lastModifiedHeader != null) {
            result.setLastModified(makeDateFromString(lastModifiedHeader.getValue()));
        } else {
            // get system time
            result.setLastModified(new Date());
        }
        result.setName(PathHelper.getFileName(originatingUrl.getPath()));
        if (result.isFile() && executedMethod instanceof GetMethod) {
            result.setSize(((GetMethod) executedMethod).getResponseContentLength());
        } else {
            result.setSize(0);
        }
        result.setMetadata(ItemProperties.METADATA_ORIGINATING_URL, originatingUrl.toString());
        return result;
    }

}