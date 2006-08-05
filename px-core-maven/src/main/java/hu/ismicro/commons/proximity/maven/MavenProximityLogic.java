package hu.ismicro.commons.proximity.maven;

import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.base.ProxiedItem;
import hu.ismicro.commons.proximity.base.ProxiedItemProperties;
import hu.ismicro.commons.proximity.base.logic.DefaultProximityLogic;

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
     * This implementation says true if the request is about metadata CONTENT
     * (propertiesOnly == false) but not metadata checksum.
     * 
     * @return true if the request is about metadata.
     */
    public boolean isGroupSearchNeeded(ProximityRequest request) {

        return MavenArtifactRecognizer.isMetadata(request.getPath())
                && !MavenArtifactRecognizer.isChecksum(request.getPath());

    }

    /**
     * This postprocessing simply merges the fetched list of metadatas.
     * 
     * @return the merged metadata.
     */
    public ProxiedItem postprocessItemList(List listOfProxiedItems) throws IOException {
        
        if (listOfProxiedItems.size() == 0) {

            throw new IllegalArgumentException("The listOfProxiedItems list cannot be 0 length!");
        }

        ProxiedItem item = (ProxiedItem) listOfProxiedItems.get(0);
        ProxiedItemProperties itemProps = (ProxiedItemProperties) item.getProperties();

        if (listOfProxiedItems.size() == 1) {

            // we found only one, no merging is needed
            return item;
        }

        logger.info("Item found in total of " + listOfProxiedItems.size() + " repositories, will merge them.");

        MetadataXpp3Reader metadataReader = new MetadataXpp3Reader();
        MetadataXpp3Writer metadataWriter = new MetadataXpp3Writer();
        InputStreamReader isr = null;
        Metadata mergedMetadata = new Metadata();

        for (int i = 0; i < listOfProxiedItems.size(); i++) {

            ProxiedItem currentItem = (ProxiedItem) listOfProxiedItems.get(i);
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

        return item;

    }
}
