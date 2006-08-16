package hu.ismicro.commons.proximity.maven;

import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.impl.ItemImpl;
import hu.ismicro.commons.proximity.impl.ItemPropertiesImpl;
import hu.ismicro.commons.proximity.logic.DefaultProximityLogic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

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
    public ItemImpl postprocessItemList(ProximityRequest request, List listOfProxiedItems) throws IOException {
        
        if (listOfProxiedItems.size() == 0) {

            throw new IllegalArgumentException("The listOfProxiedItems list cannot be 0 length!");
        }

        ItemImpl item = (ItemImpl) listOfProxiedItems.get(0);
        ItemPropertiesImpl itemProps = (ItemPropertiesImpl) item.getProperties();

        if (listOfProxiedItems.size() == 1) {

            // we found only one, no merging is needed
            return item;
        }

        logger.info("Item for path " + request.getPath() + " found in total of " + listOfProxiedItems.size() + " repositories, will merge them.");

        MetadataXpp3Reader metadataReader = new MetadataXpp3Reader();
        MetadataXpp3Writer metadataWriter = new MetadataXpp3Writer();
        InputStreamReader isr = null;
        Metadata mergedMetadata = new Metadata();

        for (int i = 0; i < listOfProxiedItems.size(); i++) {

            ItemImpl currentItem = (ItemImpl) listOfProxiedItems.get(i);
            try {
                isr = new InputStreamReader(currentItem.getStream());
                mergedMetadata.merge(metadataReader.read(isr));
            } catch (XmlPullParserException ex) {
                logger.warn("Could not merge M2 metadata: " + currentItem.getProperties().getAbsolutePath()
                        + " from repository " + currentItem.getProperties().getRepositoryId(), ex);
            } catch (IOException ex) {
                logger.warn("Got IOException during merge of M2 metadata: "
                        + currentItem.getProperties().getAbsolutePath() + " from repository "
                        + currentItem.getProperties().getRepositoryId(), ex);
            }

        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(bos);
        metadataWriter.write(osw, mergedMetadata);
        osw.flush();
        osw.close();
        ByteArrayInputStream is = new ByteArrayInputStream(bos.toByteArray());
        item.setStream(is);
        itemProps.setSize(bos.size());
        
        // what about checksum?

        return item;

    }
}
