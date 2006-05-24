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
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
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

    private HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();

    private HttpClient httpClient = new HttpClient(connectionManager);

    private HttpMethodRetryHandler httpRetryHandler = new DefaultHttpMethodRetryHandler();

    private boolean followRedirection = true;

    private String queryString = null;

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    protected int executeMethod(HttpMethod method) {
        method.setFollowRedirects(isFollowRedirection());
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, getHttpRetryHandler());
        method.setQueryString(getQueryString());
        int resultCode = 0;
        try {
            logger.debug("Executing " + method + " on URI " + method.getURI());
            resultCode = httpClient.executeMethod(method);
        } catch (HttpException ex) {
            logger.error("Protocol error while executing " + method.getName() + " method with query string "
                    + method.getQueryString(), ex);
        } catch (IOException ex) {
            logger.error("Tranport error while executing " + method.getName() + " method with query string "
                    + method.getQueryString(), ex);
        }
        logger.info("Received response code [" + resultCode + "] for executing [" + method.getName()
                + "] method with path [" + method.getPath() + "]");
        return resultCode;
    }

    public void setHttpRetryHandler(HttpMethodRetryHandler httpRetryHandler) {
        this.httpRetryHandler = httpRetryHandler;
    }

    public HttpMethodRetryHandler getHttpRetryHandler() {
        return httpRetryHandler;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        connectionManager.getParams().setConnectionTimeout(connectionTimeout);
    }

    public int getConnectionTimeout() {
        return connectionManager.getParams().getConnectionTimeout();
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
                    logger.debug("Constructing ProxiedItemProperties");
                    ProxiedItemProperties properties = constructItemPropertiesFromGetResponse(path,
                            originatingUrlString, get);

                    logger.debug("Constructing ProxiedItem");
                    ProxiedItem result = new ProxiedItem();
                    result.setProperties(properties);
                    if (properties.isFile()) {
                        // TODO: Solve this in  a better way
                        File tmpFile = File.createTempFile(PathHelper.getFileName(path), null);
                        FileOutputStream fos = new FileOutputStream(tmpFile);
                        IOUtils.copy(get.getResponseBodyAsStream(), fos);
                        fos.flush();
                        fos.close();
                        InputStream is = new FileInputStream(tmpFile);
                        result.setStream(is);
                    } else {
                        result.setStream(null);
                    }
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
        Header lastModifiedHeader = executedMethod.getResponseHeader("last-modified");
        if (locationHeader != null) {
            // we may had redirection
            logger.info("We got location header " + locationHeader.getValue());
            originatingUrlString = locationHeader.getValue();
        }
        URL originatingUrl = new URL(originatingUrlString);

        ProxiedItemProperties result = new ProxiedItemProperties();
        result.setAbsolutePath(PathHelper.changePathLevel(path, PathHelper.PATH_PARENT));
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
