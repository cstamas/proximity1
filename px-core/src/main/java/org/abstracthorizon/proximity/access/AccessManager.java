package org.abstracthorizon.proximity.access;

import java.util.Map;

import org.abstracthorizon.proximity.AccessDeniedException;
import org.abstracthorizon.proximity.ProximityRequest;

/**
 * Interface for access manager.
 * 
 * @author t.cservenak
 * 
 */
public interface AccessManager {

    /**
         * The implementation of this method should throw AccessDeniedException
         * or any subclass if it denies access.
         * 
         * @param grantee
         * @param request
         * @param config
         * @throws AccessDeniedException
         */
    void decide(ProximityRequest request, Map config) throws AccessDeniedException;

}
