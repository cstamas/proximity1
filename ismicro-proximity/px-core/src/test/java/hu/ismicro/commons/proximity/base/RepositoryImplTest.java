package hu.ismicro.commons.proximity.base;

import junit.framework.Assert;
import junit.framework.TestCase;

public class RepositoryImplTest extends TestCase {
    
    private RepositoryImpl repo = new RepositoryImpl();
    
    public void testPrefixRemoval() {
        repo.setUriPrefix("/someprefix");
        Assert.assertEquals("/test", repo.removePathPrefix("/someprefix/test"));
        Assert.assertEquals("/test?a=b", repo.removePathPrefix("/someprefix/test?a=b"));
    }

    public void testPrefixAddition() {
        repo.setUriPrefix("/someprefix");
        Assert.assertEquals("/someprefix/test", repo.putPathPrefix("/test"));
        Assert.assertEquals("/someprefix/test?a=b", repo.putPathPrefix("/test?a=b"));
    }

}
