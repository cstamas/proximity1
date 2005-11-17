package hu.ismicro.commons.proximity;

import java.util.List;

public interface ProximitySupport {

    public List searchAllRepositories(String regexp);
    
    public List searchRepository(String reposName, String regexp);

}
