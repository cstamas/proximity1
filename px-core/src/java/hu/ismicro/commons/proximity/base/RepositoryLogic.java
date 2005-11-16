package hu.ismicro.commons.proximity.base;

import hu.ismicro.commons.proximity.Repository;

public interface RepositoryLogic {

    void orchestrateItemRequest(String path, Repository repository);

    void orchestrateListRequest(String path, Repository repository);

}
