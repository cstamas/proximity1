package hu.ismicro.commons.proximity;

public class ItemNotFoundException extends ProximityException {

    private String path;

    public ItemNotFoundException(String path) {
        super("Item not found on path " + path);
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

}
