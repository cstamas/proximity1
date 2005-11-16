package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.ProximityRequest;

public class SimpleProximityRequest implements ProximityRequest {

    private String path;

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
