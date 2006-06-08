package hu.ismicro.commons.proximity.base.indexer;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.maven.MavenArtifactRecognizer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class MavenAwareLuceneIndexer extends LuceneIndexer {

    public static final String KIND_KEY = "mavenKind";

    public static final String KIND_POM = "pom";

    public static final String KIND_METADATA = "metadata";

    public static final String KIND_SNAPSHOT = "snapshot";

    protected Document postProcessDocument(Item item, Document doc) {
        if (item.getStream() != null) {
            // if we have "meat" we should do our job
            ItemProperties ip = item.getProperties();
            if (MavenArtifactRecognizer.isPom(ip.getName())) {
                doc.add(Field.Keyword(KIND_KEY, KIND_POM));
                
                

            } else if (MavenArtifactRecognizer.isMetadata(ip.getName())) {
                doc.add(Field.Keyword(KIND_KEY, KIND_METADATA));
            } else if (MavenArtifactRecognizer.isSnapshot(ip.getName())) {
                doc.add(Field.Keyword(KIND_KEY, KIND_SNAPSHOT));
            }
        }
        return doc;
    }

}
