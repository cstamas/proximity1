package hu.ismicro.commons.proximity.base.logic;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MavenProxyLogicTest extends TestCase {
    
    private MavenProxyLogic logic = new MavenProxyLogic();
    
    public void testIsPom() {
        Assert.assertEquals(true,logic.isPom("aaa.pom"));
    }


}
