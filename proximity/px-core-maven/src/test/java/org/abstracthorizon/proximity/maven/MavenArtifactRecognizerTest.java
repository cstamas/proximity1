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

import junit.framework.Assert;
import junit.framework.TestCase;

// TODO: Auto-generated Javadoc
/**
 * The Class MavenArtifactRecognizerTest.
 */
public class MavenArtifactRecognizerTest
    extends TestCase
{

    /**
     * Test is pom.
     */
    public void testIsPom()
    {
        Assert.assertEquals( true, MavenArtifactRecognizer.isPom( "aaa.pom" ) );
        Assert.assertEquals( true, MavenArtifactRecognizer.isPom( "zxc-1-2-3.pom" ) );
        Assert.assertEquals( false, MavenArtifactRecognizer.isPom( "aaa.jar" ) );
        Assert.assertEquals( false, MavenArtifactRecognizer.isPom( "aaa.pom-a" ) );
    }

    /**
     * Test is snapshot1.
     */
    public void testIsSnapshot1()
    {
        Assert.assertEquals( true, MavenArtifactRecognizer.isSnapshot(
            "/org/somewhere/aid/1.0-SNAPSHOT",
            "xsd-SNAPSHOT.jar" ) );
        Assert.assertEquals( true, MavenArtifactRecognizer.isSnapshot(
            "/org/somewhere/aid/1.0-SNAPSHOT",
            "xsd-SNAPSHOT.pom" ) );
        Assert.assertEquals( true, MavenArtifactRecognizer.isSnapshot(
            "/org/somewhere/aid/1.0-SNAPSHOT",
            "/a/b/c/xsd-1.2.3-SNAPSHOT.pom" ) );
        Assert.assertEquals( false, MavenArtifactRecognizer.isSnapshot( "/org/somewhere/aid/1.0", "xsd-SNAPsHOT.jar" ) );
        Assert.assertEquals( false, MavenArtifactRecognizer.isSnapshot( "/org/somewhere/aid/1.0", "xsd-SNAPHOT.pom" ) );
        Assert.assertEquals( false, MavenArtifactRecognizer.isSnapshot(
            "/org/somewhere/aid/1.0",
            "/a/b/c/xsd-1.2.3NAPSHOT.pom" ) );
    }

    /**
     * Test is snapshot2.
     */
    public void testIsSnapshot2()
    {
        Assert.assertEquals( true, MavenArtifactRecognizer.isSnapshot(
            "/org/somewhere/aid/1.0-SNAPSHOT",
            "appassembler-maven-plugin-1.0-20060714.142547-1.pom" ) );
        Assert.assertEquals( false, MavenArtifactRecognizer.isSnapshot(
            "/org/somewhere/aid/1.0",
            "appassembler-maven-plugin-1.0-20060714.142547-1.pom" ) );
    }

    /**
     * Test is metadata.
     */
    public void testIsMetadata()
    {
        Assert.assertEquals( true, MavenArtifactRecognizer.isMetadata( "maven-metadata.xml" ) );
        Assert.assertEquals( false, MavenArtifactRecognizer.isMetadata( "aven-metadata.xml" ) );
    }

}
