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
		try {
			Item item = proximity.retrieveItem("/antlr/jars/antlr-2.7.1.jar");
			logger.info("Got response of type " + item.getClass() + ":" + item);
		} catch (ItemNotFoundException ex) {
			logger.error("Got exception", ex);
			fail();
		}
		try {
			Item item = proximity.retrieveItem("/antlr/jars/antlr-2.7.1.jar-not-exists");
			logger.info("Got response of type " + item.getClass() + ":" + item);
			fail();
		} catch (ItemNotFoundException ex) {
			logger.error("Good, got exception", ex);
		}
		List items = proximity.listItems("/antlr/jars");
		logger.info("Got response of type " + items.getClass() + ":" + items);
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
