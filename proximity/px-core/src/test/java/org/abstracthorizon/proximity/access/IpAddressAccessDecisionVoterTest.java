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
package org.abstracthorizon.proximity.access;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.access.AccessDecisionVoter;

// TODO: Auto-generated Javadoc
/**
 * The Class IpAddressAccessDecisionVoterTest.
 */
public class IpAddressAccessDecisionVoterTest
    extends TestCase
{

    /** The voter. */
    private IpAddressAccessDecisionVoter voter = new IpAddressAccessDecisionVoter();

    /**
     * Gets the dummy rq.
     * 
     * @param ipAddr the ip addr
     * 
     * @return the dummy rq
     */
    protected ProximityRequest getDummyRq( String ipAddr )
    {
        ProximityRequest result = new ProximityRequest();
        result.getAttributes().put( ProximityRequest.REQUEST_REMOTE_ADDRESS, ipAddr );
        return result;
    }

    /**
     * Test approved1.
     */
    public void testApproved1()
    {
        voter.setAllowDeny( true );
        voter.setAllowFromPattern( "192\\.168\\.0\\..*" );
        voter.setDenyFromPattern( ".*" );
        Assert.assertEquals( AccessDecisionVoter.ACCESS_APPROVED, voter.vote( getDummyRq( "192.168.0.1" ), null, null ) );
    }

    /**
     * Test approved2.
     */
    public void testApproved2()
    {
        voter.setAllowDeny( false );
        voter.setAllowFromPattern( "192\\.168\\.0\\..*" );
        voter.setDenyFromPattern( "192\\.169.*" );
        Assert.assertEquals( AccessDecisionVoter.ACCESS_APPROVED, voter.vote( getDummyRq( "192.168.0.1" ), null, null ) );
    }

    /**
     * Test denied1.
     */
    public void testDenied1()
    {
        voter.setAllowDeny( true );
        voter.setAllowFromPattern( "192\\.168\\.0\\..*" );
        voter.setDenyFromPattern( ".*" );
        Assert.assertEquals( AccessDecisionVoter.ACCESS_DENIED, voter.vote( getDummyRq( "142.18.0.1" ), null, null ) );
    }

    /**
     * Test denied2.
     */
    public void testDenied2()
    {
        voter.setAllowDeny( false );
        voter.setAllowFromPattern( "192\\.168\\.0\\..*" );
        voter.setDenyFromPattern( "192\\.169.*" );
        Assert.assertEquals( AccessDecisionVoter.ACCESS_DENIED, voter.vote( getDummyRq( "192.169.0.1" ), null, null ) );
    }

}
