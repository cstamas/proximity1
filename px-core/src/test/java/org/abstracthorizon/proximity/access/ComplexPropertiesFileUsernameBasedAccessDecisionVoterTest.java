package org.abstracthorizon.proximity.access;

import java.util.Properties;

import junit.framework.TestCase;

import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.impl.LogicDrivenRepositoryImpl;

public class ComplexPropertiesFileUsernameBasedAccessDecisionVoterTest
    extends TestCase
{
    protected Repository repository1;

    protected Repository repository2;

    protected ComplexPropertiesFileUsernameBasedAccessDecisionVoter voter;

    public void setUp()
        throws Exception
    {
        super.setUp();
        repository1 = new LogicDrivenRepositoryImpl();
        repository1.setId( "repo1" );
        repository2 = new LogicDrivenRepositoryImpl();
        repository2.setId( "repo2" );

        Properties defaults = new Properties();
        defaults.load( this.getClass().getResourceAsStream( "/org/abstracthorizon/proximity/access/complex-defaults.properties" ) );

        voter = new ComplexPropertiesFileUsernameBasedAccessDecisionVoter();
        voter.setPropertiesBase( "/org/abstracthorizon/proximity/access/" );
        voter.setDefaultRights( defaults );
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
        request = new ProximityRequest( "/some/path" );
        request.getAttributes().put( UsernameBasedAccessDecisionVoter.REQUEST_USERNAME, "brian" );
        // This part is made in frontend -- END
        assertEquals( AccessDecisionVoter.ACCESS_APPROVED, voter.vote(
            request,
            repository2,
            RepositoryPermission.LIST ) );
    }

    public void testDenied()
    {
        // This part is made in frontend -- BEGIN
        ProximityRequest request = new ProximityRequest( "/some/path/to/read" );
        request.getAttributes().put( UsernameBasedAccessDecisionVoter.REQUEST_USERNAME, "cstamas" );
        // This part is made in frontend -- END
        assertEquals( AccessDecisionVoter.ACCESS_DENIED, voter.vote(
            request,
            repository2,
            RepositoryPermission.STORE ) );

        // This part is made in frontend -- BEGIN
        request = new ProximityRequest( "/some/other/path/to/deploy" );
        request.getAttributes().put( UsernameBasedAccessDecisionVoter.REQUEST_USERNAME, "brian" );
        // This part is made in frontend -- END
        assertEquals( AccessDecisionVoter.ACCESS_DENIED, voter.vote(
            request,
            repository1,
            RepositoryPermission.DELETE ) );
    }

    public void testNoAccessConfig()
    {
        // This part is made in frontend -- BEGIN
        ProximityRequest request = new ProximityRequest( "/org/sonatype" );
        request.getAttributes().put( UsernameBasedAccessDecisionVoter.REQUEST_USERNAME, "somebody" );
        // This part is made in frontend -- END
        assertEquals( AccessDecisionVoter.ACCESS_DENIED, voter.vote(
            request,
            repository2,
            RepositoryPermission.STORE ) );

        assertEquals( AccessDecisionVoter.ACCESS_APPROVED, voter.vote(
            request,
            repository2,
            RepositoryPermission.LIST ) );
    }

    public void testNonExistentRepo()
    {
        // This part is made in frontend -- BEGIN
        ProximityRequest request = new ProximityRequest( "/some/path" );
        request.getAttributes().put( UsernameBasedAccessDecisionVoter.REQUEST_USERNAME, "cstamas" );
        // This part is made in frontend -- END

        repository1.setId( "nonexistent" );
        
        assertEquals( AccessDecisionVoter.ACCESS_APPROVED, voter.vote(
            request,
            repository1,
            RepositoryPermission.RETRIEVE ) );
    }

}
