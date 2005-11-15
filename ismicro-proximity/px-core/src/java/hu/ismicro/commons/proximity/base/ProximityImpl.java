package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.Proximity;
import hu.ismicro.commons.proximity.ProximityException;
import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.ProximityResponse;
import hu.ismicro.commons.proximity.Repository;

import java.util.Iterator;
import java.util.List;

public class ProximityImpl implements Proximity {
    
    private List repositories;
    
    public void setRepositories(List repositories) {
        this.repositories = repositories;
    }

    public List getRepositories() {
        return repositories;
    }

    public ProximityResponse handleRequest(ProximityRequest request) throws ProximityException {
        ProximityResponse response = null;
        for (Iterator i = repositories.iterator(); i.hasNext(); ) {
            Repository repo = (Repository) i.next();
            if (response == null) {
                response = repo.handleRequest(request);
            } else {
                response.mergeResponses(repo.handleRequest(request));
            }
        }
        return response;
    }

}
