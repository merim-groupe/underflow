package com.merim.digitalpayment.underflow.tests.units;

import com.merim.digitalpayment.underflow.handlers.context.path.PathMatcher;
import org.junit.jupiter.api.*;

/**
 * PathMatcherTests.
 *
 * @author Pierre Adam
 * @since 22.07.26
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PathMatcherTest {

    @Test
    @Order(1)
    @DisplayName("Simple routes")
    public void testSimpleRoutes() {
        Assertions.assertTrue(new PathMatcher("/foo", "/foo", false).find());
        Assertions.assertTrue(new PathMatcher("/foo", "/foo$", false).find());
        Assertions.assertTrue(new PathMatcher("/foo/bar", "/foo", false, true).find());
        Assertions.assertFalse(new PathMatcher("/foo/bar", "/foo", false, false).find());
        Assertions.assertFalse(new PathMatcher("/FOO/bar", "/foo", false, true).find());
        Assertions.assertTrue(new PathMatcher("/FOO/bar", "/foo", true, true).find());

        Assertions.assertTrue(new PathMatcher("/foo/bar", "/foo/bar", false).find());
        Assertions.assertFalse(new PathMatcher("/FOO/BAR", "/foo/bar", false).find());
        Assertions.assertTrue(new PathMatcher("/FOO/BAR", "/foo/bar", true).find());

        Assertions.assertFalse(new PathMatcher("/foo/bar", "foo", false, false).find());
        Assertions.assertTrue(new PathMatcher("/foo/bar", "foo", false, true).find());
        Assertions.assertFalse(new PathMatcher("/foo/bar", "bar", false, false).find());
        Assertions.assertFalse(new PathMatcher("/foo/bar", "bar", false, true).find());
        Assertions.assertFalse(new PathMatcher("/foo/bar", "/bar", false, false).find());
        Assertions.assertFalse(new PathMatcher("/foo/bar", "/bar", false, true).find());
        Assertions.assertFalse(new PathMatcher("/foo/bar", "fo", false, false).find());
        Assertions.assertFalse(new PathMatcher("/foo/bar", "fo", false, true).find());
        Assertions.assertFalse(new PathMatcher("/foo/bar", "/f", false, false).find());
        Assertions.assertFalse(new PathMatcher("/foo/bar", "/f", false, true).find());

        Assertions.assertTrue(new PathMatcher("/", "/", false, false).find());
        Assertions.assertTrue(new PathMatcher("/", "/", false, true).find());

        Assertions.assertTrue(new PathMatcher("/", "", false, false).find());
        Assertions.assertTrue(new PathMatcher("/", "", false, true).find());

        Assertions.assertTrue(new PathMatcher("", "/", false, false).find());
        Assertions.assertTrue(new PathMatcher("", "/", false, true).find());

        Assertions.assertTrue(new PathMatcher("", "", false, false).find());
        Assertions.assertTrue(new PathMatcher("", "", false, true).find());
    }

    @Test
    @Order(2)
    @DisplayName("Routes with patterns")
    public void testPatternsRoutes() {
        PathMatcher pm = new PathMatcher("/foo/bar", "/f(o)+", false, false);
        Assertions.assertFalse(pm.find());

        pm = new PathMatcher("/foo/bar", "/f(o)+", false, true);
        Assertions.assertTrue(pm.find());
        Assertions.assertEquals("/bar", pm.getRemainingPath());

        pm = new PathMatcher("/foooooo/bar", "/f(o)+", false, true);
        Assertions.assertTrue(pm.find());
        Assertions.assertEquals("/bar", pm.getRemainingPath());
        Assertions.assertEquals("/foooooo", pm.getFullMatch());

        pm = new PathMatcher("/foOoOOo/bar", "/f(o)+", true, true);
        Assertions.assertTrue(pm.find());
        Assertions.assertEquals("/bar", pm.getRemainingPath());
        Assertions.assertEquals("/foOoOOo", pm.getFullMatch());

        pm = new PathMatcher("/fo/bar", "/f(o)+", false, true);
        Assertions.assertTrue(pm.find());
        Assertions.assertEquals("/bar", pm.getRemainingPath());

        pm = new PathMatcher("/f/bar", "/f(o)+", false, true);
        Assertions.assertFalse(pm.find());

        pm = new PathMatcher("/f/bar", "/f(o)*", false, true);
        Assertions.assertTrue(pm.find());
        Assertions.assertEquals("/bar", pm.getRemainingPath());
    }

    @Test
    @Order(3)
    @DisplayName("Routes with arguments")
    public void testArgumentsRoutes() {
        PathMatcher pm = new PathMatcher("/foo/5/bar", "/foo/(?<id>[0-9]+)", false, true);
        Assertions.assertTrue(pm.find());
        Assertions.assertEquals("/bar", pm.getRemainingPath());
        Assertions.assertTrue(pm.hasGroup("id"));
        Assertions.assertFalse(pm.hasGroup("foo"));
        Assertions.assertEquals("5", pm.getGroup("id"));

        pm = new PathMatcher("/foo/5/bar", "/foo/(?<id>[0-9]+)", false, false);
        Assertions.assertFalse(pm.find());

        pm = new PathMatcher("/foo/5a/bar", "/foo/(?<id>[0-9]+)", false, true);
        Assertions.assertFalse(pm.find());

        pm = new PathMatcher("/foo/123456", "/foo/(?<id>[0-9]+)", false, false);
        Assertions.assertTrue(pm.find());
        Assertions.assertEquals("", pm.getRemainingPath());
        Assertions.assertTrue(pm.hasGroup("id"));
        Assertions.assertFalse(pm.hasGroup("bar"));
        Assertions.assertEquals("123456", pm.getGroup("id"));
    }

    @Test
    @Order(4)
    @DisplayName("Non existing group")
    public void testNonExistingGroup() {
        final PathMatcher pm = new PathMatcher("/foo/5/bar", "/foo/(?<id>[0-9]+)", false, true);
        Assertions.assertTrue(pm.find());
        Assertions.assertEquals("/bar", pm.getRemainingPath());
        Assertions.assertThrows(IllegalArgumentException.class, () -> pm.getGroup("gnarf"));

        final PathMatcher pm2 = new PathMatcher("/foo/5a/bar", "/foo/(?<id>[0-9]+)", false, true);
        Assertions.assertFalse(pm2.find());
        Assertions.assertFalse(pm2.hasGroup("gnarf"));
        Assertions.assertThrows(IllegalStateException.class, () -> pm2.getGroup("gnarf"));
    }

    @Test
    @Order(5)
    @DisplayName("Reset Matcher")
    public void testResetMatcher() {
        final PathMatcher pm = new PathMatcher("/foo/bar", "/foo", false, true);

        Assertions.assertTrue(pm.find());
        Assertions.assertFalse(pm.find());
        Assertions.assertFalse(pm.find());

        pm.reset();
        Assertions.assertTrue(pm.find());
        Assertions.assertFalse(pm.find());

        pm.reset();
        Assertions.assertTrue(pm.find());
    }

    @Test
    @Order(5)
    @DisplayName("Test Prefix")
    public void testPrefix() {
        Assertions.assertTrue(new PathMatcher("/foo", "/foo", "", false, false).find());
        Assertions.assertTrue(new PathMatcher("/foo", "/foo", "", false, true).find());

        Assertions.assertTrue(new PathMatcher("/foo", "/foo", "/", false, false).find());
        Assertions.assertTrue(new PathMatcher("/foo", "/foo", "/", false, true).find());

        Assertions.assertTrue(new PathMatcher("/foo", "", "/foo", false, false).find());
        Assertions.assertTrue(new PathMatcher("/foo", "", "/foo", false, true).find());
    }
}
