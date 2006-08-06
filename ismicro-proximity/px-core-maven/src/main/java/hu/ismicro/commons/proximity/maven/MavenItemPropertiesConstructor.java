package hu.ismicro.commons.proximity.maven;

import hu.ismicro.commons.proximity.base.ProxiedItemProperties;
import hu.ismicro.commons.proximity.base.ProxiedItemPropertiesConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class MavenItemPropertiesConstructor implements ProxiedItemPropertiesConstructor {

    protected Log logger = LogFactory.getLog(this.getClass());

    public static final String KIND_KEY = "kind";

    public static final String KIND_POM = "pom";

    public static final String KIND_METADATA = "metadata";

    public static final String KIND_SNAPSHOT = "snapshot";

    public static final String POM_GID_KEY = "pom.gid";

    public static final String POM_AID_KEY = "pom.aid";

    public static final String POM_PCK_KEY = "pom.pck";

    public static final String POM_URL_KEY = "pom.url";

    public static final String POM_VERSION_KEY = "pom.version";

    public static final String POM_DESCRIPTION_KEY = "pom.prjDesc";

    public void expandItemProperties(ProxiedItemProperties ip, File file) {

        if (MavenArtifactRecognizer.isPom(ip.getName())) {

            if (!MavenArtifactRecognizer.isChecksum(ip.getName())) {

                ip.setMetadata(KIND_KEY, KIND_POM, true);

                try {

                    MavenXpp3Reader reader = new MavenXpp3Reader();
                    InputStreamReader ir = new InputStreamReader(new FileInputStream(file));
                    Model pom = reader.read(ir);

                    if (pom.getGroupId() != null) {
                        ip.setMetadata(POM_GID_KEY, pom.getGroupId(), true);
                    }
                    if (pom.getArtifactId() != null) {
                        ip.setMetadata(POM_AID_KEY, pom.getArtifactId(), true);
                    }
                    if (pom.getPackaging() != null) {
                        ip.setMetadata(POM_PCK_KEY, pom.getPackaging(), true);
                    }
                    if (pom.getUrl() != null) {
                        ip.setMetadata(POM_URL_KEY, pom.getUrl(), true);
                    }
                    if (pom.getVersion() != null) {
                        ip.setMetadata(POM_VERSION_KEY, pom.getVersion(), true);
                    }
                    if (pom.getDescription() != null) {
                        ip.setMetadata(POM_DESCRIPTION_KEY, pom.getDescription(), false);
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
            ip.setMetadata(KIND_KEY, KIND_METADATA, true);
        } else if (MavenArtifactRecognizer.isSnapshot(ip.getName())) {
            ip.setMetadata(KIND_KEY, KIND_SNAPSHOT, true);
        }
    }
}