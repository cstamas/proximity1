package hu.ismicro.commons.proximity;

import hu.ismicro.commons.proximity.base.ProxiedItemProperties;

import java.util.List;
import java.util.Map;

public class ProximityIntegrationTest extends AbstractProximityIntegrationTest {

    private Proximity proximity;

    protected String[] getConfigLocations() {
        return new String[] { "/hu/ismicro/commons/proximity/applicationContext.xml" };
    }

    public void setProximity(Proximity proximity) {
        this.proximity = proximity;
    }

    public Proximity getProximity() {
        return proximity;
    }

    protected ProximityRequest getRequest(String path) {
        ProximityRequest request = new ProximityRequest();
        request.setPath(path);
        request.getAttributes().put(ProximityRequest.REQUEST_REMOTE_ADDRESS, "127.0.0.1");
        return request;
    }

    public void testSimpleArtifact() {
        try {
            Item item = proximity.retrieveItem(getRequest("/antlr/antlr/2.7.5/antlr-2.7.5.jar"));
            logger.info("Got response of type " + item.getClass() + ":" + item);
        } catch (ProximityException ex) {
            logger.error("Got exception", ex);
            fail();
        }
        try {
            Item item = proximity.retrieveItem(getRequest("/antlr/antlr/2.7.5/antlr-2.7.5.jar-NO_SUCH"));
            logger.info("Got response of type " + item.getClass() + ":" + item);
            fail();
        } catch (ProximityException ex) {
            logger.error("Good, got exception", ex);
        }
    }

    public void testPomArtifact() {
        try {
            Item item = proximity.retrieveItem(getRequest("/antlr/poms/antlr-2.7.5.pom"));
            logger.info("Got response of type " + item.getClass() + ":" + item);
        } catch (ProximityException ex) {
            logger.error("Good, got exception", ex);
            fail();
        }
    }

    public void testMetadatadaArtifact() {
        try {
            Item item = proximity.retrieveItem(getRequest("/ant/ant/maven-metadata.xml"));
            logger.info("Got response of type " + item.getClass() + ":" + item);
        } catch (ProximityException ex) {
            logger.error("Got exception", ex);
            fail();
        }
        try {
            Item item = proximity.retrieveItem(getRequest("/ant/ant/maven-metadata.xml.md5"));
            logger.info("Got response of type " + item.getClass() + ":" + item);
        } catch (ProximityException ex) {
            logger.error("Got exception", ex);
            fail();
        }
    }

    public void testSearchByExample1() {
        ProxiedItemProperties example = new ProxiedItemProperties();
        example.setName("antlr-2.7.5.jar");
        List result = proximity.searchItem(example);
        logger.info("Got search result:" + result);
        example = new ProxiedItemProperties();
        example.setName("antlr*");
        result = proximity.searchItem(example);
        logger.info("Got search result:" + result);
        example = new ProxiedItemProperties();
        example.setAbsolutePath("/ismicro/jars");
        result = proximity.searchItem(example);
        logger.info("Got search result:" + result);
    }

    public void testSimpleDir() {
        try {
            List items = proximity.listItems(getRequest("/"));
            logger.info("Got response of type " + items.getClass() + ":" + items);
            items = proximity.listItems(getRequest("/ismicro"));
            logger.info("Got response of type " + items.getClass() + ":" + items);
            items = proximity.listItems(getRequest("/ismicro/jars"));
            logger.info("Got response of type " + items.getClass() + ":" + items);
        } catch (ProximityException ex) {
            logger.error("Got ex but i should not have it!", ex);
            fail();
        }
    }

    public void testStats() {
        Map stats = proximity.getStatistics();
        logger.info("Got stats " + stats);
    }

}
