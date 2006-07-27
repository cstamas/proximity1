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
import java.net.SocketException;
import java.net.URL;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;

/**
 * Naive remote storage implementation based on Apache Commons Net FTPClient's
 * library. It uses FTP LIST method for existence checking and FTP GET for item
 * retrieval.
 * 
 * @author cstamas
 * 
 */
public class CommonsNetFtpRemotePeer extends AbstractRemoteStorage {

    private FTPClientConfig ftpClientConfig = new FTPClientConfig(FTPClientConfig.SYST_UNIX);

    private int connectionTimeout = 5000;

    private int retrievalRetryCount = 3;

    private String proxyHost = null;

    private int proxyPort = 8080;

    private String proxyUsername = null;

    private String proxyPassword = null;

    private String ftpUsername = null;

    private String ftpPassword = null;

    public FTPClientConfig getFtpClientConfig() {
        return ftpClientConfig;
    }

    public void setFtpClientConfig(FTPClientConfig ftpClientConfig) {
        this.ftpClientConfig = ftpClientConfig;
    }

    public String getFtpUsername() {
        return ftpUsername;
    }

    public void setFtpUsername(String ftpUsername) {
        this.ftpUsername = ftpUsername;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }

    public void setFtpPassword(String ftpPassword) {
        this.ftpPassword = ftpPassword;
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

    public boolean containsItem(String path) throws StorageException {
        try {
            return retrieveItemProperties(path) != null;
        } catch (ItemNotFoundException ex) {
            return false;
        }
    }

    public ProxiedItemProperties retrieveItemProperties(String path) throws ItemNotFoundException, StorageException {
        String originatingUrlString = getAbsoluteUrl(path);
        FTPClient client = null;
        try {
            client = getFTPClient();
            try {
                if (client.changeWorkingDirectory(PathHelper.concatPaths(getRemoteUrlAsUrl().getPath(), PathHelper
                        .getDirName(path)))) {
                    FTPFile[] fileList = client.listFiles(PathHelper.getFileName(path));
                    if (fileList.length == 1) {
                        return constructItemPropertiesFromGetResponse(path, originatingUrlString, fileList[0]);
                    } else {
                        logger.info("Item " + path + " not found in FTP remote peer of " + getRemoteUrl());
                        throw new ItemNotFoundException("Item " + path + " not found in FTP remote peer of "
                                + getRemoteUrl());
                    }
                } else {
                    logger.info("Path " + PathHelper.getDirName(path) + " not found in FTP remote peer of "
                            + getRemoteUrl());
                    throw new ItemNotFoundException("Path " + PathHelper.getDirName(path)
                            + " not found in FTP remote peer of " + getRemoteUrl());
                }
            } catch (IOException ex) {
                throw new StorageException("Cannot execute FTP operation on remote peer.", ex);
            }
        } finally {
            try {
                if (client.isConnected()) {
                    client.disconnect();
                }
            } catch (IOException ex) {
                logger.warn("Could not disconnect FTPClient", ex);
            }
        }
    }

    public ProxiedItem retrieveItem(String path) throws ItemNotFoundException, StorageException {
        String originatingUrlString = getAbsoluteUrl(path);
        FTPClient client = null;
        try {
            client = getFTPClient();
            try {
                if (client.changeWorkingDirectory(PathHelper.concatPaths(getRemoteUrlAsUrl().getPath(), PathHelper
                        .getDirName(path)))) {
                    FTPFile[] fileList = client.listFiles(PathHelper.getFileName(path));
                    if (fileList.length == 1) {
                        FTPFile ftpFile = fileList[0];
                        ProxiedItemProperties properties = constructItemPropertiesFromGetResponse(path,
                                originatingUrlString, ftpFile);
                        ProxiedItem result = new ProxiedItem();
                        if (properties.isFile()) {
                            // TODO: Solve this in a better way
                            File tmpFile = File.createTempFile(PathHelper.getFileName(path), null);
                            FileOutputStream fos = new FileOutputStream(tmpFile);
                            client.retrieveFile(PathHelper.getFileName(path), fos);
                            fos.flush();
                            fos.close();
                            InputStream is = new FileInputStream(tmpFile);
                            result.setStream(is);
                        } else {
                            result.setStream(null);
                        }
                        result.setProperties(properties);
                        return result;
                    } else {
                        logger.info("Item " + path + " not found in FTP remote peer of " + getRemoteUrl());
                        throw new ItemNotFoundException("Item " + path + " not found in FTP remote peer of "
                                + getRemoteUrl());
                    }
                } else {
                    logger.info("Path " + PathHelper.getDirName(path) + " not found in FTP remote peer of "
                            + getRemoteUrl());
                    throw new ItemNotFoundException("Path " + PathHelper.getDirName(path)
                            + " not found in FTP remote peer of " + getRemoteUrl());
                }
            } catch (IOException ex) {
                throw new StorageException("Cannot execute FTP operation on remote peer.", ex);
            }
        } finally {
            try {
                if (client.isConnected()) {
                    client.disconnect();
                }
            } catch (IOException ex) {
                logger.warn("Could not disconnect FTPClient", ex);
            }
        }
    }

    public FTPClient getFTPClient() {
        try {
            logger.info("Creating CommonsNetFTPClient instance");
            FTPClient ftpc = new FTPClient();
            ftpc.configure(ftpClientConfig);
            ftpc.connect(getRemoteUrlAsUrl().getHost());
            ftpc.login(getFtpUsername(), getFtpPassword());
            ftpc.setFileType(FTPClient.BINARY_FILE_TYPE);
            return ftpc;
        } catch (SocketException ex) {
            throw new StorageException("Got SocketException while creating FTPClient", ex);
        } catch (IOException ex) {
            throw new StorageException("Got IOException while creating FTPClient", ex);
        }
    }

    protected ProxiedItemProperties constructItemPropertiesFromGetResponse(String path, String originatingUrlString,
            FTPFile remoteFile) throws MalformedURLException {
        URL originatingUrl = new URL(originatingUrlString);
        ProxiedItemProperties result = new ProxiedItemProperties();
        result.setAbsolutePath(PathHelper.changePathLevel(path, PathHelper.PATH_PARENT));
        result.setDirectory(remoteFile.isDirectory());
        result.setFile(remoteFile.isFile());
        result.setLastModified(remoteFile.getTimestamp().getTime());
        result.setName(PathHelper.getFileName(originatingUrl.getPath()));
        if (result.isFile()) {
            result.setSize(remoteFile.getSize());
        } else {
            result.setSize(0);
        }
        result.setMetadata(ItemProperties.METADATA_ORIGINATING_URL, originatingUrl.toString());
        return result;
    }

}