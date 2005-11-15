package hu.ismicro.commons.proximity;

public interface Storage {

    boolean containsArtifact(String key);

    Artifact retrieveArtifact(String key);

    void storeArtifact(String key, Artifact af);

}
