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
	
	public static final String METADATA_ORIGINATING_URL = "origin.url";
	
	public static final String METADATA_OWNING_REPOSITORY = "repository.id";

	public static final String METADATA_EXPIRES = "expires";

    /**
     * Returns the absolute path of the item. Using this path appended
     * with getName() will fetch the Item represented by this properties.
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
     * Returns the filesize of the item. If it is a dir, the size equals
     * to 0.
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
     * Returns the metadata value.
     * 
     * @param key
     * @return
     */
    String getMetadata(String key);
    
    /**
     * Sets a metadata value.
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
