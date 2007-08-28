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
package org.abstracthorizon.proximity.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.abstracthorizon.proximity.ProximityRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class PathBasedGroupRequestMapperTest.
 */
public class PathBasedGroupRequestMapperTest
    extends TestCase
{

    /** The mapper. */
    protected PathBasedGroupRequestMapper mapper;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp()
        throws Exception
    {
        super.setUp();
        mapper = new PathBasedGroupRequestMapper();
        Map incl = new HashMap();
        List publicIncl = new ArrayList();
        publicIncl.add( "/com/mycompany/.*=internal1,internal2" );
        incl.put( "public", publicIncl );
        Map excl = new HashMap();
        List publicExcl = new ArrayList();
        publicExcl.add( "/com/mycompany/.*=central" );
        publicExcl.add( "/com/mycompany/alpha/.*=internal2" );
        publicExcl.add( "/com/something/.*=internal2" );
        publicExcl.add( "/.*=internal3" );
        excl.put( "public", publicExcl );
        mapper.setExclusions( excl );
        mapper.setInclusions( incl );
    }

    /**
     * Test this.
     */
    public void testThis()
    {

        List reposes = new ArrayList();
        reposes.add( "central" );
        reposes.add( "internal1" );
        reposes.add( "internal2" );
        reposes.add( "internal3" );

        ProximityRequest request = new ProximityRequest();
        List result = null;
        List expectedResult = null;

        request.setPath( "/org/apache/maven" );
        System.out.println( request.getPath() );
        result = mapper.getMappedRepositories( "public", request, reposes );
        expectedResult = new ArrayList();
        expectedResult.add( "central" );
        expectedResult.add( "internal1" );
        expectedResult.add( "internal2" );
        System.out.println( result );
        Assert.assertEquals( expectedResult, result );

        request.setPath( "/com/something/somewhere" );
        System.out.println( request.getPath() );
        result = mapper.getMappedRepositories( "public", request, reposes );
        expectedResult = new ArrayList();
        expectedResult.add( "central" );
        expectedResult.add( "internal1" );
        System.out.println( result );
        Assert.assertEquals( expectedResult, result );

        request.setPath( "/com/mycompany/something" );
        System.out.println( request.getPath() );
        result = mapper.getMappedRepositories( "public", request, reposes );
        expectedResult = new ArrayList();
        expectedResult.add( "internal1" );
        expectedResult.add( "internal2" );
        System.out.println( result );
        Assert.assertEquals( expectedResult, result );

        request.setPath( "/com/mycompany/alpha/something" );
        System.out.println( request.getPath() );
        result = mapper.getMappedRepositories( "public", request, reposes );
        expectedResult = new ArrayList();
        expectedResult.add( "internal1" );
        System.out.println( result );
        Assert.assertEquals( expectedResult, result );

        request.setPath( "/com/mycompany/beta/something" );
        System.out.println( request.getPath() );
        result = mapper.getMappedRepositories( "public", request, reposes );
        expectedResult = new ArrayList();
        expectedResult.add( "internal1" );
        expectedResult.add( "internal2" );
        System.out.println( result );
        Assert.assertEquals( expectedResult, result );

    }

    /**
     * Test listing.
     */
    public void testListing()
    {
        System.out.println( mapper.toString() );
    }

}
