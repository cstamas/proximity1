package org.abstracthorizon.proximity;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Interface for Item properties abstraction.
 * 
 * @author cstamas
 * 
 */
public interface ItemProperties extends Serializable {
    
    public static final String PATH_SEPARATOR = "/";
    
    public static final String PATH_ROOT = PATH_SEPARATOR;

    public static final String METADATA_DIRECTORY_PATH = "item.directoryPath";

    public static final String METADATA_NAME = "item.name";

    public static final String METADATA_IS_DIRECTORY = "item.isDirectory";

    public static final String METADATA_IS_FILE = "item.isFile";

    public static final String METADATA_IS_CACHED = "item.isCached";

    public static final String METADATA_FILESIZE = "item.filesize";

    public static final String METADATA_LAST_MODIFIED = "item.lastModified";

    public static final String METADATA_REMOTE_URL = "item.remoteUrl";

    public static final String METADATA_EXT = "item.ext";

    public static final String METADATA_HASH_MD5 = "item.hash.md5";
    
    public static final String METADATA_HASH_SHA1 = "item.hash.sha1";

    public static final String METADATA_OWNING_REPOSITORY = "repository.id";

    public static final String METADATA_OWNING_REPOSITORY_GROUP = "repository.groupId";

    public static final String METADATA_SCANNED = "item.scanned";
    
    public static final String METADATA_SCANNED_EXT = "item.scanned.ext";
    
    /**
     * Returns the directory path of the item. Using this path appended with
     * getName() will fetch the Item represented by this properties.
     * 
     * @return absolute path of the item.
     */
    String getDirectoryPath();
    
    void setDirectoryPath(String path);

    /**
     * Returns the name of the item (pathelements striped off).
     * 
     * @return the name of the item.
     */
    String getName();
    
    void setName(String name);
    
    /**
     * Returns file extension, if any.
     * 
     * @return file extension (without leading ".") or null if none.
     */
    String getExtension();
    
    void setExtension(String ext);

    /**
     * Returns getAbsolutePath() concatenated properly with getName(). Properly
     * means for "/".
     * 
     * @return
     */
    String getPath();

    /**
     * Tests whether this item is Directory.
     * 
     * @return true if directory, false otherwise.
     */
    boolean isDirectory();
    
    void setDirectory(boolean isDir);

    /**
     * Tests whether this item is File.
     * 
     * @return true if file, false otherwise.
     */
    boolean isFile();
    
    void setFile(boolean isFile);

    /**
     * Returns the filesize of the item in bytes. If it is a dir, the size equals to 0.
     * 
     * @return the file size or 0 in case of directory.
     */
    long getSize();
    
    void setSize(long size);

    /**
     * Returns the last modification date of the item.
     * 
     * @return the last modification date.
     */
    Date getLastModified();
    
    void setLastModified(Date lastModified);

    /**
     * The date when item was last scanned for default properties.
     * 
     * @return
     */
    Date getLastScanned();
    
    void setLastScanned(Date lastScanned);
    
    /**
     * The date whan item was last checksummed and scanned for any extra
     * properties.
     * 
     * @return
     */
    Date getLastScannedExt();
    
    void setLastScannedExt(Date lastScannedExt);

    /**
     * Returns the id of the source repository.
     * 
     * @return
     */
    String getRepositoryId();
    
    void setRepositoryId(String repoId);

    /**
     * Returns the groupId of the source repository.
     * 
     * @return
     */
    String getRepositoryGroupId();
    
    void setRepositoryGroupId(String repoGroupId);

    /**
     * Tests whether this item has remote origin.
     * 
     * @return true if it has remote origin.
     */
    boolean isCached();
    
    void setCached(boolean cached);

    /**
     * Returns the remote path (URL) of the item.
     * 
     * @return true if it has remote origin.
     */
    String getRemoteUrl();
    
    void setRemoteUrl(String remoteUrl);

    /**
     * Returns items MD5 hash if any, in hex encoded form.
     * 
     * @return hash as string in hex encoded form or null if none.
     */
    String getHashMd5();
    
    void setHashMd5(String md5hash);

    /**
     * Returns item MD5 hash if any as byte array.
     * 
     * @return hash as byte array or null if none.
     */
    byte[] getHashMd5AsBytes();
    
    void setHashMd5AsBytes(byte[] md5hash);

    /**
     * Returns item SHA1 hash if any, in hex encoded form.
     * 
     * @return hash string in hex encoded form or null if none.
     */
    String getHashSha1();
    
    void setHashSha1(String sha1hash);

    /**
     * Returns item SHA1 hash if any as byte array.
     * 
     * @return hash as byte array or null if none.
     */
    byte[] getHashSha1AsBytes();
    
    void setHashSha1AsBytes(byte[] sha1hash);

    /**
     * Returns the metadata value.
     * 
     * @param key
     * @return
     */
    String getMetadata(String key);

    /**
     * Sets a non-indexed metadata key.
     * 
     * @param key
     * @param value
     */
    void setMetadata(String key, String value);

    /**
     * Returns all metadata in a map.
     * 
     * @return
     */
    Map getAllMetadata();
    
    void putAllMetadata(Map md);

}
