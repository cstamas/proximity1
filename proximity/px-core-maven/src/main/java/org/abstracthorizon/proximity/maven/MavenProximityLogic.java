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
package org.abstracthorizon.proximity.maven;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.logic.DefaultProximityLogic;
import org.abstracthorizon.proximity.storage.remote.DeleteOnCloseFileInputStream;
import org.apache.commons.codec.binary.Hex;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Initial implementation of Maven specific ProximityLogic.
 * <p>
 * This logic will force the Proximity to do group search if the questioned request is M2 metadata.
 * <p>
 * The postprocessing consists of merging the metadata and returning it to Proximity to serve it.
 * 
 * @author cstamas
 */
public class MavenProximityLogic
    extends DefaultProximityLogic
{

    /**
     * This implementation says true if the request is about metadata.
     * 
     * @param request the request
     * 
     * @return true if the request is about metadata.
     */
    public boolean isGroupSearchNeeded( ProximityRequest request )
    {

        // will trigger for metadata and their checksums
        return MavenArtifactRecognizer.isMetadata( request.getPath() );

    }

    /**
     * This postprocessing simply merges the fetched list of metadatas.
     * 
     * @param request the request
     * @param groupRequest the group request
     * @param listOfProxiedItems the list of proxied items
     * 
     * @return the merged metadata.
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public Item postprocessItemList( ProximityRequest request, ProximityRequest groupRequest, List listOfProxiedItems )
        throws IOException
    {

        if ( listOfProxiedItems.size() == 0 )
        {

            throw new IllegalArgumentException( "The listOfProxiedItems list cannot be 0 length!" );
        }

        Item item = (Item) listOfProxiedItems.get( 0 );
        ItemProperties itemProps = item.getProperties();

        if ( listOfProxiedItems.size() > 1 )
        {

            if ( MavenArtifactRecognizer.isChecksum( request.getPath() ) )
            {

                File tmpFile = new File( System.getProperty( "java.io.tmpdir" ), request.getPath().replace(
                    ItemProperties.PATH_SEPARATOR.charAt( 0 ),
                    '_' ) );
                if ( tmpFile.exists() )
                {
                    logger.info( "Item for path " + request.getPath() + " SPOOFED with merged metadata checksum." );
                    item.setStream( new DeleteOnCloseFileInputStream( tmpFile ) );
                    itemProps.setSize( tmpFile.length() );
                }
                else
                {
                    logger.debug( "Item for path " + request.getPath() + " SPOOFED with first got from repo group." );
                }

            }
            else
            {
                logger.debug( "Item for path " + request.getPath() + " found in total of " + listOfProxiedItems.size()
                    + " repositories, will merge them." );

                MetadataXpp3Reader metadataReader = new MetadataXpp3Reader();
                MetadataXpp3Writer metadataWriter = new MetadataXpp3Writer();
                InputStreamReader isr;

                Metadata mergedMetadata = null;

                for ( int i = 0; i < listOfProxiedItems.size(); i++ )
                {

                    Item currentItem = (Item) listOfProxiedItems.get( i );
                    try
                    {
                        isr = new InputStreamReader( currentItem.getStream() );
                        Metadata imd = metadataReader.read( isr );
                        if ( mergedMetadata == null )
                        {
                            mergedMetadata = imd;
                        }
                        else
                        {
                            mergedMetadata.merge( imd );
                        }
                        isr.close();
                    }
                    catch ( XmlPullParserException ex )
                    {
                        logger.warn( "Could not merge M2 metadata: " + currentItem.getProperties().getDirectoryPath()
                            + " from repository " + currentItem.getProperties().getRepositoryId(), ex );
                    }
                    catch ( IOException ex )
                    {
                        logger.warn( "Got IOException during merge of M2 metadata: "
                            + currentItem.getProperties().getDirectoryPath() + " from repository "
                            + currentItem.getProperties().getRepositoryId(), ex );
                    }

                }

                try
                {
                    // we know that maven-metadata.xml is relatively small
                    // (few
                    // KB)
                    MessageDigest md5alg = MessageDigest.getInstance( "md5" );
                    MessageDigest sha1alg = MessageDigest.getInstance( "sha1" );
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    DigestOutputStream md5os = new DigestOutputStream( bos, md5alg );
                    DigestOutputStream sha1os = new DigestOutputStream( md5os, sha1alg );
                    OutputStreamWriter osw = new OutputStreamWriter( sha1os );
                    metadataWriter.write( osw, mergedMetadata );
                    osw.flush();
                    osw.close();

                    storeDigest( request, md5alg );
                    storeDigest( request, sha1alg );

                    ByteArrayInputStream is = new ByteArrayInputStream( bos.toByteArray() );
                    item.setStream( is );
                    itemProps.setSize( bos.size() );
                }
                catch ( NoSuchAlgorithmException ex )
                {
                    throw new IllegalArgumentException( "No MD5 or SHA1 algorithm?" );
                }
            }

        }

        return item;
    }

    /**
     * Store digest.
     * 
     * @param req the req
     * @param dig the dig
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void storeDigest( ProximityRequest req, MessageDigest dig )
        throws IOException
    {
        File tmpFile = new File( System.getProperty( "java.io.tmpdir" ), req.getPath().replace(
            ItemProperties.PATH_SEPARATOR.charAt( 0 ),
            '_' )
            + "." + dig.getAlgorithm().toLowerCase() );
        tmpFile.deleteOnExit();
        FileWriter fw = new FileWriter( tmpFile );
        try
        {
            fw.write( new String( Hex.encodeHex( dig.digest() ) ) + "\n" );
            fw.flush();
        }
        finally
        {
            fw.close();
        }

    }
}
