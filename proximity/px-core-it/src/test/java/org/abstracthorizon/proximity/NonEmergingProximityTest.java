package org.abstracthorizon.proximity;

public class NonEmergingProximityTest extends AbstractProximityIntegrationTest {

	protected String[] getConfigLocations() {
		return new String[] { "/org/abstracthorizon/proximity/applicationContext.xml",
				"/org/abstracthorizon/proximity/proximityHelpers.xml",
				"/org/abstracthorizon/proximity/proximityRepositories.xml"
		};
	}
	
	public void testSomthin() {
		
	}
	
}
