package hu.ismicro.commons.proximity.remote;

import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.SimpleProxiedItem;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;

public class HttpClientRemotePeer extends AbstractRemotePeer {

    private HttpClient httpClient = new HttpClient(new SimpleHttpConnectionManager());

    private HttpMethodRetryHandler httpRetryHandler = new DefaultHttpMethodRetryHandler();

    private int connectionTimeout = 3000;

    private boolean followRedirection = false;

    protected int executeMethod(HttpMethod method) {
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(getConnectionTimeout());
        method.setFollowRedirects(isFollowRedirection());
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, getHttpRetryHandler());
        int resultCode = 0;
        try {
            resultCode = httpClient.executeMethod(method);
        } catch (HttpException ex) {
            logger.error("Protocol error while executing " + method.getName() + " method with query string "
                    + method.getQueryString(), ex);
        } catch (IOException ex) {
            logger.error("Tranport error while executing " + method.getName() + " method with query string "
                    + method.getQueryString(), ex);
        }
        logger.info("Received response code " + resultCode + " for executing " + method.getName()
                + " method with query string " + method.getPath());
        return resultCode;
    }

    public void setHttpRetryHandler(HttpMethodRetryHandler httpRetryHandler) {
        this.httpRetryHandler = httpRetryHandler;
    }

    public HttpMethodRetryHandler getHttpRetryHandler() {
        return httpRetryHandler;
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

    public boolean containsItem(String path) {
        HeadMethod head = new HeadMethod(getRemoteUrl() + path);
        head.setFollowRedirects(isFollowRedirection());
        int response = executeMethod(head);
        return response == HttpStatus.SC_OK;
    }

    public ProxiedItem retrieveItem(String path) {
        GetMethod get = new GetMethod(getRemoteUrl() + path);
        get.setFollowRedirects(isFollowRedirection());
        try {
            int response = executeMethod(get);
            if (response == HttpStatus.SC_OK) {
                SimpleProxiedItem result = new SimpleProxiedItem();
                result.setPath(path);
                result.setStorageName(getRemoteUrl());
                result.setOriginatingUrl(new URL(getRemoteUrl() + path));
                result.setDirectory(false);
                result.setSize(get.getResponseContentLength());
                result.setLastModified(makeDateFromString(get.getResponseHeader("Last-Modified") != null ? get
                        .getResponseHeader("Last-Modified").getValue() : null));
                result.setStream(get.getResponseBodyAsStream());
                logger.info("Received content with Length: " + get.getResponseContentLength());
                return result;
            } else {
                logger.error("The method execution returned result code " + response);
                return null;
            }
        } catch (MalformedURLException ex) {
            logger.error("The path " + path + " is malformed!", ex);
            return null;
        } catch (IOException ex) {
            logger.error("IO Error during response stream handling!", ex);
            return null;
        } finally {
            get.releaseConnection();
        }
    }

    public List listItems(String path) {
        throw new UnsupportedOperationException("RemotePeer is not listable.");
    }

    protected Date makeDateFromString(String date) {
        Date result = null;
        if (date == null) {
            result = new Date();
        } else {
            try {
                result = DateUtil.parseDate(date);
            } catch (DateParseException ex) {
                logger.warn("Could not parse date " + date + ", using NOW");
                result = new Date();
            }
        }
        return result;
    }

}
