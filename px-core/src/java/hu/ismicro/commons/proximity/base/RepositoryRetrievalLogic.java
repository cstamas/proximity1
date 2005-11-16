package hu.ismicro.commons.proximity.base;

public interface RepositoryRetrievalLogic {

    ProxiedItem orchestrateItemRequest(String path, InnerRepository repository);

}
