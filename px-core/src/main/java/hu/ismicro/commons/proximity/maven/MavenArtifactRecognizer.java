package hu.ismicro.commons.proximity.maven;

public class MavenArtifactRecognizer {

    public static boolean isPom(String name) {
        return name.endsWith(".pom");
    }

    public static boolean isSnapshot(String name) {
        return name.indexOf("SNAPSHOT") != -1;
    }

    public static boolean isMetadata(String name) {
        return name.startsWith("maven-metadata.xml") || name.endsWith(".sha1") || name.endsWith(".md5");
    }

}
