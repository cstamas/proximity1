package org.abstracthorizon.proximity;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public abstract class AbstractProximityIntegrationTest extends AbstractDependencyInjectionSpringContextTests {

    protected String[] getConfigLocations() {
        return new String[] { "/org/abstracthorizon/proximity/applicationContext.xml" };
    }

}
