package hu.ismicro.commons.proximity.base;

import junit.framework.Assert;
import junit.framework.TestCase;

public class PathHelperTest extends TestCase {

    public void testAbsolutizePathFromBase() {
        Assert.assertEquals("/b/c", PathHelper.absolutizePathFromBase("/a", "/b/c"));
        Assert.assertEquals("/a/b/c", PathHelper.absolutizePathFromBase("/a", "b/c"));
        Assert.assertEquals("/a/b/c", PathHelper.absolutizePathFromBase("/a", "../a/./b/c"));
        Assert.assertEquals("/a/b/c", PathHelper.absolutizePathFromBase("/a", "../a/./../d/../a/./b/c"));
        Assert.assertEquals("/a/b/c", PathHelper.absolutizePathFromBase("/", "./a/./../d/../a/./b/c"));
    }

    public void testChangePathLevel() {
        Assert.assertEquals("/", PathHelper.changePathLevel("/", ".."));
        Assert.assertEquals("/", PathHelper.changePathLevel("/", "."));
        try {
            Assert.assertEquals("/", PathHelper.changePathLevel("/", "/"));
            fail();
        } catch (IllegalArgumentException ex) {

        }
        Assert.assertEquals("/a", PathHelper.changePathLevel("/a/b", ".."));
        Assert.assertEquals("/a/b", PathHelper.changePathLevel("/a", "b"));
    }

    public void testWalkThePath() {
        Assert.assertEquals("/a/b/c", PathHelper.walkThePath("/a", "/b/c"));
        Assert.assertEquals("/a/b/c", PathHelper.walkThePath("/a/", "/b/c"));
        Assert.assertEquals("/a/b/c", PathHelper.walkThePath("/", "a/b/c"));
        Assert.assertEquals("/a/b/c", PathHelper.walkThePath("/", "/a/b/c"));
        Assert.assertEquals("/a/b/c", PathHelper.walkThePath("/a/b/c", ""));
        Assert.assertEquals("/a/b/c", PathHelper.walkThePath("/a/b/c", "/"));
        Assert.assertEquals("/a/b/c", PathHelper.walkThePath("", "/a/b/c"));
        Assert.assertEquals("/a/b/c", PathHelper.walkThePath("", "a/b/c"));
        Assert.assertEquals("http://a.c/a/b/c", PathHelper.walkThePath("http://a.c", "a/b/c"));
        Assert.assertEquals("http://a.c/a/b/c", PathHelper.walkThePath("http://a.c", "/a/b/c"));
    }

    public void testGetFileName() {
        Assert.assertEquals("c", PathHelper.getFileName("c"));
        Assert.assertEquals("c", PathHelper.getFileName("/a/b/c"));
        Assert.assertEquals("aa", PathHelper.getFileName("aa///"));
        Assert.assertEquals("/", PathHelper.getFileName("///"));
        Assert.assertEquals("file", PathHelper.getFileName("/a/b/c/file///"));

    }

}
