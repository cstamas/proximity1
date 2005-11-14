package hu.ismicro.commons.proximity;

public interface Storage {

    boolean containsArtifact(String key);

    void storeArtifact(String key, Artifact af);

    Artifact retrieveArtifact(String key);

}
