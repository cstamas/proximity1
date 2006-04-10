package hu.ismicro.commons.proximity;

/**
 * Thrown by repository if the requested item is not found.
 * 
 * @author cstamas
 *
 */
public class ItemNotFoundException extends ProximityException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4964273361722823796L;

	public ItemNotFoundException(String path) {
        super("Item not found on path " + path);
    }

    public ItemNotFoundException(String path, String repo) {
        super("Item not found on path " + path + " in repository " + repo);
    }

}
