package org.abstracthorizon.proximity.storage;

import org.abstracthorizon.proximity.metadata.InspectorListProcessingProxiedItemPropertiesFactory;
import org.abstracthorizon.proximity.metadata.ProxiedItemPropertiesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractStorage implements Storage {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected ProxiedItemPropertiesFactory proxiedItemPropertiesConstructor = new InspectorListProcessingProxiedItemPropertiesFactory();

    public void setProxiedItemPropertiesFactory(ProxiedItemPropertiesFactory pic) {
	this.proxiedItemPropertiesConstructor = pic;
    }

    public ProxiedItemPropertiesFactory getProxiedItemPropertiesFactory() {
	return this.proxiedItemPropertiesConstructor;
    }

}
