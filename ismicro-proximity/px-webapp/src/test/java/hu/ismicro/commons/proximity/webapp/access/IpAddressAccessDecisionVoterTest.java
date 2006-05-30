package hu.ismicro.commons.proximity.webapp.access;

import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.access.AccessDecisionVoter;
import junit.framework.Assert;
import junit.framework.TestCase;

public class IpAddressAccessDecisionVoterTest extends TestCase {
    
    private IpAddressAccessDecisionVoter voter = new IpAddressAccessDecisionVoter();
    
    protected ProximityRequest getDummyRq(String ipAddr) {
        ProximityRequest result = new ProximityRequest();
        result.getAttributes().put(ProximityRequest.REQUEST_REMOTE_ADDRESS, ipAddr);
        return result;
    }
    
    public void testApproved1() {
        voter.setAllowDeny(true);
        voter.setAllowFromPattern("192\\.168\\.0\\..*");
        voter.setDenyFromPattern(".*");
        Assert.assertEquals(AccessDecisionVoter.ACCESS_APPROVED, voter.vote(getDummyRq("192.168.0.1"), null));
    }

    public void testApproved2() {
        voter.setAllowDeny(false);
        voter.setAllowFromPattern("192\\.168\\.0\\..*");
        voter.setDenyFromPattern("192\\.169.*");
        Assert.assertEquals(AccessDecisionVoter.ACCESS_APPROVED, voter.vote(getDummyRq("192.168.0.1"), null));
    }

    public void testDenied1() {
        voter.setAllowDeny(true);
        voter.setAllowFromPattern("192\\.168\\.0\\..*");
        voter.setDenyFromPattern(".*");
        Assert.assertEquals(AccessDecisionVoter.ACCESS_DENIED, voter.vote(getDummyRq("142.18.0.1"), null));
    }

    public void testDenied2() {
        voter.setAllowDeny(false);
        voter.setAllowFromPattern("192\\.168\\.0\\..*");
        voter.setDenyFromPattern("192\\.169.*");
        Assert.assertEquals(AccessDecisionVoter.ACCESS_DENIED, voter.vote(getDummyRq("192.169.0.1"), null));
    }

}
