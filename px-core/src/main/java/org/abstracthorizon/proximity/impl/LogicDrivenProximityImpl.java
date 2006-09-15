package org.abstracthorizon.proximity.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.NoSuchRepositoryException;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.RepositoryNotAvailableException;
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

    protected ItemImpl retrieveItemController(ProximityRequest request) throws ItemNotFoundException,
            AccessDeniedException, NoSuchRepositoryException {

        ItemImpl item = null;

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

    protected ItemImpl retrieveItemByAbsoluteOrder(ProximityRequest request) throws ItemNotFoundException,
            AccessDeniedException, NoSuchRepositoryException {
        for (Iterator i = repositoryOrder.iterator(); i.hasNext();) {
            String reposId = (String) i.next();
            try {
                Repository repo = (Repository) repositories.get(reposId);
                ItemImpl item = repo.retrieveItem(request);
                return item;
            } catch (RepositoryNotAvailableException ex) {
                logger.info("Repository unavailable", ex);
            } catch (ItemNotFoundException ex) {
                logger.debug(request.getPath() + " not found in repository " + reposId);
            }
        }
        throw new ItemNotFoundException(request.getPath());
    }

    protected ItemImpl retrieveItemByRepoGroupId(String groupId, ProximityRequest request)
            throws ItemNotFoundException, AccessDeniedException, NoSuchRepositoryException {
        if (repositoryGroups.containsKey(groupId)) {
            List repositoryGroupOrder = (List) repositoryGroups.get(groupId);
            for (Iterator i = repositoryGroupOrder.iterator(); i.hasNext();) {
                String reposId = (String) i.next();
                try {
                    Repository repo = (Repository) repositories.get(reposId);
                    ItemImpl item = repo.retrieveItem(request);
                    return item;
                } catch (RepositoryNotAvailableException ex) {
                    logger.info("Repository unavailable", ex);
                } catch (ItemNotFoundException ex) {
                    logger.debug(request.getPath() + " not found in repository " + reposId);
                }
            }
        }
        throw new ItemNotFoundException(request.getPath());
    }

    protected ItemImpl retrieveItemByRepoId(String repoId, ProximityRequest request) throws ItemNotFoundException,
            AccessDeniedException, NoSuchRepositoryException {
        if (repositories.containsKey(repoId)) {
            Repository repo = (Repository) repositories.get(repoId);
            try {
                ItemImpl item = repo.retrieveItem(request);
                return item;
            } catch (RepositoryNotAvailableException ex) {
                logger.info("Repository unavailable", ex);
            } catch (ItemNotFoundException ex) {
                logger.debug(request.getPath() + " not found in targeted repository " + repo.getId());
                throw ex;
            }
        }
        throw new NoSuchRepositoryException(request.getTargetedReposId());
    }

}
