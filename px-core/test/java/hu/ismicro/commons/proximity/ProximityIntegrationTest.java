package hu.ismicro.commons.proximity;

import java.util.List;

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
    
    public void testSimpleArtifact() {
        Item item = proximity.retrieveItem("antlr/jars/antlr-2.7.1.jar");
        logger.info("Got response of type " + item.getClass() + ":" + item);
    }

    public void testSimpleDir() {
        List items = proximity.listItems("/");
        logger.info("Got response of type " + items.getClass() + ":" + items);
        items = proximity.listItems("/ismicro");
        logger.info("Got response of type " + items.getClass() + ":" + items);
        items = proximity.listItems("/ismicro/jars");
        logger.info("Got response of type " + items.getClass() + ":" + items);
    }

}
