package hu.ismicro.commons.proximity;

public class ItemNotFoundException extends ProximityException {
	
	private String path;
	
	public ItemNotFoundException(String path) {
		super();
		this.path = path;
	}
	
	public String getPath() {
		return this.path;
	}

}
