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

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Class that simply "recognizes" the item by its name.
 * <p>
 * It works simply by substring matching like in case of POM: <tty>
 * name.endsWith(".pom") || name.endsWith(".pom.sha1") || name.endsWith(".pom.md5")
 * </tty>
 * 
 * <p>
 * This is "ugly" but simple implementation.
 * 
 * @author cstamas
 */
public class MavenArtifactRecognizer
{

    /**
     * Is this item M1/M2 Checksum?.
     * 
     * @param name the full name of item.
     * 
     * @return true if it is Checksum, false otherwise.
     */
    public static boolean isChecksum( String name )
    {
        return name.endsWith( ".sha1" ) || name.endsWith( ".md5" );
    }

    /**
     * Is this item M1/M2 POM?.
     * 
     * @param name the full name of item.
     * 
     * @return true if it is M1/M2 POM, false otherwise.
     */
    public static boolean isPom( String name )
    {
        return name.endsWith( ".pom" ) || name.endsWith( ".pom.sha1" ) || name.endsWith( ".pom.md5" );
    }

    /**
     * Is this item M1/M2 snapshot?.
     * 
     * @param name the full name of item.
     * @param dir the dir
     * 
     * @return true if it is M1/M2 snapshot, false otherwise.
     */
    public static boolean isSnapshot( String dir, String name )
    {
        // TODO: review SNAPSHOT recognition, in M2 thins changed!
        // the file is in form:
        // appassembler-maven-plugin-1.0-20060714.142547-1.pom
        return dir.indexOf( "SNAPSHOT" ) != -1 || name.indexOf( "SNAPSHOT" ) != -1;
    }

    /**
     * Is this item M2 metadata?.
     * 
     * @param name the full name of item.
     * 
     * @return true if it is M2 metadata, false otherwise.
     */
    public static boolean isMetadata( String name )
    {
        return name.endsWith( "maven-metadata.xml" ) || name.endsWith( "maven-metadata.xml.sha1" )
            || name.endsWith( "maven-metadata.xml.md5" );
    }

}
