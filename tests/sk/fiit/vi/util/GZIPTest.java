package sk.fiit.vi.util;

import junit.framework.TestCase;
import org.junit.Assert;

import java.io.BufferedReader;

public class GZIPTest extends TestCase {

    public void testRead() throws Exception {
        BufferedReader in;
        in = GZIP.read("data/people.gz");
        Assert.assertNotNull(in);
        in.close();

        in = GZIP.read("data/births.gz");
        Assert.assertNotNull(in);
        in.close();

        in = GZIP.read("data/deceased_persons.gz");
        Assert.assertNotNull(in);
        in.close();

        in = GZIP.read("data/people.gz");
        Assert.assertNotNull(in);
        in.close();
    }
}