package hu.ismicro.commons.proximity.remote.httpclient;

import hu.ismicro.commons.proximity.Artifact;
import hu.ismicro.commons.proximity.remote.AbstractRemotePeer;

import java.io.IOException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class HttpClientRemotePeer extends AbstractRemotePeer {

    private HttpClient httpClient = new HttpClient(new SimpleHttpConnectionManager());
    
    private HttpMethodRetryHandler httpRetryHandler = new DefaultHttpMethodRetryHandler();
    
    private int connectionTimeout = 3000;
    
    private boolean followRedirection = false;
    
    protected void executeMethod(HttpMethod method) {
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(getConnectionTimeout());
        method.setFollowRedirects(isFollowRedirection());
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, getHttpRetryHandler());
        int resultCode = 0;
        try {
            resultCode = httpClient.executeMethod(method);
        } catch (HttpException ex) {
            logger.error("Protocol error while executing " + method.getName() + " method with query string " + method.getQueryString(), ex);
        } catch (IOException ex) {
            logger.error("Tranport error while executing " + method.getName() + " method with query string " + method.getQueryString(), ex);
        } finally {
            logger.info("Received response code " + resultCode + " for executing " + method.getName() + " method with query string " + method.getQueryString());
            method.releaseConnection();
        }
//        return resultCode == HttpStatus.SC_OK;
        
    }

    public boolean containsArtifact(String key) {
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(getConnectionTimeout());
        GetMethod get = new GetMethod(key);
        get.setFollowRedirects(isFollowRedirection());
        get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, getHttpRetryHandler());
        int resultCode = 0;
        try {
            resultCode = httpClient.executeMethod(get);
        } catch (HttpException ex) {
            logger.error("Protocol error while GETting " + key, ex);
        } catch (IOException ex) {
            logger.error("Transport error while GETting " + key, ex);
        } finally {
            logger.info("Received response code " + resultCode + " for key " + key);
            get.releaseConnection();
        }
        return resultCode == HttpStatus.SC_OK;
    }

    public Artifact retrieveArtifact(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    public void storeArtifact(String key, Artifact af) {
        // TODO Auto-generated method stub

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

}
