package hu.ismicro.commons.proximity;

import hu.ismicro.commons.proximity.base.SimpleProximityRequest;

public class ProximityIntegrationTest extends AbstractProximityIntegrationTest {
    
    private Proximity proximity;

    protected String[] getConfigLocations() {
        return new String[] { "/hu/ismicro/commons/proximity/ProximityContext.xml" };
    }

    public void setProximity(Proximity proximity) {
        this.proximity = proximity;
    }

    public Proximity getProximity() {
        return proximity;
    }
    
    public void testSimple() {
        SimpleProximityRequest request = new SimpleProximityRequest();
        request.setPath("antlr/jars/antlr-2.7.1.jar");
        ProximityResponse response = proximity.handleRequest(request);
        logger.info(response.getClass());
    }

}
