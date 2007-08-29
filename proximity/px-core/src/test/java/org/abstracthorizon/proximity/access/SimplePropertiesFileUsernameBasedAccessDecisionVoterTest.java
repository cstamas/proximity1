package org.abstracthorizon.proximity.access;

import java.util.Properties;

import junit.framework.TestCase;

import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.impl.LogicDrivenRepositoryImpl;

public class SimplePropertiesFileUsernameBasedAccessDecisionVoterTest
    extends TestCase
{
    protected Repository repository1;

    protected Repository repository2;

    protected SimplePropertiesFileUsernameBasedAccessDecisionVoter voter;

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
