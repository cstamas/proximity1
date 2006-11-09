package org.abstracthorizon.proximity.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.NoSuchRepositoryException;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.logic.DefaultProximityLogic;
import org.abstracthorizon.proximity.logic.ProximityLogic;

public class LogicDrivenProximityImpl extends AbstractProximity {

	private ProximityLogic proximityLogic = new DefaultProximityLogic();

	public ProximityLogic getProximityLogic() {
		return proximityLogic;
	}

	public void setProximityLogic(ProximityLogic proximityLogic) {
		this.proximityLogic = proximityLogic;
	}

	protected Item retrieveItemController(ProximityRequest request) throws ItemNotFoundException,
			AccessDeniedException, NoSuchRepositoryException {

		Item item = null;

		try {

			if (request.getTargetedReposId() != null) {

				logger.debug("Going for targeted reposId {}", request.getTargetedReposId());
				item = retrieveItemByRepoId(request.getTargetedReposId(), request);

			} else if (request.getTargetedReposGroupId() != null) {

				logger.debug("Going for targeted reposGroupId {}", request.getTargetedReposGroupId());
				item = retrieveItemByRepoGroupId(request.getTargetedReposGroupId(), request);

			} else {

				logger.debug("Going for by absolute order, no target");
				item = retrieveItemByAbsoluteOrder(request);

			}

			// if not a targeted request that affects only one repos and
			// we need group search
			if (request.getTargetedReposId() == null && proximityLogic.isGroupSearchNeeded(request)) {

				ProximityRequest groupRequest = proximityLogic.getGroupRequest(request);

				List repositoryGroupOrder = (List) repositoryGroups.get(item.getProperties().getRepositoryGroupId());
				List itemList = new ArrayList();

				for (Iterator i = repositoryGroupOrder.iterator(); i.hasNext();) {
					String reposId = (String) i.next();
					try {
						itemList.add(retrieveItemByRepoId(reposId, groupRequest));
					} catch (ItemNotFoundException ex) {
						logger.debug("[{}] not found in repository {}", groupRequest.getPath(), reposId);
					}
				}

				item = proximityLogic.postprocessItemList(request, groupRequest, itemList);

			}

		} catch (IOException ex) {
			logger.error("Got IOException during retrieveItem.", ex);
		} catch (ItemNotFoundException ex) {
			throw ex;
		}

		return item;

	}


}
