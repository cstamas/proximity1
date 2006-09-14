package org.abstracthorizon.proximity.impl;

import org.abstracthorizon.proximity.impl.LogicDrivenRepositoryImpl;

import junit.framework.Assert;
import junit.framework.TestCase;

public class LogicDrivenRepositoryImplTest extends TestCase {

    private LogicDrivenRepositoryImpl repo = new LogicDrivenRepositoryImpl();

    /** Dummy method */
    public void testDummy() {
        Assert.assertEquals(repo.getGroupId(), repo.getGroupId());
    }

}
