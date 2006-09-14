package org.abstracthorizon.proximity.maven;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.impl.ItemImpl;
import org.abstracthorizon.proximity.impl.ItemPropertiesImpl;
import org.abstracthorizon.proximity.logic.DefaultProximityLogic;
import org.apache.commons.codec.binary.Hex;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * <p>
 * Initial implementation of Maven specific ProximityLogic.
 * <p>
 * This logic will force the Proximity to do group search if the questioned
 * request is M2 metadata.
 * <p>
 * The postprocessing consists of merging the metadata and returning it to
 * Proximity to serve it.
 * 
 * @author cstamas
 * 
 */
public class MavenProximityLogic extends DefaultProximityLogic {

    /**
     * This implementation says true if the request is about metadata.
     * 
     * @return true if the request is about metadata.
     */
    public boolean isGroupSearchNeeded(ProximityRequest request) {

        // will trigger for metadata and their checksums
        return MavenArtifactRecognizer.isMetadata(request.getPath());

    }

    /**
     * This postprocessing simply merges the fetched list of metadatas.
     * 
     * @return the merged metadata.
     */
    public ItemImpl postprocessItemList(ProximityRequest request, ProximityRequest groupRequest, List listOfProxiedItems)
            throws IOException {

        if (listOfProxiedItems.size() == 0) {

            throw new IllegalArgumentException("The listOfProxiedItems list cannot be 0 length!");
        }

        ItemImpl item = (ItemImpl) listOfProxiedItems.get(0);
        ItemPropertiesImpl itemProps = (ItemPropertiesImpl) item.getProperties();

        if (listOfProxiedItems.size() > 1) {

            if (MavenArtifactRecognizer.isChecksum(request.getPath())) {

                File tmpFile = new File(System.getProperty("java.io.tmpdir"), request.getPath().replace(
                        ItemProperties.PATH_SEPARATOR, "_"));
                if (tmpFile.exists()) {
                    logger.info("Item for path " + request.getPath() + " SPOOFED with merged metadata checksum.");
                    FileInputStream fis = new FileInputStream(tmpFile);
                    item.setStream(fis);
                    itemProps.setSize(tmpFile.length());
                } else {
                    logger.info("Item for path " + request.getPath() + " SPOOFED with first got from repo group.");
                }

            } else {
                logger.info("Item for path " + request.getPath() + " found in total of " + listOfProxiedItems.size()
                        + " repositories, will merge them.");

                MetadataXpp3Reader metadataReader = new MetadataXpp3Reader();
                MetadataXpp3Writer metadataWriter = new MetadataXpp3Writer();
                InputStreamReader isr = null;
                Metadata mergedMetadata = new Metadata();

                for (int i = 0; i < listOfProxiedItems.size(); i++) {

                    ItemImpl currentItem = (ItemImpl) listOfProxiedItems.get(i);
                    try {
                        isr = new InputStreamReader(currentItem.getStream());
                        mergedMetadata.merge(metadataReader.read(isr));
                        isr.close();
                    } catch (XmlPullParserException ex) {
                        logger.warn("Could not merge M2 metadata: " + currentItem.getProperties().getDirectory()
                                + " from repository " + currentItem.getProperties().getRepositoryId(), ex);
                    } catch (IOException ex) {
                        logger.warn("Got IOException during merge of M2 metadata: "
                                + currentItem.getProperties().getDirectory() + " from repository "
                                + currentItem.getProperties().getRepositoryId(), ex);
                    }

                }

                try {
                    // we know that maven-metadata.xml is relatively small (few
                    // KB)
                    MessageDigest md5alg = MessageDigest.getInstance("md5");
                    MessageDigest sha1alg = MessageDigest.getInstance("sha1");
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    DigestOutputStream md5os = new DigestOutputStream(bos, md5alg);
                    DigestOutputStream sha1os = new DigestOutputStream(md5os, sha1alg);
                    OutputStreamWriter osw = new OutputStreamWriter(sha1os);
                    metadataWriter.write(osw, mergedMetadata);
                    osw.flush();
                    osw.close();

                    storeDigest(request, md5alg);
                    storeDigest(request, sha1alg);

                    ByteArrayInputStream is = new ByteArrayInputStream(bos.toByteArray());
                    item.setStream(is);
                    itemProps.setSize(bos.size());
                } catch (NoSuchAlgorithmException ex) {
                    throw new IllegalArgumentException("No MD5 or SHA1 algorithm?", ex);
                }
            }

        }

        return item;
    }

    protected void storeDigest(ProximityRequest req, MessageDigest dig) throws IOException {
        File tmpFile = new File(System.getProperty("java.io.tmpdir"), req.getPath().replace(
                ItemProperties.PATH_SEPARATOR, "_")
                + "." + dig.getAlgorithm().toLowerCase());
        tmpFile.deleteOnExit();
        FileWriter fw = new FileWriter(tmpFile);
        fw.write(new String(Hex.encodeHex(dig.digest())) + "\n");
        fw.flush();
        fw.close();

    }
}
