package hu.ismicro.commons.proximity.remote;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.base.SimpleProxiedItem;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

public class HttpClientRemotePeer extends AbstractRemotePeer {

    private URL remoteUrl;

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
        } finally {
            method.releaseConnection();
        }
        logger.info("Received response code " + resultCode + " for executing " + method.getName()
                + " method with query string " + method.getQueryString());
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

    public URL getRemoteUrl() {
        return this.remoteUrl;
    }

    public void setRemoteUrl(URL url) {
        if (!url.getPath().endsWith("/")) {
            throw new IllegalArgumentException("The URL is not ending with '/' (slash)!");
        }
        this.remoteUrl = url;
    }

    public boolean containsItem(String path) {
        HeadMethod head = new HeadMethod(path);
        head.setFollowRedirects(isFollowRedirection());
        int response = executeMethod(head);
        return response == HttpStatus.SC_OK;
    }

    public Item retrieveItem(String path) {
        try {
            GetMethod get = new GetMethod(path);
            get.setFollowRedirects(isFollowRedirection());
            int response = executeMethod(get);
            if (response == HttpStatus.SC_OK) {
                SimpleProxiedItem result = new SimpleProxiedItem();
                result.setPath(path);
                result.setOriginatingUrl(new URL(getRemoteUrl() + path));
                result.setStream(get.getResponseBodyAsStream());
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
        }
    }

    public void storeItem(Item item) {
        throw new UnsupportedOperationException("RemotePeer is unable to store items.");
    }

    public List listItems(String path) {
        throw new UnsupportedOperationException("RemotePeer is not listable.");
    }

}
