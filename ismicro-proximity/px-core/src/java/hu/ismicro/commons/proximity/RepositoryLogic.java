package hu.ismicro.commons.proximity;

public interface RepositoryLogic {

    void orchestrateItemRequest(String path, Repository repository);

    void orchestrateListRequest(String path, Repository repository);

}
