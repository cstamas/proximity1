package org.abstracthorizon.proximity;

/**
 * Thrown by proximity if the requested Repository does not exists.
 * 
 * @author cstamas
 * 
 */
public class NoSuchRepositoryException extends ProximityException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4422677352666814031L;

	public NoSuchRepositoryException(String repoId) {
		super("Repository with name " + repoId + " not found!");
	}

}
