package hu.ismicro.commons.proximity.maven;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MavenArtifactRecognizerTest extends TestCase {

    public void testIsPom() {
        Assert.assertEquals(true, MavenArtifactRecognizer.isPom("aaa.pom"));
        Assert.assertEquals(true, MavenArtifactRecognizer.isPom("zxc-1-2-3.pom"));
        Assert.assertEquals(false, MavenArtifactRecognizer.isPom("aaa.jar"));
        Assert.assertEquals(false, MavenArtifactRecognizer.isPom("aaa.pom-a"));
    }

    public void testIsSnapshot() {
        Assert.assertEquals(true, MavenArtifactRecognizer.isSnapshot("xsd-SNAPSHOT.jar"));
        Assert.assertEquals(true, MavenArtifactRecognizer.isSnapshot("xsd-SNAPSHOT.pom"));
        Assert.assertEquals(true, MavenArtifactRecognizer.isSnapshot("/a/b/c/xsd-1.2.3-SNAPSHOT.pom"));
        Assert.assertEquals(false, MavenArtifactRecognizer.isSnapshot("xsd-SNAPsHOT.jar"));
        Assert.assertEquals(false, MavenArtifactRecognizer.isSnapshot("xsd-SNAPHOT.pom"));
        Assert.assertEquals(false, MavenArtifactRecognizer.isSnapshot("/a/b/c/xsd-1.2.3NAPSHOT.pom"));
    }

    public void testIsMetadata() {
        Assert.assertEquals(true, MavenArtifactRecognizer.isMetadata("maven-metadata.xml"));
        Assert.assertEquals(false, MavenArtifactRecognizer.isMetadata("aven-metadata.xml"));
    }

}
