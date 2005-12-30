package hu.ismicro.commons.proximity.base.remote;

import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.base.PathHelper;
import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.ProxiedItemProperties;
import hu.ismicro.commons.proximity.base.StorageException;

import java.io.ByteArrayInputStream;
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
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

public class HttpClientRemotePeer extends AbstractRemoteStorage {

	private HttpClient httpClient = new HttpClient(new SimpleHttpConnectionManager());

	private HttpMethodRetryHandler httpRetryHandler = new DefaultHttpMethodRetryHandler();

	private int connectionTimeout = 5000;

	private boolean followRedirection = true;

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
		HeadMethod head = new HeadMethod(getAbsoluteUrl(path));
		int response = executeMethod(head);
		return response == HttpStatus.SC_OK;
	}

	public ProxiedItemProperties retrieveItemProperties(String path) throws ItemNotFoundException, StorageException {
		String originatingUrlString = getAbsoluteUrl(path);
		GetMethod get = new GetMethod(originatingUrlString);
		try {
			int response = executeMethod(get);
			if (response == HttpStatus.SC_OK) {
				return constructItemPropertiesFromGetResponse(originatingUrlString, get);
			} else {
				logger.error("The method execution returned result code " + response);
				throw new StorageException("The method execution returned result code " + response);
			}
		} catch (MalformedURLException ex) {
			logger.error("The path " + path + " is malformed!", ex);
			throw new StorageException("The method execution got MalformedURLException!", ex);
		} finally {
			get.releaseConnection();
		}
	}

	public ProxiedItem retrieveItem(String path) throws ItemNotFoundException, StorageException {
		String originatingUrlString = getAbsoluteUrl(path);
		GetMethod get = new GetMethod(originatingUrlString);
		try {
			int response = executeMethod(get);
			if (response == HttpStatus.SC_OK) {
				ProxiedItemProperties properties = constructItemPropertiesFromGetResponse(originatingUrlString, get);

				ProxiedItem result = new ProxiedItem();
				result.setProperties(properties);
				if (properties.isFile()) {
					// a little stream acrobatics
					ByteArrayOutputStream bos = new ByteArrayOutputStream((int) properties.getSize());
					IOUtils.copy(get.getResponseBodyAsStream(), bos);
					InputStream is = new ByteArrayInputStream(bos.toByteArray());
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

	protected ProxiedItemProperties constructItemPropertiesFromGetResponse(String originatingUrlString,
			GetMethod executedMethod) throws MalformedURLException {
		Header locationHeader = executedMethod.getResponseHeader("location");
		Header lastModifiedHeader = executedMethod.getResponseHeader("last-modified");
		if (locationHeader != null) {
			// we may had redirection
			logger.info("We got location header " + locationHeader.getValue());
			originatingUrlString = locationHeader.getValue();
		}
		URL originatingUrl = new URL(originatingUrlString);

		ProxiedItemProperties result = new ProxiedItemProperties();
		result.setAbsolutePath(PathHelper.changePathLevel(originatingUrl.getPath(), PathHelper.PATH_PARENT));
		// TODO: ibiblio behaves like this, check for others
		result.setDirectory(lastModifiedHeader != null);
		result.setFile(lastModifiedHeader == null);
		result.setLastModified(makeDateFromString(lastModifiedHeader.getValue()));
		result.setName(PathHelper.getFileName(originatingUrl.getPath()));
		result.setMetadata(ItemProperties.METADATA_ORIGINATING_URL, originatingUrl.toString());
		result.setSize(result.isFile() ? executedMethod.getResponseContentLength() : 0);
		logger.info("Received content with length: " + executedMethod.getResponseContentLength());
		return result;
	}

}
