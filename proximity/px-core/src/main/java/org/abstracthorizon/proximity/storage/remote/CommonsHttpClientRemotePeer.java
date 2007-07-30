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

    private String queryString = null;

    private String userAgentString = null;

    private int connectionTimeout = 5000;

    private int retrievalRetryCount = 3;

    private String proxyHost = null;

    private int proxyPort = 8080;

    private String username = null;

    private String password = null;

    private String ntlmDomain = null;

    private String ntlmHost = null;

    private String proxyUsername = null;

    private String proxyPassword = null;

    private String proxyNtlmDomain = null;

    private String proxyNtlmHost = null;

    public String getUserAgentString() {
	return userAgentString;
    }

    public void setUserAgentString(String userAgentString) {
	this.userAgentString = userAgentString;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getNtlmDomain() {
	return ntlmDomain;
    }

    public void setNtlmDomain(String ntlmDomain) {
	this.ntlmDomain = ntlmDomain;
    }

    public String getNtlmHost() {
	return ntlmHost;
    }

    public void setNtlmHost(String ntlmHost) {
	this.ntlmHost = ntlmHost;
    }

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

    public boolean containsItemProperties(String path) {
	return containsItem(path);
    }

    public boolean containsItem(String path) {
	HeadMethod head = new HeadMethod(getAbsoluteUrl(path));
	int response = executeMethod(head);
	return response == HttpStatus.SC_OK;
    }

    public Item retrieveItem(String path, boolean propsOnly) throws ItemNotFoundException, StorageException {
	// TODO: propsOnly is ignored, use HTTP HEAD?
	String originatingUrlString = getAbsoluteUrl(path);
	GetMethod get = new GetMethod(originatingUrlString);
	try {
	    try {
		logger.info("Fetching item [{}] from remote location {}", path, originatingUrlString);
		int response = executeMethod(get);
		if (response == HttpStatus.SC_OK) {
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
		    if (!get.getPath().endsWith(ItemProperties.PATH_SEPARATOR)) {
			// if (get.getResponseHeader("last-modified") != null) {
			File tmpFile = File.createTempFile(FilenameUtils.getName(path), null);
			tmpFile.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(tmpFile);
			try {
			    InputStream is = get.getResponseBodyAsStream();
			    if (get.getResponseHeader("Content-Encoding") != null
				    && "gzip".equals(get.getResponseHeader("Content-Encoding").getValue())) {
				is = new GZIPInputStream(is);
			    }

			    IOUtils.copy(is, fos);
			    fos.flush();
			} finally {
			    fos.close();
			}
			tmpFile.setLastModified(makeDateFromHeader(get.getResponseHeader("last-modified")));
			ip = getProxiedItemPropertiesFactory().expandItemProperties(path, tmpFile, true);
			result.setStream(new DeleteOnCloseFileInputStream(tmpFile));
		    } else {
			// TODO: dirty hack, I am creating a dir named after the
			// directory retrieval just to get item properties!!!
			// Fix this!
			File tmpdir = new File(System.getProperty("java.io.tmpdir"), FilenameUtils.getName(path));
			tmpdir.mkdir();
			ip = getProxiedItemPropertiesFactory().expandItemProperties(path, tmpdir, true);
			tmpdir.delete();
			result.setStream(null);
		    }
		    result.setProperties(ip);
		    result.getProperties().setRemoteUrl(originatingUrlString);
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
	    httpRetryHandler = new DefaultHttpMethodRetryHandler(retrievalRetryCount, true);
	    httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
	    httpClient.getParams().setConnectionManagerTimeout(getConnectionTimeout());

	    httpConfiguration = httpClient.getHostConfiguration();

	    // BASIC and DIGEST auth only
	    if (getUsername() != null) {

		List authPrefs = new ArrayList(2);
		authPrefs.add(AuthPolicy.DIGEST);
		authPrefs.add(AuthPolicy.BASIC);

		if (getNtlmDomain() != null) {
		    // Using NTLM auth, adding it as first in policies
		    authPrefs.add(0, AuthPolicy.NTLM);

		    logger.info("... authentication setup for NTLM domain {}, username {}", getNtlmDomain(), getUsername());
		    httpConfiguration.setHost(getNtlmHost());

		    httpClient.getState().setCredentials(AuthScope.ANY,
			    new NTCredentials(getUsername(), getPassword(), getNtlmHost(), getNtlmDomain()));
		} else {

		    // Using Username/Pwd auth, will not add NTLM
		    logger.info("... setting authentication setup for remote peer {}, with username {}", getRemoteUrl(), getUsername());

		    httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(getUsername(), getPassword()));

		}
		httpClient.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
	    }

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

			if (getNtlmHost() != null) {
			    logger.warn("... CommonsHttpClient is unable to use NTLM auth scheme\n"
				    + " for BOTH server side and proxy side authentication!\n"
				    + " You MUST reconfigure server side auth and use BASIC/DIGEST scheme\n"
				    + " if you have to use NTLM proxy, since otherwise it will not work!\n" + " *** SERVER SIDE AUTH OVERRIDDEN");
			}
			logger.info("... proxy authentication setup for NTLM domain {}, username {}", getProxyNtlmDomain(), getProxyUsername());
			httpConfiguration.setHost(getProxyNtlmHost());

			httpClient.getState().setProxyCredentials(AuthScope.ANY,
				new NTCredentials(getProxyUsername(), getProxyPassword(), getProxyNtlmHost(), getProxyNtlmDomain()));
		    } else {

			// Using Username/Pwd auth, will not add NTLM
			logger.info("... proxy authentication setup for http proxy {}, username {}", getProxyHost(), getProxyUsername());

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
	if (getUserAgentString() != null) {
	    method.setRequestHeader(new Header("user-agent", getUserAgentString()));
	}
	method.setRequestHeader(new Header("accept", "*/*"));
	method.setRequestHeader(new Header("accept-language", "en-us"));
	method.setRequestHeader(new Header("accept-encoding", "gzip, identity"));
	method.setRequestHeader(new Header("connection", "Keep-Alive"));
	method.setRequestHeader(new Header("cache-control", "no-cache"));
	// TODO: fix for #93
	// method.setFollowRedirects(isFollowRedirection());
	method.setFollowRedirects(true);
	method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, httpRetryHandler);
	method.setQueryString(getQueryString());
	int resultCode = 0;
	try {
	    resultCode = getHttpClient().executeMethod(httpConfiguration, method);
	} catch (HttpException ex) {
	    logger.error("Protocol error while executing " + method.getName() + " method", ex);
	} catch (IOException ex) {
	    logger.error("Tranport error while executing " + method.getName() + " method", ex);
	}
	return resultCode;
    }

    protected long makeDateFromHeader(Header date) {
	Date result = null;
	if (date != null) {
	    try {
		result = DateUtil.parseDate(date.getValue());
	    } catch (DateParseException ex) {
		logger.warn("Could not parse date {} because of {}, using system current time as item creation time.", date, ex);
		result = new Date();
	    } catch (NullPointerException ex) {
		logger.warn("Parsed date is null, using system current time as item creation time.");
		result = new Date();
	    }
	} else {
	    result = new Date();
	}
	return result.getTime();
    }

}
