package sk.fiit.vi.util;

import junit.framework.TestCase;
import org.junit.Assert;

public class ConfigurationTest extends TestCase {

    public void testGetInstance() throws Exception {
        Assert.assertTrue(Configuration.getInstance() != null);
    }
}