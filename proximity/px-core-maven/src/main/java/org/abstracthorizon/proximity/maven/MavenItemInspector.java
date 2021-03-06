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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.metadata.inspectors.AbstractItemInspector;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

// TODO: Auto-generated Javadoc
/**
 * The Class MavenItemInspector.
 */
public class MavenItemInspector
    extends AbstractItemInspector
{

    /** The Constant M2KIND. */
    public static final String M2KIND = "m2kind";

    /** The Constant KIND_POM. */
    public static final String KIND_POM = "pom";

    /** The Constant KIND_METADATA. */
    public static final String KIND_METADATA = "metadata";

    /** The Constant KIND_SNAPSHOT. */
    public static final String KIND_SNAPSHOT = "snapshot";

    /** The Constant POM_GID_KEY. */
    public static final String POM_GID_KEY = "pom.gid";

    /** The Constant POM_AID_KEY. */
    public static final String POM_AID_KEY = "pom.aid";

    /** The Constant POM_VERSION_KEY. */
    public static final String POM_VERSION_KEY = "pom.version";

    /** The Constant POM_VERSION_MAJOR_KEY. */
    public static final String POM_VERSION_MAJOR_KEY = "pom.version.major";

    /** The Constant POM_VERSION_MINOR_KEY. */
    public static final String POM_VERSION_MINOR_KEY = "pom.version.minor";

    /** The Constant POM_VERSION_INCREMENTAL_KEY. */
    public static final String POM_VERSION_INCREMENTAL_KEY = "pom.version.incremental";

    /** The Constant POM_VERSION_BUILDNUM_KEY. */
    public static final String POM_VERSION_BUILDNUM_KEY = "pom.version.buildNum";

    /** The Constant POM_VERSION_QUALIFIER_KEY. */
    public static final String POM_VERSION_QUALIFIER_KEY = "pom.version.qualifier";

    /** The Constant POM_PCK_KEY. */
    public static final String POM_PCK_KEY = "pom.pck";

    /** The Constant POM_URL_KEY. */
    public static final String POM_URL_KEY = "pom.url";

    /** The Constant POM_DESCRIPTION_KEY. */
    public static final String POM_DESCRIPTION_KEY = "pom.prjDesc";

    /** The Constant POM_DEPENDENCIES_KEY. */
    public static final String POM_DEPENDENCIES_KEY = "pom.deps";

    /** The Constant POM_PARENT_KEY. */
    public static final String POM_PARENT_KEY = "pom.parent";

    /* (non-Javadoc)
     * @see org.abstracthorizon.proximity.metadata.inspectors.ItemInspector#isHandled(org.abstracthorizon.proximity.ItemProperties)
     */
    public boolean isHandled( ItemProperties ip )
    {
        return MavenArtifactRecognizer.isPom( ip.getName() ) || MavenArtifactRecognizer.isMetadata( ip.getName() )
            || MavenArtifactRecognizer.isSnapshot( ip.getDirectoryPath(), ip.getName() );
    }

    /* (non-Javadoc)
     * @see org.abstracthorizon.proximity.metadata.inspectors.ItemInspector#getIndexableKeywords()
     */
    public List getIndexableKeywords()
    {
        List result = new ArrayList( 4 );
        result.add( M2KIND );
        result.add( POM_GID_KEY );
        result.add( POM_AID_KEY );
        result.add( POM_PCK_KEY );
        result.add( POM_VERSION_KEY );
        result.add( POM_VERSION_MAJOR_KEY );
        result.add( POM_VERSION_MINOR_KEY );
        result.add( POM_VERSION_INCREMENTAL_KEY );
        result.add( POM_VERSION_BUILDNUM_KEY );
        result.add( POM_VERSION_QUALIFIER_KEY );
        result.add( POM_DEPENDENCIES_KEY );
        result.add( POM_PARENT_KEY );
        return result;
    }

    /* (non-Javadoc)
     * @see org.abstracthorizon.proximity.metadata.inspectors.ItemInspector#processItem(org.abstracthorizon.proximity.ItemProperties, java.io.File)
     */
    public void processItem( ItemProperties ip, File file )
    {
        if ( MavenArtifactRecognizer.isPom( ip.getName() ) )
        {

            if ( !MavenArtifactRecognizer.isChecksum( ip.getName() ) )
            {

                ip.setMetadata( M2KIND, KIND_POM );

                try
                {

                    MavenXpp3Reader reader = new MavenXpp3Reader();
                    FileInputStream fis = new FileInputStream( file );
                    InputStreamReader ir = new InputStreamReader( fis );
                    Model pom = null;
                    try
                    {
                        pom = reader.read( ir );
                        ir.close();
                    }
                    finally
                    {
                        fis.close();
                    }

                    if ( pom.getGroupId() != null )
                    {
                        ip.setMetadata( POM_GID_KEY, pom.getGroupId() );
                    }
                    else
                    {
                        if ( pom.getParent().getGroupId() != null )
                        {
                            ip.setMetadata( POM_GID_KEY, pom.getParent().getGroupId() );
                        }
                    }
                    if ( pom.getArtifactId() != null )
                    {
                        ip.setMetadata( POM_AID_KEY, pom.getArtifactId() );
                    }
                    if ( pom.getPackaging() != null )
                    {
                        ip.setMetadata( POM_PCK_KEY, pom.getPackaging() );
                    }
                    if ( pom.getVersion() != null )
                    {
                        ip.setMetadata( POM_VERSION_KEY, pom.getVersion() );
                    }
                    else
                    {
                        if ( pom.getParent().getVersion() != null )
                        {
                            ip.setMetadata( POM_VERSION_KEY, pom.getParent().getVersion() );
                        }
                    }
                    if ( ip.getMetadata( POM_VERSION_KEY ) != null )
                    {
                        DefaultArtifactVersion af = new DefaultArtifactVersion( ip.getMetadata( POM_VERSION_KEY ) );
                        ip.setMetadata( POM_VERSION_MAJOR_KEY, Integer.toString( af.getMajorVersion() ) );
                        ip.setMetadata( POM_VERSION_MINOR_KEY, Integer.toString( af.getMinorVersion() ) );
                        ip.setMetadata( POM_VERSION_INCREMENTAL_KEY, Integer.toString( af.getIncrementalVersion() ) );
                        ip.setMetadata( POM_VERSION_BUILDNUM_KEY, Integer.toString( af.getBuildNumber() ) );
                        // do not put null
                        if ( af.getQualifier() != null )
                        {
                            ip.setMetadata( POM_VERSION_QUALIFIER_KEY, af.getQualifier() );
                        }
                    }

                    if ( pom.getUrl() != null )
                    {
                        ip.setMetadata( POM_URL_KEY, pom.getUrl() );
                    }
                    if ( pom.getDescription() != null )
                    {
                        ip.setMetadata( POM_DESCRIPTION_KEY, pom.getDescription() );
                    }

                    if ( pom.getParent() != null )
                    {
                        StringBuffer parent = new StringBuffer();
                        parent.append( pom.getParent().getGroupId() );
                        parent.append( ":" );
                        parent.append( pom.getParent().getArtifactId() );
                        if ( pom.getParent().getVersion() != null )
                        {
                            parent.append( ":" );
                            parent.append( pom.getParent().getVersion() );
                        }
                        ip.setMetadata( POM_PARENT_KEY, parent.toString() );
                    }

                    if ( pom.getDependencies() != null )
                    {
                        StringBuffer deps = new StringBuffer();
                        for ( Iterator i = pom.getDependencies().iterator(); i.hasNext(); )
                        {
                            Dependency dep = (Dependency) i.next();
                            deps.append( dep.getGroupId() );
                            deps.append( ":" );
                            deps.append( dep.getArtifactId() );
                            // TODO: version ranges?
                            if ( dep.getVersion() != null && !( dep.getVersion().indexOf( "[" ) > 0 )
                                && !( dep.getVersion().indexOf( "(" ) > 0 ) )
                            {
                                deps.append( ":" );
                                deps.append( dep.getVersion() );
                            }
                            deps.append( "\n" );
                        }
                        ip.setMetadata( POM_DEPENDENCIES_KEY, deps.toString() );
                    }

                }
                catch ( XmlPullParserException ex )
                {
                    logger.warn( "Got XmlPullParserException during reading POM, content will not be indexed on "
                        + ip.getPath(), ex );
                }
                catch ( IOException ex )
                {
                    logger.error(
                        "Got IOException during reading POM, content will not be indexed on " + ip.getPath(),
                        ex );
                }

            }

        }
        else if ( MavenArtifactRecognizer.isMetadata( ip.getName() ) )
        {
            ip.setMetadata( M2KIND, KIND_METADATA );
        }
        else if ( MavenArtifactRecognizer.isSnapshot( ip.getDirectoryPath(), ip.getName() ) )
        {
            ip.setMetadata( M2KIND, KIND_SNAPSHOT );
        }
    }

}
