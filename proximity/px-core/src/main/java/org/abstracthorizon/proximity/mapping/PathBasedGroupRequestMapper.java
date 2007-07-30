package org.abstracthorizon.proximity.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.abstracthorizon.proximity.ProximityRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathBasedGroupRequestMapper implements GroupRequestMapper {
    
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private boolean compiled = false;

    private Map groupInclusionsPrepared = new HashMap();
    
    private Map groupExclusionsPrepared = new HashMap();

    private Map groupInclusions = new HashMap();
    
    private Map groupExclusions = new HashMap();

    public List getMappedRepositories(String groupId, ProximityRequest request, List originalRepositoryGroupOrder) {
	if (!compiled) {
	    compile();
	}
	List reposList = new ArrayList();
	// if include found, add it to the list.
	if (groupInclusionsPrepared.containsKey(groupId)) {
	    Map inclusions = (Map) groupInclusionsPrepared.get(groupId);
	    for (Iterator i = inclusions.keySet().iterator(); i.hasNext();) {
		Pattern pattern = (Pattern) i.next();
		if (pattern.matcher(request.getPath()).matches()) {
		    reposList.addAll((List) inclusions.get(pattern));
		}
	    }
	}
	// then, if exlude found, remove it.
	if (groupExclusionsPrepared.containsKey(groupId)) {
	    Map exclusions = (Map) groupExclusionsPrepared.get(groupId);
	    for (Iterator i = exclusions.keySet().iterator(); i.hasNext();) {
		Pattern pattern = (Pattern) i.next();
		if (pattern.matcher(request.getPath()).matches()) {
		    reposList.removeAll((List) exclusions.get(pattern));
		}
	    }
	}
	// at the end, if the list is empty, add all repos
	// if reposList is empty, return original list
	if (reposList.isEmpty()) {
	    logger.debug("No mapping exists for request path, using all repository group members for request.");
	    return originalRepositoryGroupOrder;
	} else {
	    logger.info("Request path in group {} is mapped, using only {} group members for request.", reposList.toString());
	    return reposList;
	}
    }
    
    protected void compile() {
	HashMap res = new HashMap(groupInclusions.size());
	for (Iterator i = groupInclusions.keySet().iterator(); i.hasNext();) {
	    String groupId = (String) i.next();
	    List incList = (List) groupInclusions.get(groupId);
	    Map gRes = new HashMap(incList.size());
	    for (Iterator j = incList.iterator(); j.hasNext();) {
		String inc = (String) j.next();
		String regexp = inc.substring(0, inc.indexOf("="));
		String reposes = inc.substring(inc.indexOf("=") + 1, inc.length());
		List reposList = Arrays.asList(reposes.split(","));
		gRes.put(Pattern.compile(regexp), reposList);
	    }
	    res.put(groupId, gRes);
	}
	this.groupInclusionsPrepared = res;

	res = new HashMap(groupExclusions.size());
	for (Iterator i = groupExclusions.keySet().iterator(); i.hasNext();) {
	    String groupId = (String) i.next();
	    List excList = (List) groupExclusions.get(groupId);
	    Map gRes = new HashMap(excList.size());
	    for (Iterator j = excList.iterator(); j.hasNext();) {
		String exc = (String) j.next();
		String regexp = exc.substring(0, exc.indexOf("="));
		String reposes = exc.substring(exc.indexOf("=") + 1, exc.length());
		List reposList = Arrays.asList(reposes.split(","));
		gRes.put(Pattern.compile(regexp), reposList);
	    }
	    res.put(groupId, gRes);
	}
	this.groupExclusionsPrepared = res;
	this.compiled = true;
    }

    public void setInclusions(Map inclusions) {
	this.groupInclusions = inclusions;
	this.compiled = false;
    }
    
    public void setExclusions(Map exclusions) {
	this.groupExclusions = exclusions;
	this.compiled = false;
    }

    public Map getInclusions() {
	return this.groupInclusions;
    }
    
    public Map getExclusions() {
	return this.groupExclusions;
    }
    
    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append("INCLUSIONS:\n");
	Map inc = getInclusions();
	for (Iterator groups = inc.keySet().iterator(); groups.hasNext(); ) {
	    String groupId = (String) groups.next();
	    List gInc = (List) inc.get(groupId);
	    for (Iterator patterns = gInc.iterator(); patterns.hasNext(); ) {
		sb.append(patterns.next());
		sb.append("\n");
	    }
		
	}
	sb.append("EXCLUSIONS:\n");
	Map exc = getExclusions();
	for (Iterator groups = exc.keySet().iterator(); groups.hasNext(); ) {
	    String groupId = (String) groups.next();
	    List gExc = (List) exc.get(groupId);
	    for (Iterator patterns = gExc.iterator(); patterns.hasNext(); ) {
		sb.append(patterns.next());
		sb.append("\n");
	    }
		
	}
	return sb.toString();
    }

}
