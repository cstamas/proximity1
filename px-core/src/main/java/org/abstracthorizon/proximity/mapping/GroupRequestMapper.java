package org.abstracthorizon.proximity.mapping;

import java.util.List;

import org.abstracthorizon.proximity.ProximityRequest;

public interface GroupRequestMapper {
    
    List getMappedRepositories(String groupId, ProximityRequest request, List originalRepositoryGroupOrder);

}
