/*

   Copyright 2005-2007 Tamas Cservenak (t.cservenak@gmail.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.abstracthorizon.proximity;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * Interface for Item properties abstraction.
 * 
 * @author cstamas
 */
public interface ItemProperties
    extends Serializable
{

    /** The Constant PATH_SEPARATOR. */
    public static final String PATH_SEPARATOR = "/";

    /** The Constant PATH_ROOT. */
    public static final String PATH_ROOT = PATH_SEPARATOR;

    /** The Constant METADATA_DIRECTORY_PATH. */
    public static final String METADATA_DIRECTORY_PATH = "item.directoryPath";

    /** The Constant METADATA_NAME. */
    public static final String METADATA_NAME = "item.name";

    /** The Constant METADATA_IS_DIRECTORY. */
    public static final String METADATA_IS_DIRECTORY = "item.isDirectory";

    /** The Constant METADATA_IS_FILE. */
    public static final String METADATA_IS_FILE = "item.isFile";

    /** The Constant METADATA_IS_CACHED. */
    public static final String METADATA_IS_CACHED = "item.isCached";

    /** The Constant METADATA_FILESIZE. */
    public static final String METADATA_FILESIZE = "item.filesize";

    /** The Constant METADATA_LAST_MODIFIED. */
    public static final String METADATA_LAST_MODIFIED = "item.lastModified";

    /** The Constant METADATA_REMOTE_URL. */
    public static final String METADATA_REMOTE_URL = "item.remoteUrl";

    /** The Constant METADATA_EXT. */
    public static final String METADATA_EXT = "item.ext";

    /** The Constant METADATA_HASH_MD5. */
    public static final String METADATA_HASH_MD5 = "item.hash.md5";

    /** The Constant METADATA_HASH_SHA1. */
    public static final String METADATA_HASH_SHA1 = "item.hash.sha1";

    /** The Constant METADATA_OWNING_REPOSITORY. */
    public static final String METADATA_OWNING_REPOSITORY = "repository.id";

    /** The Constant METADATA_OWNING_REPOSITORY_GROUP. */
    public static final String METADATA_OWNING_REPOSITORY_GROUP = "repository.groupId";

    /** The Constant METADATA_SCANNED. */
    public static final String METADATA_SCANNED = "item.scanned";

    /** The Constant METADATA_SCANNED_EXT. */
    public static final String METADATA_SCANNED_EXT = "item.scanned.ext";

    /**
     * Returns the directory path of the item. Using this path appended with getName() will fetch the Item represented
     * by this properties.
     * 
     * @return absolute path of the item.
     */
    String getDirectoryPath();

    /**
     * Sets the directory path.
     * 
     * @param path the new directory path
     */
    void setDirectoryPath( String path );

    /**
     * Returns the name of the item (pathelements striped off).
     * 
     * @return the name of the item.
     */
    String getName();

    /**
     * Sets the name.
     * 
     * @param name the new name
     */
    void setName( String name );

    /**
     * Returns file extension, if any.
     * 
     * @return file extension (without leading ".") or null if none.
     */
    String getExtension();

    /**
     * Sets the extension.
     * 
     * @param ext the new extension
     */
    void setExtension( String ext );

    /**
     * Returns getAbsolutePath() concatenated properly with getName(). Properly means for "/".
     * 
     * @return the path
     */
    String getPath();

    /**
     * Tests whether this item is Directory.
     * 
     * @return true if directory, false otherwise.
     */
    boolean isDirectory();

    /**
     * Sets the directory.
     * 
     * @param isDir the new directory
     */
    void setDirectory( boolean isDir );

    /**
     * Tests whether this item is File.
     * 
     * @return true if file, false otherwise.
     */
    boolean isFile();

    /**
     * Sets the file.
     * 
     * @param isFile the new file
     */
    void setFile( boolean isFile );

    /**
     * Returns the filesize of the item in bytes. If it is a dir, the size equals to 0.
     * 
     * @return the file size or 0 in case of directory.
     */
    long getSize();

    /**
     * Sets the size.
     * 
     * @param size the new size
     */
    void setSize( long size );

    /**
     * Returns the last modification date of the item.
     * 
     * @return the last modification date.
     */
    Date getLastModified();

    /**
     * Sets the last modified.
     * 
     * @param lastModified the new last modified
     */
    void setLastModified( Date lastModified );

    /**
     * The date when item was last scanned for default properties.
     * 
     * @return the last scanned
     */
    Date getLastScanned();

    /**
     * Sets the last scanned.
     * 
     * @param lastScanned the new last scanned
     */
    void setLastScanned( Date lastScanned );

    /**
     * The date whan item was last checksummed and scanned for any extra properties.
     * 
     * @return the last scanned ext
     */
    Date getLastScannedExt();

    /**
     * Sets the last scanned ext.
     * 
     * @param lastScannedExt the new last scanned ext
     */
    void setLastScannedExt( Date lastScannedExt );

    /**
     * Returns the id of the source repository.
     * 
     * @return the repository id
     */
    String getRepositoryId();

    /**
     * Sets the repository id.
     * 
     * @param repoId the new repository id
     */
    void setRepositoryId( String repoId );

    /**
     * Returns the groupId of the source repository.
     * 
     * @return the repository group id
     */
    String getRepositoryGroupId();

    /**
     * Sets the repository group id.
     * 
     * @param repoGroupId the new repository group id
     */
    void setRepositoryGroupId( String repoGroupId );

    /**
     * Tests whether this item has remote origin.
     * 
     * @return true if it has remote origin.
     */
    boolean isCached();

    /**
     * Sets the cached.
     * 
     * @param cached the new cached
     */
    void setCached( boolean cached );

    /**
     * Returns the remote path (URL) of the item.
     * 
     * @return true if it has remote origin.
     */
    String getRemoteUrl();

    /**
     * Sets the remote url.
     * 
     * @param remoteUrl the new remote url
     */
    void setRemoteUrl( String remoteUrl );

    /**
     * Returns items MD5 hash if any, in hex encoded form.
     * 
     * @return hash as string in hex encoded form or null if none.
     */
    String getHashMd5();

    /**
     * Sets the hash md5.
     * 
     * @param md5hash the new hash md5
     */
    void setHashMd5( String md5hash );

    /**
     * Returns item MD5 hash if any as byte array.
     * 
     * @return hash as byte array or null if none.
     */
    byte[] getHashMd5AsBytes();

    /**
     * Sets the hash md5 as bytes.
     * 
     * @param md5hash the new hash md5 as bytes
     */
    void setHashMd5AsBytes( byte[] md5hash );

    /**
     * Returns item SHA1 hash if any, in hex encoded form.
     * 
     * @return hash string in hex encoded form or null if none.
     */
    String getHashSha1();

    /**
     * Sets the hash sha1.
     * 
     * @param sha1hash the new hash sha1
     */
    void setHashSha1( String sha1hash );

    /**
     * Returns item SHA1 hash if any as byte array.
     * 
     * @return hash as byte array or null if none.
     */
    byte[] getHashSha1AsBytes();

    /**
     * Sets the hash sha1 as bytes.
     * 
     * @param sha1hash the new hash sha1 as bytes
     */
    void setHashSha1AsBytes( byte[] sha1hash );

    /**
     * Returns the metadata value.
     * 
     * @param key the key
     * 
     * @return the metadata
     */
    String getMetadata( String key );

    /**
     * Sets a non-indexed metadata key.
     * 
     * @param key the key
     * @param value the value
     */
    void setMetadata( String key, String value );

    /**
     * Returns all metadata in a map.
     * 
     * @return the all metadata
     */
    Map getAllMetadata();

    /**
     * Put all metadata.
     * 
     * @param md the md
     */
    void putAllMetadata( Map md );

}
