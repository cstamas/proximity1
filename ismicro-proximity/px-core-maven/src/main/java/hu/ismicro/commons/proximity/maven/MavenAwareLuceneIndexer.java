package hu.ismicro.commons.proximity.maven;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.base.indexer.LuceneIndexer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class MavenAwareLuceneIndexer extends LuceneIndexer {

    public static final String KIND_KEY = "kind";

    public static final String KIND_POM = "pom";

    public static final String KIND_METADATA = "metadata";

    public static final String KIND_SNAPSHOT = "snapshot";

    public static final String POM_GID_KEY = "pom.gid";

    public static final String POM_AID_KEY = "pom.aid";

    public static final String POM_PCK_KEY = "pom.pck";

    public static final String POM_URL_KEY = "pom.url";

    public List getSearchableKeywords() {
        List result = super.getSearchableKeywords();
        result.add(KIND_KEY);
        result.add(POM_GID_KEY);
        result.add(POM_AID_KEY);
        result.add(POM_PCK_KEY);
        result.add(POM_URL_KEY);
        return result;
    }

    protected Document postProcessDocument(ItemProperties ip, Document doc) {
        // if we have "meat" we should do our job
        if (MavenArtifactRecognizer.isPom(ip.getName())) {
            doc.add(Field.Keyword(KIND_KEY, KIND_POM));

            Item item = null;
            try {

                item = retrieveItemFromStorages(ip.getPath());

                MavenXpp3Reader reader = new MavenXpp3Reader();
                InputStreamReader ir = new InputStreamReader(item.getStream());
                Model pom = reader.read(ir);

                if (pom.getGroupId() != null) {
                    doc.add(Field.Keyword(POM_GID_KEY, pom.getGroupId()));
                }
                if (pom.getArtifactId() != null) {
                    doc.add(Field.Keyword(POM_AID_KEY, pom.getArtifactId()));
                }
                if (pom.getPackaging() != null) {
                    doc.add(Field.Keyword(POM_PCK_KEY, pom.getPackaging()));
                }
                if (pom.getUrl() != null) {
                    doc.add(Field.Text(POM_URL_KEY, pom.getUrl()));
                }

            } catch (ItemNotFoundException ex) {
                logger.warn("Could not retrieve " + ip.getPath(), ex);
            } catch (XmlPullParserException ex) {
                logger.warn("Got XmlPullParserException during reading POM, content will not be indexed on "
                        + ip.getPath(), ex);
            } catch (IOException ex) {
                logger.error("Got IOException during reading POM, content will not be indexed on " + ip.getPath(), ex);
            } finally {
                try {
                    item.getStream().close();
                } catch (Exception e) { /* Ignore it */
                }
            }

        } else if (MavenArtifactRecognizer.isMetadata(ip.getName())) {
            doc.add(Field.Keyword(KIND_KEY, KIND_METADATA));
        } else if (MavenArtifactRecognizer.isSnapshot(ip.getName())) {
            doc.add(Field.Keyword(KIND_KEY, KIND_SNAPSHOT));
        }
        return doc;
    }

}
