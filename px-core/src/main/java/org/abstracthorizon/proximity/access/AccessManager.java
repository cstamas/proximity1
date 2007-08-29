package org.abstracthorizon.proximity.access;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;

/**
 * Interface for access manager.
 * 
 * @author t.cservenak
 */
public interface AccessManager
{

    /**
     * The implementation of this method should throw AccessDeniedException or any subclass if it denies access.
     * 
     * @param request
     * @param repository
     * @param permission
     * @throws AccessDeniedException
     */
    void decide( ProximityRequest request, Repository repository, RepositoryPermission permission )
        throws AccessDeniedException;

}
