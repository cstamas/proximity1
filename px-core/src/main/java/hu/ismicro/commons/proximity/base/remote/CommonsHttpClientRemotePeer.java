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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.apache.commons.io.IOUtils;

/**
 * Naive remote storage implementation based on Apache Commons HttpClient's
 * library. It uses HTTP HEAD method for existence checking and HTTP GET for
 * item retrieval.
 * 
 * @author cstamas
 * 
 */
public class CommonsHttpClientRemotePeer extends AbstractRemoteStorage {

    private HttpMethodRetryHandler httpRetryHandler = null;

    private HostConfiguration httpConfiguration = null;

    private HttpClient httpClient = null;

    private boolean followRedirection = true;

    private String queryString = null;

    private int connectionTimeout = 5000;

    private int retrievalRetryCount = 3;

    private String proxyHost = null;

    private int proxyPort = 8080;

    private String proxyUsername = null;

    private String proxyPassword = null;

    private String proxyNtlmDomain = null;

    private String proxyNtlmHost = null;

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

    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public String getProxyNtlmDomain() {
        return proxyNtlmDomain;
    }

    public void setProxyNtlmDomain(String proxyNtlmDomain) {
        this.proxyNtlmDomain = proxyNtlmDomain;
    }

    public String getProxyNtlmHost() {
        return proxyNtlmHost;
    }

    public void setProxyNtlmHost(String proxyNtlmHost) {
        this.proxyNtlmHost = proxyNtlmHost;
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

    public int getRetrievalRetryCount() {
        return retrievalRetryCount;
    }

    public void setRetrievalRetryCount(int retrievalRetryCount) {
        this.retrievalRetryCount = retrievalRetryCount;
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

    public ProxiedItem retrieveItem(String path) throws ItemNotFoundException, StorageException {
        String originatingUrlString = getAbsoluteUrl(path);
        GetMethod get = new GetMethod(originatingUrlString);
        try {
            try {
                int response = executeMethod(get);
                if (response == HttpStatus.SC_OK) {
                    logger.info("Item [{}] fetched from remote location {}", path, getRemoteUrl());
                    // ProxiedItemProperties properties =
                    // constructItemPropertiesFromGetResponse(path,
                    // originatingUrlString, get);

                    ProxiedItem result = new ProxiedItem();
                    ProxiedItemProperties ip = null;

                    // is it a file?
                    if (get.getResponseHeader("last-modified") != null) {
                        File tmpFile = File.createTempFile(PathHelper.getFileName(path), null);
                        FileOutputStream fos = new FileOutputStream(tmpFile);
                        IOUtils.copy(get.getResponseBodyAsStream(), fos);
                        fos.flush();
                        fos.close();
                        tmpFile.setLastModified(makeDateFromHeader(get.getResponseHeader("last-modified")));
                        ip = getProxiedItemPropertiesFactory().expandItemProperties(tmpFile, true);
                        InputStream is = new FileInputStream(tmpFile);
                        result.setStream(is);
                    } else {
                        result.setStream(null);
                    }
                    result.setProperties(ip);
                    result.getProperties().setMetadata(ItemProperties.METADATA_ORIGINATING_URL, originatingUrlString);
                    return result;
                } else {
                    if (response == HttpStatus.SC_NOT_FOUND) {
                        throw new ItemNotFoundException(path);
                    } else {
                        throw new StorageException("The method execution returned result code " + response);
                    }
                }
            } catch (MalformedURLException ex) {
                throw new StorageException("The path " + path + " is malformed!", ex);
            } catch (IOException ex) {
                throw new StorageException("IO Error during response stream handling!", ex);
            }
        } finally {
            get.releaseConnection();
        }
    }

    public HttpClient getHttpClient() {
        if (httpClient == null) {
            logger.info("Creating CommonsHttpClient instance");
            httpRetryHandler = new DefaultHttpMethodRetryHandler(retrievalRetryCount, false);
            httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
            httpClient.getParams().setConnectionManagerTimeout(getConnectionTimeout());
            httpConfiguration = httpClient.getHostConfiguration();

            if (getProxyHost() != null) {
                logger.info("... proxy setup with host {}", getProxyHost());
                httpConfiguration.setProxy(getProxyHost(), getProxyPort());

                if (getProxyUsername() != null) {

                    List authPrefs = new ArrayList(2);
                    authPrefs.add(AuthPolicy.DIGEST);
                    authPrefs.add(AuthPolicy.BASIC);

                    if (getProxyNtlmDomain() != null) {

                        // Using NTLM auth, adding it as first in policies
                        authPrefs.add(0, AuthPolicy.NTLM);

                        logger.info("... proxy authentication setup for NTLM domain {}, username {}",
                                getProxyNtlmDomain(), getProxyUsername());
                        httpConfiguration.setHost(getProxyNtlmHost());

                        httpClient.getState().setProxyCredentials(
                                AuthScope.ANY,
                                new NTCredentials(getProxyUsername(), getProxyPassword(), getProxyNtlmHost(),
                                        getProxyNtlmDomain()));
                    } else {

                        // Using Username/Pwd auth, will not add NTLM
                        logger.info("... proxy authentication setup for http proxy {}, username {}", getProxyHost(),
                                getProxyUsername());

                        httpClient.getState().setProxyCredentials(AuthScope.ANY,
                                new UsernamePasswordCredentials(getProxyUsername(), getProxyPassword()));

                    }
                    httpClient.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
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
            resultCode = getHttpClient().executeMethod(httpConfiguration, method);
        } catch (HttpException ex) {
            logger.error("Protocol error while executing {} method", method.getName(), ex);
        } catch (IOException ex) {
            logger.error("Tranport error while executing {} method", method.getName(), ex);
        }
        return resultCode;
    }

    protected long makeDateFromHeader(Header date) {
        Date result = null;
        if (date != null) {
            try {
                result = DateUtil.parseDate(date.getValue());
            } catch (DateParseException ex) {
                logger.warn("Could not parse date {}, using system current time as item creation time.", date, ex);
                result = new Date();
            } catch (NullPointerException ex) {
                logger.warn("Parsed date is null, using system current time as item creation time.", ex);
                result = new Date();
            }
        } else {
            result = new Date();
        }
        return result.getTime();
    }

}
