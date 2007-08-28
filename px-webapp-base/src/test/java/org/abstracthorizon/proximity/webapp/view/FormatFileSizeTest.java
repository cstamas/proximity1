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
package org.abstracthorizon.proximity.webapp.view;

import junit.framework.TestCase;

// TODO: Auto-generated Javadoc
/**
 * The Class FormatFileSizeTest.
 */
public class FormatFileSizeTest
    extends TestCase
{

    /** The ffs. */
    private FormatFileSize ffs = new FormatFileSize();

    /**
     * Test simple.
     */
    public void testSimple()
    {
        System.out.println( "1=" + ffs.getFileSizeAsString( "1" ) );
        System.out.println( "1a=" + ffs.getFileSizeAsString( "1a" ) );
        System.out.println( "1024=" + ffs.getFileSizeAsString( "1024" ) );
        System.out.println( "2545=" + ffs.getFileSizeAsString( "2544" ) );
        System.out.println( "34436=" + ffs.getFileSizeAsString( "34436" ) );
        System.out.println( "344363=" + ffs.getFileSizeAsString( "344363" ) );
        System.out.println( "3443633=" + ffs.getFileSizeAsString( "3443633" ) );
        System.out.println( "34436332=" + ffs.getFileSizeAsString( "34436332" ) );
        System.out.println( "344363329=" + ffs.getFileSizeAsString( "344363329" ) );
        System.out.println( "1040548345=" + ffs.getFileSizeAsString( "1040548345" ) );
        System.out.println( "1540548345=" + ffs.getFileSizeAsString( "1540548345" ) );
        System.out.println( "41540548345=" + ffs.getFileSizeAsString( "41540548345" ) );
    }

}
