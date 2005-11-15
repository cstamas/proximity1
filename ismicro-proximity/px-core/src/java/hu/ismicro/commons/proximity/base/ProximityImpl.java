package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.BrowsingNotAllowedException;
import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.Proximity;
import hu.ismicro.commons.proximity.ProximityException;
import hu.ismicro.commons.proximity.ProximityRequest;
import hu.ismicro.commons.proximity.ProximityResponse;
import hu.ismicro.commons.proximity.Repository;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProximityImpl implements Proximity {
	
	protected Log logger = LogFactory.getLog(this.getClass());

	private List repositories;

	public void setRepositories(List repositories) {
		this.repositories = repositories;
	}

	public List getRepositories() {
		return repositories;
	}

	public ProximityResponse handleRequest(ProximityRequest request)
			throws ProximityException {
		ProximityResponse response = null;
		for (Iterator i = repositories.iterator(); i.hasNext();) {
			Repository repo = (Repository) i.next();
			try {
				if (response == null) {
					response = repo.handleRequest(request);
					if (!response.isMergeableResponse()) {
						return response;
					}
				} else {
					response.mergeResponses(repo.handleRequest(request));
				}
			} catch (BrowsingNotAllowedException ex) {
				logger.info("Browsing of repository " + repo.getName() + " is forbidden!");
			} catch (ItemNotFoundException ex) {
				logger.info("Item " + request.getPath() + " not found in repository " + repo.getName());
			}
		}
		return response;
	}

}
