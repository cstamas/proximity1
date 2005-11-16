package hu.ismicro.commons.proximity;

import java.net.URL;

public interface ProxiedItem extends Item {

    String getRepositoryName();

    URL getOriginatingUrl();

}
