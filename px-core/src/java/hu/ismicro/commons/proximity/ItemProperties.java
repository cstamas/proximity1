package hu.ismicro.commons.proximity;

import java.net.URL;
import java.util.Date;

/**
 * Interface for Item properties abstraction.
 * 
 * @author cstamas
 *
 */
public interface ItemProperties {

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
     * Returns the full originating URL from where this item were fetched,
     * if applicable. Result is null if there is no info about item source.
     *  
     * @return full URL or null if no info.
     */
    URL getOriginatingUrl();

    /**
     * Returns the Id of the owning repository.
     * 
     * @return The Id of the owning repository.
     */
    String getRepositoryId();
    
}
