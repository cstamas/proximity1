package hu.ismicro.commons.proximity.maven;

import hu.ismicro.commons.proximity.impl.ItemPropertiesImpl;
import hu.ismicro.commons.proximity.metadata.inspectors.AbstractItemInspector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class MavenItemInspector extends AbstractItemInspector {

    public static final String M2KIND = "m2kind";

    public static final String KIND_POM = "pom";

    public static final String KIND_METADATA = "metadata";

    public static final String KIND_SNAPSHOT = "snapshot";

    public static final String POM_GID_KEY = "pom.gid";

    public static final String POM_AID_KEY = "pom.aid";

    public static final String POM_VERSION_KEY = "pom.version";

    public static final String POM_PCK_KEY = "pom.pck";

    public static final String POM_URL_KEY = "pom.url";

    public static final String POM_DESCRIPTION_KEY = "pom.prjDesc";

    public boolean isHandled(ItemPropertiesImpl ip) {
        return MavenArtifactRecognizer.isPom(ip.getName()) || MavenArtifactRecognizer.isMetadata(ip.getName())
                || MavenArtifactRecognizer.isSnapshot(ip.getAbsolutePath(), ip.getName());
    }

    public List getIndexableKeywords() {
        List result = new ArrayList(4);
        result.add(M2KIND);
        result.add(POM_GID_KEY);
        result.add(POM_AID_KEY);
        result.add(POM_PCK_KEY);
        result.add(POM_VERSION_KEY);
        return result;
    }

    public void processItem(ItemPropertiesImpl ip, File file) {
        if (MavenArtifactRecognizer.isPom(ip.getName())) {

            if (!MavenArtifactRecognizer.isChecksum(ip.getName())) {

                ip.setMetadata(M2KIND, KIND_POM);

                try {

                    MavenXpp3Reader reader = new MavenXpp3Reader();
                    InputStreamReader ir = new InputStreamReader(new FileInputStream(file));
                    Model pom = reader.read(ir);

                    if (pom.getGroupId() != null) {
                        ip.setMetadata(POM_GID_KEY, pom.getGroupId());
                    } else {
                        if (pom.getParent().getGroupId() != null) {
                            ip.setMetadata(POM_GID_KEY, pom.getParent().getGroupId());
                        }
                    }
                    if (pom.getArtifactId() != null) {
                        ip.setMetadata(POM_AID_KEY, pom.getArtifactId());
                    }
                    if (pom.getPackaging() != null) {
                        ip.setMetadata(POM_PCK_KEY, pom.getPackaging());
                    }
                    if (pom.getVersion() != null) {
                        ip.setMetadata(POM_VERSION_KEY, pom.getVersion());
                    }
                    if (pom.getUrl() != null) {
                        ip.setMetadata(POM_URL_KEY, pom.getUrl());
                    }
                    if (pom.getDescription() != null) {
                        ip.setMetadata(POM_DESCRIPTION_KEY, pom.getDescription());
                    }

                } catch (XmlPullParserException ex) {
                    logger.warn("Got XmlPullParserException during reading POM, content will not be indexed on "
                            + ip.getPath(), ex);
                } catch (IOException ex) {
                    logger.error("Got IOException during reading POM, content will not be indexed on " + ip.getPath(),
                            ex);
                }

            }

        } else if (MavenArtifactRecognizer.isMetadata(ip.getName())) {
            ip.setMetadata(M2KIND, KIND_METADATA);
        } else if (MavenArtifactRecognizer.isSnapshot(ip.getAbsolutePath(), ip.getName())) {
            ip.setMetadata(M2KIND, KIND_SNAPSHOT);
        }
    }

}
