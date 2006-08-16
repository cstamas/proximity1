package hu.ismicro.commons.proximity.base.inspectors;

import hu.ismicro.commons.proximity.base.ItemInspector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractItemInspector implements ItemInspector {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

}
