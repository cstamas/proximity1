package org.abstracthorizon.proximity.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.abstracthorizon.proximity.ItemProperties;
import org.apache.commons.io.FilenameUtils;

public class ProximityUtils {

    public static List explodePathToList(String path) {
	List result = new ArrayList();
	String[] explodedPath = path.split(ItemProperties.PATH_SEPARATOR);
	for (int i = 0; i < explodedPath.length; i++) {
	    if (explodedPath[i].length() > 0) {
		result.add(explodedPath[i]);
	    }
	}
	return result;
    }

    public static void mangleItemPathsForEmergeGroups(List items) {
	for (Iterator i = items.iterator(); i.hasNext();) {
	    ItemProperties ip = (ItemProperties) i.next();
	    if (ip.getDirectoryPath().equals(ItemProperties.PATH_ROOT)) {
		// make /groupId as path
		ip.setDirectoryPath(ItemProperties.PATH_ROOT + ip.getRepositoryGroupId());
	    } else {
		// make /groupId/... as path WITHOUT trailing /
		ip.setDirectoryPath(FilenameUtils.separatorsToUnix(FilenameUtils.normalizeNoEndSeparator(ItemProperties.PATH_ROOT
			+ ip.getRepositoryGroupId() + ip.getDirectoryPath())));
	    }
	}
    }

}
