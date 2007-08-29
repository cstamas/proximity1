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

import java.util.Properties;

import junit.framework.TestCase;

import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.impl.LogicDrivenRepositoryImpl;

// TODO: Auto-generated Javadoc
/**
 * The Class SimplePropertiesFileUsernameBasedAccessDecisionVoterTest.
 */
public class SimplePropertiesFileUsernameBasedAccessDecisionVoterTest
    extends TestCase
{
    
    /** The repository1. */
    protected Repository repository1;

    /** The repository2. */
    protected Repository repository2;

    /** The voter. */
    protected SimplePropertiesFileUsernameBasedAccessDecisionVoter voter;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp()
        throws Exception
    {
        super.setUp();
        repository1 = new LogicDrivenRepositoryImpl();
        repository1.setId( "repo1" );
        repository2 = new LogicDrivenRepositoryImpl();
        repository2.setId( "repo2" );

        Properties props = new Properties();
        props.load( this.getClass().getResourceAsStream( "/org/abstracthorizon/proximity/access/simple.properties" ) );

        voter = new SimplePropertiesFileUsernameBasedAccessDecisionVoter();
        voter.setAuthProperties( props );
    }

    /**
     * Test approved.
     */
    public void testApproved()
    {
        // This part is made in frontend -- BEGIN
        ProximityRequest request = new ProximityRequest( "/some/path" );
        request.getAttributes().put( UsernameBasedAccessDecisionVoter.REQUEST_USERNAME, "cstamas" );
        // This part is made in frontend -- END

        assertEquals( AccessDecisionVoter.ACCESS_APPROVED, voter.vote(
            request,
            repository1,
            RepositoryPermission.RETRIEVE ) );

        // This part is made in frontend -- BEGIN
        request.getAttributes().put( UsernameBasedAccessDecisionVoter.REQUEST_USERNAME, "brian" );
        // This part is made in frontend -- END
        assertEquals( AccessDecisionVoter.ACCESS_APPROVED, voter.vote(
            request,
            repository2,
            RepositoryPermission.RETRIEVE ) );
    }

    /**
     * Test denied.
     */
    public void testDenied()
    {
        // This part is made in frontend -- BEGIN
        ProximityRequest request = new ProximityRequest( "/some/path" );
        request.getAttributes().put( UsernameBasedAccessDecisionVoter.REQUEST_USERNAME, "cstamas" );
        // This part is made in frontend -- END
        assertEquals( AccessDecisionVoter.ACCESS_DENIED, voter.vote(
            request,
            repository2,
            RepositoryPermission.RETRIEVE ) );

        // This part is made in frontend -- BEGIN
        request.getAttributes().put( UsernameBasedAccessDecisionVoter.REQUEST_USERNAME, "brian" );
        // This part is made in frontend -- END
        assertEquals( AccessDecisionVoter.ACCESS_DENIED, voter.vote(
            request,
            repository1,
            RepositoryPermission.RETRIEVE ) );
    }

    /**
     * Test non existent repo.
     */
    public void testNonExistentRepo()
    {
        // This part is made in frontend -- BEGIN
        ProximityRequest request = new ProximityRequest( "/some/path" );
        request.getAttributes().put( UsernameBasedAccessDecisionVoter.REQUEST_USERNAME, "cstamas" );
        // This part is made in frontend -- END

        repository1.setId( "nonexistent" );

        assertEquals( AccessDecisionVoter.ACCESS_DENIED, voter.vote(
            request,
            repository2,
            RepositoryPermission.RETRIEVE ) );
    }

}
