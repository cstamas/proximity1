package hu.ismicro.commons.proximity.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractStorage implements Storage {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected ProxiedItemPropertiesFactory proxiedItemPropertiesConstructor = new InspectorListProcessingProxiedItemPropertiesConstructor();

    public void setProxiedItemPropertiesFactory(ProxiedItemPropertiesFactory pic) {
        this.proxiedItemPropertiesConstructor = pic;
    }
    
    public ProxiedItemPropertiesFactory getProxiedItemPropertiesFactory() {
        return this.proxiedItemPropertiesConstructor;
    }
    
}
