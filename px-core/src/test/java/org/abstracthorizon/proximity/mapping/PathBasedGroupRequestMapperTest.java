package org.abstracthorizon.proximity.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.abstracthorizon.proximity.ProximityRequest;

public class PathBasedGroupRequestMapperTest extends TestCase {

    protected PathBasedGroupRequestMapper mapper;

    public void setUp() throws Exception {
	super.setUp();
	mapper = new PathBasedGroupRequestMapper();
	Map incl = new HashMap();
	List publicIncl = new ArrayList();
	publicIncl.add("/com/mycompany/.*=internal1,internal2");
	incl.put("public", publicIncl);
	Map excl = new HashMap();
	List publicExcl = new ArrayList();
	// publicExcl.add("/com/mycompany/.*=central");
	publicExcl.add("/com/mycompany/alpha/.*=internal2");
	excl.put("public", publicExcl);
	mapper.setExclusions(excl);
	mapper.setInclusions(incl);
    }

    public void testThis() {

	List reposes = new ArrayList();
	reposes.add("central");
	reposes.add("internal1");
	reposes.add("internal2");

	ProximityRequest request = new ProximityRequest();
	List result = null;
	List expectedResult = null;

	request.setPath("/org/apache/maven");
	System.out.println(request.getPath());
	result = mapper.getMappedRepositories("public", request, reposes);
	expectedResult = new ArrayList();
	expectedResult.add("central");
	expectedResult.add("internal1");
	expectedResult.add("internal2");
	System.out.println(result);
	Assert.assertEquals(expectedResult, result);

	request.setPath("/com/mycompany/something");
	System.out.println(request.getPath());
	result = mapper.getMappedRepositories("public", request, reposes);
	expectedResult = new ArrayList();
	expectedResult.add("internal1");
	expectedResult.add("internal2");
	System.out.println(result);
	Assert.assertEquals(expectedResult, result);

	request.setPath("/com/mycompany/alpha/something");
	System.out.println(request.getPath());
	result = mapper.getMappedRepositories("public", request, reposes);
	expectedResult = new ArrayList();
	expectedResult.add("internal1");
	System.out.println(result);
	Assert.assertEquals(expectedResult, result);

	request.setPath("/com/mycompany/beta/something");
	System.out.println(request.getPath());
	result = mapper.getMappedRepositories("public", request, reposes);
	expectedResult = new ArrayList();
	expectedResult.add("internal1");
	expectedResult.add("internal2");
	System.out.println(result);
	Assert.assertEquals(expectedResult, result);

    }
    
    public void testListing() {
	System.out.println(mapper.toString());
    }

}
