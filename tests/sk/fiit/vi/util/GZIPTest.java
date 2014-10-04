package sk.fiit.vi.util;

import junit.framework.TestCase;
import org.junit.Assert;
import sk.fiit.vi.parser.ParseDump2Parts;

import java.io.BufferedReader;

public class GZIPTest extends TestCase {

    public void testRead() throws Exception {
        BufferedReader in;
        in = GZIP.read(ParseDump2Parts.FILE_PEOPLE);
        Assert.assertNotNull(in);
        in.close();

        in = GZIP.read(ParseDump2Parts.FILE_BIRTHS);
        Assert.assertNotNull(in);
        in.close();

        in = GZIP.read(ParseDump2Parts.FILE_DEATH);
        Assert.assertNotNull(in);
        in.close();

        in = GZIP.read(ParseDump2Parts.FILE_OBJECTS);
        Assert.assertNotNull(in);
        in.close();
    }
}