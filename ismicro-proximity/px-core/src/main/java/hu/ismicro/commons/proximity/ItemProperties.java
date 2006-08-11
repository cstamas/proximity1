package hu.ismicro.commons.proximity;

import java.util.Date;
import java.util.Map;

/**
 * Interface for Item properties abstraction.
 * 
 * @author cstamas
 * 
 */
public interface ItemProperties {

    public static final String METADATA_ABSOLUTE_PATH = "item.absolutePath";

    public static final String METADATA_NAME = "item.name";

    public static final String METADATA_IS_DIRECTORY = "item.isDirectory";

    public static final String METADATA_IS_FILE = "item.isFile";

    public static final String METADATA_FILESIZE = "item.filesize";

    public static final String METADATA_LAST_MODIFIED = "item.lastModified";

    public static final String METADATA_ORIGINATING_URL = "item.origin";

    public static final String METADATA_EXT = "item.ext";

    public static final String METADATA_HASH_MD5 = "item.hash.md5";
    
    public static final String METADATA_HASH_SHA1 = "item.hash.sha1";

    public static final String METADATA_OWNING_REPOSITORY = "repository.id";

    public static final String METADATA_OWNING_REPOSITORY_GROUP = "repository.groupId";

    /**
     * Returns the absolute path of the item. Using this path appended with
     * getName() will fetch the Item represented by this properties.
     * 
     * @return absolute path of the item.
     */
    String getAbsolutePath();

    /**
     * Returns the name of the item (pathelements striped off).
     * 
     * @return the name of the item.
     */
    String getName();

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

    /**
     * Tests whether this item is File.
     * 
     * @return true if file, false otherwise.
     */
    boolean isFile();

    /**
     * Returns the filesize of the item. If it is a dir, the size equals to 0.
     * 
     * @return the file size or 0 in case of directory.
     */
    long getSize();

    /**
     * Returns the last modification date of the item.
     * 
     * @return the last modification date.
     */
    Date getLastModified();

    /**
     * Returns the id of the source repository.
     * 
     * @return
     */
    String getRepositoryId();

    /**
     * Returns the groupId of the source repository.
     * 
     * @return
     */
    String getRepositoryGroupId();

    /**
     * Tests whether this item has remote origin.
     * 
     * @return true if it has remote origin.
     */
    boolean hasRemoteOrigin();

    /**
     * Returns the remote path (URL) of the item.
     * 
     * @return true if it has remote origin.
     */
    String getRemotePath();

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

}
