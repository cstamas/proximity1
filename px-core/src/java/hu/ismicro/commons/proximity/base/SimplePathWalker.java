package hu.ismicro.commons.proximity.base;

public class SimplePathWalker {
    
    public static final String PATHSEPARATOR = "/";
    
    static String getParent(String path) {
        return path;
    }
    
    static String getChild(String basePath, String childPath) {
        String left = basePath;
        String right = childPath;
        if (!basePath.endsWith(PATHSEPARATOR)) {
            left = basePath + PATHSEPARATOR;
        } else {
            left = basePath;
        }
        if (!childPath.startsWith(PATHSEPARATOR)) {
            right = PATHSEPARATOR + childPath;
        } else {
            right = childPath;
        }
        return left + right;
    }

}
