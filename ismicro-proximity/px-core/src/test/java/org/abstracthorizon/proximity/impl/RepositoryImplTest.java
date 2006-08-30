package org.abstracthorizon.proximity.impl;

import org.abstracthorizon.proximity.impl.RepositoryImpl;

import junit.framework.Assert;
import junit.framework.TestCase;

public class RepositoryImplTest extends TestCase {

    private RepositoryImpl repo = new RepositoryImpl();

    /** Dummy method */
    public void testDummy() {
        Assert.assertEquals(repo.getGroupId(), repo.getGroupId());
    }

}
