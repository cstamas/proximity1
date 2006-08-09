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

    public void testIsSnapshot1() {
        Assert.assertEquals(true, MavenArtifactRecognizer.isSnapshot("/org/somewhere/aid/1.0-SNAPSHOT", "xsd-SNAPSHOT.jar"));
        Assert.assertEquals(true, MavenArtifactRecognizer.isSnapshot("/org/somewhere/aid/1.0-SNAPSHOT", "xsd-SNAPSHOT.pom"));
        Assert.assertEquals(true, MavenArtifactRecognizer.isSnapshot("/org/somewhere/aid/1.0-SNAPSHOT", "/a/b/c/xsd-1.2.3-SNAPSHOT.pom"));
        Assert.assertEquals(false, MavenArtifactRecognizer.isSnapshot("/org/somewhere/aid/1.0-SNAPSHOT", "xsd-SNAPsHOT.jar"));
        Assert.assertEquals(false, MavenArtifactRecognizer.isSnapshot("/org/somewhere/aid/1.0-SNAPSHOT", "xsd-SNAPHOT.pom"));
        Assert.assertEquals(false, MavenArtifactRecognizer.isSnapshot("/org/somewhere/aid/1.0-SNAPSHOT", "/a/b/c/xsd-1.2.3NAPSHOT.pom"));
    }

    public void testIsSnapshot2() {
        Assert.assertEquals(true, MavenArtifactRecognizer.isSnapshot("/org/somewhere/aid/1.0-SNAPSHOT", "appassembler-maven-plugin-1.0-20060714.142547-1.pom"));
    }

    public void testIsMetadata() {
        Assert.assertEquals(true, MavenArtifactRecognizer.isMetadata("maven-metadata.xml"));
        Assert.assertEquals(false, MavenArtifactRecognizer.isMetadata("aven-metadata.xml"));
    }

}
